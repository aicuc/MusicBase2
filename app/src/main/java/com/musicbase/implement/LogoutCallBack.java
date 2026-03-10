package com.musicbase.implement;

public interface LogoutCallBack {
    public final String LOGOUT_ING = "0";//正在审核
    public final String LOGOUT_SUCCESS = "1";//审核通过
    public final String LOGOUT_FAIL = "2";//审核失败
    public final String LOGOUT_CANCEL = "3";//取消注销
    public final String LOGOUT_START = "4";//可申请注销
    public final String LOGOUT_OVER = "5";//注销完成
    void onResult(String logout_result);
    void sendSMS();
}
