package com.musicbase.implement;

public interface OnWXLogin {
    void onSuccess(String openid);

    void onFailed();

}
