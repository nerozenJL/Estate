package com.uestc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.uestc.domain.Person;
import com.uestc.domain.Session;
import com.uestc.domain.WiFiProtalInfo;
import com.uestc.ui.activity.R;
import com.uestc.utils.AutoDisconnectWiFiTask;
import com.uestc.utils.WifiSocket;
import com.uestc.utils.TimerAT;

/**
 * Created by Mengcz on 2016/3/29.
 */
public class WifiUnlocker extends Service {

    //开锁过程中的各种状态代号
    public static final int UNLOCKE_BLOCKED=0;  //已经处于开锁状态，无法开锁
    public static final int ENABLE_WIFI_BY_PRPGRAME=1;  //由本程序打开的Wifi
    public static final int ENABLE_WIFI_BY_USER=2;  //由用户或其他程序打开的WIFI
    public static final int ENABLE_WIFI_FAILED_BY_LACK_PERMISSION=3;//缺少权限无法打开
    public static final int ENABLE_WIFI_OUTTIME=4;
    public static final int SCAN_WITH_NO_RESULT=5;
    public static final int SCAN_GET_RESULT=6;
    public static final int CONNECT_WIFI_START=7;
    public static final int CONNECT_WIFI_SUCCESS=8;
    public static final int CONNECT_WIFI_FAILED=9;
    public static final int SOCKET_COMMUNATE_START=10;
    public static final int SOCKET_COMMUNATE_SUCCESS=11;
    public static final int SOCKET_COMMUNATE_FAILED=12;
    public static final int CONNECT_WIFI_OUTTIME=13;
    public static final int WIFI_OPENING=17;
    public static final int LOCK_RESET_FINISHED=18;
    //开锁方式
    public static final int UNLOCKDOOR=14;
    public static final int OPEN_SHAKE_FUNCTION=15;
    public static final int CLOSE_SHAKE_FUNCTION=16;
    //与UI通信的标识动作
    public static final String WIFIUNLOCK_BROADCAST_ACTION="WIFIUNLOCK_BROADCAST_ACTION";
    //接收来自UI的Intent的EXTRA的key
    public static final String WIFIUNLOCK_TYPE="WIFIUNLOCK_TYPE";
    //WiFi连接相关对象
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private static int socket_connect_result;
    ConnectivityManager connManager;
    //WiFi连接所需信息
    private String password;
    private String SSID;
    private String secret;
    //WiFi加密方式
    public static final int TYPE_NO_PASSWD = 0x11;
    public static final int TYPE_WEP = 0x12;
    public static final int TYPE_WPA = 0x13;
    //扫描WiFi所获WiFi列表
    private static List<WiFiProtalInfo> currentScanResult;
    //开锁流程结束标识
    private static boolean isUnlockfinished=true;
    //定时器
    private static final int TIMERFORLOCKTORESET_MAXTIME=5000;
    private static final int TIMERFORLOCKTODISCONNECT_MAXTIME=20000;
    private static final int TIMER_SPACING=200;
    private static int timercounter=0;
    private TimerTask timerTaskForlocktoreset,timerTaskFordisconnect;
    private Timer mtimer;

