package com.musicbase.adapter;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.OrderRecordBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BuyRecordAdapter extends BaseQuickAdapter<OrderRecordBean.DataBean, BaseViewHolder> {
    public BuyRecordAdapter(int layoutResId, @Nullable List<OrderRecordBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderRecordBean.DataBean item) {
        helper.setText(R.id.order_title, item.getBodyName())
                .setText(R.id.order_number, "订单编号：" + item.getAttachCode())
                .setText(R.id.price_order, "¥" + item.getRealPrice());
        if (item != null && item.getPayTime() != null && item.getPayTime().getTime() > 0) {
            helper.setText(R.id.time_order, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(String.valueOf(item.getPayTime().getTime())))));
        }
    }
}
