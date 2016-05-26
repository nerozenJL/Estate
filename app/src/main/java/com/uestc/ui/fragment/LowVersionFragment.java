package com.uestc.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.domain.Session;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.service.ShakeSensorService;
import com.uestc.service.WifiUnlocker;
import com.uestc.ui.activity.BillActivity;
import com.uestc.ui.activity.MyComplainListActivity;
import com.uestc.ui.activity.MyRepairListActivity;
import com.uestc.ui.activity.R;
import com.uestc.ui.activity.TiedOwnersActivity;

import java.util.List;

/**
 * Created by Ryon on 2015/11/13.
 * email:shadycola@gmail.com
 * 这是低版本wifi开锁按钮的fragment
 */

public class LowVersionFragment extends Fragment {
    private View view;
    private ImageButton oneKey;
    private Dialog dialog;
    final static int GARDEN = 0;
    final static int BUILDING = 1;
    final static int CARLOCK = 2;
    private LinearLayout complain,repair,pay;
    private LinearLayout opendoor, opendoor2;
    private TextView opendoorText;
    private int openstatus; //1表示开始，2表示结束
    private ImageView imageview;
    private static String TAG="LowVersionFragment";


    /*呼叫物业*/
	/*2016/4/7 呼叫物业管理*/
    private LinearLayout calloutPropertyManagerLayout;
    private List<String> propertyManagementCompanyPhoneNumberList = TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList;
    private ArrayAdapter<String> phoneNumberListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            try{
                view = inflater.inflate(R.layout.low_fragment, container, false);
            }catch (InflateException ie) {
                ie.printStackTrace();
            }finally{
                if(view == null)
                    return view;
            }
            openstatus=2;
            complain=(LinearLayout) view.findViewById(R.id.ib_complain_housekeeper_wifi);
            repair=(LinearLayout) view.findViewById(R.id.ib_replair_housekeeper_wifi);
            pay = (LinearLayout) view.findViewById(R.id.ib_pay_housekeeper_wifi);
            opendoorText=(TextView)view.findViewById(R.id.wifi_open_text);
            imageview=(ImageView)view.findViewById(R.id.wifi_open_imageview);

            complain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (openstatus==2){
                        if(IsAccessableToHouseKeeperService()){//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
                            Intent intentToMyComplainListActivity = new Intent(getActivity(), MyComplainListActivity.class);
                            startActivity(intentToMyComplainListActivity);
                        }
                        else{
                            Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
                            Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
                            intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
                            startActivity(intentToTiedOwnerActivity);
                        }
                    }

                }
            });
            repair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
					/*Intent intent2 = new Intent(getActivity(), MyRepairListActivity.class);
					startActivity(intent2);*/
                    printMsg("repair.setOnClickListener");
                    if (openstatus==2) {
                        if (IsAccessableToHouseKeeperService()) {//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
                            Intent intentToMyRepairListActivity = new Intent(getActivity(), MyRepairListActivity.class);
                            startActivity(intentToMyRepairListActivity);
                        } else {
                            Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
                            Intent intentToTiedOwnerActivity = new Intent(getActivity(), TiedOwnersActivity.class);
                            intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
                            startActivity(intentToTiedOwnerActivity);
                        }
                    }
                }
            });
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
					/*Intent intent = new Intent(getActivity(),
							BillActivity.class);
					startActivity(intent);*/
                    if (openstatus==2){
                        if(IsAccessableToHouseKeeperService()){//判断用户是否有足够的角色权限去使用管家功能，如果不够，跳转到绑定物业界面
                            Intent intentToMyRepairListActivity = new Intent(getActivity(), BillActivity.class);
                            startActivity(intentToMyRepairListActivity);
                        }
                        else{
                            Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
                            Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
                            intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
                            startActivity(intentToTiedOwnerActivity);
                        }
                    }
                }
            });

            opendoor = (LinearLayout) view.findViewById(R.id.ib_open_park_low_open);
            /**
             * 开园区门
             *
             */
            opendoor.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (openstatus==2){
                        if(IsAccessableToUnLockProtal()){
//                        Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                        intent.putExtra("category", 1);
//                        startActivity(intent);
                            opendoorText.setText("开锁流程启动");
                            opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_pressed));
                            imageview.setImageResource(R.drawable.wifi_unlock_garden_pressed);
                            Intent intent=new Intent(getActivity(),WifiUnlocker.class);
                            intent.setAction("start_wifi_unlock");
                            intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.UNLOCKDOOR);
                            getActivity().startService(intent);
                            //Color color=new Color(#ec7731);
//                        Truntounpressedthread truntounpressedthread=new Truntounpressedthread();
//                        truntounpressedthread.start();
                        }
                        else{
                            Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
                            Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
                            intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
                            startActivity(intentToTiedOwnerActivity);
                        }
                    }
                }
            });

            IntentFilter filter = new IntentFilter(WifiUnlocker.WIFIUNLOCK_BROADCAST_ACTION);
            getActivity().registerReceiver(getOpenStatusBroadcastreceiver, filter);

            //opendoor2 = (LinearLayout) view.findViewById(R.id.ib_open_build_low_open);
            /**
             * 楼栋按钮蓝牙开门
             */
