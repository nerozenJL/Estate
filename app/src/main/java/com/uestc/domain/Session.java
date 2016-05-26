package com.uestc.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 这是保存cook里的seesion
 */
public class Session {

	public static String seesion="";
	public static int id = 0;
	public static int role = 0;

	//2016-3-27 新增用户开锁习惯的两个变量，表征开锁方式和摇一摇是否启动
	public static boolean isShakingUnlockAble=true;
	public static int unlockMode=2;//1是wifi，2是蓝牙

	public static boolean isShakingUnlockAble() {
		return isShakingUnlockAble;
	}

	public static int getUnlockMode() {
		return unlockMode;
	}

	public static void setIsShakingUnlockAble(boolean isShakingUnlockAble) {
		Session.isShakingUnlockAble = isShakingUnlockAble;
	}

	public static void setUnlockMode(int unlockMode) {
		Session.unlockMode = unlockMode;
	}

	public static int getRole(){
		return role;
	}

	public static  void setRole(int role){
		Session.role = role;
	}
	public static int getId() {
		return id;
	}

	public static void setId(int id) {
		Session.id = id;
	}

	public static String getSeesion() {
		return Session.seesion;
	}

	public static void setSeesion(String seesion) {
		Session.seesion = seesion;
	}

	public static List<WiFiProtalInfo> getAccessableWiFiSsidList() {
		return accessableWiFiSsidList;
	}

	public static List<BluetoothProtalInfo> getAccessableBlueToothSsidList() {
		return accessableBlueToothSsidList;
	}

	private static List<WiFiProtalInfo> accessableWiFiSsidList = new ArrayList<WiFiProtalInfo>();
	private static List<BluetoothProtalInfo> accessableBlueToothSsidList = new ArrayList<BluetoothProtalInfo>();

	public static void addMemberToAccessableWiFiSsidList(WiFiProtalInfo newMember){
		accessableWiFiSsidList.add(newMember);
	}

	public static void addMemberToAccessableBTSsidList(BluetoothProtalInfo newMember){
		accessableBlueToothSsidList.add(newMember);
	}
	
}
