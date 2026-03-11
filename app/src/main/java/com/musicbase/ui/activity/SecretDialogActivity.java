package com.musicbase.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.preferences.Preferences;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SecretDialogActivity extends BaseActivity {
    TextView tv_content, tv_cancel;
    Button btn_ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_yinsixieyi);
        initView();
    }

    private void initView() {
        tv_content = findViewById(R.id.tv_yinsi_content);
        tv_cancel = findViewById(R.id.btn_cancel);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("first_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                editor.putBoolean("isFirstIn2", false);
                // 提交修改
                editor.commit();
                setResult(1000);
                finish();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        String content = getFromAssets();
        content = content.replace("\\n", "\n");
        Spannable spannable = new SpannableStringBuilder(content);

        // 动态查找《隐私政策》位置，避免硬编码偏移量
        int yinsiStart = content.indexOf("《隐私政策》");
        int yinsiEnd = yinsiStart + "《隐私政策》".length();
        if (yinsiStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(SecretDialogActivity.this, BrowerActivity.class);
                    intent.putExtra("filePath", Preferences.YINSI_URL);
                    intent.putExtra("name", "隐私政策");
                    intent.putExtra("allowOpenInBrowser", 0);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(SecretDialogActivity.this.getResources().getColor(R.color.red_e61b19));
                    ds.setUnderlineText(false);
                }
            }, yinsiStart, yinsiEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // 动态查找《用户服务协议》位置
        int xieyiStart = content.indexOf("《用户服务协议》");
        int xieyiEnd = xieyiStart + "《用户服务协议》".length();
        if (xieyiStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(SecretDialogActivity.this, BrowerActivity.class);
                    intent.putExtra("filePath", Preferences.XIEYI_URL);
                    intent.putExtra("name", "用户协议");
                    intent.putExtra("allowOpenInBrowser", 0);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(SecretDialogActivity.this.getResources().getColor(R.color.red_e61b19));
                    ds.setUnderlineText(false);
                }
            }, xieyiStart, xieyiEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        tv_content.setText(spannable);
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public String getFromAssets() {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open("tishi.txt"));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                result += line;
            return result;
        } catch (Exception e) {
            return result;
        }
    }
}
