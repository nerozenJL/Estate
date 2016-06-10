package com.uestc.ui.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.uestc.base.ActivityCollector;
import com.uestc.domain.ADInfo;
import com.uestc.domain.Announcement;
import com.uestc.domain.GetAnnouncementData;
import com.uestc.domain.Picture;
import com.uestc.domain.Session;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.domain.WiFiProtalInfo;
import com.uestc.service.BluetoothControler;
import com.uestc.service.ShakeSensorService;
import com.uestc.service.WifiUnlocker;
import com.uestc.ui.dialog.DepartmentSwicherDialog;
import com.uestc.ui.dialog.ListViewDialog;
import com.uestc.ui.fragment.HighVersionFragment;
import com.uestc.ui.fragment.LowVersionFragment;
import com.uestc.constant.Host;
import com.uestc.ui.fragment.StorePreviewFragment;
import com.uestc.ui.fragment.StoreWebViewFragment;
import com.uestc.utils.HttpUtils;
import com.uestc.utils.ViewFactory;
import com.uestc.ui.view.viewpager.CycleViewPager;
import com.uestc.ui.view.viewpager.CycleViewPager.ImageCycleViewListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 这是登录一进来的界面
 */

public class HomeActivity extends Activity {

	public static boolean  ifWifiUsable,ifShakeboxusable;
	private RadioGroup raGroup;
	private RadioButton meRadioButton,homeRadioButton,shopRadioButton,switchRadioButton;
	private HighVersionFragment homeFragement;
	private LowVersionFragment lowFragement;
	private StoreWebViewFragment storeWebViewFragment;
	private android.app.FragmentTransaction begintTransaction;
	private android.app.FragmentManager fragmentManager;
	private Picture picture;
	private List<String> imageUrls;
	private CycleViewPager cycleViewPager,belowcycleViewPager;
	private List<ADInfo> infos = new ArrayList<ADInfo>();
	private List<ImageView> views = new ArrayList<ImageView>();
	private TextView gonggao;
	private ImageButton more;
	private Announcement announcement;
	private List<Announcement> list;
	private static boolean isExit = false;
	public static AlertDialog wifiAlertdialog;
	public static HomeActivity instance;
	public static long lastClickTime=-1;
	private static int fragmentstatus;	//1为lowfragment，2为highfragment
	private static int wifiopenstaus;		//1为正在开锁，2为开锁结束
	LinearLayout announcementLinearlayout;
	//StorePreviewFragment galaryPreview;
	RelativeLayout ifragment,storelayout;
	WebView storeWebView;
	RelativeLayout belowadfragment;
	private LinearLayout homeLinearLayout;
	public int switchrecord=0;
	public static boolean isAPIVersionRight=true;
	public static int dialogstatus;

	private static String TAG="HomeActivity";

	private CycleViewPager storeInfoPreviewGallary; //

	private List<ImageView> storePreviewPicsList = new ArrayList<ImageView>();
	private List<ADInfo> adiInfoList = TempStaticInstanceCollection.storePreviewInfo;

	//private ChangefragmentHandler changefragmentHandler;
	//private Content mContent;
//	private ImageView tView;
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance=this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_homeactivity);
		ActivityCollector.addActivity(this);
		list = new ArrayList<Announcement>();
		initView();
		imageUrls = new ArrayList<String>();
		picture = new Picture();
		imageUrls = picture.getPicture();
