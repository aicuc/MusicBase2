package com.musicbase.ui.superplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.ui.superplayer.playerview.SuperPlayerConst;
import com.musicbase.ui.superplayer.playerview.SuperPlayerView;
import com.musicbase.ui.superplayer.playerview.SuperVodListLoader;
import com.musicbase.ui.superplayer.playerview.TCVodPlayerListAdapter;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;

import java.util.ArrayList;
import java.util.List;

import static com.musicbase.preferences.Preferences.DEFAULT_APPID;

/**
 * Created by liyuejiao on 2018/7/3.
 * 超级播放器主Activity
 */

public class SuperPlayerActivity extends Activity implements View.OnClickListener,
        SuperVodListLoader.OnVodInfoLoadListener, SuperPlayerView.PlayerViewCallback,
        TCVodPlayerListAdapter.OnItemClickLitener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SuperPlayerActivity";
    private static final int LIST_TYPE_LIVE = 0;
    private static final int LIST_TYPE_VOD = 1;

    private Context mContext;
    //标题
    private RelativeLayout mLayoutTitle;
    private ImageView mIvBack;
    //    private Button mBtnScan;
//    private ImageButton mBtnLink;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //超级播放器View
    private SuperPlayerView mSuperPlayerView;
    //播放列表
    private RecyclerView mVodPlayerListView;
    private TCVodPlayerListAdapter mVodPlayerListAdapter;

    //    private ImageView mIvAdd;
    //获取点播信息接口
    private SuperVodListLoader mSuperVodListLoader;

    //上传文件列表
//    private boolean mDefaultVideo;
    private String mVideoId;
//    private GetVideoInfoListListener mGetVideoInfoListListener;

    //    private ArrayList<SuperPlayerModel> mLiveList;
    private ArrayList<SuperPlayerModel> mVodList;
    private int mDataType = LIST_TYPE_LIVE;
    //    private List<ListTabItem> mListTabs;
    private int mVideoCount;
    private boolean mVideoHasPlay;
    private List<String> fileIds;
    private int current_position = 0;
    private MarqueeView marqueeView;
    private static class ListTabItem {
        public ListTabItem(int type, TextView textView, ImageView imageView, View.OnClickListener listener) {
            this.type = type;
            this.textView = textView;
            this.imageView = imageView;
            this.textView.setOnClickListener(listener);
        }

        public int type;
        public TextView textView;
        public ImageView imageView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervod_player);
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        checkPermission();
        getIntentData();
        initView();
        initData();

        mDataType = /*mDefaultVideo ? LIST_TYPE_LIVE :*/ LIST_TYPE_VOD;
        updateList(mDataType);
/**
 * 离线下载
 */
