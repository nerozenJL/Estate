package com.uestc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.uestc.ui.activity.HomeActivity;
import com.uestc.ui.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mengcz on 2016/5/23.
 */
public class DepartmentSwicherDialog extends Dialog {
    public interface OnBackPressedListener{
        public void onClick();
    }

    public OnBackPressedListener onBackPressedListener;

    public DepartmentSwicherDialog(Context context) {
        super(context);
    }

    public DepartmentSwicherDialog(Context context, int theme) {
        super(context, theme);
    }

    protected DepartmentSwicherDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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
        OnClickListener ConfirmButtonClickListener;
        OnBackPressedListener onBackPressedListener;
        String[] departmentName;

        public Builder(Context context){
            this.context=context;
        }

        public DepartmentSwicherDialog create(){
            LayoutInflater inflater=(LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DepartmentSwicherDialog departmentSwicherDialog=new DepartmentSwicherDialog(context,R.style.DpSwitcherDialog);
            final View layout = inflater.inflate(R.layout.dialog_switch_department,null);
            departmentSwicherDialog.addContentView(layout,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            departmentSwicherDialog.setCanceledOnTouchOutside(false);
            final ListView listView=(ListView)layout.findViewById(R.id.department_switcher_listView);
            final Button button=(Button)layout.findViewById(R.id.department_switcher_button);
            List<Map<String,Object>> listItems=new ArrayList<Map<String, Object>>();
            for (int i=0;i<departmentName.length;i++){
                Map<String,Object> map=new HashMap<String, Object>();
                map.put("name",departmentName[i]);
                listItems.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(context,listItems,R.layout.listitem_dp_swicher,
                    new String[]{"name"},new int[]{R.id.textView_listItem_dp_switcher});
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    departmentSwicherDialog.setListItemStatus(position, true, layout);
                    if (lastposition != -1)
                        departmentSwicherDialog.setListItemStatus(lastposition, false, layout);
                    lastposition = position;
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmButtonClickListener.onClick(departmentSwicherDialog, DialogInterface.BUTTON_POSITIVE);
                }
            });
            departmentSwicherDialog.onBackPressedListener=new OnBackPressedListener() {
                @Override
                public void onClick() {
                    onBackPressedListener.onClick();
                }
            };
            return departmentSwicherDialog;
        }

        public void setOnConfirmButtonClickListner(OnClickListener onClickListener){
            ConfirmButtonClickListener=onClickListener;
        }

        public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
            this.onBackPressedListener=onBackPressedListener;
        }

        public int getLastPositon(){
            return lastposition;
        }

        public void setListItem(String[] list_Item){
            departmentName=list_Item;
        }

        public void setListItem(Map list_Item){
            Iterator<String> keySet=list_Item.keySet().iterator();
            int length=list_Item.keySet().size();
            departmentName=new String[length];
            int i=0;
            while (keySet.hasNext()){
                departmentName[i]=keySet.next();
                i++;
            }
        }
    }

    private void setListItemStatus(int position,boolean isChecked,View dialogLayout){
        final ListView listView=(ListView)dialogLayout.findViewById(R.id.department_switcher_listView);
        View itemView = listView.getChildAt(position);
        listView.getAdapter().getView(position, itemView, listView);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.imageView_listItem_dp_switcher);
        if (isChecked){
            imageView.setImageResource(R.drawable.dp_choose_icon);
        }else {
            imageView.setImageDrawable(null);
        }
    }
}
