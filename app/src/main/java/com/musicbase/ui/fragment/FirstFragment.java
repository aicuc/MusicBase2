package com.musicbase.ui.fragment;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.BtnListAdapter;
import com.musicbase.adapter.FirstAdapter;
import com.musicbase.entity.FirstBean;
import com.musicbase.model.BannerImageLoader;
import com.musicbase.model.recyclerviewmodel.MultipleItem;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.BookActivity;
import com.musicbase.ui.activity.BrowerActivity;
import com.musicbase.ui.activity.CardActivity;
import com.musicbase.ui.activity.ContentListActivity;
import com.musicbase.ui.activity.DetailActivity;
import com.musicbase.ui.activity.SongScore;
import com.musicbase.ui.activity.WorkWebActivity;
import com.musicbase.ui.view.BtnListItemDecoration;
import com.musicbase.ui.view.SuperSwipeRefreshLayout;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.musicbase.preferences.Preferences.SUCCESS;
import static com.musicbase.preferences.Preferences.appInfo;


public class FirstFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SuperSwipeRefreshLayout refresh_view;

    @BindView(R.id.btn_scan)
    LinearLayout btnScan;
    @BindView(R.id.btn_search)
    LinearLayout btnSearch;
    @BindView(R.id.btn_record)
    LinearLayout btnRecord;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView2;
    private View mRootView;
    Unbinder unbinder2;
    private FirstAdapter adapter;
    private BtnListAdapter btnListAdapter;

    private List<FirstBean.Data.ColumnList.CourseList> roundMapList;
    private List<FirstBean.Data.ColumnList> ordinaryList;
    private List<FirstBean.Data.NavigationBars> navigationBarsList;
    private List<MultipleItem> dataList = new ArrayList<>();
    private final String TAG = getClass().getSimpleName();

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_first, container, false);
        //        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        refresh_view.measure(0, 0);
        refresh_view.setRefreshing(true);
        requestData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //        unbinder.unbind();
        unbinder2.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }


    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    private void initData() {
        setBtnList();

        // TODO Auto-generated method stub
        List<String> imagepaths = new ArrayList<>();
        for (FirstBean.Data.ColumnList.CourseList list : roundMapList) {
            imagepaths.add(list.getCourseImgPathHorizontal());
        }

        banner.setImages(imagepaths).setImageLoader(new BannerImageLoader(getActivity())).setDelayTime(2000).setBannerStyle(BannerConfig.CIRCLE_INDICATOR).setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (roundMapList.get(position).getClassification() == 1) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("courseId", roundMapList.get(position).getCourseId());
                    startActivity(intent);
                } else if (roundMapList.get(position).getClassification() == 2) {
                    if (roundMapList.get(position).getAllowOpenInBrowser() == 2) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(roundMapList.get(position).getCourseUrl());
                        intent.setData(content_url);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), BrowerActivity.class);
                        intent.putExtra("filePath", roundMapList.get(position).getCourseUrl());
                        intent.putExtra("allowOpenInBrowser", roundMapList.get(position).getAllowOpenInBrowser());
                        intent.putExtra("name", roundMapList.get(position).getCourseName());
                        startActivity(intent);
                    }
                } else if (roundMapList.get(position).getClassification() == 4) {
                    /**
                     * 音基 pndoo://jiayue:8888/home?id=
                     */
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(roundMapList.get(position).getAppJumpInfo().getAndroidSucc()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                    boolean isValid = !activities.isEmpty();
                    if (isValid) {
                        startActivity(intent);
                    } else {
                        DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "本地未找到应用，前往下载？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(roundMapList.get(position).getAppJumpInfo().getAndroidFail()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                } else if (roundMapList.get(position).getClassification() == 5) {
                    Intent intent = new Intent(getActivity(), ContentListActivity.class);
                    intent.putExtra("id", roundMapList.get(position).getSystemCodeId());
                    intent.putExtra("content", roundMapList.get(position).getCourseName());
                    startActivity(intent);
                }
            }
        }).start();

        adapter.setNewData(dataList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        // TODO Auto-generated method stub
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.list);
        refresh_view = (SuperSwipeRefreshLayout) mRootView.findViewById(R.id.swipe);

        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //分割线
        //        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapter = new FirstAdapter(getActivity(), null);
        //动态点击按钮 header
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_first, null);
        unbinder2 = ButterKnife.bind(this, header);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        recyclerView2.addItemDecoration(new BtnListItemDecoration(getActivity()));//间距
        btnListAdapter = new BtnListAdapter(getActivity(), R.layout.item_btnlist, navigationBarsList);
        recyclerView2.setAdapter(btnListAdapter);
        /**
         * 动态按钮点击事件
         */
        btnListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FirstBean.Data.NavigationBars bean = navigationBarsList.get(position);
                switch (bean.getIconType()){
                    case "url"://跳转链接
                        if (bean.getAllowOpenInBrowser() == 2) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(bean.getIconLink());
                            intent.setData(content_url);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getActivity(), BrowerActivity.class);
                            intent.putExtra("filePath", bean.getIconLink());
                            intent.putExtra("name", "");
                            intent.putExtra("allowOpenInBrowser", bean.getAllowOpenInBrowser());
                            startActivity(intent);
                        }
                        break;
                    case "column":
                        Intent intent = new Intent(getActivity(), ContentListActivity.class);
                        intent.putExtra("id", Integer.parseInt(bean.getIconLink()));
                        intent.putExtra("content", "");
                        startActivity(intent);
                        break;
                    case "sing":
                        startActivity(new Intent(getActivity(), SongScore.class));
                        break;
                    case "test":
                        Intent intent1 = new Intent(getContext(), WorkWebActivity.class);
                        intent1.putExtra("filePath", "https://www.yinyuesuyang.com/testpaper/nengliceshi_1/");
                        intent1.putExtra("name", "在线测试");
                        intent1.putExtra("fileType", "html");
                        startActivity(intent1);
                        break;
                    case "learn":
                        startActivity(new Intent(getActivity(), CardActivity.class));
                        break;
                    case "paint":
                        Intent intent3 = new Intent(getActivity(), WorkWebActivity.class);
                        intent3.putExtra("filePath", "file:///android_asset/draw/index.html");
                        intent3.putExtra("name", "画板");
                        intent3.putExtra("fileType", "html");
                        startActivity(intent3);
                        break;
                    case "bookShelf":
                        startActivity(new Intent(getActivity(), BookActivity.class));
                        break;
                }
            }
        });

        adapter.addHeaderView(header);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (dataList.get(position).getItemType() == MultipleItem.HEAD) {
                    Intent intent = new Intent(getActivity(), ContentListActivity.class);
                    intent.putExtra("id", dataList.get(position).getCourseId());
                    intent.putExtra("content", dataList.get(position).getContent());
                    startActivity(intent);
                } else if (dataList.get(position).getItemType() == MultipleItem.CONTENT_1) {
                    if (dataList.get(position).getBean().getClassification() == 4) {
                        /**
                         * 音基 pndoo://jiayue:8888/home?id=
                         */
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataList.get(position).getBean().getAppJumpInfo().getAndroidSucc()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                        boolean isValid = !activities.isEmpty();
                        if (isValid) {
                            startActivity(intent);
                        } else {
                            DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "本地未找到应用，前往下载？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataList.get(position).getBean().getAppJumpInfo().getAndroidFail()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else if (dataList.get(position).getBean().getClassification() == 5) {//跳转栏目详情 "更多"
                        Intent intent = new Intent(getActivity(), ContentListActivity.class);
                        intent.putExtra("id", dataList.get(position).getBean().getCourseId());
                        intent.putExtra("content", dataList.get(position).getBean().getCourseName());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("courseId", dataList.get(position).getBean().getCourseId());
                        startActivity(intent);
                    }
                } else if (dataList.get(position).getItemType() == MultipleItem.CONTENT_3) {
                    if (dataList.get(position).getBean().getAllowOpenInBrowser() == 2) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(dataList.get(position).getBean().getCourseUrl());
                        intent.setData(content_url);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), BrowerActivity.class);
                        intent.putExtra("filePath", dataList.get(position).getBean().getCourseUrl() + "?userId=" + SPUtility.getUserId(getActivity()));
                        intent.putExtra("name", dataList.get(position).getBean().getCourseName());
                        intent.putExtra("allowOpenInBrowser", dataList.get(position).getBean().getAllowOpenInBrowser());
                        startActivity(intent);
                    }
                }
            }
        });
        adapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                return dataList.get(position).getSpanSize();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 中间按键列表
     */
