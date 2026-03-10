package com.musicbase.entity;

import java.util.List;

public class FolderBean {


    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : [{"resourceId":11,"resourceType":"testPaper","resourceFileName":"综合模拟初级04","resourceSize":25986676,"resourceSaveName":"e576ec98-ae4f-485c-adee-6417ccd3cdf8.zip","resourcePath":"2019/7/26/attachs/","chargeType":0,"originalPrice":"0.01","currentPrice":"0.01","isDismount":0,"isFolder":0,"isPay":0},{"resourceId":12,"resourceType":"testPaper","resourceFileName":"综合模拟初级05","resourceSize":21023528,"resourceSaveName":"bde9945b-993d-468f-bdfa-081cff547cc2.zip","resourcePath":"2019/7/26/attachs/","chargeType":1,"originalPrice":"0.01","currentPrice":"0.01","isDismount":0,"isFolder":0,"isPay":1},{"resourceId":13,"resourceType":"testPaper","resourceFileName":"综合模拟初级07","resourceSize":25759806,"resourceSaveName":"e2cb3dd5-5ed1-4ae7-ae54-ec1900ce2d7d.zip","resourcePath":"2019/7/26/attachs/","chargeType":1,"originalPrice":"0.01","currentPrice":"0.01","isDismount":0,"isFolder":0,"isPay":1}]
     */

    private String code;
    private String codeInfo;
    private List<DetailBean.DataBean.ResourceListBean> data;

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

    public List<DetailBean.DataBean.ResourceListBean> getData() {
        return data;
    }

    public void setData(List<DetailBean.DataBean.ResourceListBean> data) {
        this.data = data;
    }


}
