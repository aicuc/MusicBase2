package com.lljjcoder.style.citylist.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：liji on 2017/5/19 17:07
 * 邮箱：lijiwork@sina.com
 * QQ ：275137657
 */

public class CityInfoBean implements Parcelable {

    
    private String pid; //上级编码
    
    private String name; /*东城区*/
    private String code; //地区编码
    
    private ArrayList<CityInfoBean> areas;

    protected CityInfoBean(Parcel in) {
        pid = in.readString();
        name = in.readString();
        code = in.readString();
        areas = in.createTypedArrayList(CityInfoBean.CREATOR);
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

    public static final Creator<CityInfoBean> CREATOR = new Creator<CityInfoBean>() {
        @Override
        public CityInfoBean createFromParcel(Parcel in) {
            return new CityInfoBean(in);
        }

        @Override
        public CityInfoBean[] newArray(int size) {
            return new CityInfoBean[size];
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

    public ArrayList<CityInfoBean> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<CityInfoBean> areas) {
        this.areas = areas;
    }

    public CityInfoBean() {
    }
    
    public static CityInfoBean findCity(List<CityInfoBean> list, String cityName) {
        try {
            for (int i = 0; i < list.size(); i++) {
                CityInfoBean city = list.get(i);
                if (cityName.equals(city.getName()) /*|| cityName.contains(city.getName())
                        || city.getName().contains(cityName)*/) {
                    return city;
                }
            }
        }
        catch (Exception e) {
            return null;
        }
        return null;
    }


}
