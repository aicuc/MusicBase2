package com.musicbase.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.musicbase.R;
import com.musicbase.entity.CardBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.util.ActivityUtils;
import com.musicbase.util.SPUtility;
import com.orhanobut.logger.Logger;

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText card_num;
    private ImageView btn_back;
    private ImageView scan_img;
    private WebView card_web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        initView();
    }

    private void initView() {
        card_num = (EditText) findViewById(R.id.card_num);
        card_num.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit();
                    InputMethodManager inputManager =
                            (InputMethodManager) CardActivity.this.
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(
                            CardActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }

                return false;
            }
        });


        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        scan_img = (ImageView) findViewById(R.id.scan_img);
        scan_img.setOnClickListener(this);
        card_web = (WebView) findViewById(R.id.card_web);
        card_web.setOnClickListener(this);
        card_web.loadUrl("https://www.yinyuesuyang.com/cardInfo.html");
        WebSettings settings = card_web.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //支持javascript
        settings.setJavaScriptEnabled(true);
// 设置可以支持缩放
        settings.setSupportZoom(true);
// 设置出现缩放工具
        settings.setBuiltInZoomControls(true);
//扩大比例的缩放
        settings.setUseWideViewPort(true);
//自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
    }

    private void findBook(String code_id, int type) {//根据卡号判断是否存在或需要卡密，type=0手动输入卡号，1扫二维码得到卡号
        if (!TextUtils.isEmpty(code_id)) {
            ProgressDialog pd2 = ProgressDialog.show(CardActivity.this, "温馨提示", "正在查询，请稍后。。。");

            OkGo.<String>post(Preferences.CHECK_CARD)
                    .tag(this)
                    .params("cardCode", code_id)
                    .params("addType", type == 0 ? "manual" : "qrcode")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Logger.d(response);
                            Gson gson = new Gson();
                            CardBean bean = gson.fromJson(response.body(), CardBean.class);
                            if (bean != null) {
                                if (bean.getCode().equals("SUCCESS") && bean.getData() != null) {
                                    if (bean.getData().getIsNeedPwd().equals("1"))
                                        enterPass(code_id, type);
                                    else if (bean.getData().getIsNeedPwd().equals("0"))
                                        addSuccess(code_id, type, "");
                                    else
                                        ActivityUtils.showToast(CardActivity.this, bean.getCodeInfo());
                                } else if (bean.getCode().equals("FAIL")) {
                                    ActivityUtils.showToast(CardActivity.this, bean.getCodeInfo());
                                } else {
                                    ActivityUtils.showToast(CardActivity.this, bean.getCodeInfo());

//                                    enterPass(code_id, type);
                                }


                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            Logger.e(response.getException().getMessage());
                            ActivityUtils.showToast(CardActivity.this, getString(R.string.load_fail) + ",请检查网络");
                        }

                        @Override
                        public void onFinish() {
                            pd2.dismiss();

                        }

                    });
        }
    }

    private void addSuccess(String code_id, int type, String card_pass) {//添加成功
        ProgressDialog pd2 = ProgressDialog.show(CardActivity.this, "温馨提示", "正在添加，请稍后。。。");
        OkGo.<String>post(Preferences.COURSE_CARD)
                .tag(this)
                .params("userId", SPUtility.getUserId(CardActivity.this) + "")
                .params("cardCode", code_id)
                .params("cardPass", card_pass)
                .params("addType", type == 0 ? "manual" : "qrcode")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Logger.d(response);
                        Gson gson = new Gson();
                        CardBean bean = gson.fromJson(response.body(), CardBean.class);
                        if (bean != null) {
                            if (bean.getCode().equals("SUCCESS") && bean.getData() != null) {
                                new AlertDialog.Builder(CardActivity.this)
                                        .setTitle("添加成功")
                                        .setPositiveButton("确认", null).create().show();
                            } else if (bean.getCode().equals("FAIL")) {
                                ActivityUtils.showToast(CardActivity.this, bean.getCodeInfo());
                            } else {
                                ActivityUtils.showToast(CardActivity.this, bean.getCodeInfo());

//                                startActivity(new Intent(CardActivity.this, Cardsuccess.class));
//                                new AlertDialog.Builder(CardActivity.this)
//                                        .setTitle("添加成功")
//                                        .setPositiveButton("确认", null).create().show();
                            }


                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Logger.e(response.getException().getMessage());
                        ActivityUtils.showToast(CardActivity.this, getString(R.string.load_fail) + ",请检查网络");
                    }

                    @Override
                    public void onFinish() {
                        pd2.dismiss();

                    }

                });

    }

    private void enterPass(String code_id, int type) {//输入卡密
        final EditText et = new EditText(CardActivity.this);
        et.setBackground(getResources().getDrawable(R.drawable.inputbox_bg));
        et.setHint("请输入卡密码");
        et.setPadding(10, 0, 10, 0);
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 0, 50, 0);

        et.setLayoutParams(lp);
        linearLayout.addView(et);

        TextView title = new TextView(this);
        title.setText("输入卡密码");
        title.setPadding(10, 30, 10, 50);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(R.color.black_333333));
        title.setTextSize(20);
        new AlertDialog.Builder(CardActivity.this)
                .setView(linearLayout)
                .setCustomTitle(title)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        addSuccess(code_id, type, input);
                        dialog.dismiss();

                    }
                }).setNegativeButton("取消", null).create().show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:

                finish();
                break;
            case R.id.scan_img:
                new IntentIntegrator(this).initiateScan();

                break;
        }
    }

    private void submit() {
        // validate
        String num = card_num.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(this, "请输入卡号", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something
        findBook(num, 0);


    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫码无效！", Toast.LENGTH_LONG).show();
            } else {
                findBook(result.getContents(), 1);
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
