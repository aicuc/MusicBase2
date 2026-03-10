package com.musicbase.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.musicbase.R;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.orhanobut.logger.Logger;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


public class VideoActivity extends Activity {

    private JzvdStd video_view;
    private final String TAG = getClass().getSimpleName();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
    }

    private void initView() {
        video_view = (JzvdStd) findViewById(R.id.video_view);
        url = getIntent().getStringExtra("Video_url");
        Logger.e("jzvd---url="+url+"----title="+getIntent().getStringExtra("title"));
//        JzvdStd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//        JzvdStd.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER);//自适应
        video_view.screen = Jzvd.SCREEN_NORMAL;
        video_view.setUp(url, getIntent().getStringExtra("title"));
        video_view.startVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ActivityUtils.deleteBookFormSD(url);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
