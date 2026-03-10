package com.musicbase.entity;

import java.util.List;

public class YinkaDetailBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"systemCodeId":19,"systemCodeName":"音乐与财商","iconDark":"icon/yinyueketang/yykt_dark.png","iconLight":"icon/yinyueketang/yykt_light.png","introduce":"培养一个音乐宝贝需要多少钱？财商教育要趁早","coverImg":"icon/yinkajihua/4684d21c-0faf-4d78-8314-94a28ad4d96c.jpg","courseList":[{"courseUrl":"https://biaodan100.com/web/formview/611da6e175a03c6a0c706780","courseImgPathVertical":null,"icon_dark":"icon/yinyuezixun/yyzx_dark.png","systemcode_id":"28","classification":2,"systemcode_name":"你会让孩子炒股吗","courseBookType":1,"courseImgPathHorizontal":"2021/8/20/images/1e9bc705-376d-4d19-8adb-ab09fa6f8c14.png","allowOpenInBrowser":1,"courseId":50,"icon_light":"icon/yinyuezixun/yyzx_light.png","courseName":"音咖计划调查问卷"},{"courseUrl":"https://evolution-h5.licaimofang.com/article/10","courseImgPathVertical":null,"icon_dark":"icon/yinyueketang/yykt_dark.png","systemcode_id":"30","classification":2,"systemcode_name":"子女教育金组合是给孩子最好的备案","courseBookType":1,"courseImgPathHorizontal":null,"allowOpenInBrowser":0,"courseId":48,"icon_light":"icon/yinyueketang/yykt_light.png","courseName":"子女教育组合给孩子最好的备案"},{"courseUrl":"https://mp.weixin.qq.com/s?__biz=MzU5MDkxMTI4Nw==&mid=2247493318&idx=2&sn=0532d10a03c08eea3e3e2801e89844c9&chksm=fe35a49ec9422d88fc8f0218bb8fd395fdd8a86ebe502cf728d4391e1e15675a5cff3fc17378&mpshare=1&scene=1&srcid=0809gG0LZebTgLaZva0cBXnm&sharer_sharetime=1629364019875&sharer_shareid=c83e6797a1e0d92d4b18e97f4f218398#rd","courseImgPathVertical":"2021/8/20/images/4ffae562-6d59-4d6f-9631-4f0535eb8856.jpg","icon_dark":"icon/yinyuezixun/yyzx_dark.png","systemcode_id":"28","classification":2,"systemcode_name":"你会让孩子炒股吗","courseBookType":1,"courseImgPathHorizontal":"2021/8/20/images/db488768-a14e-4d20-8331-6cf3cab588f7.jpg","allowOpenInBrowser":1,"courseId":47,"icon_light":"icon/yinyuezixun/yyzx_light.png","courseName":"有奖互动 | 马永谙：你会让自己的孩子炒股吗？怎么正确进行财商教育？"}]}
     */

    private String code;
    private String codeInfo;
    /**
     * systemCodeId : 19
     * systemCodeName : 音乐与财商
     * iconDark : icon/yinyueketang/yykt_dark.png
     * iconLight : icon/yinyueketang/yykt_light.png
     * introduce : 培养一个音乐宝贝需要多少钱？财商教育要趁早
     * coverImg : icon/yinkajihua/4684d21c-0faf-4d78-8314-94a28ad4d96c.jpg
     * courseList : [{"courseUrl":"https://biaodan100.com/web/formview/611da6e175a03c6a0c706780","courseImgPathVertical":null,"icon_dark":"icon/yinyuezixun/yyzx_dark.png","systemcode_id":"28","classification":2,"systemcode_name":"你会让孩子炒股吗","courseBookType":1,"courseImgPathHorizontal":"2021/8/20/images/1e9bc705-376d-4d19-8adb-ab09fa6f8c14.png","allowOpenInBrowser":1,"courseId":50,"icon_light":"icon/yinyuezixun/yyzx_light.png","courseName":"音咖计划调查问卷"},{"courseUrl":"https://evolution-h5.licaimofang.com/article/10","courseImgPathVertical":null,"icon_dark":"icon/yinyueketang/yykt_dark.png","systemcode_id":"30","classification":2,"systemcode_name":"子女教育金组合是给孩子最好的备案","courseBookType":1,"courseImgPathHorizontal":null,"allowOpenInBrowser":0,"courseId":48,"icon_light":"icon/yinyueketang/yykt_light.png","courseName":"子女教育组合给孩子最好的备案"},{"courseUrl":"https://mp.weixin.qq.com/s?__biz=MzU5MDkxMTI4Nw==&mid=2247493318&idx=2&sn=0532d10a03c08eea3e3e2801e89844c9&chksm=fe35a49ec9422d88fc8f0218bb8fd395fdd8a86ebe502cf728d4391e1e15675a5cff3fc17378&mpshare=1&scene=1&srcid=0809gG0LZebTgLaZva0cBXnm&sharer_sharetime=1629364019875&sharer_shareid=c83e6797a1e0d92d4b18e97f4f218398#rd","courseImgPathVertical":"2021/8/20/images/4ffae562-6d59-4d6f-9631-4f0535eb8856.jpg","icon_dark":"icon/yinyuezixun/yyzx_dark.png","systemcode_id":"28","classification":2,"systemcode_name":"你会让孩子炒股吗","courseBookType":1,"courseImgPathHorizontal":"2021/8/20/images/db488768-a14e-4d20-8331-6cf3cab588f7.jpg","allowOpenInBrowser":1,"courseId":47,"icon_light":"icon/yinyuezixun/yyzx_light.png","courseName":"有奖互动 | 马永谙：你会让自己的孩子炒股吗？怎么正确进行财商教育？"}]
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
        private int systemCodeId;
        private String systemCodeName;
        private String iconDark;
        private String iconLight;
        private String introduce;
        private String coverImg;
        /**
         * courseUrl : https://biaodan100.com/web/formview/611da6e175a03c6a0c706780
         * courseImgPathVertical : null
         * icon_dark : icon/yinyuezixun/yyzx_dark.png
         * systemcode_id : 28
         * classification : 2
         * systemcode_name : 你会让孩子炒股吗
         * courseBookType : 1
         * courseImgPathHorizontal : 2021/8/20/images/1e9bc705-376d-4d19-8adb-ab09fa6f8c14.png
         * allowOpenInBrowser : 1
         * courseId : 50
         * icon_light : icon/yinyuezixun/yyzx_light.png
         * courseName : 音咖计划调查问卷
         */

        private List<CourseListBean> courseList;

        public int getSystemCodeId() {
            return systemCodeId;
        }

        public void setSystemCodeId(int systemCodeId) {
            this.systemCodeId = systemCodeId;
        }

        public String getSystemCodeName() {
            return systemCodeName;
        }

        public void setSystemCodeName(String systemCodeName) {
            this.systemCodeName = systemCodeName;
        }

        public String getIconDark() {
            return iconDark;
        }

        public void setIconDark(String iconDark) {
            this.iconDark = iconDark;
        }

        public String getIconLight() {
            return iconLight;
        }

        public void setIconLight(String iconLight) {
            this.iconLight = iconLight;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getCoverImg() {
            return coverImg;
        }

        public void setCoverImg(String coverImg) {
            this.coverImg = coverImg;
        }

        public List<CourseListBean> getCourseList() {
            return courseList;
        }

        public void setCourseList(List<CourseListBean> courseList) {
            this.courseList = courseList;
        }

        public static class CourseListBean {
            private String courseUrl;
            private Object courseImgPathVertical;
            private String icon_dark;
            private String systemcode_id;
            private int classification;
            private String systemcode_name;
            private int courseBookType;
            private String courseImgPathHorizontal;
            private int allowOpenInBrowser;
            private int courseId;
            private String icon_light;
            private String courseName;
            private FirstBean.Data.ColumnList.CourseList.AppJumpInfoBean appJumpInfo;

            public FirstBean.Data.ColumnList.CourseList.AppJumpInfoBean getAppJumpInfo() {
                return appJumpInfo;
            }

            public void setAppJumpInfo(FirstBean.Data.ColumnList.CourseList.AppJumpInfoBean appJumpInfo) {
                this.appJumpInfo = appJumpInfo;
            }

            public String getCourseUrl() {
                return courseUrl;
            }

            public void setCourseUrl(String courseUrl) {
                this.courseUrl = courseUrl;
            }

            public Object getCourseImgPathVertical() {
                return courseImgPathVertical;
            }

            public void setCourseImgPathVertical(Object courseImgPathVertical) {
                this.courseImgPathVertical = courseImgPathVertical;
            }

            public String getIcon_dark() {
                return icon_dark;
            }

            public void setIcon_dark(String icon_dark) {
                this.icon_dark = icon_dark;
            }

            public String getSystemcode_id() {
                return systemcode_id;
            }

            public void setSystemcode_id(String systemcode_id) {
                this.systemcode_id = systemcode_id;
            }

            public int getClassification() {
                return classification;
            }

            public void setClassification(int classification) {
                this.classification = classification;
            }

            public String getSystemcode_name() {
                return systemcode_name;
            }

            public void setSystemcode_name(String systemcode_name) {
                this.systemcode_name = systemcode_name;
            }

            public int getCourseBookType() {
                return courseBookType;
            }

            public void setCourseBookType(int courseBookType) {
                this.courseBookType = courseBookType;
            }

            public String getCourseImgPathHorizontal() {
                return courseImgPathHorizontal;
            }

            public void setCourseImgPathHorizontal(String courseImgPathHorizontal) {
                this.courseImgPathHorizontal = courseImgPathHorizontal;
            }

            public int getAllowOpenInBrowser() {
                return allowOpenInBrowser;
            }

            public void setAllowOpenInBrowser(int allowOpenInBrowser) {
                this.allowOpenInBrowser = allowOpenInBrowser;
            }

            public int getCourseId() {
                return courseId;
            }

            public void setCourseId(int courseId) {
                this.courseId = courseId;
            }

            public String getIcon_light() {
                return icon_light;
            }

            public void setIcon_light(String icon_light) {
                this.icon_light = icon_light;
            }

            public String getCourseName() {
                return courseName;
            }

            public void setCourseName(String courseName) {
                this.courseName = courseName;
            }
        }
    }
}
