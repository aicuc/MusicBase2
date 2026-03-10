package com.musicbase.entity;
public class GetPhotoBean {
	private String code;
	private String codeInfo;
	private DetailBean.DataBean.UserAvatarsBean data;
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

    public DetailBean.DataBean.UserAvatarsBean getData() {
        return data;
    }

    public void setData(DetailBean.DataBean.UserAvatarsBean data) {
        this.data = data;
    }
}
