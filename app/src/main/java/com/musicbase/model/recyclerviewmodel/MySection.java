package com.musicbase.model.recyclerviewmodel;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.musicbase.entity.FirstBean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MySection extends SectionEntity<FirstBean.Data.ColumnList.CourseList> {
    private boolean isMore;
    private int id;
    public MySection(boolean isHeader, int id,String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
        this.id = id;
    }

    public MySection(FirstBean.Data.ColumnList.CourseList t) {
        super(t);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean mroe) {
        isMore = mroe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
