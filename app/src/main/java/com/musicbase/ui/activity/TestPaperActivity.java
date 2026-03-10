package com.musicbase.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.musicbase.R;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.FirstBean;
import com.musicbase.entity.RecordBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.model.BannerImageLoader;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.TestPaperFragment;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.view.BannerViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestPaperActivity extends AppCompatActivity {
    ImageButton titlelayout_back;
    TextView tv_score, titlelayout_title, tv_time;
    ConstraintLayout container;
    DetailBean.DataBean.ResourceListBean.TestPaperInfoBean testPaperInfo;
    DetailBean.DataBean.UserAvatarsBean userAvatars;
    TestPaperFragment testPaperFragment;
    int resourceId;
    int type;
    int courseId;
    ViewStub viewStub;
    View guideView;
    Banner banner;
    Button btn_join;
    SharedPreferences preferences;
    int index;//记录再次提交位置
    boolean isNotAgainShow = false;//是否再次提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_test_paper);
        EventBus.getDefault().register(this);
        preferences = getSharedPreferences("first_pref", MODE_PRIVATE);
        boolean isShowGuide = preferences.getBoolean("isShowGuide", true);
        if (isShowGuide) {
            setGuidePager();
        } else {
            showTipDialog();
        }
        getIntentData();
        initView();
        requestData();
    }

    private void setGuidePager() {
        viewStub = findViewById(R.id.viewstub);
        if (guideView == null)
            guideView = viewStub.inflate();
        banner = guideView.findViewById(R.id.banner);
        btn_join = guideView.findViewById(R.id.btn_join);
        CheckBox checkBox = guideView.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isNotAgainShow = isChecked;
            }
        });
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishGuide();
            }
        });

        List<Integer> imagepaths = new ArrayList<>();
        imagepaths.add(R.drawable.test_g_1);
        imagepaths.add(R.drawable.test_g_2);
        imagepaths.add(R.drawable.test_g_3);
        imagepaths.add(R.drawable.test_g_4);
        imagepaths.add(R.drawable.test_g_5);
        imagepaths.add(R.drawable.test_g_6);
        imagepaths.add(R.drawable.test_g_7);

        banner.setImages(imagepaths).
                setImageLoader(new MyLoader()).
                setDelayTime(2000).
                setBannerStyle(BannerConfig.CIRCLE_INDICATOR).
                setIndicatorGravity(BannerConfig.CENTER).
                start();
    }

    private void finishGuide() {
        guideView.setVisibility(View.GONE);
        if (isNotAgainShow) {
            SharedPreferences.Editor editor = preferences.edit();
            // 存入数据
            editor.putBoolean("isShowGuide", false);
            // 提交修改
            editor.commit();
        }
        showTipDialog();
    }

    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context).load((int) path).fitCenter().into(imageView);
        }
    }

    private void showTipDialog() {
        Dialog dialog = new Dialog(this, R.style.my_dialog);
        dialog.setContentView(R.layout.dialog_error);

        TextView tv_title01 = (TextView) dialog.findViewById(R.id.dialog_titile);
        tv_title01.setText("提示");
        TextView textView01 = (TextView) dialog.findViewById(R.id.error_message);
        textView01.setText("答题时请勿佩戴耳机");

        Button button = (Button) dialog.findViewById(R.id.error_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void getIntentData() {
        testPaperInfo = (DetailBean.DataBean.ResourceListBean.TestPaperInfoBean) getIntent().getSerializableExtra("testPaperInfo");
        //        userAvatars = (DetailBean.DataBean.UserAvatarsBean) getIntent().getSerializableExtra("userAvatars");
        resourceId = getIntent().getIntExtra("resourceId", 0);
        courseId = getIntent().getIntExtra("courseId", 0);
        Log.e("courseId", "courseId testpaper==" + courseId);
    }

    private void initView() {
        titlelayout_back = findViewById(R.id.titlelayout_back);
        tv_score = findViewById(R.id.tv_score);
        titlelayout_title = findViewById(R.id.titlelayout_title);
        container = findViewById(R.id.container);
        titlelayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titlelayout_title.setText(testPaperInfo.getPaperName());
        testPaperFragment = TestPaperFragment.newInstance(testPaperInfo, resourceId, TestPaperFragment.TYPE_TEST);
        testPaperFragment.setCallBack(new TestPaperFragment.CallBack() {
            @Override
            public void onScoreShow(boolean isShow) {
                setTv_score(isShow);
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container, testPaperFragment).commit();
        tv_time = findViewById(R.id.tv_time);
        findViewById(R.id.tv_saved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestPaperActivity.this, TestPaperSaveActivity.class);
                intent.putExtra("testPaperInfo", testPaperInfo);
                intent.putExtra("resourceId", resourceId);
                intent.putExtra("questionsBean", testPaperFragment.getQuestionsBean());
                intent.putExtra("courseId", courseId);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
    }

    private void setTv_score(boolean isShow) {
        if (isShow) {
            tv_score.setClickable(true);
            tv_score.setTextColor(getResources().getColor(R.color.red_e61b19));
            tv_score.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (testPaperFragment != null)
                        testPaperFragment.stopAll();

                    Intent intent6 = new Intent(TestPaperActivity.this, TestPaperResultActivity.class);
                    intent6.putExtra("testPaperInfo", testPaperInfo);
                    intent6.putExtra("resourceId", resourceId);
                    startActivityForResult(intent6, 3333);
                }
            });
        } else {
            tv_score.setClickable(false);
            tv_score.setTextColor(getResources().getColor(R.color.grey_666666));
        }
    }

    private void requestData() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ActivityUtils.deleteFoder(new File(ActivityUtils.getSDPath(this, resourceId + "").getAbsolutePath() + "/" + "testpaper"));
        super.onDestroy();
    }

    private MyCountDownTimer myCountDownTimer;

    public class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_time.setVisibility(View.VISIBLE);
            if (type == RecordBean.RECORD_DAOJISHI) {
                tv_time.setText((int) millisUntilFinished / 1000 + "后开始录制");
            } else
                tv_time.setText((int) millisUntilFinished / 1000 + "后结束录制");
        }

        @Override
        public void onFinish() {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (testPaperFragment != null)
            testPaperFragment.stopAll();
        mHandler.removeMessages(1);
        if (banner != null)
            banner.stopAutoPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (banner != null)
            banner.startAutoPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重置题目播放事件
        if (testPaperFragment != null)//允许再次点击播放试题音频
            testPaperFragment.canClick();
        tv_time.setText("");
        tv_time.setVisibility(View.GONE);
        isRecordBegin = false;
        isDaojishi = false;

    }

    boolean isDaojishi = false;
    boolean isRecordBegin = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RecordBean bean) {
        Intent intent = new Intent(this, CameraActivity.class);
        if (bean.getType() == RecordBean.RECORD_DAOJISHI) {
            if (isDaojishi)
                return;
            isDaojishi = true;
            type = RecordBean.RECORD_DAOJISHI;
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
                myCountDownTimer = null;
            }
            long time = 0;
            if (bean.getTime() == 0)
                time = 3 * 1000;
            else
                time = bean.getTime() * 1000;
            myCountDownTimer = new MyCountDownTimer(time, 1000);
            myCountDownTimer.start();
            //打开录制
            Log.e("CJT", "limitTime-------==" + bean.getTime() + "------allTime==" + bean.getAllTime());
            intent.putExtra("resourceId", resourceId);
            intent.putExtra("limitTime", bean.getTime() + 1);
            intent.putExtra("allTime", bean.getAllTime());
            startActivityForResult(intent, 0);
        } else if (bean.getType() == RecordBean.RECORD_BEGIN) {
            if (isRecordBegin)
                return;
            isRecordBegin = true;
            type = RecordBean.RECORD_BEGIN;
            EventBus.getDefault().post("begin");
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
                myCountDownTimer = null;
            }
            myCountDownTimer = new MyCountDownTimer((bean.getAllTime() + 1) * 1000, 1000);
            myCountDownTimer.start();
            //开始录制
            //            startActivityForResult(intent,0);
        } else if (bean.getType() == RecordBean.RECORD_DESTROY) {
            type = RecordBean.RECORD_DESTROY;
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
                myCountDownTimer = null;
            }
            if (testPaperFragment != null)
                testPaperFragment.stopAll();
            mHandler.removeMessages(1);
        } else if (bean.getType() == RecordBean.RECORD_DAOJISHI_BEGIN) {
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = bean.getTime();
            msg.arg2 = bean.getAllTime();
            mHandler.sendMessageDelayed(msg, bean.getTime() * 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 111) {//获取录像url
            String url = data.getStringExtra("url");
            Intent intent = new Intent(TestPaperActivity.this, TestPaperUploadActivity.class);
            intent.putExtra("testPaperInfo", testPaperInfo);
            intent.putExtra("resourceId", resourceId);
            intent.putExtra("url", url);
            intent.putExtra("questionsBean", testPaperFragment.getQuestionsBean());
            intent.putExtra("courseId", courseId);
            intent.putExtra("index", index);
            //            intent.putExtra("userAvatars",userAvatars);
            startActivity(intent);
        } else if (requestCode == 3333 && resultCode == 3333) {
            index = data.getIntExtra("index", 0);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                EventBus.getDefault().post(new RecordBean(RecordBean.RECORD_BEGIN, msg.arg1, msg.arg2));
            }
            super.handleMessage(msg);
        }
    };
}