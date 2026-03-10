//package com.musicbase.ui.superplayer.playerview;
//
//import com.tencent.liteav.basic.log.TXCLog;
//import com.tencent.liteav.demo.common.utils.TCConstants;
//import com.tencent.rtmp.TXLog;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//
///**
// * Created by liyuejiao on 2018/7/19.
// */
//
//public class LogReport {
//
//    private static final String TAG = "LogReport";
//    private OkHttpClient mHttpClient;
//
//    private LogReport() {
//        mHttpClient = new OkHttpClient().newBuilder()
//                .connectTimeout(10, TimeUnit.SECONDS)
//                .readTimeout(10, TimeUnit.SECONDS)
//                .writeTimeout(10, TimeUnit.SECONDS)
//                .build();
//    }
//
//    private static class Holder {
//        private static LogReport instance = new LogReport();
//    }
//
//    public static LogReport getInstance() {
//        return Holder.instance;
//    }
//
//    public void uploadLogs(String action, String userName, long code, String errorMsg, okhttp3.Callback callback) {
//        TXLog.w(TAG, "uploadLogs: errorMsg " + errorMsg);
//        String reqUrl = TCConstants.DEFAULT_ELK_HOST;
//        String body = "";
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("action", action);
//            jsonObject.put("action_result_code", code);
//            jsonObject.put("action_result_msg", errorMsg);
//            jsonObject.put("type", "superplayer");
//            jsonObject.put("userName", userName);
//            jsonObject.put("platform", "android");
//            body = jsonObject.toString();
//            TXCLog.d(TAG, body);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Request request = new Request.Builder()
//                .url(reqUrl)
//                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body))
//                .build();
//        if(callback != null) {
//            mHttpClient.newCall(request).enqueue(callback);
//        } else {
//            mHttpClient.newCall(request);
//        }
//
//    }
//}
