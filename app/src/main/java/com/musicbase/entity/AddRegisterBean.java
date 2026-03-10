package com.musicbase.entity;

public class AddRegisterBean {

    /**
     * code : SUCCESS
     * codeInfo : 极光RegistrationID绑定成功!
     * data : {"registrationId":"gtrddasghas1as56fd4a56"}
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
         * registrationId : gtrddasghas1as56fd4a56
         */

        private String registrationId;

        public String getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
        }
    }
}
