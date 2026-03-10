package com.musicbase.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.FirstBean;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class BtnListAdapter extends BaseQuickAdapter<FirstBean.Data.NavigationBars, BaseViewHolder> {

    private Context context;
    public BtnListAdapter(Context context,@LayoutRes int layoutResId, @Nullable List<FirstBean.Data.NavigationBars> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, FirstBean.Data.NavigationBars item) {
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getIconPath()).into((ImageView) helper.getView(R.id.im));
        helper.setText(R.id.name, item.getIconName());
    }
}


