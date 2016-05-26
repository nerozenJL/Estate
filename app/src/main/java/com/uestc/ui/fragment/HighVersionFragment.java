package com.uestc.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.constant.Host;
import com.uestc.domain.BluetoothProtalInfo;
import com.uestc.domain.Session;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.service.BluetoothControler;
import com.uestc.ui.activity.BillActivity;
import com.uestc.ui.activity.MyComplainListActivity;
import com.uestc.ui.activity.MyRepairListActivity;
import com.uestc.ui.activity.R;
import com.uestc.ui.activity.TiedOwnersActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这是高版本的首页界面
 */
public class HighVersionFragment extends Fragment {

	public static final int NO_MANAGE = 0;
	public static final int MANAGE_SUCCESS = 1;
	private View view;
	private TextView opendialog_title;
	private LinearLayout opendoor;
	//public static AlertDialog dialog, openDialog;
	/**
	 * 0代表默认开门 按键开门，1 自动，2摇一摇 3按键
	 */
	public static int status = NO_MANAGE;//0：没有操作，1：成功；3：超时；
	private boolean BACKSTACK_STATUS = true;
	private String ssid = "";
	private Timer mTimer;
	public static String bluetooth_name="";
	public static int choice = 0;
	/*******
	 * 蓝牙
	 */
	private BluetoothControler mbluetoothControler;
	private int ZI_SUO = 0;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter;
	private String reason="";
	public static boolean isScan =false;
	private LinearLayout complain,repair,pay;
	private boolean ifyaoyiyao=false;
	private TextView bluetoothopentext;
	private ImageView imageView;
	private LinearLayout bleLinearLayout;
	/**
	 * 0表示未注册，1表示已注册
	 */
	public static int register = 0;
	/**
	 * 0表示未绑定服务，1表示已绑定服务
	 */
	public static int bind = 0;

