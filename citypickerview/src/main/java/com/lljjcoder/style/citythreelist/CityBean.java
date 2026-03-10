package com.lljjcoder.style.citythreelist;

import android.os.Parcel;
import android.os.Parcelable;

import com.lljjcoder.bean.DistrictBean;

import java.util.ArrayList;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */
public class CityBean implements Parcelable {


    private String pid; //上级编码

    private String name; /*东城区*/
    private String code; //地区编码

    private ArrayList<DistrictBean> areas;

    public CityBean() {
    }

    protected CityBean(Parcel in) {
        pid = in.readString();
        name = in.readString();
        code = in.readString();
        areas = in.createTypedArrayList(DistrictBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pid);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeTypedList(areas);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CityBean> CREATOR = new Creator<CityBean>() {
        @Override
        public CityBean createFromParcel(Parcel in) {
            return new CityBean(in);
        }

        @Override
        public CityBean[] newArray(int size) {
            return new CityBean[size];
        }
    };

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

    public ArrayList<DistrictBean> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<DistrictBean> areas) {
        this.areas = areas;
    }
}
