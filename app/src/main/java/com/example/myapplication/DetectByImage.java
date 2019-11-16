package com.example.myapplication;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ParseJson;

public class DetectByImage extends AppCompatActivity {

    Context context;
    private MyImageView photo;
    private String uploadFileName;
    private byte[] fileBuf;
    private TextView tv_dbi;
    private Handler handler;
    private LoginButton deteceSelect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectbyimage);
        context = this;
        photo = findViewById(R.id.Iv1);
        tv_dbi = findViewById(R.id.tv_dbi);
        deteceSelect = findViewById(R.id.detectSelect);
        deteceSelect.setBtnString("选择照片检测");
        deteceSelect.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                deteceSelect.start();
            }

            @Override
            public void onAnimationFinish() {
                select();
                deteceSelect.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString("detectRes");
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String err_msg = jsonObject.getString("error_msg");
                    if(err_msg.equals("SUCCESS")){
                        int[] info = ParseJson.parseLocationByJSObj4Detect(jsonObject);
                        photo.setBeginX(info[0]);
                        photo.setBeginY(info[1]);
                        photo.setEndX(info[0]+info[2]);
                        photo.setEndY(info[1]+info[3]);
                        double[] datas = ParseJson.parseAgeAndScoreByJSObj(jsonObject);
                        photo.setMsg("年龄："+datas[1]+"\r\n美丽值："+datas[0]);
                        photo.invalidate();
                        tv_dbi.setText("美丽评分："+datas[0]+"年龄："+datas[1]);
                    }else{
                        tv_dbi.setText(err_msg);
                    }
                } catch (JSONException e) {
                    Log.e("DetectJson","DetectJson解析错误");
                }
            }
        };
    }

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
                detect(bitmap,inputStream);
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
    //文件压缩的处理
    public void detect(final Bitmap bitmap, final InputStream is) {
        new Thread() {
            // 压缩
            String base64Str = "";
            @Override
            public void run() {
                try {
                    base64Str = FIleBase64Str.getBase64Str(bitmap,is);
                } catch (Exception e) {
                    Log.e("base64error","base64编码错误");
                }
                JSONObject res = FaceOpreationUtils.detect(base64Str);
                // 向handler发送返回对象
                Message message = handler.obtainMessage();
                Bundle bundle =new Bundle();
                bundle.putString("detectRes",res.toString());
                message.setData(bundle);
                handler.sendMessage(message);
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
