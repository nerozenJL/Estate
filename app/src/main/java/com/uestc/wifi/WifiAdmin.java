package com.uestc.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

import com.uestc.domain.Session;
import com.uestc.domain.WiFiProtalInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这是wifi的操作类，里面包含对wifi的操作方法
 */
public abstract class WifiAdmin {

	private static final String TAG = "WifiAdmin";

	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	private List<WifiConfiguration> mWifiConfiguration;

	private WifiLock mWifiLock;

	private Context mContext = null;
	private boolean isErrorConnect = false;

	public WifiAdmin(Context context) {

		this.mContext = context;

		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();

		Log.v(TAG, "getIpAddress = " + mWifiInfo.getIpAddress());
	}

	// 打开WIFI
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public boolean getWiFiStatus() {

		if (mWifiManager.isWifiEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	// 关闭WIFI
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	public abstract Intent myRegisterReceiver(BroadcastReceiver receiver,
											  IntentFilter filter);

	public abstract void myUnregisterReceiver(BroadcastReceiver receiver);

	public abstract void onNotifyWifiConnected();

	public abstract void onNotifyWifiConnectFailed();

	public abstract void onNotifyWifiConnecting();
	public abstract void onTimeOut();

	// 添加一个网络并连接
	public void addNetwork(WifiConfiguration wcg) {
		Log.d("Nov.12", "regist test");
		if (!isErrorConnect){
			register();
			Log.e("first----》","in in in ------->");
		}


		WifiApAdmin.closeWifiAp(mContext);
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b = mWifiManager.enableNetwork(wcgID, true);
		isErrorConnect = false;
	}

	public static final int TYPE_NO_PASSWD = 0x11;
	public static final int TYPE_WEP = 0x12;
	public static final int TYPE_WPA = 0x13;

	public void addNetwork(String ssid, String passwd, int type) {
		if (ssid == null || passwd == null || ssid.equals("")) {
			Log.e(TAG, "addNetwork() ## ssid："+ssid);
			Log.e(TAG, "addNetwork() ## passwd："+passwd);
			Log.e(TAG, "addNetwork() ## nullpointer error!");
			return;
		}

		if (type != TYPE_NO_PASSWD && type != TYPE_WEP && type != TYPE_WPA) {
			Log.e(TAG, "addNetwork() ## unknown type = " + type);
		}

		stopTimer();
		unRegister();

		addNetwork(createWifiInfo(ssid, passwd, type));
	}

	public void simpleaddNetwork(WifiConfiguration wcg) {
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean b =  mWifiManager.enableNetwork(wcgID, true);
		System.out.println("a--" + wcgID);
		System.out.println("b--" + b);
	}

	public void addErrorNetWork(String ssid, String passwd, int type) {
		if (ssid == null || passwd == null || ssid.equals("")) {
			Log.e(TAG, "addNetwork() ## nullpointer error!");
			return;
		}
		if (type != TYPE_NO_PASSWD && type != TYPE_WEP && type != TYPE_WPA) {
			Log.e(TAG, "addNetwork() ## unknown type = " + type);
		}

		isErrorConnect = true;
		addNetwork(createWifiInfo(ssid, passwd, type));
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			Log.e("wifi1", "BroadcastReceiver out out ");
				// 有可能是正在获取，或者已经获取了
				Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);
				Log.e("wifi1", "BroadcastReceiver in in ");

				switch (isWifiContected(mContext)){
					case WIFI_CONNECTED:
						Log.e("wifi1", "WIFI_CONNECTED --->");
						onNotifyWifiConnected();
						stopTimer();
						unRegister();
						break;
					case WIFI_CONNECT_FAILED:
						Log.e("wifi1", "WIFI_CONNECT_FAILED --->");
						stopTimer();
						onNotifyWifiConnectFailed();
						break;
					case WIFI_CONNECTING:
						Log.e("wifi1","WIFI_CONNECTING --->");
						onNotifyWifiConnecting();
						break;
					default:
						break;
				}
//				if (isWifiContected(mContext) == WIFI_CONNECTED) {
//					Log.e("wifi1","WIFI_CONNECTED --->");
//					onNotifyWifiConnected();
//					stopTimer();
//					unRegister();
//
//
//				} else if (isWifiContected(mContext) == WIFI_CONNECT_FAILED) {
//					Log.e("wifi1","WIFI_CONNECT_FAILED --->");
//					stopTimer();
//					onNotifyWifiConnectFailed();
//
//				} else if (isWifiContected(mContext) == WIFI_CONNECTING) {
//					Log.e("wifi1","WIFI_CONNECTING --->");
//					onNotifyWifiConnecting();
//				}

		}
	};

