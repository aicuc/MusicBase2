package com.musicbase.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.cjt2325.cameralibrary.util.DeviceUtil;
import com.cjt2325.cameralibrary.util.FileUtil;
import com.musicbase.R;
import com.musicbase.entity.RecordBean;
import com.musicbase.ui.view.MyTouchListener;
import com.musicbase.util.ActivityUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class CameraActivity extends AppCompatActivity {
    private JCameraView jCameraView;
    private int resourceId;
    private int limitTime;
    private int allTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        setContentView(R.layout.activity_camera);
        EventBus.getDefault().register(this);
        resourceId = getIntent().getIntExtra("resourceId", 0);
        limitTime = getIntent().getIntExtra("limitTime", 0);
        allTime = getIntent().getIntExtra("allTime", 0);
        initParams();
        jCameraView = (JCameraView) findViewById(R.id.jcameraview);
        jCameraView.setDuration(allTime*1000);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(ActivityUtils.getSDPath(this,resourceId+"").getAbsolutePath() + File.separator + ".nomedia");
        Log.e("CJT","savepath==="+ActivityUtils.getSDPath(this,resourceId+"").getAbsolutePath() + File.separator + ".nomedia");
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        jCameraView.setTip("");
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_LOW);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Log.i("CJT", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                Toast.makeText(CameraActivity.this, "给点录音权限可以?", Toast.LENGTH_SHORT).show();
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                //                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());

//                String path = FileUtil.saveBitmap("JCamera", bitmap);
//                Intent intent = new Intent();
//                intent.putExtra("path", path);
//                setResult(101, intent);
//                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                //获取视频路径
                String path = FileUtil.saveBitmap(url, firstFrame);
                Log.i("CJT", "url = " + url + ", Bitmap = " + path+"-----file size="+getFileLength(url));
//                Intent intent = new Intent();
//                intent.putExtra("path", path);
//                setResult(101, intent);
//                finish();

                Intent data = new Intent();
                data.putExtra("url",url);
                setResult(111,data);
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(CameraActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("CJT", DeviceUtil.getDeviceModel());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String type) {
        if(type.equals("begin"))
            jCameraView.longClick();
    }

    public long getFileLength(String filePath){
        long fileSize = 0;
        File f= new File(filePath);
        if (f.exists() && f.isFile()){
            fileSize = f.length();
        }else{
            Log.e("getFileSize","file doesn't exist or is not a file");
        }
        return ((long)fileSize/1024/1024);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_DESTROY,0,0));
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initParams() {
        WindowManager.LayoutParams p =  getWindow().getAttributes();  //获取对话框当前的参数值

        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        p.height = (int) (outMetrics.heightPixels *0.3);   //高度设置为屏幕的0.5
        p.width = (int) (outMetrics.widthPixels*0.45);    //宽度设置为屏幕的0.8
        p.alpha = 1.0f;      //设置本身透明度
        p.dimAmount = 0f;      //设置黑暗度
        p.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        p.y = 30;
        getWindow().setAttributes(p);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

}
