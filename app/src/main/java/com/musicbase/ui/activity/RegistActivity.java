package com.musicbase.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.entity.LoginBean;
import com.musicbase.entity.SmsBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.UUID;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.musicbase.preferences.Preferences.BIND_PHONE_CODE;
import static com.musicbase.preferences.Preferences.CONTRACT_USER;
import static com.musicbase.preferences.Preferences.REGIST_CODE;
import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.preferences.Preferences.phoneMatcher;


public class RegistActivity extends BaseActivity implements OnClickListener {
    EditText et_code;
    LinearLayout ll_code;
    EditText et_phone;
    private LinearLayout have_account;
    LinearLayout ll_phone;
    EditText et_password01;
    EditText et_password02;
    Button btn_send;
    String verifCode;
    private Button btn_regist;
    private int type;
    private String identity_type;
    private String identifier;
    private String imei;
    private TextView user_agreement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
        SMSSDK.registerEventHandler(eh);
    }

    /**
     * 初始化修改密码界面
     */
    @SuppressLint("MissingPermission")
    public void initView() {
        type = getIntent().getIntExtra("type", REGIST_CODE);
        initTitle();
        btn_send = (Button) findViewById(R.id.btn_send);
        et_code = (EditText) findViewById(R.id.et_code);
        ll_code = (LinearLayout) findViewById(R.id.ll_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        ll_phone = (LinearLayout) findViewById(R.id.ll_phone);
        et_password01 = (EditText) findViewById(R.id.et_password01);
        et_password02 = (EditText) findViewById(R.id.et_password02);
        btn_regist = (Button) findViewById(R.id.btn_regist);
        have_account = (LinearLayout) findViewById(R.id.have_accout);
        if (type == BIND_PHONE_CODE) {
            identity_type = getIntent().getStringExtra("identity_type");
            identifier = getIntent().getStringExtra("identifier");
            btn_regist.setText("绑定");
            have_account.setVisibility(View.GONE);
        }
//        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
//            imei = telephonyManager.getDeviceId();
//        else
//            imei = getDeviceId();
        if(TextUtils.isEmpty(SPUtility.getSPString(this,"phoneId"))){
            String uuid = UUID.randomUUID().toString().replaceAll("-","");
            SPUtility.putSPString(this,"phoneId",uuid);
            imei = uuid;
        }else
            imei = SPUtility.getSPString(this,"phoneId");
        user_agreement = (TextView) findViewById(R.id.user_agreement);
        user_agreement.setOnClickListener(this);
    }

    /**
     * 获取设备唯一标识符(Android 10)
     *
     * @return 唯一标识符
     */
    public String getDeviceUniqueId() {
        // 通过 SharedPreferences 获取 GUID

        return Settings.System.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void initTitle() {

        ((TextView) findViewById(R.id.titlelayout_title)).setText(type == BIND_PHONE_CODE ? "手机绑定" : "用户注册");
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void btnConfirm(View v) {
        String phone = et_phone.getText().toString().trim();
        verifCode = et_code.getText().toString().trim();
        Logger.d(phone + "---" + verifCode);
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
            et_phone.setText(phone);
        } else if (phone.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(phone)) {
            ActivityUtils.showToast(getApplication(), "请输入手机号码！");
            return;
        } else {
            ActivityUtils.showToast(getApplication(), "手机号码格式不正确！");
            return;
        }
        String password1 = et_password01.getText().toString().trim();
        if (!TextUtils.isEmpty(password1) && password1.length() >= 6 && password1.length() <= 16) {
            et_password01.setText(password1);
        } else if (password1.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password1)) {
            ActivityUtils.showToast(getApplication(), "请输入密码！");
            return;
        } else {
            ActivityUtils.showToast(RegistActivity.this, "密码请输入6-16位");
            return;
        }
        String password2 = et_password02.getText().toString().trim();
        if (!TextUtils.isEmpty(password1) && password2.length() >= 6 && password2.length() <= 16) {
            et_password01.setText(password1);
        } else if (password2.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(RegistActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password2)) {
            ActivityUtils.showToast(getApplication(), "请输入确认密码！");
            return;
        } else {
            ActivityUtils.showToast(RegistActivity.this, "密码请输入6-16位");
            return;
        }
        if (!password1.equals(password2)) {
            ActivityUtils.showToast(getApplication(), "密码与确认密码不一致，请重新输入");
            return;
        }
        if (!ActivityUtils.isNetworkAvailable(this)) {
            ActivityUtils.showToast(this, "无法连接网络");
            return;
        }
        userName = et_phone.getText().toString().trim();
        userPwd = et_password01.getText().toString().trim();
        //        if (code.equals(verifCode)) {
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
            Logger.d("phone=>>" + phone);

            if (type == BIND_PHONE_CODE)
                bindPhone();
            else
                reginst();
//                SMSSDK.submitVerificationCode("86",phone,verifCode);
        }
        //        } else {
        //            ActivityUtils.showToast(RegistActivity.this, "验证码错误");
        //        }

    }

    private void bindPhone() {
        DialogUtils.showMyDialog(RegistActivity.this, Preferences.SHOW_PROGRESS_DIALOG_NO, null, "正在绑定中...", null);

        OkGo.<String>post(Preferences.BIND_TEL_URL).tag(this).params("identity_type", identity_type).params("identifier", identifier).params("telPhone", et_phone.getText().toString().trim()).params("captcha", verifCode).params("user.userPwd", et_password01.getText().toString().trim()).params("user.phoneId", imei).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<LoginBean>() {
                }.getType();
                LoginBean bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    Logger.d(bean.getCodeInfo());
                    ActivityUtils.showToast(getApplication(), bean.getCodeInfo());
                    Intent intent = new Intent();
                    intent.putExtra("username", userName);
                    intent.putExtra("userPwd", userPwd);
                    intent.putExtra("identity_type", "telphone");
                    setResult(REGIST_CODE, intent);
                    finish();
                } else {
                    DialogUtils.dismissMyDialog();
                    ActivityUtils.showToast(RegistActivity.this, "绑定失败：" + bean.getCodeInfo());
                    DialogUtils.showMyDialog(RegistActivity.this, Preferences.SHOW_ERROR_DIALOG, "绑定失败", bean.getCodeInfo(), null);
                    btn_regist.setClickable(true);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(RegistActivity.this, "注册失败,请检查网络。");
                btn_regist.setClickable(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
                btn_regist.setClickable(true);
            }
        });

    }


    private void reginst() {
        RequestParams params = new RequestParams(Preferences.REGINST_URL_NEW);
        params.addQueryStringParameter("identifier", userName);
        params.addQueryStringParameter("user.userPwd", userPwd);
        params.addQueryStringParameter("user.phoneId", imei);
        params.addQueryStringParameter("captcha", verifCode);

        DialogUtils.showMyDialog(RegistActivity.this, Preferences.SHOW_PROGRESS_DIALOG_NO, null, "正在发送中...", null);
        btn_regist.setClickable(false);
        x.http().post(params, new CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.d(s);
                Gson gson = new Gson();
                Type type = new TypeToken<LoginBean>() {
                }.getType();
                LoginBean bean = gson.fromJson(s, type);

                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    Logger.d(bean.getCodeInfo());
                    ActivityUtils.showToast(getApplication(), bean.getCodeInfo());
                    Intent intent = new Intent();
                    intent.putExtra("username", userName);
                    intent.putExtra("userPwd", userPwd);
                    intent.putExtra("identity_type", "telphone");
                    setResult(REGIST_CODE, intent);
                    finish();
                } else {
                    DialogUtils.dismissMyDialog();
                    ActivityUtils.showToast(RegistActivity.this, "注册失败：" + bean.getCodeInfo());
                    btn_regist.setClickable(true);
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(RegistActivity.this, "注册失败,请检查网络。");
                btn_regist.setClickable(true);
            }

            @Override
            public void onCancelled(CancelledException e) {
                btn_regist.setClickable(true);
            }

            @Override
            public void onFinished() {
                DialogUtils.dismissMyDialog();
                btn_regist.setClickable(true);
            }
        });
    }

    TimeCount time = new TimeCount(60000, 1000);
    private String userName;
    private String userPwd;
    private String phone;

    public void btn_login(View v) {
        //		startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void btn_send(View v) {
        phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) && !phone.matches(phoneMatcher)) {
            return;
        }
        DialogUtils.showMyDialog(RegistActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "发送短信", "<center>您确认要给" + phone + "发送短信吗？</center>", new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ActivityUtils.isNetworkAvailable(RegistActivity.this)) {
                    ActivityUtils.showToast(RegistActivity.this, "无法连接网络，发送失败");
                    DialogUtils.dismissMyDialog();
                    return;
                }

                if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
                    SMSSDK.getVerificationCode("86", phone);
                    DialogUtils.dismissMyDialog();
