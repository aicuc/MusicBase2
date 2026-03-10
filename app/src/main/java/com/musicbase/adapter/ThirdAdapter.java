package com.musicbase.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.FirstBean;
import com.musicbase.entity.YinkaBean;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class ThirdAdapter extends BaseQuickAdapter<YinkaBean.DataBean.ColumnListBean, BaseViewHolder> {

    private Context context;
    public ThirdAdapter(Context context, @Nullable List<YinkaBean.DataBean.ColumnListBean> data) {
        super(R.layout.item_third_fragment, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, YinkaBean.DataBean.ColumnListBean item) {
        TextView tv = helper.getView(R.id.name);
        tv.setText(item.getSystemCodeName());
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getIconLight()).into((ImageView) helper.getView(R.id.icon));
        helper.setText(R.id.beizhu,item.getIntroduce());
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getCoverImg()).into((ImageView) helper.getView(R.id.im));
    }
}


