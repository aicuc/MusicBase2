package com.musicbase.adapter;

import android.content.Context;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.entity.SongSore;

import java.util.List;

public class SongScoreAdapter extends WZYBaseAdapter<SongSore> {
    public SongScoreAdapter(List<SongSore> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, SongSore songSore, int indexPostion) {

        TextView name = (TextView) holder.getView(R.id.name);
        TextView account = (TextView) holder.getView(R.id.account);
        name.setText(String.valueOf(indexPostion + 1));
        account.setText(songSore.getName());

    }
}
