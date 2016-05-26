package com.uestc.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这是把时间戳转成yyyy-MM-dd格式的方式
 */
public class GetData {

	@SuppressLint("SimpleDateFormat") 
	public static String getdata(Long time){
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(time);
		String string = formatter.format(curDate);
		return string;
	}
	
	
}
