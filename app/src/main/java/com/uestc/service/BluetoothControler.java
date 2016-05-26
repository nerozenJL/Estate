package com.uestc.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.uestc.domain.BluetoothProtalInfo;
import com.uestc.domain.SampleGattAttributes;
import com.uestc.domain.Session;

public class BluetoothControler extends Service {
    public static final int BLUETOOTH_SCAN_OUT_TIME=32;
    public static final int BLUETOOTH_SCAN_SUCCESS=30;
    public static final int BLUETOOTH_SCAN_START=31;
    public static final int BLUETOOTH_SCAN_FAILED=34;
    //public static final int BLUETOOTH_SCAN_FOUND_NOTHING=31;

    public static final int BLUETOOTH_CONNECT_SUCCESS=1;
    public static final int BLUETOOTH_CONNECT_FAILED=2;
    public static final int BLUETOOTH_CONNECT_OUTTIME=3;

    public static final int BLUETOOTH_DISCONNECT_SUCCESS=4;
    public static final int BLUETOOTH_DISCONNECT_FAILED=5;
    public static final int BLUETOOTH_DISCONNECT_OUTTIME=6;

    public static final int BLUETOOTH_SERVICE_DISCOVERED_SUCCESS=7;
    public static final int BLUETOOTH_SERVICE_DISCOVERED_FAILED=8;

    public static final int BLUETOOTH_DEVICE_CONNECTING=9;
    public static final int BLUETOOTH_DEVICE_CONNECT_FAILED_BY_MISSING_DEVICE=10;

    public static final int BLUETOOTH_OPENING=13;
    public static final int BLUETOOTH_OPEN_SUCCESS=14;
    public static final int BLUETOOTH_OPEN_OUTTIME=15;
    public static final int BLUETOOTH_OPEN_ALREADY=16;

    public static final int BLUETOOTH_DISSCONNECT_START=18;

    public static final int BLUETOOTH_COMMUNICATE_SUCCESS=19;
    public static final int BLUETOOTH_COMMUNICATE_FAILED=20;
    public static final int BLUETOOTH_COMMUNICATE_RETURN_VALUE_CORRECT=21;
    public static final int BLUETOOTH_COMMUNICATE_RETURN_VALUE_INCORRECT=22;
    public static final int BLUETOOTH_COMMUNICATE_OUTTIME=23;
    public static final int LAST_BLUETOOTH_UNLOCK_NOT_FINISHED=24;

    public static final int BLUETOOTH_LOCK_RESETFINISHED=28;

    public static final int USER_HAVE_NO_BLUETOOTH_DEVICE_PROPERTY=17;

    public static final int UNLOCKDOOR_BY_BUTTON=25;
    public static final int UNLOCKDOOR_BY_SHAKE=33;
    public static final int OPEN_SHAKE_FUNCTION=26;
    public static final int CLOSE_SHAKE_FUNCTION=27;

    //蓝牙广播动作
    public static final String BLUETOOTH_CONTROLER_BROADCAST="BLUETOOTH_CONTROLER_BROADCAST";
    //接收来自UI的Intent的EXTRA的key
    public static final String BLUETOOTH_UNLOCK_TYPE="BLUETOOTH_UNLOCK_TYPE";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattService mBluetoothGattService;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothProtalInfo bestbluetoothProtalInfo;
    private String mBluetoothDeviceAddress;
    private List<BluetoothProtalInfo> avaliableBluetoothList;
    private int status=-1;
    public static boolean isUnlockFinished=true;
    private static int calledtime = 0;
    private static int connectCount=0;

    private static boolean isScanning = true;
    private static boolean isConnecting=false;
    private static boolean isCommunicating=false;
    private static boolean stopAllTimer=false;

    private static boolean isWriteFinished=true;
    private static int onCharacteristicChangedCount=0;
    private static int timercounter=0;

    private TimerForCommunicate timerForCommunicate;
    private TimerForConnect timerForConnect;
    private TimerForLocktoreset timerForLocktoreset;
    private TimerForScan timerForScan;
    private TimerTask timerTaskForScan,timerTaskForConnect,timerTaskForLocktoreset,timerTaskForCommunicate;
    private static final int TIMERFORSCAN_MAXTIME=5000;
    private static final int TIMERFORCOMMUNICATE_MAXTIME=5000;
    private static final int TIMERFORCONNECT_MAXTIME=5000;
    private static final int TIMERFORLOCKTORESET_MAXTIME=5000;
    private static final int TIMER_SPACING=200;
    private Timer mtimer;
    private static final int TIMERTYPE_SCAN=0;
    private static final int TIMERTYPE_CONNECT=1;
    private static final int TIMERTYPE_LOCKTORESET=2;
    private static final int TIMERTYPE_COMMUNICATE=3;

    //private MyTimer mTimer;
    public static String TAG="BluetoothControler msg";

    private Object scanlock = new Object();

    public final static UUID UUID_INFRARED_GLUCOSE_METER = UUID
            .fromString(SampleGattAttributes.INFRARED_GLUCOSE_METER);

