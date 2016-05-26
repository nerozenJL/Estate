package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.Session;
import com.uestc.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 找回密码的界面
 * 
 */
public class FindPasswordActivity extends Activity {

	private EditText phone_number, verification_code;// ed1手机号码ed2验证码

	private Button next, send;
	private ImageButton back;
	private Mycount mc;
	private int n = 1;
	private mylisten mylisten;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_findpassword);
		ActivityCollector.addActivity(this);
		mylisten = new mylisten();

		next = (Button) findViewById(R.id.btn_next_findpassword);
		next.setOnClickListener(mylisten);
		next.setClickable(false);
		next.setBackgroundResource(R.color.gray1);
		phone_number = (EditText) findViewById(R.id.et_phone_numberrl_findpassword);
		verification_code = (EditText) findViewById(R.id.et_verification_code_findpassword);
		verification_code.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				if (arg0.length() > 0) {
					String check = verification_code.getText().toString();
					check.replace(" ", "");
					check.trim();
					if (check.length() == 6) {
						// next.setEnabled(true);
						next.setClickable(true);
						next.setBackgroundResource(R.color.style);
					} else {
						// next.setEnabled(false);
						next.setClickable(false);
						next.setBackgroundResource(R.color.gray1);
					}
				} else {
					next.setClickable(false);
					next.setBackgroundResource(R.color.gray1);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		send = (Button) findViewById(R.id.btn_send_findpassword);
		send.setOnClickListener(mylisten);
		back = (ImageButton) findViewById(R.id.ib_back_findpassword);
		back.setOnClickListener(mylisten);
		mc = new Mycount(60000, 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private class mylisten implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ib_back_findpassword:
				finish();
				break;
			case R.id.btn_next_findpassword:
				if (phone_number.getText().length() == 11) {

						if (verification_code.getText().toString().length() == 6) {

							new ensureMessAsncTack().execute(verification_code.getText()
									.toString());
							next.setClickable(false);

							next.setBackgroundResource(R.color.gray1);
							next.setText("请求已发送");
						} else {
							Toast.makeText(FindPasswordActivity.this,
									"验证码位数不同", Toast.LENGTH_SHORT).show();

						}

				} else {
					Toast.makeText(FindPasswordActivity.this, "输入正确的电话号码",
							Toast.LENGTH_SHORT).show();
				}
				break;

			case R.id.btn_send_findpassword:
				if (n == 1) {
					sendMess();
				} else if (n == 2) {
					mc.onFinish();
					sendMess();
				} else {
					sendMess();

				}

				break;
			default:
				break;
			}
		}

		private void sendMess() {

			if (phone_number.getText().length() == 11) {

				phone = phone_number.getText().toString();

				send.setClickable(false);
				send.setBackgroundResource(R.color.gray1);
				send.setText("请求已发送");
				new sendAsncTask().execute(phone);
			} else {
				Toast.makeText(FindPasswordActivity.this, "输入正确电话号码",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	class Mycount extends CountDownTimer {

		public Mycount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			send.setText("重新发送");
			n = 3;

			send.setClickable(true);
			send.setBackgroundResource(R.color.style);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			send.setClickable(false);
			send.setBackgroundResource(R.color.gray1);
			send.setText("发送中(" + millisUntilFinished / 1000 + ")");
		}
	}

	class sendAsncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String reString = HttpUtils.HttpGet(FindPasswordActivity.this,
					Host.findPassword + arg0[0]);

			return reString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (!result.equals("")) {

				JSONObject object;
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						Toast.makeText(FindPasswordActivity.this, "验证码已发送",
								Toast.LENGTH_SHORT).show();
						Session.setSeesion("JSESSIONID="
								+ object.getString("jsonString"));
						n = 2;
						mc.start();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");

						Toast.makeText(FindPasswordActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
						send.setClickable(true);
						send.setBackgroundResource(R.color.style);
						send.setText("重新发送");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(FindPasswordActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
				send.setClickable(true);
				send.setBackgroundResource(R.color.style);
				send.setText("重新发送");
			}
		}
	}

	class ensureMessAsncTack extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {

			String reString = HttpUtils.HttpGet(FindPasswordActivity.this,
					Host.findPasswordCheckVerify + arg0[0]);
			return reString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			JSONObject object;
			if (!result.equals("")) {
				try {
					object = new JSONObject(result);
					if (object.getBoolean("status")) {
						Intent intent = new Intent(FindPasswordActivity.this,
								SetPasswordActivity.class);
						phone = phone_number.getText().toString();
						Bundle bundle = new Bundle();
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(FindPasswordActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
						next.setClickable(true);
						next.setText("下一步");
						next.setBackgroundResource(R.color.style);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(FindPasswordActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
				next.setClickable(true);
				next.setText("下一步");
				next.setBackgroundResource(R.color.style);
			}

		}

	}

}