	private final int STATE_REGISTRING = 0x01;
	private final int STATE_REGISTERED = 0x02;
	private final int STATE_UNREGISTERING = 0x03;
	private final int STATE_UNREGISTERED = 0x04;

	private int mHaveRegister = STATE_UNREGISTERED;

	private synchronized void register() {
		Log.v(TAG, "register() ##mHaveRegister = " + mHaveRegister);

		if (mHaveRegister == STATE_REGISTRING
				|| mHaveRegister == STATE_REGISTERED) {
			return;
		}

		mHaveRegister = STATE_REGISTRING;
		IntentFilter wifiIntentFilter = new IntentFilter();
		wifiIntentFilter.addAction("android.net.wifi.RSSI_CHANGED");
		wifiIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
		wifiIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		myRegisterReceiver(mBroadcastReceiver, wifiIntentFilter);
		Log.e("wifi1","RegisterReceiver");
		mHaveRegister = STATE_REGISTERED;

		if (mTimer == null) {
			startTimer();
		}

	}

	private synchronized void unRegister() {
		Log.v(TAG, "unRegister() ##mHaveRegister = " + mHaveRegister);
		if (mHaveRegister == STATE_UNREGISTERED
				|| mHaveRegister == STATE_UNREGISTERING) {
			return;
		}

		mHaveRegister = STATE_UNREGISTERING;
		myUnregisterReceiver(mBroadcastReceiver);
		mHaveRegister = STATE_UNREGISTERED;
	}

	private Timer mTimer = null;

