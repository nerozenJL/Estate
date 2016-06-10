package com.uestc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mengcz on 2016/5/23.
 */
public class ListViewDialog extends Dialog {
    public interface OnBackPressedListener{
        public void onClick();
    }

    public OnBackPressedListener onBackPressedListener;

    public ListViewDialog(Context context) {
        super(context);
    }

    public ListViewDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ListViewDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
    public void onBackPressed(){
        onBackPressedListener.onClick();
        super.onBackPressed();
    }

    public static class Builder {

        Context context;
        int lastposition=-1;
        boolean isMultyChoosen=false;
        OnClickListener ConfirmButtonClickListener;
        OnBackPressedListener onBackPressedListener;
        String[] ListItemName=new String[]{"departmentA","departmentB","departmentC","departmentD"};
        String title="TITLE";
        String defaultListItem=null;
        public int[] statusint;


        public Builder(Context context){
            this.context=context;
        }

        public ListViewDialog create(int defaultindex){
            statusint=new int[ListItemName.length];
            for (int i:statusint){
                i=0;
            }
            LayoutInflater inflater=(LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ListViewDialog listViewDialog =new ListViewDialog(context,R.style.DpSwitcherDialog);
            final View layout = inflater.inflate(R.layout.dialog_switch_department,null);
            listViewDialog.addContentView(layout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            listViewDialog.setCanceledOnTouchOutside(false);
            final ListView listView=(ListView)layout.findViewById(R.id.department_switcher_listView);
            final Button button=(Button)layout.findViewById(R.id.department_switcher_button);
            TextView textView=(TextView)layout.findViewById(R.id.textView_title_switchdepartment);
            textView.setText(title);
            List<Map<String,Object>> listItems=new ArrayList<Map<String, Object>>();
            for (int i=0;i<ListItemName.length;i++){
                Map<String,Object> map=new HashMap<String, Object>();
                map.put("name",ListItemName[i]);
                listItems.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(context,listItems,R.layout.listitem_dp_swicher,
                    new String[]{"name"},new int[]{R.id.textView_listItem_dp_switcher});
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!isMultyChoosen){
                        listViewDialog.setListItemStatus(position, true, listView);
                        statusint[position]=1;
                        if (lastposition != -1){
                            listViewDialog.setListItemStatus(lastposition, false, listView);
                            statusint[lastposition]=0;
                        }
                    }else {
                        if (statusint[position]==0){
                            listViewDialog.setListItemStatus(position, true, listView);
                            statusint[position]=1;
                        }else {
                            listViewDialog.setListItemStatus(position, false, listView);
                            statusint[position]=0;
                        }
                    }
                    lastposition=position;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmButtonClickListener.onClick(listViewDialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
            listViewDialog.onBackPressedListener=new OnBackPressedListener() {
                @Override
                public void onClick() {
                    onBackPressedListener.onClick();
                }
            };
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    for (int i=0;i<statusint.length;i++){
                        if (statusint[i]==1){

                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
//            setDefaultListItem(defaultindex);
//            defaultListItem=ListItemName[defaultindex];
//            if (defaultListItem!=null){
//                for (int i=0;i<ListItemName.length;i++){
//                    if (ListItemName[i].equals(defaultListItem)){
//                        listViewDialog.setListItemStatus(i,true,listView);
//                        break;
//                    }
//                }
//            }

            return listViewDialog;
        }

        public void setOnConfirmButtonClickListner(OnClickListener onClickListener){
            ConfirmButtonClickListener=onClickListener;
        }

        public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
            this.onBackPressedListener=onBackPressedListener;
        }
        public void setDefaultListItem(int index){


        }
        public void setMultyChoosen(boolean isMultyChoosen){
            this.isMultyChoosen=isMultyChoosen;
        }
        public void setListItemName(String[] ListItemName){
            this.ListItemName=ListItemName;
        }
        public void setTitle(String title){
            this.title=title;
        }
        public void setListItemName(Map map){
            Set set = map.keySet();
            Iterator iter = set.iterator();
            String[] name=new String[map.size()];
            int i=0;
            while (iter.hasNext()) {
                String key = (String) iter.next();
                // printkey
                name[i]=key;
                i++;
            }
            ListItemName=name;
        }
        public String getKey(){
            return ListItemName[lastposition];
        }
        public void setDefaultListItem(String DefaultListItem){
            defaultListItem=DefaultListItem;
        }
    }

    private void setListItemStatus(int position,boolean isChecked,ListView listView){
        View itemView = listView.getChildAt(-listView.getFirstVisiblePosition()+position);
        listView.getAdapter().getView(position, itemView, listView);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.imageView_listItem_dp_switcher);
        if (isChecked){
            imageView.setImageResource(R.drawable.dp_choose_icon);
        }else {
            imageView.setImageDrawable(null);
        }
    }
}
