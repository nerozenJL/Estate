package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONObject;

/**
 * 这是找回密码输入验证码，并且验证码正确后跳转的设置密码界面
 */
public class SetPasswordActivity extends Activity {

	private EditText setpassword, ensurepassword;
	private Button ensure;
	private String phone;
	private String YES = "yes";
	private String FILE = "saveUserNamePwd";//
	private SharedPreferences sp = null;//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setpassword);
		ActivityCollector.addActivity(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		phone = bundle.getString("phone");
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initView() {

		setpassword = (EditText) findViewById(R.id.et_new_password_setpassword);
		setpassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!setpassword.getText().toString().equals("")) {
					if (setpassword.getText().toString()
							.equals(ensurepassword.getText().toString())) {
						ensure.setEnabled(true);
						ensure.setBackgroundResource(R.color.style);
					}
				} else {
					ensure.setEnabled(false);
					ensure.setBackgroundResource(R.color.gray1);
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
		ensurepassword = (EditText) findViewById(R.id.et_ensure_password_setpassword);
		ensurepassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!ensurepassword.getText().toString().equals("")) {
					if (ensurepassword.getText().toString()
							.equals(setpassword.getText().toString())) {
						ensure.setEnabled(true);
						ensure.setBackgroundResource(R.color.style);
					}
				} else {
					ensure.setEnabled(false);
					ensure.setBackgroundResource(R.color.gray1);
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
		ensure = (Button) findViewById(R.id.btn_ensure_setpassword);
		ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ensure.setEnabled(false);
				ensure.setBackgroundResource(R.color.gray1);
				ensure.setText("请求已发送");
				new newPassword().execute(ensurepassword.getText().toString());
			}
		});

		ensure.setEnabled(false);
		ensure.setBackgroundResource(R.color.gray1);
	}

	class newPassword extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String string = HttpUtils.HttpGet(SetPasswordActivity.this,
					Host.setPassword + arg0[0]);
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						Toast.makeText(SetPasswordActivity.this, "密码修改成功",
								Toast.LENGTH_SHORT).show();
						if (sp == null) {
							sp = getSharedPreferences(FILE, MODE_PRIVATE);
						}
						Editor editor = sp.edit();
						editor.putString("isMemory", YES);
						editor.putString("name",phone);
						editor.putString("password",ensurepassword.getText().toString());
						editor.commit();
						finish();
					} else {
						ensure.setEnabled(true);
						ensure.setBackgroundResource(R.color.style);
						ensure.setText("确认");
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(SetPasswordActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else {
				ensure.setEnabled(true);
				ensure.setBackgroundResource(R.color.style);
				ensure.setText("确认");
				Toast.makeText(SetPasswordActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
