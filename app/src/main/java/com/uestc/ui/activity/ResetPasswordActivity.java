package com.uestc.ui.activity;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 这是修改密码的界面
 */
public class ResetPasswordActivity extends Activity {

	private ImageButton back;
	private EditText old_password_edit, new_password_edit,
			ensure_password_edit;
	private Button ensure;
	private String YES = "yes";
	private String FILE = "saveUserNamePwd";
	private String oldPassword,newPassword;
	private SharedPreferences sp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset_password);
		ActivityCollector.addActivity(this);
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		initView();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.resetpasswoed_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ensure = (Button) findViewById(R.id.btn_ensure_reset_password);
		ensure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				oldPassword=old_password_edit.getText().toString();
				newPassword=new_password_edit.getText().toString();
				new ResetPasswordAsyncTask().execute();
				ensure.setEnabled(false);
				ensure.setBackgroundResource(R.color.gray1);
				ensure.setText("请求已发送");
			}
		});
		ensure.setEnabled(false);
		ensure.setBackgroundResource(R.color.gray1);
		old_password_edit = (EditText) findViewById(R.id.old_password_edit);

		new_password_edit = (EditText) findViewById(R.id.new_password_edit);
		new_password_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (!old_password_edit.getText().toString().equals("")) {
					if (arg0.length() > 0) {
						if (ensure_password_edit.getText().toString().equals(new_password_edit.getText().toString().toString())) {
							ensure.setEnabled(true);
							ensure.setBackgroundResource(R.color.style);
						} else {
							ensure.setEnabled(false);
							ensure.setBackgroundResource(R.color.gray1);
						}

					} else {
						ensure.setEnabled(false);
						ensure.setBackgroundResource(R.color.gray1);
					}
				} else {
					ensure.setEnabled(false);
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
		ensure_password_edit = (EditText) findViewById(R.id.ensure_password_edit);
		ensure_password_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.length() > 0) {

					if (!old_password_edit.getText().toString().equals("")) {

						if (ensure_password_edit.getText().toString().equals(new_password_edit.getText().toString())) {
							ensure.setEnabled(true);
							ensure.setBackgroundResource(R.color.style);
						} else {
							ensure.setEnabled(false);
							ensure.setBackgroundResource(R.color.gray1);
						}

					} else {
						ensure.setEnabled(false);
						ensure.setBackgroundResource(R.color.gray1);
					}

				} else {
					ensure.setEnabled(false);
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
		
		
	}
	
	
	class ResetPasswordAsyncTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... arg0) {
			
			String result = HttpUtils.HttpGet(ResetPasswordActivity.this, Host.reSetPassword+"?oldPassword="+oldPassword+"&newPassword="+newPassword);
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
						if (sp == null) {
							sp = getSharedPreferences(FILE, MODE_PRIVATE);
						}
						Editor editor = sp.edit();
						editor.putString("isMemory", YES);
						editor.putString("password",ensure_password_edit.getText().toString());
						editor.commit();
						Toast.makeText(ResetPasswordActivity.this, "修改密码成功", Toast.LENGTH_SHORT)
								.show();
						finish();
					} else {
						ensure.setEnabled(true);
						ensure.setBackgroundResource(R.color.style);
						ensure.setText("确认");
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(ResetPasswordActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else {
				ensure.setEnabled(true);
				ensure.setBackgroundResource(R.color.style);
				ensure.setText("确认");
				Toast.makeText(ResetPasswordActivity.this, "请检查网络设置", Toast.LENGTH_SHORT)
				.show();
			}

		}
		
		
	}
}