//    private int[] im = {R.mipmap.examination_01, R.mipmap.assistant_06, R.mipmap.assistant_09, R.mipmap.assistant_03};
//    //    private int[] im = {R.mipmap.assistant_01, R.mipmap.assistant_02, R.mipmap.assistant_03, R.mipmap.assistant_04, R.mipmap.assistant_05, R.mipmap.assistant_06, R.mipmap.assistant_07, R.mipmap.assistant_08};
//    private String[] name = {"能力测试", "你唱我评", "学习卡", "画板"};
//    //    private String[] name = {"能力评测", "模拟考试", "趣练", "师资认证", "直播", "你唱我评", "节拍器", "更多"};
//    private List<BtnListBean> list = new ArrayList<>();

    private void setBtnList() {
        btnListAdapter.setNewData(navigationBarsList);
        btnListAdapter.notifyDataSetChanged();
    }


    private void requestData() {
        Logger.d("userid===" + SPUtility.getUserId(getActivity()));
        OkGo.<String>post(Preferences.HOME_INFO).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<FirstBean>() {
                }.getType();
                FirstBean bean = null;
                try {
                    Logger.e("FirstBean====="+response.body());
                    bean = gson.fromJson(response.body(), type);
                } catch (JsonSyntaxException e) {

                    e.printStackTrace();

                }
                if (bean == null)
                    return;
                if (bean.getCode().equalsIgnoreCase(SUCCESS)) {
                    ordinaryList = bean.getData().getColumnList();
                    roundMapList = bean.getData().getCourseRoundMapList();
                    navigationBarsList = bean.getData().getNavigationBars();
                    transforToData();
                    initData();
                } else {
                    ActivityUtils.showToast(getActivity(), "出现错误：" + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (isAdded())
                    ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                if (refresh_view.isRefreshing())
                    refresh_view.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<FirstBean>() {
                }.getType();
                FirstBean bean = gson.fromJson(response.body(), type);
                if (bean != null) {
                    ordinaryList = bean.getData().getColumnList();
                    roundMapList = bean.getData().getCourseRoundMapList();
                    navigationBarsList = bean.getData().getNavigationBars();
                    transforToData();
                    initData();
                }
            }
        });

        //        RequestParams params = new RequestParams(Preferences.HOME_INFO);
        //        params.addQueryStringParameter("userId", SPUtility.getUserId(getActivity()) + "");
        //        Logger.d("userid===" + SPUtility.getUserId(getActivity()));
        //
        //        x.http().post(params, new Callback.CommonCallback<String>() {
        //            @Override
        //            public void onSuccess(String s) {
        //                Logger.d("HOME_INFO===" + s);
        //                Gson gson = new Gson();
        //                Type type = new TypeToken<FirstBean>() {
        //                }.getType();
        //                FirstBean bean = gson.fromJson(s, type);
        //                if (bean != null) {
        //                    ordinaryList = bean.getData().getColumnList();
        //                    roundMapList = bean.getData().getCourseRoundMapList();
        //                    transforToData();
        //                    initData();
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable throwable, boolean b) {
        //                if (isAdded())
        //                    ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + ",请检查网络");
        //            }
        //
        //            @Override
        //            public void onCancelled(CancelledException e) {
        //            }
        //
        //            @Override
        //            public void onFinished() {
        //                if (refresh_view.isRefreshing())
        //                    refresh_view.setRefreshing(false);
        //            }
        //        });

    }

    int[] imIds = {R.mipmap.yueqi_01, R.mipmap.yueqi_02, R.mipmap.yueqi_03, R.mipmap.yueqi_04, R.mipmap.yueqi_04, R.mipmap.yueqi_06, R.mipmap.yueqi_05};

    //转换数据
    private void transforToData() {
        dataList.clear();
        for (int i = 0; i < ordinaryList.size(); i++) {
            FirstBean.Data.ColumnList data = ordinaryList.get(i);
            if (data.getSystemCodeName().equals("名家名师") && data.getCourseList().size() < 3)
                continue;
            else if (data.getCourseList().size() < 1)
                continue;
            dataList.add(new MultipleItem(MultipleItem.HEAD, MultipleItem.HEAD_SIZE, data.getSystemCodeId(), data.getSystemCodeName(), data.getIconLight()));
            for (int j = 0; j < data.getCourseList().size(); j++) {
                FirstBean.Data.ColumnList.CourseList course = data.getCourseList().get(j);
                if (course.getClassification() == 1) {
                    dataList.add(new MultipleItem(MultipleItem.CONTENT_1, MultipleItem.CONTENT_1_SIZE, course));
                } else if (course.getClassification() == 2) {
                    dataList.add(new MultipleItem(MultipleItem.CONTENT_3, MultipleItem.CONTENT_3_SIZE, course));
                } else if (course.getClassification() == 3) {
                    dataList.add(new MultipleItem(MultipleItem.CONTENT_1, MultipleItem.CONTENT_1_SIZE, course));
                }
            }
        }
    }


    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        refresh_view.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        }, 0);

    }

    @OnClick({R.id.btn_scan, R.id.btn_search, R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                break;
            case R.id.btn_search:
                break;
            case R.id.btn_record:
                break;
        }
    }
}
