package com.musicbase.entity;

public class WXLoginBean {

    /**
     * access_token : 22_AKK_D77q7cxGyXh0yXjmWrrNE5d81kGhlEHgk9QhbIIv3AnEFkUtJ34o_tA8mA5jW1JyR3dnh7tBinNV9-8Ab13-WpUZhTfv3rbbjEivXGI
     * expires_in : 7200
     * refresh_token : 22_ZVRfCzaZ4TAXwAhU3v44lWtVUQn_fz7J2a_mx4W9XxfG6oXRxLMDEmGEC3Sk8FRszvrRwuFuoYpbwOc5iOLeiiz1qbgPEFU9YUNoUj-p7dY
     * openid : oNrPR0o5C29j2g4PV5KJAGB0WZx8
     * scope : snsapi_userinfo
     * unionid : ojJ4Iw65r_C3NsGVBrekfC-Fq7bI
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
