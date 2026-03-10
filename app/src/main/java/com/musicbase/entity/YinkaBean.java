package com.musicbase.entity;

import java.util.List;

public class YinkaBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"courseRoundMapList":[{"courseUrl":"https://www.yinyuesuyang.com/","courseImgPathVertical":null,"classification":2,"courseBookType":1,"courseImgPathHorizontal":"2021/8/20/images/81ddaa94-318f-4af4-a1a8-818d0dc022e4.jpg","allowOpenInBrowser":0,"courseId":49,"courseName":"音咖计划轮播图网页"}],"columnList":[{"systemCodeId":19,"systemCodeName":"音乐与财商","iconDark":"icon/yinyueketang/yykt_dark.png","iconLight":"icon/yinyueketang/yykt_light.png","introduce":"培养一个音乐宝贝需要多少钱？财商教育要趁早","coverImg":"icon/yinkajihua/4684d21c-0faf-4d78-8314-94a28ad4d96c.jpg","sc_release":1},{"systemCodeId":20,"systemCodeName":"音咖计划","iconDark":"icon/jiaoxuefanli/jxfl_dark.png","iconLight":"icon/jiaoxuefanli/jxfl_light.png","introduce":"来这里，孩子的音乐教育金提早规划","sc_release":0},{"systemCodeId":21,"systemCodeName":"线下活动","iconDark":"icon/jingpinxiti/jpxt_dark.png","iconLight":"icon/jingpinxiti/jpxt_light.png","introduce":"即将上线","sc_release":0}]}
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
         * courseUrl : https://www.yinyuesuyang.com/
         * courseImgPathVertical : null
         * classification : 2
         * courseBookType : 1
         * courseImgPathHorizontal : 2021/8/20/images/81ddaa94-318f-4af4-a1a8-818d0dc022e4.jpg
         * allowOpenInBrowser : 0
         * courseId : 49
         * courseName : 音咖计划轮播图网页
         */

        private List<CourseRoundMapListBean> courseRoundMapList;
        /**
         * systemCodeId : 19
         * systemCodeName : 音乐与财商
         * iconDark : icon/yinyueketang/yykt_dark.png
         * iconLight : icon/yinyueketang/yykt_light.png
         * introduce : 培养一个音乐宝贝需要多少钱？财商教育要趁早
         * coverImg : icon/yinkajihua/4684d21c-0faf-4d78-8314-94a28ad4d96c.jpg
         * sc_release : 1
         */

        private List<ColumnListBean> columnList;

        public List<CourseRoundMapListBean> getCourseRoundMapList() {
            return courseRoundMapList;
        }

        public void setCourseRoundMapList(List<CourseRoundMapListBean> courseRoundMapList) {
            this.courseRoundMapList = courseRoundMapList;
        }

        public List<ColumnListBean> getColumnList() {
            return columnList;
        }

        public void setColumnList(List<ColumnListBean> columnList) {
            this.columnList = columnList;
        }

        public static class CourseRoundMapListBean {
            private String courseUrl;
            private Object courseImgPathVertical;
            private int classification;
            private int courseBookType;
            private String courseImgPathHorizontal;
            private int allowOpenInBrowser;
            private int courseId;
            private String courseName;
            private int systemCodeId;
            private FirstBean.Data.ColumnList.CourseList.AppJumpInfoBean appJumpInfo;

            public int getSystemCodeId() {
                return systemCodeId;
            }

            public void setSystemCodeId(int systemCodeId) {
                this.systemCodeId = systemCodeId;
            }

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

            public int getClassification() {
                return classification;
            }

            public void setClassification(int classification) {
                this.classification = classification;
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

            public String getCourseName() {
                return courseName;
            }

            public void setCourseName(String courseName) {
                this.courseName = courseName;
            }
        }

        public static class ColumnListBean {
            private int systemCodeId;
            private String systemCodeName;
            private String iconDark;
            private String iconLight;
            private String introduce;
            private String coverImg;
            private int sc_release;

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

            public int getSc_release() {
                return sc_release;
            }

            public void setSc_release(int sc_release) {
                this.sc_release = sc_release;
            }
        }
    }
}
