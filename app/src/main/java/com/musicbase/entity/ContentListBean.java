package com.musicbase.entity;

import java.util.List;

/**
 * Created by BAO on 2018-09-27.
 */

public class ContentListBean {


    private String code;
    private String codeInfo;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private int systemCodeId;
        private String systemCodeName;
        private List<FirstBean.Data.ColumnList.CourseList> courseList;
        private List<ContentListBean.Data> children;

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

        public List<FirstBean.Data.ColumnList.CourseList> getCourseList() {
            return courseList;
        }

        public void setCourseList(List<FirstBean.Data.ColumnList.CourseList> courseList) {
            this.courseList = courseList;
        }

        public List<ContentListBean.Data> getChildren() {
            return children;
        }

        public void setChildren(List<ContentListBean.Data> children) {
            this.children = children;
        }
    }
}
