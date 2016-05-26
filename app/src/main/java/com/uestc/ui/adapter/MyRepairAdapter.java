package com.uestc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uestc.domain.MyRepair;
import com.uestc.ui.activity.R;

import java.util.List;

/**
 * Created by Ryon on 2015/10/29.
 * email:shadycola@gmail.com
 */
public class MyRepairAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyRepair> list;

    public MyRepairAdapter(Context mContext, List<MyRepair> list) {
        this.mContext = mContext;
        this.list = list;
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
            viewHolder.title = (TextView) arg1.findViewById(R.id.tv_title_item_myrepair);
            viewHolder.status = (ImageView) arg1.findViewById(R.id.iv_status_item_myrepair);
            viewHolder.statusText = (TextView) arg1.findViewById(R.id.tv_status_item_myrepair);
            viewHolder.time = (TextView) arg1.findViewById(R.id.tv_time_item_myrepair);
            viewHolder.description = (TextView) arg1.findViewById(R.id.tv_descriptiontv_item_myrepair);
//            viewHolder.review = (Button) arg1.findViewById(R.id.add_review);
//            viewHolder.delete = (Button) arg1.findViewById(R.id.delete_record);
            arg1.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) arg1.getTag();
        }

        viewHolder.description.setText(list.get(arg0).getDescription());

        switch (list.get(arg0).getStatus()){
            case 0:
                viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wait_save));
                viewHolder.statusText.setText("待处理");
                break;
            case 1:
                viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wait_save));
                viewHolder.statusText.setText("处理中");
                break;
            case 2:
                viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wait_save));
                viewHolder.statusText.setText("待评价");
                break;
            case 3:
                viewHolder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.saved));
                viewHolder.statusText.setText("已评价");
                break;
            default:
                break;
        }
        viewHolder.title.setText(list.get(arg0).getTitle());
        viewHolder.time.setText(list.get(arg0).getSubmitTime());

//        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                list.remove(arg0);
//                notifyDataSetChanged();
//
//            }
//        });
        return arg1;
    }

    class ViewHolder {
        private TextView title, time,description,statusText;
        private ImageView status;
//        private Button review,delete;
    }


}
