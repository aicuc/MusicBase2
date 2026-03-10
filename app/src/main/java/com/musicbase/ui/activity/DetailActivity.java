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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.DetailAdapter;
import com.musicbase.adapter.ModuleAdapter;
import com.musicbase.adapter.RelationAdapter;
import com.musicbase.download2.TestService;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.db.DataBaseHelper;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.ResourseBean;
import com.musicbase.model.recyclerviewmodel.MultipleItem_Module;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.superplayer.SuperPlayerActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.MyLog;
import com.musicbase.util.SPUtility;
import com.musicbase.util.TimeUtils;
import com.musicbase.util.ToastUtil;
import com.musicbase.util.ZipUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.Jzvd;

import static com.musicbase.preferences.Preferences.FILE_DOWNLOAD_URL;
import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class DetailActivity extends Activity {

    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;


    @BindView(R.id.teacher_bg)
    ImageView teacherBg;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;//课程列表
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.price_text)
    TextView price_text;
    @BindView(R.id.btn_buy)
    LinearLayout btnBuy;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerview2;//相关推荐列表

    @BindView(R.id.layout_course)
    LinearLayout layout_course;
    @BindView(R.id.layout_relation)
    LinearLayout layoutRelation;
    @BindView(R.id.change_tab)
    LinearLayout change_tab;
    @BindView(R.id.view_divide)
    View view_divide;
    private DetailAdapter adapter;
    private RelationAdapter adapter2;
    private ModuleAdapter moduleAdapter;
    private int courseId;
    private int systemCodeId;
    private DetailBean.DataBean data;
    private List<DetailBean.DataBean.ResourceListBean> resourceList;
    private List<DetailBean.DataBean.InformationList> informationList;
    private List<DetailBean.DataBean.ModuleList> moduleListList;
    private List<MultipleItem_Module> multipleList;
    DownloadManager downloadManager;
    BroadcastReceiver broadcastReceiver;
    private String[] myPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    private TextView all_course;
    private TextView profile_text;
    private RecyclerView recycler_module;
    private NestedScrollView scroll;
    private TextView buy_text;
    private boolean isCourse = true;
    private boolean layout_1 = false;
    private boolean buyed;
    private DetailBean.DataBean.ResourceListBean autoBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        //        requestData();
    }

    private void getIntentData() {
        courseId = getIntent().getIntExtra("courseId", 0);
        systemCodeId = getIntent().getIntExtra("systemCodeId", 0);
        buyed = getIntent().getBooleanExtra("buyed", false);
        autoBean = (DetailBean.DataBean.ResourceListBean) getIntent().getSerializableExtra("autoBean");
        Logger.d(courseId);
    }

    private void initView() {
        titlelayoutTitle.setText("详 情");
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        downloadManager = DownloadManager.getInstance(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_shape));
        recyclerview.addItemDecoration(divider);
        adapter = new DetailAdapter(this, R.layout.item_course_catalog, null);
        if (autoBean != null && autoBean.getResourcePid().equals("0")) {//搜索跳转，搜索结果标记
            adapter.setAutoId(autoBean.getResourceId());
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (resourceList.get(position).getIsFolder() == 1) {
                    //跳转文件夹
                    Intent intent = new Intent(DetailActivity.this, FolderActivity.class);
                    intent.putExtra("resourceId", String.valueOf(resourceList.get(position).getResourceId()));
                    intent.putExtra("resourceFileName", String.valueOf(resourceList.get(position).getResourceFileName()));
                    intent.putExtra("folderPrice", resourceList.get(position).getIsPay() == 0 ? "0.00" : String.valueOf(resourceList.get(position).getCurrentPrice()));
                    intent.putExtra("img_url_str", String.valueOf(data.getCourseImgPathVertical()));
                    intent.putExtra("courseId", courseId);
                    //                    intent.putExtra("userAvatars",data.getUserAvatars());
                    Log.e("courseId", "courseId detail==" + courseId);
                    startActivity(intent);
                } else if (resourceList.get(position).getIsPay() == 0) {
                    click(position);
                    openAttach(resourceList.get(position).getResourceId() + "");
                } else if (resourceList.get(position).getIsPay() == 1 && resourceList.get(position).getCurrentPrice().equals("0.00")) {
                    ToastUtil.showShort(DetailActivity.this, "该课程不支持单独购买，请整体购买！");
                } else {
                    orderPrice = resourceList.get(position).getCurrentPrice();
                    orderName = resourceList.get(position).getResourceFileName();
                    getOrder(BUY_SINGLE, resourceList.get(position).getResourceId() + "");
                }
            }
        });
        recyclerview.setAdapter(adapter);
        recyclerview2.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adapter2 = new RelationAdapter(this, R.layout.item_course_catalog, null);
        adapter2.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (informationList.get(position).getCategory() == 0) {
                    Intent intent = new Intent(DetailActivity.this, BrowerActivity.class);
                    intent.putExtra("filePath", informationList.get(position).getLinks());
                    intent.putExtra("name", informationList.get(position).getTitle());
                    intent.putExtra("allowOpenInBrowser", informationList.get(position).getAllowOpenInBrowser());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("courseId", Integer.parseInt(informationList.get(position).getLinks()));
                    startActivity(intent);
                }
            }
        });
        recyclerview2.setAdapter(adapter2);

        initDownload(this);
        initBroadcast();
        all_course = (TextView) findViewById(R.id.all_course);
        profile_text = (TextView) findViewById(R.id.profile_text);

        all_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all_course.setTextColor(getResources().getColor(R.color.red_e61b19));
                profile_text.setTextColor(getResources().getColor(R.color.grey_666666));
                recycler_module.setVisibility(View.GONE);
                layout_course.setVisibility(View.VISIBLE);
                isCourse = true;
                if (moduleAdapter != null)//结束简介中多媒体播放
                    moduleAdapter.stopAVMedia();
            }
        });
        profile_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_text.setTextColor(getResources().getColor(R.color.red_e61b19));
                all_course.setTextColor(getResources().getColor(R.color.grey_666666));
                recycler_module.setVisibility(View.VISIBLE);
                layout_course.setVisibility(View.GONE);
                isCourse = false;
            }
        });
        recycler_module = (RecyclerView) findViewById(R.id.recycler_module);
        recycler_module.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        recycler_module.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.video);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });

        scroll = (NestedScrollView) findViewById(R.id.scroll);
        buy_text = (TextView) findViewById(R.id.buy_text);


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
                intentAudio.putExtra("courseName", data.getCourseName());
                startActivity(intentAudio);
                break;
            case "mp4":
                if (TextUtils.isEmpty(item.getOnlineLinks()))
                    downloadOrplay(position);
                else {
                    Intent mp4Intnet = new Intent(this, VideoActivity.class);
                    mp4Intnet.putExtra("Video_url", "file://" + ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName());
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
                    intentMP3.putExtra("courseName", data.getCourseName());
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
                    Intent intent3 = new Intent(DetailActivity.this, BrowerActivity.class);
                    intent3.putExtra("filePath", item.getResourceSaveName());
                    intent3.putExtra("allowOpenInBrowser", item.getAllowOpenInBrowser());
                    intent3.putExtra("name", item.getResourceFileName());
                    startActivity(intent3);
                }
                break;
            //            case "pdf":
            //                Intent intent4 = new Intent(DetailActivity.this, WorkWebActivity.class);
            //                intent4.putExtra("filePath", item.getResourceSaveName());
            //                startActivity(intent4);
            //                break;
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
        if (!ActivityUtils.isExistByName(DetailActivity.this, item.getResourceId() + "", item.getResourceSaveName()) && !ActivityUtils.isExistByName(DetailActivity.this, item.getResourceId() + "", saveName)) {
            if (!ActivityUtils.NetSwitch(DetailActivity.this, Boolean.parseBoolean(SPUtility.getSPString(DetailActivity.this, "switchKey")))) {
                ActivityUtils.showToast(DetailActivity.this, "请在有WIFI的情况下下载");
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
                if (item.getResourceSaveName().equals(docInfo.getName())) {
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
                    //                    if (item.getResourceType().equalsIgnoreCase("mp4")) {
                    //                        Intent mp4Intnet = new Intent(this, VideoActivity.class);
                    //                        mp4Intnet.putExtra("Video_url", "file://" + ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + item.getResourceSaveName());
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

    private void requestData() {
        DialogUtils.showMyDialog(DetailActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载课程...", null);

        OkGo.<String>post(Preferences.COURSE_DETAIL).tag(this).params("userId", SPUtility.getUserId(this) + "").params("courseId", courseId + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                MyLog.e("DetailActivity", "DetailBean===" + response.body());
                Gson gson = new Gson();

                //                Type type = new TypeToken<DetailBean>() {
                //                }.getType();
                DetailBean bean = gson.fromJson(response.body(), DetailBean.class);
                if (bean != null && bean.getCode().equalsIgnoreCase("SUCCESS")) {
                    data = bean.getData();
                    resourceList = data.getResourceList();
                    informationList = data.getInformationList();
                    moduleListList = data.getModuleList();
                    //                    transforToData();
                    initData();
                } else {
                    assert bean != null;
                    ToastUtil.showShort(DetailActivity.this, bean.getCodeInfo());
                    clearData();
                }
            }

            @Override
            public void onError(Response<String> response) {
                //                Logger.e(response.getRawResponse());
                ActivityUtils.showToast(DetailActivity.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                DialogUtils.dismissMyDialog();

                //                if (refresh_view.isRefreshing())
                //                    refresh_view.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                //                Type type = new TypeToken<DetailBean>() {
                //                }.getType();
                DetailBean bean = gson.fromJson(response.body(), DetailBean.class);
                if (bean != null && bean.getCode().equalsIgnoreCase("SUCCESS")) {
                    data = bean.getData();
                    resourceList = data.getResourceList();
                    informationList = data.getInformationList();
                    moduleListList = data.getModuleList();
                    //                    transforToData();
                    initData();
                } else {
                    assert bean != null;
                    ToastUtil.showShort(DetailActivity.this, bean.getCodeInfo());
                    clearData();
                }
            }
        });

    }

    private void clearData() {
        btnBuy.setVisibility(View.GONE);
        //        buy_text.setVisibility(View.GONE);
        layout_course.setVisibility(View.GONE);
        layoutRelation.setVisibility(View.GONE);
    }

    private void initData() {
        if (data.getCourseBookType() == 1) {
            if (isCourse) {
                recycler_module.setVisibility(View.GONE);
                layout_course.setVisibility(View.VISIBLE);
            } else {
                recycler_module.setVisibility(View.VISIBLE);
                layout_course.setVisibility(View.GONE);
            }
        }

        if (data.getCourseBookType() == 2) {//图书
            buy_text.setText("图书购买：");
            price.setText("立即购买");
            change_tab.setVisibility(View.GONE);
            btnBuy.setVisibility(View.VISIBLE);
            price_text.setText("¥" + data.getCurrentPrice());
        } else if (data.getChargeType() == 0) {//不用买的
            buy_text.setVisibility(View.GONE);
            price_text.setVisibility(View.GONE);
        } else if (data.getCourseIsPay() == 0) {//买过的（区分从什么界面过来）
            if (buyed) {
                buy_text.setText("剩余");
                btnBuy.setVisibility(View.VISIBLE);
                price.setBackgroundColor(getResources().getColor(R.color.grey_e5));
                price_text.setText(TimeUtils.getDiscrepantDaysToCurrent(data.getEndTime()) + "天");
                price.setText("我要续费");
                view_divide.setVisibility(View.VISIBLE);
                price.setTextColor(getResources().getColor(R.color.black_333333));
                price_text.setTextColor(getResources().getColor(R.color.black_333333));

            } else {
                buy_text.setVisibility(View.GONE);
                price_text.setVisibility(View.GONE);
                price.setText("开始学习");

            }

        } else {//没买过的
            btnBuy.setVisibility(View.VISIBLE);
            price_text.setText("¥" + data.getCurrentPrice());
        }
        if (resourceList == null || resourceList.size() == 0)
            layout_course.setVisibility(View.GONE);
        adapter.setNewData(resourceList);
        adapter.notifyDataSetChanged();
        if (informationList == null || informationList.size() == 0)
            layoutRelation.setVisibility(View.GONE);
        transformList();
        moduleAdapter = new ModuleAdapter(multipleList);
        recycler_module.setAdapter(moduleAdapter);
        adapter2.setNewData(informationList);
        adapter2.notifyDataSetChanged();
        Glide.with(this).load(Preferences.IMAGE_HTTP_LOCATION + data.getCourseImgPathHorizontal()).into(teacherBg);

        if (autoBean != null) {
            if (!autoBean.getResourcePid().equals("0")) {//搜索结果 继续跳转
                DetailBean.DataBean.ResourceListBean bean = null;
                for (DetailBean.DataBean.ResourceListBean temp_bean : resourceList) {
                    if (temp_bean.getResourceId() == Integer.parseInt(autoBean.getResourcePid()))
                        bean = temp_bean;
                }
                if (bean == null)
                    return;
                Intent intent = new Intent(DetailActivity.this, FolderActivity.class);
                intent.putExtra("resourceId", String.valueOf(bean.getResourceId()));
                intent.putExtra("resourceFileName", String.valueOf(bean.getResourceFileName()));
                intent.putExtra("folderPrice", bean.getIsPay() == 0 ? "0.00" : String.valueOf(bean.getCurrentPrice()));
                intent.putExtra("img_url_str", String.valueOf(data.getCourseImgPathVertical()));
                intent.putExtra("courseId", courseId);
                //                    intent.putExtra("userAvatars",data.getUserAvatars());
                intent.putExtra("autoBean", autoBean);
                autoBean = null;
                startActivity(intent);
            }
        }
    }

    private void transformList() {
        multipleList = new ArrayList<>();
        if (moduleListList != null && moduleListList.size() > 0) {
            MultipleItem_Module item = null;
            for (DetailBean.DataBean.ModuleList list : moduleListList) {
                switch (list.getModuleType()) {//（'text'代表文本信息 ,'url'代表网址,'img'代表图片,'video'代表视频,'audio'代表音频）
                    case "text":
                        item = new MultipleItem_Module(MultipleItem_Module.TYPE_TEXT, list);
                        multipleList.add(item);
                        break;
                    case "url":
                        item = new MultipleItem_Module(MultipleItem_Module.TYPE_URL, list);
                        multipleList.add(item);
                        break;
                    case "img":
                        item = new MultipleItem_Module(MultipleItem_Module.TYPE_IMG, list);
                        multipleList.add(item);
                        break;
                    case "video":
                        item = new MultipleItem_Module(MultipleItem_Module.TYPE_VIDEO, list);
                        multipleList.add(item);
                        break;
                    case "audio":
                        item = new MultipleItem_Module(MultipleItem_Module.TYPE_MUSIC, list);
                        multipleList.add(item);
                        break;
                }
            }
        }
    }

    private final String BUY_ALL = "0";
    private final String BUY_SINGLE = "1";
    private String orderName;
    private String orderPrice;

    @OnClick(R.id.price)
    public void onViewClicked() {
        if (data.getChargeType() == 0)
            addToBuyed();
        else if (data.getCourseBookType() == 1 && data.getCourseIsPay() == 0 && !buyed)
            intentToStudy();
        else
            getOrder(BUY_ALL, data.getCourseId() + "");
    }

    private void intentToStudy() {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        intent.putExtra("mainFlag", 1);
        intent.putExtra("courseId", courseId);
        intent.putExtra("systemCodeId", systemCodeId);
        startActivity(intent);
    }

    private void addToBuyed() {
        OkGo.<String>post(Preferences.ADD_YUEXUE).tag(this).params("userId", SPUtility.getUserId(this) + "").params("courseId", courseId + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                //                Type type = new TypeToken<DetailBean>() {
                //                }.getType();
                DetailBean bean = gson.fromJson(response.body(), DetailBean.class);
                if (bean != null) {
                    if (bean.getCode().equalsIgnoreCase("SUCCESS")) {
                        ActivityUtils.showToast(DetailActivity.this, bean.getData().getMessage());
                    } else {
                        ActivityUtils.showToast(DetailActivity.this, bean.getCodeInfo());

                    }
                    //                    data = bean.getData();
                }
            }

            @Override
            public void onError(Response<String> response) {
                //                Logger.e(response.getRawResponse());
                ActivityUtils.showToast(DetailActivity.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                //                if (refresh_view.isRefreshing())
                //                    refresh_view.setRefreshing(false);
            }


        });
    }

    private void getOrder(final String attachType, String attachId) {
        Bundle b = new Bundle();
        if (attachType.equalsIgnoreCase(BUY_ALL)) {
            int sum = 1;
            if (data.getResourceList() == null || data.getResourceList().size() == 0) {
                sum = 1;
            } else {
                sum = data.getResourceList().size();
            }
            b.putString("sum", sum + "");
            b.putString("bookName", data.getCourseName());
            b.putString("image_url_str", data.getCourseImgPathVertical());
            b.putString("price", data.getCurrentPrice() + "");
        } else {
            b.putString("sum", "1");
            b.putString("bookName", orderName);
            b.putString("image_url_str", data.getCourseImgPathVertical());
            b.putString("price", orderPrice);
        }

        b.putString("attachId", attachId);
        b.putString("attachType", attachType);
        b.putInt("courseBookType", data.getCourseBookType());
        Intent intent = new Intent(DetailActivity.this, PayChooseActivity.class);
        intent.putExtra("data", b);
        startActivityForResult(intent, 0);


        //        DialogUtils.showMyDialog(DetailActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        //        RequestParams params = new RequestParams();
        //        params.setUri(Preferences.BUY_ORDER);
        //        params.addQueryStringParameter("attachId", attachId);
        //        params.addQueryStringParameter("amount", "1");
        //        params.addQueryStringParameter("expressId", "0");
        //        params.addQueryStringParameter("systemType", "android");
        //        params.addQueryStringParameter("userId", SPUtility.getUserId(DetailActivity.this) + "");
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
        //
        //                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
        //                    ActivityUtils.showToast(DetailActivity.this, "加载失败," + bean.getCodeInfo());
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable throwable, boolean b) {
        //                ActivityUtils.showToast(DetailActivity.this, "加载失败,请检查网络!");
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
                        ActivityUtils.showToast(DetailActivity.this, info.getBookName() + "下载完成");
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
        builder.setMessage("加阅下载需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
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
            String finalName = name;
            final String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!item.getResourceType().equals("proto_testPaper")) {
                        unLock(item.getResourceId() + "", finalName, finalName + "copy.zip");
                    }
                    try {
                        ZipUtils.unzip(item.getResourceType().equals("proto_testPaper") ? file : file + "copy.zip", ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath(), new ZipUtils.onZipSuccess() {
                            @Override
                            public void onZipSuccess(String index) {
                                if (!item.getResourceType().equals("proto_testPaper")) {
                                    ActivityUtils.deleteBookFormSD(file + "copy.zip");
                                }
                                if (item.getResourceType().equals("proto_testPaper")) {
                                    Intent intent6 = new Intent(DetailActivity.this, TestPaperActivity.class);
                                    intent6.putExtra("testPaperInfo", item.getTestPaperInfo());
                                    //                                        intent6.putExtra("userAvatars",data.getUserAvatars());
                                    intent6.putExtra("resourceId", item.getResourceId());
                                    intent6.putExtra("courseId", courseId);
                                    startActivity(intent6);
                                } else {
                                    String fileName = TextUtils.isEmpty(index) ? item.getResourceFileName() + (item.getResourceType().equalsIgnoreCase("zip") ? "/Index.html" : "/index.html") : index;
                                    //                                String type = item.getResourceType().equalsIgnoreCase("zip") ? "html" : "testPaper";

                                    Intent intent = new Intent(DetailActivity.this, WorkWebActivity.class);
                                    intent.putExtra("filePath", "file://" + ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath() + "/" + fileName);
                                    //                                intent.putExtra("fileType", "html");
                                    startActivity(intent);
                                }
                                dismissMyDialog();
                            }

                        });

                    } catch (Exception e) {
                        dismissMyDialog();
                        Looper.prepare();
                        Toast.makeText(getApplication(), "文件损坏，请重新下载！", Toast.LENGTH_LONG).show();
                        DataBaseHelper helper = new DataBaseHelper(getBaseContext());
                        List<DocInfo> infos = helper.getInfo2(item.getResourceId() + "");
                        for (DocInfo docInfo : infos) {
                            if (item.getResourceSaveName().equals(docInfo.getName())) {
                                Logger.d(docInfo.getStatus());
                                docInfo.setStatus(DataBaseFiledParams.WAITING);
                                //                                ActivityUtils.deleteBookFormSD(getApplication(),docInfo.getBookId(), docInfo.getName());
                                ActivityUtils.deleteFolder(ActivityUtils.getSDPath(DetailActivity.this, item.getResourceId() + "").getAbsolutePath());
                                //                                mDataBaseHelper.deleteValue(info);
                                //                                adapter.notifyDataSetChanged();
                                //                                initData();
                                DetailActivity.this.runOnUiThread(new Runnable() {
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
            Intent intent3 = new Intent(DetailActivity.this, WorkWebActivity.class);
            intent3.putExtra("filePath", file);
            intent3.putExtra("name", item.getResourceFileName());
            intent3.putExtra("fileType", "pdf");
            startActivity(intent3);
            dismissMyDialog();
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
            intentMP3.putExtra("courseName", data.getCourseName());
            //            Logger.d(item.getResourceFileName());
            startActivity(intentMP3);
            dismissMyDialog();
        } else if (item.getResourceType().equalsIgnoreCase("mp4")) {
            final String name = item.getResourceSaveName();
            unLock(item.getResourceId() + "", name, name + "copy.mp4");
            final String file = ActivityUtils.getSDPath(this, item.getResourceId() + "").getAbsolutePath() + "/" + name + "copy.mp4";
            Logger.d("file:" + file + ",name:" + name);
            Intent mp4Intnet = new Intent(this, VideoActivity.class);
            mp4Intnet.putExtra("Video_url", "file://" + file);
            startActivity(mp4Intnet);
        } else {
            Toast.makeText(getApplication(), "不支持此文件类型！", Toast.LENGTH_LONG).show();
            dismissMyDialog();
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
    protected void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        if (resultCode == PayChooseActivity.PAY_SUCCESS)
        //            requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != broadcastReceiver) {
            this.unregisterReceiver(broadcastReceiver);
        }
        if (Jzvd.CURRENT_JZVD != null) {
            Jzvd.releaseAllVideos();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (moduleAdapter != null)
            moduleAdapter.stopAVMedia();
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
