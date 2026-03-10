package com.musicbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 音乐条目
 *
 * @author Administrator
 */
@Entity
public class AudioItem implements Parcelable {

    @Id
    private Long id;

    private String title;//音频名

    private String courseName;//课程名

    private String field;//音乐路径或者腾讯云id


    protected AudioItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        courseName = in.readString();
        field = in.readString();
    }

    @Generated(hash = 135280848)
    public AudioItem(Long id, String title, String courseName, String field) {
        this.id = id;
        this.title = title;
        this.courseName = courseName;
        this.field = field;
    }

    @Generated(hash = 1683268244)
    public AudioItem() {
    }

    public static final Creator<AudioItem> CREATOR = new Creator<AudioItem>() {
        @Override
        public AudioItem createFromParcel(Parcel in) {
            return new AudioItem(in);
        }

        @Override
        public AudioItem[] newArray(int size) {
            return new AudioItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
        dest.writeString(courseName);
        dest.writeString(field);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
