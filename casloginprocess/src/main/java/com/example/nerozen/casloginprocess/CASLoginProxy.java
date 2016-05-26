package com.example.nerozen.casloginprocess;

import android.content.Context;
import android.content.Intent;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.jacoblong.myvolleyrequestlib.BasicErrorListener;
import com.example.jacoblong.myvolleyrequestlib.BasicRequest;
import com.example.jacoblong.myvolleyrequestlib.RedirectErrorListener;
import com.example.jacoblong.myvolleyrequestlib.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nerozen on 2016/5/11.
 */
public class CASLoginProxy {

    public static final String CAS_ERROR_BROADCAST_ACTION = "CAS_ERROR_BROADCAST_ACTION"; //登录过程中错误标识，比如网络异常，服务器异常
    public static final String CAS_SUCCESS_BROADCAST_ACTION = "CAS_BROADCAST_ACTION"; //登录各阶段的成功标识


    /** 数据传输的定义
     * 1.CAS_DATA_TRANSITION_BROADCAST_ACTION ：表明该广播是发送CAS数据的（已弃用）
     * 1.1.CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION 传输hostsession
     * 1.2 CAS_DATA_TRANSITION_HOSTINFO_MAP_ACTION 传输园区信息映射列表
     * 2.EXTRA_KEY_DATA_TRANSITION_GARDEN_DB_MAP 表明传输的是园区和数据库名字的映射
     * 3.EXTRA_KEY_DATA_TRANSITION_GADEN_HOST_MAP 表明传输的是园区和其主机的地址映射
     * 4.EXTRA_KEY_DATA_TRANSITION_HOST_SESSION 表明传输的是主机的SESSIONID
     * */
    //public static final String CAS_DATA_TRANSITION_BROADCAST_ACTION = "CAS_DATA_TRANSITION_BROADCAST_ACTION";

    public static final String CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION = "CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION";
    public static final String CAS_DATA_TRANSITION_HOSTINFO_MAP_BROADCAST_ACTION = "CAS_DATA_TRANSITION_HOSTINFO_MAP_ACTION";

    public static final String EXTRA_KEY_DATA_TRANSITION_GARDEN_DB_MAP = "DB_NAME_MAP";
    public static final String EXTRA_KEY_DATA_TRANSITION_GADEN_HOST_MAP = "HOST_URL_MAP";
    public static final String EXTRA_KEY_DATA_TRANSITION_HOST_SESSION = "HOST_SESSION";


    /** 错误定义
     * 1.网络错误（可能没开启网络连接）
     * 2.服务器异常
     * 3.连接超时
     * 4.用户名或密码有误
     * 5.返回的园区列表有误
     * */
    public static final int CAS_REQUEST_ERROR_NETWORK_ERROR = 1;
    public static final int CAS_REQUEST_ERROR_SERVER_EXCEPTION = 2;
    public static final int CAS_REQUEST_ERROR_CONNECT_TIMEOUT = 3;
    public static final int CAS_AUTHENTICATION_FAILED_USER_INFO_INVALID = 4;
    public static final int CAS_HOST_LIST_JSON_EXCEPTION = 5;

    /** 正确反馈定义
     * 1.host session激活成功，CAS登录或园区切换过程成功
     * 2.认证成功，用户有效
     * */
    public static final int CAS_ACTIVATE_HOST_SESSION_SUCCESS = 10;
    public static final int CAS_AUTHENTICATION_SUCCESS = 11;


    protected Context context;
    protected final String CASLoginUrl = "https://nervtech.cn:8443/yunpassCAS";
    protected final String dispatchUrl = "https://www.nervtech.cn:8443/yc-dispatch/";
    protected final String activateHostSessionUrlSegment = "/uc/getRole";

    protected String CASAuthenticationUrlWithArugment = "";

    protected final String userNameArgumentFormat = "username=";
    protected final String passwordArgumentFormat = "password=";
    protected final String loginTicketArgumentFormat = "lt=";
    protected final String eventIdArgumentFormat = "_eventId=";
    protected final String executionvalueFormat = "execution=";

    protected BasicRequest grabLoginPageRequest ,CASAuthenticationRequest, getHostRequest, activateHostSessionRequest;
    protected BasicErrorListener generalTotalErrorListener;

    protected RedirectErrorListener redirectErrorListener;
    protected RedirectErrorListener getHostSessionRedirectListener;
    protected RedirectErrorListener sessionInjectionRedirectListener;

