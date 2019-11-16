package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ParseJson;

public class RegisterByImage extends AppCompatActivity {

    Handler handler;
    Context context;
    private MyImageView photo;
    private String uploadFileName;
    private byte[] fileBuf;
    // 上传结果提示信息
    private String tipInfo;
    // 用户注册所需信息
    private String userid;
    private String userName;
    private LoginButton select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerbyimage);
        photo = findViewById(R.id.Iv1);
        String info = getIntent().getStringExtra("userInfo");
        String[] split = info.split("==");
        userid = split[0];
        userName = split[1];
        select = findViewById(R.id.select);
        select.setBtnString("选择照片上传");
        select.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                select.start();
            }

            @Override
            public void onAnimationFinish() {
                select();
                select.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString("upLoadRes");
                try {

                    JSONObject jsonObject = new JSONObject(res);
                     /*
                        此处需判断是否检测成功
                     */
                    String err_msg = jsonObject.getString("error_msg");
                    if(err_msg.equals("SUCCESS")){
                        int[] info = ParseJson.parseLocationByJSObj4Upload(jsonObject);
                        photo.setBeginX(info[0]);
                        photo.setBeginY(info[1]);
                        photo.setEndX(info[0]+info[2]);
                        photo.setEndY(info[1]+info[3]);
                        photo.invalidate();
                    }else{

                    }

                } catch (JSONException e) {
                    Log.e("UploadJson","UploadJson解析错误");
                }
            }
        };
    }
    // 按钮点击
    public void select() {
        String[] permissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
        else{
            openGallery(); //打开相册，进行选择
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
                else{
                    Toast.makeText(this,"读相册的操作被拒绝",Toast.LENGTH_LONG).show();
                }
        }
    }
    //打开相册,进行照片的选择
    private void openGallery(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                handleSelect(data);
        }
    }
    //选择后照片的读取工作
    private void handleSelect(Intent intent){
        Cursor cursor=null;
        Uri uri=intent.getData();
        //如果直接是从"相册"中选择，则Uri的形式是"content://xxxx"的形式
        if("content".equalsIgnoreCase(uri.getScheme())){
            cursor= getContentResolver().query(uri,null,null,null,null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                uploadFileName = cursor.getString(columnIndex);
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                fileBuf=convertToBytes(inputStream);
                Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
                photo.setImageBitmap(bitmap);
                upload(bitmap,inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        else{
            Log.i("other","其它数据类型.....");
        }
        cursor.close();
    }
    //文件上传的处理
    public void upload(final Bitmap bitmap, final InputStream is) {
        new Thread() {
            @Override
            public void run() {
                // 压缩
                String baseStr = "";
                try {
                    baseStr = FIleBase64Str.getBase64Str(bitmap,is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject =  FaceOpreationUtils.register(userid,userName,"class_9083",baseStr);
                // 向handler发送返回对象
                Message message = handler.obtainMessage();
                Bundle bundle =new Bundle();
                bundle.putString("upLoadRes",jsonObject.toString());
                message.setData(bundle);
                handler.sendMessage(message);
                try {
                    tipInfo = jsonObject.getString("error_msg");
                } catch (JSONException e) {
                    tipInfo = "过程出现异常";
                    e.printStackTrace();
                }
                showToast();
            }
        }.start();
    }
    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }
    // 在别的线程中调用此方法可以使用Toast
    public void showToast() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), tipInfo,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        select.reset();
    }
}
