package com.example.jacoblong.myvolleyrequestlib;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jacob Long on 2016/4/25.
 *
 * 基础的网络请求错误回调，针对以下几种错误
 * 1.连接超时和错误超时(非http协议错误0
 * 2.常见网络错误响应（如404）
 */
public class BasicErrorListener implements Response.ErrorListener {

    private final String timeOutMessage = "com.volley.timeouterror";

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.v("BasicErrorListener", "request error");
        if(isNetworkProblem(error)) {
            Log.v("error", "Network Error");
        }
        if(isServerError(error)) {
            Log.v("error", "Server Error");
        }
        if(isTimeoutError(error)) {
            Log.v("error", "Timeout Error");
        }
    }

    public static boolean isTimeoutError(VolleyError error) {
        return (error instanceof TimeoutError);
    }

    public static boolean isNetworkProblem(VolleyError error) {
        /*无响应内容返回NetworkError， 无网络响应代码（本地连接异常）返回NoConnectionError*/
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    public static boolean isServerError(VolleyError error) {
        /*服务器返回300以上的代码，如果要判别重定向错误，需要更进一步的检查*/
        return (error instanceof ServerError);
    }
}
