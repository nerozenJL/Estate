package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.MyRepair;
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
 * 这是具体某一个维修的activity
 */
public class MyRepairActivity extends Activity {

	private ImageButton back, submit;
	private TextView content, title, result, mainTextView,repairmanName,repairmanPhone,status_text;
	private RatingBar ratingBar;
	private RadioGroup reviewRadio;
	private EditText elaborate;
	private MyRepair myRepair;
	private float rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myrepair_evaluate);
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

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		myRepair = (MyRepair) bundle.getSerializable("myRepair");

	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.myrepair_evaluate_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		title = (TextView) findViewById(R.id.myrepair_title1);
		title.setText(myRepair.getTitle());
		mainTextView = (TextView) findViewById(R.id.myrepair_textview3);
		mainTextView.setText("对报修的评价");

		content = (TextView) findViewById(R.id.myrepair_content1);
		content.setText(myRepair.getContent());

		status_text = (TextView) findViewById(R.id.status);

		ratingBar = (RatingBar) findViewById(R.id.repair_rating);
		ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				rating = arg1;
			}
		});
		ratingBar.setRating((float) myRepair.getRemark());
		elaborate = (EditText) findViewById(R.id.myrepair_elaborate);
		repairmanName = (TextView) findViewById(R.id.repairman_name);
		repairmanName.setText(myRepair.getRepairName());
		repairmanPhone = (TextView) findViewById(R.id.repairman_phone);
		repairmanPhone.setText(myRepair.getRrepairPhone());
		repairmanPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent dialIntent = new Intent();
				dialIntent.setAction(Intent.ACTION_CALL);
				dialIntent.setData(Uri.parse("tel:"+repairmanPhone.getText().toString()));
				startActivity(dialIntent);
			}
		});
		Log.d("MyRepairActivity",""+myRepair.getStatus());
/*		result = (TextView) findViewById(R.id.mycomplain_result);
		result.setText(myComplair.getResult());*/
		switch (myRepair.getStatus()) {
			case 0:
				status_text.setText("等待处理");
				repairmanName.setVisibility(View.INVISIBLE);
				repairmanPhone.setVisibility(View.INVISIBLE);
				ratingBar.setVisibility(View.INVISIBLE);
				elaborate.setVisibility(View.INVISIBLE);
				break;
			case 1:
				status_text.setText("修理中");
				ratingBar.setVisibility(View.INVISIBLE);
				elaborate.setVisibility(View.INVISIBLE);
				break;
			case 2:
				status_text.setText("待评价");

				break;
			case 3:
				status_text.setText("修理已完成");
				ratingBar.setVisibility(View.INVISIBLE);
				elaborate.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
		}

//		reviewRadio = (RadioGroup) findViewById(R.id.point_radio);
//		reviewRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup arg0, int arg1) {
//				switch (arg1) {
//					case R.id.goodpoint:
//						rating = 100;
//					break;
//					case R.id.normalpoint:
//						rating = 60;
//					break;
//					case R.id.badpoint:
//						rating = 0;
//						break;
//					default:
//						break;
//				}
//			}
//		});


		submit = (ImageButton) findViewById(R.id.my_submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (myRepair.getStatus() == 1) {
					new SubmitAsyncTask().execute();
				} else if (myRepair.getStatus() == 2) {
					Toast.makeText(MyRepairActivity.this, "该报修已评价",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MyRepairActivity.this, "正在处理，还不能评价",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	class SubmitAsyncTask extends AsyncTask<Void, Void, String> {

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
						Toast.makeText(MyRepairActivity.this, "提交成功",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(MyRepairActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			} else {
				Toast.makeText(MyRepairActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String submitPost() {
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.remarkRepair);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		paramters.add(new BasicNameValuePair("id", myRepair.getId() + ""));
		paramters.add(new BasicNameValuePair("remark", rating + ""));
		paramters.add(new BasicNameValuePair("remarkText", elaborate.getText()
				.toString()));
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

	// private void getPicture() {
	// DisplayImageOptions options = new DisplayImageOptions.Builder()
	// .showImageOnLoading(R.drawable.icon_stub)
	// .showImageOnFail(R.drawable.icon_error).cacheInMemory(true)
	// .bitmapConfig(Bitmap.Config.RGB_565).build();
	//
	// // ImageLoader.getInstance().displayImage(path, imageView, options);
	//
	// }

}
