package com.uestc.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyStopActivity extends ActionBarActivity {
    private ListView functionListView;

    private List<FunctionListItem> functionItemList = new ArrayList<FunctionListItem>(); //功能列表的列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_stop);
        InitUIComponents();
    }

    /*初始化"我的车位"的UI组件*/
    private void InitUIComponents() {
        //功能列表初始化
        InitFunctionList();
        //添加事件监听
    }

    private void InitFunctionList() {
        functionListView = (ListView)findViewById(R.id.my_stop_function_list);
        InitFunctionItemList();
        functionListView.setAdapter(new FunctionListAdapter(this.functionItemList));
        functionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(MyStopActivity.this, "haha", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private class FunctionListAdapter extends BaseAdapter {

        private List<FunctionListItem> itemList;

        public FunctionListAdapter(List<FunctionListItem> functionListItemList) {
            this.itemList = functionListItemList;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            FunctionListItemView functionListItemView;
            if(convertView == null) {
                convertView = LayoutInflater.from(MyStopActivity.this).inflate(R.layout.item_my_stop_function_list, null);
                functionListItemView = new FunctionListItemView();
                functionListItemView.imageView = (ImageView) convertView.findViewById(R.id.my_stop_function_list_item_icon);
                functionListItemView.textView = (TextView)convertView.findViewById(R.id.my_stop_function_list_item_text);
                convertView.setTag(functionListItemView);
                /*convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch(position) { //利用传进来的position表明按到的到底是哪个功能选项
                            case 0:
                                //Toast.makeText(MyStopActivity.this, "查看车位信息", Toast.LENGTH_SHORT).show();
                                JumpToMyStopsInfo();
                                break;
                            case 1:
                                JumpToStopsLeaseStatus();
                                break;
                            case 2:
                                JumpToSetExceptionStragety();
                                break;
                            case 3:
                                JumpToLeasHistory();
                                break;
                        }
                    }
                });*/

            }else {
                 functionListItemView = (FunctionListItemView)convertView.getTag();
            }

            Drawable drawable = itemList.get(position).functionIcon;
            String text = itemList.get(position).functionName;

            functionListItemView.imageView.setImageDrawable(drawable);
            functionListItemView.textView.setText(text);



            return convertView;
        }
    }

    private void JumpToSetExceptonStragety() {
        Intent intent = new Intent();
    }

    private void JumpToStopsLeaseStatus() {
    }

    private void JumpToMyStopsInfo() {
    }

    private void JumpToLeasHistory() {

    }

    private void InitFunctionItemList() {
        this.functionItemList.add(new FunctionListItem("车位信息", getResources().getDrawable(R.drawable.check_my_stops)));
        this.functionItemList.add(new FunctionListItem("车位出租情况", getResources().getDrawable(R.drawable.check_my_stops_lease_status)));
        this.functionItemList.add(new FunctionListItem("设置特殊情况执行策略", getResources().getDrawable(R.drawable.set_exception_process)));
        this.functionItemList.add(new FunctionListItem("租用记录", getResources().getDrawable(R.drawable.check_lease_history)));
    }

    private class FunctionListItemView {
        public TextView textView;
        public ImageView imageView;
    }

    private class FunctionListItem {
        public String functionName;
        public Drawable functionIcon;

        public FunctionListItem(String functionName, Drawable functionIcon) {
            this.functionIcon = functionIcon;
            this.functionName = functionName;
        }
    }
}


