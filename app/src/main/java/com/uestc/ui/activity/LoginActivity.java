package com.uestc.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.base.MyBitmapFactory;
import com.uestc.domain.ADInfo;
import com.uestc.domain.BluetoothProtalInfo;
import com.uestc.domain.GetAnnouncementData;
import com.uestc.domain.Person;
import com.uestc.domain.Session;
import com.uestc.constant.Host;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.domain.WiFiProtalInfo;
import com.uestc.ui.dialog.DepartmentSwicherDialog;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.JsonTools;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 这是登录的界面
 */
public class LoginActivity extends Activity {
	private Drawable forgetPasswordDraw,useNameTextDraw,passwordTextDraw;
	private TextView forgetPassWord,useNameText,passwordText;
	private ImageButton zuce;
	private EditText useName, setpassword;
	private Button login;
	public static String name, password;
	private Person person;
	private CheckBox rem_password;
	private String isMemory = "";//记录以前是否勾选 yes 勾选，no没有勾选
	private String YES = "yes";
	private String NO = "no";
	private String FILE = "saveUserNamePwd";//SharedPreferences保存地址
	private String currentVersionName, newVersionName, newVersionUrl;
	private SharedPreferences sp = null;//保存密码的SharedPreferences
	private String nameString, passwordString;//保存SharedPreferences里面的name和password
	private mylisten mylisten;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		ActivityCollector.addActivity(this);
		mylisten = new mylisten();
		initView();

		//16-03-07新增
		InputStream is=this.getResources().openRawResource(R.raw.yunpass);
		Bitmap yunpassBitmap= MyBitmapFactory.decodeRawBitMap(is);
		ImageView yunpassimageView=(ImageView)findViewById(R.id.iv_verpass_login);
		yunpassimageView.setImageBitmap(yunpassBitmap);

		new GetVersionTask().execute();
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		isMemory = sp.getString("isMemory", NO);
		// 看是否以前有记住密码
		if (isMemory.equals(YES)) {
			nameString = sp.getString("name", "");
			passwordString = sp.getString("password", "");
			useName.setText(nameString);
			setpassword.setText(passwordString);
		}

		person = new Person();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		isMemory = sp.getString("isMemory", NO);
		if (isMemory.equals(YES)) {
			nameString = sp.getString("name", "");
			passwordString = sp.getString("password", "");
			useName.setText(nameString);
			setpassword.setText(passwordString);
		}

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
				case R.id.ib_zuce_buttton_login:
					Intent intent1 = new Intent(LoginActivity.this, RegisterActivity1.class);
					startActivity(intent1);
					break;
//			case R.id.exit:
//				finish();
//				break;
				case R.id.btn_login:
//				 Intent intent2 = new Intent(LoginActivity.this, HomeActivity.class);
//				 startActivity(intent2);

