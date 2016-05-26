package com.uestc.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nerozen on 2016/3/27.
 * 1.本类是为了临时解决登录流程与主业网络交互导致的消息传递问题
 * 2.新版本后所有该类的消息都建议用序列化传递，而不是用静态变量存储
 */
public class TempStaticInstanceCollection {
    public static GetAnnouncementData announcementList = null;
    public static List<ADInfo> storePreviewInfo = new ArrayList<ADInfo>();
    public static List<String> propertyManagerCompanyPhoneNumberList = new ArrayList<String>();
}
