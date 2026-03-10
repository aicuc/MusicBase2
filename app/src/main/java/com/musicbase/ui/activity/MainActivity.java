package com.musicbase.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.musicbase.BaseActivity;
import com.musicbase.R;
import com.musicbase.adapter.MyViewPagerAdapter;
import com.musicbase.entity.JPushBean;
import com.musicbase.preferences.Preferences;
import com.musicbase.ui.fragment.BuyedFragment;
import com.musicbase.ui.fragment.ExamFragment;
import com.musicbase.ui.fragment.FirstFragment;
import com.musicbase.ui.fragment.Fragment_Browse;
import com.musicbase.ui.fragment.MeFragment;
import com.musicbase.ui.fragment.ThirdFragment;
import com.musicbase.ui.view.CustomViewpager;
import com.musicbase.util.DialogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private Fragment f_first, f_buyed, f_exam, f_me,f_browser;
    private CustomViewpager viewpager;
    private MyViewPagerAdapter adapter;
    private List<Fragment> list = new ArrayList<Fragment>();
    private int current_activity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        getData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int id = getIntent().getIntExtra("mainFlag", 0);
        final int courseId = getIntent().getIntExtra("courseId", 0);
        final int systemCodeId = getIntent().getIntExtra("systemCodeId", 0);

        if (id == 1) {
            changeClickToNormal(current_activity);
            setContent(2);
            if (courseId == 0 && systemCodeId == 0)
                return;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent intent2 = new Intent(MainActivity.this, DetailActivity.class);
                    intent2.putExtra("courseId", courseId);
                    intent2.putExtra("systemCodeId", systemCodeId);
                    intent2.putExtra("buyed", true);
                    startActivity(intent2);
                }
            }, 500);

        }
    }

    private void initView() {
        f_first = new FirstFragment();
        f_buyed = new BuyedFragment();
//        f_exam = new ExamFragment();
        f_me = new MeFragment();
        f_browser = new Fragment_Browse();
        ((MeFragment) f_me).setListener(new MainListener() {

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
                finish();
            }
        });
        list.add(f_first);
        list.add(f_buyed);
        list.add(f_browser);
        list.add(f_me);
        viewpager = (CustomViewpager) findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(4);
        adapter = new MyViewPagerAdapter(getSupportFragmentManager(), list);
        viewpager.setAdapter(adapter);

    }

    private void getData() {//接Login传来数据
        String rule = getIntent().getStringExtra("bindRule");
        if(!TextUtils.isEmpty(rule)){
            DialogUtils.showMyDialog(this, Preferences.SHOW_ERROR_DIALOG, "提示", rule, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    DialogUtils.dismissMyDialog();
                }
            });
        }
        JPushBean bean = getIntent().getParcelableExtra("extra");
        Log.e("MainActivity","MainActivity bean======="+bean);
        if(bean == null)
            return;
        if(!TextUtils.isEmpty(bean.getJumpType())){
            switch (bean.getJumpType()){
                case "url":
                    if(!TextUtils.isEmpty(bean.getJumpUrl())) {
                        Log.e("MainActivity","push----url="+bean.getJumpUrl());
                        Intent intent = new Intent(MainActivity.this, BrowerActivity.class);
                        intent.putExtra("filePath", bean.getJumpUrl());
                        intent.putExtra("name", bean.getTitle());
                        startActivity(intent);
                    }
                    break;
                case "recommendCourse":
                    if(!TextUtils.isEmpty(bean.getJumpUrl())) {
                        Log.e("MainActivity","push----id="+bean.getJumpUrl());
                        Intent intent = new Intent(this, DetailActivity.class);
                        intent.putExtra("courseId", Integer.parseInt(bean.getJumpUrl()));
                        startActivity(intent);
                    }
                    break;
                case "more":
                    if(!TextUtils.isEmpty(bean.getJumpUrl())) {
                        Log.e("MainActivity","push----more="+bean.getJumpUrl());
                        Intent intent = new Intent(this, ContentListActivity.class);
                        intent.putExtra("id", Integer.parseInt(bean.getJumpUrl()));
                        intent.putExtra("content", bean.getTitle());
                        startActivity(intent);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 点击状态变成正常状态
    private void changeClickToNormal(int current_activity) {
        LinearLayout ll = (LinearLayout) findViewById(getResources().getIdentifier("nav0" + current_activity, "id", "com.music.base"));
        // ll.setBackgroundResource(R.drawable.bg_tab_normal);
        ImageView iv = (ImageView) ll.getChildAt(0);
        iv.setImageResource(getResources().getIdentifier("tab_normal_image" + current_activity, "mipmap", "com.music.base"));
        TextView tv = (TextView) ll.getChildAt(1);
        tv.setTextColor(this.getResources().getColor(R.color.grey_666666));
    }

    // 点击状态变成正常状态
    private void changeNormalToClick(int current_activity) {
        LinearLayout ll = (LinearLayout) findViewById(getResources().getIdentifier("nav0" + current_activity, "id", "com.music.base"));
        // ll.setBackgroundResource(R.drawable.bg_tab_click);
        ImageView iv = (ImageView) ll.getChildAt(0);
        iv.setImageResource(getResources().getIdentifier("tab_click_image" + current_activity, "mipmap", "com.music.base"));
        TextView tv = (TextView) ll.getChildAt(1);
        tv.setTextColor(this.getResources().getColor(R.color.red_e61b19));
    }

    // 导航栏按键控制
    public void btnNavOnclick(View v) {
        int id = v.getId();
        if (id == R.id.nav01) {
            changeClickToNormal(current_activity);
            setContent(1);
        } else if (id == R.id.nav02) {
            changeClickToNormal(current_activity);
            setContent(2);
        } else if (id == R.id.nav03) {
            changeClickToNormal(current_activity);
            setContent(3);
//            DialogUtils.showMyDialog(this, Preferences.SHOW_CONFIRM_DIALOG, "跳转提示", "优惠拼购商品需在手机自带浏览器中支付，在本App中只能浏览不能支付，是否跳转到浏览器？", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.yinyuesuyang.com/group"));
//                    intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent5);
//                    DialogUtils.dismissMyDialog();
//                }
//            });
        } else if (id == R.id.nav04) {
            changeClickToNormal(current_activity);
            setContent(4);
        } else if (id == R.id.nav_music) {
            startActivity(new Intent(this, MusicActivity.class));
        }

    }

    public void setContent(int current_activity) {
        viewpager.setCurrentItem(current_activity - 1, false);
        changeNormalToClick(current_activity);
        this.current_activity = current_activity;
        // Intent intent=new Intent(getBaseContext(),BookActivity00.class);
        // startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (current_activity == 3 && ((Fragment_Browse) f_browser).canGoBack()) {
                ((Fragment_Browse) f_browser).goBack();
                return true;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                // SPUtility.putSPString(getApplicationContext(), "isPlay",
                // "false");
                // this.stopService(new Intent(this, MusicPlayerService.class));
                finish();
                // System.exit(0);
            }
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public interface MainListener {
        void onFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(current_activity == 4&&f_me!=null){
            ((MeFragment)f_me).setHeadImage();
        }
    }
}
