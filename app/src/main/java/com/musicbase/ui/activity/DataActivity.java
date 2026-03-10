package com.musicbase.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.musicbase.SplashADActivity;
import com.musicbase.entity.JPushBean;

import java.util.List;
/**
 * 跳转接入参数
 * type 详情页：recommendCourse 需要id
 * 网页：url 需要id
 * 更多：more 需要id和name
 * 首页：home
 * 音基 pndoo://yinyuesuyang:8888/home?id=''&name=''&type=''
 * 加阅 pndoo://jiayue:8888/home?id= 只有首页
 */
public class DataActivity extends Activity {
	private final String TAG = getClass().getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String id = null;
		String type = null;
		String name = null;
		Uri uri = getIntent().getData();
		if (uri != null) {
		    // 完整的url信息
		    String url = uri.toString();
		    Log.e(TAG, "url: " + uri);
		    // scheme部分
		    String scheme = uri.getScheme();
		    Log.e(TAG, "scheme: " + scheme);
		    // host部分
		    String host = uri.getHost();
		    Log.e(TAG, "host: " + host);
		    //port部分
		    int port = uri.getPort();
		    Log.e(TAG, "host: " + port);
		    // 访问路劲
		    String path = uri.getPath();
		    Log.e(TAG, "path: " + path);
		    List<String> pathSegments = uri.getPathSegments();
		    // Query部分
		    String query = uri.getQuery();
		    Log.e(TAG, "query: " + query);
		    //获取指定参数值
		    String goodsId = uri.getQueryParameter("goodsId");
		    Log.e(TAG, "goodsId: " + goodsId);
		    id = uri.getQueryParameter("id");
            Log.e(TAG, "id: " + id);
            name = uri.getQueryParameter("name");
            Log.e(TAG, "name: " + name);

            type = uri.getQueryParameter("type");
            Log.e(TAG, "type: " + type);
		}
		Intent intent = new Intent(this, SplashADActivity.class);
		if(!TextUtils.isEmpty(id)){
            JPushBean bean = new JPushBean();
            bean.setJumpType(type);
            bean.setJumpUrl(id);
            bean.setTitle(name);
		    intent.putExtra("extra",bean);
        }
        startActivity(intent);
		finish();
	}
}
