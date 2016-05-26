package com.uestc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.base.MyBitmapFactory;
import com.uestc.constant.ConfigKeyAndValue;
import com.uestc.domain.Session;
import com.uestc.service.BluetoothControler;
import com.uestc.service.ShakeSensorService;
import com.uestc.service.WifiUnlocker;
import com.uestc.utils.ConfigurationFilesAdapter;

import java.io.InputStream;

/**
 * 这是个人中 开门方式选择的activity
 */
public class OpendoorActivity extends Activity {
	private ImageButton back;
	private RadioButton bluetoothRadioButton,wifiRadioButton;
	private CheckBox yaoyiyaoSwitcherCheckbox;
	private RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3;
	private mylisten mylisten;
	private ImageView sild1, sild2, sild3, opendoor_choice1, opendoor_choice2,
			opendoor_choice3;
	private LinearLayout rbUnlockwithWiFi,rbUnlockwithBluetooth;
	private TextView textViewrbUnlockwithWiFi,textViewrbUnlockWithBluetooth;
	private BitmapDrawable bdForbluetoothRadioButtonTrue, bdForwifiRadioButtonTrue,
			bdForbluetoothRadioButtonFalse, bdForwifiRadioButtonFalse,bdYaoyiyaoSwitcherTrue,bdYaoyiyaoSwitcherFalse;

	private InputStream is;
	private Bitmap b;

	private ConfigurationFilesAdapter configurationFilesAdapter;

	public final static String action="ifwifiusable_action";
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_opendoor);
		ActivityCollector.addActivity(this);
		mylisten = new mylisten();
		rbUnlockwithBluetooth=(LinearLayout)findViewById(R.id.rb_unlock_with_bluetooth);
		rbUnlockwithWiFi=(LinearLayout)findViewById(R.id.rb_unlock_with_wifi);
		textViewrbUnlockWithBluetooth=(TextView)findViewById(R.id.text_unlock_with_bluetooth);
		textViewrbUnlockwithWiFi=(TextView)findViewById(R.id.text_unlock_with_wifi);

		configurationFilesAdapter=new ConfigurationFilesAdapter();ConfigurationFilesAdapter configurationFilesAdapter=new ConfigurationFilesAdapter();

//		this.is = this.getResources().openRawResource(R.raw.bluetooth_true);
//		b = MyBitmapFactory.decodeRawBitMap(is);
//		this.bdForbluetoothRadioButtonTrue = new BitmapDrawable(getResources(), b);
//
//		this.is = this.getResources().openRawResource(R.raw.bluetooth_false);
//		b = MyBitmapFactory.decodeRawBitMap(is);
//		this.bdForbluetoothRadioButtonFalse = new BitmapDrawable(getResources(), b);
//
//		this.is = this.getResources().openRawResource(R.raw.wifi_true);
//		b = MyBitmapFactory.decodeRawBitMap(is);
//		this.bdForwifiRadioButtonTrue = new BitmapDrawable(getResources(), b);
//
//		this.is = this.getResources().openRawResource(R.raw.wifi_false);
//		b = MyBitmapFactory.decodeRawBitMap(is);
//		this.bdForwifiRadioButtonFalse = new BitmapDrawable(getResources(), b);

		this.is = this.getResources().openRawResource(R.raw.checkbox_off);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdYaoyiyaoSwitcherFalse = new BitmapDrawable(getResources(), b);

		this.is = this.getResources().openRawResource(R.raw.checkbox_on);
		b = MyBitmapFactory.decodeRawBitMap(is);
		this.bdYaoyiyaoSwitcherTrue = new BitmapDrawable(getResources(), b);
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
		if (HomeActivity.ifWifiUsable)
			setWiFiUsable(true);
		else
			setWiFiUsable(false);
