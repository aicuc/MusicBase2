package com.musicbase.model.recyclerviewmodel;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.musicbase.entity.FirstBean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MultipleItem implements MultiItemEntity {
    public static final int HEAD = 0;
    public static final int CONTENT_1 = 1;
    public static final int CONTENT_3 = 2;

    public static final int HEAD_SIZE = 3;
    public static final int CONTENT_1_SIZE = 1;
    public static final int CONTENT_3_SIZE = 3;

    private int itemType;
    private int spanSize;
    private FirstBean.Data.ColumnList.CourseList bean;
    private int courseId;
    private String imId;

    public MultipleItem(int itemType, int spanSize, int courseId,String content,String imId) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.content = content;
        this.courseId = courseId;
        this.imId = imId;
    }

    public MultipleItem(int itemType, int spanSize) {
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public MultipleItem(int itemType, int spanSize, FirstBean.Data.ColumnList.CourseList bean) {
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.bean = bean;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public FirstBean.Data.ColumnList.CourseList getBean() {
        return bean;
    }

    public void setBean(FirstBean.Data.ColumnList.CourseList bean) {
        this.bean = bean;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
