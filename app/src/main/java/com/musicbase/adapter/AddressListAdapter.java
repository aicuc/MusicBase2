package com.musicbase.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;

import java.util.List;

import static com.musicbase.entity.AddressBean.*;

public class AddressListAdapter extends BaseQuickAdapter<DataBean, BaseViewHolder> {
    public AddressListAdapter(int layoutResId, @Nullable List<DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DataBean item) {
        helper.setText(R.id.address_name, item.getUserName())
                .setText(R.id.address_phone, item.getTelphone())
                .addOnClickListener(R.id.address_edit)
                .setText(R.id.address_text, item.getAreaName() + item.getAddress());
        if (item.getIsDefault().equalsIgnoreCase("1")) {
            helper.setVisible(R.id.address_default, true);
        } else {
            helper.setVisible(R.id.address_default, false);
        }


    }

}