    private static Timer mTimerToDisconnect = new Timer();
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent, flags, startId);
        wifiManager=(WifiManager)getSystemService(this.WIFI_SERVICE);
        wifiInfo=wifiManager.getConnectionInfo();
        int opentype=-1;
        socket_connect_result=-1;
        opentype=intent.getIntExtra(WIFIUNLOCK_TYPE, opentype);
        if (opentype==UNLOCKDOOR) {
            new unlockTask().execute();
        }else if (opentype== WifiUnlocker.OPEN_SHAKE_FUNCTION){
            Intent openShakeIntent=new Intent(WifiUnlocker.this,ShakeSensorService.class);
            openShakeIntent.putExtra(ShakeSensorService.UNLOCK_TYPE,ShakeSensorService.OPEN_BY_WIFI);
            openShakeIntent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
            startService(openShakeIntent);
        }else if (opentype==WifiUnlocker.CLOSE_SHAKE_FUNCTION) {
            while (!isUnlockfinished);
            Intent closeShakeIntent=new Intent(WifiUnlocker.this,ShakeSensorService.class);
            closeShakeIntent.putExtra(WIFIUNLOCK_TYPE,opentype);
            startService(closeShakeIntent);
        }
        return START_STICKY;
    }
    //开锁子线程
    class unlockTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... arg0) {
            if (!isUnlockfinished){
                Intent intent=new Intent();
                intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
                intent.putExtra("ACTION_TYPE", UNLOCKE_BLOCKED);
                sendBroadcast(intent);
                return UNLOCKE_BLOCKED;
            }
            isUnlockfinished=false;
            timerTaskForlocktoreset=new TimerTask() {
                @Override
                public void run() {
                if (timercounter>=TIMERFORLOCKTORESET_MAXTIME/TIMER_SPACING){
                    stopTimer();
                    Intent intent = new Intent();
                    intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
                    intent.putExtra("ACTION_TYPE",LOCK_RESET_FINISHED);
                    sendBroadcast(intent);
                    timercounter=0;
                }else
                    timercounter++;
                }
            };
            timerTaskFordisconnect=new TimerTask() {
                @Override
                public void run() {
                    if (timercounter>=TIMERFORLOCKTODISCONNECT_MAXTIME/TIMER_SPACING){
                        stopTimer();
                        if (wifiManager!=null){
                            wifiManager.disconnect();
                        }
                        timercounter=0;
                    }else
                        timercounter++;
                }
            };
            stopTimer();
            ClickToUnlockWithWifi();
            return 1;
        }

        protected void onPostExecute(Integer result) {
            if (result!=UNLOCKE_BLOCKED)
                isUnlockfinished=true;
            super.onPostExecute(result);
        }
    }
    //点击开锁
    public void ClickToUnlockWithWifi(){

        if(AutoDisconnectWiFiTask.isRunning()) {
            //mTimerToDisconnect.cancel();
            //mTimerToDisconnect = new Timer();
            AutoDisconnectWiFiTask.getInstance(20, getApplicationContext(), mTimerToDisconnect); //重置定时器
            //mTimerToDisconnect.schedule(AutoDisconnectWiFiTask.getInstance(20, getApplicationContext()), 0, 1000);
        }

        int enablewifiResult=EnableWifi(5000);
        Intent openWifiintent=new Intent();
        openWifiintent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
        if (enablewifiResult==ENABLE_WIFI_BY_USER||enablewifiResult==ENABLE_WIFI_BY_PRPGRAME){
            openWifiintent.putExtra("ACTIION_TYPE",enablewifiResult);
            sendBroadcast(openWifiintent);
        }else {
            openWifiintent.putExtra("ACTION_TYPE",enablewifiResult);
            sendBroadcast(openWifiintent);
            return;
        }
        if (enablewifiResult==ENABLE_WIFI_BY_PRPGRAME){
            currentScanResult = this.ReFreshWiFi(5000);
        }
        currentScanResult = this.ReFreshWiFi(5000);
        Intent scanNothingIntent=new Intent();
        if (currentScanResult.size()==0){   //确保当前有符合要求的设备
            //Toast.makeText(WifiUnlocker.this, "未能扫描到锁，请确认锁在附近", Toast.LENGTH_SHORT).show();
            scanNothingIntent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            scanNothingIntent.putExtra("ACTION_TYPE", SCAN_WITH_NO_RESULT);
            sendBroadcast(scanNothingIntent);
            return;
        }
        scanNothingIntent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
        scanNothingIntent.putExtra("ACTION_TYPE", SCAN_GET_RESULT);
        scanNothingIntent.putExtra("VALUE", currentScanResult.get(0).getSsid());
        sendBroadcast(scanNothingIntent);
        String bestcurrentScanSSID="\""+currentScanResult.get(0).getSsid()+"\"";
        WiFiProtalInfo bestwiFiProtalInfo=currentScanResult.get(0);
        List<WiFiProtalInfo> wiFiProtalInfoList=Session.getAccessableWiFiSsidList();
        for (int i=0;i<wiFiProtalInfoList.size();i++){  //获取用户当前的权限列表
            if (wiFiProtalInfoList.get(i).getSsid().equals(bestwiFiProtalInfo.getSsid())){
                password=wiFiProtalInfoList.get(i).getWiFiPassword();
                SSID=wiFiProtalInfoList.get(i).getSsid();
                secret=wiFiProtalInfoList.get(i).getSecret();
                break;
            }
        }
        if (!bestcurrentScanSSID.equals(this.getCurrentwifiSSID())){  //开始连接wifi
            Intent startConnectIntent = new Intent();
            startConnectIntent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            startConnectIntent.putExtra("ACTION_TYPE", CONNECT_WIFI_START);
            sendBroadcast(startConnectIntent);
            int ConnectResult=ConnectWiFi(SSID,password);
            Intent connectResultintent=new Intent();
            connectResultintent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            if (ConnectResult==CONNECT_WIFI_FAILED){
                connectResultintent.putExtra("ACTION_TYPE", CONNECT_WIFI_FAILED);
                sendBroadcast(connectResultintent);
                //Toast.makeText(WifiUnlocker.this, "连接失败", Toast.LENGTH_SHORT).show();
                return;
            }else {
                connectResultintent.putExtra("ACTION_TYPE", CONNECT_WIFI_SUCCESS);
                sendBroadcast(connectResultintent);
                //Toast.makeText(WifiUnlocker.this, "连接成功", Toast.LENGTH_SHORT).show();
            }
        }
        try {   //进行Socket通信
            //while (wifiStatus!=wifi_connect_success);
            Intent communicatestartIntent = new Intent();
            communicatestartIntent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            communicatestartIntent.putExtra("ACTION_TYPE",SOCKET_COMMUNATE_START);
            sendBroadcast(communicatestartIntent);
            int CommunicateResult=CommunicateWithWiFi(Person.getPhone(),password,secret);
            Intent intent=new Intent();
            intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            if (CommunicateResult==SOCKET_COMMUNATE_SUCCESS){
                Vibrator vibrator = (Vibrator) getSystemService(WifiUnlocker.this.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                intent.putExtra("ACTION_TYPE",SOCKET_COMMUNATE_SUCCESS);
                sendBroadcast(intent);
                //Toast.makeText(WifiUnlocker.this, "开锁成功", Toast.LENGTH_SHORT).show();
            }else {
                intent.putExtra("ACTION_TYPE",SOCKET_COMMUNATE_FAILED);
                sendBroadcast(intent);
                //Toast.makeText(WifiUnlocker.this, "开锁失败", Toast.LENGTH_SHORT).show();
            }
//            TimerForlocktoReset timerForlocktoReset=new TimerForlocktoReset();
//            timerForlocktoReset.start();
            starttimer();
            if(AutoDisconnectWiFiTask.isRunning()) {
                //上次计时尚未结束
            } else {
                AutoDisconnectWiFiTask disconnectTask = AutoDisconnectWiFiTask.getInstance(20, getApplicationContext(), mTimerToDisconnect);
                mTimerToDisconnect.schedule(disconnectTask, 0, 1000);
            }


            /*TimerAT timerAT= TimerAT.getInstance(getApplicationContext(), 20);    //开始计时20s，之后若无重置情况则断开连接
            if(!timerAT.isExcuting()){
                timerAT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }*/
        } catch (Exception e) {
            Log.i("tag", "socket连接失败");
        }
    }
    //检测并开启Wifi,需输入等待时间上限（不得低于500毫秒）
    private int EnableWifi(int wait_time){
        if (!wifiManager.isWifiEnabled()){
            Intent intent=new Intent();
            intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            intent.putExtra("ACTION_TYPE", WIFI_OPENING);
            sendBroadcast(intent);
            wifiManager.setWifiEnabled(true);
            for (int i=0;i<wait_time;i+=500){
                try {
                    Thread.sleep(500);
                    if (wifiManager.isWifiEnabled())
                        return ENABLE_WIFI_BY_PRPGRAME;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!isWiFiPermissionGained(WifiUnlocker.this))
                return ENABLE_WIFI_OUTTIME;
            else
                return ENABLE_WIFI_FAILED_BY_LACK_PERMISSION;
        }
        return ENABLE_WIFI_BY_USER;
    }
    //用户是否给与程序Wifi权限
    public boolean isWiFiPermissionGained(Context context){
        PackageManager packageManager = context.getPackageManager();
        int hasPermission = packageManager.checkPermission("android.permission.CHANGE_WIFI_STATE", "com.example.mengcz.wifiunlockdemo");
        if(hasPermission == packageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }
    //连接WiFi
    private int ConnectWiFi(String SSID,String password){
        this.simpleaddNetwork(this.createWifiInfo(SSID, password,   //连接wifi
                this.TYPE_WPA));
        int connectResult=isConnectionSuccessful();
        return connectResult;
    }
    //建立Socket并通信
    private int CommunicateWithWiFi(String phone,String password,String secret){
        //int socket_connect_result=2;
        ThreadrForConnect threadrForConnect=new ThreadrForConnect(phone);
        threadrForConnect.start();
        for (int i=0;i<8000;i+=100){
            if (socket_connect_result!=-1){
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (socket_connect_result==-1){
            socket_connect_result=SOCKET_COMMUNATE_FAILED;
        }
        return socket_connect_result;
    }
    //刷新WiFi列表,需输入等待时间上限（不得低于500毫秒）
    private List<WiFiProtalInfo> ReFreshWiFi(int wait_time){
        List<WiFiProtalInfo> AccessableWiFiList = null;
        for (int i=0;i<wait_time;i+=500){
            AccessableWiFiList = ScanWiFi();//扫描可用的WiFi
            AccessableWiFiList = FilterWiFiByAccessableSSID(AccessableWiFiList, Session.getAccessableWiFiSsidList());//根据下载好的SSID列表进行筛选
            AccessableWiFiList = SortWiFiBySignalStrenth(AccessableWiFiList);//根据信号强度对筛选后的列表进行排序
            if (AccessableWiFiList.size()!=0)
                return AccessableWiFiList;
            wifiManager.startScan();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return AccessableWiFiList;
    }
    private List<WiFiProtalInfo> ReFreshWiFi(){
        List<WiFiProtalInfo> AccessableWiFiList = null;
        AccessableWiFiList = ScanWiFi();//扫描可用的WiFi
        AccessableWiFiList = FilterWiFiByAccessableSSID(AccessableWiFiList, Session.getAccessableWiFiSsidList());//根据下载好的SSID列表进行筛选
        AccessableWiFiList = SortWiFiBySignalStrenth(AccessableWiFiList);//根据信号强度对筛选后的列表进行排序
        return AccessableWiFiList;
    }
    //扫描并过滤WiFi冗余信息
    public List<WiFiProtalInfo> ScanWiFi(){
        List<ScanResult> originalWiFiInfo = this.wifiManager.getScanResults(); //在构造函数中已经获取了wifimanager对象，可以直接用
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
    //过滤WiFi列表
    public List<WiFiProtalInfo> FilterWiFiByAccessableSSID(List<WiFiProtalInfo> firstStepWiFiList, List<WiFiProtalInfo> DownloadedAccessableWiFiList){
        //从第一步加工获得的列表里逐一选出，查看ssid是否在用户可用的ssid内，不满足的剔除出队列
        for(int index = 0; index < firstStepWiFiList.size(); index++){
            boolean isInDownloadedList = false;
            String ssid1 = firstStepWiFiList.get(index).getSsid();
            System.out.println(ssid1);
            for(int j = 0; j < DownloadedAccessableWiFiList.size(); j++){
                if(ssid1.equals(DownloadedAccessableWiFiList.get(j).getSsid())){
                    isInDownloadedList = true;
                    Log.i("tag", "socket连接失败");
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
    //排序WiFi列表
    public List<WiFiProtalInfo> SortWiFiBySignalStrenth(List<WiFiProtalInfo> SecondStepWiFiList){

        //根据信号强度进行排序
        Collections.sort(SecondStepWiFiList, new Comparator<WiFiProtalInfo>() {
            @Override
            public int compare(WiFiProtalInfo lhs, WiFiProtalInfo rhs) {
                if (lhs.getSignalStrenthLevel() < rhs.getSignalStrenthLevel()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return SecondStepWiFiList;
    }
    //根据加密方式配置指定Wifi
    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
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
    //判断指定WiFi是否已经被配置过
    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
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
    //链接已经配置好的WiFi
    public void simpleaddNetwork(WifiConfiguration wcg) {
        int wcgID = wifiManager.addNetwork(wcg);
        boolean b =  wifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }
    //链接等待函数，等待直到连接成功，若等待时间超过阈值则判定超时
    public int isConnectionSuccessful(){
        int res = WifiUnlocker.CONNECT_WIFI_START;
        for(int time = 0; time < 10; time++){
            ConnectivityManager connManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                res = WifiUnlocker.CONNECT_WIFI_SUCCESS;
                break;
            }
            else {
                res = WifiUnlocker.CONNECT_WIFI_FAILED;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    res = WifiUnlocker.CONNECT_WIFI_FAILED;
                }
            }
        }
        return res;
    }
    //获取当前已经连接的WiFi信息
    public String getCurrentwifiSSID(){
        if (wifiManager.getConnectionInfo()==null){
            return "NULL";
        }else {
            String ssid=wifiManager.getConnectionInfo().getSSID();
            return ssid;
        }
        //return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }
    //新建定时器，用于锁复位
    private class TimerForlocktoReset extends Thread{
        @Override
        public void run(){
            for (int i=0;i<2000;i+=1){
                if (!isUnlockfinished){
//                    Intent intent = new Intent();
//                    intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
//                    intent.putExtra("ACTION_TYPE", WifiUnlocker.LOCK_RESET_FINISHED);
//                    sendBroadcast(intent);
                    return;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent();
            intent.setAction(WIFIUNLOCK_BROADCAST_ACTION);
            intent.putExtra("ACTION_TYPE",WifiUnlocker.LOCK_RESET_FINISHED);
            sendBroadcast(intent);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //在新线程中执行具体的连接操作
    private class ThreadrForConnect extends Thread{
        String phone;
        public ThreadrForConnect(String phone){
            this.phone=phone;
        }
        @Override
        public void run(){
            try {
                WifiSocket mwifisocket = new WifiSocket(phone,password,secret);
                socket_connect_result=mwifisocket.connect();
            } catch (Exception e) {
                Log.i("tag", "socket连接失败");
            }
        }
    }

    public static boolean isUnlockeFinished(){
        return isUnlockfinished;
    }

    private void stopTimer(){
        if(mtimer != null) {
            mtimer.cancel();
            timercounter=0;
        }
    }

    private void starttimer(){
        if (mtimer!=null)
            stopTimer();
        mtimer=new Timer(true);
        mtimer.schedule(timerTaskForlocktoreset,0,TIMER_SPACING);
    }
}
/*
* 废弃代码
* if (currentScanResult != null){ //非第一次开锁
            if (currentScanResult.size()>0){ //若第一次开锁扫描到了符合要求的锁
                bestcurrentScanSSID=
                String CurrentWifiSSID=this.getCurrentwifiSSID();
                if (bestcurrentScanSSID.equals(this.getCurrentwifiSSID())) {  //且此符合要求的锁当前正处于连接状态
                    currentScanResult = this.ReFreshWiFi();
                    bestcurrentScanSSID="\""+currentScanResult.get(0).getSsid()+"\"";
                }
            }
        }else { //第一次开锁
            currentScanResult = this.ReFreshWiFi();
            if (currentScanResult.size()!=0)
                bestcurrentScanSSID="\""+currentScanResult.get(0).getSsid()+"\"";
        }
* */