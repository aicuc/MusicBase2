package com.musicbase.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.adapter.ThirdAdapter;
import com.musicbase.adapter.YinkaAdapter;
import com.musicbase.entity.YinkaBean;
import com.musicbase.entity.YinkaDetailBean;
import com.musicbase.model.BannerImageLoader;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.SuperSwipeRefreshLayout;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ToastUtil;
import com.orhanobut.logger.Logger;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.musicbase.preferences.Preferences.SUCCESS;
import static com.musicbase.preferences.Preferences.appInfo;

public class YinkaActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    SuperSwipeRefreshLayout refresh_view;
    private int systemCodeId;
    private List<YinkaDetailBean.DataBean.CourseListBean> ordinaryList;
    private YinkaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_third);
        systemCodeId = getIntent().getIntExtra("systemCodeId", 0);
        initView();
        refresh_view.measure(0, 0);
        refresh_view.setRefreshing(true);
        requestData();
    }

    private void initView() {
        // TODO Auto-generated method stub
        View layout_title = findViewById(R.id.layout_title);
        layout_title.setVisibility(View.VISIBLE);
        ImageButton titlelayoutBack = findViewById(R.id.titlelayout_back);
        TextView titlelayoutTitle = findViewById(R.id.titlelayout_title);
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list);
        refresh_view = (SuperSwipeRefreshLayout) findViewById(R.id.swipe);

        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_transparent));
        recyclerView.addItemDecoration(divider);

        adapter = new YinkaAdapter(this, null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ordinaryList.get(position).getClassification() == 1) {
                    Intent intent = new Intent(YinkaActivity.this, DetailActivity.class);
                    intent.putExtra("courseId", ordinaryList.get(position).getCourseId());
                    startActivity(intent);
                } else if (ordinaryList.get(position).getClassification() == 2) {
                    if (ordinaryList.get(position).getAllowOpenInBrowser() == 2) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(ordinaryList.get(position).getCourseUrl());
                        intent.setData(content_url);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(YinkaActivity.this, BrowerActivity.class);
                        intent.putExtra("filePath", ordinaryList.get(position).getCourseUrl());
                        intent.putExtra("allowOpenInBrowser", ordinaryList.get(position).getAllowOpenInBrowser());
                        intent.putExtra("name", ordinaryList.get(position).getCourseName());
                        startActivity(intent);
                    }
                } else if (ordinaryList.get(position).getClassification() == 4) {
                    /**
                     * 音基 pndoo://jiayue:8888/home?id=
                     */
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ordinaryList.get(position).getAppJumpInfo().getAndroidSucc()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    List<ResolveInfo> activities = YinkaActivity.this.getPackageManager().queryIntentActivities(intent, 0);
                    boolean isValid = !activities.isEmpty();
                    if (isValid) {
                        startActivity(intent);
                    } else {
                        DialogUtils.showMyDialog(YinkaActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "本地未找到应用，前往下载？", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ordinaryList.get(position).getAppJumpInfo().getAndroidFail()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                } else if (ordinaryList.get(position).getClassification() == 5) {
                    Intent intent = new Intent(YinkaActivity.this, ContentListActivity.class);
                    intent.putExtra("id", ordinaryList.get(position).getSystemcode_id());
                    intent.putExtra("content", ordinaryList.get(position).getCourseName());
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void requestData() {
        OkGo.<String>post(Preferences.ACTIVITY_YINKA).tag(this).params("userId", SPUtility.getUserId(this) + "").params("systemCodeId", systemCodeId).params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<YinkaDetailBean>() {
                }.getType();
                YinkaDetailBean bean = null;
                try {
                    Logger.e("YinkaDetailBean=====" + response.body());
                    bean = gson.fromJson(response.body(), type);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (bean == null)
                    return;
                if (bean.getCode().equalsIgnoreCase(SUCCESS)) {
                    ordinaryList = bean.getData().getCourseList();
                    initData();
                } else {
                    ActivityUtils.showToast(YinkaActivity.this, "出现错误：" + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (!isFinishing())
                    ActivityUtils.showToast(YinkaActivity.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {
                if (refresh_view.isRefreshing())
                    refresh_view.setRefreshing(false);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Gson gson = new Gson();
                Type type = new TypeToken<YinkaDetailBean>() {
                }.getType();
                YinkaDetailBean bean = gson.fromJson(response.body(), type);
                if (bean != null) {
                    ordinaryList = bean.getData().getCourseList();
                    initData();
                }
            }
        });
    }

    private void initData() {
        adapter.setNewData(ordinaryList);
        adapter.notifyDataSetChanged();
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