package com.musicbase.ui.view;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.musicbase.util.ActivityUtils;

/**
 * Created by BAO on 2018-09-14.
 */

public class BtnListItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;

    public BtnListItemDecoration(Context context) {
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int width = ActivityUtils.dip2px(context, 60);
        int offset = (ActivityUtils.getScreenWidth(context) - 60 * 2 - width * 4) / 3 - 6;
//        if (parent.getChildLayoutPosition(view)% 4 != 0) {
        outRect.left = offset / 2;
        outRect.right = offset / 2;
//                    (width+offset)*(parent.getChildLayoutPosition(view)% 4);
//            outRect.right = (width+offset)*(parent.getChildLayoutPosition(view)% 4)+width;
//        }
//        view.x
    }
}
