package com.musicbase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.multidex.MultiDexApplication;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.mob.MobSDK;
import com.musicbase.preferences.Preferences;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.qq.e.comm.managers.GDTAdSdk;
import com.umeng.commonsdk.UMConfigure;

import org.xutils.x;

/**
 * Created by BAO on 2018-09-13.
 */

public class MyApplication extends MultiDexApplication {

    public static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        initOkgo();
        //        CrashHandler crashHandler = CrashHandler.getInstance();
        //        crashHandler.init(getApplicationContext());


        //        JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
        //        JPushInterface.init(this);
        // 开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
        //        Config.DEBUG = false;
        //        Config.isJumptoAppStore = true;
        //        UMShareAPI.get(this);
        // 初始化SDK

        x.Ext.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder().showThreadInfo(false)  //（可选）是否显示线程信息。 默认值为true
                .methodCount(2)         // （可选）要显示的方法行数。 默认2
                .methodOffset(7)        // （可选）隐藏内部方法调用到偏移量。 默认5
                .tag("PNDOO")   //（可选）每个日志的全局标记。 默认PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));


        UMConfigure.preInit(this, null, null);
//        MobSDK.submitPolicyGrantResult(true, null);
        // GDTAdSdk.init 延迟到用户同意隐私政策后调用，避免在同意前获取 Android_ID
        // GDTAdSdk.init(this, Preferences.APPID);

        //屏蔽android9 webview加载问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            String packageName = this.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
    }

    /**
     * 在用户同意隐私政策后调用此方法初始化需要采集设备信息的 SDK。
     * 包含防重复初始化逻辑。
     */
    private static boolean sSdkInitialized = false;

    public static synchronized void initSDKsAfterConsent(Context context) {
        if (sSdkInitialized) {
            return;
        }
        sSdkInitialized = true;
        GDTAdSdk.init(context.getApplicationContext(), Preferences.APPID);
        MobSDK.submitPolicyGrantResult(true, null);
        Log.d("MyApplication", "GDTAdSdk & MobSDK initialized after user consent");
    }

    private String getProcessName(Context context) {
        if (context == null)
            return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    private void initOkgo() {
        OkGo.getInstance().init(this).setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE);
    }

    //各个平台的配置，建议放在全局Application或者程序入口
    //    {
    //        PlatformConfig.setWeixin("wxb5c69e77124b6557", "d7cf4e1d3096f8cbfbfa7f91dcd7916c");
    //        PlatformConfig.setSinaWeibo("491304448", "f6192e4455dc39a492cd523ccdab8c60","http://www.pndoo.com");
    //        PlatformConfig.setQQZone("1105281436", "O5SPjXocx1QWTdsf");
    //    }

    private static Activity music_activity;

    public static void addMusicActivity(Activity activity) {
        music_activity = activity;
    }

    public static void stopMusicActivity() {
        if (music_activity != null)
            music_activity.finish();
    }
}
