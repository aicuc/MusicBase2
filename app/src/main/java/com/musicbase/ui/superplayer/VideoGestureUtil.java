package com.musicbase.ui.superplayer;
import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import androidx.annotation.IntDef;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */

public class VideoGestureUtil {
    private static final String TAG = "VideoGestureUtil";
    private static final int NONE = 0, VOLUME = 1, BRIGHTNESS = 2, FF_REW = 3;
    private
    @ScrollMode
    int mScrollMode = NONE;

    @IntDef({NONE, VOLUME, BRIGHTNESS, FF_REW})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ScrollMode {
    }

    private VideoGestureListener mVideoGestureListener;
    
    private int offsetX = 1;

    private int mVideoWidth;

    //brightness
    private float mBrightness = 1;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    private ContentResolver mResolver;

    //audio
    private AudioManager mAudioManager;
    private int mMaxVolume = 0;
    private int mOldVolume = 0;

    public VideoGestureUtil(Context context) {

        mAudioManager = (AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (context instanceof Activity) {
            mWindow = ((Activity)context).getWindow();
            mLayoutParams = mWindow.getAttributes();
            mBrightness = mLayoutParams.screenBrightness;
        }

        mResolver = context.getContentResolver();
    }

    public void setVideoGestureListener(VideoGestureListener videoGestureListener) {
        mVideoGestureListener = videoGestureListener;
    }

    public void reset(int videoWidth) {
        mVideoWidth = videoWidth;
        mScrollMode = NONE;
        mOldVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mBrightness = mLayoutParams.screenBrightness;
        if (mBrightness == -1){
            //一开始是默认亮度的时候，获取系统亮度，计算比例值
            mBrightness = getBrightness() / 255.0f;
        }
    }

    public void check(int height, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        switch (mScrollMode) {
            case NONE:
                //offset是让快进快退不要那么敏感的值
                if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
                    mScrollMode = FF_REW;
                } else {
                    if (e1.getX() < mVideoWidth / 2) {
                        mScrollMode = BRIGHTNESS;
                    } else {
                        mScrollMode = VOLUME;
                    }
                }
                break;
            case VOLUME:
                int value = height/ mMaxVolume;
                int newVolume = (int) ((e1.getY() - e2.getY())/value + mOldVolume);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,AudioManager.FLAG_PLAY_SOUND);

                float volumeProgress = newVolume/Float.valueOf(mMaxVolume) *100;
                if (mVideoGestureListener != null) {
                    mVideoGestureListener.onVolumeGesture(volumeProgress);
                }

                break;
            case BRIGHTNESS:
                float newBrightness = height == 0? 0: (e1.getY() - e2.getY()) / height ;
                newBrightness += mBrightness;

                if (newBrightness < 0){
                    newBrightness = 0;
                }else if (newBrightness > 1){
                    newBrightness = 1;
                }
                if (mLayoutParams != null) {
                    mLayoutParams.screenBrightness = newBrightness;
                }
                if (mWindow != null) {
                    mWindow.setAttributes(mLayoutParams);
                }

                if (mVideoGestureListener != null) {
                    mVideoGestureListener.onBrightnessGesture(newBrightness);
                }
                break;
        }
    }

    private int getBrightness(){
        if (mResolver != null) {
            return Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS, 255);
        } else {
            return 255;
        }
    }


    /**
     * 用于提供给外部实现的视频手势处理接口
     */

    public interface VideoGestureListener {
        //亮度手势，手指在Layout左半部上下滑动时候调用
        public void onBrightnessGesture(float newBrightness);

        //音量手势，手指在Layout右半部上下滑动时候调用
        public void onVolumeGesture(float volumeProgress);

    }

}
