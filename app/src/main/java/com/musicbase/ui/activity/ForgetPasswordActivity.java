package com.musicbase.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.entity.Bean;
import com.musicbase.entity.SmsBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.musicbase.preferences.Preferences.phoneMatcher;


/**
 * 重置密码
 *
 * @author BAO
 */
public class ForgetPasswordActivity extends BaseActivity {

    private EditText et_phone;
    private EditText et_code;
    private EditText et_password;
    private EditText et_confirm;
    TimeCount time = new TimeCount(60000, 1000);
    private String phone;
    private String confirm;
    private String verifCode;
    Button btn_send;
    private String telPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        initView();
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private String code;

    public void initView() {
        initTitle();
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_password = (EditText) findViewById(R.id.et_password01);
        et_confirm = (EditText) findViewById(R.id.et_password02);
        btn_send = (Button) findViewById(R.id.btn_send);
        telPhone = getIntent().getStringExtra("telPhone");
        et_phone.setText(telPhone);
    }

    private void initTitle() {
        ((TextView) findViewById(R.id.titlelayout_title)).setText("忘记密码");
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void btn_send(View v) {
        phone = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
            SMSSDK.getVerificationCode("86", phone);
            DialogUtils.dismissMyDialog();
        }
    }

    public void Confirm(View v) {
        code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !phone.matches(phoneMatcher)) {
            ActivityUtils.showToast(this, "手机号码为空或格式不正确");
            return;
        }
        String password = et_password.getText().toString().trim();
        if (!TextUtils.isEmpty(password) && password.length() >= 6 || password.length() <= 16) {
            et_password.setText(password);

        } else if (password.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(password)) {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "请输入密码！");
            return;
        } else {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "密码请输入6-16位!");
            return;
        }
        confirm = et_confirm.getText().toString().trim();
        if (!TextUtils.isEmpty(confirm) && confirm.length() >= 6 || confirm.length() <= 16) {
            et_confirm.setText(password);
        } else if (confirm.matches("[\u4E00-\u9FA5a]+")) {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "请不要输入中文!");
            return;
        } else if (TextUtils.isEmpty(confirm)) {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "请输入确认密码！");
            return;
        } else {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "密码请输入6-16位!");
            return;
        }
        if (!password.equals(confirm)) {
            ActivityUtils.showToast(ForgetPasswordActivity.this, "密码与确认密码不一致，请重新输入");
            return;
        }
        if (!ActivityUtils.isNetworkAvailable(this)) {
            ActivityUtils.showToast(this, "无法连接网络");
            return;
        }
        verifCode = code;
        if (code.equals(verifCode)) {
            reset();
        } else {
            ActivityUtils.showToast(this, "验证码错误");
        }
    }

    public void reset() {
        RequestParams params = new RequestParams(Preferences.RESET_PASSWORD);
        params.addQueryStringParameter("identifier", phone);
        params.addQueryStringParameter("user.userPwd", confirm);
        params.addQueryStringParameter("captcha", verifCode);

        DialogUtils.showMyDialog(ForgetPasswordActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在发送中...", null);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);
                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    ActivityUtils.showToast(ForgetPasswordActivity.this, "密码重置成功！");
                    finish();
                } else {
                    DialogUtils.showMyDialog(ForgetPasswordActivity.this, Preferences.SHOW_ERROR_DIALOG, "密码重置失败", bean.getCodeInfo(), null);
                }

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(ForgetPasswordActivity.this, "密码重置失败,请检查网络。");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

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
                //回调完成
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    //                    reginst();
                } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    ActivityUtils.showToast(ForgetPasswordActivity.this, "正在发送中...");
                    time.start();
                } else if (msg.arg1 == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                ((Throwable) msg.obj).printStackTrace();
                ActivityUtils.processError(ForgetPasswordActivity.this,msg.obj);
            }
        }
    };
}
