package com.uestc.domain;


import java.util.ArrayList;
import java.util.List;
/**
 * 这报修里面选择时间的数据
 */
public class Times {
	private List<String> list2;
	private String time;
	

	
	public List<String> getTime(){
		list2=new ArrayList<String>();
		time="请选择上门时间";
		list2.add(time);
		time="08--10";
		list2.add(time);
		time="10--12";
		list2.add(time);
		time="12--14";
		list2.add(time);
		time="14--16";
		list2.add(time);
		time="16--18";
		list2.add(time);
		time="18--20";
		list2.add(time);
		return list2;
	}
	
}
