package com.uestc.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hekesong on 2015/10/30.
 * 这是activity的控制类
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<Activity>();
    /**
     * 添加活动
     * @param activity
     */
    public static void addActivity(Activity activity){
        if(!activities.contains(activity)){
            activities.add(activity);
        }
    }

    /**
     * 移除活动
     * @param activity
     */
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    /**
     * 结束所有活动
     */
    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    public static void LogOut(String result,Context context){
        if(result.equals("no hacked!")){
            //启用广播
            Intent intent = new Intent("com.example.broadcastpractice.FORCE_OFFLINE");
            //发送广播--标准广播
            context.sendBroadcast(intent);
        }
    }
}
