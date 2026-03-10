package com.musicbase.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicbase.MyApplication;
import com.musicbase.R;
import com.musicbase.adapter.AudioAdapter;
import com.musicbase.entity.AudioDaoUtils;
import com.musicbase.entity.AudioItem;
import com.musicbase.ui.superplayer.utils.TCUtils;
import com.musicbase.ui.view.DrawableVerticalButton;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DensityUtil;
import com.musicbase.util.SPUtility;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXPlayerAuthBuilder;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.musicbase.preferences.Preferences.DEFAULT_APPID;
import static com.tencent.rtmp.TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_BEGIN;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_END;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_PROGRESS;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MusicActivity";

    private ImageButton button;
    private TextView title_music;
    private TextView music_speed;
    private TextView title_second;
    private SeekBar seekbar;
    private TextView time_now;
    private TextView time_all;
    private DrawableVerticalButton refreshMusic;
    private ImageButton beforeMusic;
    private ImageButton playAndPause;
    private ImageButton nextMusic;
    private ImageButton circleMode;
    private ImageView image_view;
    private ImageButton musicList;
    private DrawableVerticalButton shareMusic;
    private DrawableVerticalButton likeMusic;
    private DrawableVerticalButton downloadMusic;
    private TXCloudVideoView mView;
    private String field;
    private String resourceFileName;
    private String courseName;
    private TXVodPlayer mVodPlayer;
    private int allTime;
    private Dialog dialog;
    private float nowRate = 1.0f;

    private AudioDaoUtils audioDaoUtils;
    private List<AudioItem> audioItems;

    private int positionPlaying;

    public static final int REPEATE_NORMAL = 0;//顺序播放

    public static final int REPEATE_CURRENT = 1;//单曲循环

    public static final int RANDOM_ALL = 2;//随机播放

    private int playMode = REPEATE_NORMAL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        MyApplication.addMusicActivity(this);
        initView();
        getIntentData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();

    }

    private void getIntentData() {
        checkPermission();
        initSuperVodGlobalSetting();
        if (TextUtils.isEmpty(getIntent().getStringExtra("fileId"))) {
            initMusic();
            return;
        }
        field = getIntent().getStringExtra("fileId");
        resourceFileName = getIntent().getStringExtra("resourceFileName");
        courseName = getIntent().getStringExtra("courseName");


        initData();

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        button = (ImageButton) findViewById(R.id.button);
        title_music = (TextView) findViewById(R.id.title_music);
        music_speed = (TextView) findViewById(R.id.music_speed);
        title_second = (TextView) findViewById(R.id.title_second);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        time_now = (TextView) findViewById(R.id.time_now);
        time_all = (TextView) findViewById(R.id.time_all);
        image_view = (ImageView) findViewById(R.id.image_view);
        button.setOnClickListener(this);
        refreshMusic = (DrawableVerticalButton) findViewById(R.id.refreshMusic);
        refreshMusic.setOnClickListener(this);
        beforeMusic = (ImageButton) findViewById(R.id.beforeMusic);
        beforeMusic.setOnClickListener(this);
        playAndPause = (ImageButton) findViewById(R.id.playAndPause);
        playAndPause.setOnClickListener(this);
        nextMusic = (ImageButton) findViewById(R.id.nextMusic);
        nextMusic.setOnClickListener(this);
        circleMode = (ImageButton) findViewById(R.id.circleMode);
        circleMode.setOnClickListener(this);
        musicList = (ImageButton) findViewById(R.id.musicList);
        musicList.setOnClickListener(this);
        shareMusic = (DrawableVerticalButton) findViewById(R.id.shareMusic);
        shareMusic.setOnClickListener(this);
        likeMusic = (DrawableVerticalButton) findViewById(R.id.likeMusic);
        likeMusic.setOnClickListener(this);
        downloadMusic = (DrawableVerticalButton) findViewById(R.id.downloadMusic);
        downloadMusic.setOnClickListener(this);
        mView = (TXCloudVideoView) findViewById(R.id.video_view);
        audioDaoUtils = new AudioDaoUtils(this);

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button.setImageDrawable(null);

                } else {
                    button.setImageResource(R.drawable.iconmusic_03);
                }
                return false;
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mVodPlayer.isPlaying()) {
                    mVodPlayer.seek(progress * allTime / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initData() {

        playDefaultVideo(DEFAULT_APPID, field);
        AudioItem audioItem = new AudioItem();
        audioItem.setField(field);
        audioItem.setTitle(resourceFileName);
        audioItem.setCourseName(courseName);
        audioDaoUtils.updateOrInsertBase(audioItem);

        title_music.setText(resourceFileName);
        title_second.setText(courseName);
        initMusic();
    }

    private void initMusic() {
        audioItems = audioDaoUtils.queryAudioItems();
        Collections.reverse(audioItems);
        positionPlaying = 0;
        playMode = SPUtility.getSPInteger(this, "playMode");
        showMode();
    }


    /**
     * 初始化超级播放器全局配置
     */
    private void initSuperVodGlobalSetting() {
//        // 播放器配置
//        SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
//// 开启悬浮窗播放
//        prefs.enableFloatWindow = true;
////设置悬浮窗的初始位置和宽高
//        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
//        rect.x = 0;
//        rect.y = 0;
//        rect.width = 10;
//        rect.height = 10;
// ...其他配置
        if (mVodPlayer == null) {
            mVodPlayer = new TXVodPlayer(this);

            TXVodPlayConfig mConfig = new TXVodPlayConfig();
            mConfig.setCacheFolderPath(
                    Environment.getExternalStorageDirectory().getPath() + "/txcache");

            //指定本地最多缓存多少文件，避免缓存太多数据
            mConfig.setMaxCacheItems(10);
            mVodPlayer.setConfig(mConfig);
            mVodPlayer.setPlayerView(mView);
            mVodPlayer.setVodListener(new ITXVodPlayListener() {
                @Override
                public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {
                    if (event == PLAY_EVT_PLAY_PROGRESS) {
                        if (mVodPlayer.isPlaying()) {
                            playAndPause.setImageResource(R.drawable.iconmusic_06);
                        } else {
                            playAndPause.setImageResource(R.drawable.iconmusic_07);
                        }

                        // 视频总长, 单位是秒
                        int durationAll = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);
                        if(durationAll==0)
                            return;
                        // 可以用于设置时长显示等等
                        time_all.setText(TCUtils.formattedTime(durationAll));

                        allTime = durationAll;

                        // 播放进度, 单位是秒
                        int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
                        seekbar.setProgress(progress * 100 / durationAll);
                        time_now.setText(TCUtils.formattedTime(progress));

                        // 加载进度, 单位是秒
                        int duration = param.getInt(TXLiveConstants.NET_STATUS_AV_PLAY_INTERVAL);
                        seekbar.setSecondaryProgress(duration * 100 / durationAll);

                    }
                    if (event == PLAY_EVT_PLAY_BEGIN)
                        mVodPlayer.setRate(nowRate);
                    if (event == PLAY_ERR_FILE_NOT_FOUND) {
                        ActivityUtils.showToast(getApplicationContext(), "该文件已被删除，请重新下载！");
                    }
                    if(event == PLAY_EVT_PLAY_END){
                        setNextPosition();
                    }
//                    ActivityUtils.showToast(MusicActivity.this, "" + event);


                }

                @Override
                public void onNetStatus(TXVodPlayer txVodPlayer, Bundle param) {


                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                //后期变成在服务中进行播放
                moveTaskToBack(true);
                break;
            case R.id.beforeMusic:
                setPrePosition();
                break;
            case R.id.playAndPause:
                if (TextUtils.isEmpty(field)) {
                    ActivityUtils.showToast(getApplicationContext(), "当前没有文件!");
                    return;
                }
                if (mVodPlayer.isPlaying()) {
                    mVodPlayer.pause();
                    playAndPause.setImageResource(R.drawable.iconmusic_07);
                } else {
                    mVodPlayer.resume();
                    playAndPause.setImageResource(R.drawable.iconmusic_06);
                }
                break;
            case R.id.nextMusic:
                setNextPosition();
                break;
            case R.id.circleMode:
                setPlayMode();
                break;
            case R.id.musicList:
                showDialog();
                break;
        }
    }

    private void setPlayMode() {

        if (++playMode > RANDOM_ALL)
            playMode = REPEATE_NORMAL;

        SPUtility.putSPInteger(this, "playMode", playMode);
        showMode();
    }

    private void showMode() {
        switch (playMode) {
            case REPEATE_NORMAL:
                circleMode.setImageResource(R.drawable.iconmusic_30);
                break;
            case REPEATE_CURRENT:
                circleMode.setImageResource(R.drawable.iconmusic_38);
                break;
            case RANDOM_ALL:
                circleMode.setImageResource(R.drawable.iconmusic_35);
                break;
        }
    }


    public void showDialog() {
        dialog = new Dialog(this, R.style.my_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.list_bottom, null);
        ListView listView = (ListView) dialogView.findViewById(R.id.listview);
        final AudioAdapter adapter;
        adapter = new AudioAdapter(this, audioItems, positionPlaying);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                positionPlaying = position;

                playPosition();

                adapter.setIndex(position);
                adapter.notifyDataSetChanged();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //删除歌曲方法
                new AlertDialog.Builder(MusicActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("删除音乐")
                        .setMessage(String.format("您是否将%s音乐删除？", audioItems.get(position).getTitle()))
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                audioDaoUtils.deleteAudioItem(audioItems.get(position).getField());
                                audioItems = audioDaoUtils.queryAudioItems();
                                Collections.reverse(audioItems);
//                                    db.delete(MusicListBean.class, WhereBuilder.b("list_id", "=", musicLists.get(position).getId()));
//                                    musicLists = db.findAll(MusicList.class);

                                adapter.updateRes(audioItems);
                                ActivityUtils.showToast(getApplicationContext(), "删除完毕！");
                                if (position == positionPlaying) {
                                    title_music.setText("");
                                    title_second.setText("");
                                    mVodPlayer.stopPlay(true);
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });
        dialog.setContentView(dialogView);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 300));
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimTheme2); // 添加动画
        dialogWindow.setAttributes(lp);

        dialog.show();
    }

    private void playPosition() {
        field = audioItems.get(positionPlaying).getField();
        playDefaultVideo(DEFAULT_APPID, field);
        resourceFileName = audioItems.get(positionPlaying).getTitle();
        courseName = audioItems.get(positionPlaying).getCourseName();
        title_music.setText(resourceFileName);
        title_second.setText(courseName);
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
//        mVodPlayer.se


    }

    private void setNextPosition() {
        if (playMode == REPEATE_NORMAL) {
            // 顺序播放
            if (audioItems != null && audioItems.size() > 0) {
                positionPlaying++;
                // 屏蔽非法值
                if (positionPlaying > audioItems.size() - 1) {
                    positionPlaying = 0;
                }
            }
        } else if (playMode == RANDOM_ALL) {
            // 随机播放
            if (audioItems != null && audioItems.size() > 0) {
                if (audioItems.size() == 1) {
                    positionPlaying = 0;
                } else {
                    positionPlaying = new Random().nextInt(audioItems.size());
                }
            }
        }
        playPosition();

    }


    private void setPrePosition() {

        if (playMode == REPEATE_NORMAL) {
            // 顺序播放
            if (audioItems != null && audioItems.size() > 0) {
                positionPlaying--;
                // 屏蔽非法值
                if (positionPlaying < 0) {
                    positionPlaying = audioItems.size() - 1;
                }
            }
        } else if (playMode == RANDOM_ALL) {
            // 随机播放
            if (audioItems != null && audioItems.size() > 0) {
                if (audioItems.size() == 1) {
                    positionPlaying = 0;
                } else {
                    positionPlaying = new Random().nextInt(audioItems.size());
                }
            }
        }
        playPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVodPlayer.stopPlay(true);
        mView.onDestroy();
    }


    public void speedChange(View view) {
        dialog = new Dialog(this, R.style.my_dialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.speed_bottom, null);
        TextView speed0_5 = dialogView.findViewById(R.id.speed0_5);
        TextView speed0_75 = dialogView.findViewById(R.id.speed0_75);
        TextView speed1_0 = dialogView.findViewById(R.id.speed1);
        TextView speed1_25 = dialogView.findViewById(R.id.speed1_25);
        TextView speed1_5 = dialogView.findViewById(R.id.speed1_5);
        TextView speed1_75 = dialogView.findViewById(R.id.speed1_75);
        TextView speed2_0 = dialogView.findViewById(R.id.speed2_0);

        if (nowRate == 0.5f) {
            speed0_5.setTextColor(getResources().getColor(R.color.red_e61b19));
        } else if (nowRate == 0.75f) {
            speed0_75.setTextColor(getResources().getColor(R.color.red_e61b19));

        } else if (nowRate == 1f) {
            speed1_0.setTextColor(getResources().getColor(R.color.red_e61b19));

        } else if (nowRate == 1.25f) {
            speed1_25.setTextColor(getResources().getColor(R.color.red_e61b19));

        } else if (nowRate == 1.5f) {
            speed1_5.setTextColor(getResources().getColor(R.color.red_e61b19));

        } else if (nowRate == 1.75f) {
            speed1_75.setTextColor(getResources().getColor(R.color.red_e61b19));

        } else if (nowRate == 2.0f) {
            speed2_0.setTextColor(getResources().getColor(R.color.red_e61b19));

        }
        speed0_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 0.5f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 0.5x");
                dialog.dismiss();
            }
        });

        speed0_75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 0.75f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 0.75x");
                dialog.dismiss();
            }
        });

        speed1_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 1.0f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 1.0x");
                dialog.dismiss();
            }
        });

        speed1_25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 1.25f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 1.25x");
                dialog.dismiss();
            }
        });

        speed1_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 1.5f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 1.5x");
                dialog.dismiss();
            }
        });

        speed1_75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 1.75f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 1.75x");
                dialog.dismiss();
            }
        });

        speed2_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowRate = 2.0f;
                mVodPlayer.setRate(nowRate);
                music_speed.setText(" 2.0x");
                dialog.dismiss();
            }
        });


        dialogView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogView);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 381));
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.AnimTheme2); // 添加动画
        dialogWindow.setAttributes(lp);

        dialog.show();

    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                //                doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("下载需要访问 “外部存储器”，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