//        TXVodDownloadManager downloader = TXVodDownloadManager.getInstance();
//        downloader.setDownloadPath("<指定您的下载目录>");
//        downloader.setListener(new ITXVodDownloadListener() {
//            @Override
//            public void onDownloadStart(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
//
//            }
//
//            @Override
//            public void onDownloadProgress(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
//
//            }
//
//            @Override
//            public void onDownloadStop(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
//
//            }
//
//            @Override
//            public void onDownloadFinish(TXVodDownloadMediaInfo txVodDownloadMediaInfo) {
//
//            }
//
//            @Override
//            public void onDownloadError(TXVodDownloadMediaInfo txVodDownloadMediaInfo, int i, String s) {
//
//            }
//        });
    }

    private void getIntentData() {
        fileIds = getIntent().getStringArrayListExtra("fileIdList");
        Logger.d(fileIds);
        current_position = getIntent().getIntExtra("position", 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
//                permissions.add(Manifest.permission.CAMERA);
//            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }

    private void initView() {
        mLayoutTitle = (RelativeLayout) findViewById(R.id.layout_title);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
//        mBtnScan = (Button) findViewById(R.id.btnScan);
//        mBtnScan.setOnClickListener(this);
//        mBtnLink = (ImageButton) findViewById(R.id.webrtc_link_button);
//        mBtnLink.setOnClickListener(this);

        mSuperPlayerView = (SuperPlayerView) findViewById(R.id.superVodPlayerView);
        mSuperPlayerView.setPlayerViewCallback(this);

        mVodPlayerListView = (RecyclerView) findViewById(R.id.recycler_view);
        mVodPlayerListView.setLayoutManager(new LinearLayoutManager(this));
        mVodPlayerListAdapter = new TCVodPlayerListAdapter(this);
        mVodPlayerListAdapter.setOnItemClickLitener(this);
        mVodPlayerListView.setAdapter(mVodPlayerListAdapter);

        marqueeView = (MarqueeView) findViewById(R.id.marqueeView);
        String phone = SPUtility.getSPString(this,"username");
        String phoneNumber = phone.substring(0, 4) + "***" + phone.substring(7, phone.length());
        List<String> info = new ArrayList<>();
        info.add(phoneNumber);
        info.add(phoneNumber);
        marqueeView.startWithList(info);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_list);
        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mIvAdd = (ImageView) findViewById(R.id.iv_add);
//        mIvAdd.setOnClickListener(this);
//
//        mListTabs = new ArrayList<>();
//        mListTabs.add(LIST_TYPE_LIVE, new ListTabItem(LIST_TYPE_LIVE, (TextView) findViewById(R.id.text_live), null, this));
//        mListTabs.add(LIST_TYPE_VOD, new ListTabItem(LIST_TYPE_VOD, (TextView) findViewById(R.id.text_vod), null, this));

    }

    private void initData() {
//        mLiveList = new ArrayList<>();
        mVodList = new ArrayList<>();
//        mDefaultVideo = getIntent().getBooleanExtra(TCConstants.PLAYER_DEFAULT_VIDEO, true);
        mSuperVodListLoader = new SuperVodListLoader();
        mSuperVodListLoader.setOnVodInfoLoadListener(this);

        initSuperVodGlobalSetting();

        mVideoHasPlay = false;

        mVideoCount = 0;

        TXLiveBase.setAppID(DEFAULT_APPID + "");
    }

    private void updateData() {
        switch (mDataType) {
            case LIST_TYPE_LIVE:
                updateLiveList();
                break;
            case LIST_TYPE_VOD:
                updateVodList();
                break;
        }
    }

    private void updateLiveList() {
//        if (mDefaultVideo) {
//            ArrayList<SuperPlayerModel> superPlayerModels = mSuperVodListLoader.loadDefaultLiveList();
//            for(int i = 0; i<superPlayerModels.size(); i++) {
//                SuperPlayerModel model = superPlayerModels.get(i);
//                mVodPlayerListAdapter.addSuperPlayerModel(model);
//            }
//        } else {
//        mLiveList.clear();
//        mSuperVodListLoader.getLiveList(new SuperVodListLoader.OnListLoadListener() {
//            @Override
//            public void onSuccess(final ArrayList<SuperPlayerModel> superPlayerModelList) {
//
//                SuperPlayerActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mDataType != LIST_TYPE_LIVE) return;
//                        mVodPlayerListAdapter.clear();
//                        for (SuperPlayerModel superPlayerModel:
//                                superPlayerModelList) {
//                            mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
//                            mLiveList.add(superPlayerModel);
//                        }
//                        if (!mVideoHasPlay && !mLiveList.isEmpty()) {
//                            if (mLiveList.get(0).appid > 0) {
//                                TXLiveBase.setAppID(""+mLiveList.get(0).appid);
//                            }
//                            mSuperPlayerView.playWithMode(mLiveList.get(0));
//                            mVideoHasPlay = true;
//                        }
//                        mVodPlayerListAdapter.notifyDataSetChanged();
//
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onFail(int errCode) {
//                TXCLog.e(TAG,"updateLiveList error");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        });
//        }
//        mVodPlayerListAdapter.notifyDataSetChanged();

//        if (mIvAdd != null) {
//            mIvAdd.setVisibility(View.GONE);
//        }
    }

    private void updateVodList() {
//        if (mDefaultVideo) {
        mVodList.clear();
        ArrayList<SuperPlayerModel> superPlayerModels = mSuperVodListLoader.loadVodList(fileIds);
        mSuperVodListLoader.getVodInfoOneByOne(superPlayerModels);

//            mIvAdd.setVisibility(View.VISIBLE);
//        }
//        else {
//            mVideoId = getIntent().getStringExtra(TCConstants.PLAYER_VIDEO_ID);
//            if (!TextUtils.isEmpty(mVideoId)) {
//                playDefaultVideo(TCConstants.VOD_APPID, mVideoId);
//                mVideoHasPlay = true;
//            }
//
//            mGetVideoInfoListListener = new GetVideoInfoListListener() {
//                @Override
//                public void onGetVideoInfoList(final List<VideoInfo> videoInfoList) {
//                    if (mDataType != LIST_TYPE_VOD) return;
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mVodPlayerListAdapter.clear();
//                            mVodPlayerListAdapter.notifyDataSetChanged();
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            ArrayList<SuperPlayerModel> superPlayerModels = VideoDataMgr.getInstance().loadVideoInfoList(videoInfoList);
//                            if (superPlayerModels != null && superPlayerModels.size() != 0) {
//                                mSuperVodListLoader.getVodInfoOneByOne(superPlayerModels);
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onFail(int errCode) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(mContext, "获取已上传的视频列表失败", Toast.LENGTH_SHORT).show();
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                    });
//                }
//            };
//
//            mVodList.clear();
//            VideoDataMgr.getInstance().setGetVideoInfoListListener(mGetVideoInfoListListener);
//            VideoDataMgr.getInstance().getVideoList();
//
//            mBtnScan.setVisibility(View.GONE);
//            mIvAdd.setVisibility(View.GONE);
//        }
    }

    private void playDefaultVideo(int appid, String fileid) {
        SuperPlayerModel superPlayerModel = new SuperPlayerModel();
        superPlayerModel.appid = appid;
        superPlayerModel.fileid = fileid;
        superPlayerModel.title = "小视频-特效剪辑";
        if (superPlayerModel.appid > 0) {
            TXLiveBase.setAppID("" + superPlayerModel.appid);
        }
        mSuperPlayerView.playWithMode(superPlayerModel);
    }

    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
        // 开启悬浮窗播放
        prefs.enableFloatWindow = true;
        // 设置悬浮窗的初始位置和宽高
        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
        rect.x = 0;
        rect.y = 0;
        rect.width = 810;
        rect.height = 540;
        prefs.floatViewRect = rect;
        // 播放器默认缓存个数
        prefs.maxCacheItem = 5;
        // 设置播放器渲染模式
        prefs.enableHWAcceleration = true;
        prefs.renderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperPlayerView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAY) {
            Log.i(TAG, "onResume state :" + mSuperPlayerView.getPlayState());
            mSuperPlayerView.onResume();
            if (mSuperPlayerView.getPlayMode() == SuperPlayerConst.PLAYMODE_FLOAT) {
                mSuperPlayerView.requestPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause state :" + mSuperPlayerView.getPlayState());
        if (mSuperPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT) {
            mSuperPlayerView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSuperPlayerView.getPlayMode() != SuperPlayerConst.PLAYMODE_FLOAT) {
            mSuperPlayerView.resetPlayer();
        }
//        VideoDataMgr.getInstance().setGetVideoInfoListListener(null);
    }

    /**
     * 获取点播信息成功
     */
    @Override
    public void onSuccess(final SuperPlayerModel superPlayerModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mDataType != LIST_TYPE_VOD) return;
                mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
                mVodList.add(superPlayerModel);
            }
        });
        if (current_position < mVodList.size())
            play(current_position, mVodList.get(current_position));
    }

    /**
     * 获取点播信息失败
     *
     * @param errCode
     */
    @Override
    public void onFail(int errCode) {
        TXCLog.i(TAG, "onFail errCode:" + errCode);
    }

    @Override
    public void onItemClick(int position, SuperPlayerModel superPlayerModel) {
        play(position, superPlayerModel);
    }

    private void play(int position, SuperPlayerModel superPlayerModel) {
        if (position == mVodPlayerListAdapter.getCurrent_position())
            return;
        if (superPlayerModel.appid > 0) {
            TXLiveBase.setAppID("" + superPlayerModel.appid);
        }
        mSuperPlayerView.playWithMode(superPlayerModel);
        mVodPlayerListAdapter.setCurrent_position(position);
        mVodPlayerListAdapter.notifyDataSetChanged();
        marqueeView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_add:   //[点击+添加一个点播列表项]
//                showAddVideoDialog();
//                break;
//            case R.id.btnScan:  //[扫描二维码播放一个视频]
//                scanQRCode();
//                break;
            case R.id.iv_back:  //悬浮窗播放
//                showFloatWindow();
                finish();
                break;
            /*case R.id.text_live:
                mDataType = LIST_TYPE_LIVE;
                updateList(mDataType);
                break;
            case R.id.text_vod:
                mDataType = LIST_TYPE_VOD;
                updateList(mDataType);
                break;*/

//            case R.id.webrtc_link_button:
//                showCloudLink();
//                break;
        }
    }

    private void showCloudLink() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://cloud.tencent.com/document/product/454/18872"));
        startActivity(intent);
    }

    private void updateList(int dataType) {
//        for (ListTabItem item : mListTabs) {
//            if (item.type == dataType) {
//                item.textView.setTextColor(Color.rgb(255, 255, 255));
////                item.imageView.setVisibility(View.VISIBLE);
//            } else {
//                item.textView.setTextColor(Color.rgb(119, 119, 119));
////                item.imageView.setVisibility(View.INVISIBLE);
//            }
//        }

        mVodPlayerListAdapter.clear();
        switch (mDataType) {
//            case LIST_TYPE_LIVE:
//                if (mLiveList.isEmpty()) {
//                    updateLiveList();
//                } else {
//                    for (SuperPlayerModel superPlayerModel:
//                            mLiveList) {
//                        mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
//                    }
//                }
//                break;
            case LIST_TYPE_VOD:
                if (mVodList.isEmpty()) {
                    updateVodList();
                } else {
                    for (SuperPlayerModel superPlayerModel :
                            mVodList) {
                        mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
                    }
                }
                break;
        }

        mVodPlayerListAdapter.notifyDataSetChanged();
    }

    /**
     * 悬浮窗播放
     */
    private void showFloatWindow() {
        if (mSuperPlayerView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAY) {
            mSuperPlayerView.requestPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
        } else {
            mSuperPlayerView.resetPlayer();
            finish();
        }
    }

    /**
     * 扫描二维码
     */
