package com.uestc.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jacoblong.myvolleyrequestlib.BasicErrorListener;
import com.example.jacoblong.myvolleyrequestlib.BasicRequest;
import com.example.jacoblong.myvolleyrequestlib.RequestManager;
import com.example.nerozen.casloginprocess.CASLoginProxy;
import com.example.nerozen.casloginprocess.GardenInfoList;
import com.uestc.BroadcastReceivers.AutoConnectWiFiBroadcastReceiver;
import com.uestc.base.MyBitmapFactory;
import com.uestc.constant.Host;
import com.uestc.domain.ADInfo;
import com.uestc.domain.BluetoothProtalInfo;
import com.uestc.domain.GetAnnouncementData;
import com.uestc.domain.Person;
import com.uestc.domain.Secret;
import com.uestc.domain.Session;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.domain.WiFiProtalInfo;
import com.uestc.ui.dialog.ListViewDialog;
import com.uestc.utils.ConfigurationFilesAdapter;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.JsonTools;
import com.uestc.utils.ResponseJsonParser;

import org.apache.http.protocol.RequestExpectContinue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nerozen on 2016/3/16.
 * 1.作为应用启动项，先检查自动登录配置文件是否存有自动登录信息；
 * 2.如果存在，则直接进行登录操作，登录成功跳转到首页
 * 3.如果登录失败或没有自动登录配置，则跳转到登录界面
 *
 * 4.实现CAS登录流程
 * 5.使用volley框架进行网络请求
 */
public class DetectAutoLoginActivity extends Activity {
    private String gardenDBName = null, gardenHostName = null; //用户选中的园区名和数据库名

    private Object volley_count_lock = new Object(); //互斥增加volley响应数
    private int volley_count;//记录volley请求完成的个数，所有请求完成之后，进行主业跳转

    private final String autoLoginConfigFilePath = "/data/data/com.znt.estate/autologinconfig.properties";

    private ImageView appCoverImageView; //过渡界面的背景

