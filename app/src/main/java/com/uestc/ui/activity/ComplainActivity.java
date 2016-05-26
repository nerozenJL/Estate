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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.domain.MyComplain;
import com.uestc.domain.Session;
import com.uestc.constant.Host;

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
 * 
 * 新增投诉的界面
 *
 */
public class ComplainActivity extends Activity {

	private ImageButton back, upload_picture,submit;
	private EditText theme, content;
	private static AlertDialog dialog;
	private RelativeLayout relativeLayout1, relativeLayout2;
	private LinearLayout pictureLayout;
	private MyComplain myComplain;
	private String picturePath;
	private File file;
	private List<File> files;
	private LayoutInflater layoutInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_complain);
		ActivityCollector.addActivity(this);
		files = new ArrayList<File>();
		layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	private void initData() {

		myComplain = new MyComplain();
	}

	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		deletePic();
	}

	private void initView() {

		back = (ImageButton) findViewById(R.id.im_back_complain);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletePic();
				finish();
			}
		});
		upload_picture = (ImageButton) findViewById(R.id.ib_upload_picture_complain);
		upload_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				showDialog();
			}
		});

		theme = (EditText) findViewById(R.id.et_theme_complain);
		content = (EditText) findViewById(R.id.et_elaborate_complain);
		submit = (ImageButton) findViewById(R.id.im_submit_complain);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if (theme.getText().toString().equals("")) {
					Toast.makeText(ComplainActivity.this, "请填写主题",
							Toast.LENGTH_SHORT).show();
				} else if (content.getText().toString().equals("")) {
					Toast.makeText(ComplainActivity.this, "请填写详细描述",
							Toast.LENGTH_SHORT).show();
				} else {
					myComplain.setTitle(theme.getText().toString());
					myComplain.setContent(content.getText().toString());
					new HanderAsyncTask().execute(myComplain);

				}
			}
		});

		pictureLayout = (LinearLayout) findViewById(R.id.ll_picture_complain);
	}

	public void showDialog() {
		LayoutInflater inflater = LayoutInflater.from(ComplainActivity.this);
		View view = inflater.inflate(R.layout.dialog_upload_picture, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ComplainActivity.this);
		relativeLayout1 = (RelativeLayout) view
				.findViewById(R.id.rl_shoot_upload_picture);
		relativeLayout1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//相机拍照
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
					Toast.makeText(ComplainActivity.this, "内存卡不存在",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		relativeLayout2 = (RelativeLayout) view
				.findViewById(R.id.ll_album_upload_picture);
		relativeLayout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//相册选择
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
			startPhotoZoom(data.getData());

		} else if (resultCode == RESULT_OK && requestCode == 3) {
			if (data != null) {
				getImageToView(data);
			} else {
				Toast.makeText(ComplainActivity.this, "选择图片失败,请重新选择",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(ComplainActivity.this, "选择图片失败,请重新选择",
					Toast.LENGTH_SHORT).show();
		}

	}

	

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		intent.putExtra("crop", "true");

		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, 3);
	}

	/**
	 *
	 *  保存图片
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

	private void deletePic() {

		for (File f : files)
			f.delete();
	}

	class HanderAsyncTask extends AsyncTask<MyComplain, Void, String> {

		@Override
		protected String doInBackground(MyComplain... myComplain) {

			String result;

			result = complainPost(myComplain[0]);
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
						Toast.makeText(ComplainActivity.this, "提交成功",
								Toast.LENGTH_SHORT).show();
						deletePic();
						finish();
					} else {
						JSONObject object2 = object.getJSONObject("errorMsg");
						String description = object2.getString("description");
						Toast.makeText(ComplainActivity.this, description,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			} else {
				Toast.makeText(ComplainActivity.this, "检查网络设置",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	private String complainPost(MyComplain complair) {
		String result = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Host.addComplain);
		httpPost.setHeader("cookie", Session.getSeesion());

		try {

			MultipartEntity entity = new MultipartEntity();
			for (int i = 0; i < files.size(); i++) {
				FileBody fileBody2 = new FileBody(files.get(i));
				entity.addPart("File" + i, fileBody2);
			}
			StringBody content = new StringBody(complair.getContent(),Charset.forName("UTF-8"));
			entity.addPart("content", content);

			StringBody title = new StringBody(complair.getTitle(),Charset.forName("UTF-8"));
			entity.addPart("title", title);
			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return result;
	}

}
