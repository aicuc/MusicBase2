package com.musicbase.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.entity.DocInfo;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class CacheAdapter extends BaseQuickAdapter<DocInfo, BaseViewHolder> {

    private Context context;

    public CacheAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<DocInfo> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, DocInfo item) {
        helper.setText(R.id.tv_name, item.getBookName());
        if(item.getDownloadProgress()==100){
            helper.setVisible(R.id.pb_syn,false);
            helper.setVisible(R.id.iv_status,false);
        }else{
            helper.setVisible(R.id.pb_syn,true);
            helper.setVisible(R.id.iv_status,true);
            helper.setProgress(R.id.pb_syn,item.getDownloadProgress());
            helper.addOnClickListener(R.id.iv_status);
            if (item.getStatus() == DataBaseFiledParams.LOADING) {
                helper.setImageResource(R.id.iv_status,R.mipmap.doing_click);
            } else {
                helper.setImageResource(R.id.iv_status,R.mipmap.stop_click);
            }
        }
        helper.addOnClickListener(R.id.iv_cancel);
    }
}


