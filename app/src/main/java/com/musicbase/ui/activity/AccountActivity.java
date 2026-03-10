package com.musicbase.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.music.base.wxapi.WXLoginUtils;
import com.musicbase.R;
import com.musicbase.entity.BindListBean;
import com.musicbase.implement.OnWXLogin;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.Constant;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.musicbase.util.ToastUtil;
import com.orhanobut.logger.Logger;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.List;

import static com.musicbase.ui.activity.LoginActivity.mWxApi;
import static com.tencent.connect.common.Constants.PACKAGE_QQ;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private TextView text_now_use;
    private TextView safe_setting;
    private TextView phone_number;
    private TextView change_phone;
    private TextView password_text;
    private TextView change_pass;
    private TextView band_setting;
    private TextView wechat_text;
    private TextView wechat_bind;
    private TextView qq_text;
    private TextView qq_bind;
    private TextView weibo_text;
    private TextView weibo_bind;
    private boolean isWXBind = false;
    private String wxOpenid = "";
    private boolean isQQBind = false;
    private String qqOpenid = "";
    private boolean isSinaBind = false;
    private String sinaOpenid = "";
    //初始化腾讯服务
    private Tencent mTencent;
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);
        initView();
        initSDK();
        initData();
    }

    private void initSDK() {
        mTencent = Tencent.createInstance(Preferences.QQ_APP_ID, this);
        mAuthInfo = new AuthInfo(this, Constant.APP_KEY, Constant.REDIRECT_URL, Constant.SCOPE);
        WbSdk.install(this, mAuthInfo);
        mSsoHandler = new SsoHandler(AccountActivity.this);
    }

    //微博回调
    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            AccountActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
//                    if (mAccessToken.isSessionValid()) {
                    // 显示 Token
//                        updateTokenView(false);
                    // 保存 Token 到 SharedPreferences
                    AccessTokenKeeper.writeAccessToken(AccountActivity.this, mAccessToken);
//                    Toast.makeText(LoginActivity.this,
//                            "成功登录 uid:" + mAccessToken.getUid(), Toast.LENGTH_SHORT).show();

                    bind("sina", mAccessToken.getUid());

