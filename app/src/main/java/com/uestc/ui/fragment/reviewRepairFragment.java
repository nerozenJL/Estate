//package com.uestc.ui.fragment;
//
//
//
//import android.app.Fragment;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.InflateException;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.znt.ui.activity.adapter.MyRepairAdapter;
//import com.znt.data.MyRepair;
//import com.znt.estate.MyRepairActivity;
//import com.znt.estate.R;
//import com.znt.constant.Host;
//import com.znt.utils.HttpUtils;
//import com.znt.utils.GetData;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Ryon on 2015/11/11.
// * email:shadycola@gmail.com
// */
//public class reviewRepairFragment extends Fragment {
//
//    private View view;
//    private ListView listView;
//    private List<MyRepair> list;
//    private MyRepairAdapter mAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (view == null) {
//            try {
//                view = inflater.inflate(R.layout.repair_list_frag, container, false);
//            } catch (InflateException ie) {
//                ie.printStackTrace();
//            } finally {
//                if (view == null)
//                    return view;
//            }
//
//            list = new ArrayList<MyRepair>();
//            listView = (ListView) getActivity().findViewById(R.id.myrepair_listview);
//            new getRepairListASyncTask() .execute();
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                        long arg3) {
//                    MyRepair myRepair;
//                    myRepair = list.get(arg2);
//                    Intent intent = new Intent(getActivity(), MyRepairActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("myRepair", myRepair);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            });
//
//            return view;
//        }
//        return view;
//    }
//
//    class getRepairListASyncTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... arg0) {
//            String string = HttpUtils.HttpGet(getActivity(), Host.getRepair+"?status=1");
//            Log.d("defaultRepairFragment",string);
//            return string;
//        }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//        if (!result.equals("")) {
//            Log.e("result", result);
//            JSONObject object;
//            try {
//                object = new JSONObject(result);
//                if (object.getBoolean("status")) {
//                    JSONArray jsonArray = object.getJSONArray("jsonString");
//                    if (list.size() != 0) {
//                        list.removeAll(list);
//                    }
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        JSONObject object2 = jsonArray.getJSONObject(i);
//                        MyRepair repair = new MyRepair();
//                        repair.setId(object2.getInt("id"));
//                        repair.setTitle(object2.getString("title"));
//                        repair.setContent(object2.getString("content"));
//                        String description = object2.getString("description");
//
//                        if(!object2.getString("remark").equals("")&&!object2.getString("remark").equals("null")) {
//                            repair.setRemark(object2.getDouble("remark"));
//                        }else{
//                            repair.setRemark(100);
//                        }
//
//                        if(!object2.getString("remarkText").equals("null")) {
//                            repair.setRemarkText(object2.getString("remarkText"));
//                        }else{
//                            repair.setRemarkText("");
//                        }
//                        if(!object2.getString("repairManEntity").equals("")&&!object2.getString("repairManEntity").equals("null")) {
//                            JSONObject repairManObj = object2.getJSONObject("repairManEntity");
//                            repair.setRepairPhone(repairManObj.getString("phone"));
//                            repair.setRepairName(repairManObj.getString("name"));
//                        }else {
//                            repair.setRepairPhone("暂无数据");
//                            repair.setRepairName("暂无数据");
//                        }
//
//                        if (description!=null&&!description.equals("")&&!description.equals("null")) {
//                            repair.setDescription(description);
//                        }else {
//                            repair.setDescription("");
//                        }
//                        String result1 = object2.getString("result");
//                        if (result1.equals("") || result1.equals("null")) {
//                            repair.setResult("");
//                        } else {
//                            repair.setResult(result1);
//                        }
////							repair.setType(object2.getInt("type"));
//                        repair.setSubmitTime(GetData.getdata(object2.getLong("submitTime")));
//                        repair.setStatus(object2.getInt("status"));
//                        list.add(repair);
//                    }
//
//                    mAdapter = new MyRepairAdapter(getActivity(), list);
//
//                    listView.setAdapter(mAdapter);
//                } else {
//                    JSONObject object2 = object.getJSONObject("errorMsg");
//
//                    Toast.makeText(getActivity(),
//                            object2.getString("description"),
//                            Toast.LENGTH_SHORT).show();
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            Toast.makeText(getActivity(), "检查网络设置", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//    }
//}