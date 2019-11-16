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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.ui.MyImageView2;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ImgLocation;
import myapp.utils.ParseJson;

public class SearchByImage extends AppCompatActivity {
    Context context;
    private MyImageView2 photo;
    private String uploadFileName;
    private byte[] fileBuf;
    private TextView tv_searchInfo;
    private Handler handler;
    private LoginButton searchBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_image);
        context = this;
        photo = findViewById(R.id.miv1);
        tv_searchInfo = findViewById(R.id.tv_searchInfo2);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setBtnString("选择照片搜索");
        searchBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                searchBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                search();
                searchBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.e("search","handler");
                String res = msg.getData().getString("searchRes");
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String err_msg = jsonObject.getString("error_msg");
                    if(err_msg.equals("SUCCESS")){
                        Log.e("search","成功");
                        List<ImgLocation> locations = ParseJson.parseLocationByJSObj4Select(jsonObject);
                        photo.drawRect(locations);
                        photo.invalidate();
                        String str1 = "";
                        for (ImgLocation location : locations) {
                            str1 += location.getMsg();
                        }
                        tv_searchInfo.setText(str1);
                    }else{
                        Log.e("search","失败");
                        tv_searchInfo.setText(err_msg);
                    }
                } catch (JSONException e) {
                    Log.e("DetectJson","DetectJson解析错误");
                }
            }
        };
    }

    public void search() {
        String[] permissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
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
                JSONObject res = FaceOpreationUtils.serachMUltiFromGroupList(base64Str);
                // 向handler发送返回对象
                Message message = handler.obtainMessage();
                Bundle bundle =new Bundle();
                bundle.putString("searchRes",res.toString());
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
