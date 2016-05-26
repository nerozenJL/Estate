package com.uestc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.Person;
import com.uestc.domain.Secret;
import com.uestc.utils.WifiSocket;
import com.uestc.utils.RuntimeCmd;
import com.uestc.wifi.WifiAdmin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 这是版本号低于18的低版本手机，wifi开门的activity
 */
public class LowOpenActivity extends ActionBarActivity implements View.OnClickListener{

    private ImageButton lowBack,refresh,lowOpen;
    private TextView lowStatus;
    private Bundle mBundle;
    private int category;
    private String check_ssid = "";
    private List<Secret> secrets;
    private WifiManager mWifiManager;
    private WifiAdmin mWifiAdmin;
    private List<ScanResult> scanresult;
    private String phone = "";
    private String password = "";
    private String secret = "";
    private String ssid = "";
    public static int status = 0;//0：没有操作，1：成功；3：超时；4:按钮变灰
    private boolean BACKSTACK_STATUS = true;
//    public static AlertDialog dialog, openDialog;
    private WifiSocket mwifisocket;
    public static final int NO_MANAGE = 0;
    public static final int MANAGE_SUCCESS = 1;
//    private TextView opendialog_title;
    private Timer mTimer;
    private Task task;
    private boolean reConnect = false;
    private boolean reScan = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_low_open);
        ActivityCollector.addActivity(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        category = bundle.getInt("category");
        phone= Person.getPhone();
        if (category == 0){
            check_ssid ="YCWY_Y";
        }else if (category == 1){
            check_ssid ="YCWY_D";
        }else if (category == 2){
            check_ssid ="YCWY_C";
        }

        initView();

        getSecret();

        myWifiExecute();

    }

    //尝试查找并连接Wifi
    private void myWifiExecute(){

        Log.e("myWifiExecute", "myWifiExecuting");

        mWifiAdmin = new WifiAdmin(this) {

            @Override
            public void myUnregisterReceiver(BroadcastReceiver receiver) {
                getApplicationContext().unregisterReceiver(receiver);
            }

            @Override
            public Intent myRegisterReceiver(
                    BroadcastReceiver receiver, IntentFilter filter) {
                getApplicationContext().registerReceiver(receiver, filter);
                return null;
            }

            @Override
            public void onNotifyWifiConnected() {
                Log.e("end", "" + System.currentTimeMillis());
                new DhcpAsynctask().execute();

            }

            @Override
            public void onNotifyWifiConnectFailed() {
                // dialog3.cancel();
                Log.i("SHIBAI", "5555555555555555555555");
                handler.sendEmptyMessageAtTime(2, 1);
            }

            @Override
            public void onNotifyWifiConnecting() {
                // showDialog4();
            }

            @Override
            public void onTimeOut() {
                handler.sendEmptyMessageAtTime(6,1);
            }
        };

        mWifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);

        if (mWifiAdmin.getWiFiStatus()) {
            // mWifiManager.disconnect();

            mWifiAdmin.startScan();

            scanresult = mWifiAdmin.getWifiList();

            ssid = "";

            for (int i = 0; i < scanresult.size(); i++) {
                if (scanresult.get(i).SSID.indexOf(check_ssid) >= 0) {
                    ssid = scanresult.get(i).SSID;
                    break;
                }
            }

            if (ssid.equals("")) {
                lowStatus.setText("没有检测到设备\n\n点击按钮重新扫描");
                setLowOpenTrue();
                reConnect = true;
                reScan = true;
            } else {

                boolean isHave = false;
                for (int i=0;i<secrets.size();i++){
                    if (secrets.get(i).getSymbol().equals(ssid)){
                        password = secrets.get(i).getSsidPassword();
                        secret = secrets.get(i).getSecret();
                        task = new Task();
                        task.execute();
//                        handler.sendEmptyMessageAtTime(1, 1);
                        lowStatus.setText("正在连接设备...");
                        isHave = true;
                        break;
                    }
                }
                if (!isHave){
                    lowStatus.setText("检测到设备，无权限开锁\n" +
                            "\n点击按钮重新扫描");
                    reConnect = true;
                    setLowOpenTrue();
                }


            }

        } else {

            handler.sendEmptyMessageAtTime(0, 1);
            new MyThread1().start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        mWifiAdmin.addErrorNetWork(ssid, password + "a", WifiAdmin.TYPE_WPA);
        BACKSTACK_STATUS = false;
    }

    class WaitTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            myWifiExecute();
        }
    }
    private void initView() {
        secrets = new ArrayList<Secret>();
        lowBack = (ImageButton) findViewById(R.id.ib_back_low_open);
        lowBack.setOnClickListener(this);
        refresh = (ImageButton) findViewById(R.id.ib_refresh_low_open);
        refresh.setOnClickListener(this);
        lowOpen = (ImageButton) findViewById(R.id.btn_opendoorr_low_open);
        lowOpen.setOnClickListener(this);
        lowStatus = (TextView) findViewById(R.id.tv_status_low_open);
        mBundle = getIntent().getExtras();
        category = mBundle.getInt("category");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_back_low_open:
                finish();
                break;
            case R.id.ib_refresh_low_open:
                if (status != 4) {
                    if (!ssid.equals("")) {
                        lowStatus.setText("正在检查设备...");
                        new DhcpAsynctask().execute();
                    } else {
                        lowStatus.setText("没有检测到设备\n" +
                                "\n点击按钮重新扫描");
                        setLowOpenTrue();
                        reConnect = true;
                        reScan = true;
                    }
                }
                break;
            case R.id.btn_opendoorr_low_open:

                if (reConnect){
                    reConnect = false;
                    setLowOpenFalse();
                    lowStatus.setText("正在检查设备...");
                    if (reScan){
                        new WaitTask().execute();
                    }else{
                        myWifiExecute();
                    }
                }else{
                    MyThread myThread = new MyThread();
                    myThread.start();
                    mHandler.sendEmptyMessage(100);
                    startTimer2();
                    lowStatus.setText("正在开锁...");
                    setLowOpenFalse();
                }


                break;
            default:
                break;
        }
    }


    //连接WiFi
    class Task extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
