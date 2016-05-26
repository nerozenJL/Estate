package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.ui.adapter.MyPropertyAdapter;
import com.uestc.domain.MyProperty;
import com.uestc.domain.Session;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是我的物业的activity
 */

public class MyPropertyActivity extends Activity {

	private ImageButton back,add;
	private ListView listView;
	private List<MyProperty> list;
	private MyPropertyAdapter myPropertyAdapter;
	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mypropert);
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
		list = new ArrayList<MyProperty>();
	}

	private void initView() {
		title = (TextView)findViewById(R.id.tv_title_mypropert);
		if (Session.getRole() != 3){
			title.setText("我的绑定");
		}
		back = (ImageButton) findViewById(R.id.ib_back_mypropert);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		listView = (ListView) findViewById(R.id.listview_mypropert);
		add = (ImageButton) findViewById(R.id.ib_add_mypropert);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MyPropertyActivity.this,TiedOwnersActivity.class);
				startActivity(intent);
			}
		});
		new getPropertyAsyncTask().execute();
	}

	class getPropertyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String string = HttpUtils.HttpGet(MyPropertyActivity.this,
					Host.getProperty);
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						if (list.size() != 0) {
							list.removeAll(list);
						}
						JSONArray jsonArray = object.getJSONArray("jsonString");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object2 = jsonArray.getJSONObject(i);
							MyProperty myProperty = new MyProperty();
							myProperty.setId(object2.getInt("id"));
							int role = object2.getInt("userRole");
							if (role == 3){
								myProperty.setRole(3);
								myProperty.setProperty_status("正常");
							}else{
								myProperty.setRole(role);
								int sta = object2.getInt("status");

								if (sta == 1){
									myProperty.setProperty_status("已审核");
								}else{
									myProperty.setProperty_status("未审核");
								}
							}

							JSONObject object1 = object2.getJSONObject("propertyEntity");



							myProperty.setCode(object1.getString("code"));
							myProperty.setLocation(object1
									.getString("location"));
							myProperty.setPropertySquare(object1
									.getInt("propertySquare") + "");

							int type = object1.getInt("type");

							if (type == 1) {

								myProperty.setType("商户");
							} else {
								myProperty.setType("住宅");
							}

							int st = object2.getInt("status");
							if (st == -1) {
								myProperty.setStatus("出租");
							} else {
								myProperty.setStatus("自用");
							}
							JSONObject object3 = object1.getJSONObject("buildingEntity");

							myProperty.setBuildingName(object3.getString("buildingName"));

							list.add(myProperty);
						}

						myPropertyAdapter = new MyPropertyAdapter(
								MyPropertyActivity.this, list);
						listView.setAdapter(myPropertyAdapter);
						myPropertyAdapter.notifyDataSetChanged();

					} else {
						Session.setId(-1);
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(MyPropertyActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					
				}

			} else {
				Toast.makeText(MyPropertyActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}

		}

	}
}
