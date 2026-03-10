package com.musicbase.adapter;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.LogoutReasonBean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class LogoutReasonAdapter extends BaseQuickAdapter<LogoutReasonBean.DataBean, BaseViewHolder> {

    private LinkedList<LogoutReasonBean.DataBean> resultList = new LinkedList<>();
    public LogoutReasonAdapter(List<LogoutReasonBean.DataBean> data) {
        super(R.layout.item_logout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LogoutReasonBean.DataBean item) {
        helper.setText(R.id.tv,item.getReason());
        helper.setOnCheckedChangeListener(R.id.cb, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    resultList.add(item);
                else{
                    resultList.remove(item);
                }
            }
        });
    }

    public List<LogoutReasonBean.DataBean> getResultList() {
        return resultList;
    }
}


