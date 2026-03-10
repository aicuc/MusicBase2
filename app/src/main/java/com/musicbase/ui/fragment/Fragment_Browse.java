package com.musicbase.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.musicbase.R;
import com.musicbase.ui.view.ProgressWebview;


/**
 * Created by BAO on 2016-08-09.
 */
public class Fragment_Browse extends Fragment {

	private View mRootView;
	private String filepath = "https://h5.yinyuesuyang.com";
	private ProgressWebview mWebView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_browser,null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		initView();
    }

    public void initView() {

		mWebView = (ProgressWebview) mRootView.findViewById(R.id.wv_brower);
		WebSettings ws = mWebView.getSettings();
//		webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		ws.setDomStorageEnabled(true);
		ws.setJavaScriptEnabled(true);
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
		ws.setAllowFileAccess(true);
		ws.setAllowFileAccessFromFileURLs(true);
		ws.setAllowUniversalAccessFromFileURLs(true);	
		// 是否允许缩放
		ws.setBuiltInZoomControls(false);
		ws.setSupportZoom(false);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ws.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}




		mWebView.setWebViewClient(new WebViewClient() {
			// 这个函数我们可以做很多操作，比如我们读取到某些特殊的URL，于是就可以不打开地址，取消这个操作，进行预先定义的其他操作，这对一个程序是非常必要的。
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
//				Log.d(TAG, "url====" + url);
//				// ActivityUtils.showToast(JDWebViewActivity.this,
//				// webView.getUrl().equals(URL)+url);
//				if (model == MODEL_SHANGCHENG && !url.equals("http://www.pndoo.com/link_list.html")) {
//					Log.d(TAG, "111111111111111111111====" + url);
//					Intent intent = new Intent(getActivity(), BrowerActivity.class);
//					intent.putExtra("filePath", url);
//					intent.putExtra("model", BrowerActivity.MODEL_BROWSER);
//					startActivity(intent);
//					return true;
//				}
				return false;
			}
		});
		
		mWebView.loadUrl(filepath);
	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public boolean canGoBack(){
		return mWebView.canGoBack();
	}
	
	public void goBack(){
		mWebView.goBack();
	}
}
