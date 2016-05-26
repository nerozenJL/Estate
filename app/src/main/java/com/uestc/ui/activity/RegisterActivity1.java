package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.base.MyBitmapFactory;
import com.uestc.domain.Session;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * 这是输入电话号码，和发送验证码的注册界面
 */
public class RegisterActivity1 extends Activity {

	private ImageButton exit;
	private TextView huoqu;
	private Drawable huoquDraw;
	private Button ensure;
	private EditText et1, et2;// et1手机号码，et2是验证码
	private Mycount mc;
	private int n = 1;//1表示没有点击发送验证码，2表示验证码已发送，3表示时间60秒时间过去了的样子
	private String phone;
	private myListener myListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist);
		ActivityCollector.addActivity(this);
		myListener = new myListener();
		mc = new Mycount(60000, 1000);
		exit = (ImageButton) findViewById(R.id.zuce_back);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ensure = (Button) findViewById(R.id.zuce_ensure);
		ensure.setOnClickListener(myListener);
		ensure.setClickable(false);
		ensure.setBackgroundResource(R.color.gray1);

		et1 = (EditText) findViewById(R.id.shuru_number);
		et2 = (EditText) findViewById(R.id.shuru_yzm);
		et2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

				if (arg0.length() > 0) {
					String check = et2.getText().toString();
					check.replace(" ", "");
					check.trim();
					if (check.length() == 6) {
//						ensure.setEnabled(true);
						ensure.setClickable(true);
						ensure.setBackgroundResource(R.color.style);
					} else {
//						ensure.setEnabled(false);
						ensure.setClickable(false);
						ensure.setBackgroundResource(R.color.gray1);
					}
				} else {
					ensure.setClickable(false);
					ensure.setBackgroundResource(R.color.gray1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		huoqu = (TextView) findViewById(R.id.send_mess);
		huoquDraw = getResources().getDrawable(R.drawable.send_yzm);
		huoquDraw.setBounds(0,0,40,70);
		huoqu.setCompoundDrawables(huoquDraw, null, null, null);
		huoqu.setOnClickListener(myListener);

		//160307新增
		InputStream is=this.getResources().openRawResource(R.raw.zhuce_title);
		Bitmap yunpassBitmap= MyBitmapFactory.decodeRawBitMap(is);
		ImageView yunpassimageView=(ImageView)findViewById(R.id.zhuce_themepic);
		yunpassimageView.setImageBitmap(yunpassBitmap);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void ensure() {

		if (et1.getText().length() == 11) {
			phone = et1.getText().toString();
			new zuceAsyncTask().execute(phone);
			huoqu.setClickable(false);
			huoqu.setTextColor(getResources().getColor(R.color.gray1));
			huoqu.setText("请求已发送");
		} else {
			Toast.makeText(RegisterActivity1.this, "输入正确的电话号码", Toast.LENGTH_SHORT)
					.show();
		}
	}

	class Mycount extends CountDownTimer {

		public Mycount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);

		}

		@Override
		public void onFinish() {

			huoqu.setText("重新发送");
			n = 3;
			huoqu.setClickable(true);
			huoqu.setTextColor(getResources().getColor(R.color.style));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			huoqu.setClickable(false);
			huoqu.setTextColor(getResources().getColor(R.color.gray1));
			huoqu.setText("发送中(" + millisUntilFinished / 1000 + ")");
		}
	}

	// 发送验证码的按钮的task
	class zuceAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... arg0) {
			String resultString = HttpUtils.HttpGet(RegisterActivity1.this,
					Host.getVerify + "/" + arg0[0]);

			return resultString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						Toast.makeText(RegisterActivity1.this, "验证码已发送",
								Toast.LENGTH_SHORT).show();
						Session.setSeesion("JSESSIONID="
								+ object.getString("jsonString"));
						n = 2;
						mc.start();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String code = object2.getString("code");
						if (code.equals("100200")) {
							Toast.makeText(RegisterActivity1.this, "电话号码已注册",
									Toast.LENGTH_SHORT).show();
						} else if (code.equals("100210")) {
							Toast.makeText(RegisterActivity1.this, "请输入正确的电话号码",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(RegisterActivity1.this,
									object2.getString("description"),
									Toast.LENGTH_SHORT).show();
						}

						huoqu.setClickable(true);
						huoqu.setTextColor(getResources().getColor(R.color.style));
						huoqu.setText("重新发送");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(RegisterActivity1.this, "请检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}

		}
	}

	class myListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.zuce_ensure:
				if (n != 1) {
					if (et1.getText().length() == 11) {
						if (et2.getText().toString().length() == 6) {
							ensure.setClickable(false);
							ensure.setBackgroundResource(R.color.gray1);
							new YZMAsyncTask().execute(et2.getText().toString());

						} else {
							Toast.makeText(RegisterActivity1.this, "验证码位数不对",
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(RegisterActivity1.this, "请输入正确的电话号码",
								Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(RegisterActivity1.this, "请点击发送验证码",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.send_mess:

				if (n == 1) {
					ensure();
				} else if (n == 2) {
					mc.onFinish();
					ensure();
				} else {
					ensure();
				}
				break;
			default:
				break;
			}

		}

	}

	class YZMAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Log.e("zucu  zuce  zuce ", arg0[0]);
			String string = HttpUtils.HttpGet(RegisterActivity1.this,
					Host.checkVerify + "/" + arg0[0]);

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
						Intent intent = new Intent(RegisterActivity1.this,
								RegisterActivity2.class);
						phone = et1.getText().toString();
						Bundle bundle = new Bundle();
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(RegisterActivity1.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
						ensure.setClickable(true);
						ensure.setBackgroundResource(R.color.style);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				ensure.setClickable(true);
				ensure.setBackgroundResource(R.color.style);
				Toast.makeText(RegisterActivity1.this, "请检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