    protected Response.Listener<String> grabLoginPageRequestSuccessListener;
    protected Response.Listener<String> AuthenticationSuccessListener;
    protected Response.Listener<String> getHostRequestSuccessListener;
    protected Response.Listener<String> activateHostSessionSuccessListener;


    private Map<String, String> gardenNameHostUrlMap, gardenNameDBNameMap;

    protected void setGardenNameHostUrlMap(Map gardenNameHostUrlMap) {
        this.gardenNameHostUrlMap = gardenNameHostUrlMap;
    }

    protected void setGardenNameDBNameMap(Map gardenNameDBNameMap) {
        this.gardenNameDBNameMap = gardenNameDBNameMap;
    }

    protected String userName, passWord;

    protected CASLoginSession casLoginHeader;

    /**构造方法，传入Activity上下文，用户名和密码，初始化登录时会用到的数据结构，比如报头，session*/
    public CASLoginProxy(Context context, String userName, String password) {
        this(context);
        this.userName = userName;
        this.passWord = password;
        generalTotalErrorListener = InitGeneralTotalErrorListener(); //通用的错误处理
        casLoginHeader = new CASLoginSession(); //所有登录数据均存储在这里，与casproxy生命周期一致，里面数据如果需要传递给别的对象通过广播发送
    }

    /**构造方法*/
    public CASLoginProxy(Context context) {
        this.context = context;
        generalTotalErrorListener = InitGeneralTotalErrorListener(); //通用的错误处理
        casLoginHeader = new CASLoginSession();
    }

    /**CAS登录方法，传入用户名和密码，在CAS服务器认证，获得可访问的园区列表*/
    public void CASLogin(String userName, String password) {
        this.userName = userName;
        this.passWord = password;
        grabLoginPageRequest = InitgrabLoginPageRequest();
        AddRequestToRequestManager(grabLoginPageRequest);
    }

    /**CAS切换园区，在激活身份的时候调用，传入可用的园区跳转地址，使园区承认用户身份*/
    private void CASChangeHost(String hostUrl, String dbName) {
        ActivateHostSession(hostUrl, dbName);
    }

