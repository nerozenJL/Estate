package com.uestc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uestc.domain.MyComplain;
import com.uestc.ui.activity.R;

import java.util.List;

public class MyComplairAdapter extends BaseAdapter {
	private Context mContext;
	private List<MyComplain> list;
//	private int mRightWidth = 0;
//	private onRightItemClickListener mListener = null;

	public MyComplairAdapter(Context mContext, List<MyComplain> list
//			, int mRightWidth
	) {
		this.mContext = mContext;
		this.list = list;
//		this.mRightWidth = mRightWidth;
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
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		

		final ViewHolder viewHolder;
		if (arg1 == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			arg1 = inflater.inflate(R.layout.item_myrepair, arg2, false);
			viewHolder = new ViewHolder();
//			viewHolder.item_right = (RelativeLayout) arg1
//					.findViewById(R.id.item_right);
//			viewHolder.item_left = (LinearLayout) arg1
//					.findViewById(R.id.left_linearLayout);
			viewHolder.title = (TextView) arg1.findViewById(R.id.tv_title_item_myrepair);
			viewHolder.tv_status =(TextView) arg1.findViewById(R.id.tv_status_item_myrepair);

			viewHolder.status = (ImageView) arg1.findViewById(R.id.iv_status_item_myrepair);
			viewHolder.time = (TextView) arg1.findViewById(R.id.tv_time_item_myrepair);
			viewHolder.description = (TextView) arg1.findViewById(R.id.tv_descriptiontv_item_myrepair);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		
		viewHolder.description.setText(list.get(arg0).getDescription());
		if (list.get(arg0).getStatus() == 0) {
			viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wait_save));
		}else {
			viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.saved));
		}
		if (list.get(arg0).getStatus() == 0){
			viewHolder.tv_status.setText("待处理");
		}else if(list.get(arg0).getStatus() == 1){
			viewHolder.tv_status.setText("处理中");
		}else if(list.get(arg0).getStatus() == 2){
			viewHolder.tv_status.setText("待评价");
		}else if(list.get(arg0).getStatus() == 3){
			viewHolder.tv_status.setText("已评价");
		}

		viewHolder.title.setText(list.get(arg0).getTitle());
		viewHolder.time.setText(list.get(arg0).getTime());
		
//		LinearLayout.LayoutParams lp1 = new LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		viewHolder.item_left.setLayoutParams(lp1);
//		LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth,
//				LayoutParams.MATCH_PARENT);
//		viewHolder.item_right.setLayoutParams(lp2);
//
//		viewHolder.item_right.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (mListener != null) {
//					mListener.onRightItemClick(v, arg0);
//				}
//			}
//		});
		return arg1;
	}

	class ViewHolder {
		private TextView title, time,description,tv_status;
//		private RelativeLayout item_right;
		private LinearLayout item_left;
		private ImageView status;
	}

//	public void setOnRightItemClickListener(onRightItemClickListener listener) {
//		mListener = listener;
//	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}

}
