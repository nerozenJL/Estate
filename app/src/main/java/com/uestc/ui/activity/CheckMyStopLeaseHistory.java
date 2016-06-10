package com.uestc.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabWidget;
import android.widget.TextView;

import com.uestc.ui.fragment.LeaseIncomeFragment;
import com.uestc.ui.fragment.LeaseHistoryFragment;

public class CheckMyStopLeaseHistory extends FragmentActivity {

    private FragmentTabHost topTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_my_stop_lease_history);
        InitViews();
        ImageButton imageButton = (ImageButton)findViewById(R.id.back_from_check_lease_history);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckMyStopLeaseHistory.this, MyStopActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void InitViews() {
        topTabs = (FragmentTabHost)findViewById(R.id.fragment_tabhost_check_stop_lease_history);
        topTabs.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        topTabs.getTabWidget().setDividerDrawable(null);

        topTabs.addTab(topTabs.newTabSpec("0").setIndicator("租用历史"), LeaseHistoryFragment.class, null);
        topTabs.addTab(topTabs.newTabSpec("1").setIndicator("收益情况"), LeaseIncomeFragment.class, null);

        TabWidget tabWidget = topTabs.getTabWidget();
        for (int i=0; i<tabWidget.getChildCount(); i++) {
            View view = tabWidget.getChildAt(i);
            view.setBackgroundResource(R.drawable.selector_check_lease_history_tob_tab_selector);//设置颜色转变
            TextView textView = (TextView)view.findViewById(android.R.id.title);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
        }
    }
}

/**
 viewPager = (ViewPager)findViewById(R.id.top_tab_viewpagers_lease_history);
 LayoutInflater tabsInflater = getLayoutInflater();
 checkOrderDetailTabView = tabsInflater.inflate(R.layout.tab_layout_check_lease_order_details, null);
 checkLeaseIncomeTabView = tabsInflater.inflate(R.layout.tab_layout_check_lease_income_detail, null);

 tabsViewList = new ArrayList<View>();
 tabsViewList.add(checkOrderDetailTabView);
 tabsViewList.add(checkLeaseIncomeTabView);

 viewPager.setAdapter(new MyPagerAdapter(tabsViewList));

 TextView checkOrderDetailtv = (TextView)findViewById(R.id.tv_check_lease_order_detail);
 TextView checkIncomeDetailtv = (TextView)findViewById(R.id.tv_check_lease_income_detail);

 checkIncomeDetailtv.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
switch (viewPager.getCurrentItem()) {
case 0:
viewPager.setCurrentItem(1);
break;
case 1:
break;
}
}
});

 checkOrderDetailtv.setOnClickListener(new View.OnClickListener(){
@Override
public void onClick(View v) {
switch (viewPager.getCurrentItem()) {
case 1:
viewPager.setCurrentItem(0);
break;
case 0:
break;
}
}
});*/
