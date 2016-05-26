package com.uestc.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uestc.domain.Review;
import com.uestc.ui.activity.R;
import com.uestc.constant.Host;
import com.uestc.utils.HttpUtils;

import org.json.JSONObject;

import java.util.List;

public class OwerReviewAdapter extends BaseAdapter {
	private Context context;
	private List<Review> list;
	private int id;

	public OwerReviewAdapter(Context context, List<Review> list) {
		this.context = context;
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
			LayoutInflater inflater = LayoutInflater.from(context);
			arg1 = inflater.inflate(R.layout.item_ower_review, arg2, false);
			viewHolder = new ViewHolder();
			viewHolder.phone = (TextView) arg1.findViewById(R.id.tv_phone_item_owner_review);
			viewHolder.name = (TextView) arg1.findViewById(R.id.tv_name_item_owner_review);
			viewHolder.address = (TextView) arg1.findViewById(R.id.tv_address_item_owner_review);
			viewHolder.agree = (Button) arg1.findViewById(R.id.btn_agree_item_owner_review);
			viewHolder.refuse = (Button) arg1.findViewById(R.id.btn_refuse_item_owner_review);
//			viewHolder.code = (TextView) arg1
//					.findViewById(R.id.owerreview_code);
			viewHolder.role= (TextView) arg1.findViewById(R.id.tv_role_item_owner_review);
			arg1.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) arg1.getTag();
		}
		viewHolder.phone.setText(list.get(arg0).getPhone());
		viewHolder.address.setText(list.get(arg0).getAddress());
		viewHolder.name.setText(list.get(arg0).getName());
//		viewHolder.code.setText(list.get(arg0).getCode());
		viewHolder.role.setText(list.get(arg0).getRole());
		viewHolder.agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				id = arg0;
				showDialog(arg0);
			}
		});
		viewHolder.refuse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				id = arg0;
				showDialog1(arg0);
			}
		});
		return arg1;
	}

	class ViewHolder {
		private TextView name, phone, address,role;
		private Button agree, refuse;
	}

	class agreeAsyncTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... arg0) {
			String string = HttpUtils.HttpGet(context, Host.agreeBind+list.get(arg0[0]).getId());
			return string;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			if (!result.equals("")) {

				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						new HandleAsyncTask().execute(id);
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(context,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, "检查网络设置", Toast.LENGTH_SHORT).show();
			}
		}

	}

	class RefuseAsyncTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... arg0) {

			String string = HttpUtils.HttpGet(context, Host.refuseBind+list.get(arg0[0]).getId());
			return string;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						new HandleAsyncTask1().execute(id);
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						Toast.makeText(context,
								object2.getString("description"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, "检查网络设置", Toast.LENGTH_SHORT).show();
			}

		}
	}

	class HandleAsyncTask extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {

			int a = params[0];
			return a;
		}

		@Override
		protected void onPostExecute(Integer result) {

			super.onPostExecute(result);
			list.remove(list.get(result));
			OwerReviewAdapter.this.notifyDataSetChanged();
			Toast.makeText(context, "已同意", Toast.LENGTH_SHORT).show();
		}
	}

	class HandleAsyncTask1 extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {

			int a = params[0];
			return a;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			list.remove(list.get(result));
			OwerReviewAdapter.this.notifyDataSetChanged();
			Toast.makeText(context, "已拒绝", Toast.LENGTH_SHORT).show();
		}
	}

	public void showDialog(final int m) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("确定同意吗");
		builder.setPositiveButton("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				new agreeAsyncTask().execute(m);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void showDialog1(final int m) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("确定拒绝吗");
		builder.setPositiveButton("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		builder.setNegativeButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				new RefuseAsyncTask().execute(m);
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
