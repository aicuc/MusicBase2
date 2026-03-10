package com.musicbase.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.download2.Utils.DownloadManager;
import com.musicbase.download2.entity.DocInfo;
import com.musicbase.entity.Bean;
import com.musicbase.entity.UpdateBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.MeFragment;
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
import cn.jpush.android.api.JPushInterface;

import static com.musicbase.preferences.Preferences.appInfo;

public class SettingActivity extends BaseActivity {


    @BindView(R.id.titlelayout_back)
    ImageButton titlelayoutBack;
    @BindView(R.id.titlelayout_title)
    TextView titlelayoutTitle;
    @BindView(R.id.switch_online)
    ToggleButton switchOnline;
    @BindView(R.id.wifi_download)
    LinearLayout wifiDownload;
    @BindView(R.id.switch_info)
    ToggleButton switchInfo;
    @BindView(R.id.info)
    LinearLayout info;
    @BindView(R.id.account)
    LinearLayout account;
    @BindView(R.id.clear_cache)
    LinearLayout clearCache;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.upgrade)
    LinearLayout upgrade;
    @BindView(R.id.btn_exitlogin)
    Button btnExitlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titlelayoutTitle.setText("设置");
        titlelayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        version.setText(getClientVersion());
        switchOnline.setChecked(Boolean.parseBoolean(SPUtility.getSPString(this, "switchKey")));
        switchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtility.putSPString(SettingActivity.this, "switchKey", String.valueOf(isChecked));
            }
        });
    }

    private String getClientVersion() {
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
    }


    @OnClick({R.id.titlelayout_back, R.id.account, R.id.clear_cache, R.id.upgrade, R.id.btn_exitlogin,R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.titlelayout_back:
                finish();
                break;
            case R.id.account:
                Intent intentAccount = new Intent(SettingActivity.this, AccountActivity.class);
                startActivity(intentAccount);
                break;
            case R.id.clear_cache:
                clearCache();
                break;
            case R.id.upgrade:
                btnUpgrade();
                break;
            case R.id.btn_exitlogin:
                btnExitClick();
                break;
            case R.id.logout:
                startActivityForResult(new Intent(SettingActivity.this,LogoutActivity.class),0);
                break;
        }
    }

    private void btnUpgrade() {
        Intent intent = new Intent(SettingActivity.this, UpdateActivity.class);
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
        DialogUtils.showMyDialog(this, Preferences.SHOW_CONFIRM_DIALOG, "清除数据", "是否清除所有数据？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager manager = DownloadManager.getInstance(SettingActivity.this);
                List<DocInfo> infos = manager.getListDone();
                for (int i = 0; i < infos.size(); i++) {
                    manager.cancel(SettingActivity.this, infos.get(i));
                }
                ActivityUtils.deleteFoder(getCacheDir());
                ActivityUtils.deleteFoder(getExternalCacheDir());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityUtils.deleteFoder(getExternalFilesDir(null));
                        Looper.prepare();
                        ActivityUtils.showToast(SettingActivity.this, "数据清除完毕");
                        Looper.loop();
                    }
                }).start();
                GlideCacheUtil.getInstance().clearImageAllCache(SettingActivity.this);
                DialogUtils.dismissMyDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==0x01){
            setResult(MeFragment.EXIT_LOGIN);
            finish();
        }
    }

    public void btnExitClick() {
        DialogUtils.showMyDialog(SettingActivity.this, Preferences.SHOW_BUTTON_DIALOG, "退出登录", "取        消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.dismissMyDialog();
                cancel();
                SPUtility.clear(SettingActivity.this);
                JPushInterface.deleteAlias(SettingActivity.this, 0);
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                setResult(MeFragment.EXIT_LOGIN);
                finish();
            }

        });
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = DialogUtils.dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        lp.height = (int) (display.getHeight()); // 设置高度
        DialogUtils.dialog.getWindow().setAttributes(lp);
    }

    public void cancel() {
        String userId = SPUtility.getUserId(this) + "";
        RequestParams params = new RequestParams(Preferences.LOGOUT_URL);
        params.addQueryStringParameter("userId", userId);
        params.addQueryStringParameter("AppInfo",appInfo);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<Bean>() {
                }.getType();
                Bean bean = gson.fromJson(s, type);

                if (bean != null && bean.getCode().equals("SUCCESS")) {

                } else {
                    ActivityUtils.showToast(SettingActivity.this, bean.getCodeInfo());
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ActivityUtils.showToast(SettingActivity.this, "退出失败,请检查网络。");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
