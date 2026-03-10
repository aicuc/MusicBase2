package com.musicbase.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.entity.AddressAddBean;
import com.musicbase.entity.AddressBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ToastUtil;
import com.orhanobut.logger.Logger;
import com.sh.zsh.code.check.FormCheckInterface;
import com.sh.zsh.code.form.FormInit;
import com.sh.zsh.code.form.FormUtls;

import static com.musicbase.ui.activity.PayChooseActivity.expressId;
import static com.musicbase.ui.activity.PayChooseActivity.hasExpressId;

public class AddNewAddress extends AppCompatActivity implements View.OnClickListener, FormCheckInterface {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private TextView spinner;
    private ToggleButton switch_online;
    private Button btn_save_address;
    private int type;
    private AddressBean.DataBean dataBean;
    private String areaCode;
    private String id;
    private EditText user_name;
    private EditText phone_number;
    private EditText address_text;
    private TextView delete_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        initView();
        initData();
    }

    private void initData() {
//        dataBean = new AddressBean.DataBean();
        type = getIntent().getIntExtra("type", 0);
        String title = type == 0 ? "新建收货地址" : "编辑收货地址";
        titlelayout_title.setText(title);

        if (type == 1) {
            delete_text.setVisibility(View.VISIBLE);
            dataBean = (AddressBean.DataBean) getIntent().getSerializableExtra("dataBean");
//            FormUtls.objectToForm(this, dataBean);
            user_name.setText(dataBean.getUserName());
            phone_number.setText(dataBean.getTelphone());
            address_text.setText(dataBean.getAddress());
            spinner.setText(dataBean.getAreaName());
            areaCode = dataBean.getAreaCode();
            id = dataBean.getId();
            if (dataBean.getIsDefault().equals("1")) {
                switch_online.setChecked(true);
            } else {
                switch_online.setChecked(false);
            }
        }

    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);

        spinner = (TextView) findViewById(R.id.spinner);
        switch_online = (ToggleButton) findViewById(R.id.switch_online);
        btn_save_address = (Button) findViewById(R.id.btn_save_address);

        titlelayout_back.setOnClickListener(this);
        spinner.setOnClickListener(this);
        switch_online.setOnClickListener(this);
        btn_save_address.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        user_name.setOnClickListener(this);
        phone_number = (EditText) findViewById(R.id.phone_number);
        phone_number.setOnClickListener(this);
        address_text = (EditText) findViewById(R.id.address_text);
        address_text.setOnClickListener(this);
        FormInit.injection(this);

        delete_text = (TextView) findViewById(R.id.delete_text);
        delete_text.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        /**
         * 注销表单
         */
        FormInit.deleteInjection(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:
                finish();
                break;
            case R.id.switch_online:

                break;
            case R.id.spinner:
                //隐藏软键盘
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(AddNewAddress.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                JDCityPicker cityPicker = new JDCityPicker();
                JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

                jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
                cityPicker.init(this);
                cityPicker.setConfig(jdCityConfig);
                cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        String cityName = city.getName();
                        if (cityName.equalsIgnoreCase("省直辖县级行政单位")) {
                            cityName = province.getName();
                        }
                        spinner.setText("中国" + province.getName() + cityName + district.getName());
                        areaCode = district.getCode();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
                cityPicker.showCityPicker();

                break;
            case R.id.btn_save_address:
                dataBean = FormUtls.formToObjectAndCheck(this, AddressBean.DataBean.class);
                if (dataBean != null) {
                    if (type == 0)
                        submit(Preferences.ADDRESS_ADD);
                    else
                        submit(Preferences.ADDRESS_UPDATE);
                }

                break;
            case R.id.delete_text:
                DialogUtils.showMyDialog(AddNewAddress.this, Preferences.SHOW_CONFIRM_DIALOG, "删除地址", "确定删除地址吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteAddress();
                    }
                });
                break;
        }
    }

    private void deleteAddress() {
        OkGo.<String>post(Preferences.ADDRESS_DELETE).tag(this)
                .params("userId", SPUtility.getUserId(this) + "")
                .params("id", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Logger.d(response);
                        Gson gson = new Gson();
                        AddressBean bean = gson.fromJson(response.body(), AddressBean.class);
                        if (bean != null) {
                            ToastUtil.showShort(AddNewAddress.this, bean.getCodeInfo());
                            if (bean.getCode().equalsIgnoreCase("SUCCESS")) {
                                setResult(333);
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Logger.e(response.getException().getMessage());
                        ActivityUtils.showToast(AddNewAddress.this, getString(R.string.load_fail) + ",请检查网络");
                    }

                    @Override
                    public void onFinish() {

                    }


                });
    }

    private void submit(String url) {
//        dataBean.setUserId(SPUtility.getUserId(this) + "");
        OkGo.<String>post(url).tag(this)
                .params("userId", SPUtility.getUserId(this) + "")
                .params("userName", dataBean.getUserName())
                .params("telphone", dataBean.getTelphone())
                .params("areaCode", areaCode)
                .params("id", id)
                .params("address", dataBean.getAddress())
                .params("isDefault", switch_online.isChecked() ? "1" : "0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Logger.d(response.body());
                        Gson gson = new Gson();
                        AddressAddBean bean = gson.fromJson(response.body(), AddressAddBean.class);
                        if (bean != null) {
                            ToastUtil.showShort(AddNewAddress.this, bean.getCodeInfo());
                            if (bean.getCode().equalsIgnoreCase("SUCCESS")) {
                                if(url.equals(Preferences.ADDRESS_ADD)) {
                                    expressId = bean.getData().getId();
                                    hasExpressId = true;
                                }
                                setResult(444);
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Logger.e(response.getException().getMessage());
                        ActivityUtils.showToast(AddNewAddress.this, getString(R.string.load_fail) + ",请检查网络");
                    }

                    @Override
                    public void onFinish() {

                    }


                });
    }

    @Override
    public boolean formCheck(View v) {
        return true;
    }

    @Override
    public void formCheckParamCall(View v, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