	private void startTimer() {
		if (mTimer != null) {
			stopTimer();

		}
		Log.e("mtimer","null null ------》");
		mTimer = new Timer(true);
		// mTimer.schedule(mTimerTask, 0, 20 * 1000);// 20s
		mTimer.schedule(mTimerTask, 5000);
	}

	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e("wifi1", "timer out!");
			// onNotifyWifiConnectFailed();
			unRegister();
			onTimeOut();
//			Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
//

		}
	};

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	protected void finalize() {
		try {
			super.finalize();
			unRegister();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WifiConfiguration createWifiInfo(String SSID, String password,
											int type) {

		Log.v(TAG, "SSID = " + SSID + "## Password = " + password
				+ "## Type = " + type);

		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		// 分为三种情况：1没有密码2用wep加密3用wpa加密
		if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;

		} else if (type == TYPE_WEP) { // WIFICIPHER_WEP
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WPA) { // WIFICIPHER_WPA
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}

		return config;
	}

	public static final int WIFI_CONNECTED = 0x01;
	public static final int WIFI_CONNECT_FAILED = 0x02;
	public static final int WIFI_CONNECTING = 0x03;

	/**
	 * 判断wifi是否连接成功,不是network
	 *
	 * @param context
	 * @return
	 */
	public int isWifiContected(Context context) {
		Log.e("wifi1", "in in in in");
		try{
			Thread.sleep(100);
		}catch (Exception e){

		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//		Log.v(TAG,
//				"isConnectedOrConnecting = "
//						+ wifiNetworkInfo.isConnectedOrConnecting());
//		Log.d(TAG,
//				"wifiNetworkInfo.getDetailedState() = "
//						+ wifiNetworkInfo.getDetailedState());
		switch (wifiNetworkInfo.getDetailedState()){
			case OBTAINING_IPADDR:
				Log.e("wifi1","WIFI_CONNECTING");
				return WIFI_CONNECTING;

			case CONNECTING:
				Log.e("wifi1","WIFI_CONNECTING");
				return WIFI_CONNECTING;

			case CONNECTED:
				Log.e("wifi1","WIFI_CONNECTED");
				return WIFI_CONNECTED;
			case FAILED:
				Log.e("wifi1","FAILED");
				return WIFI_CONNECT_FAILED;

			default:
				Log.e("wifi1",wifiNetworkInfo.getDetailedState()+"default");
				break;

		}
//		if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR
//				|| wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING) {
//			Log.e("wifi1","WIFI_CONNECTING");
//			return WIFI_CONNECTING;
//		} else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED) {
//			Log.e("wifi1","WIFI_CONNECTED");
//			return WIFI_CONNECTED;
//
//		} else if (wifiNetworkInfo.getDetailedState() == DetailedState.FAILED) {
//			Log.d(TAG,
//					"getDetailedState() == "
//							+ wifiNetworkInfo.getDetailedState());
//			return WIFI_CONNECT_FAILED;
//
//		}
		return 0;
	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"") /*
																 * &&
																 * existingConfig
																 * .
																 * preSharedKey.
																 * equals("\"" +
																 * password +
																 * "\"")
																 */) {
				return existingConfig;
			}
		}
		return null;
	}

	// 断开指定ID的网络
	public void disconnectWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	// 检查当前WIFI状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定WifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁WifiLock
	public void releaseWifiLock() {
		// 判断时候锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个WifiLock
	public void creatWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("Test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfiguration;
	}

	// 指定配置好的网络进行连接
	public void connectConfiguration(int index) {
		// 索引大于配置好的网络索引返回
		if (index > mWifiConfiguration.size()) {
			return;
		}
		// 连接配置好的指定ID的网络
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
				true);
	}

	public void startScan() {
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuilder lookUpScan() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < mWifiList.size(); i++) {
			stringBuilder
					.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			stringBuilder.append((mWifiList.get(i)).toString());
			stringBuilder.append("/n");
		}
		return stringBuilder;
	}

	// 得到MAC地址
	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	// 得到接入点的BSSID
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	// 得到IP地址
	public int getIPAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetworkId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public void removeWifi(int netId) {
		disconnectWifi(netId);
		mWifiManager.removeNetwork(netId);
	}

	public void disconnectCurrentWiFi(){
		mWifiManager.disconnect();
	}

	// 得到WifiInfo的所有信息包
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	public String getCurrentwifiSSID(){
		if (mWifiInfo==null){
			return "NULL";
		}else {
			String ssid=mWifiInfo.getSSID();
			return ssid;
		}
		//return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	public boolean networkStatusOK(Context mContext) {
		boolean netStatus = false;
		try {
			ConnectivityManager connectManager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectManager
					.getActiveNetworkInfo();
			if (activeNetworkInfo != null) {
				if (activeNetworkInfo.isAvailable()
						&& activeNetworkInfo.isConnected()) {
					netStatus = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return netStatus;
	}


	public List<WiFiProtalInfo> RefreshWiFi(){
		List<WiFiProtalInfo> AccessableWiFiList = null;
		AccessableWiFiList = ScanWiFi();//扫描可用的WiFi
		AccessableWiFiList = FilterWiFiByAccessableSSID(AccessableWiFiList, Session.getAccessableWiFiSsidList());//根据下载好的SSID列表进行筛选
		AccessableWiFiList = SortWiFiBySignalStrenth(AccessableWiFiList);//根据信号强度对筛选后的列表进行排序
		return AccessableWiFiList;
	}

	public List<WiFiProtalInfo> ScanWiFi(){
		List<ScanResult> originalWiFiInfo = this.mWifiManager.getScanResults(); //在构造函数中已经获取了wifimanager对象，可以直接用
		List<WiFiProtalInfo> firstStepWiFiInfo = new ArrayList<WiFiProtalInfo>();
		for(int index = 0; index < originalWiFiInfo.size(); index++){
			String strSsid = originalWiFiInfo.get(index).SSID;
			String strBssid = originalWiFiInfo.get(index).BSSID;
			String strCapabilities = originalWiFiInfo.get(index).capabilities;
			int strengthLevel = originalWiFiInfo.get(index).level;

			WiFiProtalInfo WProtalenum = new WiFiProtalInfo(strSsid, strengthLevel);
			firstStepWiFiInfo.add(WProtalenum);

		}
		return firstStepWiFiInfo;
	}

	public List<WiFiProtalInfo> FilterWiFiByAccessableSSID(List<WiFiProtalInfo> firstStepWiFiList, List<WiFiProtalInfo> DownloadedAccessableWiFiList){
		//从第一步加工获得的列表里逐一选出，查看ssid是否在用户可用的ssid内，不满足的剔除出队列
		for(int index = 0; index < firstStepWiFiList.size(); index++){
			boolean isInDownloadedList = false;
			String ssid1 = firstStepWiFiList.get(index).getSsid();
			for(int j = 0; j < DownloadedAccessableWiFiList.size(); j++){
				if(ssid1.equals(DownloadedAccessableWiFiList.get(j).getSsid())){
					isInDownloadedList = true;

					firstStepWiFiList.get(index).setWiFiPassword(DownloadedAccessableWiFiList.get(j).getWiFiPassword());
					firstStepWiFiList.get(index).setSecret(DownloadedAccessableWiFiList.get(j).getSecret());

					break;
				}
			}
			if(!isInDownloadedList){
				firstStepWiFiList.remove(index);
				index--;
			}
			/*if(DownloadedAccessableWiFiList.indexOf(firstStepWiFiList.get(index).getSsid()) == -1){
				firstStepWiFiList.remove(index);
			}*/
		}

		return firstStepWiFiList;
	}

	public List<WiFiProtalInfo> SortWiFiBySignalStrenth(List<WiFiProtalInfo> SecondStepWiFiList){

		//根据信号强度进行排序
		Collections.sort(SecondStepWiFiList, new Comparator<WiFiProtalInfo>() {
			@Override
			public int compare(WiFiProtalInfo lhs, WiFiProtalInfo rhs) {
				if(lhs.getSignalStrenthLevel() < rhs.getSignalStrenthLevel()){
					return 1;
				}
				else {
					return -1;
				}
			}
		});

		return SecondStepWiFiList;
	}

//	public void connectWifi(final long uptime, final OnResultCallBack callback) {
//		new Thread() {
//			@Override
//			public void run() {
//				mController.scan();
//				config = mController.CreateWifiInfo(
//						SharedPrefs.getInstance(getApplicationContext())
//								.getSSID(),
//						SharedPrefs.getInstance(getApplicationContext())
//								.getPassWord(),
//						SharedPrefs.getInstance(getApplicationContext())
//								.getType());
//				netId = mController.addNetWork(config);
//				long t = 0, sleep = uptime < 500 ? uptime : 500;
//				String check = SharedPrefs.getInstance(getApplicationContext())
//						.getSSID();
//				while (true) {
//					String ssid = mController.getSSID();
//					if (!TextUtils.isEmpty(ssid) && !ssid.contains("\"")) {
//						ssid = "\"" + ssid + "\"";
//					}
//					if (check.equals(ssid))
//						break;
//					if (t > uptime) {
//						mController.disconnectNetWork(netId);
//						netId = -1;
//						mWState = WifiState.WIFI_UNCONNECTED;
//						break;
//					}
//					try {
//						Thread.sleep(sleep * 2);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					t += sleep;
//				}
//				try {
//					Thread.sleep(sleep);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (netId != -1)
//					mWState = WifiState.WIFI_CONNECTED;
//				mHandler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						callback.result(netId != -1, "");
//					}
//				});
//			};
//		}.start();
//	}

}
