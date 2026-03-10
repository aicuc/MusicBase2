package com.musicbase.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbase.R;
import com.musicbase.ui.view.DiffuseView;
import com.musicbase.util.Entry;
import com.musicbase.util.ScoreUtils2;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import static com.musicbase.preferences.Preferences.isNotMusic;

public class SongScored extends AppCompatActivity implements View.OnClickListener {

    private ImageButton titlelayout_back;
    private TextView titlelayout_title;
    private ImageView image_song;
    private DiffuseView begin_sing;
    private Button begin_again;
    private Button begin_score;
    private Button listen_score;
    private MediaPlayer mediaPlayer;


    private TextView sign_song;
    private String name;
    private String title;
    private boolean isBegin;
    private Thread audioThread;
    private long dateTime;
    private long dateDuringTime;
    List<Entry> yDataList = new ArrayList<Entry>();// y轴数据数据源
    List<Entry> yStandardDataList = new ArrayList<Entry>();// y轴数据数据源
    private TextView time_count;
    private Timer timer;
    private AudioDispatcher dispatcher;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private String[] myPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    stringBuffer.append(visibleNum + "|");
//                    dorimi.append(visibleNum + "|");
                    sign_song.setText((String) msg.obj);
//                    begin_sing.setDiffuseWidth((60 - msg.arg1) / 10);
//                    begin_sing.start();
                    break;
                case 1:
//                    nowPitchWord.setText(msg.obj.toString());
                    time_count.setText((String) msg.obj);
                    time_count.setTextColor(Color.parseColor("#e61b19"));
                    break;
                case 3:
                    begin_sing.setCoreImage(R.drawable.stop);
                    begin_sing.stop();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_scored);
        isBegin = false;
        initData();
        initView();
    }

    private void initData() {
        name = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("title");
    }

    private void initView() {
        titlelayout_back = (ImageButton) findViewById(R.id.titlelayout_back);
        titlelayout_title = (TextView) findViewById(R.id.titlelayout_title);
        image_song = (ImageView) findViewById(R.id.image_song);
        begin_sing = (DiffuseView) findViewById(R.id.begin_sing);
        begin_again = (Button) findViewById(R.id.begin_again);
        begin_score = (Button) findViewById(R.id.begin_score);
        sign_song = (TextView) findViewById(R.id.sign_song);
        listen_score = (Button) findViewById(R.id.listen_score);
        listen_score.setOnClickListener(this);
        titlelayout_back.setOnClickListener(this);
        begin_sing.setOnClickListener(this);
        begin_again.setOnClickListener(this);
        begin_score.setOnClickListener(this);
        titlelayout_title.setText(name);

        try {
            InputStream is = getAssets().open(title + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            image_song.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();

        }
        sign_song.setText("");
        mediaPlayer = new MediaPlayer();

        time_count = (TextView) findViewById(R.id.time_count);
        time_count.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isBegin) {
            new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                    .setTitle("温 馨 提 示 :")
                    .setMessage("您还在测试中，确认要退出测试吗？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (timer != null)
                                timer.cancel();
                            if (dispatcher != null&&!dispatcher.isStopped())
                                dispatcher.stop();
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlelayout_back:
                if (isBegin) {
                    new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                            .setTitle("温 馨 提 示 :")
                            .setMessage("您还在测试中，确认要退出测试吗？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (timer != null)
                                        timer.cancel();
                                    if (dispatcher != null&&!dispatcher.isStopped())
                                        dispatcher.stop();
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                    return;
                }
                finish();
                break;
            case R.id.begin_sing:
                Boolean isAllGranted = checkPermissionAllGranted(myPermissions);
                if (!isAllGranted) {
                    ActivityCompat.requestPermissions(
                            this,
                            myPermissions,
                            0
                    );
                    return;
                }
                if (isBegin) {
                    isBegin = false;
                    begin_sing.setCoreImage(R.drawable.stop);
                    begin_sing.stop();
                    time_count.setTextColor(Color.parseColor("#666666"));
                    if (timer != null)
                        timer.cancel();
                    if (dispatcher == null)
                        return;
                    if (!dispatcher.isStopped())
                        dispatcher.stop();
                    dateDuringTime = new Date().getTime();

                } else {
                    isBegin = true;
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                    begin_sing.setCoreImage(R.drawable.start);
                    openAssetMusics(true);
//                    testFromFile();
                }
                break;
            case R.id.begin_again:
                Boolean granted = checkPermissionAllGranted(myPermissions);
                if (!granted) {
                    ActivityCompat.requestPermissions(
                            this,
                            myPermissions,
                            0
                    );
                    return;
                }
                yDataList.clear();
                isBegin = false;
                begin_sing.setCoreImage(R.drawable.stop);
                begin_sing.stop();
                sign_song.setText("");
                time_count.setText("00:00:00");
                time_count.setTextColor(Color.parseColor("#666666"));
                dateTime = 0;
                if (timer != null)
                    timer.cancel();
                if (dispatcher == null)
                    return;
                if (!dispatcher.isStopped())
                    dispatcher.stop();
                break;
            case R.id.begin_score:
                if (isBegin) {
                    Toast.makeText(this, "正在测试，请先停止测试再评分！", Toast.LENGTH_SHORT).show();
                    return;
                }
//                String data = SPUtility.getSPString(MainActivity.this, "listStr");
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Entry>>() {
                }.getType();
                yStandardDataList = gson.fromJson(readJSONFromAsset(), listType);

                ScoreUtils2 scoreUtils2 = new ScoreUtils2(yStandardDataList, yDataList);
                float[] scoreTime = scoreUtils2.scoreTime();
                float[] scoreFrequency = scoreUtils2.scoreFrequency();
                int genTimeScore = 0;
                int genzScore = (int) (100 / (scoreFrequency[0] + scoreFrequency[2] / scoreTime[1]));
                genzScore = genzScore > 100 ? 100 : genzScore;
                genTimeScore = (int) (8 / scoreTime[0]);
                genTimeScore = genTimeScore > 100 ? 100 : genTimeScore;
                yStandardDataList.clear();
//                    final CommonDialog commonDialog = new CommonDialog(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("得分情况:");
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yDataList.clear();
                        isBegin = false;

                        sign_song.setText("");
                        dateTime = 0;
                        if (timer != null)
                            timer.cancel();
                        time_count.setText("00:00:00");
                        time_count.setTextColor(Color.parseColor("#666666"));
//                        if (dispatcher == null)
//                            return;
//                        if (!dispatcher.isStopped())
//                            dispatcher.stop();
                        dialog.dismiss();
//                        handler.sendEmptyMessage(3);
                    }
                });
//                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
                if (isNotMusic)
                    builder.setMessage("您好像还没有唱歌哦！");
                else if (scoreFrequency[2] / scoreTime[2] > 0.8)
                    builder.setMessage("您的错误率很高，不是同一首歌吧！");
                else
                    builder.setMessage("系统评分：节奏" + genTimeScore + "分，音准" + genzScore + "分\n明细如下\n节奏误差：" + scoreTime[0] + "，标准唱总帧数为：" + scoreTime[1] + "，您的总帧数为：" + scoreTime[2] + "，音准误差率为：" + scoreFrequency[0] + "，误差个数为：" + scoreFrequency[1] + "，大幅度偏差个数为：" + scoreFrequency[2]);


                builder.create().show();
//                    commonDialog.setTitle(" 得 分 情 况 : ");
//                    commonDialog.setRightButtonClickListener(new CommonDialog.RightButtonClickListener() {
//                        @Override
//                        public void onRightButtonClick() {
//                            xDataList.clear();
//                            yDataList.clear();
//                            count = 0;
//                            isBegin = false;
//                            bigin.setText("开始测试");
//                            lineChart.clear();
//                            initData();
//                            commonDialog.cancel();
//                        }
//                    });
////                    commonDialog.setLeftButtonClickListener();
//                    commonDialog.setMessage("系统评分：节奏" + genTimeScore + "分，音准" + genzScore + "分\n明细如下\n节奏误差：" + scoreTime[0] + "，标准唱总帧数为：" + scoreTime[1] + "，您的总帧数为：" + scoreTime[2] + "，音准误差率为：" + scoreFrequency[0] + "，误差个数为：" + scoreFrequency[1]);
//                    commonDialog.show();


                break;
            case R.id.listen_score:
                openAssetMusics(false);
                break;
        }

    }

    private void startRecord() {

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100, 10000, 5000);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isBegin) {
                            processPitch(pitchInHz);
                        }

                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 44100, 10000, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);
        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
        final DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = df.format(new Date().getTime() - dateTime);
                handler.sendMessage(message);
            }
        }, 1000, 1000);

    }

    private void processPitch(final float pitchInHz) {
//            yAxis.setDrawGridLines(true);
        int value = 0;
        String text = "";
        if (pitchInHz < 85) {
            value = 0;
            text = "0";
        } else if (pitchInHz < 90) {
            value = 1;
            text = "2F";
        } else if (pitchInHz < 95) {
            value = 2;
            text = "2F#";
        } else if (pitchInHz < 100) {
            value = 3;
            text = "2G";
        } else if (pitchInHz < 106) {
            value = 4;
            text = "2G#";
        } else if (pitchInHz < 113) {
            value = 5;
            text = "2A";
        } else if (pitchInHz < 120) {
            value = 6;
            text = "2A#";
        } else if (pitchInHz < 127) {
            value = 7;
            text = "2B";
        } else if (pitchInHz < 135) {
            value = 8;
            text = "3C";
        } else if (pitchInHz < 142) {
            value = 9;
            text = "3C#";
        } else if (pitchInHz < 151) {
            value = 10;
            text = "3D";
        } else if (pitchInHz < 160) {
            value = 11;
            text = "3D#";
        } else if (pitchInHz < 170) {
            value = 12;
            text = "3E";
        } else if (pitchInHz < 180) {
            value = 13;
            text = "3F";
        } else if (pitchInHz < 191) {
            value = 14;
            text = "3F#";
        } else if (pitchInHz < 202) {
            value = 15;
            text = "3G";
        } else if (pitchInHz < 214) {
            value = 16;
            text = "3G#";
        } else if (pitchInHz < 226) {
            value = 17;
            text = "3A";
        } else if (pitchInHz < 240) {
            value = 18;
            text = "3A#";
        } else if (pitchInHz < 254) {
            value = 19;
            text = "3B";
        } else if (pitchInHz < 269) {
            value = 20;
            text = "4C";
        } else if (pitchInHz < 285) {
            value = 21;
            text = "4C#";
        } else if (pitchInHz < 302) {
            value = 22;
            text = "4D";
        } else if (pitchInHz < 320) {
            value = 23;
            text = "4D#";
        } else if (pitchInHz < 339) {
            value = 24;
            text = "4E";
        } else if (pitchInHz < 359) {
            value = 25;
            text = "4F";
        } else if (pitchInHz < 381) {
            value = 26;
            text = "4F#";
        } else if (pitchInHz < 404) {
            value = 27;
            text = "4G";
        } else if (pitchInHz < 428) {
            value = 28;
            text = "4G#";
        } else if (pitchInHz < 453) {
            value = 29;
            text = "4A";
        } else if (pitchInHz < 480) {
            value = 30;
            text = "4A#";
        } else if (pitchInHz < 508) {
            value = 31;
            text = "4B";
        } else if (pitchInHz < 539) {
            value = 32;
            text = "5C";
        } else if (pitchInHz < 571) {
            value = 33;
            text = "5C#";
        } else if (pitchInHz < 604) {
            value = 34;
            text = "5D";
        } else if (pitchInHz < 641) {
            value = 35;
            text = "5D#";
        } else if (pitchInHz < 679) {
            value = 36;
            text = "5E";
        } else if (pitchInHz < 719) {
            value = 37;
            text = "5F";
        } else if (pitchInHz < 762) {
            value = 38;
            text = "5F#";
        } else if (pitchInHz < 807) {
            value = 39;
            text = "5G";
        } else if (pitchInHz < 855) {
            value = 40;
            text = "5G#";
        } else if (pitchInHz < 906) {
            value = 41;
            text = "5A";
        } else if (pitchInHz >= 906) {
            value = 42;
            text = "HIGH";
        } else {
            value = 0;
        }
        Message message = new Message();
        message.what = 0;
        message.arg1 = value;
        message.obj = text;
        handler.sendMessage(message);
        yDataList.add(new Entry(value, 0));
//        handler.sendEmptyMessage(1);
    }

    private String readJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open(title + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    /**
     * 打开assets下的音乐mp3文件
     */
    private void openAssetMusics(boolean b) {
        //打开Asset目录
        if (!b && isBegin) {
            Toast.makeText(this, "正在测试，无法播放参考答案！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //打开音乐文件shot.mp3

            if (!b && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                return;
            }
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(b ? title + "_b.mp3" : title + ".mp3");
            mediaPlayer.reset();
            //设置媒体播放器的数据资源
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (isBegin) {
                        begin_sing.start();
                        if (dateTime == 0) {
                            dateTime = new Date().getTime();
                        } else {
                            dateTime = dateTime + new Date().getTime() - dateDuringTime;
                        }
                        time_count.setTextColor(Color.parseColor("#e61b19"));
//                    if (xDataList.size() == 2) {
//                        dateTime = new Date().getTime();
//                    }

                        startRecord();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("GsonUtils", "IOException" + e.toString());
        }
    }
    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("需要访问 “外部存储器和录音权限”，请到 “设置 -> 应用权限” 中授予！");
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
    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
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
}
