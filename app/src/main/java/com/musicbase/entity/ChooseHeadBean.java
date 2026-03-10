package com.musicbase.entity;

public class ChooseHeadBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"userAvatar":"user_avatar/upload/2023/12/8e20761c-4d21-4fa7-b651-5c2214cd5275.png"}
     */

    private String code;
    private String codeInfo;
    /**
     * userAvatar : user_avatar/upload/2023/12/8e20761c-4d21-4fa7-b651-5c2214cd5275.png
     */

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
        private String userAvatar;

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }
    }
}