    /*后续登陆操作,使用volley并发请求的方法*/
    private void SubsequentLogin() {
        //不如用volley写吧
        //需要getrole，getsecrets，get公告，get商店预览图片，get物管电话
        Map<String, String> header = new HashMap<String, String>();
        header.put("Cookie", Session.seesion);
        volley_count = 0;
        VolleyGetRole(header);
        VolleyGetSecret(header);
        VolleyGetNotify(header);
        VolleyGetStorePreview(header);
        VolleyGetPMphonenumber(header);
    }
    private void VolleyGetRole(Map header) {
        BasicErrorListener errorListener = new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        Response.Listener<String> getRoleListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getBoolean("status")) {
                        String roles = jsonObject.getString("jsonString");
                        int maxRole = 0;
                        for (String roleString : Arrays.asList(roles.split(","))) {
                            int role = Integer.parseInt(roleString);
                            if (role > maxRole)
                                maxRole = role;
                        }
                        Session.role = maxRole;
                        synchronized (volley_count_lock){
                            volley_count++;
                            if(volley_count >= 5) {
                                JumpToHomeActivity();
                            }
                        }
                    }else {
                        Session.role = -1;
                    }
                }catch (JSONException e) {
                }
            }
        };
        RequestManager.getInstance(getApplicationContext())
                .addRequest(new BasicRequest(Request.Method.GET, Host.getRole, getRoleListener, errorListener, header));
    }
    private void VolleyGetSecret(Map header) {
        BasicErrorListener errorListener = new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        Response.Listener<String> getRoleListener = new Response.Listener<String>() {
            int a;
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject getSecretResult = new JSONObject(response);
                    if (getSecretResult.getBoolean("status")) {
                        //返回成功，将所有jsonstring进行解析
                        JSONArray ssidArrayJson = getSecretResult.getJSONArray("jsonString");

                        for (int index = 0; index < ssidArrayJson.length(); index++) {
                            JSONObject singleSsidJsonObj = ssidArrayJson.getJSONObject(index);
                            int type = singleSsidJsonObj.getInt("type");
                            if (type == 2) {
                                String ssid = singleSsidJsonObj.getString("symbol");
                                String secret = singleSsidJsonObj.getString("secret");
                                String password = singleSsidJsonObj.getString("password");
                                WiFiProtalInfo singleWiFiProtalInfo = new WiFiProtalInfo(ssid, password, secret);
                                Session.addMemberToAccessableWiFiSsidList(singleWiFiProtalInfo);
                            } else if (type == 1) {
                                String ssid = singleSsidJsonObj.getString("symbol");
                                String secret = singleSsidJsonObj.getString("secret");
                                BluetoothProtalInfo singleBTProtalInfo = new BluetoothProtalInfo(ssid, secret);
                                Session.addMemberToAccessableBTSsidList(singleBTProtalInfo);
                            }
                        }
                    }
                    synchronized (volley_count_lock){
                        volley_count++;
                        if(volley_count >= 5) {
                            JumpToHomeActivity();
                        }
                    }
                }catch (JSONException e) {

                }
            }
        };
        RequestManager.getInstance(getApplicationContext()).
                addRequest(new BasicRequest(Request.Method.GET, Host.getSecret, getRoleListener, errorListener, header));
    }
    private void VolleyGetNotify(Map header) {
        BasicErrorListener errorListener = new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        Response.Listener<String> getRoleListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String getNotify) {
                if (!getNotify.equals("")) {
                    GetAnnouncementData getAnnouncementData = new GetAnnouncementData();
                    getAnnouncementData = JsonTools.getAnnouncement(DetectAutoLoginActivity.this, "jsonString", getNotify);
                    if (getAnnouncementData.isStatus()) {
                        TempStaticInstanceCollection.announcementList = getAnnouncementData;
                        if(getAnnouncementData == null){
                            TempStaticInstanceCollection.announcementList.setStatus(false);
                        }
                    } else {
                        //Log.v("notify failed", "获取公告失败");
                        TempStaticInstanceCollection.announcementList = new GetAnnouncementData();
                    }
                }
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        RequestManager.getInstance(getApplicationContext()).addRequest(new BasicRequest(Request.Method.GET, Host.getNotice1, getRoleListener, errorListener, header));
    }
    private void VolleyGetStorePreview(Map header) {
        BasicErrorListener errorListener = new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        Response.Listener<String> getRoleListener = new Response.Listener<String>() {
            int a = 0;
            @Override
            public void onResponse(String getPics) {
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
                        TempStaticInstanceCollection.storePreviewInfo.get(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //获取失败
                    Log.v("getOnlineStorePictures", "获取商店预览图片失败");
                }
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        RequestManager.getInstance(getApplicationContext())
                .addRequest(new BasicRequest(Request.Method.GET, Host.getStorePreviewPictures, getRoleListener, errorListener, header));
    }
    private void VolleyGetPMphonenumber(Map header) {
        BasicErrorListener errorListener = new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        Response.Listener<String> getRoleListener = new Response.Listener<String>() {
            int a;
            @Override
            public void onResponse(String getPhone) {
                if(!getPhone.equals("")){
                    JSONObject jsonObject = null;
                    JSONArray phoneJsonArray = null;
                    try {
                        jsonObject = new JSONObject(getPhone);
                        phoneJsonArray = jsonObject.getJSONArray("jsonString");
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
                synchronized (volley_count_lock){
                    volley_count++;
                    if(volley_count >= 5) {
                        JumpToHomeActivity();
                    }
                }
            }
        };
        RequestManager.getInstance(getApplicationContext()).
                addRequest(new BasicRequest(Request.Method.GET, Host.getPropertyManagerPhoneList, getRoleListener, errorListener, header));
    }

    private void JumpToLoginActivity() {
        Intent intent = new Intent(DetectAutoLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
    private void JumpToHomeActivity() {
        Intent intent = new Intent(DetectAutoLoginActivity.this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    /*2016-5-25 CAS登录代理类，完成登录流程*/
    protected CASLoginProxy casLoginProxy = new CASLoginProxy(DetectAutoLoginActivity.this);
    /*2016-6-3，CAS登录流程中反馈信息的广播处理*/
    protected BroadcastReceiver CASBroadcastReceiver = new BroadcastReceiver() {
        int a = 1;
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CASLoginProxy.CAS_ERROR_BROADCAST_ACTION)) {
                //登陆过程中 出错的情况
                switch(intent.getIntExtra("errorInfo", -1)) {
                    case CASLoginProxy.CAS_AUTHENTICATION_FAILED_USER_INFO_INVALID:
                        //用户名或密码错误
                        Toast.makeText(DetectAutoLoginActivity.this, "用户名或密码有误,请重新输入", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JumpToLoginActivity();
                        break;
                    case CASLoginProxy.CAS_REQUEST_ERROR_CONNECT_TIMEOUT:
                        Toast.makeText(DetectAutoLoginActivity.this, "网络环境差，请稍后重试", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JumpToLoginActivity();
                        break;
                    case CASLoginProxy.CAS_REQUEST_ERROR_NETWORK_ERROR:
                        Toast.makeText(DetectAutoLoginActivity.this, "请检查网络设备", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JumpToLoginActivity();
                        break;
                    case CASLoginProxy.CAS_REQUEST_ERROR_SERVER_EXCEPTION:
                        Toast.makeText(DetectAutoLoginActivity.this, "服务器异常,请稍后重试", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JumpToLoginActivity();
                        break;
                    case CASLoginProxy.CAS_HOST_LIST_JSON_EXCEPTION:
                        Toast.makeText(DetectAutoLoginActivity.this, "园区信息异常，请重新登录", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JumpToLoginActivity();
                        break;
                    case -1:default:
                        //出bug了，需要修复
                        break;
                }
            }
            if(intent.getAction().equals(CASLoginProxy.CAS_SUCCESS_BROADCAST_ACTION)) {
                //阶段成功反馈
                switch (intent.getIntExtra("errorInfo", -1)) {
                    case -1:default:
                        Log.d("logical error", "CAS broadcast logical bug"); //跑到这里说明代码逻辑有bug
                        break;
                    case CASLoginProxy.CAS_ACTIVATE_HOST_SESSION_SUCCESS:
                        Log.d("CAS", "CAS Injection Session Success");
                        break;
                }
            }
            if(intent.getAction().equals(CASLoginProxy.CAS_DATA_TRANSITION_HOSTINFO_MAP_BROADCAST_ACTION)) {
                //数据传输
                final GardenInfoList gardenHostMap = (GardenInfoList) intent.getParcelableExtra(CASLoginProxy.EXTRA_KEY_DATA_TRANSITION_GADEN_HOST_MAP);
                final GardenInfoList gardenDBnameMap = (GardenInfoList)intent.getParcelableExtra(CASLoginProxy.EXTRA_KEY_DATA_TRANSITION_GARDEN_DB_MAP);
                Session.gardenDBNameMap = gardenDBnameMap.getGardenCertainInfoMap();
                Session.gardenHostMap = gardenHostMap.getGardenCertainInfoMap();
                String hosturl = null;
                String dbName = null;
                boolean isAutoSelectedGardenLeagel = false;
                for(String gardenName : gardenHostMap.getGardenCertainInfoMap().keySet()) {
                    if(gardenName.equals(configdata.autoLoginGardenName)) {
                        hosturl = gardenHostMap.getGardenCertainInfoMap().get(configdata.autoLoginGardenName);
                        dbName = gardenDBnameMap.getGardenCertainInfoMap().get(configdata.autoLoginGardenName);
                        isAutoSelectedGardenLeagel = true;
                        break;
                    }
                }

                //此处注意，如果用户选择了"记住我的选择"，先进行匹配(是否在园区列表里面，没有的话还是要再选一次)
                if(configdata.isAutoSelectGarden && isAutoSelectedGardenLeagel) {
                    casLoginProxy.ActivateHostSession(hosturl, dbName);
                }else {
                    //获得园区选择列表后，创建选择对话框，让用户进行选择
                    final ListViewDialog.Builder builder = new ListViewDialog.Builder(DetectAutoLoginActivity.this);
                    builder.setListItemName(gardenHostMap.getGardenCertainInfoMap());
                    builder.setTitle("请选择登录园区");

                    if(isAutoSelectedGardenLeagel) {
                        builder.setDefaultListItem(configdata.autoLoginGardenName);
                    }else {
                        //builder.setDefaultListItem(0);
                    }
                    builder.setMultyChoosen(false);
                    builder.setOnBackPressedListener(new ListViewDialog.OnBackPressedListener() {
                        @Override
                        public void onClick() {
                            //do nothing
                        }
                    });
                    builder.setOnConfirmButtonClickListner(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String key = builder.getKey();
                            String hostUrl = gardenHostMap.getGardenCertainInfoMap().get(key);
                            String dbName = gardenDBnameMap.getGardenCertainInfoMap().get(key);
                            gardenHostName = hostUrl;
                            gardenDBName = dbName;
                            casLoginProxy.ActivateHostSession(hostUrl, dbName);
                            dialog.dismiss();
                        }
                    });
                    builder.create(0).show();
                }
            }

            if(intent.getAction().equals(CASLoginProxy.CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION)) { //园区JSESSION的传输
                Session.seesion = intent.getStringExtra(CASLoginProxy.EXTRA_KEY_DATA_TRANSITION_HOST_SESSION);
                //动态改变所有请求
                Host.DynamiclySetAPIs(gardenHostName, gardenDBName);
                SubsequentLogin();
            }
        }
    };
    /*绑定CAS广播接收者需要接受的广播*/
    private void BindCASReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CASLoginProxy.CAS_ERROR_BROADCAST_ACTION);
        intentFilter.addAction(CASLoginProxy.CAS_SUCCESS_BROADCAST_ACTION);
        intentFilter.addAction(CASLoginProxy.CAS_DATA_TRANSITION_HOSTINFO_MAP_BROADCAST_ACTION);
        intentFilter.addAction(CASLoginProxy.CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION);
        DetectAutoLoginActivity.this.registerReceiver(CASBroadcastReceiver, intentFilter);
    }

    private ConfigData configdata; //自动登录配置文件的信息
    //配置数据
    private class ConfigData {
        public String autoLoginuserName, autoLoginpassword; //登录参数，用户名和密码
        public String autoLoginGardenName; //多园区选择时候的首选园区，用户选择了"记住我的选择"才会用上
        public int UnlockMode; //开锁模式，现有wifi和蓝牙
        public boolean isAutoSelectGarden; //是否自动选择园区
        public boolean isShakeUnlock; //是否摇一摇开锁自动开启
        public boolean isAutoLogin; //是否要自动登录，到现在为止都没有用

        public ConfigData(String filePath) {
            if(!ReadLoginConfiguration(filePath)) {
                isAutoLogin = false;
            }
        }

        public ConfigData(Properties fileProperties) {
        }

        /*定义一个更健康的配置文件接口读取，把所有配置文件项全部读取出来*/
        private boolean ReadLoginConfiguration(String loginConfigFilePath) {
            boolean isRead = false;
            //读取配置文件
            Properties loginConfigProperties = new Properties();
            try {
                File loginConfigureFile = new File(autoLoginConfigFilePath);
                if (loginConfigureFile.exists()){
                    loginConfigureFile.createNewFile();
                    return isRead;
                } else {
                    FileInputStream fs = new FileInputStream(loginConfigureFile);
                    loginConfigProperties.load(fs);
                    autoLoginuserName = loginConfigProperties.getProperty("userName"); //获取用户名
                    autoLoginpassword = loginConfigProperties.getProperty("passWord"); //获取密码
                    if(loginConfigProperties.getProperty("isAutoLoginExpected").equals("true")){ //是否需要自动登录
                        isAutoLogin = true;
                    }else {
                        isAutoLogin = false;
                    }
                    if(loginConfigProperties.getProperty("preferredUnlockMethod").equals("WiFi")) { //默认开锁模式
                        UnlockMode = 1; //WiFi开门
                    }else if(loginConfigProperties.getProperty("preferredUnlockMethod").equals("Bluetooth")) {
                        UnlockMode = 2; //蓝牙开门
                    }
                    if(loginConfigProperties.getProperty("isShakingAble").equals("true")){ //摇一摇开关是否开启
                        isShakeUnlock = true;
                    }else {
                        isShakeUnlock = false;
                    }
                    if(loginConfigProperties.getProperty("isAutoSelectGarden").equals("true")) { //是否自动选择园区
                        isAutoSelectGarden = true;
                        autoLoginGardenName = loginConfigProperties.getProperty("GardenName");
                    }else {
                        isAutoSelectGarden = false;
                        autoLoginGardenName = "";
                    }
                    isRead = true;
                }
            }catch (Exception e) {
            }finally {
                return isRead;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_cover);

        appCoverImageView = (ImageView) (findViewById(R.id.appcover_imgview));
        InputStream is = this.getResources().openRawResource(R.raw.appcover);
        Bitmap appCoverBitmap = MyBitmapFactory.decodeRawBitMap(is);
        appCoverImageView.setImageBitmap(appCoverBitmap);


        /*Intent intent = new Intent(DetectAutoLoginActivity.this, CheckMyStopLeaseHistory.class);
        startActivity(intent);*/

        /*new DetectAutoLoginTask().execute(); //开始自动登录活动的线程*/
        configdata = new ConfigData(autoLoginConfigFilePath);
        configdata.isAutoLogin = true;
        configdata.autoLoginuserName = "18215598182";
        configdata.autoLoginpassword = "123456";
        if(configdata.isAutoLogin) {
            BindCASReceiver(); //注册并启动CAS广播接收者
            casLoginProxy.CASLogin(configdata.autoLoginuserName, configdata.autoLoginpassword);
        }else {
            //使用handler让线程睡眠1秒后进入登录界面
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   JumpToLoginActivity();
                }
            }, 500);
        }
    }

    /**
     * 以下代码已经被弃用
    * */
    //private boolean IsAutoLoginExpected;
    /*判断配置文件是否存在，不存在会新增该配置文件*/
    private boolean IsAutoLoginConfigureFileExist(String strAutoLoginConfigureFile) {
        try {
            File f = new File(strAutoLoginConfigureFile);
            if (!f.exists()) {
                f.createNewFile();
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean ReadSavedUserInfoToLoginInfo() {
        String configKey;
        Properties properties = new Properties();
        try {
            File autoLoginConfigureFile = new File(this.autoLoginConfigFilePath);
            FileInputStream autoLoginConfigureStream = new FileInputStream(autoLoginConfigureFile);
            properties.load(autoLoginConfigureStream);

            Enumeration allConfigKey = properties.propertyNames();

            int keyIndex = 0;
            while (allConfigKey.hasMoreElements()) {
                //将所有的配置项读完并赋值
                configKey = (String) allConfigKey.nextElement();
                IdentifyConfigKeyAndAssignToReferredValue(configKey, properties);
                keyIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //return false;
        }
        /*return this.IsAutoLoginExpected;*/
        return false;
    }
    private void IdentifyConfigKeyAndAssignToReferredValue(String configKey, Properties autoLoginConfig) {
        if (configKey.equals("userName")) {
            //setUserName(autoLoginConfig.getProperty(configKey));
            Person.setPhone(autoLoginConfig.getProperty(configKey));
        }
        if (configKey.equals("passWord")) {
            //setPassWord(autoLoginConfig.getProperty(configKey));
        }
        if (configKey.equals("isAutoLoginExpected")) {
            if (autoLoginConfig.getProperty(configKey).equals("true")) {
                //setIsAutoLoginExpected(true);
            } else {
                //setIsAutoLoginExpected(false);
            }
        }

        /*新增读取用户开锁习惯配置项*/
        if (configKey.equals("preferredUnlockMethod")) {
            if (autoLoginConfig.getProperty(configKey).equals("WiFi")) {
                Session.setUnlockMode(1);
            }
            if (autoLoginConfig.getProperty(configKey).equals("Bluetooth")) {
                Session.setUnlockMode(2);
            }
        }
        if (configKey.equals("isShakingAble")) {
            if (autoLoginConfig.getProperty(configKey).equals("true")) {
                Session.setIsShakingUnlockAble(true);
            } else {
                Session.setIsShakingUnlockAble(false);
            }
        }
        //日后可能会拓展配置项的key值
    }
    /*非CAS登录流程的主函数，已废弃*/
    private boolean DetectAutoLogin() {
        /*
        * 检测自动登录的方法
        * 1.读取相应的记录文件
        * 2.解析记录，并执行登录操作
        * 3.如果没有记录，则跳转到登录页面
        * 4.2016-3-27 新增：完成公告信息的在线获取，不再在智能通首页中获取
        * */
        String strPathofAutoLoginConfigureFile = this.autoLoginConfigFilePath;

        String configKey, configValue;

        if (IsAutoLoginConfigureFileExist(strPathofAutoLoginConfigureFile)) {
            //如果存在，则开始读取配置文件
            Properties properties = new Properties();
            try {

                File autoLoginConfigureFile = new File(strPathofAutoLoginConfigureFile);
                FileInputStream autoLoginConfigureStream = new FileInputStream(autoLoginConfigureFile);
                properties.load(autoLoginConfigureStream);

                Enumeration allConfigKey = properties.propertyNames();

                int keyIndex = 0;
                while (allConfigKey.hasMoreElements()) {

                    configKey = (String) allConfigKey.nextElement();

                    ReadSavedUserInfoToLoginInfo();

                    keyIndex++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                //return false;
            }

            /*获得记录的用户名和密码后，开始进行网络通信，与一般的用户登录流程一致*/
            //发送两次http请求，第一次是验证用户身份，第二次是获得用户的最高权限，完成后跳转
            String loginResult = "";
                    //HttpUtils.HttpGet(DetectAutoLoginActivity.this, Host.login + "?phone=" + this.userName + "&password=" + this.passWord);
            //此处使用了原版的http接口，在新一轮的迭代时要更换为volley或其他框架
            try {
                JSONObject loginResultJsonParseString = new JSONObject(loginResult);
                boolean loginStatus = loginResultJsonParseString.getBoolean("status");

                if (loginStatus) {
                    //如果登录结果返回为真，则进行权限获取
                    Session.setSeesion("JSESSIONID=" + loginResultJsonParseString.getString("jsonString"));

                    //再次进行一次http请求
                    String getRoleResult = HttpUtils.HttpGet(DetectAutoLoginActivity.this, Host.getRole);
                    JSONObject getRoleResultJsonParseString = new JSONObject(getRoleResult);
                    boolean getRoleStatus = getRoleResultJsonParseString.getBoolean("status");

                    if (getRoleStatus) {
                        //分析获取角色权限的请求结果
                        String roles = getRoleResultJsonParseString.getString("jsonString");
                        int maxRole = 0;
                        for (String roleString : Arrays.asList(roles.split(","))) {
                            int role = Integer.parseInt(roleString);
                            if (role > maxRole)
                                maxRole = role;
                        }

                        Session.setRole(maxRole);

                        String getSecretJsonString = HttpUtils.HttpGet(DetectAutoLoginActivity.this,
                                Host.getSecret);

                        if (!getSecretJsonString.equals("")) {
                            //登录成功
                            SharedPreferences sp = getSharedPreferences("secret", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("jsonString", getSecretJsonString);

                            //新增，将获得的可用列表全部写到内存中
                            JSONObject getSecretResult = new JSONObject(getSecretJsonString);
                            if (getSecretResult.getBoolean("status")) {
                                //返回成功，将所有jsonstring进行解析
                                JSONArray ssidArrayJson = getSecretResult.getJSONArray("jsonString");

                                for (int index = 0; index < ssidArrayJson.length(); index++) {
                                    JSONObject singleSsidJsonObj = ssidArrayJson.getJSONObject(index);

                                    int type = singleSsidJsonObj.getInt("type");
                                    if (type == 2) {
                                        String ssid = singleSsidJsonObj.getString("symbol");
                                        String secret = singleSsidJsonObj.getString("secret");
                                        String password = singleSsidJsonObj.getString("password");
                                        WiFiProtalInfo singleWiFiProtalInfo = new WiFiProtalInfo(ssid, password, secret);
                                        Session.addMemberToAccessableWiFiSsidList(singleWiFiProtalInfo);
                                    } else if (type == 1) {
                                        String ssid = singleSsidJsonObj.getString("symbol");
                                        String secret = singleSsidJsonObj.getString("secret");
                                        BluetoothProtalInfo singleBTProtalInfo = new BluetoothProtalInfo(ssid, secret);
                                        Session.addMemberToAccessableBTSsidList(singleBTProtalInfo);
                                    }
                                }
                            }
                            //List<WiFiProtalInfo> templist = Session.getAccessableWiFiSsidList();
                            editor.commit();
                            GetOnlineNotifyList();
                            GetStorePreviewPictures();
                            GetPropertyManagementCompanyPhone();
                            return true;
                        }
                    } else {
                    }
                } else {
                    //结果为假
                }
            } catch (JSONException jsone) {
                jsone.printStackTrace();
            }

        } else {
            //如果不存在，则返回假，提示ui组件跳转到登录界面
            return false;
        }
        return false;
    }
    /*获取公告（非volley实现），已废弃*/
    private void GetOnlineNotifyList() {
        String getNotify = HttpUtils.HttpGet(DetectAutoLoginActivity.this, Host.getNotice1);
        if (!getNotify.equals("")) {
            GetAnnouncementData getAnnouncementData = new GetAnnouncementData();
            getAnnouncementData = JsonTools.getAnnouncement(DetectAutoLoginActivity.this, "jsonString", getNotify);
            if (getAnnouncementData.isStatus()) {
                TempStaticInstanceCollection.announcementList = getAnnouncementData;
                if(getAnnouncementData == null){
                    TempStaticInstanceCollection.announcementList.setStatus(false);
                }
            } else {
                //Log.v("notify failed", "获取公告失败");
                TempStaticInstanceCollection.announcementList = new GetAnnouncementData();
            }
        }
    }
    /*2016/4/6获取看板图片（非volley实现），已废弃*/
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
                TempStaticInstanceCollection.storePreviewInfo.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //获取失败
            Log.v("getOnlineStorePictures", "获取商店预览图片失败");
        }
    }
    /*2016/4/7获取物管电话（非volley实现），已废弃*/
    private void GetPropertyManagementCompanyPhone(){
        String getPhone = HttpUtils.HttpGet(DetectAutoLoginActivity.this, Host.getPropertyManagerPhoneList);
        /*getPhone = "\"status\":true,\"errorMsg\":{\"code\":null,\"description\":null},\"jsonString\":{}"*/;
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



