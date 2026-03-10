package com.musicbase.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.DetailAdapter;
import com.musicbase.download2.TestService;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.db.DataBaseHelper;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.FolderBean;
import com.musicbase.entity.ResourseBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.superplayer.SuperPlayerActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ZipUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.musicbase.preferences.Preferences.FILE_DOWNLOAD_URL;
import static com.musicbase.preferences.Preferences.MUSIC_ID;
import static com.musicbase.preferences.Preferences.MUSIC_NAME;
import static com.musicbase.preferences.Preferences.MUSIC_TITLE;
import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class FolderActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private RecyclerView recyclerview;
    private TextView price_text;
    private TextView price;
    private LinearLayout btn_buy;
    private String resourceId;
    private String resourceFileName;
    private String folderPrice;
    private String img_url_str;
    DownloadManager downloadManager;
    private DetailAdapter adapter;
    private List<DetailBean.DataBean.ResourceListBean> resourceList;
    BroadcastReceiver broadcastReceiver;
    private String[] myPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    int courseId;
    DetailBean.DataBean.UserAvatarsBean userAvatars;
    DetailBean.DataBean.ResourceListBean autoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        resourceId = getIntent().getStringExtra("resourceId");
        resourceFileName = getIntent().getStringExtra("resourceFileName");
        folderPrice = getIntent().getStringExtra("folderPrice");
        img_url_str = getIntent().getStringExtra("img_url_str");
        courseId = getIntent().getIntExtra("courseId", 0);
        autoBean = (DetailBean.DataBean.ResourceListBean) getIntent().getSerializableExtra("autoBean");
        Log.e("courseId", "courseId folder==" + courseId);
        //        userAvatars = (DetailBean.DataBean.UserAvatarsBean) getIntent().getSerializableExtra("userAvatars");
        initView();
        requestData();

    }

    private final String TAG = getClass().getSimpleName();

    private void requestData() {
        OkGo.<String>post(Preferences.GET_FOLDER).tag(this).params("userId", SPUtility.getUserId(this) + "").params("AppInfo", appInfo).params("resourceId", resourceId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Log.e(TAG, "FolderBean=====" + response.body());
                FolderBean bean = gson.fromJson(response.body(), FolderBean.class);
                if (bean != null) {
                    resourceList = bean.getData();
                    //                    folderPrice=bea
                    //                    transforToData();
                    initData();
                }
            }

            @Override
            public void onError(Response<String> response) {
                //                Logger.e(response.getRawResponse());
                ActivityUtils.showToast(FolderActivity.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                //                if (refresh_view.isRefreshing())
                //                    refresh_view.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                FolderBean bean = gson.fromJson(response.body(), FolderBean.class);
                if (bean != null) {
                    resourceList = bean.getData();
                    //                    transforToData();
                    initData();
                }
            }
        });
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        titlelayout_title.setText(resourceFileName);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        price_text = (TextView) findViewById(R.id.price_text);
        price = (TextView) findViewById(R.id.price);
        price.setOnClickListener(this);
        btn_buy = (LinearLayout) findViewById(R.id.btn_buy);


        titlelayout_back.setOnClickListener(this);
        downloadManager = DownloadManager.getInstance(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        //        recyclerview.setLayoutManager(new LinearLayoutManager(this) {
        //            @Override
        //            public boolean canScrollVertically() {
        //                return false;
        //            }
        //        });
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_shape));
        recyclerview.addItemDecoration(divider);
        adapter = new DetailAdapter(this, R.layout.item_course_catalog, null);
        if (autoBean != null)
            adapter.setAutoId(autoBean.getResourceId());
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (resourceList.get(position).getIsFolder() == 1) {
                    //跳转文件夹
                    Intent intent = new Intent(FolderActivity.this, FolderActivity.class);
                    intent.putExtra("resourceId", String.valueOf(resourceList.get(position).getResourceId()));
                    intent.putExtra("resourceFileName", String.valueOf(resourceList.get(position).getResourceFileName()));
                    intent.putExtra("folderPrice", String.valueOf(resourceList.get(position).getCurrentPrice()));
                    intent.putExtra("img_url_str", img_url_str);
                    intent.putExtra("courseId", courseId);
                    //                    intent.putExtra("userAvatars",userAvatars);
                    startActivity(intent);
                } else if (resourceList.get(position).getIsPay() == 0) {
                    click(position);
                    openAttach(resourceList.get(position).getResourceId() + "");
                } else {
                    if (resourceList.get(position).getCurrentPrice().equals("0.00"))
                        ActivityUtils.showToast(FolderActivity.this, "不能单独购买");
                    else {
                        orderPrice = resourceList.get(position).getCurrentPrice();
                        orderName = resourceList.get(position).getResourceFileName();
                        getOrder(BUY_SINGLE, resourceList.get(position).getResourceId() + "");
                    }
                }
            }
        });
        recyclerview.setAdapter(adapter);
        initDownload(this);
        initBroadcast();

    }

    /**
     * 打开附件 统计信息
     */
    private void openAttach(String resourceId) {
        OkGo.<String>post(Preferences.OPEN_ATTACH).tag(this).params("userId", SPUtility.getUserId(this) + "").params("resourceId", resourceId).params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response.body());
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.body());
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:
                finish();
                break;
            case R.id.price:
                getOrder(BUY_ALL, resourceId + "");
                break;
        }
    }

    /**
     * 设置download监听
     *
     * @param cotext
     */
    public void initDownload(Context cotext) {
        downloadManager.removeDownloadListener();
        downloadManager.addDownloadListener(new DownloadManager.DownloadListener() {

            @Override
            public void onUpdateProgress(DocInfo info) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.test");// action与接收器相同
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);
                intent.putExtra("bundle", bundle);
                intent.putExtra("flag", "update");
                sendBroadcast(intent);
            }

            @Override
            public void onDownloadFailed(DocInfo info) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.test");// action与接收器相同
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);
                intent.putExtra("bundle", bundle);
                intent.putExtra("flag", "failed");
                sendBroadcast(intent);
            }

            @Override
            public void onDownloadCompleted(DocInfo info) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.test");// action与接收器相同
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", info);
                intent.putExtra("bundle", bundle);
                intent.putExtra("flag", "success");
                sendBroadcast(intent);

            }
        });
    }

    private void click(int position) {
        DetailBean.DataBean.ResourceListBean item = resourceList.get(position);
        switch (item.getResourceType()) {
            case "cloudVideo":
                ResourseBean bean = getVideoResourse(item.getBunchPlant());
                Intent intent = new Intent(this, SuperPlayerActivity.class);
                intent.putExtra("position", bean.getPosition());
                intent.putStringArrayListExtra("fileIdList", (ArrayList<String>) bean.getFileIds());
                startActivity(intent);
                break;
            case "cloudAudio":
                Intent intentAudio = new Intent(this, MusicActivity.class);
                //                MUSIC_NAME = item.getResourceFileName();
                //                MUSIC_TITLE = data.getCourseName();
                //                MUSIC_ID = item.getBunchPlant();
                intentAudio.putExtra("fileId", item.getBunchPlant());
                intentAudio.putExtra("resourceFileName", item.getResourceFileName());
                intentAudio.putExtra("courseName", resourceFileName);
                startActivity(intentAudio);
                break;
            case "mp4":
                if (TextUtils.isEmpty(item.getOnlineLinks()))
                    downloadOrplay(position);
                else {
                    Intent mp4Intnet = new Intent(this, VideoActivity.class);
                    mp4Intnet.putExtra("Video_url", "file://" + ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName());
                    startActivity(mp4Intnet);
                }
                break;
            case "MP3":
            case "mp3":
                if (TextUtils.isEmpty(item.getOnlineLinks()))
                    downloadOrplay(position);
                else {
                    Intent intentMP3 = new Intent(this, MusicActivity.class);
                    //                    MUSIC_NAME = item.getResourceFileName();
                    //                    MUSIC_TITLE = data.getCourseName();
                    //                    MUSIC_ID = "file://" + ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName();
                    intentMP3.putExtra("fileId", item.getBunchPlant());
                    intentMP3.putExtra("resourceFileName", item.getResourceFileName());
                    intentMP3.putExtra("courseName", resourceFileName);
                    startActivity(intentMP3);
                }

                break;
            case "url":
                if (item.getAllowOpenInBrowser() == 2) {
                    Intent intent6 = new Intent();
                    intent6.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(item.getResourceSaveName());
                    intent6.setData(content_url);
                    startActivity(intent6);
                } else {
                    Intent intent3 = new Intent(FolderActivity.this, BrowerActivity.class);
                    intent3.putExtra("filePath", item.getResourceSaveName());
                    intent3.putExtra("name", item.getResourceFileName());
                    intent3.putExtra("allowOpenInBrowser", item.getAllowOpenInBrowser());
                    startActivity(intent3);
                }
                break;
            case "appJump":
                /**
                 * 音基 pndoo://jiayue:8888/home?id=
                 */
                Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getAppJumpInfoBean().getAndroidSucc()));
                intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent4, 0);
                boolean isValid = !activities.isEmpty();
                if (isValid) {
                    startActivity(intent4);
                } else {
                    DialogUtils.showMyDialog(this, Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "本地未找到应用，前往下载？", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getAppJumpInfoBean().getAndroidFail()));
                            intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent5);
                        }
                    });
                }
                break;
            default:
                if (TextUtils.isEmpty(item.getOnlineLinks()))
                    downloadOrplay(position);
                else {
                    Intent intent2 = new Intent(this, BrowerActivity.class);
                    intent2.putExtra("filePath", item.getOnlineLinks());
                    intent2.putExtra("name", item.getResourceFileName());
                    intent2.putExtra("allowOpenInBrowser", item.getAllowOpenInBrowser());
                    Logger.d(item.getOnlineLinks());
                    Logger.d(item.getResourceFileName());
                    startActivity(intent2);
                }
                break;
        }
    }

    private void downloadOrplay(int position) {
        DetailBean.DataBean.ResourceListBean item = resourceList.get(position);
        String saveName = "12345asdfewq___";
        if (item.getResourceType().equals("proto_testPaper")) {
            String[] ss = item.getTestPaperInfo().getPackSavePath().split("/");
            saveName = ss[2];
        }
        if (!ActivityUtils.isExistByName(FolderActivity.this, item.getResourceId() + "", item.getResourceSaveName()) && !ActivityUtils.isExistByName(FolderActivity.this, item.getResourceId() + "", saveName)) {
            if (!ActivityUtils.NetSwitch(FolderActivity.this, Boolean.parseBoolean(SPUtility.getSPString(FolderActivity.this, "switchKey")))) {
                ActivityUtils.showToast(FolderActivity.this, "请在有WIFI的情况下下载");
                return;
            }
            Boolean isAllGranted = checkPermissionAllGranted(myPermissions);
            if (!isAllGranted) {
                ActivityCompat.requestPermissions(this, myPermissions, 0);
                return;
            }
            ActivityUtils.showToast(this, "加入同步列表");
            //            if (attachTwos.get(position).getAttachTwoType().endsWith("lrc")) {
            //                downLoadFile(attachTwos.get(position).getAttachTwoPath() + attachTwos.get(position).getAttachTwoSaveName(), attachTwos.get(position).getAttachTwoName() + "." + attachTwos
            //                        .get(position).getAttachTwoType(), fujianName);
            //            } else {
            downLoadFile(item);
            //            }


        } else {
            DataBaseHelper helper = new DataBaseHelper(getBaseContext());
            List<DocInfo> infos = helper.getInfo2(item.getResourceId() + "");
            if (item.getResourceType().equals("proto_testPaper")) {
                String[] ss = item.getTestPaperInfo().getPackSavePath().split("/");
                saveName = ss[2];
            } else
                saveName = item.getResourceSaveName();
            for (DocInfo docInfo : infos) {
                if (saveName.equals(docInfo.getName())) {
                    Logger.d(docInfo.getStatus());
                    if (docInfo.getStatus() == DataBaseFiledParams.DONE) {
                        break;
                    } else if (docInfo.getStatus() == DataBaseFiledParams.LOADING) {
                        docInfo.setStatus(DataBaseFiledParams.PAUSING);
                        downloadManager.pause(docInfo);
                    } else if (docInfo.getStatus() == DataBaseFiledParams.PAUSING) {
                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                        downloadManager.startForActivity(this, docInfo);
                    } else if (docInfo.getStatus() == DataBaseFiledParams.WAITING) {
                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                        downloadManager.startForActivity(this, docInfo);
                    } else if (docInfo.getStatus() == DataBaseFiledParams.FAILED) {
                        docInfo.setStatus(DataBaseFiledParams.LOADING);
                        downloadManager.startForActivity(this, docInfo);
                    }
                    adapter.notifyDataSetChanged();
                    return;
                }
            }


            // 文件是否正在同步
            if (downloadManager.isDownloading(item)) {
                ActivityUtils.showToast(this, "该文件正在同步请稍后。");
            } else {
                try {
                    //                    if (item.getResourceType().equalsIgnoreCase("mp3")) {
                    //                        Intent intentMP3 = new Intent(this, MusicActivity.class);
                    ////                        MUSIC_NAME = item.getResourceFileName();
                    ////                        MUSIC_TITLE = resourceFileName;
                    ////                        MUSIC_ID = "file://" + ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName();
                    //                        intentMP3.putExtra("fileId", "file://" + ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName());
                    //                        intentMP3.putExtra("resourceFileName", item.getResourceFileName());
                    //                        intentMP3.putExtra("courseName", resourceFileName);
                    //                        startActivity(intentMP3);
                    //                    } else if (item.getResourceType().equalsIgnoreCase("mp4")) {
                    //                        Intent mp4Intnet = new Intent(this, VideoActivity.class);
                    //                        mp4Intnet.putExtra("Video_url", "file://" + ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName());
                    //                        startActivity(mp4Intnet);
                    //                    } else
                    unLock(item);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private ResourseBean getVideoResourse(String fileId) {
        List<String> fileIds = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < resourceList.size(); i++) {
            DetailBean.DataBean.ResourceListBean data = resourceList.get(i);
            if (data.getResourceType().equals("cloudVideo") && data.getIsPay() == 0) {
                fileIds.add(data.getBunchPlant());
                if (fileId.equals(data.getBunchPlant()))
                    position = i;
            }
        }
        return new ResourseBean(fileIds, position);
    }

    private void initData() {
        // TODO Auto-generated method stub
        if (Float.valueOf(folderPrice) == 0f) {
            btn_buy.setVisibility(View.GONE);
        } else {
            btn_buy.setVisibility(View.VISIBLE);
            price_text.setText("¥" + folderPrice);
        }

        adapter.setNewData(resourceList);
        adapter.notifyDataSetChanged();
        if (autoBean!=null){
            for(int i =0;i<resourceList.size();i++){
                if(autoBean.getResourceId() == resourceList.get(i).getResourceId()){
                    if (adapter != null) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
                                    recyclerview.smoothScrollToPosition(i);
//                            }
//                        }, 1000);
                        break;
                    }
                }
            }
        }
    }

    private final String BUY_ALL = "0";
    private final String BUY_SINGLE = "1";
    private String orderName;
    private String orderPrice;

    private void getOrder(final String attachType, String attachId) {
        Bundle b = new Bundle();
        if (attachType.equalsIgnoreCase(BUY_ALL)) {
            int sum = 1;
            if (resourceList == null || resourceList.size() == 0) {
                sum = 1;
            } else {
                sum = resourceList.size();
            }
            b.putString("sum", sum + "");
            b.putString("bookName", resourceFileName);
            b.putString("image_url_str", img_url_str);
            b.putString("price", folderPrice + "");
        } else {
            b.putString("sum", "1");
            b.putString("bookName", orderName);
            b.putString("image_url_str", img_url_str);
            b.putString("price", orderPrice);
        }

        b.putString("attachId", attachId);
        b.putString("attachType", "1");
        Intent intent = new Intent(FolderActivity.this, PayChooseActivity.class);
        intent.putExtra("data", b);
        startActivityForResult(intent, 0);


        //        DialogUtils.showMyDialog(FolderActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        //        RequestParams params = new RequestParams();
        //        params.setUri(Preferences.BUY_ORDER);
        //        params.addQueryStringParameter("attachId", attachId);
        //        params.addQueryStringParameter("amount", "1");
        //        params.addQueryStringParameter("expressId", "0");
        //        params.addQueryStringParameter("systemType", "android");
        //        params.addQueryStringParameter("userId", SPUtility.getUserId(FolderActivity.this) + "");
        //        params.addQueryStringParameter("attachType", attachType);
        //
        //        x.http().post(params, new Callback.CommonCallback<String>() {
        //            @Override
        //            public void onSuccess(String s) {
        //                Gson gson = new Gson();
        //                Type type = new TypeToken<OrderBean>() {
        //                }.getType();
        //                OrderBean bean = gson.fromJson(s, type);
        //                dismissMyDialog();
        //                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
        //                    Bundle b = new Bundle();
        //                    if (attachType.equalsIgnoreCase(BUY_ALL)) {
        //                        b.putString("sum", resourceList.size() + "");
        //                        b.putString("bookName", resourceFileName);
        //                        b.putString("image_url_str", img_url_str);
        //                        b.putString("price", folderPrice);
        //                    } else {
        //                        b.putString("sum", "1");
        //                        b.putString("bookName", orderName);
        //                        b.putString("image_url_str", img_url_str);
        //                        b.putString("price", orderPrice);
        //                    }
        //
        //                    b.putString("ordercode", bean.getData().getAttachCode());
        //                    Intent intent = new Intent(FolderActivity.this, PayChooseActivity.class);
        //                    intent.putExtra("data", b);
        //                    startActivityForResult(intent, 0);
        //                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
        //                    ActivityUtils.showToast(FolderActivity.this, "加载失败," + bean.getCodeInfo());
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable throwable, boolean b) {
        //                ActivityUtils.showToast(FolderActivity.this, "加载失败,请检查网络!");
        //                dismissMyDialog();
        //            }
        //
        //            @Override
        //            public void onCancelled(CancelledException e) {
        //                dismissMyDialog();
        //            }
        //
        //            @Override
        //            public void onFinished() {
        //                dismissMyDialog();
        //            }
        //        });
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.intent.action.test")) {
                    String flag = intent.getStringExtra("flag");
                    Bundle bundle = intent.getBundleExtra("bundle");
                    DocInfo info = (DocInfo) bundle.getSerializable("info");
                    if (flag.equals("success") && isFileDownloaded(info.getName()) && null != adapter) {
                        ActivityUtils.showToast(FolderActivity.this, info.getBookName() + "下载完成");
                        adapter.notifyDataSetChanged();
                    } else if (flag.equals("update")) {
                        if (resourceList == null)
                            return;
                        for (int i = 0; i < resourceList.size(); i++) {
                            //                            if (info.getName().equals(resourceList.get(i).getResourceSaveName())) {
                            //                                adapter.notifyItemChanged(i);
                            adapter.notifyDataSetChanged();
                            return;
                            //                            }
                        }
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.test");
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(broadcastReceiver, filter);


        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //实例化IntentFilter对象
        //        IntentFilter filterNet = new IntentFilter();
        //        filterNet.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //        NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();
        //        netBroadcastReceiver.setListener(new NetBroadcastReceiver.NetChangeListener() {
        //            @Override
        //            public void onChangeListener(int status) {
        //                Logger.d(status);
        //            }
        //        });
        //        //注册广播接收
        //        registerReceiver(netBroadcastReceiver, filter);
        ////        }filter
    }

    /**
     * 判断后台发出的下载成功广播是否是当前界面中的附件信息
     *
     * @param fileSDName 保存在sd卡的名称
     * @return true-成功 false-不成功
     */
    public boolean isFileDownloaded(String fileSDName) {
        if (resourceList != null && resourceList.size() > 0) {
            for (DetailBean.DataBean.ResourceListBean attachTwo : resourceList) {
                String fileName = attachTwo.getResourceSaveName();
                if (attachTwo.getResourceType().equals("proto_testPaper")) {
                    String[] ss = attachTwo.getTestPaperInfo().getPackSavePath().split("/");
                    fileName = ss[2];
                }
                if (!TextUtils.isEmpty(fileName) && fileName.equals(fileSDName)) {
                    return true;
                }
            }
        }
        return false;
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
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
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


    /**
     * 下载附件
     */
    public void downLoadFile(DetailBean.DataBean.ResourceListBean item) {
        String path = null;
        String saveName = null;
        if (item.getResourceType().equals("proto_testPaper")) {
            path = getPath(FILE_DOWNLOAD_URL + item.getTestPaperInfo().getPackSavePath(), "");
            String[] ss = item.getTestPaperInfo().getPackSavePath().split("/");
            saveName = ss[2];
        } else
            path = getPath(FILE_DOWNLOAD_URL + item.getResourcePath() + item.getResourceSaveName(), item.getResourceId() + "");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(this, TestService.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            DocInfo d = new DocInfo();
            d.setUrl(path);
            d.setDirectoty(false);
            d.setName(TextUtils.isEmpty(saveName) ? item.getResourceSaveName() : saveName);

            d.setBookId(item.getResourceId() + "");
            d.setBookName(item.getResourceFileName());
            bundle.putSerializable("info", d);
            intent.putExtra("bundle", bundle);
            startService(intent);
        } else {
            ActivityUtils.showToast(this, "未检测到SD卡,或未赋予应用存储权限");
        }
    }

    private String getPath(String url, String id) {
        String path = url;
        return path;
    }

    private void unLock(final DetailBean.DataBean.ResourceListBean item) throws Exception {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG_NO, null, "正在加载中，请稍后...", null);
        Logger.d(item.getResourceType());
        if (item.getResourceType().equalsIgnoreCase("zip") || item.getResourceType().equalsIgnoreCase("testPaper") || item.getResourceType().equalsIgnoreCase("courseware") || item.getResourceType().equalsIgnoreCase("proto_testPaper")) {
            String name = item.getResourceSaveName();
            if (item.getResourceType().equals("proto_testPaper")) {
                String[] ss = item.getTestPaperInfo().getPackSavePath().split("/");
                name = ss[2];
            }
            String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name;
            String finalName = name;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!item.getResourceType().equals("proto_testPaper")) {
                        unLock(item.getResourceId() + "", finalName, finalName + "copy.zip");
                    }
                    try {
                        ZipUtils.unzip(item.getResourceType().equals("proto_testPaper") ? file : file + "copy.zip", ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath(), new ZipUtils.onZipSuccess() {
                            @Override
                            public void onZipSuccess(String index) {
                                if (!item.getResourceType().equals("proto_testPaper")) {
                                    ActivityUtils.deleteBookFormSD(file + "copy.zip");
                                }
                                if (item.getResourceType().equals("proto_testPaper")) {
                                    Intent intent6 = new Intent(FolderActivity.this, TestPaperActivity.class);
                                    intent6.putExtra("testPaperInfo", item.getTestPaperInfo());
                                    //                                    intent6.putExtra("userAvatars",userAvatars);
                                    intent6.putExtra("resourceId", item.getResourceId());
                                    intent6.putExtra("courseId", courseId);
                                    startActivity(intent6);
                                } else {
                                    String fileName = TextUtils.isEmpty(index) ? item.getResourceFileName() + (item.getResourceType().equalsIgnoreCase("zip") ? "/Index.html" : "/index.html") : index;
                                    //                                String type = item.getResourceType().equalsIgnoreCase("zip") ? "html" : "testPaper";

                                    Intent intent = new Intent(FolderActivity.this, WorkWebActivity.class);
                                    intent.putExtra("filePath", "file://" + ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + fileName);
                                    startActivity(intent);
                                }
                                DialogUtils.dismissMyDialog();
                            }

                        });

                    } catch (Exception e) {
                        DialogUtils.dismissMyDialog();
                        Looper.prepare();
                        Toast.makeText(getApplication(), "文件损坏，请重新下载！", Toast.LENGTH_LONG).show();
                        DataBaseHelper helper = new DataBaseHelper(getBaseContext());
                        List<DocInfo> infos = helper.getInfo2(item.getResourceId() + "");
                        for (DocInfo docInfo : infos) {
                            if (item.getResourceSaveName().equals(docInfo.getName())) {
                                Logger.d(docInfo.getStatus());
                                docInfo.setStatus(DataBaseFiledParams.WAITING);
                                //                                ActivityUtils.deleteBookFormSD(getApplication(),docInfo.getBookId(), docInfo.getName());
                                ActivityUtils.deleteFolder(ActivityUtils.getSDPath(FolderActivity.this, item.getResourceId() + "").getAbsolutePath());
                                //                                mDataBaseHelper.deleteValue(info);
                                //                                adapter.notifyDataSetChanged();
                                //                                initData();
                                FolderActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        Looper.loop();
                    }
                }
            }).start();
        } else if (item.getResourceType().equalsIgnoreCase("pdf")) {
            final String name = item.getResourceSaveName();
            unLock(item.getResourceId() + "", name, name + "copy.pdf");
            final String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name + "copy.pdf";
            Logger.d("file:" + file + ",name:" + name);
            Intent intent3 = new Intent(FolderActivity.this, WorkWebActivity.class);
            intent3.putExtra("filePath", file);
            intent3.putExtra("name", item.getResourceFileName());
            intent3.putExtra("fileType", "pdf");
            startActivity(intent3);
        } else if (item.getResourceType().equalsIgnoreCase("mp3")) {

            final String name = item.getResourceSaveName();
            unLock(item.getResourceId() + "", name, name + "copy.mp3");
            final String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name + "copy.mp3";
            Logger.d("file:" + file + ",name:" + name);
            //            Intent intent3 = new Intent(DetailActivity.this, WorkWebActivity.class);
            //            intent3.putExtra("filePath", file);
            //            intent3.putExtra("name", item.getResourceFileName());
            //            intent3.putExtra("fileType", "pdf");
            //            startActivity(intent3);
            Intent intentMP3 = new Intent(this, MusicActivity.class);
            //            MUSIC_NAME = item.getResourceFileName();
            //            MUSIC_TITLE = data.getCourseName();
            //            MUSIC_ID = file;
            intentMP3.putExtra("fileId", file);
            intentMP3.putExtra("resourceFileName", item.getResourceFileName());
            intentMP3.putExtra("courseName", resourceFileName);
            //            Logger.d(item.getResourceFileName());
            startActivity(intentMP3);
            dismissMyDialog();
        } else if (item.getResourceType().equalsIgnoreCase("mp4")) {
            final String name = item.getResourceSaveName();
            unLock(item.getResourceId() + "", name, name + "copy.mp4");
            final String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name + "copy.mp4";
            Logger.d("file:" + file + ",name:" + name);
            Intent mp4Intnet = new Intent(this, VideoActivity.class);
            mp4Intnet.putExtra("Video_url", file);
            mp4Intnet.putExtra("title", item.getResourceFileName());
            startActivity(mp4Intnet);
            dismissMyDialog();
        } else {
            Toast.makeText(getApplication(), "不支持此文件类型！", Toast.LENGTH_LONG).show();
            DialogUtils.dismissMyDialog();
        }
    }

    /**
     * 解密文件
     *
     * @param bookId        图书Id
     * @param soureFileName 源文件
     * @param saveFileName  目标文件
     */

    public void unLock(String bookId, String soureFileName, String saveFileName) {
        File soureFile = new File(ActivityUtils.getSDPath(this, bookId).getAbsolutePath() + "/" + soureFileName);
        File saveFile = new File(ActivityUtils.getSDPath(this, bookId).getAbsolutePath() + "/" + saveFileName);
        if (saveFile.exists())
            return;
        ZipUtils.unLockFile(soureFile, saveFile);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PayChooseActivity.PAY_SUCCESS)
            requestData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != broadcastReceiver) {
            this.unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
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
}
