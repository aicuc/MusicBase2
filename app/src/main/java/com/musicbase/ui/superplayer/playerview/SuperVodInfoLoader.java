package com.musicbase.ui.superplayer.playerview;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;

import com.musicbase.ui.superplayer.SuperPlayerModel;
import com.tencent.liteav.basic.log.TXCLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by liyuejiao on 2018/7/3.
 * 超级播放器内部获取点播信息
 */

public class SuperVodInfoLoader {

    private static final String TAG = "SuperVodInfoLoader";
    private Handler mMainHandler;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private boolean mIsHttps;
    private final String BASE_URL = "http://playvideo.qcloud.com/getplayinfo/v2";
    private final String BASE_URLS = "https://playvideo.qcloud.com/getplayinfo/v2";
    private OnVodInfoLoadListener mOnVodInfoLoadListener;

    public SuperVodInfoLoader() {
        mHandlerThread = new HandlerThread("SuperVodListLoader");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void setOnVodInfoLoadListener(OnVodInfoLoadListener listener) {
        mOnVodInfoLoadListener = listener;
    }

    public void getVodByFileId(final SuperPlayerModel model) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String urlStr = makeUrlString(model.appid, model.fileid, null, null, -1, null);

                RequestParams params = new RequestParams(urlStr);
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        parseJson(s);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        //获取请求信息失败
                        if (mOnVodInfoLoadListener != null) {
                            mOnVodInfoLoadListener.onFail(-1);
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });

            }
        });
    }

    private void parseJson(String content) {
        if (TextUtils.isEmpty(content)) {
            TXCLog.e(TAG, "parseJson err, content is empty!");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(content);
            int code = jsonObject.getInt("code");
            if (code != 0) {
                String message = jsonObject.getString("message");
                TXCLog.e(TAG, message);
                return;
            }
            final TXPlayInfoResponse playInfoResponse = new TXPlayInfoResponse(jsonObject);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOnVodInfoLoadListener != null) {
                        mOnVodInfoLoadListener.onSuccess(playInfoResponse);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String makeUrlString(int appId, String fileId, String timeout, String us, int exper, String sign) {
        String urlStr;
        if (mIsHttps) {
            urlStr = String.format("%s/%d/%s", BASE_URL, appId, fileId);
        } else {
            urlStr = String.format("%s/%d/%s", BASE_URLS, appId, fileId);
        }
        String query = makeQueryString(timeout, us, exper, sign);
        if (query != null) {
            urlStr = urlStr + "?" + query;
        }
        return urlStr;
    }

    private String makeQueryString(String timeout, String us, int exper, String sign) {
        StringBuilder str = new StringBuilder();
        if (timeout != null) {
            str.append("t=" + timeout + "&");
        }
        if (us != null) {
            str.append("us=" + us + "&");
        }
        if (sign != null) {
            str.append("sign=" + sign + "&");
        }
        if (exper >= 0) {
            str.append("exper=" + exper + "&");
        }
        if (str.length() > 1) {
            str.deleteCharAt(str.length() - 1);
        }
        return str.toString();
    }

    public interface OnVodInfoLoadListener {
        void onSuccess(TXPlayInfoResponse response);

        void onFail(int errCode);
    }

}
