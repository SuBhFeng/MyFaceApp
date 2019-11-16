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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import myapp.ui.LoginButton;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ParseJson;

public class CompareByImage extends AppCompatActivity {

    Context context;
    private TextView tv1;
    private ImageView iv1;
    private ImageView iv2;
    // 标识设置哪一个imageview
    private boolean flag;
    // 存储base64编码
    private String base64Str1;
    private String base64Str2;
    private String uploadFileName;
    private byte[] fileBuf;
    private Handler handler;
    private LoginButton compareBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        context = this;
        tv1 = findViewById(R.id.tv1);
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        flag = false;
        compareBtn = findViewById(R.id.compareBtn);
        compareBtn.setBtnString("开始对比");
        compareBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                compareBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                compare();
                compareBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        base64Str1 = "";
        base64Str2 = "";
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString("compareRes");
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String err_msg = jsonObject.getString("error_msg");
                    if(err_msg.equals("SUCCESS")){
                        JSONObject result = jsonObject.getJSONObject("result");
                        int score = result.getInt("score");
                        tv1.setText("相似度："+score);
                    }else{
                        tv1.setText(err_msg);
                    }
                } catch (JSONException e) {
                    Log.e("CompareJson","CompareJson解析错误");
                }
            }
        };
    }

    public void select1(View view){
        flag = true;
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

    public void select2(View view){
        flag = false;
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
    public void compare(){
        compare1();
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
                if(flag){
                    iv1.setImageBitmap(bitmap);
                }else{
                    iv2.setImageBitmap(bitmap);
                }
                // todo
                initBase64(bitmap,inputStream);
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
    // 初始化两个base64编码的图片
    private void initBase64(final Bitmap bitmap, final InputStream is){
        try {
            if(flag){
                base64Str1 = FIleBase64Str.getBase64Str(bitmap,is);
            }else{
                base64Str2 = FIleBase64Str.getBase64Str(bitmap,is);
            }
        } catch (Exception e) {
            Log.e("base64error","base64编码错误");
        }
    }
    //文件压缩的处理
    public void compare1() {
        new Thread() {
            @Override
            public void run() {
                if(!"".equals(base64Str1) && !"".equals(base64Str2)){
                    JSONObject res = FaceOpreationUtils.faceCompare(base64Str1,base64Str2);
                    // 向handler发送返回对象
                    Message message = handler.obtainMessage();
                    Bundle bundle =new Bundle();
                    bundle.putString("compareRes",res.toString());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
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


}
