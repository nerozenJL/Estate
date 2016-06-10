package com.uestc.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;
import com.uestc.domain.LeaseHistoryBean;
import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaseIncomeFragment extends Fragment {


    public LeaseIncomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news, null);
        android.util.Log.d("mark", "onCreateView()--------->news Fragment");
        ListView plv = (ListView)view.findViewById(R.id.lease_history_pinned_list_view);
        Map<String, List<LeaseHistoryBean>> map = new HashMap<String, List<LeaseHistoryBean>>();
        List<LeaseHistoryBean> list = new ArrayList<LeaseHistoryBean>();
        list.add(new LeaseHistoryBean(1,1, "蓝光一号", "2:00-3:00"));
        list.add(new LeaseHistoryBean(1,1, "蓝光二号", "2:00-3:00"));
        map.put("2016-2-1", list);
        map.put("2016-2-2", list);
        plv.setAdapter(new AdapterPinnedSectionListView(container.getContext(), map));
        return view;
    }

    private class AdapterPinnedSectionListView extends BaseAdapter
            implements PinnedSectionListView.PinnedSectionListAdapter {

        private final static int SECTION_TYPE_HEADER = 0;
        private final static int SECTION_TYPE_CONTENT = 1;
        private Map historyMap;
        private List<LeaseHistoryBean> historyBeanList;
        private Context context;

        public AdapterPinnedSectionListView(Context context, Map historyMap) {
            //传入日期——历史记录列表的映射，日期将会成为header，其下的记录列表会成为这个header下面的内容
            this.historyMap = historyMap;
            this.historyBeanList = InitHistoryBeanList(this.historyMap); //按照日期排序
            this.context = context;
        }

        private List InitHistoryBeanList(Map<String, List<LeaseHistoryBean>> historyMap) {
            historyBeanList = new ArrayList<LeaseHistoryBean>();
            Set<String> dateSet = historyMap.keySet();
            for(String date : dateSet) {
                historyBeanList.add(new LeaseHistoryBean(0, date));
                List<LeaseHistoryBean> historyDetailContentList = (List<LeaseHistoryBean>)historyMap.get(date);
                Iterator<LeaseHistoryBean> iterator = historyDetailContentList.iterator();
                while (iterator.hasNext()) {
                    historyBeanList.add(iterator.next());
                }
            }
            return historyBeanList;
        }

        @Override
        public int getItemViewType(int position) {
            return historyBeanList.get(position).getSECTIONTYPE(); //获取当前下标的类型，header还是content
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            //我猜测是使用区别渲染的方法，按照viewtype的值类型来绘制不一样的list格式
            //定义日期头的type为0，内容的type为1
            return viewType == LeaseHistoryBean.SECTIONTYPE_HEADER;
        }

        @Override
        public int getCount() {
            int count = 0;
            count = historyMap.size(); //先计算有多少天
            Set<String> dateSet = historyMap.keySet();
            for(String date : dateSet) {
                count += (((List<LeaseHistoryBean>)(historyMap.get(date))).size()); //每一天下面有多少条记录
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return historyBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2; //只有header和content两类
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                //首次载入初始化
                switch (getItemViewType(position)) {
                    case SECTION_TYPE_HEADER: //日期部分
                        convertView = LayoutInflater.from(this.context).
                                inflate(R.layout.layout_lease_history_date_header, parent, false); //载入头部布局
                        TextView tv = (TextView)convertView.findViewById(R.id.tv_lease_record_set_date);
                        tv.setText(historyBeanList.get(position).getDate());
                        break;
                    case SECTION_TYPE_CONTENT: //记录正文部分
                        convertView = LayoutInflater.from(this.context).
                                inflate(R.layout.layout_lease_history_content_body, parent, false);
                        //
                        TextView stateTv = (TextView) convertView.findViewById(R.id.tv_lease_state_remark);
                        stateTv.setText("+12.30");
                        TextView stopNameTv = (TextView)convertView.findViewById(R.id.tv_stop_name_lease_record);
                        stopNameTv.setText("蓝光停车位1号");
                        TextView periodTv = (TextView)convertView.findViewById(R.id.tv_lease_plan_executable_period);
                        periodTv.setText("8:00 —— 10:00");
                        convertView.setTag(new contentView(stateTv, stopNameTv, periodTv));
                        break;
                    default:
                        break;
                }
            }else {
                switch (getItemViewType(position)) {
                    case SECTION_TYPE_HEADER:
                        headerView hv = (headerView) convertView.getTag();
                        break;
                    case SECTION_TYPE_CONTENT:
                        contentView cv = (contentView)convertView.getTag();
                        break;
                }

            }
            return convertView;
        }


        private class contentView {
            public TextView state;
            public TextView stopName;
            public TextView planPeriod;
            public contentView(TextView state, TextView stopName, TextView planPeriod) {
                this.state = state;
                this.stopName = stopName;
                this.planPeriod = planPeriod;
            }
        }
        private class headerView {
            public TextView date;
        }
    }
}