//            opendoor2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    if(IsAccessableToHouseKeeperService()) {
//                        Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                        intent.putExtra("category", 2);
//                        startActivity(intent);
//                    }
//                    else{
//                        Toast.makeText(getActivity(), "要使用该功能，请先绑定物业", Toast.LENGTH_SHORT).show();
//                        Intent intentToTiedOwnerActivity = new Intent(getActivity(),TiedOwnersActivity.class);
//                        intentToTiedOwnerActivity.putExtra("fromwhichActivity", "HomeActivity");//告知跳转的目标页面，是智能通页面触发的跳转
//                        startActivity(intentToTiedOwnerActivity);
//                    }
//                }
//            });
            /**
             *车闸按钮蓝牙开门
             **/
//			opendoor3= (ImageButton) view.findViewById(R.id.ib_open_car_high_open);
//
//			opendoor3.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					bluetooth_name = "YCWY_C";
//					myBluetoothExecute();
//				}
//			});


            //160307新增
            //setImageButtonBitMap(true);

            /**
             * 这是保证在返回到开门界面，status=3时，两个按钮可用点击
             */
//            oneKey=(ImageButton) view.findViewById(R.id.one_key);
//            oneKey.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
////                    Intent intent=new Intent(getActivity(), LowOpendoorWay.class);
////                    startActivity(intent);
////                    setDialog();
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setIcon(R.drawable.ic_launcher);
//                    builder.setTitle("请选择开锁对象");
//                    //    指定下拉列表的显示数据
//                    final String[] openWays = {"园区开锁", "楼栋开锁", "车闸开锁"};
//                    //    设置一个下拉的列表选择项
//                    builder.setItems(openWays, new DialogInterface.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which)
//                        {
//                            Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                            intent.putExtra("category", which);
//                            startActivity(intent);
//                        }
//                    });
//                    builder.show();
//                }
//            });

            /*2016/4/7新增呼叫物业*/
            calloutPropertyManagerLayout = (LinearLayout)view.findViewById(R.id.ib_call_out_property_manager_wifi);
            //propertyManagementCompanyPhoneNumberList.add("123");
            //propertyManagementCompanyPhoneNumberList.add("456");
            phoneNumberListAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item);
            //phoneNumberListAdapter.add("18680237011"); // 测试用的电话，正式使用时请注释

            if(propertyManagementCompanyPhoneNumberList.size() == 0){
                phoneNumberListAdapter.add("暂无绑定的物业电话");
            }else{
                for(int index = 0; index < propertyManagementCompanyPhoneNumberList.size(); index++){
                    phoneNumberListAdapter.add(propertyManagementCompanyPhoneNumberList.get(index));
                }
            }


            calloutPropertyManagerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
					/*创建一个列表对话框，将获得的电话号码展示，并给每个列表元素添加对应拨号操作*/
                    AlertDialog.Builder propertyManagerPhoneNumbersList = new AlertDialog.Builder(getActivity());
                    propertyManagerPhoneNumbersList.setTitle("请点击呼叫物业电话:");

                    propertyManagerPhoneNumbersList.setAdapter(phoneNumberListAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("phone enum", "电话触发");
                            Intent dialIntent = new Intent();
                            dialIntent.setAction(Intent.ACTION_CALL);
                            dialIntent.setData(Uri.parse("tel:" + phoneNumberListAdapter.getItem(which).toString()));
                            Context context = LowVersionFragment.this.getActivity();
                            LowVersionFragment.this.getActivity().startActivity(dialIntent);
                        }
                    });
                    propertyManagerPhoneNumbersList.create().show();
                }
            });


        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

//    private void setImageButtonBitMap(boolean ifsuccess){
//        if (ifsuccess){
//            Bitmap unlockBitmap;
//            InputStream is=view.getResources().openRawResource(R.raw.building_unlock_true);
//            unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//            opendoor2.setImageBitmap(unlockBitmap);
//            is=view.getResources().openRawResource(R.raw.garden_unlock_true);
//            unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//            opendoor.setImageBitmap(unlockBitmap);
////			is=view.getResources().openRawResource(R.raw.car_unlock_true);
////			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
////			opendoor3.setImageBitmap(unlockBitmap);
//            is = view.getResources().openRawResource(R.raw.complain);
//            Bitmap b = MyBitmapFactory.decodeRawBitMap(is);
//            complain.setImageBitmap(b);
//
//            is = view.getResources().openRawResource(R.raw.complair);
//            b = MyBitmapFactory.decodeStream(is);
//            repair.setImageBitmap(b);
//
//            is = view.getResources().openRawResource(R.raw.pay);
//            b = MyBitmapFactory.decodeStream(is);
//            pay.setImageBitmap(b);
//        }else {
//            Bitmap unlockBitmap;
//            InputStream is=view.getResources().openRawResource(R.raw.building_unlock_false);
//            unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//            opendoor2.setImageBitmap(unlockBitmap);
//            is=view.getResources().openRawResource(R.raw.garden_unlock_false);
//            unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
//            opendoor.setImageBitmap(unlockBitmap);
////			is=view.getResources().openRawResource(R.raw.car_unlock_false);
////			unlockBitmap= MyBitmapFactory.decodeRawBitMap(is);
////			opendoor3.setImageBitmap(unlockBitmap);
//        }
//    }

    private boolean IsAccessableToHouseKeeperService(){
        boolean isAccessable = true;
        switch(Session.getRole()){
            case 0:
                isAccessable = false;
                break;
            case -1:
                isAccessable = false;
                break;
            default:
                isAccessable = true;
                break;
        }
        return isAccessable;
    }
    private boolean IsAccessableToUnLockProtal(){
        boolean isAccessable = true;
        switch(Session.getRole()){
            case -1:
                isAccessable = false;
                break;
            default:
                isAccessable = true;
                break;
        }
        return isAccessable;
    }

