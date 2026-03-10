package com.musicbase.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbase.R;
import com.musicbase.entity.UpdateBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Type;

public class UpdateActivity extends Activity {
    LinearLayout ll_content;
    TextView tv_updateInfo;
    TextView tv_updateTitle;
    Button btn_update;
    UpdateBean.Data update;

    private final String TAG = getClass().getSimpleName();
    String client_version;
    LinearLayout ll_container;
    LinearLayout layout_btn;
    ProgressBar pb_syn;
    RelativeLayout rl_pb;
    Button btn_plan;
    boolean needUpdate;
    Button btn_cancel;
    Callback.Cancelable cancelable;
    boolean isDownload = false;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (!TextUtils.isEmpty(update.getIsUpdate()))
                        updateContentUI(Integer.parseInt(update.getIsUpdate()));
                    tv_updateTitle.setText("当前已是最新版本:音乐素养 " + client_version);
                    break;
                case 1:
                    if (!TextUtils.isEmpty(update.getIsUpdate()))
                        updateContentUI(Integer.parseInt(update.getIsUpdate()));
                    tv_updateTitle.setText("已有新版本：音乐素养 " + update.getVersionName());
                    if(!TextUtils.isEmpty(update.getDesc())) {
                        String update_text = update.getDesc().replace("$", "\n");
                        tv_updateInfo.setText(update_text);
                    }
                    break;
                case 2:
                    break;
                case 3:
                    pb_syn.setProgress(msg.getData().getInt("size"));
                    float result = (float) pb_syn.getProgress() / (float) pb_syn.getMax();
                    int num = (int) (result * 100);
                    btn_plan.setText("下载中..." + num + "%");
                    Log.i("progress", "" + num);
                    //                    if (pb_syn.getProgress() == pb_syn.getMax()) {
                    //                        //						pb_syn.setVisibility(View.GONE);
                    //                        btn_plan.setText("下载完成");
                    //                        File file = new File(ActivityUtils.getSDPath(), "jiayue.apk");
                    //                        installApk(file);
                    //                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update);
        initView();
    }

    public void initView() {
        initTitle();
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        tv_updateInfo = (TextView) findViewById(R.id.tv_updateInfo);
        tv_updateTitle = (TextView) findViewById(R.id.tv_updatetitle);
        btn_update = (Button) findViewById(R.id.btn_update);
        layout_btn = (LinearLayout) findViewById(R.id.layout_btn);
        pb_syn = (ProgressBar) findViewById(R.id.pb_syn);
        rl_pb = (RelativeLayout) findViewById(R.id.rl_pb);
        btn_plan = (Button) findViewById(R.id.btn_plan);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        client_version = getClientVersion();
        update = (UpdateBean.Data) getIntent().getExtras().getSerializable("update");
        Logger.d(update);
        if (update.getIsUpdate().equals("3")) {
            getVerisonUpdate();
        } else {
            setUpgradeInfo(update);
        }
    }

    private void setUpgradeInfo(final UpdateBean.Data bean) {
        if (bean.getIsUpdate().equals("0")) {
            needUpdate = false;
            handler.sendEmptyMessage(0);

        } else {
            Log.i("CastUpdateActivity", "update=" + update.toString() + "---------locale---version=" + client_version);
            needUpdate = true;
            handler.sendEmptyMessage(1);
        }
    }

    private void initTitle() {
        ((TextView) findViewById(R.id.titlelayout_title)).setText("版本更新");
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateContentUI(int update) {
        layout_btn.setVisibility(update == 0 ? View.GONE : View.VISIBLE);
        btn_cancel.setVisibility(update == 1 ? View.VISIBLE : View.GONE);
        tv_updateInfo.setVisibility(update == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (isDownload && keyCode == KeyEvent.KEYCODE_BACK) {
            if (cancelable != null) {
                cancelable.cancel();
            }
            Toast.makeText(getApplicationContext(),"当前更新已取消..." , Toast.LENGTH_LONG).show();
            setResult(222);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void getVerisonUpdate() {

        RequestParams params = new RequestParams(Preferences.VERISON_UPDATE_URL);
        params.addQueryStringParameter("version", getClientVersion());
        params.addQueryStringParameter("systemType", "android");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.d("getVerisonUpdate=========" + s);
                Gson gson = new Gson();
                Type type = new TypeToken<UpdateBean>() {
                }.getType();
                UpdateBean bean = gson.fromJson(s, type);
                if (bean != null) {
                    update = bean.getData();
                    setUpgradeInfo(bean.getData());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Logger.d("throwable=" + throwable.getMessage());
                ActivityUtils.showToast(UpdateActivity.this, getString(R.string.internet_fail));
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private String getClientVersion() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    public void updateApp(View v) {
        if (ActivityUtils.isWifiEnabled(this)) {
            updateApp2();
        } else {
            DialogUtils.showMyDialog(this, Preferences.SHOW_CONFIRM_DIALOG, "流量提醒", "您现在使用的是移动网络，是否继续下载？", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateApp2();
                }
            });
        }
    }

    private void updateApp2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //当前系统大于等于6.0
            permission();
        } else {
            //当前系统小于6.0，直接调用拍照
            doBackup();
        }
    }

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String[] myPermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private void permission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(myPermissions);
        Log.d(TAG, "isAllGranted==" + isAllGranted);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(this, myPermissions, MY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            Log.d(TAG, "isAllGranted222====" + isAllGranted);
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 后续操作
     */
    private void doBackup() {

        String path = update.getUrl();
        Log.d("ppppppppppppppppp", "down path====" + path);
        download();
    }

    private void setDownloadLayout(){
        if(isDownload){
            layout_btn.setVisibility(View.GONE);
            rl_pb.setVisibility(View.VISIBLE);
        }else{
            layout_btn.setVisibility(View.VISIBLE);
            rl_pb.setVisibility(View.GONE);
        }

    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("加阅需要访问“外部存储器”权限，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    public void cancelApp(View view) {
        setResult(222);
        finish();
    }
    // 下载apk
    private void download() {
        final String path = ActivityUtils.getSDPath(this,"").getAbsolutePath();

        String apkName = "yinyuesuyang_"+update.getVersionName()+".apk";
        File file = new File(path);
        File[] fileList = file.listFiles();
        for(File f:fileList){
            if(f.isFile()&&f.getName().contains("yinyuesuyang_")&&f.getName().contains(".apk")&&!f.getName().equals(apkName))
                f.delete();
        }
        RequestParams params = new RequestParams(update.getUrl());
        // 自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(path+"/"+apkName);
        //        // 自动为文件命名
        //        params.setAutoRename(true);
        params.setAutoResume(true);
        params.setCancelFast(true);
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "imageDownload onFailure------" + ex.getMessage());
                ActivityUtils.showToast(UpdateActivity.this, "更新下载失败----" + ex.getMessage());
                isDownload = false;
                setDownloadLayout();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled------");
                isDownload = false;
                setDownloadLayout();
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished------");
                isDownload = false;
            }

            // 网络请求之前回调
            @Override
            public void onWaiting() {
            }

            // 网络请求开始的时候回调
            @Override
            public void onStarted() {
                isDownload = true;
                setDownloadLayout();
            }

            // 下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                // 当前进度和文件总大小
                Log.i("JAVA", "current：" + current + "，total：" + total);
                pb_syn.setMax((int) total);// 设置进度条的最大刻度为文件长度
                Message msg = new Message();
                msg.what = 3;
                msg.getData().putInt("size", (int)current);
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(File file) {
                // TODO Auto-generated method stub
                try {
                    //						pb_syn.setVisibility(View.GONE);
                    btn_plan.setText("下载完成,等待安装...");
                    installApk(file);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 安装下载成功的apk
     *
     * @param file apk的文件对象
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        Uri uri = null;
        // 查看的意图 (动作)
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
            //com.xxx.xxx.fileprovider为上述manifest中provider所配置相同
            uri = FileProvider.getUriForFile(this, "com.musicbase.provider.MyProvider", file);
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0以后，系统要求授予临时uri读取权限，安装完毕以后，系统会自动收回权限，该过程没有用户交互
        } else {//7.0以下
            uri = Uri.fromFile(file);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
    }
}