    public BluetoothControler() {
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        int order=-1;
        order=intent.getIntExtra(BLUETOOTH_UNLOCK_TYPE, -1);
        switch (order){
            case UNLOCKDOOR_BY_BUTTON:
                if (!isUnlockFinished){
                    Intent unfinishedIntent=new Intent();
                    unfinishedIntent.setAction(BLUETOOTH_CONTROLER_BROADCAST);
                    unfinishedIntent.putExtra("ACTION_TYPE", LAST_BLUETOOTH_UNLOCK_NOT_FINISHED);
                    sendBroadcast(unfinishedIntent);
                }else {

                    openByBluetooth();
                }
                break;
            case UNLOCKDOOR_BY_SHAKE:
                if (!isUnlockFinished){
                    Intent unfinishedIntent=new Intent();
                    unfinishedIntent.setAction(BLUETOOTH_CONTROLER_BROADCAST);
                    unfinishedIntent.putExtra("ACTION_TYPE", LAST_BLUETOOTH_UNLOCK_NOT_FINISHED);
                    sendBroadcast(unfinishedIntent);
                }else {

                    UnlockWithBluetooth unlockThread = new UnlockWithBluetooth();
                    unlockThread.start();
                }
                break;
            case OPEN_SHAKE_FUNCTION:
                Intent openshakeIntent=new Intent(BluetoothControler.this, ShakeSensorService.class);
                openshakeIntent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
                openshakeIntent.putExtra(ShakeSensorService.UNLOCK_TYPE, ShakeSensorService.OPEN_BY_BLUETOOTH);
                startService(openshakeIntent);
                break;
            case CLOSE_SHAKE_FUNCTION:
                Intent closeshakeIntent=new Intent(BluetoothControler.this,ShakeSensorService.class);
                closeshakeIntent.putExtra(ShakeSensorService.OPEN_OR_CLOSE, ShakeSensorService.CLOSE_SENSOR);
                startService(closeshakeIntent);
                break;
        }
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void openByBluetooth(){
        printMsg("openByBluetooth");
        if (!isUnlockFinished){
            return;
        }
        isUnlockFinished=false;
        initData();
        int EnableResult=EnableBluetooth(8000);
        switch (EnableResult){
            case BLUETOOTH_OPEN_SUCCESS:
            case BLUETOOTH_OPEN_ALREADY:
                break;
            case BLUETOOTH_OPEN_OUTTIME:
                updateBroadCast(BLUETOOTH_OPEN_OUTTIME);
                isUnlockFinished=true;
                return;
        }

        updateBroadCast(BLUETOOTH_SCAN_START);
        if (mBluetoothAdapter.startLeScan(leScanCallback)){
//            if (timerForScan.isAlive())
//                timerForScan.stop();
//            timerForScan=new TimerForScan(5000);
//            timerForScan.start();
            starttimer(TIMERTYPE_SCAN);
        }else {
            Log.e(TAG, "openByBluetooth: start scan failed");
            updateBroadCast(BLUETOOTH_SCAN_FAILED);
        }
        return;
    }

    private void initData(){
        Log.d(TAG, "initData: " + "Entered");
        printMsg("initData");
        isUnlockFinished=false;
        stopAllTimer=true;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timerForCommunicate=new TimerForCommunicate(5000);
        timerForConnect=new TimerForConnect(5000);
        timerForLocktoreset=new TimerForLocktoreset(5000);
        timerForScan=new TimerForScan(5000);

        timerTaskForCommunicate=new TimerTask() {
            @Override
            public void run() {
                if (timercounter==0){
                    Log.e(TAG, "run: timerTaskForCommunicate is runing...");
                    printMsg("timerTaskForCommunicate");
                }
                if (timercounter>=TIMERFORCOMMUNICATE_MAXTIME/TIMER_SPACING){
                    stopTimer();
                    updateBroadCast(BLUETOOTH_COMMUNICATE_OUTTIME);
                    Log.e(TAG, "run: timerTaskForCommunicate return for Communicate is out of time");
                    timercounter=0;
                }else
                    timercounter++;
            }
        };
        timerTaskForConnect=new TimerTask() {
            @Override
            public void run() {
                if (timercounter==0){
                    Log.e(TAG, "run: timerTaskForConnect is runing...");
                    printMsg("timerTaskForConnect");
                }
                if (timercounter>=TIMERFORCONNECT_MAXTIME/TIMER_SPACING){
                    stopTimer();
                    updateBroadCast(BLUETOOTH_CONNECT_OUTTIME);
                    Log.e(TAG, "run: timerTaskForScan return for Communicate is out of time");
                    timercounter=0;
                }else
                    timercounter++;
            }
        };
        timerTaskForScan=new TimerTask() {
            @Override
            public void run() {
                if (timercounter==0){
                    Log.e(TAG, "run: timerTaskForScan is runing...");
                    printMsg("timerTaskForScan");
                }
                if (timercounter>=TIMERFORSCAN_MAXTIME/TIMER_SPACING){
                    stopTimer();
                    updateBroadCast(BLUETOOTH_SCAN_OUT_TIME);
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                    Log.e(TAG, "run: timerTaskForScan return for Communicate is out of time");
                    timercounter=0;
                }else
                    timercounter++;
            }
        };
        timerTaskForLocktoreset=new TimerTask() {
            @Override
            public void run() {
                if (timercounter==0){
                    Log.e(TAG, "run: timerTaskForLocktoreset is runing...");
                    printMsg("timerTaskForConnect");
                }
                if (timercounter>=TIMERFORLOCKTORESET_MAXTIME/TIMER_SPACING){
                    stopTimer();
                    updateBroadCast(BLUETOOTH_LOCK_RESETFINISHED);
                    Log.e(TAG, "run: timerTaskForLocktoreset return for Communicate is out of time");
                    timercounter=0;
                }else
                    timercounter++;
            }
        };
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(BluetoothControler.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bestbluetoothProtalInfo=null;
        mBluetoothGatt=null;
        avaliableBluetoothList = Session.getAccessableBlueToothSsidList();
        isScanning=true;
        isCommunicating=true;
        isConnecting=true;
        stopAllTimer=false;
        Log.e(TAG, "initData: Finished");
    }

    class UnlockWithBluetooth extends Thread{
        @Override
        public void run() {
            openByBluetooth();
        }
    }

    private int EnableBluetooth(int wait_time){
        Log.e(TAG, "EnableBluetooth: Entered");
        printMsg("EnableBluetooth");
        if (mBluetoothAdapter==null){
            Log.e(TAG, "EnableBluetooth: Return because adapter is null");
            return BLUETOOTH_OPEN_OUTTIME;
        }
        if (!mBluetoothAdapter.isEnabled()){
            Intent intent=new Intent();
            intent.setAction(BLUETOOTH_CONTROLER_BROADCAST);
            intent.putExtra("ACTION_TYPE", BLUETOOTH_OPENING);
            sendBroadcast(intent);
            Log.e(TAG, "EnableBluetooth: opening");
            mBluetoothAdapter.enable();
            for (int i=0;i<wait_time;i+=500){
                try {
                    Thread.sleep(500);
                    if (mBluetoothAdapter.isEnabled()){
                        Log.e(TAG, "EnableBluetooth: open bluetooth success!");
                        return BLUETOOTH_OPEN_SUCCESS;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "EnableBluetooth: return because open out of time");
            return BLUETOOTH_OPEN_OUTTIME;
        }
        Log.e(TAG, "EnableBluetooth: return because bluetooth already open");
        return BLUETOOTH_OPEN_ALREADY;
    }

    private void ConnectDevice(){
        Log.e(TAG, "ConnectDevice: ConnectDevice is runing");
        printMsg("ConnectDevice");
        if (!connectDevice(bestbluetoothProtalInfo.getBluetoothDevice())){
            updateBroadCast(BLUETOOTH_CONNECT_FAILED);
//                                isUnlockFinished=true;
            Log.e(TAG, "onLeScan: connect failed,start timerForLocktoreset thread...");
//            timerForLocktoreset=new TimerForLocktoreset(5000);
//            timerForLocktoreset.start();
            starttimer(TIMERTYPE_LOCKTORESET);
        }else {
//                                if (timerForScan.isAlive()){
//                                    timerForScan.stop();
//                                }
//                                if (timerForConnect.isAlive())
//                                    timerForConnect.stop();
            Log.e(TAG, "onLeScan: start timerForConnect thread...");
//            timerForConnect=new TimerForConnect(5000);
//            timerForConnect.start();
            starttimer(TIMERTYPE_CONNECT);
        }
    }

    private class ConnectDeviceThread extends Thread{
        BluetoothDevice device;
        public ConnectDeviceThread(BluetoothDevice device){
            this.device=device;
        }
        @Override
        public void run(){
            Log.d(TAG, "run: ConnectDeviceThread is runing");
            printMsg("run in ConnectDeviceThread");
            if (!connectDevice(bestbluetoothProtalInfo.getBluetoothDevice())){
                updateBroadCast(BLUETOOTH_CONNECT_FAILED);
//                                isUnlockFinished=true;
                Log.d(TAG, "onLeScan: connect failed,start timerForLocktoreset thread...");
                starttimer(TIMERTYPE_LOCKTORESET);
            }else {
//                                if (timerForScan.isAlive()){
//                                    timerForScan.stop();
//                                }
//                                if (timerForConnect.isAlive())
//                                    timerForConnect.stop();
                Log.d(TAG, "onLeScan: start timerForConnect thread...");
                starttimer(TIMERTYPE_CONNECT);
            }
        }
    }
    //连接远端蓝牙设备，本身在连接请求发出后立即返回，不负责连接成功与否的判断
    public boolean connectDevice(BluetoothDevice device){
        Log.e(TAG, "connectDevice: entered");
        printMsg("connectDevice");
        if (connectCount>0){
            Log.e(TAG, "connectDevice: connect failed because connectCount larger than 1");
            //if (mBluetoothGatt!=null){
            disconnect();
            //closeGatt();
            //}
            return false;
        }
        connectCount++;
        if (mBluetoothAdapter == null || device.getAddress() == null) {
            updateBroadCast(BLUETOOTH_DEVICE_CONNECT_FAILED_BY_MISSING_DEVICE);
//            isUnlockFinished=true;
            Log.e(TAG, "connectDevice: BLUETOOTH_DEVICE_CONNECT_FAILED_BY_MISSING_DEVICE");
//            timerForLocktoreset=new TimerForLocktoreset(5000);
//            timerForLocktoreset.start();
            starttimer(TIMERTYPE_LOCKTORESET);
            return false;
        }

        // 若之前连接过该设备，则直接重连
//        if (mBluetoothDeviceAddress != null
//                && device.getAddress().equals(mBluetoothDeviceAddress)
//                && mBluetoothGatt != null) {
//            if (mBluetoothGatt.connect()) {
//                updateBroadCast(BLUETOOTH_DEVICE_CONNECTING);
//                return true;
//            } else {
//                return false;
//            }
//        }
        // 否则，走一般流程进行蓝牙连接

        // We want to directly dialog_highversionfragment to the device, so we are setting the
        // autoConnect
        // parameter to false.
        Log.d(TAG, "connectDevice: start run connectGatt method...");
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mBluetoothGatt = device.connectGatt(this, false, mbluetoothGattCalback);
        isConnecting=true;
        mBluetoothDeviceAddress = device.getAddress();
        updateBroadCast(BLUETOOTH_DEVICE_CONNECTING);
        Log.d(TAG, "connectDevice: return because connect begin");
        return true;
    }
    //负责对连接成功与否的判断与后续相关操作
    //public void initGattCall
    BluetoothGattCallback mbluetoothGattCalback = new BluetoothGattCallback() {
        /**
         * Callback indicating when GATT client has connected/disconnected to/from a remote
         * GATT server.
         *
         * @param gatt     GATT client
         * @param status   Status of the connect or disconnect operation.
         *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         * @param newState Returns the new connection state. Can be one of
         *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
         *                 {@link BluetoothProfile#STATE_CONNECTED}
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "onConnectionStateChange: entered");
            Log.e(TAG, "onConnectionStateChange: status: " + status + " ;newState: " + newState);
            printMsg("onConnectionStateChange");
            super.onConnectionStateChange(gatt, status, newState);
            if (status!= BluetoothGatt.GATT_SUCCESS){
                if (newState== BluetoothProfile.STATE_DISCONNECTED){
                    updateBroadCast(BLUETOOTH_DISCONNECT_FAILED);
                }else if (newState== BluetoothProfile.STATE_CONNECTED){
                    updateBroadCast(BLUETOOTH_CONNECT_FAILED);
                }
            }else {
                if (newState== BluetoothProfile.STATE_CONNECTED){
                    updateBroadCast(BLUETOOTH_CONNECT_SUCCESS);
                    mBluetoothGatt.discoverServices();
                    //sendCommand(bestbluetoothProtalInfo.getAccessSecret(),gatt);
                }else if (newState== BluetoothProfile.STATE_DISCONNECTED){
                    closeGatt();
                    updateBroadCast(BLUETOOTH_DISCONNECT_SUCCESS);
                }
            }
        }

        /**
         * Callback invoked when the list of remote services, characteristics and descriptors
         * for the remote device have been updated, ie new services have been discovered.
         *
         * @param gatt   GATT client invoked {@link BluetoothGatt#discoverServices}
         * @param status {@link BluetoothGatt#GATT_SUCCESS} if the remote device
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: entered");
            Log.d(TAG, "onServicesDiscovered: status: "+status);
            printMsg("onServicesDiscovered");
            super.onServicesDiscovered(gatt, status);
            if (status== BluetoothGatt.GATT_SUCCESS){
                isConnecting=false;
                stopTimer();
                updateBroadCast(BLUETOOTH_SERVICE_DISCOVERED_SUCCESS);
                mBluetoothGatt=gatt;
                Log.d(TAG, "onServicesDiscovered: runing sendcommand thread");
                sendCommandThread sendcommandThread=new sendCommandThread(bestbluetoothProtalInfo.getAccessSecret(),gatt);
                sendcommandThread.start();
//                sendCommand(gatt);
                Log.d(TAG, "onServicesDiscovered: runing timerforcommunicate thread");
                starttimer(TIMERTYPE_COMMUNICATE);
            }else if (status== BluetoothGatt.GATT_FAILURE){
                isConnecting=false;
                stopTimer();
                updateBroadCast(BLUETOOTH_SERVICE_DISCOVERED_FAILED);
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#readCharacteristic}
         * @param characteristic Characteristic that was read from the associated
         *                       remote device.
         * @param status         {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead: entered");
            Log.d(TAG, "onCharacteristicRead: status: "+status);
            super.onCharacteristicRead(gatt, characteristic, status);

        }

        /**
         * Callback indicating the result of a characteristic write operation.
         * <p>
         * <p>If this callback is invoked while a reliable write transaction is
         * in progress, the value of the characteristic represents the value
         * reported by the remote device. An application should compare this
         * value to the desired value to be written. If the values don't match,
         * the application must abort the reliable write transaction.
         *
         * @param gatt           GATT client invoked {@link BluetoothGatt#writeCharacteristic}
         * @param characteristic Characteristic that was written to the associated
         *                       remote device.
         * @param status         The result of the write operation
         *                       {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite: entered");
            Log.d(TAG, "onCharacteristicWrite: status: "+status);
            Log.d(TAG, "onCharacteristicWrite: getValue= "+characteristic.getValue());
            Log.d(TAG, "onCharacteristicWrite: isWriteFinished: " + isWriteFinished);
            printMsg("onCharacteristicWrite");
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status== BluetoothGatt.GATT_SUCCESS){
                updateBroadCast(BLUETOOTH_COMMUNICATE_SUCCESS);
                if (isWriteFinished){
                    Log.d(TAG, "onCharacteristicWrite: second write finished...");
                    isWriteFinished=true;
                    isCommunicating=false;
                    stopTimer();
                    Log.d(TAG, "onCharacteristicWrite: start TimerForLocktoreset thread");
                    disconnect();
//                    TimerForLocktoreset timerForLocktoreset=new TimerForLocktoreset(2000);
//                    timerForLocktoreset.start();
                    starttimer(TIMERTYPE_LOCKTORESET);
                    return;
                }
                return;
            }else {
                isCommunicating=false;
                stopTimer();
                disconnect();
//                TimerForLocktoreset timerForLocktoreset=new TimerForLocktoreset(2000);
//                timerForLocktoreset.start();
                starttimer(TIMERTYPE_LOCKTORESET);
                updateBroadCast(BLUETOOTH_COMMUNICATE_FAILED);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
            //super.onCharacteristicChanged(gatt, characteristic);
            updateBroadCast(BLUETOOTH_COMMUNICATE_SUCCESS);
            printMsg("onCharacteristicChanged");
            onCharacteristicChangedCount++;
            if (isReturnValueCorrect(characteristic)) {
                Log.d(TAG, "onCharacteristicRead: check return value correct");
                if (onCharacteristicChangedCount>1){
                    isCommunicating=false;
                    onCharacteristicChangedCount-=2;
                    Log.d(TAG, "onCharacteristicRead: start TimerForLocktoreset thread");
                    disconnect();
                    TimerForLocktoreset timerForLocktoreset=new TimerForLocktoreset(2000);
                    timerForLocktoreset.start();
                    return;
                }
                return;
            }
            onCharacteristicChangedCount=0;
            Log.d(TAG, "onCharacteristicRead: check return value incorrect");
            isCommunicating=false;
            Log.d(TAG, "onCharacteristicRead: start TimerForLocktoreset thread");
            disconnect();
            TimerForLocktoreset timerForLocktoreset=new TimerForLocktoreset(2000);
            timerForLocktoreset.start();
        }

        /**
         * Callback reporting the result of a descriptor read operation.
         *
         * @param gatt       GATT client invoked {@link BluetoothGatt#readDescriptor}
         * @param descriptor Descriptor that was read from the associated
         *                   remote device.
         * @param status     {@link BluetoothGatt#GATT_SUCCESS} if the read operation
         */
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }
    };

    private void updateBroadCast(int msg){
        Log.d(TAG, "updateBroadCast: entered");
        Log.d(TAG, "updateBroadCast: msg: "+msg);
        printMsg("updateBroadCast");
        Intent intent = new Intent();
        intent.setAction(BLUETOOTH_CONTROLER_BROADCAST);
        intent.putExtra("ACTION_TYPE", msg);
        status=msg;
        sendBroadcast(intent);
        switch (msg){
            case BLUETOOTH_CONNECT_OUTTIME:
            case BLUETOOTH_COMMUNICATE_OUTTIME:
                Log.d(TAG, "updateBroadCast: start recycleBleRes");
                RecycleBleRes();
                //mBluetoothAdapter.disable();
                isUnlockFinished=true;
                if (connectCount>0){
                    connectCount--;
                }
                //openByBluetooth();
                break;
            case BLUETOOTH_SCAN_OUT_TIME:
            case BLUETOOTH_DISCONNECT_FAILED:
            case BLUETOOTH_CONNECT_FAILED:
                mBluetoothAdapter.stopLeScan(leScanCallback);
                isUnlockFinished=true;
                break;
            case BLUETOOTH_LOCK_RESETFINISHED:
                isUnlockFinished=true;
        }
    }

    private class Reopendoor extends Thread {
        @Override
        public void run(){
            Log.d(TAG, "run: reopendoor thread running...");
            disconnect();
            closeGatt();
            isScanning=false;
            isConnecting=false;
            isCommunicating=false;
            timerForConnect.stop();
            timerForCommunicate.stop();
            timerForLocktoreset.stop();
            timerForScan.stop();
            //openByBluetooth();
        }
    }

    private class sendCommandThread extends Thread {
        private String key;
        private BluetoothGatt bluetoothGatt;

        sendCommandThread(String key, BluetoothGatt bluetoothGatt){
            this.key = key;
            this.bluetoothGatt = bluetoothGatt;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: sendCommandThread runing...");
            printMsg("run in sendCommandThread");
            sendCommand(bluetoothGatt);
        }
    }

    private void sendCommand(String key, BluetoothGatt bluetoothGatt){
        //完成服务连接后，进行开锁
        //写死两个uuid
        Log.d(TAG, "sendCommand: entered");
        isCommunicating=true;
        String serviceuuid = "0000fff0-0000-1000-8000-00805f9b34fb";
        String characteristicuuid = "0000fff8-0000-1000-8000-00805f9b34fb";

        UUID notifyCharacteristicUuid = UUID.fromString("0000ff7-0000-1000-8000-00805f9b34fb");

        UUID Suuid = UUID.fromString(serviceuuid);
        UUID Cuuid = UUID.fromString(characteristicuuid);
        BluetoothGattService bgservice = bluetoothGatt.getService(Suuid);//传入uuid
        BluetoothGattCharacteristic bgcharacteristic = bgservice.getCharacteristic(Cuuid);//传入对应的uuid

        BluetoothGattCharacteristic notifyusedCharacteristic = bgservice.getCharacteristic(notifyCharacteristicUuid);
        bluetoothGatt.setCharacteristicNotification(notifyusedCharacteristic, true);

        //组织数据
        byte[] value_ = new byte[20];
        value_[0] = (byte) 0x00;

//		WriteBytes = PASS_WORD_MESSAGE.getBytes();
        String secret="12345678";
        byte[] WriteBytes = secret.getBytes();
        byte [] testValue = new byte[20];//
        for (int i = 0; i < testValue.length;i++) {
            testValue[i] = 0;
        }
        testValue[0] = 0x02;
        testValue[1] = 0x48;
        for (int i = 0; i < secret.length();i++) {
            testValue[i+2] = WriteBytes[i];
        }

        bgcharacteristic.setValue(testValue);//传入数据
        Log.d(TAG, "sendCommand: setvalue1: "+bgcharacteristic.getValue());
        bluetoothGatt.writeCharacteristic(bgcharacteristic);//传入了密码


        //第二次传入数据
        //String secondtimeSUuid = "0000fff8-0000-1000-8000-00805f9b34fb";
        String secondtimeCuuid = "0000fff8-0000-1000-8000-00805f9b34fb";
        UUID secCuuid = UUID.fromString(secondtimeCuuid);
        BluetoothGattCharacteristic secbgcharacteristic = bgservice.getCharacteristic(secCuuid);

        String ORDER_OPEN_MESSAGE = "123456789123456789";
        byte[] value = new byte[20];
        value[0] = (byte) 0x00;
        WriteBytes = ORDER_OPEN_MESSAGE.getBytes();
        WriteBytes[0] = 0x02;
        WriteBytes[1] = 0x20;

        secbgcharacteristic.setValue(value[0],
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        secbgcharacteristic.setValue(WriteBytes);

        bluetoothGatt.writeCharacteristic(secbgcharacteristic);
        Log.d(TAG, "sendCommand: setvalue2: " + secbgcharacteristic.getValue());
    }

    private boolean isReturnValueCorrect(BluetoothGattCharacteristic bluetoothGattCharacteristic){
        Log.d(TAG, "isReturnValueCorrect: entered");
        printMsg("isReturnValueCorrect");
        final byte[] data,finaldata;
        Intent intent=new Intent();
        if (UUID_INFRARED_GLUCOSE_METER.equals(bluetoothGattCharacteristic.getUuid())) {// @@
            data = bluetoothGattCharacteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(
                        data.length);
                intent.putExtra("EXTRA_DATA", data);
            }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            data = bluetoothGattCharacteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(
                        data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra("EXTRA_DATA", new String(data) + "\n"
                        + stringBuilder.toString());
            }
        }
        finaldata=intent.getByteArrayExtra("EXTRA_DATA");
        Log.e("返回数据— - - - - - --》》》", finaldata[0] + finaldata[1] + finaldata[2] + "");
        if ((0xFF == finaldata[0]) && (0x41 == finaldata[1]) && (0xFF == finaldata[2])){
            updateBroadCast(BLUETOOTH_COMMUNICATE_RETURN_VALUE_INCORRECT);
            Log.d(TAG, "isReturnValueCorrect: return because BLUETOOTH_COMMUNICATE_RETURN_VALUE_CORRECT");
            return true;
        }
        if ((0xFF == finaldata[0]) && (0x21 == finaldata[1]) && (0xFF == finaldata[2])){
            updateBroadCast(BLUETOOTH_COMMUNICATE_RETURN_VALUE_INCORRECT);
            Log.d(TAG, "isReturnValueCorrect: return because BLUETOOTH_COMMUNICATE_RETURN_VALUE_CORRECT");
            return true;
        }
        else if ((0xFF == finaldata[0]) && (0x41 == finaldata[1]) && (0x00 == finaldata[2])){
            updateBroadCast(BLUETOOTH_COMMUNICATE_RETURN_VALUE_CORRECT);
            Log.d(TAG, "isReturnValueCorrect: return because BLUETOOTH_COMMUNICATE_RETURN_VALUE_INCORRECT");
            return false;
        }
            // else if(data=="FF21FF")
        Log.d(TAG, "isReturnValueCorrect: return because unexpect situation");
        return true;
    }

    public void disconnect() {
        Log.d(TAG, "disconnect: entered");
        printMsg("disconnect");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
//            updateBroadCast(BLUETOOTH_DISSCONNECT_START);
            Log.d(TAG, "disconnect: return because one of mBluetoothAdapter or mBluetoothGatt is null");
            if (connectCount>0)
                connectCount--;
            return;
        }
        mBluetoothGatt.disconnect();
        Log.d(TAG, "disconnect: returned normaly");
    }

    private void sendCommand(BluetoothGatt gatt){
        Log.d(TAG, "sendCommand: entered");
        printMsg("sendCommand");
        UUID serviceUuid = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
        UUID firstCharacteristicUuid = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb");
        UUID secondCharacteristicUuid = UUID.fromString("0000fff8-0000-1000-8000-00805f9b34fb");

        BluetoothGattService service = gatt.getService(serviceUuid);

        BluetoothGattCharacteristic firstCharacteristic = service.getCharacteristic(firstCharacteristicUuid);
        BluetoothGattCharacteristic secondCharacteristic = service.getCharacteristic(secondCharacteristicUuid);


        int firstCharaProp = firstCharacteristic.getProperties();
        if ((firstCharaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            //mNotifyCharacteristic = firstCharacteristic;
            //mBluetoothLeService.setCharacteristicNotification(firstCharacteristic, true);
        }

        int secondCharaProp = secondCharacteristic.getProperties();

        if ((secondCharaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (!isWriteFinished)
                return;
            isWriteFinished=false;
            byte[] value_ = new byte[20];
            byte[] WriteBytes = new byte[20];
            value_[0] = (byte) 0x00;

            /**
             * 初始化要发送的数据
             * */
            //WriteBytes = PASS_WORD_MESSAGE.getBytes();
            String secret="12345678";
            WriteBytes = secret.getBytes();
            byte [] testValue = new byte[20];
            for (int i = 0; i < testValue.length;i++) {
                testValue[i] = 0;
            }
            testValue[0] = 0x02;
            testValue[1] = 0x48;
            for (int i = 0; i < secret.length();i++) {
                testValue[i+2] = WriteBytes[i];
            }

            /**
             * 发送password
             */
            Log.e("WriteBytes", WriteBytes.length + "");
            secondCharacteristic.setValue(value_[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            secondCharacteristic.setValue(testValue);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // opendialog_title.setText("正在开锁");
            gatt.writeCharacteristic(secondCharacteristic);
            Log.d(TAG, "sendCommand: setValue1: " + secondCharacteristic.getValue());
            Log.e("data data ------->", "有数据有数据1");
            Log.e("data data ------->", "有数据有数据1");
            Log.e("data data ------->", "有数据有数据1");
            Log.e("data data ------->", "有数据有数据1");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BluetoothGattCharacteristic characteristic8 = secondCharacteristic;
            int charaProp = characteristic8.getProperties();

            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                //发送key
                byte[] value = new byte[20];
                value[0] = (byte) 0x00;
                WriteBytes = "  123456789123456789".getBytes();
                WriteBytes[0] = 0x02;
                WriteBytes[1] = 0x20;

                characteristic8.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                characteristic8.setValue(WriteBytes);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                gatt.writeCharacteristic(characteristic8);// 鍙戯拷锟藉紑閿佸懡锟 ?
                Log.d(TAG, "sendCommand: setvalue2: " + characteristic8.getValue());
                Log.e("data data ------->", "有数据有数据2");
                Log.e("data data ------->", "有数据有数据2");
                Log.e("data data ------->", "有数据有数据2");
                Log.e("data data ------->", "有数据有数据2"); // }

                response();
//                if (timerForLocktoreset.isAlive())
//                    timerForLocktoreset.stop();
                isWriteFinished=true;
                Log.d(TAG, "sendCommand: returned");

            }
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        public int getCalledtime(){
            return calledtime;
        }

        public void resetCalledtime(){
            calledtime = 0;
        }
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.v("onLeScan", "a le device has been discoverd!");
            Log.d(TAG, "onLeScan: entered");
            printMsg("onLeScan");
            calledtime++;
            String deviceName = device.getName();
            String address = device.getAddress();
            synchronized (scanlock){
                if(isScanning != false){
                    BluetoothDevice bdevice = device;
                    if (device!=null){
                        for(int index = 0; index < avaliableBluetoothList.size(); index++){
                            if (device==null)
                                break;
                            if(deviceName.equals(avaliableBluetoothList.get(index).getSsid())){
                                //名字相符
                                BluetoothProtalInfo info = new BluetoothProtalInfo(deviceName, avaliableBluetoothList.get(index).getAccessSecret());
                                info.setBluetoothDevice(device);
                                Log.d(TAG, "onLeScan: avaliablebluetoothlist [ " + index + " ] = " + avaliableBluetoothList.get(index).getSsid());
                                bestbluetoothProtalInfo = info;
                                //mBluetoothAdapter.stopLeScan(this);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isScanning = false;
                                        stopTimer();
                                        updateBroadCast(BLUETOOTH_SCAN_SUCCESS);
                                        if (mBluetoothAdapter==null)
                                            return;
                                        mBluetoothAdapter.stopLeScan(leScanCallback);
                                    }
                                }).start();
                                //isScanning = false;
                                Log.d(TAG, "onLeScan: start connectDevice thread...");
                            ConnectDeviceThread connectDeviceThread=new ConnectDeviceThread(device);
                            connectDeviceThread.start();
//                                ConnectDevice();
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    private class TimerForScan extends Thread {
        int wait_time;
        public TimerForScan(int wait_time){
            this.wait_time=wait_time;
        }
        @Override
        public void run(){
            Log.d(TAG, "run: timer for scan is running...");
            printMsg("TimerForScan");
            for (int i=0;i<wait_time;i+=200){
                if (stopAllTimer)
                {
                    Log.d(TAG, "run: timer for scan stop because stopalltimer is true...");
                    return;
                }
                if (!isScanning){
                    //return true;
                    if (mBluetoothAdapter==null){
                        Log.d(TAG, "run: timer for scan stop because mBluetoothAdapter is null");
                        return;
                    }
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                    updateBroadCast(BLUETOOTH_SCAN_SUCCESS);
                    isScanning=false;
                    Log.d(TAG, "run: timer for scan stop because scan success...");
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mBluetoothAdapter==null){
                Log.d(TAG, "run: timer for scan stop because mBluetoothAdapter is null...");
                return;
            }
            mBluetoothAdapter.stopLeScan(leScanCallback);
            updateBroadCast(BLUETOOTH_SCAN_OUT_TIME);
            isScanning=false;
            Log.d(TAG, "run: timer for scan stop because scan out of time...");
            return;
            //return false;
        }

    }

    private class TimerForLocktoreset extends Thread {
        int wait_time;
        public TimerForLocktoreset(int wait_time){
            this.wait_time=wait_time;
        }

        @Override
        public void run(){
            Log.d(TAG, "run: timer for lock to reset running...");
            printMsg("TimerForLocktoReset");
            //RecycleBleRes();
            for (int i=0;i<wait_time;i+=200){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //updateBroadCast(BLUETOO);
            Log.d(TAG, "run: start recycleBleRes in TimerForLocktoReset thread");
            isUnlockFinished=true;
            updateBroadCast(BLUETOOTH_LOCK_RESETFINISHED);
            Log.d(TAG, "run: TimerForLocktoReset returned");
        }
    }

    private class TimerForCommunicate extends Thread {
        int wait_time;
        public TimerForCommunicate(int wait_time){
            this.wait_time=wait_time;
        }
        @Override
        public void run(){
            Log.d(TAG, "run: TimerForCommunicate runing...");
            printMsg("run in TimerForCommunicate");
            if (stopAllTimer)
            {
                Log.d(TAG, "run: TimerForCommunicate stop because stopAllTimer is true");
                return;
            }
            for (int i=0;i<wait_time;i+=200){
                if (!isCommunicating){
                    Log.d(TAG, "run: TimerForCommunicate stop because communicate success");
                    updateBroadCast(BLUETOOTH_COMMUNICATE_SUCCESS);
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "run: TimerForCommunicate stop because communicate out of time");
            updateBroadCast(BLUETOOTH_COMMUNICATE_OUTTIME);
            return;
        }
    }

    private class TimerForConnect extends Thread {
        int wait_time;
        public TimerForConnect(int wait_time){
            this.wait_time=wait_time;
        }
        @Override
        public void run(){
            Log.d(TAG, "run: TimerForConnect running");
            printMsg("run in TimerForConnect");
            for (int i=0;i<wait_time;i+=200){
                if (stopAllTimer)
                {
                    Log.d(TAG, "run: TimerForConnect stop because stopAlltimer is true...");
                    return;
                }
                if (!isConnecting){
                    //updateBroadCast(BLUETOOTH_CONNECT_SUCCESS);
                    Log.d(TAG, "run: TimerForConnect return because connect success");
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateBroadCast(BLUETOOTH_CONNECT_OUTTIME);
            Log.d(TAG, "run: TimerForConnect stop because connect out of time");
            return;
        }
    }

    private void closeGatt(){
        Log.d(TAG, "closeGatt: entered");
        printMsg("closeGatt");
        if (mBluetoothGatt == null) {
            Log.d(TAG, "closeGatt: returned because mBluetoothGatt is null");
            return;
        }
        mBluetoothGatt.close();
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        TimerForLocktoreset timerForLocktoreset=new TimerForLocktoreset(2000);
//        timerForLocktoreset.start()
//        starttimer(TIMERTYPE_LOCKTORESET);
        mBluetoothGatt=null;
        connectCount--;
        Log.d(TAG, "closeGatt: returned normaly");
        //mBluetoothGatt = null;
    }

    public void RecycleBleRes(){
        Log.d(TAG, "RecycleBleRes: entered");
        printMsg("RecycleBleRes");
        stopAllTimer=true;
        disconnect();
        starttimer(TIMERTYPE_LOCKTORESET);
        //closeGatt();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printMsg(String functionName){
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                .getStackTrace();
        int index=3;

        System.out.println("The stackTraceElements length: "
                + stackTraceElements.length);
//        for (int i = 0; i < stackTraceElements.length; ++i) {
        Log.d(TAG, "printMsg in " + functionName + " : ----  the " + index + " element  ----");
        Log.d(TAG, "printMsg in " + functionName + " : ThreadID: " + Thread.currentThread().getId());
        Log.d(TAG, "printMsg in " + functionName + "  toString: " + stackTraceElements[index].toString());
        Log.d(TAG, "printMsg in " + functionName + "  ClassName: "
                + stackTraceElements[index].getClassName());
        Log.d(TAG, "printMsg in " + functionName + "  FileName: "
                + stackTraceElements[index].getFileName());
        Log.d(TAG, "printMsg in " + functionName + "  LineNumber: "
                + stackTraceElements[index].getLineNumber());
        Log.d(TAG, "printMsg in " + functionName + "  MethodName: "
                + stackTraceElements[index].getMethodName());
//        }
    }

    private void starttimer(int timertype){
        if (mtimer!=null)
            stopTimer();
        switch (timertype){
            case TIMERTYPE_SCAN:
                mtimer=new Timer(true);
                mtimer.schedule(timerTaskForScan,0,TIMER_SPACING);
                break;
            case TIMERTYPE_CONNECT:
                mtimer=new Timer(true);
                mtimer.schedule(timerTaskForConnect,0,TIMER_SPACING);
                break;
            case TIMERTYPE_COMMUNICATE:
                mtimer=new Timer(true);
                mtimer.schedule(timerTaskForCommunicate,0,TIMER_SPACING);
                break;
            case TIMERTYPE_LOCKTORESET:
                mtimer=new Timer(true);
                mtimer.schedule(timerTaskForLocktoreset,0,TIMER_SPACING);
                break;
        }
    }

    private void stopTimer(){
        mtimer.cancel();
        timercounter=0;
    }

    private NotificationManager mgr;
    private Notification nt;
    int soundId;
    //开锁响应
    public void response(){
        if (mgr==null){
            mgr = (NotificationManager) BluetoothControler.this
                    .getSystemService(BluetoothControler.NOTIFICATION_SERVICE);
            nt = new Notification();
            nt.defaults = Notification.DEFAULT_SOUND;
            soundId = new Random(System.currentTimeMillis())
                    .nextInt(Integer.MAX_VALUE);
        }
        mgr.notify(soundId, nt);
        Vibrator vibrator = (Vibrator) getSystemService(BluetoothControler.this.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

//    private TimerTask timerForScan = new TimerTask() {
//        @Override
//        public void run() {
//
//        }
//    }
}
