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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.musicbase.adapter.ThirdAdapter;
import com.musicbase.entity.FirstBean;
import com.musicbase.entity.YinkaBean;
import com.musicbase.model.BannerImageLoader;
import com.musicbase.model.recyclerviewmodel.MultipleItem;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.BrowerActivity;
import com.musicbase.ui.activity.CardActivity;
import com.musicbase.ui.activity.ContentListActivity;
import com.musicbase.ui.activity.DetailActivity;
import com.musicbase.ui.activity.SongScore;
import com.musicbase.ui.activity.WorkWebActivity;
import com.musicbase.ui.activity.YinkaActivity;
import com.musicbase.ui.view.BtnListItemDecoration;
import com.musicbase.ui.view.SuperSwipeRefreshLayout;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ToastUtil;
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

/**
 * 音咖计划
 */
public class ThirdFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerView;
    SuperSwipeRefreshLayout refresh_view;

    @BindView(R.id.banner)
    Banner banner;
    private View mRootView;
    Unbinder unbinder2;
    private ThirdAdapter adapter;

    private List<YinkaBean.DataBean.CourseRoundMapListBean> roundMapList;
    private List<YinkaBean.DataBean.ColumnListBean> ordinaryList;
    private final String TAG = getClass().getSimpleName();

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_third, container, false);
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

        // TODO Auto-generated method stub
        List<String> imagepaths = new ArrayList<>();
        for (YinkaBean.DataBean.CourseRoundMapListBean list : roundMapList) {
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

        adapter.setNewData(ordinaryList);
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        // TODO Auto-generated method stub
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.list);
        refresh_view = (SuperSwipeRefreshLayout) mRootView.findViewById(R.id.swipe);

        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider_transparent));
        recyclerView.addItemDecoration(divider);

        adapter = new ThirdAdapter(getActivity(),null);
        //动态点击按钮 header
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_third, null);
        unbinder2 = ButterKnife.bind(this, header);

        adapter.addHeaderView(header);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(ordinaryList.get(position).getSc_release() == 0){
                    ToastUtil.showShort(getActivity(),"即将上线");
                }else{
                    Intent intent = new Intent(getActivity(), YinkaActivity.class);
                    intent.putExtra("systemCodeId",ordinaryList.get(position).getSystemCodeId());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void requestData() {
        OkGo.<String>post(Preferences.FRAGMENT_YINKA).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<YinkaBean>() {
                }.getType();
                YinkaBean bean = null;
                try {
                    Logger.e("YinkaBean====="+response.body());
                    bean = gson.fromJson(response.body(), type);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (bean == null)
                    return;
                if (bean.getCode().equalsIgnoreCase(SUCCESS)) {
                    ordinaryList = bean.getData().getColumnList();
                    roundMapList = bean.getData().getCourseRoundMapList();
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
                Type type = new TypeToken<YinkaBean>() {
                }.getType();
                YinkaBean bean = gson.fromJson(response.body(), type);
                if (bean != null) {
                    ordinaryList = bean.getData().getColumnList();
                    roundMapList = bean.getData().getCourseRoundMapList();
                    initData();
                }
            }
        });
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
}
