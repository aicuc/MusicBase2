package com.musicbase.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.musicbase.R;
import com.musicbase.model.JSToAndroid;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.ProgressWebview;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.ToastUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkWebActivity extends Activity {

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;
    @BindView(R.id.delete_text)
    TextView delete_text;
    @BindView(R.id.wv_brower)
    ProgressWebview mWebView;
    @BindView(R.id.pdfView)
    PDFView pdfView;

    private String filepath = "";
    private String fileType = "";
    int sum = 0;// 记录进去页面次数，为1时，退出
    private TextView tv_header_title, close;
    private LinearLayout title_layout, ll_header;
    private String title_name = "";
    private boolean isCheck = true;
    private String file_name = "";
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志
    String deletepath;
    private CallBack callBack;
    public interface CallBack{
        void exit();
        void startVideo();
        void startAudio();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        setContentView(R.layout.activity_work_web);
        ButterKnife.bind(this);
        initView();

        initData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void initData() {
        if (fileType.equals("pdf")) {
            mWebView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.fromFile(new File(filepath))
                    .defaultPage(1)
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            // 当用户在翻页时候将回调。
                            Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
                        }
                    }).load();


        } else {
            WebSettings ws = mWebView.getSettings();

            ws.setJavaScriptEnabled(true);
            ws.setJavaScriptCanOpenWindowsAutomatically(true);
            ws.setLoadWithOverviewMode(true);
            ws.setMediaPlaybackRequiresUserGesture(false);
            // 通过addJavascriptInterface()将Java对象映射到JS对象
            //参数1：Javascript对象名
            //参数2：Java对象名

            mWebView.addJavascriptInterface(new JSToAndroid(this, filepath, new CallBack() {
                @Override
                public void exit() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ActivityUtils.deleteFolder(deletepath);
                        }
                    }).start();
                    finish();
                }

                @Override
                public void startVideo() {
                    mWebView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
                }

                @Override
                public void startAudio() {
                    mWebView.loadUrl("javascript:(function() { var audios = document.getElementsByTagName('audio'); for(var i=0;i<audios.length;i++){audios[i].play();}})()");
                }
            }), "android");//AndroidtoJS类对象映射到js的test对象


            ws.setAllowFileAccess(true);
            ws.setAllowFileAccessFromFileURLs(true);
            ws.setAllowUniversalAccessFromFileURLs(true);
            // 是否允许缩放
            ws.setBuiltInZoomControls(true);
            ws.setSupportZoom(true);
            mWebView.loadUrl(filepath);
            Logger.d(filepath);
            if (!filepath.startsWith("https://www.bilibili.com") && !filepath.startsWith("https://www.ximalaya.com")) {
                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                String url = "";
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    url = request.getUrl().toString();
//                } else {
//                    url = request.toString();
//                }
                        return false;
//                Logger.d(url);
//                    view.loadUrl(url);
////                else
////                    ActivityUtils.showToast(WorkWebActivity.this, "非音基应用选项，不可操作！");
                    }

//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        return "bilibili".equalsIgnoreCase(Uri.parse(url).getScheme());
//
//                    }
                });
            } else {
                Uri uri = Uri.parse(filepath);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }

    }

    private void initView() {

        if (getIntent().getStringExtra("filePath") != null && (!getIntent().getStringExtra("filePath").equals(""))) {
            filepath = getIntent().getStringExtra("filePath");
        }
        if (getIntent().getStringExtra("name") != null && (!getIntent().getStringExtra("name").equals(""))) {
            title_name = getIntent().getStringExtra("name");
        }
        if (getIntent().getStringExtra("fileType") != null && (!getIntent().getStringExtra("fileType").equals(""))) {
            fileType = getIntent().getStringExtra("fileType");
        }

        titlelayoutTitle.setText(title_name);
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        delete_text.setVisibility(View.VISIBLE);
//        delete_text.setText("");
//        delete_text.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.browser,0);

        deletepath = new File(filepath).getParentFile().getAbsolutePath().replace("file:", "");

//        int wv_brower = getResources().getIdentifier("wv_brower", "id", getPackageName());
//        mWebView = (ProgressWebview) findViewById(wv_brower);

    }

    @Override
    protected void onResume() {
        mWebView.onResume();
//        initData();

        super.onResume();

    }

    @Override
    protected void onPause() {
//        //如果当前web服务不是null
//        if (webChromeClient != null)
//            //通知app当前页面要隐藏它的自定义视图。
//            webChromeClient.onHideCustomView();
        //让webview重新加载，用于停掉音视频的声音
        mWebView.reload();
        //先重载webview再暂停webview，这时候才真正能够停掉音视频的声音，api 2.3.3 以上才能暂停
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mWebView.onPause(); // 暂停网页中正在播放的视频
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //webview停止加载
        mWebView.stopLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.deleteFolder(deletepath);
            }
        }).start();
    }

    public void btnBack(View v) {
        showExitDialog();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            showHomeDialog();
            return true;
        }
        return false;
    }

    private void showExitDialog() {
        Log.d(TAG, new File(filepath).getParentFile().getAbsolutePath());
        DialogUtils.showMyDialog(WorkWebActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "提示", "是否确认退出？", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogUtils.dismissMyDialog();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityUtils.deleteFolder(deletepath);
                    }
                }).start();
                finish();
            }
        });
    }

    private void showHomeDialog() {
        DialogUtils.showMyDialog(WorkWebActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "", "返回系统主界面，可能导致退出，是否确认返回？", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DialogUtils.dismissMyDialog();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                startActivity(intent);
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    //    /**
//     * 第 3 步: 申请权限结果返回处理
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
//            boolean isAllGranted = true;
//
//            // 判断是否所有的权限都已经授予了
//            for (int grant : grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED) {
//                    isAllGranted = false;
//                    break;
//                }
//            }
//            Log.d(TAG, "isAllGranted222====" + isAllGranted);
//            if (isAllGranted) {
//                // 如果所有的权限都授予了, 则执行备份代码
//                jstoandroid.doBackup(file_name);
//
//            } else {
//                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
//                jstoandroid.openAppDetails();
//            }
//        }
//
//    }
}
