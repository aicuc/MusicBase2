package com.musicbase.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musicbase.R;
import com.musicbase.entity.BookVOBean;
import com.musicbase.preferences.Preferences;

import java.io.File;
import java.util.List;

public class BookAdapter extends WZYBaseAdapter<BookVOBean.DataBean> {
    private final Context context;

    public BookAdapter(List<BookVOBean.DataBean> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
        this.context = context;
    }

    @Override
    public void bindData(ViewHolder holder, BookVOBean.DataBean item, int indexPostion) {
        // 书籍封面获取
        ImageView imageView01 = (ImageView) holder.getView(R.id.iv_book1);
        String image_url = Preferences.IMAGE_HTTP_LOCATION + item.getCourseImgPathVertical();
            Glide
                    .with(context)
                    .load(image_url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.mipmap.zhibo_default)
                    .into(imageView01);


        ImageView iv_timeLimit = (ImageView) holder.getView(R.id.iv_timeLimit);

        if(item.getDeadlineType()==0){
            iv_timeLimit.setVisibility(View.GONE);
        }else{
            iv_timeLimit.setVisibility(View.VISIBLE);
            if(item.getIsOverdue()==0)
                iv_timeLimit.setBackgroundResource(R.drawable.time_);
            else
                iv_timeLimit.setBackgroundResource(R.drawable.time_over);
        }

        if(item.getAllowOpen()==0){
            holder.getView(R.id.layout_meng).setVisibility(View.VISIBLE);
        }else
            holder.getView(R.id.layout_meng).setVisibility(View.GONE);
    }


}
