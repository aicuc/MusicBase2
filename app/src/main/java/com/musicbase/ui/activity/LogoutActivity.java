package com.musicbase.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.entity.LogoutProcessBean;
import com.musicbase.implement.LogoutCallBack;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.Logout2Fragment;
import com.musicbase.ui.fragment.LogoutFragment;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.musicbase.preferences.Preferences.phoneMatcher;


public class LogoutActivity extends BaseActivity implements LogoutCallBack {
    private LogoutProcessBean bean;
    LogoutFragment fragment;
    Logout2Fragment logout2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        SMSSDK.registerEventHandler(eh); //注册短信回调
        initTitle();
        initData();
    }

    private void initData() {
        DialogUtils.showMyDialog(LogoutActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        OkGo.<String>post(Preferences.LOGOUT_PROCESS).tag(this).params("userId", SPUtility.getUserId(this) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response.body());
                Gson gson = new Gson();
                Type type = new TypeToken<LogoutProcessBean>() {
                }.getType();
                bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    switch (bean.getData().getCancel_process()) {
                        case "not_started":
                            setLogout_Start();
                            break;
                        case "in_progress":
                            setLogout_Process(bean.getData().getCancel_info().getCheckState());
                            break;
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(LogoutActivity.this, "请检查网络。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    private void initTitle() {

        ((TextView) findViewById(R.id.titlelayout_title)).setText("申请注销");
        ((ImageButton) findViewById(R.id.titlelayout_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 加载申请注销界面
     */
    private void setLogout_Start() {
        logout2Fragment = null;
        fragment = LogoutFragment.newInstance();
        fragment.setCallBack(this);
        replace(fragment);
    }

    private void setLogout_Process(String logout_state) {
        fragment = null;
        if (bean.getData() == null || bean.getData().getCancel_info() == null)
            logout2Fragment = Logout2Fragment.newInstance(logout_state, null, false);
        else
            logout2Fragment = Logout2Fragment.newInstance(logout_state, bean.getData().getCancel_info().getCheckReply(), bean.getData().getCancel_info().getAllowCancel() == 1 ? true : false);
        logout2Fragment.setCallBack(this);
        replace(logout2Fragment);
    }

    private void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (time != null) {
            time.cancel();
            time = null;
        }
        SMSSDK.unregisterEventHandler(eh);
    }

    Button btn_send;
    String verifCode;

    public void showYanzhengDialog() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_login_jiesuorenzheng);
        final TextView textView = (TextView) dialog.findViewById(R.id.dialog_titile);
        textView.setText(SPUtility.getSPString(this, "username"));
        final EditText editText = (EditText) dialog.findViewById(R.id.editText1);
        btn_send = (Button) dialog.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                btn_send(textView.getText().toString().trim());
            }
        });
        Button button07 = (Button) dialog.findViewById(R.id.btn_right);
        ImageButton button = (ImageButton) dialog.findViewById(R.id.imageButton1);
        button07.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verifCode = editText.getText().toString().trim();
                if (fragment != null) {
                    fragment.requireLogout(verifCode);
                } else if (logout2Fragment != null) {
                    logout2Fragment.confirmLogout(verifCode);
                }
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void btn_send(String phone) {
        if (TextUtils.isEmpty(phone) && !phone.matches(phoneMatcher)) {
            return;
        }
        if (!TextUtils.isEmpty(phone) && phone.matches(phoneMatcher)) {
            SMSSDK.getVerificationCode("86", phone);
            DialogUtils.dismissMyDialog();
        }
    }

    TimeCount time = new TimeCount(60000, 1000);

    @Override
    public void onResult(String logout_result) {
        switch (logout_result) {
            case LOGOUT_ING:
            case LOGOUT_FAIL:
            case LOGOUT_SUCCESS:
                setLogout_Process(logout_result);
                break;
            case LOGOUT_CANCEL:
                finish();
                break;
            case LOGOUT_START:
                setLogout_Start();
                break;
            case LOGOUT_OVER:
                setResult(0x01);
                finish();
                break;
        }
    }

    @Override
    public void sendSMS() {
        showYanzhengDialog();
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            if (btn_send != null) {
                btn_send.setText("重新验证");
                btn_send.setTextColor(getResources().getColor(R.color.red_e61b19));
                btn_send.setClickable(true);
                int regist_sms_button1 = getResources().getIdentifier("regist_sms_button1", "drawable", getPackageName());
                btn_send.setBackgroundResource(regist_sms_button1);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            if (btn_send != null) {
                int regist_sms_button2 = getResources().getIdentifier("regist_sms_button2", "drawable", getPackageName());
                btn_send.setBackgroundResource(regist_sms_button2);
                btn_send.setClickable(false);
                btn_send.setTextColor(getResources().getColor(R.color.login_hint_color));
                btn_send.setText("(" + millisUntilFinished / 1000 + "秒)");
            }
        }
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    ActivityUtils.showToast(LogoutActivity.this, "正在发送中...");
                    time.start();
                } else if (msg.arg1 == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                ((Throwable) msg.obj).printStackTrace();
                ActivityUtils.processError(LogoutActivity.this, msg.obj);
            }
        }
    };
}