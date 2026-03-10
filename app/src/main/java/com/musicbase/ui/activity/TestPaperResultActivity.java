package com.musicbase.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.musicbase.R;
import com.musicbase.adapter.AudioAdapter;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.RecordBean;
import com.musicbase.entity.SubmitAgainBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.TestPaperFragment;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DensityUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;

import cn.jzvd.Jzvd;

public class TestPaperResultActivity extends AppCompatActivity {
    ImageButton titlelayout_back;
    TextView titlelayout_title;
    ConstraintLayout container;
    DetailBean.DataBean.ResourceListBean.TestPaperInfoBean testPaperInfo;
    TestPaperFragment testPaperFragment;
    int resourceId;
    int index;//记录再次提交位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paper_result);
        EventBus.getDefault().register(this);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        testPaperInfo = (DetailBean.DataBean.ResourceListBean.TestPaperInfoBean) getIntent().getSerializableExtra("testPaperInfo");
        resourceId = getIntent().getIntExtra("resourceId", 0);
    }

    private void initView() {
        titlelayout_back = findViewById(R.id.titlelayout_back);
        titlelayout_title = findViewById(R.id.titlelayout_title);
        container = findViewById(R.id.container);
        titlelayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testPaperFragment != null)
                    testPaperFragment.stopAll();
                finish();
            }
        });
        titlelayout_title.setText(testPaperInfo.getPaperName());
        testPaperFragment = TestPaperFragment.newInstance(testPaperInfo, resourceId, TestPaperFragment.TYPE_SCORE);
        testPaperFragment.setCallBack(new TestPaperFragment.CallBack() {
            @Override
            public void onScoreShow(boolean isShow) {

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.container, testPaperFragment).commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SubmitAgainBean bean) {
        if (testPaperFragment != null)
            testPaperFragment.stopAll();

        index = bean.getIndex();
        Intent intent = new Intent();
        intent.putExtra("index",bean.getIndex());
        setResult(3333,intent);
        finish();
    }

}