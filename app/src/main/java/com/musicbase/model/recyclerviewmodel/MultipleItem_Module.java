package com.musicbase.model.recyclerviewmodel;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.FirstBean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MultipleItem_Module implements MultiItemEntity {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMG = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_URL = 4;

    private int itemType;
    private DetailBean.DataBean.ModuleList bean;

    public MultipleItem_Module(int itemType, DetailBean.DataBean.ModuleList bean) {
        this.itemType = itemType;
        this.bean = bean;
    }

    public DetailBean.DataBean.ModuleList getBean() {
        return bean;
    }

    public void setBean(DetailBean.DataBean.ModuleList bean) {
        this.bean = bean;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
