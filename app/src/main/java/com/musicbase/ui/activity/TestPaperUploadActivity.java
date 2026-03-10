package com.musicbase.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.entity.Bean;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.GetPhotoBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.TestPaperFragment;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.MyLog;
import com.musicbase.util.SPUtility;

import java.io.File;
import java.util.logging.Logger;

import cn.jzvd.Jzvd;

import static com.musicbase.util.DialogUtils.dismissMyDialog;

public class TestPaperUploadActivity extends AppCompatActivity {
    ImageButton titlelayout_back;
    TextView titlelayout_title, tv_upload, tv_delete;
    DetailBean.DataBean.ResourceListBean.TestPaperInfoBean testPaperInfo;
    int resourceId;
    String url;
    Button btn_submit;
    DetailBean.DataBean.UserAvatarsBean userAvatars;
    DetailBean.DataBean.UserAvatarsBean.AvatarList avatar;
    TestPaperDetailBean.DataBean.QuestionsBean questionsBean;
    ImageView iv_photo;
    JzvdVideoView video;
    int courseId;
    int index;//记录再次提交位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_paper_upload);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        testPaperInfo = (DetailBean.DataBean.ResourceListBean.TestPaperInfoBean) getIntent().getSerializableExtra("testPaperInfo");
        resourceId = getIntent().getIntExtra("resourceId", 0);
        //        userAvatars = (DetailBean.DataBean.UserAvatarsBean) getIntent().getSerializableExtra("userAvatars");
        url = getIntent().getStringExtra("url");
        questionsBean = (TestPaperDetailBean.DataBean.QuestionsBean) getIntent().getSerializableExtra("questionsBean");
        courseId = getIntent().getIntExtra("courseId", 0);
        index = getIntent().getIntExtra("index",0);
    }

    private void initView() {
        titlelayout_back = findViewById(R.id.titlelayout_back);
        titlelayout_title = findViewById(R.id.titlelayout_title);
        titlelayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titlelayout_title.setText(testPaperInfo.getPaperName());
        iv_photo = findViewById(R.id.iv_photo);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showMyDialog(TestPaperUploadActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "确认提交", "提交本视频给考官进行打分与点评，将消耗您一次提交次数", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (questionsBean.getUploadState().equals("allow_upload")) {
                            if (questionsBean.getUploadRecords() != null && questionsBean.getUploadRecords().size() > 0) {
                                TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean bean = questionsBean.getUploadRecords().get(index);
                                if (bean.getCheckState() == 2 && bean.getSubmitNum() < 2) {
                                    submitAgain();
                                } else {
                                    ActivityUtils.showToast(TestPaperUploadActivity.this, "请购买后再次提交");
                                }
                            } else {
                                requestData();
                            }
                        } else {
                            if (questionsBean.getUploadRecords() != null && questionsBean.getUploadRecords().size() > 0) {
                                TestPaperDetailBean.DataBean.QuestionsBean.UploadRecordsBean bean = questionsBean.getUploadRecords().get(index);
                                if (bean.getCheckState() == 2 && bean.getSubmitNum() < 2) {
                                    submitAgain();
                                } else {
                                    ActivityUtils.showToast(TestPaperUploadActivity.this, "请购买后再次提交");
                                }
                            } else {
                                ActivityUtils.showToast(TestPaperUploadActivity.this, "请购买后再次提交");
                            }
                        }
                    }
                });

            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        video = findViewById(R.id.video);
        video.setType(1);
        video.setUp(url, "", Jzvd.SCREEN_NORMAL);
        video.startVideo();
        tv_upload = findViewById(R.id.tv_upload);

        getPhoto();
        tv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestPaperUploadActivity.this, PhotoListActivity.class);
                intent.putExtra("userAvatars", userAvatars);
                intent.putExtra("resourceId", resourceId);
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, 1);
            }
        });
        tv_delete = findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showMyDialog(TestPaperUploadActivity.this, Preferences.SHOW_CONFIRM_DIALOG, "删除视频", "确定删除视频吗？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.dismissMyDialog();
                        setResult(112);
                        finish();
                        ActivityUtils.deleteBookFormSD(url);
                        ActivityUtils.deleteBookFormSD(url.replace(".mp4", ".jpg"));
                    }
                });
            }
        });
    }

    private void getPhoto() {
        OkGo.<String>post(Preferences.GET_PHOTO).tag(this).params("userId", SPUtility.getUserId(this) + "").params("resourceId", resourceId + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                MyLog.e("photo", "userAvatars==" + response.body());
                Gson gson = new Gson();
                GetPhotoBean bean = gson.fromJson(response.body(), GetPhotoBean.class);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    userAvatars = bean.getData();
                    if (!isFinishing())
                        setPhoto();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(TestPaperUploadActivity.this, "获取失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(TestPaperUploadActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    private void submitAgain() {
        if (avatar == null) {
            ActivityUtils.showToast(this, "请先上传头像");
            return;
        }
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        File file = new File(url);
        OkGo.<String>post(Preferences.AGAINSUBMIT_TESTPAPER).tag(this).params("userId", SPUtility.getUserId(this) + "").params("recordId", questionsBean.getUploadRecords().get(0).getRecordId() + "").params("avatarId", avatar.getAvatarId() + "").params("file", file).isMultipart(true).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(response.body(), Bean.class);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    ActivityUtils.showToast(TestPaperUploadActivity.this, "上传成功");
                    finish();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(TestPaperUploadActivity.this, "上传失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(TestPaperUploadActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    private void setPhoto() {
        if (userAvatars != null && userAvatars.getAvatarList() != null && userAvatars.getAvatarList().size() > 0) {
            for (DetailBean.DataBean.UserAvatarsBean.AvatarList bean : userAvatars.getAvatarList()) {
                if (bean.getIsDefault() == 1) {
                    avatar = bean;
                    Glide.with(this).load(Preferences.RESOURCE_URL + bean.getUserAvatar()).into(iv_photo);
                    tv_upload.setText("更换做题人>");
                }
            }
        }
    }

    private void requestData() {
        if (avatar == null) {
            ActivityUtils.showToast(this, "请先上传头像");
            return;
        }
        DialogUtils.showMyDialog(this, Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        File file = new File(url);
        OkGo.<String>post(Preferences.FIRSTSUBMIT_TESTPAPER).tag(this).params("userId", SPUtility.getUserId(this) + "").params("resourceId", resourceId + "").params("paperId", testPaperInfo.getPaperId() + "").params("bankId", questionsBean.getBankId() + "").params("avatarId", avatar.getAvatarId() + "").params("file", file).isMultipart(true).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                Bean bean = gson.fromJson(response.body(), Bean.class);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS)) {
                    ActivityUtils.showToast(TestPaperUploadActivity.this, "上传成功");
                    finish();
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(TestPaperUploadActivity.this, "上传失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(TestPaperUploadActivity.this, "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (video != null) {
            video.reset();
            video.removeAllViews();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 1111) {
            getPhoto();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}