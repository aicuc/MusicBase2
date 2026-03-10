package com.musicbase.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.ContentListAdapter;
import com.musicbase.entity.ContentListBean;
import com.musicbase.entity.FirstBean;
import com.musicbase.model.recyclerviewmodel.MySection;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.SuperSwipeRefreshLayout;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.musicbase.preferences.Preferences.SUCCESS;
import static com.musicbase.preferences.Preferences.appInfo;

public class ContentListActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_view)
    SuperSwipeRefreshLayout refreshView;
    private int getId;
    private String getName;
    private int systemCodeId;
    private String systemCodeName;
    private ContentListAdapter adapter;

    private List<ContentListBean.Data> childrenList;
    private List<FirstBean.Data.ColumnList.CourseList> courseList;
    private List<MySection> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        ButterKnife.bind(this);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        getId = getIntent().getIntExtra("id", 0);
        getName = getIntent().getStringExtra("content");
    }

    private void initView() {
        initTitle();
        refreshView.setOnRefreshListener(this);
        refreshView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContentListAdapter(this, R.layout.item_head2_content, R.layout.item_head_2, dataList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MySection mySection = dataList.get(position);
                if (mySection.isHeader)
                    ActivityUtils.showToast(ContentListActivity.this, "已加载全部内容");
                else {
                    if (mySection.t.getClassification() == 1 || mySection.t.getClassification() == 3) {
                        Intent intent = new Intent(ContentListActivity.this, DetailActivity.class);
                        intent.putExtra("courseId", mySection.t.getCourseId());
                        startActivity(intent);
                    } else if (mySection.t.getClassification() == 4) {
                        /**
                         * 音基 pndoo://jiayue:8888/home?id=
                         */
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mySection.t.getAppJumpInfo().getAndroidSucc()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);
                        boolean isValid = !activities.isEmpty();
                        if (isValid) {
                            startActivity(intent);
                        } else {
                            DialogUtils.showMyDialog(ContentListActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "本地未找到应用，前往下载？", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mySection.t.getAppJumpInfo().getAndroidFail()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else if (mySection.t.getClassification() == 5) {//跳转栏目详情 "更多"
                        Intent intent = new Intent(ContentListActivity.this, ContentListActivity.class);
                        intent.putExtra("id", mySection.t.getCourseId());
                        intent.putExtra("content", mySection.t.getCourseName());
                        startActivity(intent);
                    } else {
                        if (mySection.t.getAllowOpenInBrowser() == 2) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(mySection.t.getCourseUrl());
                            intent.setData(content_url);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ContentListActivity.this, BrowerActivity.class);
                            intent.putExtra("filePath", mySection.t.getCourseUrl());
                            intent.putExtra("name", mySection.t.getCourseName());
                            intent.putExtra("allowOpenInBrowser", mySection.t.getAllowOpenInBrowser());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initTitle() {
        if (!TextUtils.isEmpty(getName))
            titlelayoutTitle.setText(getName);
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void requestData() {
        //        RequestParams params = new RequestParams(Preferences.HOME_MORE_INFO);
        //        params.addQueryStringParameter("userId", SPUtility.getUserId(this) + "");
        //        params.addQueryStringParameter("systemCodeId", getId + "");
        //
        //        x.http().post(params, new Callback.CommonCallback<String>() {
        //            @Override
        //            public void onSuccess(String s) {
        //                Logger.d(s);
        //                Gson gson = new Gson();
        //                Type type = new TypeToken<ContentListBean>() {
        //                }.getType();
        //                ContentListBean bean = gson.fromJson(s, type);
        //                if (bean != null) {
        //                    childrenList = bean.getData().getChildren();
        //                    transforToData();
        //                    initData();
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable throwable, boolean b) {
        //                Logger.e(throwable.getMessage());
        //                ActivityUtils.showToast(ContentListActivity.this, getString(R.string.load_fail) + ",请检查网络");
        //            }
        //
        //            @Override
        //            public void onCancelled(CancelledException e) {
        //            }
        //
        //            @Override
        //            public void onFinished() {
        //                if (refreshView.isRefreshing())
        //                    refreshView.setRefreshing(false);
        //            }
        //        });

        OkGo.<String>post(Preferences.HOME_MORE_INFO).tag(this).params("userId", SPUtility.getUserId(this) + "").params("AppInfo", appInfo).params("systemCodeId", getId + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.e("ContentListBean===" + response.body());
                Gson gson = new Gson();
                //                        Type type = new TypeToken<ContentListBean>() {
                //                        }.getType();
                ContentListBean bean = gson.fromJson(response.body(), ContentListBean.class);
                if (bean != null && bean.getCode().equalsIgnoreCase(SUCCESS)) {
                    childrenList = bean.getData().getChildren();
                    courseList = bean.getData().getCourseList();
                    systemCodeId = bean.getData().getSystemCodeId();
                    systemCodeName = bean.getData().getSystemCodeName();
                    transforToData();
                    initData();
                }
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.message());
                ActivityUtils.showToast(ContentListActivity.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                if (refreshView.isRefreshing())
                    refreshView.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                //                        Type type = new TypeToken<ContentListBean>() {
                //                        }.getType();
                ContentListBean bean = gson.fromJson(response.body(), ContentListBean.class);
                if (bean != null) {
                    childrenList = bean.getData().getChildren();
                    courseList = bean.getData().getCourseList();
                    transforToData();
                    initData();
                }
            }
        });

    }

    private void transforToData() {
        dataList.clear();
        for (int i = 0; i < childrenList.size(); i++) {
            ContentListBean.Data data = childrenList.get(i);
            if (data.getCourseList().size() == 0)
                continue;
            dataList.add(new MySection(true, data.getSystemCodeId(), data.getSystemCodeName(), true));
            for (int j = 0; j < data.getCourseList().size(); j++) {
                FirstBean.Data.ColumnList.CourseList course = data.getCourseList().get(j);
                dataList.add(new MySection(course));
            }
        }

        if (courseList.size() == 0)
            return;
        dataList.add(new MySection(true, systemCodeId, systemCodeName, true));
        for (int j = 0; j < courseList.size(); j++) {
            FirstBean.Data.ColumnList.CourseList course = courseList.get(j);
            dataList.add(new MySection(course));
        }
    }

    private void initData() {
        // TODO Auto-generated method stub
        Logger.d(dataList.size());
        adapter.setNewData(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshView.setRefreshing(true);
        requestData();
    }

    @Override
    public void onRefresh() {
        refreshView.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        }, 0);
    }
}