//		ifWifiUsable=false;

		if (Session.getUnlockMode()==1){
			ifWifiUsable=true;
		}else{
			ifWifiUsable=false;
		}
		//homeactivitytitle=(RelativeLayout)findViewById(R.id.rl_title_homeactivity);
		cycleViewPager = (CycleViewPager) getFragmentManager()
				.findFragmentById(R.id.fragment_cycle_viewpager_content);
		belowcycleViewPager=(CycleViewPager)getFragmentManager().findFragmentById(R.id.gallary_store_preview);
		configImageLoader();
		initialize();
		homeFragement = new HighVersionFragment();
		lowFragement = new LowVersionFragment();
		storeWebViewFragment=new StoreWebViewFragment();
		announcementLinearlayout=(LinearLayout)findViewById(R.id.ll_announcement_homeactivity);
		ifragment=(RelativeLayout)findViewById(R.id.ifragement);
		//storeWebView=(WebView)findViewById(R.id.webview_storeinfo);
		homeLinearLayout=(LinearLayout)findViewById(R.id.home_linearlayout);
		storelayout=(RelativeLayout)findViewById(R.id.store_context);

		getFragmentManager().beginTransaction().add(R.id.store_context,storeWebViewFragment).commit();
		getFragmentManager().beginTransaction().hide(storeWebViewFragment).commit();

		fragmentManager = getFragmentManager();
		begintTransaction = fragmentManager.beginTransaction();
		raGroup = (RadioGroup) findViewById(R.id.radioGroup_homeactivity);
		if(ifWifiUsable){
			begintTransaction.add(R.id.ifragement, lowFragement);
			begintTransaction.commit();
			fragmentstatus=1;
		}else {
			begintTransaction.add(R.id.ifragement, homeFragement);
			begintTransaction.commit();
			fragmentstatus=2;
		}
		ifShakeboxusable=Session.isShakingUnlockAble();

		if (getAndroidSDKVersion()<18){
			ifWifiUsable=true;
			isAPIVersionRight=false;
		}

		IntentFilter filter = new IntentFilter(OpendoorActivity.action);
		registerReceiver(broadcastReceiver, filter);

		//changefragmentHandler=new ChangefragmentHandler();

		raGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
					case R.id.rb_store_homeactivity:
						homeLinearLayout.setVisibility(View.INVISIBLE);
						getFragmentManager().beginTransaction().show(storeWebViewFragment).commit();
						shopRadioButton.setTextColor(getResources().getColor(R.color.style));
						homeRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						meRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						switchrecord=2;
						break;
					case R.id.rb_me_homeactivity:
						//new HanderAsyncTask().execute();
						getME();
						meRadioButton.setTextColor(getResources().getColor(R.color.style));
						meRadioButton.setChecked(false);
						homeRadioButton.setChecked(true);
						switchrecord=1;
						break;
					case R.id.rb_switch_homeactivity:
						final ListViewDialog.Builder builder=new ListViewDialog.Builder(HomeActivity.this);
						builder.setListItemName(Session.gardenHostMap);
						builder.setOnConfirmButtonClickListner(new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//builder.
								String key = builder.getKey();
								String hostUrl = (String)Session.gardenHostMap.get(key);
								String dbName = (String)Session.gardenDBNameMap.get(key);
								Host.DynamiclySetAPIs(hostUrl, dbName);
								dialog.dismiss();
								switchRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
								if (switchrecord==1){
									homeRadioButton.setChecked(true);
									homeRadioButton.setTextColor(getResources().getColor(R.color.style));
								}else {
									shopRadioButton.setChecked(true);
									shopRadioButton.setTextColor(getResources().getColor(R.color.style));
								}
							}
						});
						builder.setOnBackPressedListener(new ListViewDialog.OnBackPressedListener() {
							@Override
							public void onClick() {
								switchRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
								if (switchrecord==1){
									homeRadioButton.setChecked(true);
									homeRadioButton.setTextColor(getResources().getColor(R.color.style));
								}else {
									shopRadioButton.setChecked(true);
									shopRadioButton.setTextColor(getResources().getColor(R.color.style));
								}
							}
						});
						shopRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						homeRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						meRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						switchRadioButton.setTextColor(getResources().getColor(R.color.style));
						builder.create(0).show();
						break;
					case R.id.rb_home_homeactivity: {
						//new HanderAsyncTask().execute();
						homeLinearLayout.setVisibility(View.VISIBLE);
						getFragmentManager().beginTransaction().hide(storeWebViewFragment).commit();
						switchrecord=1;
						shopRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						homeRadioButton.setTextColor(getResources().getColor(R.color.style));
						meRadioButton.setTextColor(getResources().getColor(R.color.rb_unpressed));
						if(ifWifiUsable&&fragmentstatus==2){
//								getFragmentManager().beginTransaction()
//										.replace(R.id.ifragement, lowFragement).commit();

							switchContent(homeFragement, lowFragement);
							fragmentstatus=1;
						}else if (!ifWifiUsable&&fragmentstatus==1){
//								getFragmentManager().beginTransaction()
//										.replace(R.id.ifragement, homeFragement).commit();
							switchContent(lowFragement,homeFragement);
							fragmentstatus=2;
						}
					}
					break;

					default:
						break;
				}
			}
		});

		//160307新增
		//InputStream inputStream=this.getResources().openRawResource(R.raw)
		changeOpentype();

		BackgrandThread backgrandThread=new BackgrandThread();
		backgrandThread.start();


		/*2016/4/7*/

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);

	}

	private void getME(){
		if (!Session.getSeesion().equals("")) {
			switch (Session.getRole()) {
				case -1:
//					Intent intent = new Intent(HomeActivity.this,
//							TiedOwnersActivity.class);
//					startActivity(intent);
					//new GetRoleTask().execute();
					Intent intent=new Intent(HomeActivity.this,MeActivity3.class);
					startActivity(intent);
					break;
				case 0:
					Intent intent0 = new Intent(HomeActivity.this,MeActivity.class);
					startActivity(intent0);
					break;
				case 1:
					Intent intent1 = new Intent(HomeActivity.this,
							MeActivity.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(HomeActivity.this,
							MeActivity.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(HomeActivity.this,
							MeActivity2.class);
					startActivity(intent3);
					break;

				default:
					break;
			}
		}else{
			Intent intent = new Intent("com.example.broadcastpractice.FORCE_OFFLINE");
			//发送广播--标准广播
			sendBroadcast(intent);
		}

	}


	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		};
	};

	@Override
	public void onBackPressed() {
		if (isExit != true) {
			isExit = true;
			Toast.makeText(this, "再按一次后退键退出", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			if (ShakeSensorService.isSensorRuning&&HomeActivity.ifWifiUsable==true){
				Intent intent=new Intent(HomeActivity.this,WifiUnlocker.class);
				intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.CLOSE_SHAKE_FUNCTION);
			}else if (ShakeSensorService.isSensorRuning&&HomeActivity.ifWifiUsable==false){
				Intent intent=new Intent(HomeActivity.this,WifiUnlocker.class);
				intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.CLOSE_SHAKE_FUNCTION);
			}

			TempStaticInstanceCollection.storePreviewInfo = new ArrayList<ADInfo>();
			TempStaticInstanceCollection.propertyManagerCompanyPhoneNumberList = new ArrayList<String>();
			TempStaticInstanceCollection.announcementList = null;

			unregisterReceiver(broadcastReceiver);
			Intent intent1 = new Intent(HomeActivity.this,ShakeSensorService.class);
			stopService(intent1);
			Intent intent2 = new Intent(HomeActivity.this,WifiUnlocker.class);
			stopService(intent2);
			Intent intent3 = new Intent(HomeActivity.this,BluetoothControler.class);
			stopService(intent3);

			finish();
			System.exit(0);
		}

	}

	private void initView() {
		meRadioButton = (RadioButton) findViewById(R.id.rb_me_homeactivity);
		homeRadioButton = (RadioButton) findViewById(R.id.rb_home_homeactivity);
		shopRadioButton=(RadioButton)findViewById(R.id.rb_store_homeactivity);
		switchRadioButton=(RadioButton)findViewById(R.id.rb_switch_homeactivity);
		more = (ImageButton) findViewById(R.id.ib_more_homeactivity);
		more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeActivity.this,
						AnnouncementListActivity.class);
				startActivity(intent);
			}
		});
