package com.musicbase.ui.superplayer.playerview;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.musicbase.R;
import com.musicbase.ui.superplayer.SuperPlayerModel;
import com.musicbase.ui.superplayer.utils.TCUtils;

import java.util.ArrayList;

/**
 * Created by liyuejiao on 2018/7/3.
 */

public class TCVodPlayerListAdapter extends RecyclerView.Adapter<TCVodPlayerListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<SuperPlayerModel> mSuperPlayerModelList;
    private OnItemClickLitener mOnItemClickLitener;
    private int current_position = -1;

    public TCVodPlayerListAdapter(Context context) {
        mContext = context;
        mSuperPlayerModelList = new ArrayList<SuperPlayerModel>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_new_vod, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SuperPlayerModel superPlayerModel = mSuperPlayerModelList.get(position);
//        Glide.with(mContext).load(superPlayerModel.placeholderImage).into(holder.thumb);
        if (superPlayerModel.duration > 0) {
            holder.duration.setText(TCUtils.formattedTime(superPlayerModel.duration));
        } else {
            holder.duration.setText("");
        }
        if (superPlayerModel.title != null) {
            if (position == current_position)
                holder.title.setTextColor(mContext.getResources().getColor(R.color.red_e61b19));
            else
                holder.title.setTextColor(Color.WHITE);
            holder.title.setText(superPlayerModel.title.replaceAll(".mp4", ""));
        }
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLitener != null) {
                    mOnItemClickLitener.onItemClick(position, superPlayerModel);
                }
            }
        });
//        holder.thumb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnItemClickLitener != null) {
//                    mOnItemClickLitener.onItemClick(position, superPlayerModel);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mSuperPlayerModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView duration;
        private TextView title;
//        private ImageView thumb;

        public ViewHolder(final View itemView) {
            super(itemView);
//            thumb = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.textview);
            duration = (TextView) itemView.findViewById(R.id.tv_duration);
        }
    }

    /**
     * 添加一个SuperPlayerModel
     *
     * @param superPlayerModel
     */
    public void addSuperPlayerModel(SuperPlayerModel superPlayerModel) {
        notifyItemInserted(mSuperPlayerModelList.size());
        mSuperPlayerModelList.add(superPlayerModel);
    }

    public void setOnItemClickLitener(OnItemClickLitener listener) {
        mOnItemClickLitener = listener;
    }

    public void clear() {
        mSuperPlayerModelList.clear();
    }

    public int getCurrent_position() {
        return current_position;
    }

    public void setCurrent_position(int current_position) {
        this.current_position = current_position;
    }

    public interface OnItemClickLitener {
        void onItemClick(int position, SuperPlayerModel superPlayerModel);
    }

}
