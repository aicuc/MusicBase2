package com.musicbase.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.model.recyclerviewmodel.MultipleItem;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class FirstAdapter extends BaseMultiItemQuickAdapter<MultipleItem,BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    private Context context;
    public FirstAdapter(Context context, List<MultipleItem> data) {
        super(data);
        this.context = context;
        addItemType(MultipleItem.HEAD, R.layout.item_head);
        addItemType(MultipleItem.CONTENT_1, R.layout.item_content_1);
        addItemType(MultipleItem.CONTENT_3, R.layout.item_content_3);
    }



    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        switch (helper.getItemViewType()) {
            case MultipleItem.HEAD:
                Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getImId()).into((ImageView) helper.getView(R.id.im));
                helper.setText(R.id.name, item.getContent());
                break;
            case MultipleItem.CONTENT_1:
                Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getBean().getCourseImgPathVertical()).into((ImageView) helper.getView(R.id.im));
                helper.setText(R.id.name, item.getBean().getCourseName());
                break;
            case MultipleItem.CONTENT_3:
                Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getBean().getCourseImgPathHorizontal()).into((ImageView) helper.getView(R.id.im));
                break;
        }
    }
}