//		tView = (ImageView) findViewById(R.id.verpass1);
//		tView.setBackgroundResource(R.drawable.hometitle);
		gonggao = (TextView) findViewById(R.id.tv_first_announcementrl_homeactivity);
		gonggao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (list.size() == 0) {

					Toast.makeText(HomeActivity.this, "暂无公告",
							Toast.LENGTH_SHORT).show();
				} else {
					announcement = new Announcement();
					announcement = list.get(0);
					Intent intent = new Intent(HomeActivity.this,
							AnnouncementActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("activity_announcementlist", announcement);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			}
		});
		new HanderAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//new HanderAsyncTask().execute();
		if (switchrecord==1){
			meRadioButton.setChecked(false);
			homeRadioButton.setChecked(true);
		}else if (switchrecord==2){
			//meRadioButton.setChecked(true);
			homeRadioButton.setChecked(false);

		}
		if(ifWifiUsable&&fragmentstatus!=1){
//								getFragmentManager().beginTransaction()
//										.replace(R.id.ifragement, lowFragement).commit();
			switchContent(homeFragement,lowFragement);
			fragmentstatus=1;
		}else if(!ifWifiUsable&&fragmentstatus!=2){
//								getFragmentManager().beginTransaction()
//										.replace(R.id.ifragement, homeFragement).commit();
			switchContent(lowFragement,homeFragement);
			fragmentstatus=2;
		}