	/*呼叫物业*/
	/*2016/4/7 呼叫物业管理*/
	private LinearLayout calloutPropertyManagerLayout;
	private List<String> propertyManagementCompanyPhoneNumberList = TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList;
	private ArrayAdapter<String> phoneNumberListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		BACKSTACK_STATUS = true;
		if (view == null) {

			view = inflater.inflate(R.layout.fragment_high_open, container, false);

			/*保修、投诉、支付*/
			complain=(LinearLayout) view.findViewById(R.id.ib_complain_housekeeper);
			repair=(LinearLayout) view.findViewById(R.id.ib_replair_housekeeper);
			pay = (LinearLayout) view.findViewById(R.id.ib_pay_housekeeper);
			bluetoothopentext=(TextView)view.findViewById(R.id.bt_bluetooth_text);
			imageView=(ImageView)view.findViewById(R.id.ble_open_imageview);

			complain.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					if(IsAccessableToHouseKeeperService()){//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
						Intent intentToMyComplainListActivity = new Intent(getActivity(), MyComplainListActivity.class);
						startActivity(intentToMyComplainListActivity);
					}
					else{
						if (Session.getRole()==-1){
							Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
							Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
							intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
							startActivity(intentToTiedOwnerActivity);
						}
						if (Session.getRole()==0){
							Toast.makeText(getActivity(), "您没有权限使用此功能", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			repair.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					/*Intent intent2 = new Intent(getActivity(), MyRepairListActivity.class);
					startActivity(intent2);*/
					if(IsAccessableToHouseKeeperService()){//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
						Intent intentToMyRepairListActivity = new Intent(getActivity(), MyRepairListActivity.class);
						startActivity(intentToMyRepairListActivity);
					}
					else{
						if (Session.getRole()==-1){
							Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
							Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
							intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
							startActivity(intentToTiedOwnerActivity);
						}
						if (Session.getRole()==0){
							Toast.makeText(getActivity(), "您没有权限使用此功能", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
			pay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					/*Intent intent = new Intent(getActivity(),
							BillActivity.class);
					startActivity(intent);*/
					if(IsAccessableToHouseKeeperService()){//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
						Intent intentToMyRepairListActivity = new Intent(getActivity(), BillActivity.class);
						startActivity(intentToMyRepairListActivity);
					}
					else{
						if (Session.getRole()==-1){
							Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
							Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
							intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
							startActivity(intentToTiedOwnerActivity);
						}
						if (Session.getRole()==0){
							Toast.makeText(getActivity(), "您没有权限使用此功能", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			opendoor = (LinearLayout) view.findViewById(R.id.ib_open_park_high_open);
			/**
			 * 开园区门
			 *
			 */
			opendoor.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if(IsAccessableToUnLockProtal()){
						bluetooth_name = "YCWY";
						Intent intent=new Intent(getActivity(),BluetoothControler.class);
						intent.setAction("start_bluetooth_unlock");
						intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.UNLOCKDOOR_BY_BUTTON);
						getActivity().startService(intent);
						bluetoothopentext.setText("正在开锁");
						imageView.setImageResource(R.drawable.unlock_garden_pressed);
						opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_pressed));
						opendoor.setEnabled(false);
					}
					else{
						Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
						Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
						intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
						startActivity(intentToTiedOwnerActivity);
					}
				}
			});

			//opendoor2 = (LinearLayout) view.findViewById(R.id.ib_open_build_high_open);
			/**
			 * 楼栋按钮蓝牙开门
			 */
//			opendoor2.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View arg0) {
//					if(IsAccessableToUnLockProtal()) {
//						bluetooth_name = "YCWY";
//						myBluetoothExecute();
//						List<WiFiProtalInfo> wiFiProtalInfoList=Session.getAccessableWiFiSsidList();
//						for (int i=0;i<wiFiProtalInfoList.size();i++){  //获取用户当前的权限列表
//						}
//					}
//					else{
//						Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
//						Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
//						intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
//						startActivity(intentToTiedOwnerActivity);
//					}
//				}
//			});
			/**
 			 *车闸按钮蓝牙开门
			 **/
//			opendoor3= (ImageButton) view.findViewById(R.id.ib_open_car_high_open);
//
//			opendoor3.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					bluetooth_name = "YCWY_C";
//					myBluetoothExecute();
//				}
//			});

			IntentFilter BLEfilter = new IntentFilter(BluetoothControler.BLUETOOTH_CONTROLER_BROADCAST);
			getActivity().registerReceiver(broadcastReceiver, BLEfilter);
		}

		//160307新增
		//setImageButtonBitMap(true);

		/**
		 * 这是保证在返回到开门界面，status=3时，两个按钮可用点击
		 */
		if(status == 3) {
			//setImageButtonBitMap(true);

			opendoor.setClickable(true);
			//opendoor2.setClickable(true);

			status = 0;
		}

		/*2016/4/7新增呼叫物业*/
		calloutPropertyManagerLayout = (LinearLayout)view.findViewById(R.id.ib_call_out_property_manager);
		//propertyManagementCompanyPhoneNumberList.add("123");
		//propertyManagementCompanyPhoneNumberList.add("456");
		phoneNumberListAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item);
		//phoneNumberListAdapter.add("18680237011"); // 测试用的电话，正式使用时请注释

		for(int index = 0; index < propertyManagementCompanyPhoneNumberList.size(); index++){
			phoneNumberListAdapter.add(propertyManagementCompanyPhoneNumberList.get(index));
		}

		calloutPropertyManagerLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					/*创建一个列表对话框，将获得的电话号码展示，并给每个列表元素添加对应拨号操作*/
				AlertDialog.Builder propertyManagerPhoneNumbersList = new AlertDialog.Builder(getActivity());
				propertyManagerPhoneNumbersList.setTitle("物业电话:");

				propertyManagerPhoneNumbersList.setAdapter(phoneNumberListAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d("phone enum", "电话触发");
						Intent dialIntent = new Intent();
						dialIntent.setAction(Intent.ACTION_CALL);
						dialIntent.setData(Uri.parse("tel:" + phoneNumberListAdapter.getItem(which).toString()));
						Context context = HighVersionFragment.this.getActivity();
						HighVersionFragment.this.getActivity().startActivity(dialIntent);
					}
				});
				propertyManagerPhoneNumbersList.create().show();
			}
		});

		return view;
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			int receive_msg=intent.getIntExtra("ACTION_TYPE",-1);
			switch (receive_msg){
				case BluetoothControler.BLUETOOTH_SCAN_OUT_TIME:
					bluetoothopentext.setText("无可用设备");
					opendoor.setEnabled(true);
					imageView.setImageResource(R.drawable.unlock_garden);
					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_SCAN_SUCCESS:
					bluetoothopentext.setText("扫描成功");
					break;
				case BluetoothControler.BLUETOOTH_SCAN_START:
					bluetoothopentext.setText("扫描开始");
					break;
				case BluetoothControler.BLUETOOTH_SCAN_FAILED:
					bluetoothopentext.setText("启动扫描失败\n请重试");
					opendoor.setEnabled(true);
					imageView.setImageResource(R.drawable.unlock_garden);
					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_CONNECT_SUCCESS:
					bluetoothopentext.setText("连接成功");
					break;
				case BluetoothControler.BLUETOOTH_CONNECT_FAILED:
					bluetoothopentext.setText("连接失败");
//					opendoor.setEnabled(true);
					break;
				case BluetoothControler.BLUETOOTH_CONNECT_OUTTIME:
					bluetoothopentext.setText("连接超时\n请稍后");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_DISCONNECT_SUCCESS:
					bluetoothopentext.setText("断开连接成功\n请等待锁复位");//原disconnect success
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_DISCONNECT_FAILED:
					bluetoothopentext.setText("断开连接失败\n请等待锁复位");//原disconnect failed
					break;
				case BluetoothControler.BLUETOOTH_DISCONNECT_OUTTIME:
					bluetoothopentext.setText("断开连接超时\n请等待锁复位");//原disocnnect outtime
					break;
				case BluetoothControler.BLUETOOTH_SERVICE_DISCOVERED_SUCCESS:
					bluetoothopentext.setText("成功找到服务");//原"成功发现蓝牙service"
					break;
				case BluetoothControler.BLUETOOTH_SERVICE_DISCOVERED_FAILED:
					bluetoothopentext.setText("寻找服务失败");//原"寻找蓝牙service失败"
					break;
				case BluetoothControler.BLUETOOTH_DEVICE_CONNECTING:
					bluetoothopentext.setText("正在连接蓝牙");
					break;
				case BluetoothControler.BLUETOOTH_DEVICE_CONNECT_FAILED_BY_MISSING_DEVICE:
					bluetoothopentext.setText("蓝牙连接中断");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_OPENING:
					bluetoothopentext.setText("正在开启蓝牙");
					break;
				case BluetoothControler.BLUETOOTH_OPEN_SUCCESS:
					bluetoothopentext.setText("开启蓝牙成功");
					break;
				case BluetoothControler.BLUETOOTH_OPEN_OUTTIME:
					bluetoothopentext.setText("开启蓝牙超时");
					opendoor.setEnabled(true);
					imageView.setImageResource(R.drawable.unlock_garden);
					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_OPEN_ALREADY:
					bluetoothopentext.setText("蓝牙已经开启\n开始连接");
					break;
				case BluetoothControler.BLUETOOTH_DISSCONNECT_START:
					bluetoothopentext.setText("蓝牙连接断开\n点击再次开锁");
					break;
				case BluetoothControler.BLUETOOTH_COMMUNICATE_SUCCESS:
//					bluetoothopentext.setText("蓝牙通信成功");
					break;
				case BluetoothControler.BLUETOOTH_COMMUNICATE_FAILED:
					bluetoothopentext.setText("蓝牙通信失败");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_COMMUNICATE_RETURN_VALUE_CORRECT:
					bluetoothopentext.setText("开锁反馈正确");
//					opendoor.setEnabled(true);
					break;
				case BluetoothControler.BLUETOOTH_COMMUNICATE_RETURN_VALUE_INCORRECT:
					bluetoothopentext.setText("开锁反馈异常");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.BLUETOOTH_COMMUNICATE_OUTTIME:
					bluetoothopentext.setText("发送指令超时");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.LAST_BLUETOOTH_UNLOCK_NOT_FINISHED:
					bluetoothopentext.setText("上次开锁未结束");
					break;
				case BluetoothControler.BLUETOOTH_LOCK_RESETFINISHED:
					bluetoothopentext.setText("一键开锁");
					opendoor.setEnabled(true);
					imageView.setImageResource(R.drawable.unlock_garden);
					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;
				case BluetoothControler.USER_HAVE_NO_BLUETOOTH_DEVICE_PROPERTY:
					bluetoothopentext.setText("蓝牙未开启");
//					opendoor.setEnabled(true);
//					imageView.setImageResource(R.drawable.unlock_garden);
//					opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
					break;

				default:
					bluetoothopentext.setText(""+receive_msg);
					break;
			}
		}

	};

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == REQUEST_ENABLE_BT
//				&& resultCode == Activity.RESULT_CANCELED) {
//			reason = "没有打开蓝牙";
//			return;
//		}
//		status = NO_MANAGE;
//		mbluetoothAdmin = new BluetoothAdmin(getActivity(),mBluetoothAdapter);
//		mbluetoothAdmin.scanLeDevice(true);
//		//showOpenWifiDialog();
//		//opendialog_title.setText("正在开锁");
//		mHandler3.sendEmptyMessage(100);
//		startTimer1();
//		// new waitTask().execute();
//
//	}
//
//	private void myBluetoothExecute(){
//		isScan = false;
//
//		if (!getActivity().getPackageManager().hasSystemFeature(
//				PackageManager.FEATURE_BLUETOOTH_LE)) {
//			Toast.makeText(getActivity(), "BLE is not supported",
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
//				.getSystemService(Context.BLUETOOTH_SERVICE);
//		mBluetoothAdapter = bluetoothManager.getAdapter();
//		if (mBluetoothAdapter == null) {
//			Toast.makeText(getActivity(),
//					"Bluetooth not supported", Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
//		if (!mBluetoothAdapter.isEnabled()) {
//			// if
//			// (!mBluetoothAdapter.isEnabled())
//			// {
//			Intent enableBtIntent = new Intent(
//					BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			startActivityForResult(enableBtIntent,
//					REQUEST_ENABLE_BT);
//		} else {
//			mbluetoothAdmin = new BluetoothAdmin(getActivity(),mBluetoothAdapter);
//			mbluetoothAdmin.scanLeDevice(true);
//			List<BluetoothProtalInfo> bluetoothProtalInfos=Session.getAccessableBlueToothSsidList();
//
//			String string="";
//			List<BluetoothProtalInfo> wiFiProtalInfoList=Session.getAccessableBlueToothSsidList();
//			for (int i=0;i<wiFiProtalInfoList.size();i++){
//				if (mbluetoothAdmin.ssid.equals(wiFiProtalInfoList.get(i).getSsid())){
//					string=wiFiProtalInfoList.get(i).getAccessSecret();
//					break;
//				}
//			}
//
//			HighVersionFragment.status = HighVersionFragment.NO_MANAGE;
//            //showOpenWifiDialog();
//            //opendialog_title.setText("正在开锁");
//            mHandler3.sendEmptyMessage(100);
//            startTimer1();
//		}
//	}
//
//
//
//	@Override
//	public void onDestroyView () {
//		super.onDestroyView();
//		BACKSTACK_STATUS = false;
//		Log.e("onDestroyView","onDestroyView------》");
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		BACKSTACK_STATUS = false;
//		if (register == 1) {
//		mbluetoothAdmin.myUnregister();
//			register = 0;
//		}
//
//		if (bind == 1) {
//			mbluetoothAdmin.myUnbind();
//			bind = 0;
//		}
//
//		if (mbluetoothAdmin != null){
//			mbluetoothAdmin.myServiceDisconnect();
//		}
//		Log.e("onDestroy", "onDestroy------》");
//	}
//
//
//	/**
//	 * 这是蓝牙的反馈
//	 */
//	private Handler mHandler3 = new Handler() {
//		public void handleMessage(Message msg) {
//			if (status == NO_MANAGE) {
//				mHandler3.sendEmptyMessage(100);//反复回调，等待status变化
//			} else if (status == MANAGE_SUCCESS) {
//				Toast.makeText(getActivity(), "开锁成功", Toast.LENGTH_SHORT)
//						.show();
//				status = NO_MANAGE;
//				stopTimer();
//				mbluetoothAdmin.myUnbind();
//				mbluetoothAdmin.myUnregister();
//				mbluetoothAdmin.myServiceDisconnect();
//				ShaketoUnlockService.isshakeUsable=true;
//				new SuccessuploadDoorTask().execute();
//				mhandler4.sendEmptyMessage(0);
//				setImageButtonBitMap(false);
//
//				opendoor.setClickable(false);
//				startTimer1();
////				if (openDialog.isShowing()){
////					openDialog.cancel();
////				}
//			}else if (status == 3) {
//				//这是timer时间到了，自动把status变成3
//
//				mbluetoothAdmin.stopScan();
//				if (isScan){
//
//					Toast.makeText(getActivity(), "等待反馈超时", Toast.LENGTH_SHORT)
//							.show();
//					new FailOpendoorTask().execute();
//					mbluetoothAdmin.myUnbind();
//					mbluetoothAdmin.myUnregister();
//					mbluetoothAdmin.myServiceDisconnect();
//					ShaketoUnlockService.isshakeUsable=false;
//					reason = "等待反馈超时";
//				}else{
//					Toast.makeText(getActivity(), "没有检查到设备", Toast.LENGTH_SHORT)
//							.show();
//					reason = "没有检测到设备";
//				}
//				status = 0;
//
////				if (openDialog.isShowing()){
////					openDialog.cancel();
////				}
//				stopTimer();
//				Log.e("status == 3", "timer时间到了");
//				ShaketoUnlockService.isshakeUsable=true;
//			}else if (status == 4 && BACKSTACK_STATUS ){
//				status =0;
//				stopTimer();
////				if (openDialog.isShowing()){
////					openDialog.cancel();
////				}
//				ShaketoUnlockService.isshakeUsable=true;
//			}else {
//				//不可能运行到这里
//				Toast.makeText(getActivity(), "开锁失败", Toast.LENGTH_SHORT)
//						.show();
//
//				new FailOpendoorTask().execute();
//				status = 0;
////				if (openDialog.isShowing()){
////					openDialog.cancel();
////				}
//				stopTimer();
//				ShaketoUnlockService.isshakeUsable=true;
//			}
//
//		};
//
//	};
//
//
//
//	private Handler mhandler4 = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (status == 0){
//				mhandler4.sendEmptyMessage(0);
//
//			}else if (status == 3&& BACKSTACK_STATUS ){
//				stopTimer();
//				setImageButtonBitMap(true);
//				opendoor.setClickable(true);
//				if(ifyaoyiyao=true) {
//					ifyaoyiyao = false;
//					ShaketoUnlockService.isshakeUsable = true;
//				}
//				mbluetoothAdmin.myServiceDisconnect();
//				status = 0;
//			}
//		}
//	};
//
//	/**
//	 * 计时器
//	 */
//	private class MyTimer extends TimerTask {
//		@Override
//		public void run() {
//			Log.e("status_test1",""+status);
//			status = 3;
//			Log.e("status_test2",""+status);
//		}
//
//	}
//	//10秒的计时
//	private void startTimer() {
//		if (mTimer != null) {
//			stopTimer();
//		}
//		mTimer = new Timer(true);
//		mTimer.schedule(new MyTimer(), 10 * 1000);
//		Log.e("startTimer", "startTimer--------》");
//	}
////5秒的计时
//	private void startTimer1(){
//		if (mTimer != null) {
//			stopTimer();
//		}
//
//		mTimer = new Timer(true);
//		mTimer.schedule(new MyTimer(), 5 * 1000);
//	}
//	private void startTimer2(){
//		if (mTimer != null) {
//			stopTimer();
//		}
//
//		mTimer = new Timer(true);
//		mTimer.schedule(new MyTimer(), 8 * 1000);
//	}
//
//	private void stopTimer() {
//		if (mTimer != null) {
//			mTimer.cancel();
//			mTimer = null;
//		}
//		Log.e("bluetooth","stopTimer");
//	}

	class SuccessuploadDoorTask extends  AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... voids) {

			String result = opendoorSuccess();
			return result;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!s.equals("")){
				try {
					JSONObject object = new JSONObject(s);
					boolean status = object.getBoolean("status");
					if (status){

					}else{
						new SuccessuploadDoorTask().execute();
					}
				} catch (Exception e) {

				}


			}else{
				new SuccessuploadDoorTask().execute();
			}
		}
	}

	class FailOpendoorTask extends  AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... voids) {
			String result = FailOpendoor();
			return result;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!s.equals("")){
				try {
					JSONObject object = new JSONObject(s);
					boolean status = object.getBoolean("status");
					if (status){

					}else{
						new FailOpendoorTask().execute();
					}
				} catch (Exception e) {

				}


			}else{
				new FailOpendoorTask().execute();
			}


		}
	}
	//开门成功
	private String opendoorSuccess(){
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.uploadDoorLog);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		paramters.add(new BasicNameValuePair("symbol", ssid));
		paramters.add(new BasicNameValuePair("status","1"));
		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(paramters, "UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  result;
	}
