package com.musicbase.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.musicbase.R;
import com.musicbase.entity.DetailBean;
import com.musicbase.model.recyclerviewmodel.MultipleItem_Module;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.BrowerActivity;
import com.musicbase.ui.activity.DetailActivity;
import com.musicbase.ui.superplayer.utils.TCUtils;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXPlayerAuthBuilder;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;

import static com.musicbase.preferences.Preferences.DEFAULT_APPID;
import static com.tencent.rtmp.TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_BEGIN;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_PROGRESS;

public class ModuleAdapter extends BaseMultiItemQuickAdapter<MultipleItem_Module, BaseViewHolder> {
    private final String TAG = getClass().getSimpleName();
    private JzvdVideoView current_video;
    private TXVodPlayer current_music;
    private Map<Integer,Boolean> map = new HashMap<>();

    public JzvdVideoView getCurrent_video() {
        return current_video;
    }

    public TXVodPlayer getCurrent_music() {
        return current_music;
    }

    public void stopAVMedia() {
        if (current_music != null && current_music.isPlaying()) {
            current_music.stopPlay(true);
            current_music = null;
        }

        if (current_video != null) {
//            current_video.reset();
//            current_video.removeAllViews();
//            current_video = null;
        }
    }

    public ModuleAdapter(List<MultipleItem_Module> data) {
        super(data);
        addItemType(MultipleItem_Module.TYPE_TEXT, R.layout.item_module_text);
        addItemType(MultipleItem_Module.TYPE_IMG, R.layout.item_module_img);
        addItemType(MultipleItem_Module.TYPE_MUSIC, R.layout.item_module_music);
        addItemType(MultipleItem_Module.TYPE_VIDEO, R.layout.item_module_video);
        addItemType(MultipleItem_Module.TYPE_URL, R.layout.item_module_url);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem_Module item) {
        DetailBean.DataBean.ModuleList bean = item.getBean();
        helper.setText(R.id.name1, bean.getModuleName());
        switch (item.getItemType()) {
            case MultipleItem_Module.TYPE_TEXT:
                helper.setText(R.id.text1, bean.getModuleContent());
                final Object isExpand = map.get(helper.getLayoutPosition());
                if (isExpand!=null&&(boolean)isExpand) {
                    helper.setBackgroundRes(R.id.text_more, R.drawable.take_back);
                    ((TextView) helper.getView(R.id.text1)).setMaxLines(Integer.MAX_VALUE);
                } else {
                    helper.setBackgroundRes(R.id.text_more, R.drawable.see_more);
                    ((TextView) helper.getView(R.id.text1)).setMaxLines(3);
                }
                helper.getView(R.id.layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpand!=null&&(boolean)isExpand) {
                            map.put(helper.getLayoutPosition(),false);
                            helper.setBackgroundRes(R.id.text_more, R.drawable.see_more);
                            ((TextView) helper.getView(R.id.text1)).setMaxLines(3);
                        } else {
                            map.put(helper.getLayoutPosition(),true);
                            helper.setBackgroundRes(R.id.text_more, R.drawable.take_back);
                            ((TextView) helper.getView(R.id.text1)).setMaxLines(Integer.MAX_VALUE);
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
            case MultipleItem_Module.TYPE_IMG:
                Glide.with(mContext).load(Preferences.IMAGE_HTTP_LOCATION + bean.getModuleContent()).into((ImageView) helper.getView(R.id.image1));
                break;
            case MultipleItem_Module.TYPE_URL:
                helper.getView(R.id.layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.getModuleAllowOpenInBrowser() == 2) {
                            Intent intent6 = new Intent();
                            intent6.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(bean.getModuleContent());
                            intent6.setData(content_url);
                            mContext.startActivity(intent6);
                        } else {
                            Intent intent3 = new Intent(mContext, BrowerActivity.class);
                            intent3.putExtra("filePath", bean.getModuleContent());
                            intent3.putExtra("allowOpenInBrowser", bean.getModuleAllowOpenInBrowser());
                            intent3.putExtra("name", "");
                            mContext.startActivity(intent3);
                        }
                    }
                });
                break;
            case MultipleItem_Module.TYPE_VIDEO:
                JzvdVideoView videoView = helper.getView(R.id.video);
                videoView.setType(1);
                videoView.setUp(Preferences.IMAGE_HTTP_LOCATION + bean.getModuleContent(), "", Jzvd.SCREEN_NORMAL);
                videoView.setCallBack(new JzvdVideoView.CallBack() {
                    @Override
                    public void onStatePlaying() {
                        Log.e(TAG, "videoView onStatePlaying----path==" + Preferences.IMAGE_HTTP_LOCATION + bean.getModuleContent());
                    }

                    @Override
                    public void onStatePause() {
                        Log.e(TAG, "videoView onStatePause----");
                    }

                    @Override
                    public void onStatePreparing() {
                        Log.e(TAG, "videoView onStatePreparing----path==" + Preferences.IMAGE_HTTP_LOCATION + bean.getModuleContent());
                        stopAVMedia();
                        current_video = videoView;
                    }

                    @Override
                    public void onProgress(int progress, long position, long duration) {
                        Log.e(TAG,"videoView onProgress----progress="+progress+"-----position="+position+"-----duration="+duration);
                    }
                });
                videoView.startVideo();
                break;
            case MultipleItem_Module.TYPE_MUSIC:
                ImageButton btn = helper.getView(R.id.playAndPause);
                TextView tv_now = helper.getView(R.id.time_now);
                SeekBar seekBar = helper.getView(R.id.seekbar);
                TextView tv_total = helper.getView(R.id.time_all);
                TXCloudVideoView video_view = helper.getView(R.id.video_view);
                initSuperVodGlobalSetting(btn, tv_now, seekBar, tv_total, video_view);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(bean.getModuleContent())) {
                            ActivityUtils.showToast(mContext, "播放失败");
                            return;
                        }

                        if (mVodPlayer.isPlaying()) {
                            mVodPlayer.stopPlay(false);
                            btn.setBackgroundResource(R.drawable.iconmusic_07);
                        } else {
//                            if (mVodPlayer.getDuration() != 0.0F)
//                                mVodPlayer.resume();
//                            else
                                playDefaultVideo(DEFAULT_APPID, Preferences.IMAGE_HTTP_LOCATION + bean.getModuleContent());
                            btn.setBackgroundResource(R.drawable.iconmusic_06);
                        }
                    }
                });
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && mVodPlayer.isPlaying()) {
                            Log.e(TAG, "onProgressChanged----progress==" + progress * (int) mVodPlayer.getDuration() / 100 + "---getDuration==" + mVodPlayer.getDuration());
                            mVodPlayer.seek(progress * (int) mVodPlayer.getDuration() / 100);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                break;
        }
    }

    /**
     * 初始化超级播放器全局配置
     */
    TXVodPlayer mVodPlayer;
    private void initSuperVodGlobalSetting(ImageButton btn, TextView tv_now, SeekBar seekBar, TextView tv_total, TXCloudVideoView video_view) {
        if(mVodPlayer == null) {
            mVodPlayer = new TXVodPlayer(mContext);
            mVodPlayer.setPlayerView(video_view);
            mVodPlayer.setVodListener(new ITXVodPlayListener() {
                @Override
                public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
                    if (event == PLAY_EVT_PLAY_PROGRESS) {
                        if (mVodPlayer.isPlaying()) {
                            btn.setBackgroundResource(R.drawable.iconmusic_06);
                        } else {
                            btn.setBackgroundResource(R.drawable.iconmusic_07);
                        }

                        // 视频总长, 单位是秒
                        int durationAll = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                        // 可以用于设置时长显示等等
                        tv_total.setText(TCUtils.formattedTime(durationAll));
                        // 播放进度, 单位是秒
                        int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                        seekBar.setProgress(progress * 100 / durationAll);
                        tv_now.setText(TCUtils.formattedTime(progress));

                        // 加载进度, 单位是秒
                        int duration = param.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL);
                        seekBar.setSecondaryProgress(duration * 100 / durationAll);

                    }
                    if (event == PLAY_EVT_PLAY_BEGIN) {
                        stopAVMedia();
                        current_music = mVodPlayer;
                    }
                    if (event == PLAY_ERR_FILE_NOT_FOUND) {
                        ActivityUtils.showToast(mContext, "播放失败");
                    }

                }

                @Override
                public void onNetStatus(TXVodPlayer txVodPlayer, Bundle param) {


                }
            });
        }
//        return mVodPlayer;
    }

    private void playDefaultVideo(int appid, String fileid) {
        if (fileid.contains(".mp3") || fileid.contains(".wav")) {
            mVodPlayer.startPlay(fileid);
        } else {
            TXPlayerAuthBuilder authBuilder = new TXPlayerAuthBuilder();
            authBuilder.setAppId(appid);
            authBuilder.setFileId(fileid);
            mVodPlayer.startPlay(authBuilder);
        }
    }



}
