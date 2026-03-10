package com.musicbase.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.adapter.LogoutReasonAdapter;
import com.musicbase.entity.Bean;
import com.musicbase.entity.LogoutProcessBean;
import com.musicbase.entity.LogoutReasonBean;
import com.musicbase.implement.LogoutCallBack;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.LoginActivity;
import com.musicbase.ui.activity.LogoutActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.musicbase.preferences.Preferences.phoneMatcher;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends Fragment {

    private View mRootView;
    private LogoutCallBack callBack;

    private final String URL = "http://www.pndoo.com/cancel_explanation.html";
    private WebView mWebView;
    private RecyclerView recyclerView;
    private EditText et;
    private Button btn_ok;
    private LogoutReasonAdapter adapter;
    private final String TAG = getClass().getSimpleName();

    public LogoutFragment() {
        // Required empty public constructor
    }

    public static LogoutFragment newInstance() {
        LogoutFragment fragment = new LogoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_logout_1, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        DialogUtils.showMyDialog(getContext(), Preferences.SHOW_PROGRESS_DIALOG, null, "加载中...", null);
        OkGo.<String>post(Preferences.LOGOUT_REASON).tag(this).params("userId", SPUtility.getUserId(getContext()) + "").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<LogoutReasonBean>() {
                }.getType();
                LogoutReasonBean bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    adapter.setNewData(bean.getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(getContext(), "请检查网络。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    private void initView() {
        mWebView = mRootView.findViewById(R.id.webview);
        mWebView.loadUrl(URL);

        recyclerView = mRootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LogoutReasonAdapter(null);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CheckBox cb = view.findViewById(R.id.cb);
                cb.setChecked(!cb.isChecked());
            }
        });
        et = mRootView.findViewById(R.id.et);
        btn_ok = mRootView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {
            String content = et.getText().toString().trim();
            if(TextUtils.isEmpty(content)&&adapter.getResultList().size()==0){//同时为空
                ActivityUtils.showToast(getContext(),"请填写注销原因");
            }else{
                callBack.sendSMS();
            }
        });
    }

    /**
     * 申请注销
     */
    public void requireLogout(String SMSCode) {
        StringBuffer s = new StringBuffer();
        for(int i=0;i<adapter.getResultList().size();i++){
            s.append(adapter.getResultList().get(i).getId());
            if(i!=adapter.getResultList().size()-1)
                s.append(",");
        }
        Log.e(TAG,"requireLogout---s="+s.toString());
        DialogUtils.showMyDialog(getContext(), Preferences.SHOW_PROGRESS_DIALOG_NO, null, "加载中...", null);
        OkGo.<String>post(Preferences.LOGOUT_REQUIRE).tag(this)
                .params("userId", SPUtility.getUserId(getContext()) + "")
                .params("captcha", SMSCode)
                .params("cancelReason", s.toString())
                .params("cancelRemarks", et.getText().toString().trim())
                .execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                Gson gson = new Gson();
                Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(response.body(), type);

                DialogUtils.dismissMyDialog();

                if (bean != null && bean.getCode().equals("SUCCESS")) {
                    callBack.onResult(LogoutCallBack.LOGOUT_ING);
                }else{
                    ActivityUtils.showToast(getActivity(),bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                DialogUtils.dismissMyDialog();
                ActivityUtils.showToast(getContext(), "请检查网络。");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    public void setCallBack(LogoutCallBack callBack) {
        this.callBack = callBack;
    }


}