//                    RequestParams params = new RequestParams(Preferences.SEND_SMS);
//                    params.addQueryStringParameter("phone", phone);
//
//                    ActivityUtils.showToast(RegistActivity.this, "正在发送中...");
//                    x.http().post(params, new CommonCallback<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<SmsBean>() {
//                            }.getType();
//                            SmsBean bean = gson.fromJson(s, type);
//
//                            if (bean != null && bean.getCode().equals("SUCCESS")) {
//                                //                                verifCode = bean.getData().getCheckCode();
//                            } else {
//                                ActivityUtils.showToast(RegistActivity.this, "验证码获取失败" + bean.getCodeInfo());
//                            }
//                            DialogUtils.dismissMyDialog();
//                            time.start();
//                        }
//
//                        @Override
//                        public void onError(Throwable throwable, boolean b) {
//                            DialogUtils.dismissMyDialog();
//                            ActivityUtils.showToast(RegistActivity.this, "验证码获取失败,请检查网络。");
//                        }
//
//                        @Override
//                        public void onCancelled(CancelledException e) {
//
//                        }
//
//                        @Override
//                        public void onFinished() {
//
//                        }
//                    });
                } else {
                    ActivityUtils.showToast(RegistActivity.this, "您输入的手机号格式不正确！");
                    DialogUtils.dismissMyDialog();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_agreement:

                Intent intent3 = new Intent(this, WorkWebActivity.class);
                intent3.putExtra("filePath", CONTRACT_USER);
                intent3.putExtra("name", "用户协议");
                intent3.putExtra("fileType", "html");
                startActivity(intent3);
                break;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btn_send.setText("重新验证");
            btn_send.setTextColor(getResources().getColor(R.color.red_e61b19));
            btn_send.setClickable(true);
            int regist_sms_button1 = getResources().getIdentifier("regist_sms_button1", "drawable", getPackageName());
            btn_send.setBackgroundResource(regist_sms_button1);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            int regist_sms_button2 = getResources().getIdentifier("regist_sms_button2", "drawable", getPackageName());
            btn_send.setBackgroundResource(regist_sms_button2);
            btn_send.setClickable(false);
            btn_send.setTextColor(getResources().getColor(R.color.login_hint_color));
            btn_send.setText("(" + millisUntilFinished / 1000 + "秒)");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(time!=null) {
            time.cancel();
            time = null;
        }
        SMSSDK.unregisterEventHandler(eh);
    }

    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    };

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == SMSSDK.RESULT_COMPLETE) {
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    ActivityUtils.showToast(RegistActivity.this, "正在发送中...");
                    time.start();
                } else if (msg.arg1 == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                }
            } else {
                ((Throwable) msg.obj).printStackTrace();
                ActivityUtils.processError(RegistActivity.this,msg.obj);
            }
        }
    };
}
