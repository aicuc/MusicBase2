package com.musicbase.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.BuyedListAdapter;
import com.musicbase.entity.BuyedBean;
import com.musicbase.model.recyclerviewmodel.MySection2;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.DetailActivity;
import com.musicbase.ui.view.SuperSwipeRefreshLayout;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.musicbase.preferences.Preferences.appInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.list)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SuperSwipeRefreshLayout refresh_view;
    Unbinder unbinder;
    private BuyedListAdapter adapter;

    private List<BuyedBean.Data> buyedList = new ArrayList<>();
    private List<MySection2> dataList = new ArrayList<>();
    private View notDataView;

    public BuyedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buyed, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        requestData();
        refresh_view.measure(0, 0);
        refresh_view.setRefreshing(true);
    }

    private void initView() {
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BuyedListAdapter(getActivity(), R.layout.item_buyed_content, R.layout.item_buyed_header, null);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                MySection2 mySection = dataList.get(position);
                if (!mySection.isHeader) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("courseId", mySection.t.getCourseId());
                    intent.putExtra("systemCodeId", mySection.getId());
                    intent.putExtra("buyed", true);
                    startActivity(intent);
                }
            }
        });

        //        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
        //            @Override
        //            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        //                DialogUtils.showMyDialog(getContext(), Preferences.SHOW_CONFIRM_DIALOG, "删除课程", "确定删除课程吗？", new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //
        //                        deleteYuexue(dataList.get(position).t.getCourseId());
        //                    }
        //                });
        //            }
        //        });

        notDataView = getActivity().getLayoutInflater().inflate(R.layout.layout_blank, (ViewGroup) recyclerView.getParent(), false);
        notDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void deleteYuexue(int courseId) {
        DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.DELETE_YUEXUE).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("courseId", courseId + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                BuyedBean bean = gson.fromJson(response.body(), BuyedBean.class);
                if (bean != null) {
                    ActivityUtils.showToast(getActivity(), bean.getCodeInfo());
                    if (bean.getCode().equalsIgnoreCase("SUCCESS"))
                        requestData();
                }
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.getException().getMessage());
                ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + ",请检查网络");
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onFinish() {
                if (refresh_view.isRefreshing())
                    refresh_view.setRefreshing(false);
                DialogUtils.dismissMyDialog();
            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    private void requestData() {
        //        RequestParams params = new RequestParams(Preferences.BUYED);
        //        params.addQueryStringParameter("userId", SPUtility.getUserId(getActivity()) + "");
        OkGo.<String>post(Preferences.BUYED).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                BuyedBean bean = null;
                try {
                    bean = gson.<BuyedBean>fromJson(response.body(), BuyedBean.class);

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (bean != null) {
                    buyedList = bean.getData();
                    if (buyedList != null && buyedList.size() == 0) {
                        adapter.setEmptyView(notDataView);
                        return;
                    }
                    transforToData();
                    initData();
                }
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.getException().getMessage());
                ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                if (refresh_view.isRefreshing())
                    refresh_view.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<BuyedBean>() {
                }.getType();
                BuyedBean bean = gson.fromJson(response.body(), type);
                if (bean != null) {
                    buyedList = bean.getData();
                    if (buyedList != null && buyedList.size() == 0) {
                        adapter.setEmptyView(notDataView);
                        return;
                    }
                    transforToData();
                    initData();
                }
            }
        });

        //        x.http().post(params, new Callback.CommonCallback<String>() {
        //            @Override
        //            public void onSuccess(String s) {
        //                Logger.d(s);
        //                Gson gson = new Gson();
        //                Type type = new TypeToken<BuyedBean>() {
        //                }.getType();
        //                BuyedBean bean = gson.fromJson(s, type);
        //                if (bean != null) {
        //                    buyedList = bean.getData();
        //                    if (buyedList != null && buyedList.size() == 0) {
        //                        adapter.setEmptyView(notDataView);
        //                        return;
        //                    }
        //                    transforToData();
        //                    initData();
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable throwable, boolean b) {
        //                Logger.e(throwable.getMessage());
        //                ActivityUtils.showToast(getActivity(), getString(R.string.load_fail) + ",请检查网络");
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

    private void transforToData() {
        dataList.clear();
        if (buyedList == null || buyedList.size() == 0)
            return;
        for (int i = 0; i < buyedList.size(); i++) {
            BuyedBean.Data data = buyedList.get(i);
            if (data.getCourseList().size() == 0)
                continue;
            dataList.add(new MySection2(true, data.getSystemCodeId(), data.getSystemCodeName(), false));
            for (int j = 0; j < data.getCourseList().size(); j++) {
                BuyedBean.Data.CourseList course = data.getCourseList().get(j);
                if (course.getClassification() == 1)
                    dataList.add(new MySection2(course));
            }
        }
    }

    private void initData() {
        // TODO Auto-generated method stub
        Logger.d(dataList.size());
        adapter.setNewData(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRefresh() {
        refresh_view.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        }, 0);
    }
}
