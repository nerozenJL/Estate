package com.example.jacoblong.myvolleyrequestlib;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Jacob Long on 2016/4/19.
 */
public class RequestManager {
    private static RequestQueue mQueue;
    private static RequestManager Instance;

    public static RequestManager getInstance(Context context) {
        if(Instance == null) {
            synchronized (RequestManager.class) {
                Instance = new RequestManager(context);
            }
            return Instance;
        }else {
            return Instance;
        }
    }

    public RequestManager(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    public void addRequest(Request request) {
        mQueue.add(request);
    }
}
