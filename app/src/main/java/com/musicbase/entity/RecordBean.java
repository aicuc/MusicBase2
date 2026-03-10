package com.musicbase.entity;

public class RecordBean {
    public static final int RECORD_DAOJISHI = 1;
    public static final int RECORD_BEGIN = 2;
    public static final int RECORD_DESTROY = 3;
    public static final int RECORD_DAOJISHI_BEGIN = 4;
    private int type,time,allTime;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getAllTime() {
        return allTime;
    }

    public void setAllTime(int allTime) {
        this.allTime = allTime;
    }

    public RecordBean(int type, int time, int allTime) {
        this.type = type;
        this.time = time;
        this.allTime = allTime;
    }
}