//		if (HighVersionFragment.choice == 0) {
//			sild3.setImageResource(R.color.style);
//			sild2.setImageResource(R.color.write);
//			sild1.setImageResource(R.color.write);
//			opendoor_choice3.setImageDrawable(getResources().getDrawable(R.drawable.choice));
////			opendoor_choice2.setImageResource(R.color.write);
////			opendoor_choice1.setImageResource(R.color.write);
//
//		} else if (HighVersionFragment.choice == 1) {
//			sild1.setImageResource(R.color.style);
//			sild2.setImageResource(R.color.write);
//			sild3.setImageResource(R.color.write);
//			opendoor_choice1.setImageDrawable(getResources().getDrawable(
//					R.drawable.choice));
////			opendoor_choice2.setImageResource(R.color.write);
////			opendoor_choice3.setImageResource(R.color.write);
//
//		} else if (HighVersionFragment.choice == 2) {
//			sild2.setImageResource(R.color.style);
//			sild3.setImageResource(R.color.write);
//			sild1.setImageResource(R.color.write);
//			opendoor_choice2.setImageDrawable(getResources().getDrawable(R.drawable.choice));
////			opendoor_choice3.setImageResource(R.color.write);
////			opendoor_choice1.setImageResource(R.color.write);
//
//		} else if (HighVersionFragment.choice == 3) {
//			sild3.setImageResource(R.color.style);
//			sild2.setImageResource(R.color.write);
//			sild1.setImageResource(R.color.write);
//			opendoor_choice3.setImageDrawable(getResources().getDrawable(R.drawable.choice));
////			opendoor_choice2.setImageResource(R.color.write);
////			opendoor_choice1.setImageResource(R.color.write);
		//}

	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.ib_back_opendoor);
		back.setOnClickListener(mylisten);
//		relativeLayout1 = (RelativeLayout) findViewById(R.id.opendoor_layout1);
//		relativeLayout1.setOnClickListener(mylisten);
//		relativeLayout2 = (RelativeLayout) findViewById(R.id.opendoor_layout2);
//		relativeLayout2.setOnClickListener(mylisten);
//		relativeLayout3 = (RelativeLayout) findViewById(R.id.opendoor_layout3);
//		relativeLayout3.setOnClickListener(mylisten);
//		sild1 = (ImageView) findViewById(R.id.sild1_opendoor);
////		opendoor_choice1 = (ImageView) findViewById(R.id.opendoor_choice1);
//		sild2 = (ImageView) findViewById(R.id.sild2_opendoor);
////		opendoor_choice2 = (ImageView) findViewById(R.id.opendoor_choice2);
//		sild3 = (ImageView) findViewById(R.id.sild3_opendoor);
//		opendoor_choice3 = (ImageView) findViewById(R.id.opendoor_choice3);

//		bluetoothRadioButton = (RadioButton) findViewById(R.id.bluetooth_false);
//		wifiRadioButton = (RadioButton) findViewById(R.id.wifi_true);
		yaoyiyaoSwitcherCheckbox = (CheckBox)findViewById(R.id.yaoyiyao_switcher);
		//bluetoothRadioButton.

		/*BitmapDrawable bd = new BitmapDrawable(getResources(), bd);*/
		if(HomeActivity.ifWifiUsable){
//			bluetoothRadioButton.setCompoundDrawablesWithIntrinsicBounds(bdForbluetoothRadioButtonFalse, null, null, null);
//			wifiRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForwifiRadioButtonTrue, null);
			rbUnlockwithBluetooth.setBackgroundColor(getResources().getColor(R.color.gray2));
			rbUnlockwithWiFi.setBackgroundColor(getResources().getColor(R.color.style));
		}else {
//			bluetoothRadioButton.setCompoundDrawablesWithIntrinsicBounds(bdForbluetoothRadioButtonTrue, null, null, null);
//			wifiRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForwifiRadioButtonFalse, null);
			rbUnlockwithBluetooth.setBackgroundColor(getResources().getColor(R.color.gray2));
			rbUnlockwithWiFi.setBackgroundColor(getResources().getColor(R.color.style));
		}

		if(!Session.isShakingUnlockAble){
			yaoyiyaoSwitcherCheckbox.setCompoundDrawablesWithIntrinsicBounds(bdYaoyiyaoSwitcherFalse, null, null, null);
			yaoyiyaoSwitcherCheckbox.setChecked(false);
		}else{
			yaoyiyaoSwitcherCheckbox.setCompoundDrawablesWithIntrinsicBounds(null, null, bdYaoyiyaoSwitcherTrue, null);
			yaoyiyaoSwitcherCheckbox.setChecked(true);
		}
		//radioButton1

