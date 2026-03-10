package com.musicbase.entity;

import java.util.List;

/**
 * Created by BAO on 2018-09-29.
 */

public class ResourseBean {
    private List<String> fileIds;
    private int position;

    public ResourseBean(List<String> fileIds, int position) {
        this.fileIds = fileIds;
        this.position = position;
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
