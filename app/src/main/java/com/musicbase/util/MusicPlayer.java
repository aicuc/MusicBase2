package com.musicbase.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.musicbase.entity.RecordBean;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXPlayerAuthBuilder;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.musicbase.preferences.Preferences.DEFAULT_APPID;
import static com.tencent.rtmp.TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_BEGIN;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_END;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_PROGRESS;

public class MusicPlayer {
    List<String> audioList;
    private TXCloudVideoView mView;
    private TXVodPlayer mVodPlayer;
    private Context context;
    private int positionPlaying = 0;
    int beginReordTime, allowTime;

    public MusicPlayer(Context context, List<String> audioList, TXCloudVideoView mView, int beginReordTime, int allowTime) {
        this.audioList = audioList;
        this.mView = mView;
        this.context = context;
        this.beginReordTime = beginReordTime;
        this.allowTime = allowTime;
    }

    public void start() {
        initSuperVodGlobalSetting();
        positionPlaying = 0;
        isFirst = true;
        playPosition();
    }

    /**
     * 初始化超级播放器全局配置
     */
    boolean isFirst = true;

    private void initSuperVodGlobalSetting() {
        if (mVodPlayer == null) {
            mVodPlayer = new TXVodPlayer(context);

            TXVodPlayConfig mConfig = new TXVodPlayConfig();
            mConfig.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");

            //指定本地最多缓存多少文件，避免缓存太多数据
            mConfig.setMaxCacheItems(10);
            mVodPlayer.setConfig(mConfig);
            mVodPlayer.setPlayerView(mView);
            mVodPlayer.setVodListener(new ITXVodPlayListener() {
                @Override
                public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
                    if (event == PLAY_EVT_PLAY_PROGRESS) {
                        if (positionPlaying == audioList.size() - 1) {//最后一个音频
                            int durationAll = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                            // 播放进度, 单位是秒
                            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
//                            int s = (progress % 3600) % 60;
                            //                            if (beginReordTime >= 3) {
                            //                                if (s == beginReordTime - 3) {//倒计时
                            //                                    EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_DAOJISHI, 0, allowTime));
                            //                                }
                            //                            } else {
                            if (isFirst){//限制 只发送一次
                                EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_DAOJISHI, beginReordTime, allowTime));
                                isFirst = false;
                                if(durationAll<beginReordTime)
                                    EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_DAOJISHI_BEGIN, beginReordTime, allowTime));
                            }
                            if (progress == beginReordTime) {//开始录制
                                EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_BEGIN, beginReordTime, allowTime));
                            }
                        }
                    }
                    if (event == PLAY_EVT_PLAY_END) {
                        if (positionPlaying == audioList.size() - 1)
                            onDestroy();
                        else
                            setNextPosition();
                    }
                    if (event == PLAY_EVT_PLAY_BEGIN) {

                    }
                    if (event == PLAY_ERR_FILE_NOT_FOUND) {
                        ActivityUtils.showToast(context, "该文件已被删除，请重新下载！");
                    }
                }

                @Override
                public void onNetStatus(TXVodPlayer txVodPlayer, Bundle param) {


                }
            });
        }
    }

    private void playPosition() {
        playDefaultVideo(DEFAULT_APPID, audioList.get(positionPlaying));
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

    private void setNextPosition() {
        // 顺序播放
        if (audioList != null && audioList.size() > 0) {
            positionPlaying++;
            // 屏蔽非法值
            if (positionPlaying > audioList.size() - 1) {
                positionPlaying = 0;
            }
        }
        playPosition();

    }

    public void onDestroy() {
        if (mVodPlayer != null)
            mVodPlayer.stopPlay(true);
        if (mView != null)
            mView.onDestroy();
    }
}
