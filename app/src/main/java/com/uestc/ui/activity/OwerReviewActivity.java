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
import com.uestc.ui.adapter.OwerReviewAdapter;
import com.uestc.domain.Review;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是个人中的审核绑定的界面
 */
public class OwerReviewActivity extends Activity {

	private ListView listView;
	private ImageButton imageButton;
	private List<Review> list;
	private OwerReviewAdapter owerReviewAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ower_review);
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

		listView = (ListView) findViewById(R.id.ll_ower_review);
		imageButton = (ImageButton) findViewById(R.id.ib_back);
		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		new setDataAsyncTask().execute();
	}

	class setDataAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String string = HttpUtils.HttpGet(OwerReviewActivity.this,
					Host.getBind+"?status=0");
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
					
					owerReviewAdapter = new OwerReviewAdapter(OwerReviewActivity.this,
							list);
					listView.setAdapter(owerReviewAdapter);
					owerReviewAdapter.notifyDataSetChanged();
				}else {
					JSONObject object2 =object.getJSONObject("errorMsg");
					Toast.makeText(OwerReviewActivity.this,object2.getString("description"), Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
				
				
				
			}else {
				Toast.makeText(OwerReviewActivity.this, "检查网络设置", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
