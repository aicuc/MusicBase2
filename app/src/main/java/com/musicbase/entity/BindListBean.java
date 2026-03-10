package com.musicbase.entity;

import java.util.List;

public class BindListBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"userauthsList":[{"id":116,"identity_type":"telphone","userId":107,"identifier":"13121133886"},{"id":925,"identity_type":"qq","userId":107,"identifier":"ffsfdfsdfsdf"},{"id":931,"identity_type":"weixin","userId":107,"identifier":"oNrPR0o5C29j2g4PV5KJAGB0WZx8"},{"id":933,"identity_type":"sina","userId":107,"identifier":"aaa"}]}
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
        private List<UserauthsListBean> userauthsList;

        public List<UserauthsListBean> getUserauthsList() {
            return userauthsList;
        }

        public void setUserauthsList(List<UserauthsListBean> userauthsList) {
            this.userauthsList = userauthsList;
        }

        public static class UserauthsListBean {
            /**
             * id : 116
             * identity_type : telphone
             * userId : 107
             * identifier : 13121133886
             */

            private int id;
            private String identity_type;
            private int userId;
            private String identifier;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getIdentity_type() {
                return identity_type;
            }

            public void setIdentity_type(String identity_type) {
                this.identity_type = identity_type;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getIdentifier() {
                return identifier;
            }

            public void setIdentifier(String identifier) {
                this.identifier = identifier;
            }
        }
    }
}
