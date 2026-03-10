package com.musicbase.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.DetailBean;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class RelationAdapter extends BaseQuickAdapter<DetailBean.DataBean.InformationList, BaseViewHolder> {

    private Context context;

    public RelationAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<DetailBean.DataBean.InformationList> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, DetailBean.DataBean.InformationList item) {
        helper.setImageResource(R.id.im, R.mipmap.information);
        helper.setText(R.id.content, item.getTitle());
        helper.setVisible(R.id.nodownload, false);
        helper.setVisible(R.id.free, false);
    }
}


