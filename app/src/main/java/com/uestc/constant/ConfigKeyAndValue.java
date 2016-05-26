package com.uestc.constant;

/**
 * Created by Nerozen on 2016/3/27.
 * 记录配置文件的键名和对应值名
 * 记录配置文件的路径
 */
public class ConfigKeyAndValue {
    public static final String USER_INFO_CONFIG_PATH = "/data/data/com.znt.estate/autologinconfig.properties";
    public static final String USER_INFO_CONFIG_KEY_USERNAME = "userName";
    public static final String USER_INFO_CONFIG_KEY_USERPASSWORD = "passWord";
    public static final String USER_INFO_CONFIG_KEY_AUTOLOGINABLE = "isAutoLoginExpected";
    public static final String USER_INFO_CONFIG_KEY_UNLOCKMODE = "preferredUnlockMethod";
    public static final String USER_INFO_CONFIG_KEY_SHAKINGABLE = "isShakingAble";
    public static final String GLOBAL_VALUE_TRUE = "true";
    public static final String GLOBAL_VALUE_FALSE = "false";
    public static final String USER_INFO_CONFIG_VALUE_UNLOCK_WIFI = "WiFi";
    public static final String USER_INFO_CONFIG_VALUE_UNLOCK_BLUETOOTH = "Bluetooth";
}
