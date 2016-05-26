package com.example.jacoblong.myvolleyrequestlib;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Jacob Long on 2016/4/26.
 */
public class BasicRequest extends StringRequest {

    private Map<String, String> headersToPut = null;

    private Map<String, String> headerToGet;

    public BasicRequest(int method,
                        String url,
                        Response.Listener<String> listener,
                        Response.ErrorListener errorListener,
                        Map<String, String> headersToPut) {
        this(method, url, listener, errorListener);
        this.headersToPut = headersToPut;
    }

    public void SetTimeoutAndRetryTime(int limitTime, int retryTime) { /*毫秒为单位,使用该方法，等待时间需要在1秒到15秒内,重发限制在10以内*/
        if(limitTime < 1000 || limitTime > 15000) {
            limitTime = 5000;
        }
        if(retryTime < 1 || retryTime > 10) {
            limitTime = 3;
        }
        this.setRetryPolicy(new DefaultRetryPolicy(limitTime, retryTime, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void SetDefaultTimeoutAndRetryTime() {
        this.setRetryPolicy(new DefaultRetryPolicy());
    }

    public BasicRequest(int method,
                        String url,
                        Response.Listener<String> listener,
                        Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        SetDefaultTimeoutAndRetryTime();
    }

    @Override
    public final Map<String, String> getHeaders() throws AuthFailureError {
        if(headersToPut == null) {
            return super.getHeaders();
        }
        return this.headersToPut;
    }
}
