package com.musicbase.entity;

import java.util.List;

public class SearchBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : {"pageNo":"1","pageSize":"5","totalCount":"17","results":[{"resourceId":"4404","isFolder":"0","resourceFileName":"音响63.《自新大陆》第九交响曲（第二乐章）","resourcePid":"4386","courseId":"209","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（中级·音乐版）下册"},{"resourceId":"4278","isFolder":"0","resourceFileName":"曲目113.第四十交响曲","resourcePid":"4149","courseId":"203","resourceType":"mp3","courseName":"全国音乐素养等级考试（中级）乐理·视唱练耳分册 下册"},{"resourceId":"4277","isFolder":"0","resourceFileName":"曲目113a.第四十交响曲","resourcePid":"4149","courseId":"203","resourceType":"mp3","courseName":"全国音乐素养等级考试（中级）乐理·视唱练耳分册 下册"},{"resourceId":"3395","isFolder":"0","resourceFileName":"音响72：第三交响曲（英雄）第一乐章","resourcePid":"3348","courseId":"211","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（高级·音乐版）下册"},{"resourceId":"3392","isFolder":"0","resourceFileName":"音响70：第三交响曲（英雄）第四乐章","resourcePid":"3347","courseId":"211","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（高级·音乐版）下册"}]}
     */

    private String code;
    private String codeInfo;
    /**
     * pageNo : 1
     * pageSize : 5
     * totalCount : 17
     * results : [{"resourceId":"4404","isFolder":"0","resourceFileName":"音响63.《自新大陆》第九交响曲（第二乐章）","resourcePid":"4386","courseId":"209","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（中级·音乐版）下册"},{"resourceId":"4278","isFolder":"0","resourceFileName":"曲目113.第四十交响曲","resourcePid":"4149","courseId":"203","resourceType":"mp3","courseName":"全国音乐素养等级考试（中级）乐理·视唱练耳分册 下册"},{"resourceId":"4277","isFolder":"0","resourceFileName":"曲目113a.第四十交响曲","resourcePid":"4149","courseId":"203","resourceType":"mp3","courseName":"全国音乐素养等级考试（中级）乐理·视唱练耳分册 下册"},{"resourceId":"3395","isFolder":"0","resourceFileName":"音响72：第三交响曲（英雄）第一乐章","resourcePid":"3348","courseId":"211","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（高级·音乐版）下册"},{"resourceId":"3392","isFolder":"0","resourceFileName":"音响70：第三交响曲（英雄）第四乐章","resourcePid":"3347","courseId":"211","resourceType":"mp3","courseName":"全国音乐素养等级考试 音乐基础知识 音乐常识分册（高级·音乐版）下册"}]
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
        private String pageNo;
        private String pageSize;
        private String totalCount;
        /**
         * resourceId : 4404
         * isFolder : 0
         * resourceFileName : 音响63.《自新大陆》第九交响曲（第二乐章）
         * resourcePid : 4386
         * courseId : 209
         * resourceType : mp3
         * courseName : 全国音乐素养等级考试 音乐基础知识 音乐常识分册（中级·音乐版）下册
         */

        private List<DetailBean.DataBean.ResourceListBean> results;

        public String getPageNo() {
            return pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public List<DetailBean.DataBean.ResourceListBean> getResults() {
            return results;
        }

        public void setResults(List<DetailBean.DataBean.ResourceListBean> results) {
            this.results = results;
        }


    }
}
