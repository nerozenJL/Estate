package com.uestc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uestc.domain.Announcement;
import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<Announcement> list;
	
	public  AnnouncementAdapter(Context mContext) {
		this.mContext=mContext;
		list =new ArrayList<Announcement>();
	}

	public void AddData(List<Announcement> list){
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (arg1 == null) {
			LayoutInflater inflater=LayoutInflater.from(mContext);
			arg1=inflater.inflate(R.layout.adapter_announcement, arg2, false);
			viewHolder=new ViewHolder();
			viewHolder.title=(TextView) arg1.findViewById(R.id.tv_title_adapter_announcement);
//			viewHolder.content=(TextView) arg1.findViewById(R.id.content);
//			viewHolder.time=(TextView) arg1.findViewById(R.id.time);
			arg1.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder) arg1.getTag();
		}
		
		viewHolder.title.setText(list.get(arg0).getTitle());
//		viewHolder.content.setText(list.get(arg0).getDescription());
//		viewHolder.time.setText(list.get(arg0).getTime());
		return arg1;
	}
	class ViewHolder{
		private TextView title,content,time;
	}
}
