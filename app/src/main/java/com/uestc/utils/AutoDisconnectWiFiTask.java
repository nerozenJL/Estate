package com.uestc.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jacob Long on 2016/4/15.
 */
public class AutoDisconnectWiFiTask extends TimerTask {
    private static AutoDisconnectWiFiTask instance;
    private static Object timeSetlock = new Object();
    private static int remainingTime;
    private static Context appContext;
    private static Timer Manager;

    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean isRunning;

    public static AutoDisconnectWiFiTask getInstance(int time, Context context, Timer selfManager) {
        if(instance == null){
            synchronized (AutoDisconnectWiFiTask.class){
                if(instance == null){
                    instance = new AutoDisconnectWiFiTask();
                    appContext = context;
                    Manager = selfManager;
                }
            }
        }
        synchronized (timeSetlock){
            remainingTime = time;
        }
        return instance;
    }

    private AutoDisconnectWiFiTask() {
        isRunning = true;
    }

    @Override
    public void run() {
        remainingTime--;//每隔一秒执行一次,因此传入Timer时，应该将period设为1000毫秒
        Log.v("timerToDisconnect", remainingTime + "");
        if(remainingTime == 0) {
            //发送广播，断开当前wifi
            Intent AutoDisconnectBroadcast = new Intent();
            AutoDisconnectBroadcast.setAction("AUTO_UPDATE_IDEAL_WIFI");
            appContext.sendBroadcast(AutoDisconnectBroadcast);
            synchronized (timeSetlock) {
                if(Manager != null) {
                    Manager.cancel();
                }
                isRunning = false; //task完成，状态置为不在运行中
                instance = null; //将实例置为空，等待下一次实例化
            }
        }
    }
}
