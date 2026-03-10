package com.musicbase.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.alipay.PayResult;
import com.musicbase.entity.AddressBean;
import com.musicbase.entity.AliBean;
import com.musicbase.entity.OrderBean;
import com.musicbase.entity.PostageBean;
import com.musicbase.entity.WXBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.view.QuantityView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.ArithUtil;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class PayChooseActivity extends BaseActivity {


    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private LinearLayout mAliPay, mWxPay;
    private CheckBox cb_alipay, cb_wx;

    private String sum;
    private String bookName;
    private String image_url;
    private String attachName;
    private String attachId;
    private String attachType;
    private String price;
    private String orderCode;
    private String postage;
    public static String expressId;
    public static boolean hasExpressId = false;
    private int choose_Type = 0;//0为支付宝 1微信
    private int courseBookType;//1为虚拟课程 2为实体图书
    private final String TAG = getClass().getSimpleName();
    private Button btn_pay;
    public static final int PAY_SUCCESS = 0x033;
    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;
    private List<AddressBean.DataBean> listBeans;
    private AddressBean.DataBean dataBean;
    private boolean hasRefresh = true;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
//                    Toast.makeText(PayChooseActivity.this, resultInfo.toString(), Toast.LENGTH_SHORT).show();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(PayChooseActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        setResult(PAY_SUCCESS);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayChooseActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayChooseActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(PayChooseActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };
    private TextView address_name;
    private TextView address_phone;
    private TextView address_default;
    private TextView address_text;
    private ImageView address_edit;
    private TextView add_new_text;
    private LinearLayout address_choose;
    private TextView fee_product;
    private TextView fee_trans;
    private TextView tv_total;
    private QuantityView quantityView;
    private int quantity=1;//商品数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        Bundle b = getIntent().getBundleExtra("data");

        sum = b.getString("sum");
        bookName = b.getString("bookName");
        image_url = b.getString("image_url_str");
        attachName = b.getString("attachName");
        price = b.getString("price");
        attachType = b.getString("attachType");
        attachId = b.getString("attachId");
        courseBookType = b.getInt("courseBookType");
//        orderCode = b.getString("ordercode");

        initView();
        initListener();
        if (courseBookType == 2)
            initAddress();
        else {
            address_choose.setVisibility(View.GONE);
            add_new_text.setVisibility(View.GONE);
            quantityView.setVisibility(View.INVISIBLE);
            initOrder("1");
        }

    }

    private void initAddress() {
        listBeans = new ArrayList<>();
        OkGo.<String>post(Preferences.ADDRESS_LIST).tag(this)
                .params("userId", SPUtility.getUserId(this) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                AddressBean bean = gson.fromJson(response.body(), AddressBean.class);
                if (bean != null && bean.getData() != null) {
                    listBeans.addAll(bean.getData());

                    if (listBeans != null && listBeans.size() > 0) {
                        for (AddressBean.DataBean listBean : listBeans) {
                            if (hasExpressId && listBean.getId().equalsIgnoreCase(expressId)) {
                                dataBean = listBean;
                                break;
                            } else if (listBean.getIsDefault().equalsIgnoreCase("1")) {
                                dataBean = listBean;
                                break;
                            }
                        }

                        if (dataBean == null || TextUtils.isEmpty(dataBean.getId())) {
                            dataBean = listBeans.get(0);
//                            initData();
                        }
                    }
                }
                initData();
            }

            @Override
            public void onError(Response<String> response) {
                Logger.e(response.getException().getMessage());
                ActivityUtils.showToast(PayChooseActivity.this, getString(R.string.load_fail) + ",请检查网络");
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
                    listBeans.addAll(bean.getData());

                    if (listBeans != null && listBeans.size() > 0) {
                        for (AddressBean.DataBean listBean : listBeans) {
                            if (listBean.getIsDefault().equalsIgnoreCase("1")) {
                                dataBean = listBean;
                            }
                        }

                        if (dataBean == null || TextUtils.isEmpty(dataBean.getId())) {
                            dataBean = listBeans.get(0);
//                            initData();
                        }

                    }


                }
                initData();
            }
        });
    }

    private void initData() {
        if (dataBean == null || TextUtils.isEmpty(dataBean.getId())) {
//            dataBean = listBeans.get(0);
            address_choose.setVisibility(View.GONE);
            add_new_text.setVisibility(View.VISIBLE);
        } else {
            expressId = dataBean.getId();
            address_choose.setVisibility(View.VISIBLE);
            add_new_text.setVisibility(View.GONE);
            address_name.setText(dataBean.getUserName());
            address_phone.setText(dataBean.getTelphone());
            address_text.setText(dataBean.getAreaName() + dataBean.getAddress());
            if (dataBean.getIsDefault().equalsIgnoreCase("1")) {
                address_default.setVisibility(View.VISIBLE);
            } else {
                address_default.setVisibility(View.GONE);
            }

        }
        initOrder(quantity+"");
    }

    private void initYunfei(String quantity) {
        DialogUtils.showMyDialog(PayChooseActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.COUNT_POSTAGE).tag(this)
                .params("userId", SPUtility.getUserId(this) + "")
                .params("attachId", attachId)
                .params("amount", TextUtils.isEmpty(quantity)?"1":quantity)
                .params("expressId", expressId)
                .params("attachType", attachType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        PostageBean bean = gson.fromJson(response.body(), PostageBean.class);
                        dismissMyDialog();
                        if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                            postage = bean.getData().getPostage();
                            fee_trans.setText("¥ " + postage);
                            double sum = ArithUtil.mul(Double.valueOf(price),Double.valueOf(quantity));
                            Log.e(TAG,"price==="+price+"----"+Double.valueOf(price)+"------quantity="+Double.valueOf(quantity));
                            fee_product.setText("¥ " + String.format("%.2f", sum));
                            double all_price = Double.valueOf(postage) + sum;
                            tv_total.setText("总计：¥ " + String.format("%.2f", all_price));
//                            initYunfei();
                        } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                            ActivityUtils.showToast(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ActivityUtils.showToast(PayChooseActivity.this, "加载失败,请检查网络!");
                        dismissMyDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissMyDialog();
                    }


                });
    }

    private void initOrder(String quantity) {

        DialogUtils.showMyDialog(PayChooseActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.BUY_ORDER).tag(this)
                .params("userId", SPUtility.getUserId(this) + "")
                .params("attachId", attachId)
                .params("amount", TextUtils.isEmpty(quantity)?"1":quantity)
                .params("expressId", expressId)
                .params("systemType", "android")
                .params("attachType", attachType)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        OrderBean bean = gson.fromJson(response.body(), OrderBean.class);
                        dismissMyDialog();
                        if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                            orderCode = bean.getData().getAttachCode();
                            if (courseBookType == 2)
                                initYunfei(quantity);
                            else {
                                fee_trans.setVisibility(View.GONE);
//                                fee_trans.setText("¥ 0.00");
                                tv_total.setText("¥ " + price);
                            }
                        } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                            ActivityUtils.showToast(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ActivityUtils.showToast(PayChooseActivity.this, "加载失败,请检查网络!");
                        dismissMyDialog();
                    }

                    @Override
                    public void onFinish() {
                        dismissMyDialog();
                    }


                });
    }

    private boolean isWXpay = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasRefresh && courseBookType == 2) {
            initAddress();
        }
        hasRefresh = false;
        if (isWXpay) {
            setResult(PAY_SUCCESS);
            finish();
        }
    }

    private void initView() {
        // TODO Auto-generated method stub

//        DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
//        String geff = df.format(price);//返回的是String类型的
        titlelayoutTitle.setText("确认订单信息");
        titlelayoutBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Glide.with(this).load(Preferences.IMAGE_HTTP_LOCATION + image_url).into((ImageView) findViewById(R.id.iv_book));
        ((TextView) findViewById(R.id.tv_bookname)).setText(bookName);
//        ((TextView) findViewById(R.id.tv_attach_name)).setText("图书附件 " + attachName);
        ((TextView) findViewById(R.id.tv_price)).setText("¥ " + price);
        ((TextView) findViewById(R.id.tv_sum)).setText("共" + sum + "课时");

        mAliPay = (LinearLayout) findViewById(R.id.layout_alipay);
        mWxPay = (LinearLayout) findViewById(R.id.layout_wx);
        cb_alipay = (CheckBox) findViewById(R.id.cb_alipay);
        cb_wx = (CheckBox) findViewById(R.id.cb_wx);

        tv_total = ((TextView) findViewById(R.id.tv_total));
        btn_pay = (Button) findViewById(R.id.btn_submit);

        address_name = (TextView) findViewById(R.id.address_name);
        address_phone = (TextView) findViewById(R.id.address_phone);
        address_default = (TextView) findViewById(R.id.address_default);
        address_text = (TextView) findViewById(R.id.address_text);
        address_edit = (ImageView) findViewById(R.id.address_edit);
        address_edit.setImageDrawable(getResources().getDrawable(R.mipmap.mine_50));
        address_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(PayChooseActivity.this, AddressList.class), 111);
            }
        });

        add_new_text = (TextView) findViewById(R.id.add_new_text);
        add_new_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayChooseActivity.this, AddNewAddress.class);
                intent.putExtra("type", 0);
                startActivityForResult(intent, 111);
            }
        });
        address_choose = (LinearLayout) findViewById(R.id.address_choose);
        fee_product = (TextView) findViewById(R.id.fee_product);
        fee_trans = (TextView) findViewById(R.id.fee_trans);
        fee_product.setText("¥ " + price);
        quantityView = findViewById(R.id.quantity_view);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == 222) {
            hasRefresh = true;
            dataBean = (AddressBean.DataBean) data.getSerializableExtra("dataBean");
            initData();
        } else if (requestCode == 111 && resultCode == 333) {
//            hasRefresh = true;
            initAddress();
        }
    }

    private void initListener() {
        // TODO Auto-generated method stub

        mAliPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_Type = 0;
                cb_alipay.setChecked(true);
                cb_wx.setChecked(false);
            }
        });

        mWxPay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_Type = 1;
                cb_alipay.setChecked(false);
                cb_wx.setChecked(true);
            }
        });

        btn_pay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (choose_Type == 0)
                    pay();
                else if (choose_Type == 1)
                    wxPay();
            }
        });

        quantityView.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int newQuantity, boolean programmatically) {
                quantity = newQuantity;
                initOrder(quantity+"");
            }

            @Override
            public void onLimitReached() {

            }
        });
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "获取订单中...", null);
        btn_pay.setEnabled(false);
        RequestParams params = new RequestParams();
        params.setUri(Preferences.ALIPAY);
        params.addQueryStringParameter("attachCode", orderCode);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "attachone---------=" + s);
                Gson gson = new Gson();
                Type type = new TypeToken<AliBean>() {
                }.getType();
                final AliBean bean = gson.fromJson(s, type);

                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayChooseActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(bean.getData(), true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                }
                btn_pay.setEnabled(true);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(PayChooseActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onFinished() {
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }
        });


    }

    public void btnBack(View v) {
        finish();
    }


    /***
     * 微信支付
     */
    private IWXAPI api;

    private void wxPay() {

        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "获取订单中...", null);

        final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

        // 将该app注册到微信
        msgApi.registerApp(Preferences.WX_APP_ID);
        api = WXAPIFactory.createWXAPI(this, Preferences.WX_APP_ID);

        btn_pay.setEnabled(false);


        RequestParams params = new RequestParams();
        params.setUri(Preferences.WXPAY);
        params.addQueryStringParameter("attachCode", orderCode);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "order----wx---=" + s);
                Gson gson = new Gson();
                Type type = new TypeToken<WXBean>() {
                }.getType();
                final WXBean bean = gson.fromJson(s, type);

                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                    PayReq req = new PayReq();
                    req.appId = bean.getData().getAppid();
                    req.partnerId = bean.getData().getPartnerid();//商户号id
                    req.prepayId = bean.getData().getPrepayid();
                    req.nonceStr = bean.getData().getNoncestr();
                    req.timeStamp = bean.getData().getTimestamp();
                    req.packageValue = "Sign=WXPay";
                    req.sign = bean.getData().getSign();
                    req.extData = "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                    isWXpay = true;
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(PayChooseActivity.this, "加载失败," + bean.getCodeInfo());
                }
                btn_pay.setEnabled(true);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(PayChooseActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }

            @Override
            public void onFinished() {
                dismissMyDialog();
                btn_pay.setEnabled(true);
            }
        });
    }
}
