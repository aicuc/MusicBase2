package com.musicbase.ui.superplayer.playerview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicbase.R;
import com.tencent.rtmp.TXLog;


/**
 * Created by liyuejiao on 2018/7/3.
 * <p>
 * 超级播放器全屏控制界面
 */
public class TCVodControllerLarge extends TCVodControllerBase
        implements View.OnClickListener, TCVodMoreView.Callback, TCVodQualityView.Callback {
    private static final String TAG = "TCVodControllerLarge";
    private RelativeLayout mLayoutTop;
    private LinearLayout mLayoutBottom;
    private Context mContext;
    private ImageView mIvBack;
    private ImageView mIvPause;
//    private TextView mTvCurrent;
//    private TextView mTvDuration;
//    private SeekBar mSeekBarProgress;
    private TextView mTvQuality;
    private TextView mTvTitle;
//    private ImageView mIvDanmuku;
//    private ImageView mIvSnapshot;
    private ImageView mIvLock;
    private ImageView mIvMore;
    private TCVodQualityView mVodQualityView;
    private TCVodMoreView mVodMoreView;
//    private boolean mDanmukuOn;
    private boolean mFirstShow;
//    private LinearLayout mLayoutReplay;
    private TextView mTvBackToLive;
//    private ProgressBar mPbLiveLoading;

    public TCVodControllerLarge(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public TCVodControllerLarge(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public TCVodControllerLarge(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    /**
     * 显示播放控制界面
     */
    @Override
    void onShow() {
        mLayoutTop.setVisibility(View.VISIBLE);
        mLayoutBottom.setVisibility(View.VISIBLE);

        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏播放控制界面
     */
    @Override
    void onHide() {
        mLayoutTop.setVisibility(View.GONE);
        mLayoutBottom.setVisibility(View.GONE);
        mVodMoreView.setVisibility(View.GONE);
        mVodQualityView.setVisibility(View.GONE);

        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.GONE);
        }
    }

    /**
     * 更新点播播放进度
     */
//    public void updateVodVideoProgress() {
//        float curTime = mVodController.getCurrentPlaybackTime();
//        float durTime = mVodController.getDuration();
//
//        if (durTime > 0 && curTime <= durTime) {
//            float percentage = curTime / durTime;
//            if (percentage >= 0 && percentage <= 1) {
//                int progress = Math.round(percentage * mSeekBarProgress.getMax());
//                mSeekBarProgress.setProgress(progress);
//
//                if (durTime >= 0 && curTime <= durTime) {
//                    mTvCurrent.setText(TCUtils.formattedTime((long) curTime));
//                    mTvDuration.setText(TCUtils.formattedTime((long) durTime));
//                }
//            }
//        }
//    }

//    public void updateVideoProgress(long current, long duration) {
//        mTvCurrent.setText(TCUtils.formattedTime(current));
//        if (duration > 0) {
//            float percentage = current / duration;
//            if (percentage >= 0 && percentage <= 1) {
//                int progress = Math.round(percentage * mSeekBarProgress.getMax());
//                mSeekBarProgress.setProgress(progress);
//                mTvDuration.setText(TCUtils.formattedTime(current);
//            }
//        }
//    }

    /**
     * 进度定时器
     */
//    @Override
//    void onTimerTicker() {
//        switch (mPlayType) {
//            case SuperPlayerConst.PLAYTYPE_VOD:
//                updateVodVideoProgress();
//                break;
//            case SuperPlayerConst.PLAYTYPE_LIVE:
//                mTvCurrent.setText(TCUtils.formattedTime(mLivePlayTime));
//                break;
//            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
//                mTvCurrent.setText(TCUtils.formattedTime(mLiveShiftTime));
//                break;
//        }
//    }

    private void initViews(Context context) {
        mContext = context;
        mLayoutInflater.inflate(R.layout.vod_controller_large, this);

        mLayoutTop = (RelativeLayout) findViewById(R.id.layout_top);
        mLayoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
        mLayoutReplay = (LinearLayout) findViewById(R.id.layout_replay);

        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvLock = (ImageView) findViewById(R.id.iv_lock);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvPause = (ImageView) findViewById(R.id.iv_pause);
//        mIvDanmuku = (ImageView) findViewById(R.id.iv_danmuku);
        mIvMore = (ImageView) findViewById(R.id.iv_more);
//        mIvSnapshot = (ImageView) findViewById(R.id.iv_snapshot);
        mTvCurrent = (TextView) findViewById(R.id.tv_current);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mSeekBarProgress = (SeekBar) findViewById(R.id.seekbar_progress);
        mSeekBarProgress.setProgress(0);
        mSeekBarProgress.setMax(100);
        mTvQuality = (TextView) findViewById(R.id.tv_quality);
        mTvBackToLive = (TextView) findViewById(R.id.tv_backToLive);
        mPbLiveLoading = (ProgressBar) findViewById(R.id.pb_live);

        mVodQualityView = (TCVodQualityView) findViewById(R.id.vodQualityView);
        mVodQualityView.setCallback(this);
        mVodMoreView = (TCVodMoreView) findViewById(R.id.vodMoreView);
        mVodMoreView.setCallback(this);

        mTvBackToLive.setOnClickListener(this);
        mLayoutReplay.setOnClickListener(this);
        mIvLock.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mIvPause.setOnClickListener(this);
//        mIvDanmuku.setOnClickListener(this);
//        mIvSnapshot.setOnClickListener(this);
        mIvMore.setOnClickListener(this);
        mTvQuality.setOnClickListener(this);
        mSeekBarProgress.setOnSeekBarChangeListener(this);

        if (mDefaultVideoQuality != null) {
            mTvQuality.setText(mDefaultVideoQuality.title);
        }

        mGestureProgressLayout = (SuperPlayerProgressLayout)findViewById(R.id.gesture_progress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:       // 返回
            case R.id.tv_title:
                mVodController.onBackPress(SuperPlayerConst.PLAYMODE_FULLSCREEN);
                break;
            case R.id.iv_pause:      // 暂停/播放
                changePlayState();
                break;
//            case R.id.iv_danmuku:    // 弹幕
//                toggleDanmu();
//                break;
//            case R.id.iv_snapshot:   // 截屏
//                mVodController.onSnapshot();
//                break;
            case R.id.iv_more:       // 显示更多
                showMoreView();
                break;
            case R.id.tv_quality:    // 清晰度选择
                showQualityView();
                break;
            case R.id.iv_lock:       // 锁屏
                changeLockState();
                break;
            case R.id.layout_replay: // 重播
                replay();
                break;
            case R.id.tv_backToLive: // 返回直播
                backToLive();
                break;
        }
    }

    /**
     * 返回直播
     */
    private void backToLive() {
        mVodController.resumeLive();
    }

    /**
     * 重新播放
     */
    private void replay() {
        updateReplay(false);
        mVodController.onReplay();
    }

    /**
     * 改变锁屏状态
     */
    private void changeLockState() {
        mLockScreen = !mLockScreen;
        if (mLockScreen) {
            mIvLock.setImageResource(R.drawable.ic_player_lock);
            hide();
        } else {
            mIvLock.setImageResource(R.drawable.ic_player_unlock);
        }
    }

//    /**
//     * 打开/关闭 弹幕
//     */
//    private void toggleDanmu() {
//        mDanmukuOn = !mDanmukuOn;
//        if (mDanmukuOn) {
//            mIvDanmuku.setImageResource(R.drawable.ic_danmuku_on);
//        } else {
//            mIvDanmuku.setImageResource(R.drawable.ic_danmuku_off);
//        }
//        mVodController.onDanmuku(mDanmukuOn);
//    }

    /**
     * 切换播放状态
     */
    private void changePlayState() {
        // 播放中
        if (mVodController.isPlaying()) {
            mVodController.pause();
            show();
        }
        // 未播放
        else if (!mVodController.isPlaying()) {
            updateReplay(false);
            mVodController.resume();
            show();
        }
    }

    /**
     * 显示右侧更多设置
     */
    private void showMoreView() {
        mVodMoreView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示多分辨率UI
     */
    private void showQualityView() {
        if (mVideoQualityList == null || mVideoQualityList.size() == 0) {
            TXLog.i(TAG, "showQualityView mVideoQualityList null");
            return;
        }
        // 设置默认显示分辨率文字
        mVodQualityView.setVisibility(View.VISIBLE);
        if (!mFirstShow) {
            mVodQualityView.setDefaultSelectedQuality(mVideoQualityList.size() - 1);
            mFirstShow = true;
        }
        mVodQualityView.setVideoQualityList(mVideoQualityList);
    }

//    @Override
//    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        // 拖动seekbar结束时,获取seekbar当前进度,进行seek操作,最后更新seekbar进度
//        int curProgress = seekBar.getProgress();
//        int maxProgress = seekBar.getMax();
//
//        switch (mPlayType) {
//            case SuperPlayerConst.PLAYTYPE_VOD:
//                if (curProgress >= 0 && curProgress < maxProgress) {
//                    // 关闭重播按钮
//                    updateReplay(false);
//                    float percentage = ((float) curProgress) / maxProgress;
//                    int position = (int) (mVodController.getDuration() * percentage);
//                    mVodController.seekTo(position);
//                    mVodController.resume();
//                }
//                break;
//            case SuperPlayerConst.PLAYTYPE_LIVE:
//            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
//                updateLiveLoadingState(true);
////                mTrackTime = mLivePlayTime * curProgress / maxProgress;
//                TXCLog.i(TAG, "onStopTrackingTouch time:" + mTrackTime);
////                mVodController.onBackToRecord(mLiveBaseTime, mTrackTime);
//                break;
//        }
//    }

    /**
     * 更新直播加载ProgressBar状态
     *
     * @param loading
     */
//    public void updateLiveLoadingState(boolean loading) {
//        if (loading) {
//            mPbLiveLoading.setVisibility(View.VISIBLE);
//        } else {
//            mPbLiveLoading.setVisibility(View.GONE);
//        }
//    }

    /**
     * 更新默认清晰度
     *
     * @param videoQulity
     */
    public void updateVideoQulity(TCVideoQulity videoQulity) {
        mDefaultVideoQuality = videoQulity;
        if (mTvQuality != null) {
            mTvQuality.setText(videoQulity.title);
        }
        mVodQualityView.setDefaultSelectedQuality(mDefaultVideoQuality.index);
    }

    /**
     * 更新播放UI
     *
     * @param isStart
     */
    public void updatePlayState(boolean isStart) {
        // 播放中
        if (isStart) {
            mIvPause.setImageResource(R.drawable.ic_vod_pause_normal);
        }
        // 未播放
        else {
            mIvPause.setImageResource(R.drawable.ic_vod_play_normal);
        }
    }

    /**
     * 更新标题
     *
     * @param title
     */
    public void updateTitle(String title) {
        super.updateTitle(title);
        mTvTitle.setText(mTitle);
    }

    /**
     * 更新重新播放按钮状态
     *
     * @param replay
     */
//    public void updateReplay(boolean replay) {
//        if (replay) {
//            mLayoutReplay.setVisibility(View.VISIBLE);
//        } else {
//            mLayoutReplay.setVisibility(View.GONE);
//        }
//    }

    /**
     * 更新直播播放时间和进度
     *
     * @param baseTime
     */
//    public void updateLivePlayTime(long baseTime) {
//        super.updateLivePlayTime(baseTime);
//        mTvCurrent.setText(TCUtils.formattedTime(mLivePlayTime));
//    }

    /**
     * 更新直播回看播放时间
     *
     * @param liveshiftTime
     */
//    public void updateLiveShiftPlayTime(long liveshiftTime) {
//        super.updateLiveShiftPlayTime(liveshiftTime);
//        mTvCurrent.setText(TCUtils.formattedTime(mLiveShiftTime));
//    }

    /**
     * 更新播放类型
     *
     * @param playType
     */
    public void updatePlayType(int playType) {
        super.updatePlayType(playType);
        switch (playType) {
            case SuperPlayerConst.PLAYTYPE_VOD:
                mTvBackToLive.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerConst.PLAYTYPE_VOD);
                mTvDuration.setVisibility(View.VISIBLE);
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE);
                mSeekBarProgress.setProgress(100);
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
                mTvBackToLive.setVisibility(View.VISIBLE);
                mTvDuration.setVisibility(View.GONE);
                mVodMoreView.updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE_SHIFT);
                break;
        }
    }

    @Override
    public void onQualitySelect(TCVideoQulity quality) {
        mVodController.onQualitySelect(quality);
        mVodQualityView.setVisibility(View.GONE);
    }

    @Override
    public void onSpeedChange(float speedLevel) {
        mVodController.onSpeedChange(speedLevel);
    }

    @Override
    public void onMirrorChange(boolean isMirror) {
        mVodController.onMirrorChange(isMirror);
    }

    @Override
    public void onHWAcceleration(boolean isAccelerate) {
        mVodController.onHWAcceleration(isAccelerate);
    }


}
