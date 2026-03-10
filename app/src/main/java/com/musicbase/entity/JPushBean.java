package com.musicbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class JPushBean implements Parcelable {
    String jumpType;//url 网页（打开网页）  recommendCourse 知乐推荐（打开知乐课程详情）
    String jumpUrl;//网址                                                  图书id
    String title;//（选填)打开指定页面的标题
    String allowOpenInBrowser;//（选填）：参数值为0或1，如果指定打开页面为网页，根据此参数判断是否可以跳转浏览器打开网页，0不可以，1可以，其他页面无效果
    String alertOpenInBrowser;//（选填）:参数值为0或1，用于刚进入网页是提示用户是否用浏览器打开，0不提示，1提示。

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAllowOpenInBrowser() {
        return allowOpenInBrowser;
    }

    public void setAllowOpenInBrowser(String allowOpenInBrowser) {
        this.allowOpenInBrowser = allowOpenInBrowser;
    }

    public String getAlertOpenInBrowser() {
        return alertOpenInBrowser;
    }

    public void setAlertOpenInBrowser(String alertOpenInBrowser) {
        this.alertOpenInBrowser = alertOpenInBrowser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.jumpType);
        dest.writeString(this.jumpUrl);
        dest.writeString(this.title);
        dest.writeString(this.allowOpenInBrowser);
        dest.writeString(this.alertOpenInBrowser);
    }

    public JPushBean() {
    }

    protected JPushBean(Parcel in) {
        this.jumpType = in.readString();
        this.jumpUrl = in.readString();
        this.title = in.readString();
        this.allowOpenInBrowser = in.readString();
        this.alertOpenInBrowser = in.readString();
    }

    public static final Creator<JPushBean> CREATOR = new Creator<JPushBean>() {
        @Override
        public JPushBean createFromParcel(Parcel source) {
            return new JPushBean(source);
        }

        @Override
        public JPushBean[] newArray(int size) {
            return new JPushBean[size];
        }
    };

    @Override
    public String toString() {
        return "JPushBean{" + "jumpType='" + jumpType + '\'' + ", jumpUrl='" + jumpUrl + '\'' + ", title='" + title + '\'' + ", allowOpenInBrowser='" + allowOpenInBrowser + '\'' + ", alertOpenInBrowser='" + alertOpenInBrowser + '\'' + '}';
    }
}
