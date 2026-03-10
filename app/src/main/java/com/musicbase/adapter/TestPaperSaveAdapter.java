package com.musicbase.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.download2.db.DataBaseFiledParams;
import com.musicbase.download2.entity.DocInfo;

import java.io.File;
import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class TestPaperSaveAdapter extends BaseItemDraggableAdapter<File, BaseViewHolder> {

    private Context context;

    public TestPaperSaveAdapter(Context context, @Nullable List<File> data) {
        super(R.layout.item_testpaper_save, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        String imageUrl = item.getParent()+File.separator+item.getName().replace(".mp4",".jpg");
        Log.e("imageUrl","imageUrl===="+imageUrl);
        Glide.with(context).load(new File(imageUrl)).into((ImageView) helper.getView(R.id.iv_photo));
        helper.setText(R.id.tv_name, item.getName());
        helper.addOnClickListener(R.id.layout_photo);
    }
}


