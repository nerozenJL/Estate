package com.uestc.utils;

import android.content.Context;

import com.qihoo.linker.logcollector.upload.HttpParameters;
import com.uestc.domain.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * 这是http get方法的封装
 */
public class HttpUtils {

	public static String HttpGet(Context context, String url) {
		String result = "";
		HttpGet httpGet = new HttpGet(url);

		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3500);
		defaultHttpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3500);

		//httpGet.setParams();
		httpGet.setHeader("cookie", Session.getSeesion());
		HttpResponse httpResponse = null;
		try {
			httpResponse = defaultHttpClient.execute(httpGet);
			result = EntityUtils.toString(httpResponse.getEntity());
		}catch (ConnectionPoolTimeoutException e){
			e.printStackTrace();
			return "";
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			return "";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return result;
	}
	

//	public static String HttpPost(String url, String jsonString) {
//		String result = null;
//		try {
//			HttpClient httpClient = new DefaultHttpClient();
//			HttpPost post = new HttpPost(url);
//			post.setHeader("cookie", Session.getSeesion());
//			
//			post.setEntity(new StringEntity(jsonString));
//			HttpResponse httpResponse = httpClient.execute(post);
//			result = EntityUtils.toString(httpResponse.getEntity());
//
//		} catch (Exception e) {
//			
//		}
//		return result;
//	}

}
