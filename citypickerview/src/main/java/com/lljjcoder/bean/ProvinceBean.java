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
public class ProvinceBean implements Parcelable {

    private String pid; //上级编码

    private String name; /*东城区*/
    private String code; //地区编码

    private ArrayList<CityBean> areas;

    public ProvinceBean() {
    }

    protected ProvinceBean(Parcel in) {
        pid = in.readString();
        name = in.readString();
        code = in.readString();
        areas = in.createTypedArrayList(CityBean.CREATOR);
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

    public static final Creator<ProvinceBean> CREATOR = new Creator<ProvinceBean>() {
        @Override
        public ProvinceBean createFromParcel(Parcel in) {
            return new ProvinceBean(in);
        }

        @Override
        public ProvinceBean[] newArray(int size) {
            return new ProvinceBean[size];
        }
    };

    @Override
  public String toString() {
    return  name ;
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

    public ArrayList<CityBean> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<CityBean> areas) {
        this.areas = areas;
    }
}
