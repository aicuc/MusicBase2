package com.musicbase.entity;

public class PostageBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"postage":"5.50"}
     */

    private String code;
    private String codeInfo;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * postage : 5.50
         */

        private String postage;

        public String getPostage() {
            return postage;
        }

        public void setPostage(String postage) {
            this.postage = postage;
        }
    }
}
