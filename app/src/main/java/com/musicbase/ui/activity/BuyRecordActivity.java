package com.musicbase.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.BuyRecordAdapter;
import com.musicbase.entity.OrderRecordBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class BuyRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private TextView delete_text;
    private RecyclerView recycler_address_list;
    private BuyRecordAdapter buyRecordAdapter;
    private List<OrderRecordBean.DataBean> dataBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_record);
        initView();
        initData();
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        recycler_address_list = (RecyclerView) findViewById(R.id.recycler_address_list);

        titlelayout_back.setOnClickListener(this);

        titlelayout_title.setText("购买记录");

        dataBeans = new ArrayList<>();
        buyRecordAdapter = new BuyRecordAdapter(R.layout.item_buy_record, dataBeans);
        recycler_address_list.setLayoutManager(new LinearLayoutManager(this));
        recycler_address_list.setAdapter(buyRecordAdapter);

    }

    private void initData() {
        OkGo.<String>post(Preferences.ORDER_RECORDE).tag(this)
                .params("userId", SPUtility.getUserId(this) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                OrderRecordBean bean = gson.fromJson(response.body(), OrderRecordBean.class);
                if (bean != null && bean.getData() != null) {
                    dataBeans.clear();
                    dataBeans.addAll(bean.getData());
                    if (dataBeans != null && dataBeans.size() > 0) {
                        buyRecordAdapter.notifyDataSetChanged();

                    }

                }
                if (!bean.getCode().equalsIgnoreCase("SUCCESS"))
                    ActivityUtils.showToast(BuyRecordActivity.this, bean.getCodeInfo());
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.getException().getMessage());
                ActivityUtils.showToast(BuyRecordActivity.this, getString(R.string.load_fail) + ",请检查网络");
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

        }
    }
}
