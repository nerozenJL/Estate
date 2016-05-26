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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uestc.base.ActivityCollector;
import com.uestc.constant.Host;
import com.uestc.domain.MyVerify;
import com.uestc.domain.Session;

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

public class VerificationActivity2 extends Activity {

    private EditText nameEdit,idEdit;
    private Button submitButton, takePhoto;
    private RelativeLayout shootLayout, albumLayout;
    private LinearLayout pictureLayout;
    private ImageButton backButton;
    private AlertDialog dialog;
    private String picturePath;
    private MyVerify myVerify;
    private File file;
    private List<File> files;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verification2);
        ActivityCollector.addActivity(this);
        files = new ArrayList<File>();
        layoutInflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        myVerify = new MyVerify();
        initView();
    }

    private void initView(){
        nameEdit = (EditText) findViewById(R.id.et_name_verification2);
        idEdit = (EditText) findViewById(R.id.et_id_verification2);
        takePhoto = (Button) findViewById(R.id.btn_take_photo_verification2);
        takePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDialog();
            }
        });
        backButton = (ImageButton) findViewById(R.id.im_back_verification2);
        initTopBar();
        pictureLayout = (LinearLayout) findViewById(R.id.ll_pictures_verification2_submit);
        submitButton = (Button) findViewById(R.id.btn_submit_verification2);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameEdit.getText().toString().equals("")){
                    Toast.makeText(VerificationActivity2.this, "请填写您的姓名",
                            Toast.LENGTH_SHORT).show();
                } else if (idEdit.getText().toString().equals("")){
                    Toast.makeText(VerificationActivity2.this, "请填写您的身份证号码",
                            Toast.LENGTH_SHORT).show();
                } else {
                    myVerify.setName(nameEdit.getText().toString());
                    myVerify.setId(idEdit.getText().toString());
                    new HandlerAsyncTask().execute(myVerify);
                }
            }
        });
    }

    private void initTopBar(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(VerificationActivity2.this);
        View view = inflater.inflate(R.layout.dialog_upload_picture, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(VerificationActivity2.this);

        shootLayout = (RelativeLayout) view.findViewById(R.id.rl_shoot_upload_picture);
        shootLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
                    Toast.makeText(VerificationActivity2.this, "内存卡不存在",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        albumLayout = (RelativeLayout) view.findViewById(R.id.ll_album_upload_picture);
        albumLayout.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(VerificationActivity2.this, "选择图片失败,请重新选择",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(VerificationActivity2.this, "选择图片失败,请重新选择",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, 3);
    }

    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(this.getResources(), photo);
//            View rootView = layoutInflater.inflate(R.layout.znt, null);
//            Button imageView = (Button) rootView.findViewById(R.id.btn_uestc);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            imageView.setImageDrawable(drawable);
            pictureLayout.addView(imageView);
            // pictureLayout.addView(iv,param);
            File mFile = new File(picturePath);
            files.add(mFile);
        }
    }

    private String getTempPicPath() {
        String picPath = System.currentTimeMillis() + ".jpg";
        String filepathString = Environment.getExternalStorageDirectory()
                .getPath() + File.separator + picPath;
        return filepathString;
    }

    class HandlerAsyncTask extends AsyncTask<MyVerify, Void, String> {

        @Override
        protected String doInBackground(MyVerify... myVerifies) {
            String result = "test";
            result = verifyPost(myVerifies[0]);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            Toast.makeText(VerificationActivity2.this, "请求已发送，请稍等",
                    Toast.LENGTH_SHORT).show();
            Log.d("VerificationResult",""+result);
            if (!result.equals("")) {
                try {
                    JSONObject object = new JSONObject(result);
                    boolean status = object.getBoolean("status");
                    if (status) {
                        Toast.makeText(VerificationActivity2.this, "提交成功",
                                Toast.LENGTH_SHORT).show();
                        deletePic();
                        finish();
                    } else {
                        JSONObject object2 = object.getJSONObject("errorMsg");
                        String description = object2.getString("description");
                        Toast.makeText(VerificationActivity2.this, description,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } else {
                Toast.makeText(VerificationActivity2.this, "检查网络设置",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deletePic() {

        for (File f : files)
            f.delete();
    }

    private String verifyPost(MyVerify verify) {
        String result = "";
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Host.identityVerify);
        httpPost.setHeader("cookie", Session.getSeesion());
        try {
            MultipartEntity entity = new MultipartEntity();
            for (int i = 0; i < files.size(); i++) {
                FileBody fileBody2 = new FileBody(files.get(i));
                entity.addPart("File" + i, fileBody2);
            }
            StringBody name = new StringBody(verify.getName(), Charset.forName("UTF-8"));
            entity.addPart("name", name);
            StringBody id = new StringBody(verify.getId(),Charset.forName("UTF-8"));
            entity.addPart("id", id);
            //  证件类型：
            //  0：身份证
            //  1：军人证
            //  2：护照
            StringBody cardType = new StringBody("0",Charset.forName("UTF-8"));
            entity.addPart("cardType", cardType);
            StringBody type = new StringBody("2",Charset.forName("UTF-8"));
            entity.addPart("type", type);
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
