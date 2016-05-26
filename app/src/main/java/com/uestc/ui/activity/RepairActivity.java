package com.uestc.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.MyComplain;
import com.uestc.domain.Session;
import com.uestc.domain.Times;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是增加维修的activity
 */
public class RepairActivity extends Activity {

	private ImageButton back, upload_picture;
	private EditText what, content;
	private Spinner spinner;
	private List<String> list;
	private Times times;
	private ArrayAdapter<String> arrayAdapter;
	private ImageButton ensure;
	private static AlertDialog dialog;
	private RelativeLayout relativeLayout1, relativeLayout2;
	private LinearLayout pictureLayout;
	private int m = 0;
	private String picturePath;
	private File file;
	private List<File> files;
	private LayoutInflater layoutInflater;
	private MyComplain repair;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_repair_submit);
		ActivityCollector.addActivity(this);

		files = new ArrayList<File>();
		layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		repair = new MyComplain();
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initData() {
		list = new ArrayList<String>();
		times = new Times();
		list = times.getTime();
		arrayAdapter = new ArrayAdapter<String>(RepairActivity.this,android.R.layout.simple_spinner_item, list);
		spinner.setAdapter(arrayAdapter);
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.ib_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletePic();
				finish();
			}
		});
		upload_picture = (ImageButton) findViewById(R.id.ib_upload_picture_repair_submit);
		upload_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialog();
			}
		});
		what = (EditText) findViewById(R.id.et_what_repair_submit);// ��ʲô

		spinner = (Spinner) findViewById(R.id.sp_times_repair_submit);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				m = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		content = (EditText) findViewById(R.id.et_detail_repair_submit);
		ensure = (ImageButton) findViewById(R.id.ib_ensure);
		ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!what.getText().toString().equals("")) {
					repair.setTitle(what.getText().toString());
					if (m != 0) {
						repair.setTimes(list.get(m));
						repair.setContent(content.getText().toString());
						
						if (repair != null) {
							new RepairAsyncTask().execute(repair);
						}

					} else {
						Toast.makeText(RepairActivity.this, "请选择上门时间",
								Toast.LENGTH_SHORT).show();
					}
				} else {

					Toast.makeText(RepairActivity.this, "请填写修什么",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		pictureLayout = (LinearLayout) findViewById(R.id.ll_pictures_repair_submit);
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		deletePic();
	}
	public void showDialog() {
		LayoutInflater inflater = LayoutInflater.from(RepairActivity.this);
		View view = inflater.inflate(R.layout.dialog_upload_picture, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RepairActivity.this);
		relativeLayout1 = (RelativeLayout) view
				.findViewById(R.id.rl_shoot_upload_picture);
		relativeLayout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ִ������ǰ��Ӧ�����ж�SD���Ƿ����
				String SDState = Environment.getExternalStorageState();
				if (SDState.equals(Environment.MEDIA_MOUNTED)) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					picturePath = getTempPicPath();
					file = new File(picturePath);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, 1);
					dialog.cancel();
				} else {
					dialog.cancel();
					Toast.makeText(RepairActivity.this, "内存卡不存在",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		relativeLayout2 = (RelativeLayout) view
				.findViewById(R.id.ll_album_upload_picture);
		relativeLayout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				picturePath = getTempPicPath();
				file = new File(picturePath);
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(intent.CATEGORY_OPENABLE);
				intent.setType("image/*");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				startActivityForResult(Intent.createChooser(intent, "选择图片"), 2);
				dialog.cancel();
			}
		});

		builder.setView(view);
		dialog = builder.create();
		dialog.show();

	}

	private String getTempPicPath() {
		String picPath = System.currentTimeMillis() + ".jpg";
		String filepathString = Environment.getExternalStorageDirectory()
				.getPath() + File.separator + picPath;
		return filepathString;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				startPhotoZoom(Uri.fromFile(file));
			} else {
				Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",
						Toast.LENGTH_SHORT).show();
			}

		} else if (resultCode == RESULT_OK && requestCode == 2) {
			// ContentResolver cr = this.getContentResolver();
			// Uri uri = times.getData();
			// if (uri != null) {
			// try {
			// Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
			// bitmaps.add(n, bitmap);
			// iv.setImageBitmap(bitmaps.get(n));
			// n++;
			// // LinearLayout.LayoutParams params = new
			// // LinearLayout.LayoutParams(
			// // LayoutParams.MATCH_PARENT/3,
			// // LayoutParams.MATCH_PARENT);
			// pictureLayout.addView(iv,200,160);
			//
			// // pictureLayout.addView(iv, 100,
			// // LayoutParams.WRAP_CONTENT);
			//
			// } catch (FileNotFoundException e) {
			//
			// e.printStackTrace();
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			// }
			// }
			startPhotoZoom(data.getData());

		} else if (resultCode == RESULT_OK && requestCode == 3) {

			if (data != null) {
				getImageToView(data);
			} else {
				Toast.makeText(RepairActivity.this, "选择图片失败,请重新选择",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(RepairActivity.this, "选择图片失败,请重新选择",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-times", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, 3);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param data
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(this.getResources(), photo);
			// iv.setImageDrawable(drawable);
			// LayoutParams param = new LinearLayout.LayoutParams(
			// 500,
			// 500);
			View rootView = layoutInflater.inflate(R.layout.znt, null);
			Button imageView = (Button) rootView.findViewById(R.id.btn_uestc);
			imageView.setBackgroundDrawable(drawable);
			pictureLayout.addView(rootView);
			// pictureLayout.addView(iv,param);
			File mFile = new File(picturePath);
			files.add(mFile);
		}
	}
	
	/**
	 * ɾ��ͼƬ
	 */
	
	private void deletePic() {

		for (File f : files)
			f.delete();
	}

	private String RepairPost(MyComplain repair) {
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.addRepair);
		httpPost.setHeader("cookie", Session.getSeesion());

		try {

			MultipartEntity entity = new MultipartEntity();

			for (int i = 0; i < files.size(); i++) {
				FileBody fileBody2 = new FileBody(files.get(i));
				entity.addPart("File"+i, fileBody2);
			}

			StringBody times = new StringBody(repair.getTimes(),Charset.forName("UTF-8"));
			entity.addPart("times", times);
			StringBody what = new StringBody(repair.getTitle(),Charset.forName("UTF-8"));
			entity.addPart("title", what);
			StringBody content = new StringBody(repair.getContent(),Charset.forName("UTF-8"));
			entity.addPart("content", content);
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;

	}

	class RepairAsyncTask extends AsyncTask<MyComplain, Void, String> {

		@Override
		protected String doInBackground(MyComplain... arg0) {

			String result = RepairPost(arg0[0]);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!result.equals("")) {
				
				try {
					JSONObject object = new JSONObject(result);
					boolean status = object.getBoolean("status");
					if (status) {
						Toast.makeText(RepairActivity.this, "提交成功",
								Toast.LENGTH_SHORT).show();
						deletePic();
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(RepairActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}else {
				Toast.makeText(RepairActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
				
		}
	}

}
