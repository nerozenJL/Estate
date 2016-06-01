package com.uestc.ui.activity;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CheckMyStopLeaseHistory extends Activity {

    private ViewPager viewPager;

    private List<View> tabsViewList; //存放导航栏组件的列表
    private View checkOrderDetailTabView, checkLeaseIncomeTabView; //viewpage下两个标签组件，一个是查看订单详情，一个是查看收益情况

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_my_stop_lease_history);
        InitUIcomponents();
    }

    private void InitUIcomponents() {
        /*初始化顶部导航栏*/
        viewPager = (ViewPager)findViewById(R.id.top_tab_viewpagers_lease_history);
        LayoutInflater tabsInflater = getLayoutInflater();
        checkOrderDetailTabView = tabsInflater.inflate(R.layout.tab_layout_check_lease_order_details, null);
        checkLeaseIncomeTabView = tabsInflater.inflate(R.layout.tab_layout_check_lease_income_detail, null);

        tabsViewList = new ArrayList<View>();
        tabsViewList.add(checkOrderDetailTabView);
        tabsViewList.add(checkLeaseIncomeTabView);

        viewPager.setAdapter(new MyPagerAdapter(tabsViewList));
    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<View> viewList;

        public MyPagerAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {
            return this.viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }
    }
}
