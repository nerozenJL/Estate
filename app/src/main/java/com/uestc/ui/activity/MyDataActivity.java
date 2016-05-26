package com.uestc.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.base.MyBitmapFactory;
import com.uestc.domain.MyData;
import com.uestc.domain.Session;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.GetData;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 修改个人资料的activity
 */
public class MyDataActivity extends Activity {

	private ImageView reset_born;
	private TextView born, name ,reset_id;
	private Spinner spinner;
	private EditText reset_emergency_people, reset_emergency_contact;
	private List<String> list;
	private ArrayAdapter<String> adapter;
	private int m;
	private MyData myData;
	private int sex;
	private RadioButton radioButton1, radioButton2;
	private ImageButton back, submit;

	private ImageView headImg;

	private InputStream is;
	private Bitmap b;

	//2016-3-9 --为性别选项增加四个缓存drawable，相应触碰
	private BitmapDrawable bdForRadioButton1True, bdForRadioButton2True,
			bdForRadioButton1False, bdForRadioButton2False;
	//注释结尾

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mydata);
		ActivityCollector.addActivity(this);


		/* 2016.3.9 初始化缓存BitmapDrawable For RadioButton choosing sex */
		this.is = this.getResources().openRawResource(R.raw.male_true);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdForRadioButton1True = new BitmapDrawable(getResources(), b);

		this.is = this.getResources().openRawResource(R.raw.male_false_new);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdForRadioButton1False = new BitmapDrawable(getResources(), b);

		this.is = this.getResources().openRawResource(R.raw.female_true);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdForRadioButton2True = new BitmapDrawable(getResources(), b);

		this.is = this.getResources().openRawResource(R.raw.female_false);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdForRadioButton2False = new BitmapDrawable(getResources(), b);

		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initData() {
		myData = new MyData();
		list = new ArrayList<String>();
		list.add("身份证");
		list.add("军人证");
		list.add("护照");
	}

	private void initView() {

		is = this.getResources().openRawResource(R.raw.default_head_img);
		b = MyBitmapFactory.decodeRawBitMap(is);
		headImg = (ImageView)findViewById(R.id.head_img);
		headImg.setImageBitmap(b);

		is = this.getResources().openRawResource(R.raw.goback1);
		b = MyBitmapFactory.decodeRawBitMap(is);

		back = (ImageButton) findViewById(R.id.ib_back_mydata);
		back.setImageBitmap(b);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		name = (TextView) findViewById(R.id.tv_name_mydata);

		is = this.getResources().openRawResource(R.raw.submit);
		b = MyBitmapFactory.decodeRawBitMap(is);

		submit = (ImageButton) findViewById(R.id.ib_submit_mydata);
		submit.setImageBitmap(b);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!born.getText().toString().equals("")) {
					if (!reset_emergency_people.getText().toString()
							.equals("")) {
						if (!reset_emergency_contact.getText().toString()
								.equals("")) {
							myData.setName(name.getText()
									.toString());
							myData.setBorn(born.getText()
									.toString());
							myData.setEmergency_contact(reset_emergency_contact
									.getText().toString());
							myData.setEmergency_people(reset_emergency_people
									.getText().toString());
							myData.setIdentityCode(reset_id
									.getText().toString());
							myData.setType(m);
							new EnsureAsyncTask().execute(myData);

						} else {
							Toast.makeText(MyDataActivity.this,
									"紧急联系电话不能为空", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(MyDataActivity.this, "紧急联系人不能为空",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(MyDataActivity.this, "请选择出生日期",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		born = (TextView) findViewById(R.id.tv_born_mydata);

		is = this.getResources().openRawResource(R.raw.born_date);
		b = MyBitmapFactory.decodeRawBitMap(is);

		reset_born = (ImageView) findViewById(R.id.ib_reset_born_mydata);
		reset_born.setImageBitmap(b);

		reset_born.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});
		spinner = (Spinner) findViewById(R.id.spinner_mydata);
		adapter = new ArrayAdapter<String>(MyDataActivity.this,
				android.R.layout.simple_spinner_item, list);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				m = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		reset_emergency_people = (EditText) findViewById(R.id.et_reset_emergency_people_mydata);
		reset_emergency_contact = (EditText) findViewById(R.id.et_reset_emergency_contact_mydata);
		reset_id = (TextView) findViewById(R.id.tv_id_mydata);


		/*is = this.getResources().openRawResource(R.raw.male_true);
		b = MyBitmapFactory.decodeRawBitMap(is);*/

		radioButton1 = (RadioButton) findViewById(R.id.man_mydata);
		/*BitmapDrawable bd = new BitmapDrawable(getResources(), bd);*/
		radioButton1.setCompoundDrawablesWithIntrinsicBounds(bdForRadioButton1True, null, null, null);

		//radioButton1

		radioButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sex = 0;
				myData.setSex(sex);

				//2016.3.9 -- 临时改动的radiobutton背景变动代码
				/*is = getResources().openRawResource(R.raw.female_false);
				b = MyBitmapFactory.decodeRawBitMap(is);
				BitmapDrawable bd = new BitmapDrawable(getResources(), b);*/
				radioButton2.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForRadioButton2False, null);

				/*is = getResources().openRawResource(R.raw.male_true);
				b = MyBitmapFactory.decodeRawBitMap(is);
				bd = new BitmapDrawable(getResources(), b);*/
				radioButton1.setCompoundDrawablesWithIntrinsicBounds(bdForRadioButton1True, null, null, null);
				//注释结尾
			}
		});

		/*is = this.getResources().openRawResource(R.raw.female_false);
		b = MyBitmapFactory.decodeRawBitMap(is);

		BitmapDrawable bd2 = new BitmapDrawable(getResources(), b);*/
		radioButton2 = (RadioButton) findViewById(R.id.girl_mydata);

		radioButton2.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForRadioButton2False, null);
		radioButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sex = 1;
				myData.setSex(sex);

				//2016.3.9 -- 同为临时修改的代码
				/*is = getResources().openRawResource(R.raw.female_true);
				b = MyBitmapFactory.decodeRawBitMap(is);
				BitmapDrawable bd = new BitmapDrawable(getResources(), b);*/
				radioButton2.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForRadioButton2True, null);

				/*is = getResources().openRawResource(R.raw.male_false_new);
				b = MyBitmapFactory.decodeRawBitMap(is);
				bd = new BitmapDrawable(getResources(), b);*/
				radioButton1.setCompoundDrawablesWithIntrinsicBounds(bdForRadioButton1False, null, null, null);
				//注释结尾
			}
		});
		new SetDataAsyncTask().execute();
	}

	private void showDialog() {
		Dialog dialog = null;
		Calendar c = Calendar.getInstance();
		dialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker arg0, int arg1, int arg2,
							int arg3) {
						born.setText(+arg1 + "-" + (arg2 + 1) + "-" + arg3);
					}
				}, c.get(Calendar.YEAR),
				c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH)
		);

		if (dialog != null) {
			dialog.show();
		}
	}

	class SetDataAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			String string = HttpUtils
					.HttpGet(MyDataActivity.this, Host.getData);

			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						JSONObject object2 = object.getJSONObject("jsonString");
						name.setText(object2.getString("name"));
						String sex = object2.getString("sex");
						Log.e("sex", sex);
						if (sex != null && !sex.equals("")
								&& !sex.equals("null")) {
							if (Integer.valueOf(sex) == 0) {
								radioButton1.setChecked(true);
							} else {
								radioButton2.setChecked(true);
							}
						}

						String birthday = object2.getString("birthday");
						if (birthday != null && !birthday.equals("")
								&& !birthday.equals("null")) {
							long time = Long.parseLong(birthday);
							born.setText(GetData.getdata(time));
						} else {
							born.setText("还未设置");
						}

						String urgentName = object2.getString("urgentName");
						if (urgentName != null && !urgentName.equals("null")) {
							reset_emergency_people.setText(urgentName);
						}

						String urgentPhone = object2.getString("urgentPhone");
						if (urgentPhone != null && !urgentPhone.equals("null")) {
							reset_emergency_contact.setText(urgentPhone);
						}

						String identityType = object2.getString("identityType");
						if (identityType != null
								&& !identityType.equals("null")) {
							spinner.setSelection(
									Integer.valueOf(identityType), true);
						}

						String identityCode = object2.getString("identityCode");

						if (identityCode != null
								&& !identityCode.equals("null")) {
							reset_id.setText(identityCode);
						}

					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");

						Toast.makeText(MyDataActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(MyDataActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	class EnsureAsyncTask extends AsyncTask<MyData, Void, String> {

		@Override
		protected String doInBackground(MyData... arg0) {

			String result = ensure(arg0[0]);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						Toast.makeText(MyDataActivity.this,"修改成功",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(MyDataActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MyDataActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private String ensure(MyData myData) {
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.setData);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();

		paramters.add(new BasicNameValuePair("name", myData.getName()));
		paramters.add(new BasicNameValuePair("sex", myData.getSex() + ""));
		paramters.add(new BasicNameValuePair("birthday", myData.getBorn()));
		paramters.add(new BasicNameValuePair("urgentName", myData
				.getEmergency_people()));
		paramters.add(new BasicNameValuePair("urgentPhone", myData
				.getEmergency_contact()));
		paramters.add(new BasicNameValuePair("identityCode", myData
				.getIdentityCode()));
		paramters.add(new BasicNameValuePair("identityType", myData.getType()
				+ ""));

		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(paramters, HTTP.UTF_8);
			entity.setContentEncoding("UTF-8");

			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