    /**通用的volley框架错误响应，捕获一般的网络连接超时错误，和服务器故障，通过广播将错误信息发送，由接受者做具体处理*/
    protected BasicErrorListener InitGeneralTotalErrorListener() {
        return new BasicErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DealWithCommonVolleyError(error);
                super.onErrorResponse(error);
            }
        };
    }

    protected void DealWithCommonVolleyError(VolleyError error) {
        if(BasicErrorListener.isNetworkProblem(error)) {
            //网络异常
            SendCASErrorBroadcast(CAS_REQUEST_ERROR_NETWORK_ERROR);
        }
        if(BasicErrorListener.isServerError(error)) {
            //服务器异常
            SendCASErrorBroadcast(CAS_REQUEST_ERROR_SERVER_EXCEPTION);
        }
        if(BasicErrorListener.isTimeoutError(error)) {
            //连接超时
            SendCASErrorBroadcast(CAS_REQUEST_ERROR_CONNECT_TIMEOUT);
        }
    }

    /**通用的volley重定向错误相应，除一般的错误相应外，还处理重定向的处理，根据重定向地址递归访问，直到正常相应。
    * 应注意：如果重定向时还涉及报头操作，需要自行定义更复杂的事件处理 */
    protected RedirectErrorListener InitRedirectErrorListener(Map<String, String> headers, Response.Listener<String> successListener) {
        return new RedirectErrorListener(headers, successListener, context) {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isNetworkProblem(error)) {
                    //网络异常
                    SendCASErrorBroadcast(CAS_REQUEST_ERROR_NETWORK_ERROR);
                }
                if(isServerError(error)) {
                    //服务器异常
                    SendCASErrorBroadcast(CAS_REQUEST_ERROR_SERVER_EXCEPTION);
                }
                if(isTimeoutError(error)) {
                    //连接超时
                    SendCASErrorBroadcast(CAS_REQUEST_ERROR_CONNECT_TIMEOUT);
                }
                super.onErrorResponse(error);
            }
        };
    }

    /**发生通用网络错误时（重定向不在其中），发送广播，让接受者做相应处理*/
    protected void SendCASErrorBroadcast(int errorInfo) {
        Intent intent = new Intent();
        intent.setAction(CAS_ERROR_BROADCAST_ACTION);
        intent.putExtra("errorInfo", errorInfo);
        context.sendBroadcast(intent);
    }

    /**发送各个阶段的信息，表明登录进度*/
    protected void SendPeriodicalSuccessBroadcast(int successInfo) {
        Intent intent = new Intent();
        intent.setAction(CAS_SUCCESS_BROADCAST_ACTION);
        intent.putExtra("successInfo", successInfo);
        context.sendBroadcast(intent);
    }

    /**初始化登录的HTML页面请求，这是CAS登录流程的第一步
    * 在报头截获时，获取CAS返回的JSESSION*/
    protected BasicRequest InitgrabLoginPageRequest() {
        grabLoginPageRequestSuccessListener = InitGrabLoginPageSuccessListener();
        return new BasicRequest(Request.Method.GET, CASLoginUrl,
                grabLoginPageRequestSuccessListener, generalTotalErrorListener) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                //获取登录界面，在返回报头里获取CAS的jsession
                casLoginHeader.CASServerJsession = response.headers.get("Set-Cookie");
                return super.parseNetworkResponse(response);
            }
        };
    }

    /**登录页面获取后，提取页面元素，获取lt、eventid、execution三个值，并初始化认证请求，携带用户数据和CASJSESSION*/
    protected Response.Listener<String> InitGrabLoginPageSuccessListener() {
        return new Response.Listener<String>() {
            private void extractLoginArgumentFromLoginPage(String response) {
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                casLoginHeader.loginTicket = doc.select("input[name=lt]").val();
                casLoginHeader.executionValue = doc.select("input[name=execution]").val();
                casLoginHeader.eventId = doc.select("input[name=_eventId]").val();
            }
            @Override
            public void onResponse(String response) {
                extractLoginArgumentFromLoginPage(response); //将相应的html页面中的登录参数提取出来
                AuthenticationSuccessListener = InitCASAuthenticationSuccessListener();
                //将登录参数以map的形式传入请求中
                Map<String, String> authenticationHeaders = InitAuthenticationHeaders(casLoginHeader);
                CASAuthenticationUrlWithArugment = MakeCompleteAuthenticationUrl(casLoginHeader, userName, passWord);
                CASAuthenticationRequest = InitCASAuthenticationRequest(AuthenticationSuccessListener,
                        CASAuthenticationUrlWithArugment, authenticationHeaders);
                AddRequestToRequestManager(CASAuthenticationRequest);
            }
        };
    }

    /** 拼接认证请求url（GET方法），包含lt，eventid，execution，用户名和密码五个参数*/
    protected String MakeCompleteAuthenticationUrl(CASLoginSession casLoginHeader, String userName, String passWord) {
        return (CASLoginUrl + "?" + userNameArgumentFormat + userName
                + "&" + passwordArgumentFormat + passWord
                + "&" + loginTicketArgumentFormat + casLoginHeader.loginTicket
                + "&" + eventIdArgumentFormat + casLoginHeader.eventId
                + "&" + executionvalueFormat + casLoginHeader.executionValue);
    }


    /** 初始化认证请求的报头，在报头中携带四个参数，一个是CASJSESSION，另外三个是模仿浏览器的三个参数，为了正常响应而附带，没有实际含义*/
    private Map<String, String> InitAuthenticationHeaders(CASLoginSession casLoginHeader) {
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", casLoginHeader.CASServerJsession);
        map.put("Host", "www.nervtech.cn:8443");
        map.put("Referer", "https://www.nervtech.cn:8443/yunpassCAS/login");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        return map;
    }

    /** 初始化认证正常响应的回调类，认证成功后，访问dispatch接口，获得可访问的园区列表*/
    protected Response.Listener<String> InitCASAuthenticationSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //发送广播，表明CAS认证成功
                SendPeriodicalSuccessBroadcast(CAS_AUTHENTICATION_SUCCESS);
                //请求dispatch，获得所有主机跳转地址
                if(casLoginHeader.CASTGC != null) {
                    getHostRequestSuccessListener = InitGetHostSuccessListener();
                    getHostRequest = InitGetHostRequest(getHostRequestSuccessListener);
                    AddRequestToRequestManager(getHostRequest);
                }
                SendCASErrorBroadcast(CAS_AUTHENTICATION_FAILED_USER_INFO_INVALID);
            }
        };
    }

    /** 初始化认证请求，响应报头中会携带CASTGC，将其保存起来*/
    protected BasicRequest InitCASAuthenticationRequest(Response.Listener<String> authenticationSuccessListener,
                                                        String CASAuthenticationUrlWithArugment,
                                                        Map headers) {
        return new BasicRequest(Request.Method.GET, CASAuthenticationUrlWithArugment, authenticationSuccessListener, generalTotalErrorListener, headers) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                //提取CASTGC
                String rawCASTGC = response.headers.get("Set-Cookie1");
                if(rawCASTGC == null) {
                    //认证出错，用户名或密码不正确
                    casLoginHeader.CASTGC = null;
                }else {
                    casLoginHeader.CASTGC = rawCASTGC.substring(0, rawCASTGC.indexOf(';'));
                }
                return super.parseNetworkResponse(response);
            }
        };
    }

    /** 请求园区列表正常响应后，获取园区列表，封装好列表后，通过广播发送给UI线程自行处理
     *  该回调返回后，一阶段完成
     *  等待用户选择园区，进行二阶段
     *  */
    protected Response.Listener<String> InitGetHostSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    parseGardenList(response); //解析json串，获得园区和主机地址，数据库之间的两个map映射

                    //将两个map通过广播传递出去
                    Intent intent = new Intent();
                    intent.setAction(CAS_DATA_TRANSITION_HOSTINFO_MAP_BROADCAST_ACTION);
                    intent.putExtra(EXTRA_KEY_DATA_TRANSITION_GARDEN_DB_MAP, new GardenInfoList(gardenNameDBNameMap));
                    intent.putExtra(EXTRA_KEY_DATA_TRANSITION_GADEN_HOST_MAP, new GardenInfoList(gardenNameHostUrlMap));
                    context.sendBroadcast(intent);
                } catch(JSONException e) {
                    //解析json出错
                    SendCASErrorBroadcast(CAS_HOST_LIST_JSON_EXCEPTION);
                }
            }
        };
    }

    /** 解析园区的信息列表，转化成两个Map，一个是园区名——主机地址的映射表，另一个是园区名——数据库名的映射表*/
    protected void parseGardenList(String response) throws JSONException{
        JSONArray jumpAddress = new JSONArray(response);
        JSONObject jsonObject = (JSONObject) jumpAddress.get(0);

        Map<String, JSONObject> keyGardenInfoMap = new HashMap<>();

        Map<String, String> gardenNameHostUrlmap = new HashMap<String, String>();
        Map<String, String> gardenNameDBmap = new HashMap<String, String>();

        Iterator it = jsonObject.keys();
        while(it.hasNext()){
            String key = String.valueOf(it.next());
            JSONObject value = (JSONObject)jsonObject.get(key);
            keyGardenInfoMap.put(key, value);
        }

        for(String gardenName : keyGardenInfoMap.keySet()) {
            gardenNameHostUrlmap.put(gardenName, keyGardenInfoMap.get(gardenName).getString("host"));
            gardenNameDBmap.put(gardenName, keyGardenInfoMap.get(gardenName).getString("database"));
        }

        setGardenNameDBNameMap(gardenNameDBmap);
        setGardenNameHostUrlMap(gardenNameHostUrlmap);
    }

    /** 初始化获取园区请求，因为此处需要使用通用重定向回调，需要先初始化重定向回调*/
    protected BasicRequest InitGetHostRequest(Response.Listener<String> getHostRequestSuccessListener) {
        Map<String, String> getHostHeaders = new HashMap<>();
        getHostHeaders.put("Cookie", casLoginHeader.CASTGC);
        return new BasicRequest(Request.Method.GET, dispatchUrl,
                getHostRequestSuccessListener, redirectErrorListener, getHostHeaders);
    }

    /** 注入特定园区服务器的SESSION，使用户身份在该园区激活，此处的重定向涉及了报头操作，需要定义不同的的重定向回调*/
    public void ActivateHostSession(String activateHostUrl, String paramaDBName) {
        /**
         *  向主机的注入session接口发起请求,正常情况会经历两次重定向，第一次重定向获取并记录主机返回的jsession
         *  随后向重定向地址请求，携带CASTGC和CAS主机返回的jsession（在grabloginpage的时候已经获取）
         *  第二次重定向时，携带主机返回的jsession去访问重定向地址，此时jsession激活，这个jsession可以在往后的访问中使用了
         *  */
        activateHostSessionSuccessListener = InitactivateHostSessionSuccessListener();
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", casLoginHeader.CASTGC);
        getHostSessionRedirectListener = InitGetHostSessionRedirectListener(header,
                activateHostSessionSuccessListener, InitSessionInjectionRedirectListener(activateHostSessionSuccessListener));
        InitGetHostSessionRequest(activateHostUrl + "?dbname=" + paramaDBName,
                                  activateHostSessionSuccessListener,
                                  getHostSessionRedirectListener);
    }

    /** 初始化获取主机SESSION的请求*/
    protected BasicRequest InitGetHostSessionRequest(String activateHostUrl,
                                                     Response.Listener<String> activateHostSessionSuccessListener,
                                                     BasicErrorListener getHostSessionRedirectListener) {
        return new BasicRequest(Request.Method.GET, activateHostUrl,
                activateHostSessionSuccessListener, getHostSessionRedirectListener);
    }

    /** 激活Session成功的回调，发送成功的广播，此后携带园区Session可以正常访问了*/
    protected Response.Listener<String> InitactivateHostSessionSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //发送广播，表明园区激活成功，并将园区session传出
                SendPeriodicalSuccessBroadcast(CAS_ACTIVATE_HOST_SESSION_SUCCESS);
                Intent intent = new Intent();
                intent.setAction(CAS_DATA_TRANSITION_HOSTSESSION_BRAODCAST_ACTION);
                intent.putExtra(EXTRA_KEY_DATA_TRANSITION_HOST_SESSION, casLoginHeader.HostSession);
            }
        };
    }

    /** 初始化获取HOSTSESSION成功的回调,需要将CASTGC传入重定向的请求头中*/
    protected RedirectErrorListener InitGetHostSessionRedirectListener(Map headers,
                                                                            Response.Listener<String> successListener,
                                                                            final RedirectErrorListener redirectErrorListener) {
        return new RedirectErrorListener(headers, successListener, context.getApplicationContext()) {
            @Override
            public void onErrorResponse(VolleyError error) {
                DealWithCommonVolleyError(error);
                /*注入session请求，第一次返回302，此时在报头中获取hostsession
                * 将CASsession和CASTGC封装到重定向请求报头中
                * 将第二次重定向回调传入重定向请求中
                * */
                if(isRedirectError(error.networkResponse.statusCode)) {
                    String redirectUrl = FetchRedirectUrl(error.networkResponse);
                    String hostRawSession = error.networkResponse.headers.get("Set-Cookie");
                    casLoginHeader.HostSession = hostRawSession.substring(0, hostRawSession.indexOf(';'));
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Cookie", casLoginHeader.CASServerJsession + casLoginHeader.CASTGC);
                    if(redirectUrl == null || redirectUrl.equals("")) {
                        //SendCASErrorBroadcast(CAS_REQUEST_ERROR_SERVER_EXCEPTION);
                    }else {
                        /*再次发送请求*/
                        StringRequest redirectRequest = MakeRedirectRequest(redirectErrorListener, headers);
                        AddRequestToRequestManager(redirectRequest);
                    }
                }
            }
        };
    }

    /**初始化CASSession和CASTGC注入的重定向回调
    *  注入成功后，园区服务器返回重定向地址
    *  使用园区的session（携带在请求头中）访问这个重定向地址
    *  成功后，将返回200，此时hostsession正式激活，CAS认证流程完成
    *  之后可以使用hostsession访问所有接口了，因此需要将hostsession传递出去，让静态的Session对象保存起来
    * */
    protected RedirectErrorListener InitSessionInjectionRedirectListener(Response.Listener<String> successListener) {
        return new RedirectErrorListener(null, successListener, context.getApplicationContext()) {
            @Override
            public void onErrorResponse(VolleyError error) {
                DealWithCommonVolleyError(error);
                if(isRedirectError(error.networkResponse.statusCode)) {
                    //不需要再从这个返回报头里获得数据
                    Map<String, String> header = new HashMap<>();
                    header.put("Cookie", casLoginHeader.HostSession);
                    if(redirectUrl == null || redirectUrl.equals("")) {
                        //SendCASErrorBroadcast(CAS_REQUEST_ERROR_SERVER_EXCEPTION);
                    }else {
                        /*再次发送请求*/
                        StringRequest redirectActivateHostSessionRequest = MakeRedirectRequest(this, header);
                        AddRequestToRequestManager(redirectActivateHostSessionRequest);
                    }
                }
            }
        };
    }

    /**
     * */
    protected void AddRequestToRequestManager(Request request) {
        RequestManager.getInstance(context.getApplicationContext()).addRequest(request);
    }

    protected class CASLoginSession {
        public String loginTicket;
        public String executionValue;
        public String eventId;
        public String CASServerJsession;

        public String CASTGC;
        public String HostName;
        public String HostSession;
    }

}
