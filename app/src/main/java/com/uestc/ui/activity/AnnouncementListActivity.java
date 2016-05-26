package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.Announcement;
import com.uestc.domain.GetAnnouncementData;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.JsonTools;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * 这是公告列表的activity
 *
 */
public class AnnouncementListActivity extends Activity {

	private ImageView back;
	private ListView listView;
	private List<Announcement> list;
	private Announcement announcement;
	private GetAnnouncementData getAnnouncementData;
	private TextView title;
	private com.uestc.ui.adapter.AnnouncementAdapter announcementAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_announcementlist);
		ActivityCollector.addActivity(this);
		initView();
		announcementAdapter = new com.uestc.ui.adapter.AnnouncementAdapter(
				AnnouncementListActivity.this);
		listView.setAdapter(announcementAdapter);
		list = new ArrayList<Announcement>();
		new HanderAsyncTask().execute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		back = (ImageView) findViewById(R.id.btn_back_announcementlist);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listView = (ListView) findViewById(R.id.istview_announcementlist);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				announcement = new Announcement();
				announcement = list.get(arg2);
				Intent intent = new Intent(AnnouncementListActivity.this,
						AnnouncementActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("activity_announcementlist", announcement);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		title = (TextView) findViewById(R.id.tv_title_announcementlist);

	}

	private class HanderAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String jsonString = HttpUtils.HttpGet(
					AnnouncementListActivity.this, Host.getNotice);

			return jsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!result.equals("")) {
				ActivityCollector.LogOut(result,AnnouncementListActivity.this);
				getAnnouncementData = new GetAnnouncementData();
				getAnnouncementData = JsonTools.getAnnouncement(
						AnnouncementListActivity.this, "jsonString", result);
				if (getAnnouncementData.isStatus()) {
					list = getAnnouncementData.getList();
					if (list.size() == 0) {
						title.setText("暂无公告");
					} else {
						announcementAdapter.AddData(list);
						announcementAdapter.notifyDataSetChanged();
					}
				}else {
					Toast.makeText(AnnouncementListActivity.this,
							getAnnouncementData.getDescription(),
							Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(AnnouncementListActivity.this,
						"检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
			

		}
	}
}
