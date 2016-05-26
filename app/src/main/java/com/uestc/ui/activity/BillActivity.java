package com.uestc.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.Person;
import com.uestc.constant.Host;

/**
 * 
 * 这是物业缴费的activity
 * 
 */
public class BillActivity extends Activity {

	private ImageButton back,refresh,webBack;
	private WebView payWebview;
	private String url;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pay);

		ActivityCollector.addActivity(this);
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}


	private void initView() {
        payWebview = (WebView) findViewById(R.id.pay_webview);
        payWebview.getSettings().setJavaScriptEnabled(true);
        payWebview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
        url = Host.payWebhost + "phone=" + Person.getPhone() + "&psw=" + Person.getPassword();
        Log.d("BillActivity", url);
        payWebview.loadUrl(url);

		back = (ImageButton) findViewById(R.id.pay_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		webBack = (ImageButton) findViewById(R.id.web_back);
		webBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				payWebview.goBack();
			}
		});

		refresh = (ImageButton) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				payWebview.reload();
			}
		});

	}
}
