package com.musicbase.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.BookDeleteAdapter;
import com.musicbase.entity.BookVOBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;

import java.util.ArrayList;
import java.util.List;

import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class BookDeleteActivity extends Activity implements OnRefreshListener {
    private SwipeRefreshLayout refresh_view;
    private List<BookVOBean.DataBean> books = new ArrayList<BookVOBean.DataBean>();// 书籍列表数据
    private ListView listview;
    private BookDeleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bookdelete);
        initView();
        initBook();
    }

    private void initView() {
        // TODO Auto-generated method stub
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(11);
                finish();
            }
        });
        refresh_view = (SwipeRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        refresh_view.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        listview = (ListView) findViewById(R.id.listView);
        adapter = new BookDeleteAdapter(this, books, 0);

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookBack(books.get(position).getCourseId()+"");
            }
        });
    }

    private void initBook() {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.DELETE_BOOKS).tag(this).params("userId", SPUtility.getUserId(this) + "").params("AppInfo", appInfo).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                BookVOBean bean = gson.fromJson(response.body(), BookVOBean.class);
                dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    books = bean.getData();
                    adapter.setList(books);
                    adapter.notifyDataSetChanged();
                } else {
                    ActivityUtils.showToast(BookDeleteActivity.this, "数据获取失败");
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(BookDeleteActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        initBook();
    }

    //找回书籍
    private void bookBack(String courseId) {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.BOOK_BACK).tag(this).params("userId", SPUtility.getUserId(this) + "").params("courseId", courseId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                BookVOBean bean = gson.fromJson(response.body(), BookVOBean.class);
                dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    ActivityUtils.showToast(BookDeleteActivity.this, "书籍已成功找回");
                    initBook();
                } else {
                    ActivityUtils.showToast(BookDeleteActivity.this, "书籍找回失败");
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(BookDeleteActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(11);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(11);
    }
}