//    private void scanQRCode() {
//        Intent intent = new Intent(this, QRCodeScanActivity.class);
//        startActivityForResult(intent, 100);
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (data == null || data.getExtras() == null || TextUtils.isEmpty(data.getExtras().getString("result"))) {
//            return;
//        }
//        String result = data.getExtras().getString("result");
//        if (requestCode == 200) {
//            EditText editText = (EditText) findViewById(R.id.editText);
//            editText.setText(result);
//        } else if (requestCode == 100) {
//            // 二维码播放视频
//            playNewVideo(result);
//        }
    }

    private boolean isLivePlay(SuperPlayerModel superPlayerModel) {
        String videoURL = superPlayerModel.videoURL;
        if (TextUtils.isEmpty(superPlayerModel.videoURL)) {
            return false;
        }
        if (videoURL.startsWith("rtmp://")) {
            return true;
        } else if ((videoURL.startsWith("http://") || videoURL.startsWith("https://")) && videoURL.contains(".flv")) {
            return true;
        } else {
            return false;
        }
    }

//    private void playNewVideo(String result) {
//        mVideoCount++;
//        SuperPlayerModel superPlayerModel = new SuperPlayerModel();
//        superPlayerModel.title = "测试视频" + mVideoCount;
//        superPlayerModel.videoURL = result;
//        superPlayerModel.placeholderImage = "http://xiaozhibo-10055601.file.myqcloud.com/coverImg.jpg";
//        superPlayerModel.appid = DEFAULT_APPID;
//        if (!TextUtils.isEmpty(superPlayerModel.videoURL) && superPlayerModel.videoURL.contains("5815.liveplay.myqcloud.com")) {
//            superPlayerModel.appid = 1253131631;
//            TXLiveBase.setAppID("1253131631");
//            superPlayerModel.multiVideoURLs = new ArrayList<>(3);
//            superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("超清", superPlayerModel.videoURL));
//            superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("高清", superPlayerModel.videoURL.replace(".flv", "_900.flv")));
//            superPlayerModel.multiVideoURLs.add(new SuperPlayerUrl("标清", superPlayerModel.videoURL.replace(".flv", "_550.flv")));
//        }
//        if (!TextUtils.isEmpty(superPlayerModel.videoURL) && superPlayerModel.videoURL.contains("3891.liveplay.myqcloud.com")) {
//            superPlayerModel.appid = 1252463788;
//            TXLiveBase.setAppID("1252463788");
//        }
//        mSuperPlayerView.playWithMode(superPlayerModel);
//
//        boolean needRefreshList = false;
//        if (isLivePlay(superPlayerModel)) {
//            mLiveList.add(superPlayerModel);
//            needRefreshList = mDataType == LIST_TYPE_LIVE;
//        } else {
//            mVodList.add(superPlayerModel);
//            needRefreshList = mDataType == LIST_TYPE_VOD;
//        }
//        if (needRefreshList) {
//            mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
//            mVodPlayerListAdapter.notifyDataSetChanged();
//        }
//    }

    /**
     * 点击+添加一个点播列表项
     */
