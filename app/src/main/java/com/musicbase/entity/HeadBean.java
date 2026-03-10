package com.musicbase.entity;

import java.util.List;

public class HeadBean {

    /**
     * code : SUCCESS
     * codeInfo : 操作成功!
     * data : ["user_avatar/default/default_01.png","user_avatar/default/default_02.png","user_avatar/default/default_03.png","user_avatar/default/default_04.png"]
     */

    private String code;
    private String codeInfo;
    private List<String> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
