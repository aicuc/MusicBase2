package com.musicbase.entity;

import java.util.List;

public class LogoutProcessBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"cancel_process":"in_progress","cancel_info":{"userTel":"18646454011","cancelReason":["原因1111","原因2222","原因3333"],"cancelRemarks":"aaaaaaaaaaaaaaaa","addTime":"2021-01-13 17:17:07","checkState":"2","checkTime":"2021-01-13 18:10:39","checkReply":"申请理由不充分"}}
     */

    private String code;
    private String codeInfo;
    /**
     * cancel_process : in_progress
     * cancel_info : {"userTel":"18646454011","cancelReason":["原因1111","原因2222","原因3333"],"cancelRemarks":"aaaaaaaaaaaaaaaa","addTime":"2021-01-13 17:17:07","checkState":"2","checkTime":"2021-01-13 18:10:39","checkReply":"申请理由不充分"}
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
        private String cancel_process;
        /**
         * userTel : 18646454011
         * cancelReason : ["原因1111","原因2222","原因3333"]
         * cancelRemarks : aaaaaaaaaaaaaaaa
         * addTime : 2021-01-13 17:17:07
         * checkState : 2
         * checkTime : 2021-01-13 18:10:39
         * checkReply : 申请理由不充分
         */

        private CancelInfoBean cancel_info;

        public String getCancel_process() {
            return cancel_process;
        }

        public void setCancel_process(String cancel_process) {
            this.cancel_process = cancel_process;
        }

        public CancelInfoBean getCancel_info() {
            return cancel_info;
        }

        public void setCancel_info(CancelInfoBean cancel_info) {
            this.cancel_info = cancel_info;
        }

        public static class CancelInfoBean {
            private String userTel;
            private String cancelRemarks;
            private String addTime;
            private String checkState;//0代表未审核 1代表审核通过 2代表审核失败
            private String checkTime;
            private String checkReply;
            private List<String> cancelReason;
            private int allowCancel;//为1返回，是否允许注销，1代表在截至时间之内，允许注销；0代表超过截至时间，不允许注销，需重新提交申请

            public int getAllowCancel() {
                return allowCancel;
            }

            public void setAllowCancel(int allowCancel) {
                this.allowCancel = allowCancel;
            }

            public String getUserTel() {
                return userTel;
            }

            public void setUserTel(String userTel) {
                this.userTel = userTel;
            }

            public String getCancelRemarks() {
                return cancelRemarks;
            }

            public void setCancelRemarks(String cancelRemarks) {
                this.cancelRemarks = cancelRemarks;
            }

            public String getAddTime() {
                return addTime;
            }

            public void setAddTime(String addTime) {
                this.addTime = addTime;
            }

            public String getCheckState() {
                return checkState;
            }

            public void setCheckState(String checkState) {
                this.checkState = checkState;
            }

            public String getCheckTime() {
                return checkTime;
            }

            public void setCheckTime(String checkTime) {
                this.checkTime = checkTime;
            }

            public String getCheckReply() {
                return checkReply;
            }

            public void setCheckReply(String checkReply) {
                this.checkReply = checkReply;
            }

            public List<String> getCancelReason() {
                return cancelReason;
            }

            public void setCancelReason(List<String> cancelReason) {
                this.cancelReason = cancelReason;
            }
        }
    }
}
