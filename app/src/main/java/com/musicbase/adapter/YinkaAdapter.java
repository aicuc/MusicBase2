package com.musicbase.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.YinkaBean;
import com.musicbase.entity.YinkaDetailBean;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class YinkaAdapter extends BaseQuickAdapter<YinkaDetailBean.DataBean.CourseListBean, BaseViewHolder> {

    private Context context;
    public YinkaAdapter(Context context, @Nullable List<YinkaDetailBean.DataBean.CourseListBean> data) {
        super(R.layout.item_third_fragment, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, YinkaDetailBean.DataBean.CourseListBean item) {
        TextView tv = helper.getView(R.id.name);
        tv.setText(item.getSystemcode_name());
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getIcon_light()).into((ImageView) helper.getView(R.id.icon));
        helper.setText(R.id.beizhu,item.getCourseName());
        Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION+item.getCourseImgPathHorizontal()).into((ImageView) helper.getView(R.id.im));
    }
}


