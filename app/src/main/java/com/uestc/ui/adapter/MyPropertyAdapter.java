package com.uestc.ui.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.domain.MyProperty;
import com.uestc.domain.Session;
import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

public class MyPropertyAdapter extends BaseAdapter {

	private Context mContext;
	private List<MyProperty> list;
	private ArrayList<CheckItem> arrayList;
	private Listenner listenner = new Listenner();

	public MyPropertyAdapter(Context mContext, List<MyProperty> list) {
		this.list = list;
		this.mContext = mContext;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		final ViewHolder viewHolder;
		if (arg1 == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			arg1 = inflater.inflate(R.layout.item_myproperty, arg2, false);
			viewHolder = new ViewHolder();
			viewHolder.code = (TextView) arg1.findViewById(R.id.tv_code_item_myproperty);
			viewHolder.location = (TextView) arg1.findViewById(R.id.tv_location_item_myproperty);
			viewHolder.propertySquare = (TextView) arg1
					.findViewById(R.id.tv_propertysquare_item_myproperty);
			viewHolder.status = (TextView) arg1.findViewById(R.id.tv_status_item_myproperty);

			viewHolder.type = (TextView) arg1
					.findViewById(R.id.tv_type_item_myproperty);
			viewHolder.role = (TextView)arg1.findViewById(R.id.tv_role_item_myproperty);
			viewHolder.property_status = (TextView)arg1.findViewById(R.id.tv_bind_status_item_myproperty);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		viewHolder.code.setText(list.get(arg0).getCode());
		viewHolder.location.setText(list.get(arg0).getLocation()+list.get(arg0).getBuildingName());
		viewHolder.status.setText(list.get(arg0).getStatus());
		viewHolder.propertySquare.setText(list.get(arg0).getPropertySquare());
		viewHolder.type.setText(list.get(arg0).getType());
		if (list.get(arg0).getRole() == 3){
			viewHolder.role.setText("业主");
		}
		if (list.get(arg0).getRole() == 1)
			viewHolder.role.setText("家属");
		if (list.get(arg0).getRole() == 2)
			viewHolder.role.setText("租户");

		viewHolder.property_status.setText(list.get(arg0).getProperty_status());

		return arg1;
	}

	class CheckItem {
		int position;
		boolean ischeck = true;

		public CheckItem(boolean ischeck) {
			this.ischeck = ischeck;
		}

		public void setChick(boolean ischeck) {
			this.ischeck = ischeck;
		}
	}

	class Listenner implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			CheckItem item = (CheckItem) arg0.getTag();
			setCheck(item.position, !item.ischeck);
		}
	}

	private void setCheck(int postion, boolean ischeck) {
		if (!ischeck) {
			Toast.makeText(mContext, "不能取消勾选，请直接选择其余的物业", Toast.LENGTH_SHORT)
					.show();
		} else {
			arrayList.set(postion, new CheckItem(ischeck));
			for (int i = 0; i < postion; i++) {
				arrayList.get(i).ischeck = false;
			}
			for (int i = postion + 1; i < arrayList.size(); i++) {
				arrayList.get(i).ischeck = false;
			}

			int id = list.get(postion).getId();
			Session.setId(id);
		}

		this.notifyDataSetChanged();
	}

	class ViewHolder {
		private TextView code, type, status, propertySquare, location,property_status,role;

	}
}