//    private void showAddVideoDialog() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_vod_player_fileid, null);
//
//        dialog.setView(dialogView);
//
//        final EditText etAppId = (EditText) dialogView.findViewById(R.id.et_appid);
//        final EditText etFileId = (EditText) dialogView.findViewById(R.id.et_fileid);
//
//        if (mDataType == LIST_TYPE_VOD) {
//            dialog.setTitle("请设置AppID和FileID");
//        } else {
//            dialog.setTitle("请设置播放地址");
//            dialogView.findViewById(R.id.et_appid_text).setVisibility(View.GONE);
//            dialogView.findViewById(R.id.et_fileid_text).setVisibility(View.GONE);
//            etFileId.setVisibility(View.GONE);
//        }
//
//        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setPositiveButton(R.string.btn_ok,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        if (mDataType == LIST_TYPE_VOD) {
//                            String appId = etAppId.getText().toString();
//                            String fileId = etFileId.getText().toString();
//
//                            if (TextUtils.isEmpty(appId)) {
//                                Toast.makeText(mContext, "请输入正确的AppId", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            if (TextUtils.isEmpty(fileId)) {
//                                Toast.makeText(mContext, "请输入正确的FileId", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            int appid;
//                            try {
//                                appid = Integer.parseInt(appId);
//                            } catch (NumberFormatException e) {
//                                Toast.makeText(mContext, "请输入正确的AppId", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//
//                            SuperPlayerModel superPlayerModel = new SuperPlayerModel();
//                            superPlayerModel.appid = appid;
//                            superPlayerModel.fileid = fileId;
//
//                            // 尝试请求fileid信息
//                            SuperVodListLoader loader = new SuperVodListLoader();
//                            loader.setOnVodInfoLoadListener(new SuperVodListLoader.OnVodInfoLoadListener() {
//                                @Override
//                                public void onSuccess(final SuperPlayerModel superPlayerModel) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mVodPlayerListAdapter.addSuperPlayerModel(superPlayerModel);
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onFail(int errCode) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(mContext, "fileid请求失败", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            });
//                            loader.getVodByFileId(superPlayerModel);
//                        } else {
//                            String playUrl = etAppId.getText().toString();
//                            if (TextUtils.isEmpty(playUrl)) {
//                                Toast.makeText(mContext, "请输入正确的播放地址", Toast.LENGTH_SHORT).show();
//                            } else {
//                                playNewVideo(playUrl);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mSwipeRefreshLayout.setRefreshing(false);
//                                    }
//                                });
//                            }
//                        }
//
//                    }
//                });
//        dialog.show();
//    }
    @Override
    public void hideViews() {
        mLayoutTitle.setVisibility(View.GONE);
//        if (mIvAdd != null) {
//            mIvAdd.setVisibility(View.GONE);
//        }
    }

    @Override
    public void showViews() {
        mLayoutTitle.setVisibility(View.VISIBLE);
//        if (mIvAdd != null) {
//            mIvAdd.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onQuit(int playMode) {
        if (playMode == SuperPlayerConst.PLAYMODE_FLOAT) {
            mSuperPlayerView.resetPlayer();
            finish();
        } else if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            if (mSuperPlayerView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAY) {
//                // 返回桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                mSuperPlayerView.resetPlayer();
                finish();
            }
        }
    }

    @Override
    public void onRefresh() {
//        if (mDefaultVideo) {
        mSwipeRefreshLayout.setRefreshing(false);
        return;
//        }
//        if (mDataType == LIST_TYPE_VOD) {
//            mVodList.clear();
//            VideoDataMgr.getInstance().getVideoList();
//        } else {
//            updateLiveList();
//        }

    }
    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                //                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("下载需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
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
}
