package com.uestc.utils;

import com.uestc.domain.BluetoothProtalInfo;
import com.uestc.domain.GetAnnouncementData;
import com.uestc.domain.Session;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.domain.WiFiProtalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jacob Long on 2016/5/27.
 * 该类是解析服务器响应的json数据工具类
 * 为每一个请求响应实现一个方法
 */
public class ResponseJsonParser {

    public static final int JSON_STATUS_FALSE = -2;
    //public static final int JSON_STATUS_TRUE = 100;

    public Map ParseGardenInfoList(String rawJsonString) throws JSONException, IndexOutOfBoundsException {

        JSONArray jumpAddress = new JSONArray(rawJsonString);
        JSONObject jsonObject = (JSONObject) jumpAddress.get(0);

        Map<String, JSONObject> keyGardenInfoMap = new HashMap<String, JSONObject>();

        Iterator it = jsonObject.keys();
        while(it.hasNext()){
            String key = String.valueOf(it.next());
            JSONObject value = (JSONObject)jsonObject.get(key);
            keyGardenInfoMap.put(key, value);
        }

        return keyGardenInfoMap;
    }

    public Map ParseGardenInfoList(JSONArray jsonArray) throws JSONException{
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);

        Map<String, JSONObject> keyGardenInfoMap = new HashMap<String, JSONObject>();

        Iterator it = jsonObject.keys();
        while(it.hasNext()){
            String key = String.valueOf(it.next());
            JSONObject value = (JSONObject)jsonObject.get(key);
            keyGardenInfoMap.put(key, value);
        }

        return keyGardenInfoMap;
    }

    /*解析getRole请求返回的json，最终获得整形值形式的用户权限*/
    public int ParseRolePority(String rawJsonString) throws JSONException {
        int maxRole = JSON_STATUS_FALSE;
        JSONObject jsonObject = new JSONObject(rawJsonString);
        if (jsonObject.getBoolean("Status")) {
            String roleCollection = jsonObject.getString("jsonString");
            for (String roleString : Arrays.asList(roleCollection.split(","))) {
                int role = Integer.parseInt(roleString);
                if (role > maxRole) {
                    maxRole = role;
                }
            }
        }
        return maxRole;
    }

    /*解析getSecrets返回的json，将控制器对应的名称，密码，通信密钥等写入Session中
    * 如果用户有可访问的控制器，返回控制器列表长度
    * 如果请求出错，返回JSON_STATUS_FALSE
    * 备注：由于方法内部还涉及了Session类的操作，耦合度有点高，在重构的时候可以考虑重写这个方法
    * */
    public int ParseControllerSecretList(String rawJsonString) throws JSONException {
        JSONObject getSecretResult = new JSONObject(rawJsonString);
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
            return ssidArrayJson.length();
        }
        else {
            return JSON_STATUS_FALSE;
        }
    }

    /*解析getNotice1返回的在线通知json串
    *
    * */
    public int ParseOnlineNotification(String rawJsonString) {
        return 0;
    }

    /*解析商店预览图片的json串
    * 有点问题，暂时不做*/
    public void ParseStorePreview() {

    }

    /*解析getPropertyManagerPhoneList返回的物管电话的json串
    * 有点问题，暂时不做*/
    public void ParsePropertyManagerNumberList(String rawJsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(rawJsonString);
        if(jsonObject.getBoolean("status")) {
            JSONArray phonejsonArray = jsonObject.getJSONArray("phone");
            for(int index = 0; index < phonejsonArray.length(); index++) {
                phonejsonArray.getString(index);
            }
        }else {

        }
    }
}