//		if(ifWifiUsable){
//			getFragmentManager().beginTransaction()
//					.replace(R.id.ifragement, lowFragement).commit();
//		}else {
//			getFragmentManager().beginTransaction()
//					.replace(R.id.ifragement, homeFragement).commit();
//		}
	}

	private void initialize() {
		Log.e("", "");
		for (int i = 0; i < imageUrls.size(); i++) {
			ADInfo info = new ADInfo();
			info.setUrl(imageUrls.get(i));
			info.setContent("ͼƬ-->" + i);
			infos.add(info);
		}


		views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1)
				.getUrl()));
		for (int i = 0; i < infos.size(); i++) {
			views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
		}

		views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));


		cycleViewPager.setCycle(true);


		cycleViewPager.setData(views, infos, mAdCycleViewListener);

		cycleViewPager.setWheel(true);


		cycleViewPager.setTime(2000);

		cycleViewPager.setIndicatorCenter();

		this.storeInfoPreviewGallary =
				(CycleViewPager)this.getFragmentManager().findFragmentById(R.id.gallary_store_preview);

		if (adiInfoList.size()>0){
			storePreviewPicsList.add(ViewFactory.getImageView(HomeActivity.this, adiInfoList.get(adiInfoList.size() - 1).getUrl()));
			for (int i = 0; i < adiInfoList.size(); i++) {
				storePreviewPicsList.add(ViewFactory.getImageView(HomeActivity.this, adiInfoList.get(i).getUrl()));
			}
			storePreviewPicsList.add(ViewFactory.getImageView(HomeActivity.this, adiInfoList.get(0).getUrl()));
			storeInfoPreviewGallary.setCycle(true);
			storeInfoPreviewGallary.setData(storePreviewPicsList, adiInfoList, new onSinglePictureClickedListener());
			storeInfoPreviewGallary.setWheel(true);
			storeInfoPreviewGallary.setTime(2000);
			storeInfoPreviewGallary.setIndicatorCenter();
		}
	}

	private class onSinglePictureClickedListener implements CycleViewPager.ImageCycleViewListener {
		@Override
		public void onImageClick(ADInfo info, int postion, View imageView) {
			//跳转进入商品详细页面webview
			homeLinearLayout.setVisibility(View.INVISIBLE);
			getFragmentManager().beginTransaction().show(storeWebViewFragment).commit();
			shopRadioButton.setChecked(true);
		}
	}

	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {
			if (cycleViewPager.isCycle()) {
				position = position - 1;
//				Toast.makeText(HomeActivity.this,
//						"position-->" + info.getContent(), Toast.LENGTH_SHORT)
//						.show();
			}

		}

	};

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ifWifiUsable=intent.getExtras().getBoolean("ifWifiUsable");
//			if(ifWifiUsable){
//				Message ifWifiUsablemsg=changefragmentHandler.obtainMessage();
//				ifWifiUsablemsg.arg1=1;
//				changefragmentHandler.sendMessage(ifWifiUsablemsg);
//			}else {
//				Message ifWifiUsablemsg=changefragmentHandler.obtainMessage();
//				ifWifiUsablemsg.arg1=2;
//				changefragmentHandler.sendMessage(ifWifiUsablemsg);
//			}
		}
	};

	private void configImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.icon_stub)
				.showImageForEmptyUri(R.drawable.icon_empty)
				.showImageOnFail(R.drawable.icon_error)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(options)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	private class HanderAsyncTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... arg0) {

//			String jsonString = HttpUtils.HttpGet(HomeActivity.this,
//					Host.getNotice1);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
//			if (!result.equals("")) {
				GetAnnouncementData getAnnouncementData;
				getAnnouncementData = TempStaticInstanceCollection.announcementList;
				if (getAnnouncementData.getList()==null){
					gonggao.setText("暂无公告");
					return;
				}
				if (getAnnouncementData.isStatus()) {
					if (getAnnouncementData.getList().size() == 0) {
						gonggao.setText("暂无公告");
					} else {
						list = getAnnouncementData.getList();
						gonggao.setText(list.get(0).getTitle());
					}
				} else {
					Toast.makeText(HomeActivity.this,
							getAnnouncementData.getDescription(),
							Toast.LENGTH_SHORT).show();
				}
//			} else {
//				Toast.makeText(HomeActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
//						.show();
//			}
		}
	}

	class BackgrandThread extends Thread{
		@Override
		public void run(){
			boolean isRuninfore=true;
			for (;;){
				if (!isRunningForeground(HomeActivity.this)&&isRuninfore==true&&ShakeSensorService.isSensorRuning==true){
					Intent intent=new Intent(HomeActivity.this,ShakeSensorService.class);
					intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.CLOSE_SENSOR);
					startService(intent);
					isRuninfore=false;
//					changeOpentype();
//					isRuninfore=false;
				}
				else if (isRunningForeground(HomeActivity.this)&&isRuninfore==false&&ShakeSensorService.isSensorRuning==false&&Session.isShakingUnlockAble()){
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

	class WifiAlertDialogThread extends Thread{
		@Override
		public void run(){

		}
	}

	private class GetRoleTask extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... voids) {

			String jsonString = HttpUtils.HttpGet(HomeActivity.this,
					Host.getRole);

			return jsonString;

		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			if (!s.equals("")){
				JSONObject object;
				try {
					object = new JSONObject(s);
					boolean status = object.getBoolean("status");
					if (status){
						String roles = object.getString("jsonString");
						int maxRole=0;
						for (String roleString: Arrays.asList(roles.split(",")))
						{
							int role=Integer.parseInt(roleString);
							if (role>maxRole)
								maxRole=role;
						}

						Session.setRole(maxRole);
						switch (Session.getRole()) {
							case 0:
								Intent intent = new Intent(HomeActivity.this, MeActivity3.class);
								startActivity(intent);

								break;
							case 1:
								Intent intent1 = new Intent(HomeActivity.this,
										MeActivity.class);
								startActivity(intent1);
								break;
							case 2:
								Intent intent2 = new Intent(HomeActivity.this,
										MeActivity.class);
								startActivity(intent2);
								break;
							case 3:
								Intent intent3 = new Intent(HomeActivity.this,
										MeActivity2.class);
								startActivity(intent3);
								break;

							default:
								break;
						}

					}else{
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(HomeActivity.this,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {

				}


			}else{
				Toast.makeText(HomeActivity.this, "检查网络设置", Toast.LENGTH_SHORT)
						.show();
			}


		}
	}

	//判断程序是否前台运行
	private boolean isRunningForeground (Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName()))
		{
			return true ;
		}
		return false ;
	}

//	private void changeOpentype(){
//		if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == true) {
////			Intent intent = new Intent(HomeActivity.this, WifiUnlocker.class);
////			intent.setAction("start_wifi_unlock");
////			intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.OPEN_SHAKE_FUNCTION);
////			startService(intent);
//			Intent intent = new Intent(HomeActivity.this, ShakeSensorService.class);
//			intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
//			intent.putExtra(ShakeSensorService.UNLOCK_TYPE,ShakeSensorService.OPEN_BY_WIFI);
//			startActivity(intent);
//		} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == true) {
////			Intent intent = new Intent(HomeActivity.this, WifiUnlocker.class);
////			intent.setAction("start_wifi_unlock");
////			intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.CLOSE_SHAKE_FUNCTION);
////			startService(intent);
//			Intent intent = new Intent(HomeActivity.this, ShakeSensorService.class);
//			intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.CLOSE_SENSOR);
//			startActivity(intent);
//		} else if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == false) {
////			Intent intent = new Intent(HomeActivity.this, BluetoothControler.class);
////			intent.setAction("start_bluetooth_unlock");
////			intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.OPEN_SHAKE_FUNCTION);
////			startService(intent);
//			Intent intent = new Intent(HomeActivity.this, ShakeSensorService.class);
//			intent.putExtra(ShakeSensorService.UNLOCK_TYPE,ShakeSensorService.OPEN_BY_WIFI);
//			intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.OPEN_SENSOR);
//			startActivity(intent);
//		} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == false) {
////			Intent intent = new Intent(HomeActivity.this, BluetoothControler.class);
////			intent.setAction("start_bluetooth_unlock");
////			intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.CLOSE_SHAKE_FUNCTION);
////			startService(intent);
//			Intent intent = new Intent(HomeActivity.this, ShakeSensorService.class);
//			intent.putExtra(ShakeSensorService.OPEN_OR_CLOSE,ShakeSensorService.CLOSE_SENSOR);
//			startActivity(intent);
//		}
//	}

	private void changeOpentype(){
	if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == true) {
		Intent intent = new Intent(HomeActivity.this, WifiUnlocker.class);
		intent.setAction("start_wifi_unlock");
		intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.OPEN_SHAKE_FUNCTION);
		startService(intent);
	} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == true) {
		Intent intent = new Intent(HomeActivity.this, WifiUnlocker.class);
		intent.setAction("start_wifi_unlock");
		intent.putExtra(WifiUnlocker.WIFIUNLOCK_TYPE, WifiUnlocker.CLOSE_SHAKE_FUNCTION);
		startService(intent);
	} else if (HomeActivity.ifShakeboxusable==true && HomeActivity.ifWifiUsable == false) {
		Intent intent = new Intent(HomeActivity.this, BluetoothControler.class);
		intent.setAction("start_bluetooth_unlock");
		intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.OPEN_SHAKE_FUNCTION);
		startService(intent);
	} else if (HomeActivity.ifShakeboxusable==false && HomeActivity.ifWifiUsable == false) {
		Intent intent = new Intent(HomeActivity.this, BluetoothControler.class);
		intent.setAction("start_bluetooth_unlock");
		intent.putExtra(BluetoothControler.BLUETOOTH_UNLOCK_TYPE, BluetoothControler.CLOSE_SHAKE_FUNCTION);
		startService(intent);
	}
}

