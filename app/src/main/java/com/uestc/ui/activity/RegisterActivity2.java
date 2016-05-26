package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.base.MyBitmapFactory;
import com.uestc.domain.Person;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * 这是注册验证码输入正确之后跳转的activity，输入昵称和密码的注册界面
 */
public class RegisterActivity2 extends Activity {
	private EditText name, password, ensure;
	private Button zuce;
	private String phone;
	private ImageButton back;
	private String YES = "yes";
	private String FILE = "saveUserNamePwd";
	private SharedPreferences sp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist2);
		ActivityCollector.addActivity(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		phone = bundle.getString("phone");
		Person.setPhone(phone);
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		initView();
		initData();

		//160307新增
		InputStream is=this.getResources().openRawResource(R.raw.zhuce_title);
		Bitmap yunpassBitmap= MyBitmapFactory.decodeRawBitMap(is);
		ImageView yunpassimageView=(ImageView)findViewById(R.id.zhuce_themepic);
		//yunpassBitmap.setWidth(yunpassimageView.getWidth());
		yunpassimageView.setImageBitmap(yunpassBitmap);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initData() {
		

	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.zuce2_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		name = (EditText) findViewById(R.id.shuru_name);
		password = (EditText) findViewById(R.id.textPassword);
		password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.length() > 0) {

					String check = ensure.getText().toString();
					check.replace(" ", "");
					check.trim();

					if (!name.getText().toString().equals("")) {

						if (!password.getText().toString().equals("")) {

							if (password.getText().toString()
									.equals(ensure.getText().toString())) {
								zuce.setEnabled(true);
								zuce.setBackgroundResource(R.color.style);
							} else {
								zuce.setEnabled(false);
								zuce.setBackgroundResource(R.color.gray1);
							}
						} else {
							zuce.setEnabled(false);
							zuce.setBackgroundResource(R.color.gray1);
						}
					} else {
						zuce.setEnabled(false);
						zuce.setBackgroundResource(R.color.gray1);
					}
				} else {
					zuce.setEnabled(false);
					zuce.setBackgroundResource(R.color.gray1);
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
		ensure = (EditText) findViewById(R.id.shuru_password2);
		ensure.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (arg0.length() > 0) {

					String check = password.getText().toString();
					check.replace(" ", "");
					check.trim();

					if (!name.getText().toString().equals("")) {

						if (!password.getText().toString().equals("")) {

							if (password.getText().toString()
									.equals(ensure.getText().toString())) {
								zuce.setEnabled(true);
								zuce.setBackgroundResource(R.color.style);
							} else {
								zuce.setEnabled(false);
								zuce.setBackgroundResource(R.color.gray1);
							}
						} else {
							zuce.setEnabled(false);
							zuce.setBackgroundResource(R.color.gray1);
						}
					} else {
						zuce.setEnabled(false);
						zuce.setBackgroundResource(R.color.gray1);
					}
				} else {
					zuce.setEnabled(false);
					zuce.setBackgroundResource(R.color.gray1);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		zuce = (Button) findViewById(R.id.btn_regist);

		/*2016-3-23-优化图片内存占用*/
		/*InputStream is=this.getResources().openRawResource(R.raw.zhuce_title);
		Bitmap submitBitmap= MyBitmapFactory.decodeRawBitMap(is);
		zuce.setImageBitmap(submitBitmap);*/

		//ImageView yunpassimageView=(ImageView)findViewById(R.id.zhuce_themepic);
		//zuce.set(submitBitmap);

		zuce.setEnabled(false);
		
		zuce.setBackgroundResource(R.color.gray1);
		zuce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String nameStr=name.getText().toString();
				if (!isUsernameProper(nameStr)){
					Toast.makeText(RegisterActivity2.this, "昵称只能由字母和数字组成", Toast.LENGTH_SHORT).show();
				} else if (name.getText().toString().equals("")) {
					Toast.makeText(RegisterActivity2.this, "请输入昵称",
							Toast.LENGTH_SHORT).show();
				} else if (!password.getText().toString()
						.equals(ensure.getText().toString())) {
					Toast.makeText(RegisterActivity2.this, "两次密码输入不相同ͬ",
							Toast.LENGTH_SHORT).show();
				} else {
					Person.setPassword(ensure.getText().toString());
					Person.setUserName(name.getText().toString());

					new zuce2AsyncTask().execute();
					zuce.setEnabled(false);
					zuce.setBackgroundResource(R.color.gray1);
				}
			}
		});
	}

	class zuce2AsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... arg0) {
			String returnString = HttpUtils.HttpGet(RegisterActivity2.this,
					Host.regist + "?nickname=" + Person.getUserName()
							+ "&password=" + Person.getPassword());

			return returnString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					JSONObject object2 = object.getJSONObject("errorMsg");
					if (status) {
						if (sp == null) {
							sp = getSharedPreferences(FILE, MODE_PRIVATE);
						}
						Editor editor = sp.edit();
						editor.putString("isMemory", YES);
						editor.putString("name",phone);
						editor.putString("password",ensure.getText().toString());
						editor.commit();
						if (object2.getString("code").equals("100000")) {
							Toast.makeText(RegisterActivity2.this, "尊敬的业主，你已注册成功",
									Toast.LENGTH_SHORT).show();
							
							finish();
						} else {
							Intent intent = new Intent(RegisterActivity2.this,
									TiedOwnersActivity.class);
							startActivity(intent);
							
							finish();
						}
					} else {
						zuce.setEnabled(true);
						zuce.setBackgroundResource(R.color.style);
						Toast.makeText(RegisterActivity2.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else {
				zuce.setEnabled(true);
				zuce.setBackgroundResource(R.color.style);
				Toast.makeText(RegisterActivity2.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}

		}

	}

	//测试用户名字是否符合条件
	private boolean isUsernameProper(String username){
		boolean isProper=false;
		for (int i=0;i<username.length();i++){
			if (username.charAt(i)>'a'&&username.charAt(i)<'z'){
				isProper=true;
				//break;
			}
			if (username.charAt(i)>'0'&&username.charAt(i)<'9'){
				isProper=true;
				//break;
			}
			if (username.charAt(i)>'A'&&username.charAt(i)<'Z'){
				isProper=true;
				//break;
			}
			if (isProper==false){

				return false;
			}
		}
		return true;
	}
}
