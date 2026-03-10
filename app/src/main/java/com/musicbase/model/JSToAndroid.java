package com.musicbase.model;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.musicbase.ui.activity.WorkWebActivity;
import com.musicbase.util.ActivityUtils;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;


/**
 * Created by BAO on 2018-07-13.
 */

public class JSToAndroid {

    Context context;
    private String TAG = getClass().getSimpleName();
    private boolean isCheck;
    private String file_name;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String[] myPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    String recordName;
    private WorkWebActivity.CallBack callBack ;

    public JSToAndroid(Context c, String filePath, WorkWebActivity.CallBack callBack) {
        context = c;
        recordName = new File(filePath).getParentFile().getParent() + File.separator + new File(filePath).getParentFile().getName();
        recordName = recordName.replace("file:", "");
        this.callBack = callBack;
    }

    @JavascriptInterface
    public void exit(){
        Log.e(TAG, "exitexitexitexitexitexitexit");
        callBack.exit();
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void check() {
        Log.d(TAG, "check");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            isCheck = true;
            permission(null);
        }
    }


    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void start(String name) {
        Log.d(TAG, "start");
        file_name = name;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            permission(name);
        } else {
            //当前系统小于6.0，直接调用
            doBackup(name);
        }
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public String end() {
        Log.d(TAG, "end");
        return stopRecord();
    }

    @JavascriptInterface
    public void startVideo(){
        if(callBack!=null)
            callBack.startVideo();
    }

    @JavascriptInterface
    public void startAudio(){
        if(callBack!=null)
            callBack.startAudio();
    }

    private void permission(String name) {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                myPermissions);
        Log.d(TAG, "isAllGranted==" + isAllGranted);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            doBackup(name);
            return;
        }

        boolean hasRefuse = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.RECORD_AUDIO);

        if(hasRefuse)
            openAppDetails();
        else{
            /**
             * 第 2 步: 请求权限
             */
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    myPermissions,
                    MY_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup(String name) {
        if (isCheck)
            isCheck = false;
        else
            recorder_Media(name);
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("需要访问“外部存储器”和“录音”权限，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private MediaRecorder mediaRecorder;
    private File audioFile;

    public void recorder_Media(String name) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File path = ActivityUtils.getSDPath(context,ActivityUtils.RECORD_DIR);
        try {
            audioFile = new File(recordName + name + ".m4a");
            if (audioFile.exists()) {
                audioFile.delete();
                audioFile.createNewFile();
            } else {
                audioFile.createNewFile();
            }
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
//            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String stopRecord() {
        try {
            if (mediaRecorder == null)
                return "";
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            return audioFile.getAbsolutePath();
        } catch (RuntimeException e) {
            LogUtil.e(e.toString());
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            return "";
        }
    }
}


