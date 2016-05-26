package com.uestc.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.Review;
import com.uestc.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 业主的我的绑定
 */
public class MyBindActivity extends Activity {
	private ImageButton back;
	private ListView listView;
	private List<Review> list;
	private com.uestc.ui.adapter.MyBindAdapter myBindAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mybind);
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
		list = new ArrayList<Review>();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listview_activity_bind);
		back = (ImageButton) findViewById(R.id.ib_back_activity_bind);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// reset_bangding = (TextView) findViewById(R.id.reset_binding);
		// reset_bangding.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent1 = new Intent(MyBindActivity.this,
		// TiedOwnersActivity.class);
		// startActivity(intent1);
		// }
		// });
		new getMyBindingAsyncTask().execute();
	}

	class getMyBindingAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String string = HttpUtils.HttpGet(MyBindActivity.this,
					Host.getBind+"?status=1");
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
							// Review review = new Review();
							// review.setId(object2.getInt("id"));
							// review.setCode(object2.getString("code"));
							// review.setName(object2.getString("name"));
							// review.setAddress(object2.getString("location"));
							// review.setPhone(object2.getString("phone"));
							// list.add(review);

							JSONObject object4 = object2
									.getJSONObject("propertyEntity");

							JSONObject object1 = object4.getJSONObject("buildingEntity");

							String location1 = object4.getString("location");
							String location2 = object1.getString("buildingName");
							String code = object4.getString("code");
							JSONArray jsonArray2 = object2
									.getJSONArray("bindUserInfos");
							for (int j = 0; j < jsonArray2.length(); j++) {
								JSONObject object3 = jsonArray2
										.getJSONObject(j);
								Review review = new Review();
								review.setCode(code);
								review.setAddress(location1+location2);
//								review.setPhone(object3.getString("phone"));
//								review.setName(object3.getString("userName"));
								int role = object3.getInt("role");
								if (role == 1) {
									review.setRole("家属");
								}else if (role == 2) {
									review.setRole("租户");
								}else if (role == 3){
									review.setRole("业主");
								}else {
									review.setRole("访客");
								}
								review.setId(object3.getInt("bindId"));
								JSONObject object5 = object3.getJSONObject("appUserEntity");
								review.setPhone(object5.getString("phone"));
								review.setName(object5.getString("nickname"));
								list.add(review);
							}

						}
						
						myBindAdapter = new com.uestc.ui.adapter.MyBindAdapter(MyBindActivity.this, list);
						listView.setAdapter(myBindAdapter);
						myBindAdapter.notifyDataSetChanged();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(MyBindActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			} else {
				Toast.makeText(MyBindActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
