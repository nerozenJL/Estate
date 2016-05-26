package com.uestc.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.MyComplain;
import com.uestc.ui.adapter.MyComplairAdapter;
import com.uestc.utils.GetData;
import com.uestc.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是我的报修列表展示的activity
 */
public class MyComplainListActivity extends Activity {

	private ImageButton back,complain;
//	private SwipeListView listView;
	private ListView listView;
	private List<MyComplain> myComplains;
	private MyComplairAdapter adapter;
	int Position = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_complain);
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
		super.onResume();
		new getComplairListASyncTask().execute();
	}

	private void initData() {
		myComplains = new ArrayList<MyComplain>();
	}

	public void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确定删除");
		builder.setPositiveButton("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

//				listView.deleteItem(listView.getChildAt(Position
//						- listView.getFirstVisiblePosition()));
				// content.remove(Position);
				// status.remove(Position);
				adapter.notifyDataSetChanged();
				Toast.makeText(MyComplainListActivity.this, Position + "已被删除",
						Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void initView() {

		back = (ImageButton) findViewById(R.id.ib_back_mycomplain);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
			}
		});
		complain = (ImageButton) findViewById(R.id.ib_addcomplain_mycomplain);
		complain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(MyComplainListActivity.this,
						ComplainActivity.class);
				startActivity(intent);
			}
		});

		listView = (ListView) findViewById(R.id.listview_mycomplain);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// if (status.get(arg2).equals("δ����")) {
				// Intent intent = new Intent(MyComplainListActivity.this,
				// MyComplainActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putInt("id", arg2);
				// intent.putExtras(bundle);
				// startActivity(intent);
				// } else {
				// Toast.makeText(MyComplainListActivity.this, "Ͷ�߻�δ������ ��������",
				// Toast.LENGTH_SHORT).show();
				// }

				MyComplain myComplain = new MyComplain();
				myComplain = myComplains.get(arg2);

				Intent intent = new Intent(MyComplainListActivity.this,
						MyComplainActivity.class);

				Bundle bundle = new Bundle();
				bundle.putSerializable("myComplain", myComplain);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	class getComplairListASyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			String string = HttpUtils.HttpGet(MyComplainListActivity.this,
					Host.getMyComplain);
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				ActivityCollector.LogOut(result,MyComplainListActivity.this);
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						JSONArray jsonArray = object.getJSONArray("jsonString");

						if (myComplains.size() != 0) {
							myComplains.removeAll(myComplains);
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object2 = jsonArray.getJSONObject(i);
							MyComplain complair = new MyComplain();
							complair.setId(object2.getInt("id"));
							complair.setContent(object2.getString("content"));
							
							String description = object2.getString("description");
							if (description!=null&&!description.equals("")&&!description.equals("null")) {
								complair.setDescription(description);
							}else {
								complair.setDescription("");
							}
							complair.setTitle(object2.getString("title"));

							String result1 = object2.getString("result");

							if (result1.equals("") || result1.equals("null")) {
								complair.setResult("");
							} else {
								complair.setResult(result1);
							}
							String startString = object2.getString("remark");
							if (!startString.equals("")&&!startString.equals("null")) {
								complair.setStar(Double.valueOf(startString));
							}else {
								complair.setStar(5);
							}
							
//							complair.setType(object2.getInt("type"));

							complair.setTime(GetData.getdata(object2
									.getLong("time")));
							
							complair.setStatus(object2.getInt("status"));
							
							myComplains.add(complair);
						}

						adapter = new MyComplairAdapter(
								MyComplainListActivity.this, myComplains
//								, listView.getRightViewWidth()
						);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");

						Toast.makeText(MyComplainListActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}else {
				Toast.makeText(MyComplainListActivity.this, "检查网络设置", Toast.LENGTH_SHORT).show();
			}
				

		}
	}
}