//开门失败
	private String FailOpendoor(){
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.uploadDoorLog);
		httpPost.setHeader("cookie", Session.getSeesion());
		List<NameValuePair> paramters = new ArrayList<NameValuePair>();
		paramters.add(new BasicNameValuePair("symbol", ssid));
		paramters.add(new BasicNameValuePair("status", "0"));
		paramters.add(new BasicNameValuePair("description",reason));
		paramters.add(new BasicNameValuePair("level","0"));

		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(paramters, "UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return  result;
	}





//显示dialog
//	public void showOpenWifiDialog() {
//		LayoutInflater inflater = LayoutInflater.from(getActivity());
//		View view = inflater.inflate(R.layout.dialog_wait_openwifi, null);
//		opendialog_title = (TextView) view.findViewById(R.id.tv_connect_wait_openwifi);
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setView(view);
//		openDialog = builder.create();
//		openDialog.setCanceledOnTouchOutside(false);
//		openDialog.show();
//		// WindowManager.LayoutParams params =
//		// dialog2.getWindow().getAttributes();
//		// params.width = view.getWidth();
//		// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		// dialog2.getWindow().setAttributes(params);
//	}



	@Override
		 public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void setImageButtonBitMap(boolean ifsuccess){
		if (ifsuccess){
			bluetoothopentext.setText("一键开锁");
			opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
			opendoor.setClickable(true);
			pay.setClickable(true);
			complain.setClickable(true);
			repair.setClickable(true);
//			Bitmap unlockBitmap;
//			InputStream is=view.getResources().openRawResource(R.raw.building_unlock_true);
//			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//			opendoor2.setImageBitmap(unlockBitmap);
//			is=view.getResources().openRawResource(R.raw.garden_unlock_true);
//			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//			opendoor.setImageBitmap(unlockBitmap);
//				is=view.getResources().openRawResource(R.raw.car_unlock_true);
//				unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//				opendoor3.setImageBitmap(unlockBitmap);
//			is = view.getResources().openRawResource(R.raw.complain);
//			Bitmap b = MyBitmapFactory.decodeRawBitMap(is);
//			complain.setImageBitmap(b);
//
//			is = view.getResources().openRawResource(R.raw.complair);
//			b = MyBitmapFactory.decodeStream(is);
//			repair.setImageBitmap(b);
//
//			is = view.getResources().openRawResource(R.raw.pay);
//			b = MyBitmapFactory.decodeStream(is);
//			pay.setImageBitmap(b);
		}else {
			bluetoothopentext.setText("正在开锁");
			opendoor.setClickable(false);
			opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_pressed));
			pay.setClickable(false);
			complain.setClickable(false);
			repair.setClickable(false);
//			Bitmap unlockBitmap;
//			InputStream is=view.getResources().openRawResource(R.raw.building_unlock_false);
//			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//			opendoor2.setImageBitmap(unlockBitmap);
//			is=view.getResources().openRawResource(R.raw.garden_unlock_false);
//			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//			opendoor.setImageBitmap(unlockBitmap);
//				is=view.getResources().openRawResource(R.raw.car_unlock_false);
//				unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//				opendoor3.setImageBitmap(unlockBitmap);
		}
	}

	/*2016-3-23 为开锁，管家活动相应添加一个判定函数，判断用户是否具有享受服务的角色权限*/
	private boolean IsAccessableToUnLockProtal(){
		boolean isAccessable = true;
		switch(Session.getRole()){
			case -1:
				isAccessable = false;
				break;
			default:
				isAccessable = true;
				break;
		}
		return isAccessable;
	}
	private boolean IsAccessableToHouseKeeperService(){
		boolean isAccessable = true;
		switch(Session.getRole()){
			case -1:
				isAccessable = false;
				break;
			case 0:
				isAccessable=false;
				break;
			default:
				isAccessable = true;
				break;
		}
		return isAccessable;
	}

}
