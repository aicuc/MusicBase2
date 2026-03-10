package com.musicbase.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.ScoreInfoAdapter;
import com.musicbase.adapter.TestRecordAdapter;
import com.musicbase.entity.CardBean;
import com.musicbase.entity.DetailBean;
import com.musicbase.entity.PostageBean;
import com.musicbase.entity.TestPaperDetailBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.CameraActivity;
import com.musicbase.ui.activity.CardActivity;
import com.musicbase.ui.activity.PayChooseActivity;
import com.musicbase.ui.view.JzvdVideoView;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.ArithUtil;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.MusicPlayer;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;

import static com.musicbase.util.DialogUtils.dismissMyDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestPaperFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestPaperFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private int type;
    public static final int TYPE_TEST = 111;
    public static final int TYPE_SCORE = 222;
    private CallBack callBack;

    // TODO: Rename and change types of parameters
    private DetailBean.DataBean.ResourceListBean.TestPaperInfoBean testPaperInfoBean;
    private int resourceId;
    private View mRootView;
    LinearLayout layout_score, layout_content;
    TextView tv_content, tv_title, tv_score;
    ImageView iv_content;
    TestPaperDetailBean.DataBean.QuestionsBean questionsBean;
    private TXCloudVideoView mView;
    MusicPlayer musicPlayer;
    RecyclerView recyclerView;
    View line;
    TestRecordAdapter adapter;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public TestPaperFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestPaperFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestPaperFragment newInstance(DetailBean.DataBean.ResourceListBean.TestPaperInfoBean param1, int param2, int type) {
        TestPaperFragment fragment = new TestPaperFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            testPaperInfoBean = (DetailBean.DataBean.ResourceListBean.TestPaperInfoBean) getArguments().getSerializable(ARG_PARAM1);
            resourceId = getArguments().getInt(ARG_PARAM2);
            type = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_test_paper, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkPermission();
        initView();
    }

    private void initView() {
        layout_score = mRootView.findViewById(R.id.layout_score);
        layout_content = mRootView.findViewById(R.id.layout_content);
        mView = (TXCloudVideoView) mRootView.findViewById(R.id.video_view);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        line = mRootView.findViewById(R.id.line);

        if (type == TYPE_SCORE) {
            initTestView();
            initScoreView();
        } else
            initTestView();
        requestData();
    }

    private void requestData() {

        DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_PROGRESS_DIALOG, null, "正在加载中，请稍后...", null);
        OkGo.<String>post(Preferences.GET_TESTPAPER).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("resourceId", resourceId+"").params("paperId", testPaperInfoBean.getPaperId()+"").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                TestPaperDetailBean bean = gson.fromJson(response.body(), TestPaperDetailBean.class);
                dismissMyDialog();
                if (bean.getCode() != null && bean.getCode().equals(Preferences.SUCCESS) && null != bean.getData()) {
                    if (bean.getData() != null && bean.getData().getQuestions() != null && bean.getData().getQuestions().size() > 0) {
                        questionsBean = bean.getData().getQuestions().get(0);
                        Log.e("TestPaperFragment","questionsBean=="+questionsBean.toString());
                        if (questionsBean.getUploadRecords() != null && questionsBean.getUploadRecords().size() > 0 && questionsBean.getUploadRecords().get(0) != null)
                            callBack.onScoreShow(true);
                        else
                            callBack.onScoreShow(false);
                        if (type == TYPE_SCORE) {
                            setTestViewData();
                            setScoreViewData();
                        } else
                            setTestViewData();
                    }
                } else if (!TextUtils.isEmpty(bean.getCode()) && bean.getCode().equals(Preferences.FAIL)) {
                    ActivityUtils.showToast(getActivity(), "加载失败," + bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                ActivityUtils.showToast(getActivity(), "加载失败,请检查网络!");
                dismissMyDialog();
            }

            @Override
            public void onFinish() {
                dismissMyDialog();
            }
        });
    }

    public TestPaperDetailBean.DataBean.QuestionsBean getQuestionsBean() {
        return questionsBean;
    }

    private void initTestView() {
        layout_content.setVisibility(View.VISIBLE);
        layout_score.setVisibility(View.VISIBLE);
        tv_content = mRootView.findViewById(R.id.tv_content);
        iv_content = mRootView.findViewById(R.id.iv_content);
        tv_title = mRootView.findViewById(R.id.tv_title);
        tv_score = mRootView.findViewById(R.id.tv_score);
    }

    private void setTestViewData() {
        List<String> audioList = new ArrayList<>();
        for (TestPaperDetailBean.DataBean.QuestionsBean.PlaySequenceBean bean : questionsBean.getPlaySequence()) {
            audioList.add(ActivityUtils.getSDPath(getActivity(), resourceId + "").getAbsolutePath() + "/" + bean.getTopic_path());
        }
        musicPlayer = new MusicPlayer(getActivity(), audioList, mView, questionsBean.getBeginReord(), questionsBean.getAllowTime());
        tv_title.setText(questionsBean.getTopicName());
        tv_score.setText(questionsBean.getTopicGrade() + ".0");
        tv_content.setText(getSp(questionsBean.getTopicStem()));
        tv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == TYPE_TEST) {
                    if (checkPermission()) {
                        ActivityUtils.showToast(getActivity(),"请在设置中授权存储权限和相机权限！");
                        return;
                    }
                    tv_content.setClickable(false);
                    musicPlayer.start();
                }
            }
        });
        File file = new File(ActivityUtils.getSDPath(getActivity(), resourceId + "").getAbsolutePath() + "/" + questionsBean.getTopicImg());
        Glide.with(getActivity()).load(file).into(iv_content);
    }

    private void initScoreView() {
        recyclerView.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new TestRecordAdapter(getActivity(),null);
        adapter.setCallBack(new TestRecordAdapter.CallBack() {
            @Override
            public void playVideo(String path) {
                showDialog(path);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setScoreViewData(){
        adapter.setNewData(questionsBean.getUploadRecords());
        adapter.setTopicGrade(questionsBean.getTopicGrade());
        adapter.notifyDataSetChanged();
    }

    private void showDialog(String path){
        Dialog dialog = new Dialog(getActivity(), R.style.my_dialog);
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_video, null);
        JzvdVideoView video = dialogView.findViewById(R.id.video);
        video.setType(1);
        video.setUp(Preferences.RESOURCE_URL+path, "", Jzvd.SCREEN_NORMAL);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(video!=null) {
                    video.reset();
                    video.removeAllViews();
                }
            }
        });
        dialog.setContentView(dialogView);

        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setLayout((int) d.getWidth(), (int) (d.getHeight() *2/3));
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);

        dialog.show();
        video.startVideo();
    }

    private boolean checkPermission() {
        boolean isCheck = false;
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (permissions.size() != 0) {
                isCheck = true;
                ActivityCompat.requestPermissions(getActivity(), permissions.toArray(new String[0]), 100);
            }
        }
        return isCheck;
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("需要访问 “外部存储器”和“照相机”权限，请到 “设置 -> 应用权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
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
     * 获取文字中的图片
     *
     * @param html 文字
     * @return sp
     */
    private Spanned getSp(String html) {
        if (html != null) {
            //            int size = html.length();
            String htmlbq = html.replaceAll("<icon>", "<img src=\"");
            htmlbq = htmlbq.replace("</icon>", "\">");
            //            int size_htmlbq = htmlbq.length();
            //            if (size == size_htmlbq) {
            //                hasBq = false;
            //            } else {
            //                hasBq = true;
            //            }

            Spanned sp = Html.fromHtml(htmlbq, new Html.ImageGetter() {
                @Override
                //加载本地图片
                public Drawable getDrawable(final String source) {

                    Drawable drawable = null;
                    try {
                        File file = new File(ActivityUtils.getSDPath(getActivity(), resourceId + "").getAbsolutePath() + "/" + source);
                        drawable = Drawable.createFromPath(file.getAbsolutePath());
                        if (drawable != null)
                            drawable.setBounds(0, 0, 50, 50);
                    } catch (Exception e) {
                        Log.e("getSp", "----getSp---error---" + e.getLocalizedMessage());

                    }
                    return drawable;
                }

            }, null);
            return sp;
        }
        return null;

    }

    /**
     * 停止所有活动 播放音乐 视频录制等
     */
    public void stopAll() {
        if (musicPlayer != null)
            musicPlayer.onDestroy();
    }

    public void canClick() {
        tv_content.setClickable(true);
    }

    public interface CallBack {
        void onScoreShow(boolean isShow);
    }
}