package com.musicbase.entity;

import java.util.List;

public class BookVOBean {

    /**
     * code : SUCCESS
     * codeInfo : 查询成功!
     * data : [{"courseId":69,"courseName":"书架课程5","courseBookType":1,"classification":1,"deadlineType":0,"allowOpen":1},{"courseId":61,"courseName":"书架课程1","courseBookType":1,"courseImgPathHorizontal":"2021/12/16/images/1014c869-006b-4d7b-a790-3f96037c401b.png","courseImgPathVertical":"2021/12/16/images/9baab851-c28a-47fb-87a0-a71a2133c662.png","classification":1,"deadlineType":0,"allowOpen":0,"banReason":"此为苹果端APP、微信小程序专属教材，请更换客户端"},{"courseId":66,"courseName":"书架课程2","courseBookType":1,"courseImgPathHorizontal":"2021/12/16/images/13d9a015-85d6-444b-a46e-1cbb36e4d02c.jpg","courseImgPathVertical":"2021/12/16/images/b5002d8e-9503-49d2-9154-e3ce6ddb4a4d.png","classification":1,"deadlineType":1,"isOverdue":1,"allowOpen":1},{"courseId":68,"courseName":"书架课程4","courseBookType":1,"classification":1,"deadlineType":1,"isOverdue":0,"deadlineInfo":"2021-12-30","allowOpen":1},{"courseId":67,"courseName":"书架课程3","courseBookType":1,"classification":1,"deadlineType":0,"allowOpen":1}]
     */

    private String code;
    private String codeInfo;
    /**
     * courseId : 69
     * courseName : 书架课程5
     * courseBookType : 1
     * classification : 1
     * deadlineType : 0
     * allowOpen : 1
     */

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

    public static class DataBean {
        private int courseId;
        private String courseName;
        private int courseBookType;
        private String courseImgPathHorizontal;
        private String courseImgPathVertical;
        private int classification;
        private int deadlineType;
        private int isOverdue;
        private String deadlineInfo;
        private int allowOpen;
        private String banReason;

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

        public int getCourseBookType() {
            return courseBookType;
        }

        public void setCourseBookType(int courseBookType) {
            this.courseBookType = courseBookType;
        }

        public int getClassification() {
            return classification;
        }

        public void setClassification(int classification) {
            this.classification = classification;
        }

        public int getDeadlineType() {
            return deadlineType;
        }

        public void setDeadlineType(int deadlineType) {
            this.deadlineType = deadlineType;
        }

        public int getAllowOpen() {
            return allowOpen;
        }

        public void setAllowOpen(int allowOpen) {
            this.allowOpen = allowOpen;
        }

        public String getCourseImgPathHorizontal() {
            return courseImgPathHorizontal;
        }

        public void setCourseImgPathHorizontal(String courseImgPathHorizontal) {
            this.courseImgPathHorizontal = courseImgPathHorizontal;
        }

        public String getCourseImgPathVertical() {
            return courseImgPathVertical;
        }

        public void setCourseImgPathVertical(String courseImgPathVertical) {
            this.courseImgPathVertical = courseImgPathVertical;
        }

        public int getIsOverdue() {
            return isOverdue;
        }

        public void setIsOverdue(int isOverdue) {
            this.isOverdue = isOverdue;
        }

        public String getDeadlineInfo() {
            return deadlineInfo;
        }

        public void setDeadlineInfo(String deadlineInfo) {
            this.deadlineInfo = deadlineInfo;
        }

        public String getBanReason() {
            return banReason;
        }

        public void setBanReason(String banReason) {
            this.banReason = banReason;
        }
    }
}
