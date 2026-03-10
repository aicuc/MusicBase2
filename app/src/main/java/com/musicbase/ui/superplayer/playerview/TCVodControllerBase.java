package com.musicbase.ui.superplayer.playerview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.ui.superplayer.VideoGestureUtil;
import com.musicbase.ui.superplayer.utils.TCUtils;

import java.util.ArrayList;

/**
 * Created by liyuejiao on 2018/7/3.
 */

public abstract class TCVodControllerBase extends RelativeLayout implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "TCVodControllerBase";
    protected LayoutInflater mLayoutInflater;
    protected VodController mVodController;
    protected GestureDetector mGestureDetector;
    private boolean isShowing;
    protected boolean mLockScreen;
    private static final double RADIUS_SLOP = Math.PI * 1 / 4;
    protected TCVideoQulity mDefaultVideoQuality;
    protected ArrayList<TCVideoQulity> mVideoQualityList;
    protected int mPlayType;
    protected long mDuration;
    protected String mTitle;

    protected TextView mTvCurrent;
    protected TextView mTvDuration;
    protected SeekBar mSeekBarProgress;
    protected LinearLayout mLayoutReplay;
    protected ProgressBar mPbLiveLoading;

    private VideoGestureUtil mVideoGestureUtil;
    protected SuperPlayerProgressLayout mGestureProgressLayout;

    public TCVodControllerBase(@NonNull Context context) {
        super(context);
        init();
    }

    public TCVodControllerBase(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TCVodControllerBase(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(getContext());
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null || e2 == null) {
                    return false;
                }

                if (mVideoGestureUtil != null && mGestureProgressLayout != null) {
                    mVideoGestureUtil.check(mGestureProgressLayout.getHeight(), e1, e2, distanceX, distanceY);
                }
                return true;
            }


            @Override
            public boolean onDown(MotionEvent e) {
                if (mVideoGestureUtil != null) {
                    mVideoGestureUtil.reset(TCVodControllerBase.this.getWidth());
                }
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);

        mVideoGestureUtil = new VideoGestureUtil(getContext());
        mVideoGestureUtil.setVideoGestureListener(new VideoGestureUtil.VideoGestureListener() {
            @Override
            public void onBrightnessGesture(float newBrightness) {
                if (mGestureProgressLayout != null) {
                    mGestureProgressLayout.setProgress((int) (newBrightness * 100));
                    mGestureProgressLayout.setImageResource(R.drawable.brightness_w);
                    mGestureProgressLayout.show();
                }
            }

            @Override
            public void onVolumeGesture(float volumeProgress) {
                if (mGestureProgressLayout != null) {
                    if (volumeProgress >= 50){
                        mGestureProgressLayout.setImageResource(R.drawable.volume_higher_w);
                    }else if (volumeProgress > 0){
                        mGestureProgressLayout.setImageResource(R.drawable.volume_lower_w);
                    }else {
                        mGestureProgressLayout.setImageResource(R.drawable.volume_off_w);
                    }
                    mGestureProgressLayout.setProgress((int)volumeProgress);
                    mGestureProgressLayout.show();
                }
            }
        });
    }

    public void setVideoQualityList(ArrayList<TCVideoQulity> videoQualityList) {
        mVideoQualityList = videoQualityList;
    }

    public void updateTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle = title;
        } else {
            mTitle = "这是新播放的视频";
        }
    }

    public void updateVideoProgress(long current, long duration) {
        if (mTvCurrent != null ) mTvCurrent.setText(TCUtils.formattedTime(current));

        float percentage = duration > 0 ? ((float)current/(float)duration):1.0f;
        if(current == 0) {
            mDuration = 0;
            percentage = 0;
        }
        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE || mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mDuration = mDuration>current?mDuration:current;
            long leftTime = duration - current;
            if (duration > 7200) {
                duration = 7200;
            }
            percentage = 1 - (float)leftTime/(float)duration;
        }

        if (percentage >= 0 && percentage <= 1) {
            if (mSeekBarProgress != null) {
                int progress = Math.round(percentage * mSeekBarProgress.getMax());
                mSeekBarProgress.setProgress(progress);
            }
            if (mTvDuration != null ) mTvDuration.setText(TCUtils.formattedTime(duration));
        }

    }

    public void setVodController(VodController vodController) {
        mVodController = vodController;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!mLockScreen)
                toggle();
        }
        if (!mLockScreen) {
            if (mGestureDetector != null)
                mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        Log.d(TAG, "xxxxx "+i+", "+b);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 拖动seekbar结束时,获取seekbar当前进度,进行seek操作,最后更新seekbar进度
        int curProgress = seekBar.getProgress();
        int maxProgress = seekBar.getMax();

        switch (mPlayType) {
            case SuperPlayerConst.PLAYTYPE_VOD:
                if (curProgress >= 0 && curProgress < maxProgress) {
                    // 关闭重播按钮
                    updateReplay(false);
                    float percentage = ((float) curProgress) / maxProgress;
                    int position = (int) (mVodController.getDuration() * percentage);
                    mVodController.seekTo(position);
                    mVodController.resume();
                }
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE:
            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
                updateLiveLoadingState(true);
                int seekTime = (int)(mDuration * curProgress / maxProgress);
                if (mDuration > 7200) {
                    seekTime =(int) (mDuration - 7200*curProgress/maxProgress);
                }
                mVodController.seekTo(seekTime);
                break;
        }
    }

    public void updateReplay(boolean replay) {
        if (mLayoutReplay != null) {
            mLayoutReplay.setVisibility(replay?View.VISIBLE:View.GONE);
        }
    }

    public void updateLiveLoadingState(boolean loading) {
        if (mPbLiveLoading != null) {
            mPbLiveLoading.setVisibility(loading?View.VISIBLE:View.GONE);
        }
    }

    public void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        isShowing = true;
        onShow();
    }

    public void hide() {
        isShowing = false;
        onHide();
    }

    abstract void onShow();

    abstract void onHide();

    public void updatePlayType(int playType) {
        mPlayType = playType;
    }

    public interface VodController {

        void onRequestPlayMode(int requestPlayMode);

        void onBackPress(int playMode);

        void resume();

        void pause();

        float getDuration();

        float getCurrentPlaybackTime();

        void seekTo(int position);

        boolean isPlaying();

        void onDanmuku(boolean on);

        void onSnapshot();

        void onQualitySelect(TCVideoQulity quality);

        void onSpeedChange(float speedLevel);

        void onMirrorChange(boolean isMirror);

        void onHWAcceleration(boolean isAccelerate);

        void onFloatUpdate(int x, int y);

        void onReplay();

        void resumeLive();

    }

}