//    @Override
//    public void onResume(){
//        if (openstatus==1){
//            opendoorText.setText("一键开锁");
//            opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
//        }else if (openstatus==2){
//            opendoorText.setText("正在开锁");
//            opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_pressed));
//        }
//    }



    private BroadcastReceiver getOpenStatusBroadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receive_msg=intent.getIntExtra("ACTION_TYPE",-1);
            switch (receive_msg){
                case WifiUnlocker.UNLOCKE_BLOCKED:
                    opendoorText.setText("开锁频繁\n点击重试");
//                    TimerForResetButton timerForResetButton=new TimerForResetButton();
//                    timerForResetButton.start();
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.WIFI_OPENING:
                    opendoorText.setText("启动WiFi\n请稍后...");
                    break;
                case WifiUnlocker.ENABLE_WIFI_BY_PRPGRAME:
                    opendoorText.setText(getResources().getString(R.string.textview_text_Enable_wifi_by_program));
                    break;
                case WifiUnlocker.ENABLE_WIFI_FAILED_BY_LACK_PERMISSION:
                    opendoorText.setText(getResources().getString(R.string.textview_text_Enable_wifi_failed_by_lack_permission));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.ENABLE_WIFI_OUTTIME:
                    opendoorText.setText(getResources().getString(R.string.textview_text_Enable_wifi_outtime));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.CONNECT_WIFI_START:
                    opendoorText.setText(getResources().getString(R.string.textview_text_connect_wifi_start));
                    break;
                case WifiUnlocker.CONNECT_WIFI_FAILED:
                    opendoorText.setText(getResources().getString(R.string.textview_text_connect_failed));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.CONNECT_WIFI_OUTTIME:
                    opendoorText.setText(getResources().getString(R.string.textview_text_Enable_wifi_outtime));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.CONNECT_WIFI_SUCCESS:
                    opendoorText.setText(getResources().getString(R.string.textview_text_connect_success));
                    break;
                case WifiUnlocker.SCAN_WITH_NO_RESULT:
                    opendoorText.setText(getResources().getString(R.string.textview_text_scan_noresult));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.SCAN_GET_RESULT:
                    String s="检测到\n"+intent.getStringExtra("VALUE");
                    opendoorText.setText(s);
                    break;
                case WifiUnlocker.SOCKET_COMMUNATE_START:
                    opendoorText.setText(getResources().getString(R.string.textview_text_unlock_start));
//                    opendoor.setEnabled(true);
//                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
//                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.SOCKET_COMMUNATE_FAILED:
                    opendoorText.setText(getResources().getString(R.string.textview_text_unlock_failed));
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.SOCKET_COMMUNATE_SUCCESS:
                    opendoorText.setText(getResources().getString(R.string.textview_text_unlock_success));
//                    opendoor.setEnabled(true);
//                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
//                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
                    break;
                case WifiUnlocker.LOCK_RESET_FINISHED:
                    opendoorText.setText("一键开锁");
                    opendoor.setEnabled(true);
                    opendoor.setBackgroundColor(getResources().getColor(R.color.wifi_open_unpressed));
                    imageview.setImageResource(R.drawable.wifi_unlock_garden);
            }
        }
    };

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
//    private void setDialog() {
//
//        LowOpendoorDialog.Builder builder = new LowOpendoorDialog.Builder(getActivity());
//
//        builder.setGardenButton(new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                //设置你的操作事项
//                Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                intent.putExtra("category", GARDEN);
//                startActivity(intent);
//            }
//        });
//
//        builder.setCarlockButton(
//                new android.content.DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                        intent.putExtra("category", CARLOCK);
//                        startActivity(intent);
//                    }
//                });
//
//        builder.setBuildingButton(
//                new android.content.DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(getActivity(), LowOpenActivity.class);
//                        intent.putExtra("category", BUILDING);
//                        startActivity(intent);
//                    }
//                });
//
//        builder.create().show();
//    }
}
