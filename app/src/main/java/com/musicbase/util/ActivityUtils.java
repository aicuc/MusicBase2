package com.musicbase.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.R.attr.contextPopupMenuStyle;
import static android.R.attr.type;

/**
 * Created by BAO on 2018-09-14.
 */

public class ActivityUtils {
    // 获取屏幕的宽
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        Log.i("MyGameView", "width : " + widthPixels);
        return widthPixels;
    }

    // 获取屏幕的高
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    private static long lastShowTime = 0l;
    private static String lastShowMsg = null;
    private static String curShowMsg = null;

    // 显示吐司
    public static void showToast(Context context, String s) {
        curShowMsg = s.toString();
        long curShowTime = System.currentTimeMillis();
        if (curShowMsg.equals(lastShowMsg)) {
            if (curShowTime - lastShowTime > 2000) {
                try {
                    Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } catch (Exception e) {
                    // TODO: handle exception
                }
                lastShowTime = curShowTime;
                lastShowMsg = curShowMsg;
            }
        } else {
            try {
                Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } catch (Exception e) {
                // TODO: handle exception
            }
            lastShowTime = curShowTime;
            lastShowMsg = curShowMsg;
        }
    }

    /**
     * 判断网络连接是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断GPS是否打开
     *
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        List<String> accessibleProviders = lm.getProviders(true);
        return accessibleProviders != null && accessibleProviders.size() > 0;
    }

    /**
     * 判断WIFI是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断是否是3G网络
     *
     * @param context
     * @return
     */
    public static boolean is3rd(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public static boolean NetSwitch(Context context, boolean online) {
        if (online) {
            return ActivityUtils.isNetworkAvailable(context);
        } else {
            return !ActivityUtils.is3rd(context);
        }
    }

    /**
     * 删除整个文件夹里面的所有文件
     *
     * @param bookId
     * @return
     */
    public static boolean deleteFolder(String path) {
        try {

            File file = new File(path);
            // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File delfile = new File(path + File.separator + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                        System.out.println(delfile.getAbsolutePath() + "删除文件成功");
                    } else if (delfile.isDirectory()) {
                        deleteFolder(path + File.separator + filelist[i]);
                    }
                }
                System.out.println(file.getAbsolutePath() + "删除成功");
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("deletefile() Exception:" + e.getMessage());
        }
        return true;
    }


    /**
     * 删除sd卡中的文件
     *
     * @param bookId
     * @return
     */
    public static void deleteBookFormSD(Context context, String bookId, String name) {
        if (isExistByName(context, bookId, name)) {
            File file = new File(getSDPath(context, bookId), name);
            boolean b = file.delete();
            Log.d("deleteBookFormSD", "deleteBookFormSD===" + b);
        }
    }

    public static boolean deleteFoder(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                if (files != null) {
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFoder(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
            }
            boolean isSuccess = file.delete();
            if (!isSuccess) {
                return false;
            }
        }
        Log.d("deleteBookFormSD", "deleteFoder===true");
        return true;
    }

    /**
     * 删除sd卡中的文件
     *
     * @param bookId
     * @return
     */
    public static boolean deleteBookFormSD(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public final static String RECORD_DIR = "record";
    public final static String PICTURE_DIR = "picture";

    public static File getSDPath(Context context, String foldname) {
        File file = context.getExternalFilesDir(foldname);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSDPath(Context context, String foldname, String filename) {
        File file = context.getExternalFilesDir(foldname);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSDCachePath(Context context) {
        File file = context.getExternalCacheDir();
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 判断sd卡中的文件是否存在
     *
     * @param bookname
     * @return
     */
    public static Boolean isExistByName(Context context, String bookId, String bookName) {
        if (null == getSDPath(context, bookId)) {
            return false;
        }
        if (TextUtils.isEmpty(bookName)) {
            bookName = "bookNmae";
        }
        File file = new File(getSDPath(context, bookId), bookName);
        return file.exists();
    }

//    /**
//     * 解密文件
//     *
//     * @param soureFileName 源文件
//     * @param saveFileName  目标文件
//     */
//
//    public static void unLock(String soureFileName, String saveFileName) {
//        File soureFile = new File(soureFileName);
//        File saveFile = new File(saveFileName);
//        if (saveFile.exists())
//            return;
//        ZipService.unLockFile(soureFile, saveFile);
//
//    }

    public static void processError(Context context,Object data) {
        int status = 0;
        // 根据服务器返回的网络错误，给toast提示
        try {
            ((Throwable) data).printStackTrace();
            Throwable throwable = (Throwable) data;

            JSONObject object = new JSONObject(
                    throwable.getMessage());
            String des = object.optString("detail");
            status = object.optInt("status");
            Log.e("SMSSDK","SMSSDK processError==status=="+status+"----des="+des);
            if (!TextUtils.isEmpty(des)) {
                showToast(context,des);
                return;
            }
        } catch (Exception e) {
//            Log.w(TAG, "", e);
        }
        // 如果木有找到资源，默认提示
//        int resId = DemoResHelper.getStringRes(getApplicationContext(),
//                "smsdemo_network_error");
//        String netErrMsg = getApplicationContext().getResources().getString(resId);
//        showErrorToast(netErrMsg);
    }
}