//	private class ChangefragmentHandler extends Handler{
//		@Override
//		public void handleMessage(Message msg){
//			if(msg.arg1==1) {
//				if (!lowFragement.isAdded()){
//					begintTransaction.hide(homeFragement).add(R.id.ifragement,lowFragement).commit();
//				}else {
//					begintTransaction.hide(homeFragement).show(lowFragement).commit();
//				}
//
//			}else {
//				if (!lowFragement.isAdded()){
//					begintTransaction.hide(lowFragement).add(R.id.ifragement, homeFragement);
//				}else {
//					begintTransaction.hide(lowFragement).show(homeFragement).commit();
//				}
//
//			}
//		}
//	}
	//切换low/high_opendoor_fragment
	public void switchContent(Fragment from, Fragment to) {

		if (!to.isAdded()) {    // 先判断是否被add过
			getFragmentManager().beginTransaction().
					hide(from).commit();
			getFragmentManager().beginTransaction()
					.add(R.id.ifragement, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			getFragmentManager().beginTransaction()
					.show(to).commit();
		} else {
			getFragmentManager().beginTransaction()
					.hide(from).commit();
			getFragmentManager().beginTransaction()
					.show(to).commit(); // 隐藏当前的fragment，显示下一个
		}

		//			getFragmentManager().beginTransaction()
//					.replace(R.id.ifragement, lowFragement).commit();
	}

	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			Log.e(TAG, "getAndroidSDKVersion: " + e.toString());
		}
		return version;
	}
}
