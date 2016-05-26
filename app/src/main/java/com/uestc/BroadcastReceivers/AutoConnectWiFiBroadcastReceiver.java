package com.uestc.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by Nerozen on 2016/3/24
 * 开启WiFi连接，但是没有连接上任意wifi时接受广播
 *  接收以下几个广播
 *  1. 应用启动（自动登录页面激活）发送应用启动的广播
 *  2. WiFi连接断开，发送的断开广播
 *  3. WiFi变更（从连接a改变到连接b）的广播
 */
public class AutoConnectWiFiBroadcastReceiver extends BroadcastReceiver {

    public final static String AppActivatedBroadcast = "APP_ACTIVATED";
    private String AutoChooseIdealWiFiBroadcast = "AUTO_UPDATE_IDEAL_WIFI";
    //private String WiFiDisconnected = NetworkInfo.State.DISCONNECTED;

    @Override
    public void onReceive(Context context, Intent intent) {
        String gotBroadcast = intent.getAction().toString();

        if(gotBroadcast.equals(AppActivatedBroadcast) || gotBroadcast.equals(AutoChooseIdealWiFiBroadcast)){
            WifiManager tempManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
            Toast.makeText(context, "接受到启动广播", Toast.LENGTH_SHORT).show();
//            List<WiFiProtalInfo> idealWiFiList = tempAdmin.RefreshWiFi();
            tempManager.setWifiEnabled(false);
//            tempManager.disconnect();
        }
       /* if(gotBroadcast.equals(AutoChooseIdealWiFiBroadcast)){
            //WifiAdmin.RefreshWiFi();
        }*/
    }
}
