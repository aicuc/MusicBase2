package com.musicbase.ui.superplayer.playerview;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.musicbase.preferences.Preferences;
import com.musicbase.ui.superplayer.SuperPlayerModel;
import com.orhanobut.logger.Logger;
import com.tencent.liteav.basic.log.TXCLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liyuejiao on 2018/7/3.
 * 获取点播信息
 */

public class SuperVodListLoader {

    private static final String TAG = "SuperVodListLoader";
    private static SuperVodListLoader sInstance;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private boolean mIsHttps;
    private final String BASE_URL = "http://playvideo.qcloud.com/getplayinfo/v2";
    private final String BASE_URLS = "https://playvideo.qcloud.com/getplayinfo/v2";
    private OnVodInfoLoadListener mOnVodInfoLoadListener;
    private ArrayList<SuperPlayerModel> mDefaultList;


    public SuperVodListLoader() {
        mHandlerThread = new HandlerThread("SuperVodListLoader");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mDefaultList = new ArrayList();

    }

//    public static SuperVodListLoader getInstance() {
//        if (sInstance == null) {
//            sInstance = new SuperVodListLoader();
//        }
//        return sInstance;
//    }

    public void setOnVodInfoLoadListener(OnVodInfoLoadListener listener) {
        mOnVodInfoLoadListener = listener;
    }



    public ArrayList<SuperPlayerModel> loadVodList(List<String> fileIds) {
        if (fileIds==null||fileIds.size()==0){
            /**
             * 处理音乐为空的情况
             */
            return mDefaultList;
        }
        mDefaultList.clear();
        for(String fileid:fileIds){
            SuperPlayerModel model = new SuperPlayerModel();
            model.appid = Preferences.DEFAULT_APPID;
            model.fileid = fileid;
            mDefaultList.add(model);
            Logger.d(model.fileid);
        }
        return mDefaultList;
    }

//    public SuperPlayerModel loadDefaultLiveVideo() {
//        SuperPlayerModel model1 = new SuperPlayerModel();
//        model1.appid = 1253131631;
//        model1.title = "游戏直播-支持时移播放，清晰度无缝切换";
//        model1.placeholderImage = "http://xiaozhibo-10055601.file.myqcloud.com/coverImg.jpg";
//        model1.videoURL = "http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4.flv";
//
//        model1.multiVideoURLs = new ArrayList<>(3);
//        model1.multiVideoURLs.add(new SuperPlayerUrl("超清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4.flv"));
//        model1.multiVideoURLs.add(new SuperPlayerUrl("高清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_900.flv"));
//        model1.multiVideoURLs.add(new SuperPlayerUrl("标清","http://5815.liveplay.myqcloud.com/live/5815_89aad37e06ff11e892905cb9018cf0d4_550.flv"));
//
//        return model1;
//    }
    public void getVodInfoOneByOne(ArrayList<SuperPlayerModel> superPlayerModels) {
        if (superPlayerModels == null || superPlayerModels.size() == 0)
            return;

//        for (SuperPlayerModel model : superPlayerModels) {
//            getVodByFileId(model);
//        }
        this.superPlayerModels = superPlayerModels;
        singleAdd(index);
    }

    ArrayList<SuperPlayerModel> superPlayerModels;
    int index = 0;
    public void singleAdd(int index){
        if(index<superPlayerModels.size()){
            getVodByFileId(superPlayerModels.get(index));
        }else{
            superPlayerModels = null;
            this.index = 0;
        }
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
                        parseJson(model, s);
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
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

//    public void getLiveList(final OnListLoadListener listener) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                String urlStr = "http://xzb.qcloud.com/get_live_list";
//
//                OkHttpClient okHttpClient = new OkHttpClient();
//                okHttpClient.newBuilder().connectTimeout(5, TimeUnit.SECONDS);
//                Request request = new Request.Builder().url(urlStr).build();
//                Call call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        //获取请求信息失败
//                        if (listener != null) {
//                            listener.onFail(-1);
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String content = response.body().string();
//                        try {
//                            JSONObject jsonObject = new JSONObject(content);
//                            int code = jsonObject.getInt("code");
//                            if (code != 200) {
//                                String message = jsonObject.getString("message");
//                                if (listener != null) {
//                                    listener.onFail(-1);
//                                }
//                                TXCLog.e(TAG, message);
//                                return;
//                            }
//                            JSONObject data = jsonObject.getJSONObject("data");
//                            JSONArray liveList = data.getJSONArray("list");
//                            ArrayList<SuperPlayerModel> modelList = new ArrayList<>();
//                            for(int i = 0; i < liveList.length(); i++) {
//                                JSONObject playItem = liveList.getJSONObject(i);
//                                SuperPlayerModel model = new SuperPlayerModel();
//                                model.appid = playItem.optInt("appId",0);
//                                model.title = playItem.optString("name","");
//                                model.placeholderImage = playItem.optString("coverUrl","");
//
//                                JSONArray urlList = playItem.getJSONArray("playUrl");
//                                if (urlList.length() > 0) {
//
//                                    model.multiVideoURLs = new ArrayList<>(urlList.length());
//                                    model.videoURL = urlList.getJSONObject(0).optString("url","");
//                                    for(int j = 0; j < urlList.length(); j++) {
//                                        JSONObject urlItem = urlList.getJSONObject(j);
//                                        model.multiVideoURLs.add(new SuperPlayerUrl(urlItem.optString("title",""),urlItem.optString("url","")));
//                                    }
//                                }
//
//                                modelList.add(model);
//                            }
//                            if (listener != null) {
//                                listener.onSuccess(modelList);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }

    private void parseJson(SuperPlayerModel superPlayerModel, String content) {
        if (TextUtils.isEmpty(content)) {
            TXCLog.e(TAG, "parseJson err, content is empty!");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(content);
            int code = jsonObject.getInt("code");
            if (code != 0) {
                String message = jsonObject.getString("message");
                if (mOnVodInfoLoadListener != null) {
                    mOnVodInfoLoadListener.onFail(-1);
                }
                TXCLog.e(TAG, message);
                return;
            }
            TXPlayInfoResponse playInfoResponse = new TXPlayInfoResponse(jsonObject);
            superPlayerModel.placeholderImage = playInfoResponse.coverUrl();

            TXPlayInfoStream stream = playInfoResponse.getSource();
            if (stream != null) {
                superPlayerModel.duration = stream.getDuration();
            }
            superPlayerModel.title = playInfoResponse.description();
            if (superPlayerModel.title == null || superPlayerModel.title.length() == 0) {
                superPlayerModel.title = playInfoResponse.name();
            }
            if (mOnVodInfoLoadListener != null) {
                mOnVodInfoLoadListener.onSuccess(superPlayerModel);
            }
            index++;
            singleAdd(index);
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
        void onSuccess(SuperPlayerModel superPlayerModelList);

        void onFail(int errCode);
    }

    public interface OnListLoadListener {
        void onSuccess(ArrayList<SuperPlayerModel> superPlayerModelList);

        void onFail(int errCode);
    }
}
