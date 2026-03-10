package com.musicbase.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.Bean;
import com.musicbase.entity.LogoutProcessBean;
import com.musicbase.implement.LogoutCallBack;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.LogoutActivity;
import com.musicbase.ui.activity.SettingActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.GlideCacheUtil;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Logout2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Logout2Fragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;//加载类型
    private String mParam2;//失败时 话术提示
    private Boolean success_isAble;//成功时 时限关系 可否确认注销
    private View mRootView;
    private LogoutCallBack callBack;
    private ImageView iv;
    private TextView tv_1, tv_2;
    private Button btn_ok, btn_cancel;
    private View view;//站位view

    public Logout2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment Logout2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Logout2Fragment newInstance(String param1, String param2, Boolean success_isAble) {
        Logout2Fragment fragment = new Logout2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putBoolean(ARG_PARAM3, success_isAble);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            success_isAble = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_logout_2, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    private void initView() {
        iv = mRootView.findViewById(R.id.iv);
        tv_1 = mRootView.findViewById(R.id.tv_1);
        tv_2 = mRootView.findViewById(R.id.tv_2);
        btn_ok = mRootView.findViewById(R.id.btn_ok);
        btn_cancel = mRootView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        view = mRootView.findViewById(R.id.view);

        int iv_ID = 0;
        String tv_1_content = "";
        String tv_2_content = "";
        switch (mParam1) {
            case LogoutCallBack.LOGOUT_ING:
                iv_ID = R.drawable.logout_ing;
                tv_1_content = "申请注销，等待处理！";
                tv_2_content = "预计3个工作日内审核完毕，请及时查看。";
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setText("取消注销");
                break;
            case LogoutCallBack.LOGOUT_FAIL:
                iv_ID = R.drawable.logout_fail;
                tv_1_content = "申请注销，审核失败！";
                tv_2_content = mParam2;
                btn_ok.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                btn_cancel.setText("取消注销");
                btn_ok.setText("重新提交");
                break;
            case LogoutCallBack.LOGOUT_SUCCESS:
                iv_ID = R.drawable.logout_pass;
                tv_1_content = "申请注销，审核通过！";
                tv_2.setVisibility(View.GONE);
                btn_ok.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                btn_cancel.setText("取消注销");
                btn_ok.setText("确定注销");
                break;
        }
        iv.setBackgroundResource(iv_ID);
        tv_1.setText(tv_1_content);
        tv_2.setText(tv_2_content);
    }

    public void setCallBack(LogoutCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mParam1.equals(LogoutCallBack.LOGOUT_ING))
                    cancelLogout();
                else if (mParam1.equals(LogoutCallBack.LOGOUT_SUCCESS)) {
                    if (success_isAble)
                        callBack.sendSMS();
                    else {
                        ActivityUtils.showToast(getActivity(), "确认日期超时，请重新申请！");
                        callBack.onResult(LogoutCallBack.LOGOUT_START);
                    }
                } else if (mParam1.equals(LogoutCallBack.LOGOUT_FAIL))
                    callBack.onResult(LogoutCallBack.LOGOUT_START);
                break;
            case R.id.btn_cancel:
                cancelLogout();
                break;
        }
    }

    /**
     * 确认注销
     */
    public void confirmLogout(String SMSCode) {
        DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        OkGo.<String>post(Preferences.LOGOUT_CONFIRM).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").params("captcha", SMSCode).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    clearCache();
                    callBack.onResult(LogoutCallBack.LOGOUT_OVER);
                } else {
                    ActivityUtils.showToast(getActivity(), bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(getActivity(), "请检查网络。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    /**
     * 取消注销
     */
    private void cancelLogout() {
        DialogUtils.showMyDialog(getActivity(), Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        OkGo.<String>post(Preferences.LOGOUT_CANCEL).tag(this).params("userId", SPUtility.getUserId(getActivity()) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();
                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    callBack.onResult(LogoutCallBack.LOGOUT_CANCEL);
                } else {
                    ActivityUtils.showToast(getActivity(), bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(getActivity(), "请检查网络。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    /**
     * 清除数据
     *
     * @param
     */
    public void clearCache() {
        DownloadManager manager = DownloadManager.getInstance(getActivity());
        List<DocInfo> infos = manager.getListDone();
        for (int i = 0; i < infos.size(); i++) {
            manager.cancel(getActivity(), infos.get(i));
        } ActivityUtils.deleteFoder(getActivity().getCacheDir());
        ActivityUtils.deleteFoder(getActivity().getExternalCacheDir());

        new Thread(new Runnable() {
            @Override
            public void run() {
                ActivityUtils.deleteFoder(getActivity().getExternalFilesDir(null));
            }
        }).start();
        GlideCacheUtil.getInstance().clearImageAllCache(getActivity());
        SPUtility.clear(getActivity());
        JPushInterface.deleteAlias(getActivity(), 0);
    }
}