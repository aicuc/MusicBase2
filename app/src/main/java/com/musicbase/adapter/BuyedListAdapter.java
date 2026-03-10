package com.musicbase.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.BuyedBean;
import com.musicbase.model.recyclerviewmodel.MySection2;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-27.
 */

public class BuyedListAdapter extends BaseSectionQuickAdapter<MySection2, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId      The layout resource id of each item.
     * @param data             A new list is created out of this one to avoid mutable list
     */
    private Context context;
    //音乐课堂 精品教材 精品习题 名师名家 教育联盟 音乐咨询
    int[] imIds = {R.mipmap.yueqi_01, R.mipmap.yueqi_02, R.mipmap.yueqi_03, R.mipmap.yueqi_04, R.mipmap.yueqi_05, R.mipmap.yueqi_06};

    public BuyedListAdapter(Context context, int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
        this.context = context;
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final MySection2 item) {
        int image = 0;
        switch (item.header) {
            case "音乐课堂":
                image = imIds[0];
                break;
            case "精品教材":
            case "精品图书":
                image = imIds[1];
                break;
            case "精品习题":
                image = imIds[2];
                break;
            case "名师名家":
                image = imIds[3];
                break;
        }
        helper.setImageResource(R.id.im, image);
        helper.setText(R.id.name, item.header);
    }


    @Override
    protected void convert(final BaseViewHolder helper, MySection2 item) {
        BuyedBean.Data.CourseList courseList = item.t;
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION + courseList.getCourseImgPathVertical()).into((ImageView) helper.getView(R.id.im));
        helper.setText(R.id.name, courseList.getCourseName());
        helper.setText(R.id.beizhu, courseList.getCourseState());
        helper.setText(R.id.price, courseList.getResourceCount() + "讲/" + courseList.getCurrentPrice() + "元");
//        helper.addOnClickListener(R.id.right);
        helper.getView(R.id.content1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnItemClickListener().onItemClick(BuyedListAdapter.this, v, helper.getLayoutPosition());
            }
        });

    }
}
