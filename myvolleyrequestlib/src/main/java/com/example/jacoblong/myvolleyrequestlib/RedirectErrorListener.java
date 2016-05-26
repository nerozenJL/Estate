package com.example.jacoblong.myvolleyrequestlib;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by Jacob Long on 2016/4/19.
 *
 * 该类解决的是对需要进行重定向的请求进行重定向的操作
 *
 * Volley对于状态301，302，303的重定向响应默认置为错误,因此要在请求的错误回调中进行重定向请求。
 * 本类实现Response.ErrorListener中的回调方法，在回调方法中，判断错误状态是否为重定向（301，302，303），
 * 如果是，则提取重定向地址（location）并新建请求并发送；如果不是，则按照对普通错误的处理方式进行处理。
 *
 * 本项目使用该类的场景：
 * 1.CAS完成后发送请求需要重定向
 *
 * 新建的请求中，使用的递归的方式，将自身的实例传入作为
 * 新请求的错误响应，可以解决多次重定向的场景
 */
public class RedirectErrorListener extends BasicErrorListener {

    protected Response.Listener successListener; //重定向完成且成功时的回调
    protected Map headers; //需要添加到报文里面的信息
    protected String redirectUrl = "";
    protected Context context;

    public RedirectErrorListener(Map headers, Response.Listener successListener, Context context) {
        this.headers = headers;
        this.successListener = successListener;
        this.context = context;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
         /*判断是否为重定向提示*/
        if(error.networkResponse != null) {
            if(isRedirectError(error.networkResponse.statusCode)) {
                String redirectUrl = FetchRedirectUrl(error.networkResponse);
                if(redirectUrl == null || redirectUrl.equals("")) {
                }else {
                /*再次发送请求*/
                    StringRequest redirectRequest = MakeRedirectRequest(); //
                    RequestManager.getInstance(context).addRequest(redirectRequest);
                }
            }
        }
    }

    protected StringRequest MakeRedirectRequest() {
        StringRequest redirectRequest = new StringRequest(Request.Method.GET, redirectUrl, successListener, RedirectErrorListener.this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers == null){
                    return super.getHeaders();
                }else {
                    return headers;
                }
            }
        };
        return redirectRequest;
    }

    protected BasicRequest MakeRedirectRequest(RedirectErrorListener otherRedirectListener, Map ohterMap) {
        BasicRequest redirectRequest = new BasicRequest(Request.Method.GET, redirectUrl, successListener, otherRedirectListener, ohterMap);
        return redirectRequest;
    }

    protected boolean isRedirectError(int state) {
        boolean isRedirect = false;
        switch(state) {
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
                isRedirect = true;
                break;
            default:
                break;
        }
        return  isRedirect;
    }

    protected String FetchRedirectUrl(NetworkResponse response) {
       return response.headers.get("location");
    }
}