					if (useName.getText().toString().length() == 11) {
						person.setUserName(useName.getText().toString().toString());
						name = useName.getText().toString();
						person.setPhone(useName.getText().toString().toString());

						if (!setpassword.getText().toString().equals("")) {
							person.setPassword(setpassword.getText().toString());
							password = setpassword.getText().toString();
							new handlerAsyncTask().execute(person);
							login.setClickable(false);
							login.setText("登录中");
							login.setBackgroundResource(R.drawable.fillet_button1);
						} else {
							Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(LoginActivity.this, "输入正确的电话号码", Toast.LENGTH_SHORT)
								.show();
					}

					break;
				case R.id.tv_forget_password_login:
					Intent intent = new Intent(LoginActivity.this,
							FindPasswordActivity.class);
					startActivity(intent);
					break;
				default:
					break;
			}
		}

	}

	private class handlerAsyncTask extends AsyncTask<Person, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						if (rem_password.isChecked()) {

							if (sp == null) {
								sp = getSharedPreferences(FILE, MODE_PRIVATE);
							}
							Editor editor = sp.edit();
							editor.putString("isMemory", YES);
							editor.putString("name", useName.getText()
									.toString());
							editor.putString("password", setpassword.getText()
									.toString());
							editor.commit();
						} else {

							if (sp == null) {
								sp = getSharedPreferences(FILE, MODE_PRIVATE);
							}
							Editor editor = sp.edit();
							editor.putString("isMemory", NO);
							editor.putString("name", useName.getText()
									.toString());
							editor.putString("password", setpassword.getText()
									.toString());
							editor.commit();
						}
						Session.setSeesion("JSESSIONID=" + object.getString("jsonString"));
						new GetRoleTask().execute();

					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(LoginActivity.this, description,
								Toast.LENGTH_SHORT).show();
						login.setClickable(true);
						login.setText("登录");
						login.setBackgroundResource(R.drawable.fillet_button);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(LoginActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
				login.setClickable(true);
				login.setText("登录");
				login.setBackgroundResource(R.drawable.fillet_button);
			}
		}

		@Override
		protected String doInBackground(Person... arg0) {
			String string = LogInHttpGet(LoginActivity.this, Host.login+"?phone="+person.getPhone()+"&password="+person.getPassword());
			return string;
		}

	}

	private class GetRoleTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... voids) {

			String jsonString = HttpUtils.HttpGet(LoginActivity.this,
					Host.getRole);

			return jsonString;

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			if (!s.equals("")){
				JSONObject object;
				try {
					object = new JSONObject(s);
					boolean status = object.getBoolean("status");
					if (status){
						String roles = object.getString("jsonString");
						int maxRole=0;
						for (String roleString:Arrays.asList(roles.split(",")))
						{
							int role=Integer.parseInt(roleString);
							if (role>maxRole)
								maxRole=role;
						}

						Session.setRole(maxRole);
						new GetSecretTask().execute();

					}else{
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(LoginActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {

				}
			}else{
				Toast.makeText(LoginActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private class GetSecretTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = HttpUtils.HttpGet(LoginActivity.this,
					Host.getSecret);

			GetOnlineNotifyList();//2016-3-27 临时在此处请求公告信息
			GetStorePreviewPictures();//2016/4/6 临时请求网络图片
			GetPropertyManagementCompanyPhone();//2016/4/7
			return jsonString;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!s.equals("")){
				try {
					SharedPreferences sp = getSharedPreferences("secret", Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putString("jsonString", s);

					JSONObject getSecretResult = new JSONObject(s);
					if(getSecretResult.getBoolean("status")){
						//返回成功，将所有jsonstring进行解析
						JSONArray ssidArrayJson = getSecretResult.getJSONArray("jsonString");

						for(int index = 0; index < ssidArrayJson.length(); index++){
							JSONObject singleSsidJsonObj = ssidArrayJson.getJSONObject(index);

							int type = singleSsidJsonObj.getInt("type");
							if(type == 2){//存储wifi列表
								String ssid = singleSsidJsonObj.getString("symbol");
								String secret = singleSsidJsonObj.getString("secret");
								String password = singleSsidJsonObj.getString("password");
								WiFiProtalInfo singleWiFiProtalInfo = new WiFiProtalInfo(ssid, password, secret);
								Session.addMemberToAccessableWiFiSsidList(singleWiFiProtalInfo);
							}
							else if(type == 1){//存储蓝牙列表
								String ssid = singleSsidJsonObj.getString("symbol");
								String secret = singleSsidJsonObj.getString("secret");
								BluetoothProtalInfo singleBTProtalInfo = new BluetoothProtalInfo(ssid, secret);
								Session.addMemberToAccessableBTSsidList(singleBTProtalInfo);
							}
                                    /*Secret tempSecret = new Secret();
                                    tempSecret.setControlType(myObj.getInt("controlType"));
                                    tempSecret.setSymbol(myObj.getString("symbol"));
                                    tempSecret.setSecret(myObj.getString("secret"));
                                    tempSecret.setSsidPassword(myObj.getString("password"));*/
									//secrets.add(tempSecret);
						}
					}
					editor.commit();
					/*2016-3-16新增
					* 登录成功后，将当前的用户密码写到配置文件中，以便检测页进行自动登录活动
					* */
					try{
						FileOutputStream fileStream = new FileOutputStream("/data/data/com.znt.estate/autologinconfig.properties", false);
						Properties autoLoginProperties = new Properties();

						File autoLoginFile=new File("/data/data/com.znt.estate/autologinconfig.properties");
						if(!autoLoginFile.exists()){
							autoLoginFile.createNewFile();
						}
						autoLoginProperties.setProperty("userName", useName.getText().toString());
						autoLoginProperties.setProperty("passWord", setpassword.getText().toString());

						autoLoginProperties.store(fileStream, null);

					}catch (Exception e){
						e.printStackTrace();
					}

					Intent intent2 = new Intent(LoginActivity.this,
							HomeActivity.class);
					startActivity(intent2);
					Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
							.show();
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(LoginActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private class GetVersionTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... params) {
			String jsonString = HttpUtils.HttpGet(LoginActivity.this,
					Host.getVersion);
			return jsonString;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!s.equals("")){
				JSONObject object;
				try {
					object = new JSONObject(s);
					boolean status = object.getBoolean("status");
					if (status){
						currentVersionName = getVersionName();
						JSONObject object1 =object.getJSONObject("jsonString");
						newVersionName = object1.getString("versionCode");
						String description =object1.getString("description");
						newVersionUrl = object1.getString("path");

						Log.e("testVersion", currentVersionName);
						Log.e("testVersion", newVersionName);
						//在这里获取版本号，并做出比较
						if(compareVersionName(currentVersionName,newVersionName)){
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
							builder.setTitle("更新提示！");
							builder.setMessage(description);
							builder.setIcon(R.drawable.ic_launcher);
							builder.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									//访问新版对应web页面
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_url = Uri.parse(Host.host1+newVersionUrl);
									Log.e("url",Host.host1+newVersionUrl);
									intent.setData(content_url);
									startActivity(intent);
									dialog.dismiss();
								}
							});
							builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							});
							builder.show();
						}

					}else{
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(LoginActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {

				}
			}else{
				Toast.makeText(LoginActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	public String LogInHttpGet(Context context, String url) {
		String result = "";
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = null;

		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3500);
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3500);

		try {

			httpResponse =defaultHttpClient.execute(httpGet);
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String getVersionName() throws Exception{
		//获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		return packInfo.versionName;
	}

	private void GetOnlineNotifyList() {
		String getNotify = HttpUtils.HttpGet(LoginActivity.this, Host.getNotice1);
		if (!getNotify.equals("")) {
			GetAnnouncementData getAnnouncementData = new GetAnnouncementData();
			getAnnouncementData = JsonTools.getAnnouncement(LoginActivity.this, "jsonString", getNotify);
			if (getAnnouncementData.isStatus()) {
				TempStaticInstanceCollection.announcementList = getAnnouncementData;
				if(getAnnouncementData == null){
					TempStaticInstanceCollection.announcementList.setStatus(false);
				}
				GetAnnouncementData a = TempStaticInstanceCollection.announcementList;
				GetAnnouncementData b = TempStaticInstanceCollection.announcementList;
			} else {
				//Log.v("notify failed", "获取公告失败");
				TempStaticInstanceCollection.announcementList = new GetAnnouncementData();

			}
		}
	}

	/*
	* versionNameA表示当前运行的程序版本号，versionNameB表示服务器端的版本号
	* */
	private boolean compareVersionName(String runningVersionName,String serverVersionName){
		String[] versionNumberA=runningVersionName.split(".");
		String[] versionNumberB=serverVersionName.split(".");
		int valueofA=0,valueofB=0;
		for (int i = 0;i < versionNumberA.length&&i <versionNumberB.length;i++){
			versionNumberA[i].valueOf(valueofA);
			versionNumberB[i].valueOf(valueofB);
			if (valueofA<valueofB)
				return true;
		}
		return false;
	}

	private void initView() {
		useNameText = (TextView) findViewById(R.id.tv_useName_text_login);
		useNameTextDraw = getResources().getDrawable(R.drawable.name);
		useNameTextDraw.setBounds(0,0,50,50);
		useNameText.setCompoundDrawables(useNameTextDraw, null, null, null);
		passwordText = (TextView) findViewById(R.id.tv_usePassword_text_login);
		passwordTextDraw = getResources().getDrawable(R.drawable.password);
		passwordTextDraw.setBounds(0,0,50,50);
		passwordText.setCompoundDrawables(passwordTextDraw, null, null, null);
		useName = (EditText) findViewById(R.id.et_edit_useName_login);
		setpassword = (EditText) findViewById(R.id.et_edit_password_login);
		zuce = (ImageButton) findViewById(R.id.ib_zuce_buttton_login);
		forgetPassWord = (TextView) findViewById(R.id.tv_forget_password_login);
		forgetPasswordDraw = getResources().getDrawable(R.drawable.forget_password);
		forgetPasswordDraw.setBounds(0,0,60,60);
		forgetPassWord.setCompoundDrawables(forgetPasswordDraw, null, null, null);
		login = (Button) findViewById(R.id.btn_login);
		zuce.setOnClickListener(mylisten);
		login.setOnClickListener(mylisten);
		rem_password = (CheckBox) findViewById(R.id.cb_rem_password_login);

		forgetPassWord.setOnClickListener(mylisten);
	}


	/*2016/4/6获取看板图片*/
	private void GetStorePreviewPictures(){
		String getPics = HttpUtils.HttpGet(this, Host.getStorePreviewPictures);
		List<URL> picsUrlList = new ArrayList<URL>();
		if(!getPics.equals("")){
			try {
				JSONObject totalJson = new JSONObject(getPics);
				Map<String, JSONObject> map = new HashMap<String, JSONObject>(); //使用map记录每一条json

                /*迭代初始化字符串-json映射图*/
				Iterator it = totalJson.keys();
				while(it.hasNext()){
					String key = String.valueOf(it.next());
					JSONObject value = (JSONObject)totalJson.get(key);
					map.put(key, value);
				}
				//遍历map，获取所有图片的url
				for(JSONObject values: map.values()){
					String url = values.getString("cover_pic");
					ADInfo adInfo = new ADInfo();
					adInfo.setUrl(url);
					TempStaticInstanceCollection.storePreviewInfo.add(adInfo);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			//获取失败
			Log.v("getOnlineStorePictures", "获取商店预览图片失败");
		}
	}

	/*2016/4/7获取物管电话*/
	private void GetPropertyManagementCompanyPhone(){
		String getPhone = HttpUtils.HttpGet(this, Host.getPropertyManagerPhoneList);
		//getPhone = "";
		//getPhone.toString();
		if(!getPhone.equals("")){
			JSONObject jsonObject = null;
			JSONArray phoneJsonArray = null;
			try {
				jsonObject = new JSONObject(getPhone);
				//jsonObject = jsonObject.getJSONObject("jsonString");
				phoneJsonArray = jsonObject.getJSONArray("jsonString");
				phoneJsonArray.get(0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				for(int index = 0; index < phoneJsonArray.length(); index++){
					JSONObject single = phoneJsonArray.getJSONObject(0);
					String phone = single.getString("phone");
					TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList.add(phone);
				}
				TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList.get(0);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			//获取失败
			//Toast.makeText(this, "获取物管电话失败", Toast.LENGTH_SHORT).show();
		}
	}

}