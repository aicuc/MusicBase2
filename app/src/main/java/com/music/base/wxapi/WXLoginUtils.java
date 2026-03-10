package com.music.base.wxapi;

import com.google.gson.Gson;
import com.musicbase.entity.WXLoginBean;
import com.musicbase.entity.WXUserInfo;
import com.musicbase.implement.OnWXLogin;
import com.musicbase.preferences.Preferences;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class WXLoginUtils {
    private static OnWXLogin onWXLogin;

    public static void setOnWXLogin(OnWXLogin onWXLogin) {
        WXLoginUtils.onWXLogin = onWXLogin;
    }

    public static void getWXLoginResult(String code, WXEntryActivity wxEntryActivity) {
//        DialogUtils.dismissMyDialog();
        RequestParams params = new RequestParams(Preferences.WEIXIN_TOKEN);
        params.addQueryStringParameter("appid", Preferences.WX_APP_ID);
        params.addQueryStringParameter("secret", Preferences.WX_APP_SECRET);
        params.addQueryStringParameter("code", code);
        params.addQueryStringParameter("grant_type", "authorization_code");
//        DialogUtils.showMyDialog(MyApplication.getInstance(),);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Logger.d(s);
                Gson gson = new Gson();
                WXLoginBean wxLoginBean = gson.fromJson(s, WXLoginBean.class);
//                getWXUserInfo(wxLoginBean.getAccess_token(),wxLoginBean.getOpenid(),wxLoginBean.getUnionid());
//                DialogUtils.dismissMyDialog();
                onWXLogin.onSuccess(wxLoginBean.getUnionid());
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                DialogUtils.dismissMyDialog();
                onWXLogin.onFailed();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private static void getWXUserInfo(final String access_token, final String openid, final String unionid) {
        RequestParams params = new RequestParams(Preferences.WEIXIN_USERINFO);
        params.addQueryStringParameter("access_token", access_token);
        params.addQueryStringParameter("openid", openid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

//                Logger.d(s);
                Gson gson = new Gson();
                WXUserInfo wxUserInfo = gson.fromJson(s, WXUserInfo.class);
                Logger.d(wxUserInfo.toString());
//                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
//                DialogUtils.dismissMyDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

}

