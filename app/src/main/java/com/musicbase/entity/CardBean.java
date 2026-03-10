package com.musicbase.entity;

import java.io.Serializable;
import java.util.List;

public class CardBean {




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

    public static class DataBean implements Serializable {

        private String isNeedPwd;
        private String mess;


        public String getMess() {
            return mess;
        }

        public void setMess(String mess) {
            this.mess = mess;
        }

        public String getIsNeedPwd() {
            return isNeedPwd;
        }

        public void setIsNeedPwd(String isNeedPwd) {
            this.isNeedPwd = isNeedPwd;
        }

    }
}
