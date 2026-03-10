package com.musicbase.entity;

/**
 * Created by BAO on 2018-10-31.
 */

public class OrderBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"attachCode":"20178221503387671767"}
     */

    private String code;
    private String codeInfo;
    private Data data;

    public void setCode(String code) {
        this.code = code;
    }

    public void setCodeInfo(String codeInfo) {
        this.codeInfo = codeInfo;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getCodeInfo() {
        return codeInfo;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        /**
         * attachCode : 20178221503387671767
         */

        private String attachCode;

        public void setAttachCode(String attachCode) {
            this.attachCode = attachCode;
        }

        public String getAttachCode() {
            return attachCode;
        }
    }
}
