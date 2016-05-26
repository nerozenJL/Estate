package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.Announcement;
import com.uestc.constant.Host;
/**
 * 
 * 这是具体公告的界面
 *
 */
public class AnnouncementActivity extends Activity{
	private Announcement announcement;
	private TextView title;
	private WebView webView;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_announcement);
		ActivityCollector.addActivity(this);
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initData() {
		Intent intent=getIntent();
		Bundle bundle = intent.getExtras();
		announcement=(Announcement) bundle.getSerializable("activity_announcementlist");
		
	}

	private void initView() {
		back=(ImageView) findViewById(R.id.btn_back_announcement);

		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		title=(TextView) findViewById(R.id.tv_title_announcement);
		title.setText(announcement.getTitle());

		
	    webView=(WebView) findViewById(R.id.webview_announcement);
	    webView.loadUrl(Host.host+"/notice/getContent/"+announcement.getNoticeId());

	}
	
}
