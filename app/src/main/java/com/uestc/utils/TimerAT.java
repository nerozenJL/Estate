package com.uestc.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Timer;

/**
 * Created by Nerozen on 2016/3/29.
 */
public class TimerAT extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        String timerTerminatedStatus = "1";
        isExcuting = true;
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                timerTerminatedStatus = "-1";
                break;
            }
            synchronized (timeSetlock){
                remainingTime--;
                Log.d("timerAT", remainingTime + "");
                if(remainingTime <= 0){
                    break;
                }
            }
        }
        isExcuting = false;
        instance = null;
        return timerTerminatedStatus;
    }

    @Override
    protected void onPostExecute(String s) {
        Intent AutoDisconnectBroadcast = new Intent();
        AutoDisconnectBroadcast.setAction("AUTO_UPDATE_IDEAL_WIFI");
        activityContext.sendBroadcast(AutoDisconnectBroadcast);
        super.onPostExecute(s);
    }

    private static int remainingTime;
    private static final Object timeSetlock = new Object();
    private Context activityContext;
    private volatile static TimerAT instance;
    private static boolean isTerminated = true;

    public static boolean isExcuting() {
        return isExcuting;
    }

    public static boolean isExcuting = false;


    public TimerAT(Context appContext){
        activityContext = appContext;
        isTerminated = false;
        //isExcuting = true;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public static TimerAT getInstance(Context appActivity, int time){
        if(instance == null){
            synchronized (Timer.class){
                if(instance == null){
                    instance = new TimerAT(appActivity);
                }
            }
        }
        synchronized (timeSetlock){
            remainingTime = time;
        }
        return instance;
    }
}