//			mWifiManager.disconnect();
            mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(ssid, password,
                    WifiAdmin.TYPE_WPA));

            Log.e("wifi1", ssid);
            Log.e("wifi1",password);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

    }


    //WiFi连接好后 socket通信
    class MyThread extends Thread {

        @Override
        public void run() {
            try {
                mwifisocket = new WifiSocket(phone,password,secret);
                mwifisocket.connect();
            } catch (Exception e) {
                Log.i("tag", "socket连接失败");
            }

        }

    }

    //打开WiFi
    class MyThread1 extends Thread {

        @Override
        public void run() {
            super.run();
            Log.e("WIFIWIFI", "OPEN" + System.currentTimeMillis());
            mWifiAdmin.openWifi();
            Log.e("WIFIWIFI", "OPEN OKOKOK!" + System.currentTimeMillis());
        }
    }


    /**
     * 计时器
     */
    private class MyTimer extends TimerTask {
        @Override
        public void run() {
            Log.e("status_test1",""+status);
            status = 3;
            Log.e("status_test2", "" + status);
        }

    }
    //10秒的计时
    private void startTimer() {
        if (mTimer != null) {
            stopTimer();
        }
        mTimer = new Timer(true);
        mTimer.schedule(new MyTimer(), 10 * 1000);
        Log.e("startTimer", "startTimer--------》");
    }
    //5秒的计时
    private void startTimer1(){
        if (mTimer != null) {
            stopTimer();
        }

        mTimer = new Timer(true);
        mTimer.schedule(new MyTimer(), 5 * 1000);
    }
    private void startTimer2(){
        if (mTimer != null) {
            stopTimer();
        }

        mTimer = new Timer(true);
        mTimer.schedule(new MyTimer(), 8 * 1000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class DhcpAsynctask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {
            int m = RuntimeCmd.isWifiRight();
            return m;
        }

//        @Override
//        protected Integer onPostExecute(Integer result) {
////            if (openDialog.isShowing()) {
////                openDialog.cancel();
////            }
//
//
//            lowStatus.setText("设备连接成功，点击开门");
////            showOpenWifiDialog();
//
//        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e("wifi1","dhcp-------->>>>>" +integer);
            if (integer == 0){
                Log.e("wifi1", "dhcp-------->>>>>");

                lowStatus.setText("未连接设备，点击刷新");
                setLowOpenFalse();
                reConnect = false;
                status = 0;
            }else if(integer == 1){
                Log.e("wifi1","dhcp  111111-------->>>>>");
                WifiInfo wifiInfo =mWifiManager.getConnectionInfo();
                Log.e("wifi1",wifiInfo.getSSID());
                Log.e("wifi1",ssid);
                if (wifiInfo.getSSID().equals(ssid)||wifiInfo.getSSID().equals("\""+ssid+"\"")){
                    Log.e("wifi1", "yes yes yes yes ");

                    lowStatus.setText("设备连接成功，点击开门");
                    setLowOpenTrue();
                    reConnect = false;
                }else{

                    lowStatus.setText("设备连接失败\n" +
                            "\n点击按钮重连");
                    setLowOpenTrue();
                    reConnect = true;
                }

            }
        }
    }

    /**
     * 这是WiFi连接的handler
     */

    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    // 弹出等待WiFi开启的对话框