//                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(AccountActivity.this,
                    "取消登录", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(AccountActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Preferences.REGIST_CODE && data != null) {
//            login(data.getStringExtra("identity_type"), data.getStringExtra("username"), data.getStringExtra("userPwd"));
//        }

        //腾讯QQ回调
        Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, listener);
            }


        }


        //sina login
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //吊起新浪微博客户端授权，如果未安装这使用web授权
    private void loginToSina() {
        //授权方式有三种，第一种对客户端授权 第二种对Web短授权，第三种结合前两中方式
        mSsoHandler.authorize(new AccountActivity.SelfWbAuthListener());

    }

    /**
     * QQ登录
     */
    private IUiListener listener;

    private void loginQQ() {
        listener = new IUiListener() {
            @Override
            public void onComplete(Object object) {

                Logger.e("登录成功: " + object.toString());

                JSONObject jsonObject = (JSONObject) object;
                try {
                    //得到token、expires、openId等参数
                    String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);

                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
//                    Log.e(TAG, "token: " + token);
//                    Log.e(TAG, "expires: " + expires);
//                    Log.e(TAG, "openId: " + openId);
//
//                    //获取个人信息
//                    getQQInfo();
                    bind("qq", openId);
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(UiError uiError) {
                //登录失败
                Logger.e("登录失败" + uiError.errorDetail);
                Logger.e("登录失败" + uiError.errorMessage);
                Logger.e("登录失败" + uiError.errorCode + "");

            }

            @Override
            public void onCancel() {
                //登录取消
                Logger.e("登录取消");

            }

            @Override
            public void onWarning(int i) {

            }
        };
        //context上下文、第二个参数SCOPO 是一个String类型的字符串，表示一些权限
        //应用需要获得权限，由“,”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
        //第三个参数事件监听器
        mTencent.login(this, "all", listener);
        //注销登录
        //mTencent.logout(this);
    }


    /**
     * true 安装了相应包名的app
     */
    private boolean hasApp(Context context, String packName) {
        boolean is = false;
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            String packageName = packageInfo.packageName;
            if (packageName.equals(packName)) {
                is = true;
            }
        }
        return is;
    }


    private void initData() {
        titlelayout_title.setText("账号绑定");
        String userName = SPUtility.getSPString(this, "username");
        String userPwd = SPUtility.getSPString(this, "password");
        String identity_type = SPUtility.getSPString(this, "identity_type");
        String now_type = "";
        switch (identity_type) {
            case "telphone":
                now_type = "手机" + userName.substring(0, 3) + "****" + userName.substring(7, userName.length());
                break;
            case "qq":
                now_type = "QQ";
                break;
            case "weixin":
                now_type = "微信";
                break;
            case "sina":
                now_type = "微博";
                break;
        }
        text_now_use.setText("当前登录账号：" + now_type);
        DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.GET_BIND_METHOD_URL).tag(this)
                .params("userId", SPUtility.getUserId(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Logger.d(response);
                        Gson gson = new Gson();
                        BindListBean bean = gson.fromJson(response.body(), BindListBean.class);
                        DialogUtils.dismissMyDialog();
                        if (bean != null && bean.getCode().equals("SUCCESS")) {
                            BindListBean.DataBean data = bean.getData();
                            List<BindListBean.DataBean.UserauthsListBean> userauthsList = data.getUserauthsList();
                            for (BindListBean.DataBean.UserauthsListBean userauthsListBean : userauthsList) {
                                switch (userauthsListBean.getIdentity_type()) {
                                    case "weixin":
                                        wechat_bind.setText("解除绑定    ");
                                        isWXBind = true;
                                        wxOpenid = userauthsListBean.getIdentifier();
                                        break;
                                    case "qq":
                                        qq_bind.setText("解除绑定    ");
                                        isQQBind = true;
                                        qqOpenid = userauthsListBean.getIdentifier();
                                        break;
                                    case "sina":
                                        weibo_bind.setText("解除绑定    ");
                                        isSinaBind = true;
                                        sinaOpenid = userauthsListBean.getIdentifier();
                                        break;

                                }
                            }


                        } else if (bean != null && bean.getCode().equals("FAIL")) {
                            if (!TextUtils.isEmpty(bean.getCodeInfo()) && !AccountActivity.this.isFinishing()) {// 获取数据出现异常
//                                if (bean.getCodeInfo().contains("解绑")) {
//                                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_JIESUO_DIALOG, "", "", new View.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            // TODO Auto-generated method stub
//                                            DialogUtils.dismissMyDialog();
//                                            showYanzhengDialog();
//                                        }
//
//                                    });
//                                } else {
                                DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_ERROR_DIALOG, "绑定失败", bean.getCodeInfo(), null);
//                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        DialogUtils.dismissMyDialog();
                        super.onError(response);
                    }
                });
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);

        text_now_use = (TextView) findViewById(R.id.text_now_use);
        safe_setting = (TextView) findViewById(R.id.safe_setting);

        phone_number = (TextView) findViewById(R.id.phone_number);
        change_phone = (TextView) findViewById(R.id.change_phone);
        change_phone.setOnClickListener(this);

        password_text = (TextView) findViewById(R.id.password_text);
        change_pass = (TextView) findViewById(R.id.change_pass);
        change_pass.setOnClickListener(this);

        band_setting = (TextView) findViewById(R.id.band_setting);

        wechat_text = (TextView) findViewById(R.id.wechat_text);
        wechat_bind = (TextView) findViewById(R.id.wechat_bind);
        wechat_bind.setOnClickListener(this);

        qq_text = (TextView) findViewById(R.id.qq_text);
        qq_bind = (TextView) findViewById(R.id.qq_bind);
        qq_bind.setOnClickListener(this);

        weibo_text = (TextView) findViewById(R.id.weibo_text);
        weibo_bind = (TextView) findViewById(R.id.weibo_bind);
        weibo_bind.setOnClickListener(this);


        titlelayout_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:
                finish();
                break;
            case R.id.change_phone:
                ToastUtil.showShort(this, "此功能暂未开通！");
                break;
            case R.id.change_pass:
                ToastUtil.showShort(this, "此功能暂未开通！");
                break;
            case R.id.wechat_bind:
                if (isWXBind) {

                    unbind("weixin", wxOpenid);
                } else {
                    if (!mWxApi.isWXAppInstalled()) {
                        ToastUtil.showShort(this, "您还未安装微信客户端");
                        return;
                    }
                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在使用微信登录...", null);
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "diandi_wx_login";
                    //像微信发送请求
                    mWxApi.sendReq(req);
                    WXLoginUtils.setOnWXLogin(new OnWXLogin() {
                        @Override
                        public void onSuccess(String openid) {
//                            finish();
                            bind("weixin", openid);
                        }

                        @Override
                        public void onFailed() {
//                            finish();
                            ToastUtil.showShort(AccountActivity.this, "登录失败，请重试！");
                        }
                    });
                }

                break;
            case R.id.qq_bind:

                if (isQQBind) {
                    unbind("qq", qqOpenid);
                } else {

                    //注意：此段非必要，如果手机未安装应用则会跳转网页进行授权
                    if (!hasApp(AccountActivity.this, PACKAGE_QQ)) {
                        ToastUtil.showShort(AccountActivity.this, "您还未安装QQ客户端！");
                        return;
                    }
                    //如果session无效，就开始做登录操作
//                if (!mTencent.isSessionValid()) {
                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在使用QQ登录...", null);
                    loginQQ();
//                }

                }
                break;
            case R.id.weibo_bind:
                if (isSinaBind) {
                    unbind("sina", sinaOpenid);
                } else {

//                mSsoHandler = new SsoHandler(WBAuthActivity.this);
//                mSsoHandler.authorizeClientSso(new WbAuthListener());
                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在使用微博登录...", null);
                    loginToSina();
                }

                break;
        }
    }

    private void unbind(final String method, final String openid) {
        DialogUtils.showMyDialog(this, Preferences.SHOW_CONFIRM_DIALOG, "解除绑定", "确定解除绑定吗？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.<String>post(Preferences.REMOVE_BIND_URL).tag(this)
                        .params("userId", SPUtility.getUserId(AccountActivity.this))
                        .params("identity_type", method)
                        .params("identifier", openid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Logger.d(response);
                                Gson gson = new Gson();
                                BindListBean bean = gson.fromJson(response.body(), BindListBean.class);
                                DialogUtils.dismissMyDialog();
                                if (bean != null && bean.getCode().equals("SUCCESS")) {
                                    ToastUtil.showShort(AccountActivity.this, bean.getCodeInfo());
                                    switch (method) {
                                        case "weixin":
                                            wechat_bind.setText("绑定    ");
                                            isWXBind = false;
                                            wxOpenid = "";
                                            break;
                                        case "qq":
                                            qq_bind.setText("绑定    ");
                                            isQQBind = false;
                                            qqOpenid = "";
                                            break;
                                        case "sina":
                                            weibo_bind.setText("绑定    ");
                                            isSinaBind = false;
                                            sinaOpenid = "";
                                            break;

                                    }
//                            BindListBean.DataBean data = bean.getData();
//                            List<BindListBean.DataBean.UserauthsListBean> userauthsList = data.getUserauthsList();
//                            for (BindListBean.DataBean.UserauthsListBean userauthsListBean : userauthsList) {
//                                switch (userauthsListBean.getIdentity_type()) {
//                                    case "weixin":
//                                        wechat_bind.setText("解除绑定    ");
//                                        isWXBind = true;
//                                        wxOpenid = userauthsListBean.getIdentifier();
//                                        break;
//                                    case "qq":
//                                        qq_bind.setText("解除绑定    ");
//                                        isQQBind = true;
//                                        qqOpenid = userauthsListBean.getIdentifier();
//                                        break;
//                                    case "sina":
//                                        weibo_bind.setText("解除绑定    ");
//                                        isSinaBind = true;
//                                        sinaOpenid = userauthsListBean.getIdentifier();
//                                        break;
//
//                                }
//                            }


                                } else if (bean != null && bean.getCode().equals("FAIL")) {
                                    if (!TextUtils.isEmpty(bean.getCodeInfo()) && !AccountActivity.this.isFinishing()) {// 获取数据出现异常
//                                if (bean.getCodeInfo().contains("解绑")) {
//                                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_JIESUO_DIALOG, "", "", new View.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            // TODO Auto-generated method stub
//                                            DialogUtils.dismissMyDialog();
//                                            showYanzhengDialog();
//                                        }
//
//                                    });
//                                } else {
                                        DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_ERROR_DIALOG, "解绑失败", bean.getCodeInfo(), null);
//                                }
                                    }
                                }

                            }

                            @Override
                            public void onError(Response<String> response) {
                                DialogUtils.dismissMyDialog();
                                super.onError(response);
                            }
                        });
            }
        });


    }

    private void bind(String method, String openid) {
        OkGo.<String>post(Preferences.ADD_BIND_URL).tag(this)
                .params("userId", SPUtility.getUserId(this))
                .params("identity_type", method)
                .params("identifier", openid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Logger.d(response);
                        Gson gson = new Gson();
                        BindListBean bean = gson.fromJson(response.body(), BindListBean.class);
                        DialogUtils.dismissMyDialog();
                        if (bean != null && bean.getCode().equals("SUCCESS")) {
                            ToastUtil.showShort(AccountActivity.this, "绑定成功！");
                            BindListBean.DataBean data = bean.getData();
                            List<BindListBean.DataBean.UserauthsListBean> userauthsList = data.getUserauthsList();
                            for (BindListBean.DataBean.UserauthsListBean userauthsListBean : userauthsList) {
                                switch (userauthsListBean.getIdentity_type()) {
                                    case "weixin":
                                        wechat_bind.setText("解除绑定    ");
                                        isWXBind = true;
                                        wxOpenid = userauthsListBean.getIdentifier();
                                        break;
                                    case "qq":
                                        qq_bind.setText("解除绑定    ");
                                        isQQBind = true;
                                        qqOpenid = userauthsListBean.getIdentifier();
                                        break;
                                    case "sina":
                                        weibo_bind.setText("解除绑定    ");
                                        isSinaBind = true;
                                        sinaOpenid = userauthsListBean.getIdentifier();
                                        break;

                                }
                            }


                        } else if (bean != null && bean.getCode().equals("FAIL")) {
                            if (!TextUtils.isEmpty(bean.getCodeInfo()) && !AccountActivity.this.isFinishing()) {// 获取数据出现异常
//                                if (bean.getCodeInfo().contains("解绑")) {
//                                    DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_JIESUO_DIALOG, "", "", new View.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            // TODO Auto-generated method stub
//                                            DialogUtils.dismissMyDialog();
//                                            showYanzhengDialog();
//                                        }
//
//                                    });
//                                } else {
                                DialogUtils.showMyDialog(AccountActivity.this, Preferences.SHOW_ERROR_DIALOG, "绑定失败", bean.getCodeInfo(), null);
//                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        DialogUtils.dismissMyDialog();
                        super.onError(response);
                    }
                });

    }
}
