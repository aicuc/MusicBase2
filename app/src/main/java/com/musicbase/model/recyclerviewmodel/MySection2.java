package com.musicbase.model.recyclerviewmodel;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.musicbase.entity.BuyedBean;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class MySection2 extends SectionEntity<BuyedBean.Data.CourseList> {
    private boolean isMore;
    private int id;
    public MySection2(boolean isHeader, int id, String header, boolean isMroe) {
        super(isHeader, header);
        this.isMore = isMroe;
        this.id = id;
    }

    public MySection2(BuyedBean.Data.CourseList t) {
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