//                    if (openDialog == null) {
//                        showOpenWifiDialog();
//                    } else {
//                        if (openDialog.isShowing()) {
//
//                        } else {
//                            showOpenWifiDialog();
//                        }
//                    }
                    lowStatus.setText("正在打开wifi...");
                    handler.sendEmptyMessageAtTime(4, 1);

                    break;
                case 1:
                    break;
                case 2:
                    // 这是WiFi连接失败的回调
//                    if (openDialog != null && openDialog.isShowing()) {
//                        openDialog.cancel();
//                    }
                    lowStatus.setText("连接网络设备失败\n" +
                            "\n请点击按钮重连");
                    setLowOpenTrue();
                    reConnect = true;
                    break;
                case 3:
                    //等待WiFi扫描，返回结果部位空
//                    if (openDialog == null) {
//                        showOpenWifiDialog();
//                        opendialog_title.setText("正在连接网络...");
//                    } else {
//                        if (openDialog.isShowing()) {
//                            opendialog_title.setText("正在连接网络...");
//                        } else {
//                            showOpenWifiDialog();
//                            opendialog_title.setText("正在连接网络...");
//                        }
//                    }
                    lowStatus.setText("正在连接设备");
                    new waitForSsid().execute();

                    break;
                case 4:
                    //等待wifi开启，变成可用状态
                    new DialogAsynctask().execute();
                    break;
                case 5:
                    //从WiFi列表中找出符合命名规则的ssid
                    ssid = "";
                    if (scanresult != null) {
                        Log.e("scanresult", scanresult.size() + "");
                        for (int i = 0; i < scanresult.size(); i++) {
                            if (scanresult.get(i).SSID.indexOf(check_ssid) >= 0) {
                                ssid = scanresult.get(i).SSID;
                                break;
                            }
                        }

                        if (ssid.equals("")) {

                            lowStatus.setText("没有检测到设备\n" +
                                    "\n点击按钮重新扫描");
                            reScan = true;
                            reConnect = true;
                            setLowOpenTrue();
//                            if (openDialog != null && openDialog.isShowing()) {
//                                openDialog.cancel();
//                            }
                        } else {
                            boolean isHave = false;
                            for (int i=0;i<secrets.size();i++){
                                if (secrets.get(i).getSymbol().equals(ssid)){
                                    password = secrets.get(i).getSsidPassword();
                                    secret = secrets.get(i).getSecret();
                                    task = new Task();
                                    task.execute();
                                    isHave = true;
                                    break;
                                }
                            }
                            if (!isHave){
                                lowStatus.setText("检测到设备，无权限开锁\n" +
                                        "\n请点击按钮重新连接");
                                reConnect = true;
                                setLowOpenTrue();
                            }
                        }
                    } else {
                        lowStatus.setText("没有检测到设备\n" +
                                "\n点击按钮重新扫描");
                        setLowOpenTrue();
                        reScan = true;
                        reConnect = true;
                    }
                    break;

                case 6:
                    lowStatus.setText("连接网络设备超时\n" +
                            "\n请点击按钮重连");
                    setLowOpenTrue();
                    reConnect = true;
                    break;
                default:
                    break;
            }
        };
    };


    /**
     * 这是WiFi开门反馈的handler
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            if (status == 4) {
                mHandler.sendEmptyMessage(100);
            } else if (status == MANAGE_SUCCESS) {
                lowStatus.setText("开锁成功");
                status = NO_MANAGE;
                stopTimer();
                setLowOpenFalse();
                lowStatus.setText("门已开启");
                mhandler4.sendEmptyMessage(0);
                startTimer1();

            } else if (status == 2){
                lowStatus.setText("开锁失败");
                status = NO_MANAGE;
                stopTimer();
            }else{
                lowStatus.setText("开锁超时");
                status = NO_MANAGE;
                stopTimer();
            }

        };
    };

    //这是开锁成功后按钮变灰
    private Handler mhandler4 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (status == 4) {
                mhandler4.sendEmptyMessage(0);

            }else if (status == 3&& BACKSTACK_STATUS ){
                stopTimer();
                setLowOpenTrue();
                Log.e("HighVersionFragment", "button true set start");

                Log.e("HighVersionFragment", "button true set 1/2");

                Log.e("HighVersionFragment", "button true set finish");
                status = 0;
                lowStatus.setText("设备已连接，点击开门");
                setLowOpenTrue();
                reConnect = false;
            }
        }
    };

    //等待wifi开启，到可用状态
    class DialogAsynctask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                    break;

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            handler.sendEmptyMessageAtTime(3, 100);
        }
    }

    /**
     * wifi扫描的task，目的是让WiFi列表不为空
     */
    class waitForSsid extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                mWifiAdmin.startScan();
                scanresult = new ArrayList<ScanResult>();
                scanresult = mWifiAdmin.getWifiList();
                if (scanresult != null && scanresult.size() != 0) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            handler.sendEmptyMessageAtTime(5, 1);


        }
    }

    private void setLowOpenTrue(){
        lowOpen.setImageDrawable(getResources().getDrawable(R.drawable.low_open_true));
        lowOpen.setClickable(true);
    }

    private void setLowOpenFalse(){
        lowOpen.setImageDrawable(getResources().getDrawable(R.drawable.low_open_false));
        lowOpen.setClickable(false);
        status = 4;

    }
    private void getSecret(){
        SharedPreferences sp = getSharedPreferences("secret", MODE_PRIVATE);
        String string = sp.getString("jsonString", "");
        try {
            JSONObject object = new JSONObject(string);
            String isNull = object.getString("jsonString");
            if (isNull.equals("") || isNull.equals("null")) {
                lowStatus.setText("没有检测到设备\n" +
                        "\n点击按钮重新扫描");
                reScan = true;
                reConnect = true;
            } else {
                JSONArray jsonArray = object.getJSONArray("jsonString");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject myObj = jsonArray.getJSONObject(i);
                    Secret tempSecret = new Secret();
                    tempSecret.setControlType(myObj.getInt("controlType"));
                    tempSecret.setSymbol(myObj.getString("symbol"));
                    tempSecret.setSecret(myObj.getString("secret"));
                    tempSecret.setSsidPassword(myObj.getString("password"));
                    secrets.add(tempSecret);
                }
            }
            }catch(JSONException e){
                e.printStackTrace();
            }

    }
}