//		wifiRadioButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (wifiRadioButton.isChecked()){
//					HomeActivity.ifWifiUsable=true;
//					changeOpentype();
//					Toast.makeText(OpendoorActivity.this, "WiFi开门已启用", Toast.LENGTH_SHORT).show();
//					wifiRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForwifiRadioButtonTrue, null);
//					bluetoothRadioButton.setCompoundDrawablesWithIntrinsicBounds(bdForbluetoothRadioButtonFalse, null, null, null);
//					configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_UNLOCKMODE, ConfigKeyAndValue.USER_INFO_CONFIG_VALUE_UNLOCK_WIFI);
//					Session.setUnlockMode(1);
//				}
//				//注释结尾
//			}
//		});

		yaoyiyaoSwitcherCheckbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!yaoyiyaoSwitcherCheckbox.isChecked()){
					Session.setIsShakingUnlockAble(false);
					HomeActivity.ifShakeboxusable=false;
					yaoyiyaoSwitcherCheckbox.setCompoundDrawablesWithIntrinsicBounds(null, null, bdYaoyiyaoSwitcherFalse, null);
					//Toast.makeText(OpendoorActivity.this, "摇一摇功能已关闭", Toast.LENGTH_SHORT).show();
//					changeOpentype();
					Intent intent = new Intent(OpendoorActivity.this,ShakeSensorService.class);
					intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE, ShakeSensorService.CLOSE_SENSOR);
					startService(intent);
					configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_SHAKINGABLE, "false");


				}else {
					HomeActivity.ifShakeboxusable=true;
					yaoyiyaoSwitcherCheckbox.setCompoundDrawablesWithIntrinsicBounds(bdYaoyiyaoSwitcherTrue, null, null, null);
					changeOpentype();
					configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_SHAKINGABLE, "true");
					Intent intent= new Intent(OpendoorActivity.this,ShakeSensorService.class);
					intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
					intent.putExtra(ShakeSensorService.UNLOCK_TYPE,ShakeSensorService.OPEN_BY_WIFI);
					startService(intent);
					Session.setIsShakingUnlockAble(true);
				}
			}
		});

		/*is = this.getResources().openRawResource(R.raw.female_false);
		b = MyBitmapFactory.decodeRawBitMap(is);

		BitmapDrawable bd2 = new BitmapDrawable(getResources(), b);*/


//		bluetoothRadioButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				if (bluetoothRadioButton.isChecked()){
//					HomeActivity.ifWifiUsable=false;
//					changeOpentype();
//					Toast.makeText(OpendoorActivity.this,"蓝牙开门已启用",Toast.LENGTH_SHORT).show();
//					wifiRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, bdForwifiRadioButtonFalse, null);
//					bluetoothRadioButton.setCompoundDrawablesWithIntrinsicBounds(bdForbluetoothRadioButtonTrue, null, null, null);
//					configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_UNLOCKMODE, ConfigKeyAndValue.USER_INFO_CONFIG_VALUE_UNLOCK_BLUETOOTH);
//					Session.setUnlockMode(2);
//				}
//
//				//注释结尾
//			}
//		});

		rbUnlockwithBluetooth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setWiFiUsable(false);
			}
		});
		rbUnlockwithWiFi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setWiFiUsable(true);
			}
		});
	}

	private void changeOpentype(){
		if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == true) {
			Intent intent = new Intent(OpendoorActivity.this, WifiUnlocker.class);
			intent.setAction("start_wifi_unlock");
			intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.OPEN_SHAKE_FUNCTION);
			startService(intent);
		} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == true) {
			Intent intent = new Intent(OpendoorActivity.this, WifiUnlocker.class);
			intent.setAction("start_wifi_unlock");
			intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.CLOSE_SHAKE_FUNCTION);
			startService(intent);
		} else if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == false) {
			Intent intent = new Intent(OpendoorActivity.this, BluetoothControler.class);
			intent.setAction("start_bluetooth_unlock");
			intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.OPEN_SHAKE_FUNCTION);
			startService(intent);
		} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == false) {
			Intent intent = new Intent(OpendoorActivity.this, BluetoothControler.class);
			intent.setAction("start_bluetooth_unlock");
			intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.CLOSE_SHAKE_FUNCTION);
			startService(intent);
		}
	}

	class mylisten implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.ib_back_opendoor:
				finish();
				break;
