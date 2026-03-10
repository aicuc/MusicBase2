package com.musicbase.ui.activity;

import android.Manifest;
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
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.musicbase.entity.SearchBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.superplayer.SuperPlayerActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ZipUtils;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.musicbase.preferences.Preferences.FILE_DOWNLOAD_URL;
import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerview;
    private SmartRefreshLayout smartRefresh;
    private DetailAdapter adapter;
    private List<DetailBean.DataBean.ResourceListBean> resourceList = new ArrayList<>();
    private EditText editText;
    private Button btn;
    private int pageNo = 1;
    private final int pageSize = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private final String TAG = getClass().getSimpleName();

    private void initView() {
        smartRefresh = findViewById(R.id.smart_refresh);
        smartRefresh.setEnableRefresh(false);
        smartRefresh.setEnableLoadMore(true);
        smartRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                search();
            }
        });

        editText = findViewById(R.id.et);
        btn = findViewById(R.id.btn_search);
        btn.setOnClickListener(this);
        editText.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    if(editText.getText()!=null&&!TextUtils.isEmpty(editText.getText().toString())){
                        pageNo = 1;
                        search();
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    btn.setText("取消");
                    adapter.setNewData(null);
                    adapter.notifyDataSetChanged();
                }else{
                    btn.setText("搜索");
                }
            }
        });

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
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
        adapter = new DetailAdapter(this, R.layout.item_course_catalog, null,true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                DetailBean.DataBean.ResourceListBean bean = resourceList.get(position);
                intent.putExtra("courseId", Integer.parseInt(bean.getCourseId()));
                intent.putExtra("autoBean",bean);
                startActivity(intent);
            }
        });
        recyclerview.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                if(editText.getText()!=null&&!TextUtils.isEmpty(editText.getText().toString())){
                    pageNo = 1;
                    search();
                }else{
                    finish();
                }
                break;
        }
    }

    private void search() {
        String search_content = editText.getText().toString();
        if(search_content.trim().length()<2) {
            Toast.makeText(this, "搜索内容至少两个字符", Toast.LENGTH_LONG).show();
            return;
        }
        if(pageNo==1) {
            resourceList.clear();
            adapter.setNewData(null);
            adapter.notifyDataSetChanged();
            smartRefresh.setEnableLoadMore(true);
        }
        OkGo.<String>post(Preferences.SEARCH_BOOKS).tag(this).params("userId", SPUtility.getUserId(this) + "").params("queryWord", search_content).params("pageNo", pageNo+"").params("pageSize", pageSize+"").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Log.e(TAG, "SEARCH_BOOKS=====" + response.body());
                SearchBean bean = gson.fromJson(response.body(), SearchBean.class);
                if (bean != null) {
                    if(bean.getData().getResults()==null||bean.getData().getResults().size()==0){
                        Toast.makeText(SearchActivity.this,"未搜到匹配结果",Toast.LENGTH_LONG).show();
                    }else{
                        resourceList.addAll(bean.getData().getResults());
                        if(bean.getData().getResults()!=null&&bean.getData().getResults().size()<pageSize){
                            smartRefresh.finishLoadMoreWithNoMoreData();
                        }
                        initData(bean.getData().getResults());
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                //                Logger.e(response.getRawResponse());
                ActivityUtils.showToast(SearchActivity.this, getString(R.string.load_fail) + ",请检查网络");
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
                SearchBean bean = gson.fromJson(response.body(), SearchBean.class);
                if (bean != null) {
                    resourceList = bean.getData().getResults();
                    initData(bean.getData().getResults());
                }
            }
        });
    }






    private void initData(List<DetailBean.DataBean.ResourceListBean> list) {
        if(pageNo == 1){
            adapter.setNewData(list);
        }else{
            adapter.addData(list);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
