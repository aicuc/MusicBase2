package com.musicbase.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.FirstBean;
import com.musicbase.model.recyclerviewmodel.MySection;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-27.
 */

public class ContentListAdapter extends BaseSectionQuickAdapter<MySection, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId      The layout resource id of each item.
     * @param data             A new list is created out of this one to avoid mutable list
     */
    private Context context;
    public ContentListAdapter(Context context,int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
        this.context = context;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final MySection item) {
        helper.setText(R.id.name, item.header);
        helper.setVisible(R.id.more, item.isMore());
//        helper.addOnClickListener(R.id.more);
    }


    @Override
    protected void convert(BaseViewHolder helper, MySection item) {
        FirstBean.Data.ColumnList.CourseList courseList = item.t;
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+courseList.getCourseImgPathVertical()).into((ImageView) helper.getView(R.id.im));
        helper.setText(R.id.name, courseList.getCourseName());
        helper.setText(R.id.beizhu, courseList.getCourseState());
        helper.setText(R.id.price, courseList.getResourceCount()+"讲/"+courseList.getCurrentPrice()+"元");
    }
}