//			case R.id.opendoor_layout1:
////				sild1.setImageResource(R.color.style);
////				sild2.setImageResource(R.color.write);
////				sild3.setImageResource(R.color.write);
////				opendoor_choice1.setImageDrawable(getResources().getDrawable(
////						R.drawable.choice));
////				opendoor_choice2.setImageResource(R.color.write);
////				opendoor_choice3.setImageResource(R.color.write);
////				HighVersionFragment.choice=1;
//				//Toast.makeText(OpendoorActivity.this,"暂不支持自动开门功能",Toast.LENGTH_SHORT).show();
//				Intent intent1 = new Intent(action);
//				intent1.putExtra("ifWifiUsable", true);
//				sendBroadcast(intent1);
//
//				Toast.makeText(OpendoorActivity.this,"wifi开门已启用",Toast.LENGTH_SHORT).show();
//				break;
//			case R.id.opendoor_layout2:
////				sild2.setImageResource(R.color.style);
////				sild3.setImageResource(R.color.write);
////				sild1.setImageResource(R.color.write);
////				opendoor_choice2.setImageDrawable(getResources().getDrawable(R.drawable.choice));
////				opendoor_choice3.setImageResource(R.color.write);
////				opendoor_choice1.setImageResource(R.color.write);
////				HighVersionFragment.choice=2;
//				//Toast.makeText(OpendoorActivity.this,"暂不支持摇一摇开门功能",Toast.LENGTH_SHORT).show();
//				Intent intent2 = new Intent(action);
//				intent2.putExtra("ifWifiUsable", false);
//				sendBroadcast(intent2);
//
//				Toast.makeText(OpendoorActivity.this,"蓝牙开门已启用",Toast.LENGTH_SHORT).show();
//
//				break;
//			case R.id.opendoor_layout3:
//				sild3.setImageResource(R.color.style);
//				sild2.setImageResource(R.color.write);
//				sild1.setImageResource(R.color.write);
//				//opendoor_choice3.setImageDrawable(getResources().getDrawable(R.drawable.choice));
////				opendoor_choice2.setImageResource(R.color.write);
////				opendoor_choice1.setImageResource(R.color.write);
//				HighVersionFragment.choice=3;
//				break;
			default:
				break;
			}

		}

	}

	private void setWiFiUsable(boolean isWiFiUsable){
		if(isWiFiUsable){
			rbUnlockwithBluetooth.setBackgroundColor(getResources().getColor(R.color.gray2));
			rbUnlockwithWiFi.setBackgroundColor(getResources().getColor(R.color.style));
			textViewrbUnlockwithWiFi.setTextColor(getResources().getColor(R.color.write));
			textViewrbUnlockWithBluetooth.setTextColor(getResources().getColor(R.color.gray1));
			HomeActivity.ifWifiUsable=true;
			configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_UNLOCKMODE, ConfigKeyAndValue.USER_INFO_CONFIG_VALUE_UNLOCK_WIFI);
			changeOpentype();
		}else if (HomeActivity.isAPIVersionRight){
			rbUnlockwithBluetooth.setBackgroundColor(getResources().getColor(R.color.style));
			rbUnlockwithWiFi.setBackgroundColor(getResources().getColor(R.color.gray2));
			textViewrbUnlockwithWiFi.setTextColor(getResources().getColor(R.color.gray1));
			textViewrbUnlockWithBluetooth.setTextColor(getResources().getColor(R.color.write));
			HomeActivity.ifWifiUsable=false;
			configurationFilesAdapter.SetOrUpdateProperty(ConfigKeyAndValue.USER_INFO_CONFIG_PATH, ConfigKeyAndValue.USER_INFO_CONFIG_KEY_UNLOCKMODE, ConfigKeyAndValue.USER_INFO_CONFIG_VALUE_UNLOCK_BLUETOOTH);
			changeOpentype();
		}else {
			Toast.makeText(OpendoorActivity.this, "API版本太低，无法使用蓝牙", Toast.LENGTH_SHORT).show();
		}
	}
}
