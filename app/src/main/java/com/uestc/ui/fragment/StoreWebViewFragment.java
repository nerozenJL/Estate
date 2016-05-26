package com.uestc.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nerozen on 2016/3/31.
 * 显示商店页面的fragment
 * 支持JS交互
 * 未来升级的时候可能要求支持用户数据传入和更进一步的网络交互
 */
public class StoreWebViewFragment extends Fragment {

    SwipeRefreshLayout refreshableView;
    WebView StoreWebView;
    ListView listView;
    //WebViewAdapter adapter;
    ArrayList<HashMap<String,Object>> arrayList;
    com.handmark.pulltorefresh.library.PullToRefreshWebView pullToRefreshWebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_storewebview, container, false);
        pullToRefreshWebView=(PullToRefreshWebView)view.findViewById(R.id.pull_to_refresh_webview);
        StoreWebView=pullToRefreshWebView.getRefreshableView();
        StoreWebView.getSettings().setJavaScriptEnabled(true);
        StoreWebView.setWebViewClient(new SampleWebViewClient());
        StoreWebView.loadUrl("http://www.yc-yunpass.com/mall/view/");/* + "?service=" + "http://www.yc-yunpass.com:8080/oa-test")*/;

        //http://www.yc-yunpass.com/mall/view/?service=http://www.yc-yunpass.com:8080/oa-test


//        pullToRefreshWebView.addView(StoreWebView);
//        pullToRefreshWebView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<WebView>() {
//            @Override
//            public void onPullEvent(PullToRefreshBase<WebView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
//                refreshView.getRefreshableView().loadUrl("http://www.yc-yunpass.com/mall/view/");
//            }
//        });
//        pullToRefreshWebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<WebView> refreshView) {
//                //pullToRefreshWebView.getRefreshableView().loadUrl("http://www.yc-yunpass.com/mall/view/");
//                pullToRefreshWebView.setRefreshing();
//            }
//        });

//        refreshableView = (SwipeRefreshLayout) view.findViewById(R.id.refreshable_view);
//        refreshableView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                    //重新刷新页面
//                StoreWebView.loadUrl(StoreWebView.getUrl());
//            }
//        });
//        refreshableView.setColorScheme(R.color.black1,
//                    R.color.gray2, R.color.burlywood,
//                    R.color.red);
//
//
//        StoreWebView = (WebView)view.findViewById(R.id.webview_info);
//
//        StoreWebView.loadUrl("http://www.yc-yunpass.com/mall/view/");
//        //添加javaScript支持
//        StoreWebView.getSettings().setJavaScriptEnabled(true);
//        //取消滚动条
//        StoreWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        //触摸焦点起作用
//        StoreWebView.requestFocus();
//        //点击链接继续在当前browser中响应
//        StoreWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    view.loadUrl(url);
//                    return true;
//                }
//        });
//        //设置进度条
//        StoreWebView.setWebChromeClient(new WebChromeClient(){
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                  //隐藏进度条
//                    refreshableView.setRefreshing(false);
//                } else {
//                    if (!refreshableView.isRefreshing())
//                        refreshableView.setRefreshing(true);
//                }
//
//                    super.onProgressChanged(view, newProgress);
//                }
//        });


//        WebView StoreWebView = (WebView)view.findViewById(R.id.webview_storeinfo);
//        refreshableView = (RefrashableView) view.findViewById(R.id.refreshable_view);
//        StoreWebView = new WebView(view.getContext());
//
//        listView=(ListView)view.findViewById(R.id.list_view);
//        arrayList=new ArrayList<HashMap<String, Object>>();
//        HashMap<String,Object> map=new HashMap<String, Object>();
//        map.put("webview",StoreWebView);
//        arrayList.add(map);
//
//        adapter = new WebViewAdapter(view.getContext(),arrayList);
//        listView.setAdapter(adapter);
//
//        WebSettings settings = StoreWebView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        StoreWebView.setHorizontalScrollBarEnabled(false);
//        StoreWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
//
//
//
//        if(!isNetworkAccessable()){
//            Toast.makeText(this.getActivity(), "当前网络不可用", Toast.LENGTH_LONG);
//        }
//        StoreWebView.loadUrl("http://www.yc-yunpass.com/mall/view/");//商店url
//
//        refreshableView.setOnRefreshListener(new RefrashableView.PullToRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //StoreWebView.loadUrl("http://www.yc-yunpass.com/mall/view/");//商店url
//                //reloadurl();
//                refreshableView.finishRefreshing();
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 0);
        return view;
    }

    private static class SampleWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl("http://www.yc-yunpass.com/mall/view/");
            return true;
        }
    }
//    private void reloadurl(){
//        arrayList.clear();
//        HashMap<String,Object> adapterMap=new HashMap<String,Object>();
//        adapterMap.put("webview",StoreWebView);
//        arrayList.add(adapterMap);//增加到list
//        adapter.notifyDataSetChanged();//更新界面
//
//    }


    public boolean isNetworkAccessable(){
        ConnectivityManager cm = (ConnectivityManager)this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
/*WebView wv = (WebView)findViewById(R.id.webview_storeinfo);
        wv.loadUrl("http://nervtech.cn/yunpass_mail/src/module/");

        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);*/