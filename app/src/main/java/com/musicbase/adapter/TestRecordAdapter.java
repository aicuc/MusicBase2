package com.musicbase.adapter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.SubmitAgainBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.preferences.Preferences;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by BAO on 2018-09-14.
 */

public class TestRecordAdapter extends BaseQuickAdapter<TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean, BaseViewHolder> {

    private Context context;
    private int topicGrade;
    private CallBack callBack;

    public int getTopicGrade() {
        return topicGrade;
    }

    public void setTopicGrade(int topicGrade) {
        this.topicGrade = topicGrade;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void playVideo(String path);
    }

    public TestRecordAdapter(Context context, @Nullable List<TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean> data) {
        super(R.layout.item_test_paper, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean item) {
        setScoreViewData(helper, item);
        helper.getView(R.id.btn_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.playVideo(item.getSavePath());
            }
        });

    }

    private void setScoreViewData(BaseViewHolder helper, TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean bean) {
        if (!TextUtils.isEmpty(bean.getUserAvatar()))
            Glide.with(context).load(Preferences.IMAGE_HTTP_LOCATION + bean.getUserAvatar()).into((ImageView) helper.getView(R.id.iv_photo));
        //审核状态
        String checkResult = "";
        switch (bean.getCheckState()) {
            case 0:
                checkResult = "未审核";
                break;
            case 1:
                checkResult = "审核通过";
                break;
            case 2:
                //                    if (bean.getJudgeState() == 0)
                checkResult = "审核失败";
                //                    else if (bean.getJudgeState() == 1) {
                //                        checkResult = "审核失败，" + bean.getScoreAppraisal();
                //                    }
                break;
            default:
                checkResult = "未审核";
                break;
        }
        helper.setText(R.id.tv_state, "审核状态：" + checkResult);
        //上传时间
        helper.setText(R.id.tv_time, "上传时间：" + bean.getAddTime());
        helper.setText(R.id.tv_result, bean.getScoreAppraisal());
        if (bean.getJudgeState() == 1&&bean.getCheckState() == 1) {
            helper.setVisible(R.id.layout_scoreinfo, true);
            helper.setText(R.id.tv_average, bean.getAverageScore());
            RecyclerView recyclerView = ((RecyclerView) helper.getView(R.id.recyclerView));
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setNestedScrollingEnabled(false);
            ScoreInfoAdapter adapter = new ScoreInfoAdapter(context, bean.getScoreInfo(), topicGrade);
            ((RecyclerView) helper.getView(R.id.recyclerView)).setAdapter(adapter);
        }

        if (bean.getCheckState() == 2) {
            helper.setVisible(R.id.btn_resubmit, true);
            helper.getView(R.id.btn_resubmit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new SubmitAgainBean(helper.getAdapterPosition()));
                }
            });
        } else {
            helper.setVisible(R.id.btn_resubmit, false);
        }
    }
}


