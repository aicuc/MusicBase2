package com.musicbase.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.musicbase.R;
import com.musicbase.entity.ChooseHeadBean;
import com.musicbase.entity.HeadBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class ChooseHeadActivity extends AppCompatActivity {
    private TextView titlelayout_title,btn_header_right;
    ImageButton titlelayout_back;
    ImageView iv_head;
    RecyclerView recyclerView;
    ChooseHeadAdapter adapter;
    private final String TAG = getClass().getSimpleName();
    List<String> data = new ArrayList<>();
    int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_head);
        initView();
        getData();
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        titlelayout_title.setText("选择图片");
        btn_header_right =  (TextView) findViewById(R.id.delete_text);
        btn_header_right.setText("存储");
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseHead(data.get(index));
            }
        });
        iv_head = findViewById(R.id.iv_head);
        iv_head.setLayoutParams(new LinearLayout.LayoutParams(ActivityUtils.getScreenWidth(this), ActivityUtils.getScreenWidth(this)));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false));
        adapter = new ChooseHeadAdapter(this,R.layout.item_choosehead,null);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                index = position;
                Glide.with(ChooseHeadActivity.this).load(Preferences.IMAGE_HTTP_LOCATION+data.get(position)).into(iv_head);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void chooseHead(String url){
        RequestParams params = new RequestParams(Preferences.CHOOSE_HEAD);
        params.addQueryStringParameter("userId", SPUtility.getUserId(this) + "");
        params.addQueryStringParameter("avatarPath",  url);

        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "CHOOSE_HEAD--------" + s);
                Gson gson = new Gson();
                ChooseHeadBean bean = gson.fromJson(s, ChooseHeadBean.class);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    SPUtility.putSPString(ChooseHeadActivity.this,"userAvatar",bean.getData().getUserAvatar());
                    ActivityUtils.showToast(ChooseHeadActivity.this, "头像修改成功");
                    finish();
                } else if (bean != null && bean.getCode().equals("FAIL")) {
                    ActivityUtils.showToast(ChooseHeadActivity.this, bean.getCodeInfo());
                }
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(ChooseHeadActivity.this, "加载失败,请检查网络。");
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
            }
        });
    }


    private void getData() {
        RequestParams params = new RequestParams(Preferences.LIST_HEAD);

        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "LIST_HEAD--------" + s);
                Gson gson = new Gson();
                HeadBean bean = gson.fromJson(s, HeadBean.class);
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    data.clear();
                    data.addAll(bean.getData());
                    adapter.setNewData(data);
                    adapter.notifyDataSetChanged();
                    Glide.with(ChooseHeadActivity.this).load(Preferences.IMAGE_HTTP_LOCATION+data.get(0)).into(iv_head);
                } else if (bean != null && bean.getCode().equals("FAIL")) {
                    ActivityUtils.showToast(ChooseHeadActivity.this, bean.getCodeInfo());
                }
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(ChooseHeadActivity.this, "加载失败,请检查网络。");
                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
            }
        });
    }

    public class ChooseHeadAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        private Context context;


        public ChooseHeadAdapter(Context context, int layoutId, List<String> list) {
            super(layoutId, list);
            this.context = context;

        }

        @Override
        protected void convert(BaseViewHolder helper, String data) {
            Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+data).into((ImageView)helper.getView(R.id.iv));
        }
    }
}