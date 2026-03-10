package com.musicbase.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.entity.YinkaDetailBean;
import com.musicbase.preferences.Preferences;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class ScoreInfoAdapter extends BaseQuickAdapter<TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean.ScoreInfoBean, BaseViewHolder> {

    private Context context;
    private int topicGrade;
    public ScoreInfoAdapter(Context context, @Nullable List<TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean.ScoreInfoBean> data,int topicGrade) {
        super(R.layout.item_scoreinfo, data);
        this.context = context;
        this.topicGrade = topicGrade;
    }

    @Override
    protected void convert(BaseViewHolder helper, TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean.ScoreInfoBean item) {
        helper.setText(R.id.tv,item.getCriterionName()+"：");
        RatingBar ratingBar = helper.getView(R.id.ratingbar);
        ratingBar.setNumStars(topicGrade);
        ratingBar.setStepSize(1);
        ratingBar.setRating(Integer.parseInt(item.getUserScore()));
    }
}


