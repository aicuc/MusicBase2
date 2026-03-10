package com.lljjcoder.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lljjcoder.style.citylist.bean.CityInfoBean;

import java.util.ArrayList;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class DistrictBean implements Parcelable {

    private String pid; //上级编码

    private String name; /*东城区*/
    private String code; //地区编码

    public DistrictBean() {
    }

    protected DistrictBean(Parcel in) {
        pid = in.readString();
        name = in.readString();
        code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pid);
        dest.writeString(name);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DistrictBean> CREATOR = new Creator<DistrictBean>() {
        @Override
        public DistrictBean createFromParcel(Parcel in) {
            return new DistrictBean(in);
        }

        @Override
        public DistrictBean[] newArray(int size) {
            return new DistrictBean[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
