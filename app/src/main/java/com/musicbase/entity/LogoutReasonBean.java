package com.musicbase.entity;

import java.util.List;

public class LogoutReasonBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":"1","reason":"原因1111"},{"id":"2","reason":"原因2222"},{"id":"3","reason":"原因3333"},{"id":"4","reason":"原因4444"},{"id":"5","reason":"原因5555"},{"id":"6","reason":"其他"}]
     */

    private String code;
    private String codeInfo;
    /**
     * id : 1
     * reason : 原因1111
     */

    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String reason;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
