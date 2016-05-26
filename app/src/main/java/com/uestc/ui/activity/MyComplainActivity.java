package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.MyComplain;
import com.uestc.domain.Session;
import com.uestc.constant.Host;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是具体某一个投诉的activity
 */
public class MyComplainActivity extends Activity {
	private ImageButton back, submit;
	// private Picture picture;
	private TextView content, title, result;
//	private RatingBar ratingBar;
	private EditText elaborate;
	private MyComplain myComplain;
	private float rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mycomplain_evaluate);
		ActivityCollector.addActivity(this);
		initData();
		initView();

	}

	private void initData() {
		// content = new ArrayList<String>();
		// content.add("�����й��У�˯����");
		// content.add("�ھ����Ϻܳ�����������Ϣ");
		// content.add("A���ϲ����˸߿�����");
		// content.add("����д���������Ա��û");
		// content.add("С��ͣ������");
		// status = new ArrayList<String>();
		// status.add("�������");
		// status.add("���񻷾�");
		// status.add("Σ�ղ���");
		// status.add("��Ա����");
		// status.add("��������");
		// picture = new Picture();
		// paths=new ArrayList<String>();
		// paths=picture.getPicture();
		// path=paths.get(n);
		// content1.setText(content.get(n));
		// title.setText(status.get(n));
		// getPicture();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		myComplain = (MyComplain) bundle.getSerializable("myComplain");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.mycomplain_evaluate_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		// imageView = (ImageView) findViewById(R.id.mypicture);

		content = (TextView) findViewById(R.id.mycomplain_content1);
		content.setText(myComplain.getContent());
		
		title = (TextView) findViewById(R.id.mycomplain_title1);
		title.setText(myComplain.getTitle());
		
//		result = (TextView) findViewById(R.id.mycomplain_result);
//		result.setText(myComplain.getResult());
//		elaborate = (EditText) findViewById(R.id.mycomplain_elaborate);
//		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
//		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
//
//			@Override
//			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
//				rating = arg1;
//			}
//		});
//		ratingBar.setRating((float) myComplain.getStar());
		
		submit = (ImageButton) findViewById(R.id.my_submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (myComplain.getStatus() == 1) {
					new SubmitAsyncTask().execute();
				}else if (myComplain.getStatus() == 2) {
					Toast.makeText(MyComplainActivity.this, "该投诉已评价",
							Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(MyComplainActivity.this, "正在处理，还不能评价",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}

	
	class SubmitAsyncTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... arg0) {
			String result = submitPost();
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						Toast.makeText(MyComplainActivity.this, "提交成功",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(MyComplainActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}else {
				Toast.makeText(MyComplainActivity.this, "申请发送失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private String submitPost(){
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.remarkComplain);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		paramters.add(new BasicNameValuePair("id", myComplain.getId()+""));
		paramters.add(new BasicNameValuePair("remark",rating+""));
		paramters.add(new BasicNameValuePair("remarkText",elaborate.getText().toString()));
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
