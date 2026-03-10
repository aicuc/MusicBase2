package com.musicbase.ui.fragment;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbase.MyApplication;
import com.musicbase.R;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.AudioDaoUtils;
import com.musicbase.entity.Bean;
import com.musicbase.entity.UpdateBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.activity.BrowerActivity;
import com.musicbase.ui.activity.BuyRecordActivity;
import com.musicbase.ui.activity.CacheActivity;
import com.musicbase.ui.activity.CardActivity;
import com.musicbase.ui.activity.ChooseHeadActivity;
import com.musicbase.ui.activity.LoginActivity;
import com.musicbase.ui.activity.MainActivity;
import com.musicbase.ui.activity.SecretDialogActivity;
import com.musicbase.ui.activity.SettingActivity;
import com.musicbase.ui.activity.UpdateActivity;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.DialogUtils;
import com.musicbase.util.GlideCacheUtil;
import com.musicbase.util.SPUtility;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.musicbase.preferences.Preferences.appInfo;
import static com.musicbase.preferences.Preferences.phoneMatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    @BindView(R.id.layout_cache)
    LinearLayout layoutCache;
    @BindView(R.id.study_card)
    LinearLayout study_card;
    @BindView(R.id.my_record)
    LinearLayout layoutRecord;
    @BindView(R.id.layout_version)
    LinearLayout layoutVersion;
    @BindView(R.id.layout_delete)
    LinearLayout layoutDelete;
    @BindView(R.id.layout_buy)
    LinearLayout layout_buy;
    @BindView(R.id.btn_exitlogin)
    Button btn_exitlogin;
    Unbinder unbinder;
    @BindView(R.id.btn_set)
    ImageButton btnSet;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.im)
    ImageView im;

    public MeFragment() {
        // Required empty public constructor
    }

    private MainActivity.MainListener listener;


    public MainActivity.MainListener getListener() {
        return listener;
    }

    public void setListener(MainActivity.MainListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        version.setText(getClientVersion());
        String phone = SPUtility.getSPString(getActivity(), "username");
        name.setText(phone.matches(phoneMatcher) ? phone : "");
        setHeadImage();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.study_card, R.id.layout_cache, R.id.layout_version, R.id.layout_buy, R.id.layout_delete, R.id.btn_set, R.id.my_record, R.id.btn_exitlogin, R.id.tv_fuwu, R.id.tv_yinsi,R.id.im})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.study_card:
                startActivity(new Intent(getActivity(), CardActivity.class));
                break;
            case R.id.layout_cache:
                startActivity(new Intent(getActivity(), CacheActivity.class));
                break;
            case R.id.layout_version:
                btnUpgrade();
                break;
            case R.id.layout_delete:
                clearCache();
                //                startActivity(new Intent(getActivity(), TestActivity.class));
                break;
            case R.id.layout_buy:
                startActivity(new Intent(getActivity(), BuyRecordActivity.class));
                break;
            case R.id.btn_set:
                startActivityForResult(new Intent(getActivity(), SettingActivity.class), 0);
                break;
            case R.id.my_record:
                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
                break;
            case R.id.btn_exitlogin:
                btnExitClick();
                break;
            case R.id.tv_fuwu:
                Intent intent = new Intent(getContext(), BrowerActivity.class);
                intent.putExtra("filePath", Preferences.XIEYI_URL);
                intent.putExtra("name", "用户协议");
                intent.putExtra("allowOpenInBrowser", 0);
                startActivity(intent);
                break;
            case R.id.tv_yinsi:
                Intent intent1 = new Intent(getContext(), BrowerActivity.class);
                intent1.putExtra("filePath", Preferences.YINSI_URL);
                intent1.putExtra("name", "隐私政策");
                intent1.putExtra("allowOpenInBrowser", 0);
                startActivity(intent1);
                break;
            case R.id.im:
                startActivity(new Intent(getActivity(), ChooseHeadActivity.class));
                break;
        }
    }

    public final static int EXIT_LOGIN = 0x01;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EXIT_LOGIN)
            listener.onFinish();
    }

    private void btnUpgrade() {
        Intent intent = new Intent(getContext(), UpdateActivity.class);
        UpdateBean.Data bean = new UpdateBean.Data();
        bean.setIsUpdate("3");
        intent.putExtra("update", bean);
        startActivity(intent);
    }


    /**
     * 清除数据
     *
     * @param
     */
    public void clearCache() {
        DialogUtils.showMyDialog(getContext(), Preferences.SHOW_CONFIRM_DIALOG, "清除数据", "是否清除所有数据？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager manager = DownloadManager.getInstance(getContext());
                List<DocInfo> infos = manager.getListDone();
                for (int i = 0; i < infos.size(); i++) {
                    manager.cancel(getContext(), infos.get(i));
                }
                ActivityUtils.deleteFoder(getContext().getCacheDir());
                ActivityUtils.deleteFoder(getContext().getExternalCacheDir());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityUtils.deleteFoder(getContext().getExternalFilesDir(null));
                        Looper.prepare();
                        ActivityUtils.showToast(getContext(), "数据清除完毕");
                        Looper.loop();
                    }
                }).start();
                GlideCacheUtil.getInstance().clearImageAllCache(getContext());
                AudioDaoUtils audioDaoUtils = new AudioDaoUtils(getContext());
                audioDaoUtils.deleteAudioItemAll();
                DialogUtils.dismissMyDialog();
            }
        });
    }

    public void btnExitClick() {
        DialogUtils.showMyDialog(getContext(), Preferences.SHOW_BUTTON_DIALOG, "退出登录", "取        消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.dismissMyDialog();
                cancel();
                JPushInterface.deleteAlias(getContext(), 0);
                clearSP();
                MyApplication.stopMusicActivity();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                listener.onFinish();
                getActivity().finish();
            }

        });
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = DialogUtils.dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        lp.height = (int) (display.getHeight()); // 设置高度
        DialogUtils.dialog.getWindow().setAttributes(lp);
    }

    private void clearSP() {
        if (!TextUtils.isEmpty(SPUtility.getSPString(getActivity(), "phoneId"))) {
            String id = SPUtility.getSPString(getActivity(), "phoneId");
            SPUtility.clear(getActivity());
            SPUtility.putSPString(getActivity(), "phoneId", id);
        } else
            SPUtility.clear(getActivity());
    }

    public void cancel() {
        String userId = SPUtility.getUserId(getActivity()) + "";
        RequestParams params = new RequestParams(Preferences.LOGOUT_URL);
        params.addQueryStringParameter("userId", userId);
        params.addQueryStringParameter("AppInfo", appInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {

                } else {
                    ActivityUtils.showToast(getContext(), bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(getContext(), "退出失败,请检查网络。");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private String getClientVersion() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }

    /**
     * 设置头像
     */
    public void setHeadImage(){

        String url = Preferences.IMAGE_HTTP_LOCATION+SPUtility.getSPString(getActivity(),"userAvatar");

        Glide.with(getActivity()).asBitmap().load(url).centerCrop().into(new BitmapImageViewTarget(im) {
            @Override
            protected void setResource(Bitmap resource) {
                if(getActivity()!=null&&getActivity().getResources()!=null) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    this.getView().setImageDrawable(circularBitmapDrawable);
                }
            }
        });
    }

}
