package com.uestc.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;


import com.uestc.domain.Session;
import com.uestc.service.WifiUnlocker;
import com.uestc.ui.activity.HomeActivity;

public class ShakeSensorService extends Service {
    //接收来自UI的Intent的EXTRA的key
    public static final String UNLOCK_TYPE="UNLOCK_TYPE";
    public static final String OPEN_OR_CLOSE="OPEN_OR_CLOSE";
    public static final int OPEN_BY_BLUETOOTH=1;
    public static final int OPEN_BY_WIFI=2;
    public static final int OPEN_SENSOR=3;
    public static final int CLOSE_SENSOR=4;
    //摇一摇所需静态变量
    private static int shaketimes=0;
    private static int wifiorbluetooth;
    public static boolean isSensorRuning=false;
    //摇一摇所需对象
    private static SensorManager sensorManager;
    private static ShakeSensorEventListener shakeSensorEventListener;
    public ShakeSensorService() {
    }
    @Override
    public void onCreate(){
        BackgrandThread backgrandThread=new BackgrandThread();
        backgrandThread.start();
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        int opentype=-1;
        int openorclose=-1;
        openorclose=intent.getIntExtra(OPEN_OR_CLOSE,openorclose);
        opentype=intent.getIntExtra(UNLOCK_TYPE, opentype);
        if (openorclose==OPEN_SENSOR){
            wifiorbluetooth=opentype;
            shakeSensorEventListener=new ShakeSensorEventListener();
            //获取加速度传感器
            sensorManager=(SensorManager)this.getSystemService(this.SENSOR_SERVICE);
            Sensor accelerateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(shakeSensorEventListener, accelerateSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isSensorRuning=true;
        }else if (openorclose== CLOSE_SENSOR&&isSensorRuning==true) {
            sensorManager.unregisterListener(shakeSensorEventListener);
            isSensorRuning=false;
        }
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //摇一摇事件监听器
    class ShakeSensorEventListener implements SensorEventListener {
        String TAG="";

        @Override
        public void onSensorChanged(SensorEvent event) {
            int sensorType = event.sensor.getType();
            //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;

            float x = values[0];
            float y = values[1];
            float z = values[2];

            Log.i(TAG, "x:" + x + "y:" + y + "z:" + z);
            Log.i(TAG, "Math.abs(x):" + Math.abs(x) + "Math.abs(y):" + Math.abs(y) + "Math.abs(z):" + Math.abs(z));
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                int value = 17;//摇一摇阀值,不同手机能达到的最大值不同,如某品牌手机只能达到20

                if (x >= value || x <= -value || y >= value || y <= -value || z >= value || z <= -value) {
                    Log.i(TAG, "检测到摇动");
                    if (shaketimes<3){
                        shaketimes++;
                        return;
                    }
                    shaketimes=0;
                    //进行对应的业务操作
                    if (wifiorbluetooth==OPEN_BY_WIFI&&isSensorRuning==true&&WifiUnlocker.isUnlockeFinished()){
                        Intent intent=new Intent(ShakeSensorService.this,WifiUnlocker.class);
                        intent.setAction("start_wifi_unlock");
                        intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.UNLOCKDOOR);
                        startService(intent);
                    }else if (wifiorbluetooth==OPEN_BY_BLUETOOTH&&isSensorRuning==true&&BluetoothControler.isUnlockFinished){
                        Intent intent=new Intent(ShakeSensorService.this,BluetoothControler.class);
                        intent.setAction("start_bluetooth_unlock");
                        intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.UNLOCKDOOR_BY_SHAKE);
                        startService(intent);
                    }

                }
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    class BackgrandThread extends Thread{
        @Override
        public void run(){
            boolean isRuninfore=true;
            for (;;){
                if (!isRunningForeground(ShakeSensorService.this)&&isRuninfore==true&&ShakeSensorService.isSensorRuning==true){
                    Intent intent=new Intent(ShakeSensorService.this,ShakeSensorService.class);
                    intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.CLOSE_SENSOR);
                    startService(intent);
                    isRuninfore=false;
//					changeOpentype();
//					isRuninfore=false;
                }
                else if (isRunningForeground(ShakeSensorService.this)&&isRuninfore==false&&ShakeSensorService.isSensorRuning==false&&Session.isShakingUnlockAble()){
//					Intent intent=new Intent(HomeActivity.this,ShakeSensorService.class);
//					intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
//					startService(intent);
//					isRuninfore=true;
                    changeOpentype();
                    isRuninfore=false;
                }
            }
        }
    }

    public boolean isRunningForeground (Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName()))
        {
            return true ;
        }
        return false ;
    }

    private void changeOpentype() {
        if (HomeActivity.ifShakeboxusable == true && HomeActivity.ifWifiUsable == true) {
            Intent intent = new Intent(ShakeSensorService.this, WifiUnlocker.class);
            intent.setAction("start_wifi_unlock");
            intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.OPEN_SHAKE_FUNCTION);
            startService(intent);
        } else if (HomeActivity.ifShakeboxusable == false && HomeActivity.ifWifiUsable == true) {
            Intent intent = new Intent(ShakeSensorService.this, WifiUnlocker.class);
            intent.setAction("start_wifi_unlock");
            intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.CLOSE_SHAKE_FUNCTION);
            startService(intent);
        } else if (HomeActivity.ifShakeboxusable == true && HomeActivity.ifWifiUsable == false) {
            Intent intent = new Intent(ShakeSensorService.this, BluetoothControler.class);
            intent.setAction("start_bluetooth_unlock");
            intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.OPEN_SHAKE_FUNCTION);
            startService(intent);
        } else if (HomeActivity.ifShakeboxusable == false && HomeActivity.ifWifiUsable == false) {
            Intent intent = new Intent(ShakeSensorService.this, BluetoothControler.class);
            intent.setAction("start_bluetooth_unlock");
            intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.CLOSE_SHAKE_FUNCTION);
            startService(intent);
        }
    }
}
