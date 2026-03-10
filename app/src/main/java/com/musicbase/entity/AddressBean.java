package com.musicbase.entity;

import java.io.Serializable;
import java.util.List;

public class AddressBean {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"id":"16","userId":"4","userName":"巫志英","telphone":"18010480093","areaCode":"110115","address":"北京市大兴区西红门镇","isDefault":"1","areaName":"中国北京北京市大兴区"}]
     */

    private String code;
    private String codeInfo;
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

    public static class DataBean implements Serializable {
        /**
         * id : 16
         * userId : 4
         * userName : 巫志英
         * telphone : 18010480093
         * areaCode : 110115
         * address : 北京市大兴区西红门镇
         * isDefault : 1
         * areaName : 中国北京北京市大兴区
         */

        private String id;
        private String userId;
        private String userName;
        private String telphone;
        private String areaCode;
        private String address;
        private String isDefault;
        private String areaName;
        private String isNeedPwd;
        private String mess;

        @Override
        public String toString() {
            return "DataBean{" +
                    "id='" + id + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", telphone='" + telphone + '\'' +
                    ", areaCode='" + areaCode + '\'' +
                    ", address='" + address + '\'' +
                    ", isDefault='" + isDefault + '\'' +
                    ", areaName='" + areaName + '\'' +
                    '}';
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getTelphone() {
            return telphone;
        }

        public void setTelphone(String telphone) {
            this.telphone = telphone;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }
    }
}
