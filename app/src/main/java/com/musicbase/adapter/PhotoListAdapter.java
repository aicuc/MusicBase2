package com.musicbase.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.YinkaDetailBean;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class PhotoListAdapter extends BaseQuickAdapter<DetailBean.DataBean.UserAvatarsBean.AvatarList, BaseViewHolder> {

    private Context context;
    private int selectPosition = -1;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public PhotoListAdapter(Context context, @Nullable List<DetailBean.DataBean.UserAvatarsBean.AvatarList> data) {
        super(R.layout.item_photo_list, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, DetailBean.DataBean.UserAvatarsBean.AvatarList item) {
        Glide.with(context).load(Preferences.RESOURCE_URL+item.getUserAvatar()).into((ImageView) helper.getView(R.id.iv));
//        if(item.getIsDefault()==1){
//            selectPosition = helper.getAdapterPosition();
//            helper.setVisible(R.id.layout,true);
//        }else{
//            helper.setVisible(R.id.layout,false);
//        }
    }
}


