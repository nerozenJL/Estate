package com.uestc.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;

/**
 * Created by hekesong on 2015/10/30.
 * 这是session为空时，即服务器得不到session，重新登录的逻辑
 */
public class ForceOfflineBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "强制退出，请重新登录", Toast.LENGTH_SHORT).show();
        ActivityCollector.finishAll();
        Intent intent1 = new Intent(context,LoginActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
