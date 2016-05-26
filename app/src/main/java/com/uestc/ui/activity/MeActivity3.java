package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.ADInfo;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.ui.fragment.HighVersionFragment;
import com.uestc.utils.ConfigurationFilesAdapter;
import com.uestc.utils.HttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 这是访客的个人
 */
public class MeActivity3 extends Activity {

	public static Activity guestActivity;
	private ImageButton back;
	private TextView opendoor,callUs;
	private RelativeLayout layout1, layout2, layout3, layout4, layout5, verifyId;
	private mylisten mylisten;
	private TextView versionnameTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_me3);
		ActivityCollector.addActivity(this);
		mylisten = new mylisten();
		guestActivity=this;
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
		if (HighVersionFragment.choice == 0) {
			opendoor.setText("默认选择为 按键开门");
		} else if (HighVersionFragment.choice == 1) {
			opendoor.setText("开门方式为   自动");
		} else if (HighVersionFragment.choice == 2) {
			opendoor.setText("开门方式为   摇一摇");
		} else {
			opendoor.setText("开门方式为   按键开门");
		}
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.me3_back);
		back.setOnClickListener(mylisten);

		opendoor = (TextView) findViewById(R.id.opendoorways3);
		layout1 = (RelativeLayout) findViewById(R.id.me3_layout1);
		layout1.setOnClickListener(mylisten);
		layout2 = (RelativeLayout) findViewById(R.id.me3_layout2);
		layout2.setOnClickListener(mylisten);
		layout3 = (RelativeLayout) findViewById(R.id.me3_layout3);
		layout3.setOnClickListener(mylisten);
		layout4 = (RelativeLayout) findViewById(R.id.me3_layout5);
		layout4.setOnClickListener(mylisten);
		layout5 = (RelativeLayout) findViewById(R.id.me3_layout6);
		layout5.setOnClickListener(mylisten);
//		verifyId = (RelativeLayout) findViewById(R.id.rl_verifiy_me2);
//		verifyId.setOnClickListener(mylisten);
//		callUs = (TextView) findViewById(R.id.call_us3);
//		callUs.setOnClickListener(mylisten);
		versionnameTextView=(TextView)findViewById(R.id.version_textview3);
		versionnameTextView.setText(getVersion());
	}

	private String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return "版本号："+version;
		} catch (Exception e) {
			e.printStackTrace();
			return "can_not_find_version_name";
		}
	}

	class mylisten implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
				case R.id.me3_back:
					finish();
					break;

				case R.id.me3_layout1:
					Intent intent1 = new Intent(MeActivity3.this,
							MyDataActivity.class);
					startActivity(intent1);
					break;
				case R.id.me3_layout2:
					Intent intent2 = new Intent(MeActivity3.this,
							ResetPasswordActivity.class);
					startActivity(intent2);
					break;
				case R.id.me3_layout3:
					Intent intent3 = new Intent(MeActivity3.this,
							TiedOwnersActivity.class);
					intent3.putExtra("fromwhichActivity","MeActivity3");
					startActivity(intent3);
					break;
				case R.id.me3_layout5:
					Intent intent5 = new Intent(MeActivity3.this,
							OpendoorActivity.class);
					startActivity(intent5);
					break;
				case R.id.me3_layout6:
					new LogOutAsyncTask().execute();
					break;
//				case R.id.rl_verifiy_me:
//					// android.os.Process.killProcess(android.os.Process.myPid());
//					// System.exit(0);
//					Intent intent7 = new Intent(MeActivity3.this,
//							VerificationActivity2.class);
//					startActivity(intent7);
//					break;

//				case R.id.call_us3:
//					Intent dialIntent = new Intent();
//					dialIntent.setAction(Intent.ACTION_CALL);
//					dialIntent.setData(Uri.parse("tel:10086"));
//					startActivity(dialIntent);
//					break;
				default:
					break;
			}

		}

	}

	class LogOutAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

//			String result = HttpUtils.HttpGet(MeActivity.this, Host.loginOut);
			String result="";
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
//			if (!result.equals("")) {
			try {
//					JSONObject object = new JSONObject(result);
//					boolean status = object.getBoolean("status");
//					if (status) {
//						ActivityCollector.finishAll();
				Toast.makeText(MeActivity3.this, "退出成功",
						Toast.LENGTH_SHORT).show();

						/*2016/4/7把temp存储区重新初始化*/
				TempStaticInstanceCollection.storePreviewInfo = new ArrayList<ADInfo>();
				TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList = new ArrayList<String>();
				TempStaticInstanceCollection.announcementList = null;

				ConfigurationFilesAdapter autoLoginPropertySetter = new ConfigurationFilesAdapter();
				autoLoginPropertySetter.SetOrUpdateProperty("/data/data/com.znt.estate/autologinconfig.properties", "IsAutoLoginExpected", "false");

				HomeActivity.instance.finish();
				finish();
				Intent intent5 = new Intent(MeActivity3.this,
						LoginActivity.class);
				startActivity(intent5);


//					} else {
//						JSONObject object2 = object.getJSONObject("errorMsg");
//						Toast.makeText(MeActivity.this,
//								object2.getString("description"),
//								Toast.LENGTH_SHORT).show();
//					}

			} catch (Exception e) {

				e.printStackTrace();
			}
//			}else{
//				Toast.makeText(MeActivity.this,
//						"检查网络设置",
//						Toast.LENGTH_SHORT).show();
//			}
		}

	}
}
