package com.musicbase.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.AddressListAdapter;
import com.musicbase.entity.AddressBean;
import com.musicbase.entity.AddressBean.DataBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressList extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private RecyclerView recycler_address_list;
    private Button btn_new_address;

    private AddressListAdapter addressListAdapter;
    private List<DataBean> listBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        initView();
        initData();
    }

    private void initData() {
        OkGo.<String>post(Preferences.ADDRESS_LIST).tag(this)
                .params("userId", SPUtility.getUserId(this) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                AddressBean bean = gson.fromJson(response.body(), AddressBean.class);
                if (bean != null && bean.getData() != null) {
                    listBeans.clear();
                    listBeans.addAll(bean.getData());
                    if (listBeans != null && listBeans.size() > 0) {
                        addressListAdapter.notifyDataSetChanged();

                    }

                }
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.getException().getMessage());
                ActivityUtils.showToast(AddressList.this, getString(R.string.load_fail) + ",请检查网络");
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                AddressBean bean = gson.fromJson(response.body(), AddressBean.class);
                if (bean != null && bean.getData() != null) {
                    listBeans.clear();
                    listBeans.addAll(bean.getData());
                    if (listBeans != null && listBeans.size() > 0) {
                        addressListAdapter.notifyDataSetChanged();

                    }

                }
            }
        });
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        recycler_address_list = (RecyclerView) findViewById(R.id.recycler_address_list);
        btn_new_address = (Button) findViewById(R.id.btn_new_address);

        titlelayout_back.setOnClickListener(this);
        btn_new_address.setOnClickListener(this);
        titlelayout_title.setText("收货地址");
        listBeans = new ArrayList<>();
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_recycler));
        recycler_address_list.addItemDecoration(divider);

        addressListAdapter = new AddressListAdapter(R.layout.item_address, listBeans);
        recycler_address_list.setLayoutManager(new LinearLayoutManager(this));
        recycler_address_list.setAdapter(addressListAdapter);

        addressListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("dataBean", (Serializable) listBeans.get(position));
                setResult(222, intent);
                finish();

            }
        });
        addressListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(AddressList.this, AddNewAddress.class);
                intent.putExtra("dataBean", (Serializable) listBeans.get(position));
                intent.putExtra("type", 1);
                startActivityForResult(intent, 111);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(333);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == 333) {
            initData();
        } else if (resultCode == 444)
            finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:

                finish();
                break;
            case R.id.btn_new_address:

                startActivityForResult(new Intent(AddressList.this, AddNewAddress.class), 111);
                break;
        }
    }
}
