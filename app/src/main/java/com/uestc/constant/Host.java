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
	public static String findPasswordCheckVerify = host
			+ "/uc/findPassword/checkVerifyCode/";
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

}
