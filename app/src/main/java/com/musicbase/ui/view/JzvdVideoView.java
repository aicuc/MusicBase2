package com.musicbase.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.musicbase.R;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class JzvdVideoView extends JzvdStd {
    private RelativeLayout layout_top;
    private ImageView fullscreen;
    private JzvdVideoView.CallBack callBack;

    private int show_type = 0;// 0 为普通播放时显示标题栏 1为不显示标题栏

    public JzvdVideoView(Context context) {
        super(context);
    }

    public JzvdVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface CallBack {
        void onStatePlaying();
        void onStatePause();
        void onStatePreparing();
        void onProgress(int progress, long position, long duration);
//        void onError(int what, int extra);
    }

    public void setType(int type) {
        show_type = type;
        if(show_type==1&&screen == Jzvd.SCREEN_NORMAL)
            layout_top.setVisibility(INVISIBLE);
        else
            layout_top.setVisibility(VISIBLE);
    }

    public void setCallBack(JzvdVideoView.CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        if(callBack!=null)
            callBack.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        if(callBack!=null)
            callBack.onStatePlaying();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        if(callBack!=null)
            callBack.onStatePause();
    }



    @Override
    public void init(Context context) {
        super.init(context);
        layout_top = findViewById(R.id.layout_top);
//        layout_top.setBackgroundColor(Color.parseColor("#80000000"));
        fullscreen = findViewById(R.id.fullscreen);
//        fullscreen.setVisibility(GONE);

    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
//        if(callBack!=null)
//            callBack.onError(what,extra);
    }

    @Override
    public void setScreenFullscreen() {
        if (show_type == 1)
            isTopContaintShow(true);
        super.setScreenFullscreen();
    }

    @Override
    public void setScreenNormal() {
        if (show_type == 1) {
            layout_top.setVisibility(INVISIBLE);
            isTopContaintShow(false);
        }
        super.setScreenNormal();
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();

    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
    }

    @Override
    public void onSeekComplete() {
        super.onSeekComplete();
    }

    @Override
    public void onProgress(int progress, long position, long duration) {
        super.onProgress(progress, position, duration);
        if(callBack!=null)
            callBack.onProgress(progress, position, duration);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
    }
}
