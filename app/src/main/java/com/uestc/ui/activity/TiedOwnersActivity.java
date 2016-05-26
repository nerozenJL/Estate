package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.Select;
import com.uestc.domain.Session;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定物业的activity
 */
public class TiedOwnersActivity extends Activity {

	private Button bangding;
	private Spinner park, ban, property, spinner2;
	private ImageButton back;
	// private ArrayList<String> relative, hoseinformation_list;
	private ArrayAdapter<String> parkAdapter, relativeAdapter, banAdapter,
			propertyadAdapter;
	private RadioGroup roleRadioGroup;
	private List<String> relative, park_list1, ban_list1, property_list1;// ��������text
	private List<Select> park_list, ban_list, property_list;
	private String park_id = "", ban_id = "", property_id = "";
	private int role;
	private static String fromwhichActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tied_oweners);
		ActivityCollector.addActivity(this);
		Intent intent=getIntent();
		fromwhichActivity=intent.getStringExtra("fromwhichActivity");
		park_list = new ArrayList<Select>();// ԰��
		ban_list = new ArrayList<Select>();// ¥��
		property_list = new ArrayList<Select>();// ��ҵ
		park_list1 = new ArrayList<String>();
		ban_list1 = new ArrayList<String>();
		property_list1 = new ArrayList<String>();
		parkAdapter = new ArrayAdapter<String>(TiedOwnersActivity.this,
				android.R.layout.simple_spinner_item, park_list1);
		banAdapter = new ArrayAdapter<String>(TiedOwnersActivity.this,
				android.R.layout.simple_spinner_item, ban_list1);
		propertyadAdapter = new ArrayAdapter<String>(TiedOwnersActivity.this,
				android.R.layout.simple_spinner_item, property_list1);
		new TieParksAsyncTask().execute();
		initview();
		setdata();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void setdata() {

//		relative = new ArrayList<String>();
//		relative.add("家属");
//		relative.add("租户");
//		relativeAdapter = new ArrayAdapter<String>(TiedOwnersActivity.this,
//				android.R.layout.simple_spinner_item, relative);
//		spinner2.setAdapter(relativeAdapter);
//
//		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				role = arg2 + 1;
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//
//			}
//
//		});

		park = (Spinner) findViewById(R.id.sp_park_tied_oweners);
		park.setAdapter(parkAdapter);
		ban = (Spinner) findViewById(R.id.sp_ban_tied_oweners);
		ban.setAdapter(banAdapter);
		property = (Spinner) findViewById(R.id.sp_property_tied_oweners);
		property.setAdapter(propertyadAdapter);
	}

	private void initview() {

		roleRadioGroup = (RadioGroup)findViewById(R.id.role_radioGroup);
		roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId){
					case R.id.relative_role_button:
						role=1;
						break;
					case R.id.rentor_role_button:
						role=2;
						break;
					case R.id.visitor_role_button:
						role=0;
						break;
				}
			}
		});

		bangding = (Button) findViewById(R.id.btn_tie_tied_oweners);
		bangding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (property_list1.size() == 0) {
					Toast.makeText(TiedOwnersActivity.this, "该园区没有物业可供绑定或者你还没有绑定物业", Toast.LENGTH_SHORT).show();
					bangding.setEnabled(true);
					bangding.setBackgroundResource(R.color.style);
				}else {
					bangding.setClickable(false);
					bangding.setBackgroundResource(R.color.gray1);
					new ApplyapplyAsncTask().execute();
				}
				
			}
		});
		//spinner2 = (Spinner) findViewById(R.id.sp_relatives_tied_oweners);
		back = (ImageButton) findViewById(R.id.ibtn_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class TieParksAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String returnString = HttpUtils.HttpGet(TiedOwnersActivity.this,
					Host.getAllVillage);

			return returnString;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			if (!result.equals("")) {
				/*****/
				try {
					JSONObject object = new JSONObject(result);
					JSONArray jsonArray = object.getJSONArray("jsonString");
					boolean status = object.getBoolean("status");
					if (status) {
						if (park_list.size() != 0) {
							park_list.removeAll(park_list);
						}
						if (park_list1.size() != 0) {
							park_list1.removeAll(park_list1);
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.getJSONObject(i);
							Select select = new Select();
							select.setText(jsonObject2.getString("text"));
							select.setId(jsonObject2.getString("id"));
							park_list.add(select);
							park_list1.add(jsonObject2.getString("text"));
						}

						
						parkAdapter.notifyDataSetChanged();
						park.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								
								if (property_list.size()!= 0) {
									property_list.removeAll(property_list);
								}
								if (property_list1.size()!=0) {
									property_list1.removeAll(property_list1);
								}
								propertyadAdapter.notifyDataSetChanged();
								String string = park_list1.get(arg2);

								for (int i = 0; i < park_list.size(); i++) {
									if (string.equals(park_list.get(i)
											.getText())) {
										park_id = park_list.get(i).getId();
										break;
									}
								}
								new TieBansAsyncTask().execute(park_id);

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								

							}
						});

					} else {
						Toast.makeText(TiedOwnersActivity.this, "得到园区列表失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else {
				Toast.makeText(TiedOwnersActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	class TieBansAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {

			String returnString = HttpUtils.HttpGet(TiedOwnersActivity.this,
					Host.getAllBuilding + "/" + arg0[0]);
			return returnString;
		}

		@Override
		protected void onPostExecute(String arg0) {
			
			super.onPostExecute(arg0);
			if (!arg0.equals("")) {
				/*****/
				try {
					JSONObject object = new JSONObject(arg0);
					JSONArray jsonArray = object.getJSONArray("jsonString");
					boolean status = object.getBoolean("status");
					if (ban_list.size() != 0) {
						ban_list.removeAll(ban_list);
					}
					if (ban_list1.size() != 0) {
						ban_list1.removeAll(ban_list1);
					}
					if (status) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.getJSONObject(i);
							Select select = new Select();
							select.setText(jsonObject2.getString("text"));
							select.setId(jsonObject2.getString("id"));
							ban_list.add(select);
							ban_list1.add(jsonObject2.getString("text"));
						}
						
						ban_id = ban_list.get(0).getId();
						new TiePropertyAsyncTask().execute(ban_id);
						banAdapter.notifyDataSetChanged();
						ban.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {

								String string = ban_list1.get(arg2);

								for (int i = 0; i < ban_list.size(); i++) {
									if (string
											.equals(ban_list.get(i).getText())) {
										ban_id = ban_list.get(i).getId();
										break;
									}
								}
								new TiePropertyAsyncTask().execute(ban_id);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								

							}
						});

					} else {
						Toast.makeText(TiedOwnersActivity.this, "得到楼栋列表失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}

			} else {
				Toast.makeText(TiedOwnersActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	class TiePropertyAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String returnString = HttpUtils.HttpGet(TiedOwnersActivity.this,
					Host.getAllProperty + "?villageID=" + park_id + "&"
							+ "buildingID=" + arg0[0]);
			return returnString;
		}

		@Override
		protected void onPostExecute(String arg0) {
			super.onPostExecute(arg0);
			if (!arg0.equals("")) {
				/*****/
				try {
					JSONObject object = new JSONObject(arg0);
					JSONArray jsonArray = object.getJSONArray("jsonString");
					boolean status = object.getBoolean("status");
					if (status) {
						if (property_list.size() != 0) {
							property_list.removeAll(property_list);
						}
						if (property_list1.size() != 0) {
							property_list1.removeAll(property_list1);
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.getJSONObject(i);
							Select select = new Select();
							select.setText(jsonObject2.getString("text"));
							select.setId(jsonObject2.getString("id"));
							property_list.add(select);
							property_list1.add(jsonObject2.getString("text"));
						}

						
						propertyadAdapter.notifyDataSetChanged();
						property.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {

								String string = property_list1.get(arg2);
								for (int i = 0; i < property_list.size(); i++) {
									if (string.equals(property_list.get(i)
											.getText())) {
										property_id = property_list.get(i)
												.getId();
										break;
									}
								}
							}
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								

							}
						});

					} else {
						Toast.makeText(TiedOwnersActivity.this, "得到物业列表失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}

			} else {
				Toast.makeText(TiedOwnersActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class ApplyapplyAsncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			String resultString = sendHouseInformation(role + "", park_id,
					ban_id, property_id);

			return resultString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				// Gson gson = new Gson();
				// BasicJson basicJson = gson.fromJson(result, BasicJson.class);
				// JSONObject object;
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						Toast.makeText(TiedOwnersActivity.this, "申请发送成功",
								Toast.LENGTH_SHORT).show();
						if (fromwhichActivity==null){
							finish();
							return;
						}
						if (fromwhichActivity.equals("MeActivity3")){
							MeActivity3.guestActivity.finish();
							finish();
						}else if (fromwhichActivity.equals("HomeActivity")){
							finish();
						}
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(TiedOwnersActivity.this, description,
								Toast.LENGTH_SHORT).show();
						bangding.setEnabled(true);
						bangding.setBackgroundResource(R.color.style);
						finish();
					}
				} catch (JSONException e) {
					bangding.setClickable(true);
					bangding.setBackgroundResource(R.color.style);
					Toast.makeText(TiedOwnersActivity.this, "出现异常",
							Toast.LENGTH_SHORT).show();
					bangding.setEnabled(true);
					bangding.setBackgroundResource(R.color.style);
					e.printStackTrace();
				}

			} else {
				bangding.setClickable(true);
				bangding.setBackgroundResource(R.color.style);
				Toast.makeText(TiedOwnersActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
				bangding.setEnabled(true);
				bangding.setBackgroundResource(R.color.style);
			}

		}

	}

	public String sendHouseInformation(String role, String prak_id,
			String ban_id, String property_id) {
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.bind);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();

		paramters.add(new BasicNameValuePair("role", role));
		paramters.add(new BasicNameValuePair("villageID", prak_id));
		paramters.add(new BasicNameValuePair("buildingID", ban_id));
		paramters.add(new BasicNameValuePair("propertyID", property_id));
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(paramters, "UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

}
