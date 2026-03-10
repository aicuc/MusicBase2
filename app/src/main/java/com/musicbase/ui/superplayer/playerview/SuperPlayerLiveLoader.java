//package com.musicbase.ui.superplayer.playerview;
//
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Looper;
//import android.text.TextUtils;
//
//import com.tencent.liteav.basic.log.TXCLog;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * Created by liyuejiao on 2018/7/12.
// */
//
//public class SuperPlayerLiveLoader {
//
//
//    private static final String TAG = "SuperPlayerLiveLoader";
//    private Handler mMainHandler;
//    private Handler mHandler;
//    private HandlerThread mHandlerThread;
//
//    private boolean mIsHttps;
//    private final String BASE_URL = "http://playvideo.qcloud.com/getplayinfo/v2";
//    private final String BASE_URLS = "https://playvideo.qcloud.com/getplayinfo/v2";
//    private OnVodInfoLoadListener mOnVodInfoLoadListener;
//
//    public SuperPlayerLiveLoader() {
//        mHandlerThread = new HandlerThread("SuperVodListLoader");
//        mHandlerThread.start();
//        mHandler = new Handler(mHandlerThread.getLooper());
//        mMainHandler = new Handler(Looper.getMainLooper());
//    }
//
//    public void setOnVodInfoLoadListener(OnVodInfoLoadListener listener) {
//        mOnVodInfoLoadListener = listener;
//    }
//
//    public void getLiveByURL(final String videoURL) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS);
//                Request request = new Request.Builder().url(videoURL).build();
//                Call call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        //获取请求信息失败
//                        if (mOnVodInfoLoadListener != null) {
//                            mOnVodInfoLoadListener.onFail(-1);
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String content = response.body().string();
//                        if (TextUtils.isEmpty(content)) {
//                            mMainHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (mOnVodInfoLoadListener != null) {
//                                        mOnVodInfoLoadListener.onFail(-1);
//                                    }
//                                }
//                            });
//                            return;
//                        }
//
//                        TXCLog.i("lyj", "content:" + content);
//                        String time = parseContent(content); //#EXT-TX-TS-START-TIME"
//                        if (TextUtils.isEmpty(time)) {
//                            mMainHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (mOnVodInfoLoadListener != null) {
//                                        mOnVodInfoLoadListener.onFail(-1);
//                                    }
//                                }
//                            });
//                            return;
//                        }
//
//                        final long liveTime = Long.parseLong(time);
//                        final long currentTime = System.currentTimeMillis() / 1000;
//
//                        mMainHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mOnVodInfoLoadListener != null) {
//                                    mOnVodInfoLoadListener.onSuccess(liveTime);
//                                }
//                            }
//                        });
//                        TXCLog.i("lyj", "time:" + time + ",currentTime:" + currentTime + ",diff:" + (currentTime - liveTime));
//                    }
//                });
//            }
//        });
//    }
//
//    private String parseContent(String content) {
//        if (content.contains("#EXT-TX-TS-START-TIME")) {
//            int from = content.indexOf("#EXT-TX-TS-START-TIME:") + 22;
//            if (from > 0) {
//                String subString = content.substring(from);
//                int to = subString.indexOf("#");
//                if (to > 0) {
//                    String startTime = subString.substring(0, to).replaceAll("\r\n", "");
//                    return startTime;
//                }
//            }
//        }
//        return null;
//    }
//
//    public interface OnVodInfoLoadListener {
//        void onSuccess(long playTime);
//
//        void onFail(int errCode);
//    }
//
//}
