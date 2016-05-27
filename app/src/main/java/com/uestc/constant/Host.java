package com.uestc.constant;

/**
 * 
 * URL 120.24.152.123 192.168.1.26
 */
public class Host {
	public static String testhost = "http://120.24.152.123:8080/oa-test/api";

	public static String host = "http://120.24.152.123:8080/oa/api";

	public static String host1 = "http://120.24.152.123:8080";

	public static String getNotice = host + "/notice/getSome/10";//
	public static String getNotice1 = host + "/notice/getSome/1";
	public static String login = host + "/uc/login";
	public static String loginOut = host + "/uc/loginOut";
	public static String getRole = host + "/uc/getRole";
	public static String getVerify = host + "/uc/register/getVerifyCode";
	public static String checkVerify = host + "/uc/register/checkVerifyCode";
	public static String regist = host + "/uc/register/doRegister"; //
	public static String bind = host + "/property/bind"; //
	public static String getAllVillage = host + "/query/getAllVillage"; //
	public static String getAllBuilding = host + "/query/getBuilding/"; //
	public static String getAllProperty = host + "/query/getProperty"; //
	public static String getKey = host + "/auth/getSecret/"; //
	public static String findPassword = host + "/uc/findPassword/";
	public static String findPasswordCheckVerify = host + "/uc/findPassword/checkVerifyCode/";
	public static String setPassword = host + "/uc/findPassword/reset/";
	public static String getMyComplain = host + "/complain/getMyComplain";
	public static String addComplain = host + "/complain/add";
	public static String remarkComplain = host + "/complain/remark";
	public static String addRepair = host + "/repair/add";
	public static String getRepair = host + "/repair/getMyRepair";
	public static String remarkRepair = host + "/repair/remark";
	public static String getBill = host + "/fee/getBill";
	public static String pay = host + "/fee";
	public static String reSetPassword = host + "/uc/modify/password";
	public static String getData = host + "/uc/modify/getProfile";
	public static String setData = host + "/uc/modify/submitProfile";
	public static String getProperty = host + "/property/getMyPropery";
	public static String getBind = host + "/property/getBind";
	public static String agreeBind = host + "/property/submitBind/agree/";
	public static String refuseBind = host + "/property/submitBind/refuse/";
	public static String uploadDoorLog = host + "/auth/uploadDoorLog";
	//需要后缀用户账号密码,格式："phone=XXXX&psw=XXX"
	public static String payWebhost = "http://120.24.152.123:8080/test/?";
	public static String getSecret = host + "/auth/getAllowSecret";
	public static String getVersion = host+"/query/checkUpdate";
	public static String identityVerify = host+"/vertify/add";


	/*2016/4/4/新增获取物业电话接口*/
	public static String getPropertyManagerPhoneList = host + "/query/getServicePhone";


	/*2016/4/6商店预览图片地址接口*/
	public static String getStorePreviewPictures = host + "/goods/goodsList/3/json"; //"3"指定获取了3张图片，后期写为可配置的

	/*用户选择登录园区或者切换某个园区后，根据动态修改请求url和dbname*/
	public static void DynamiclySetAPIs(String hostName, String dbname) {
		host = hostName;
		getNotice = host + "/notice/getSome/10" + "?database=" + dbname;//
		getNotice1 = host + "/notice/getSome/1" + "?database=" + dbname;;
		login = host + "/uc/login" + "?database=" + dbname;
		loginOut = host + "/uc/loginOut" + "?database=" + dbname;
		getRole = host + "/uc/getRole" + "?database=" + dbname;
		getVerify = host + "/uc/register/getVerifyCode" + "?database=" + dbname;
		checkVerify = host + "/uc/register/checkVerifyCode" + "?database=" + dbname;
		regist = host + "/uc/register/doRegister" + "?database=" + dbname;
		bind = host + "/property/bind" + "?database=" + dbname;
		getAllVillage = host + "/query/getAllVillage" + "?database=" + dbname;
		getAllBuilding = host + "/query/getBuilding/" + "?database=" + dbname;
		getAllProperty = host + "/query/getProperty" + "?database=" + dbname;
		getKey = host + "/auth/getSecret/" + "?database=" + dbname;
		findPassword = host + "/uc/findPassword/" + "?database=" + dbname;
		findPasswordCheckVerify = host + "/uc/findPassword/checkVerifyCode/" + "?database=" + dbname;
		setPassword = host + "/uc/findPassword/reset/"+ "?database=" + dbname;
		getMyComplain = host + "/complain/getMyComplain"+ "?database=" + dbname;
		addComplain = host + "/complain/add"+ "?database=" + dbname;
		remarkComplain = host + "/complain/remark"+ "?database=" + dbname;
		addRepair = host + "/repair/add"+ "?database=" + dbname;
		getRepair = host + "/repair/getMyRepair" + "?database=" + dbname;
		remarkRepair = host + "/repair/remark" + "?database=" + dbname;
		getBill = host + "/fee/getBill" + "?database=" + dbname;
		pay = host + "/fee" + "?database=" + dbname;
		reSetPassword = host + "/uc/modify/password" + "?database=" + dbname;
		getData = host + "/uc/modify/getProfile" + "?database=" + dbname;
		setData = host + "/uc/modify/submitProfile" + "?database=" + dbname;
		getProperty = host + "/property/getMyPropery" + "?database=" + dbname;
		getBind = host + "/property/getBind" + "?database=" + dbname;
		agreeBind = host + "/property/submitBind/agree/" + "?database=" + dbname;
		refuseBind = host + "/property/submitBind/refuse/" + "?database=" + dbname;
		uploadDoorLog = host + "/auth/uploadDoorLog" + "?database=" + dbname;
		payWebhost = "http://120.24.152.123:8080/test/?" + "?database=" + dbname;
		getSecret = host + "/auth/getAllowSecret" + "?database=" + dbname;
		getVersion = host+"/query/checkUpdate" + "?database=" + dbname;
		identityVerify = host+"/vertify/add" + "?database=" + dbname;
		getPropertyManagerPhoneList = host + "/query/getServicePhone"+ "?database=" + dbname;
		getStorePreviewPictures = host + "/goods/goodsList/3/json"+ "?database=" + dbname;
	}
}
