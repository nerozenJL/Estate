package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.ui.adapter.MyRepairAdapter;
import com.uestc.domain.MyRepair;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.GetData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是我的报修列表展示的activity
 */
public class MyRepairListActivity extends Activity{
	private ImageButton back;
//	private SwipeListView listView;
	private ListView listView;
	private List<MyRepair> list;
	private MyRepairAdapter adapter;
	int Position = 0;
	private ImageButton repair;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_repaire);
		ActivityCollector.addActivity(this);
		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("list_test","exeTest");
		new getRepairListASyncTask().execute();
	}
	private void initData() {
		list = new ArrayList<MyRepair>();
	}

	private void initView() {
	
		back=(ImageButton) findViewById(R.id.ib_back_myrepaire);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		repair=(ImageButton) findViewById(R.id.ib_addrepair_myrepaire);
		repair.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MyRepairListActivity.this,RepairActivity.class);
				startActivity(intent);
			}
		});
		listView=(ListView) findViewById(R.id.listview_myrepaire);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				MyRepair myRepair;
				myRepair = list.get(arg2);
				Intent intent = new Intent(MyRepairListActivity.this,
						MyRepairActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("myRepair", myRepair);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		

	}
	
	class getRepairListASyncTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... arg0) {
			Log.d("list_test","backgroundTest");
			String string = HttpUtils.HttpGet(MyRepairListActivity.this,Host.getRepair);
			Log.d("MyRepairListActivity",string);
			return string;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				JSONObject object;
				ActivityCollector.LogOut(result,MyRepairListActivity.this);
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						JSONArray jsonArray = object.getJSONArray("jsonString");
						if (list.size() != 0) {
							list.removeAll(list);
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object2 = jsonArray.getJSONObject(i);
							MyRepair repair = new MyRepair();
							repair.setId(object2.getInt("id"));
							repair.setTitle(object2.getString("title"));
							repair.setContent(object2.getString("content"));
							String description = object2.getString("description");

							if(!object2.getString("remark").equals("")&&!object2.getString("remark").equals("null")) {
								repair.setRemark(object2.getDouble("remark"));
							}else{
								repair.setRemark(100);
							}

							if(!object2.getString("remarkText").equals("null")) {
								repair.setRemarkText(object2.getString("remarkText"));
							}else{
								repair.setRemarkText("");
							}
							if(!object2.getString("repairManEntity").equals("")&&!object2.getString("repairManEntity").equals("null")) {
								JSONObject repairManObj = object2.getJSONObject("repairManEntity");
								repair.setRepairPhone(repairManObj.getString("phone"));
								repair.setRepairName(repairManObj.getString("name"));
							}else {
								repair.setRepairPhone("暂无数据");
								repair.setRepairName("暂无数据");
							}

							if (description!=null&&!description.equals("")&&!description.equals("null")) {
								repair.setDescription(description);
							}else {
								repair.setDescription("");
							}
							String result1 = object2.getString("result");
							if (result1.equals("") || result1.equals("null")) {
								repair.setResult("");
							} else {
								repair.setResult(result1);
							}
//							repair.setType(object2.getInt("type"));
							repair.setSubmitTime(GetData.getdata(object2.getLong("submitTime")));
							repair.setStatus(object2.getInt("status"));
							list.add(repair);
						}
						adapter = new MyRepairAdapter(MyRepairListActivity.this,list);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(MyRepairListActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					}

				}else {
				Toast.makeText(MyRepairListActivity.this, "检查网络设置", Toast.LENGTH_SHORT).show();
			}
			
			
		}
	}
}
