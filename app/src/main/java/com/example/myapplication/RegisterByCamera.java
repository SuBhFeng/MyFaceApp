package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ParseJson;


public class RegisterByCamera extends AppCompatActivity {

    Handler handler ;
    Context context;

    private String tipInfo;
    private Uri imgUri;
    private MyImageView iv_registerImage;
    // 用户id输入框和内容
    private EditText et_userid;
    private String userid;
    // 用户名字输入框和内容
    private EditText et_userName;
    private String userName;
    // 自定义按钮
    private LoginButton registerByCameraBtn;
    private LoginButton selectImageBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerbycamera);
        context = this;
        iv_registerImage = findViewById(R.id.iv_registerImage);
        et_userid = findViewById(R.id.et_userid);
        et_userName = findViewById(R.id.et_userName);
        registerByCameraBtn = findViewById(R.id.registerByCameraBtn);
        registerByCameraBtn.setBtnString("拍照上传");
        selectImageBtn = findViewById(R.id.selectImageBtn);
        selectImageBtn.setBtnString("选择照片上传");
        registerByCameraBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                registerByCameraBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                userid = et_userid.getText().toString();
                userName = et_userName.getText().toString();
                if(userid.equals("") || userName.equals("")){
                    Toast.makeText(context,"请先填写学号和姓名",Toast.LENGTH_SHORT).show();
                    registerByCameraBtn.reset();
                    return;
                }
                try {
                    registerByCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        selectImageBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                selectImageBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                userid = et_userid.getText().toString();
                userName = et_userName.getText().toString();
                if(userid.equals("") || userName.equals("")){
                    Toast.makeText(context,"请先填写学号和姓名",Toast.LENGTH_SHORT).show();
                    selectImageBtn.reset();
                    return;
                }
                selectImage();
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
                        iv_registerImage.setBeginX(info[0]);
                        iv_registerImage.setBeginY(info[1]);
                        iv_registerImage.setEndX(info[0]+info[2]);
                        iv_registerImage.setEndY(info[1]+info[3]);
                        iv_registerImage.invalidate();
                    }else{

                    }

                } catch (JSONException e) {
                    Log.e("UploadJson","UploadJson解析错误");
                }
            }
        };
    }

    // 选择照片
    public void selectImage() {
        userid = et_userid.getText().toString();
        userName = et_userName.getText().toString();
        if(userid.equals("") || userName.equals("")){
            Toast.makeText(context,"请先填写学号和姓名",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, RegisterByImage.class);
        intent.putExtra("userInfo",userid+"==="+userName);
        context.startActivity(intent);
    }
    // 拍照上传
    public void registerByCamera() throws Exception{
        userid = et_userid.getText().toString();
        userName = et_userName.getText().toString();
        Log.e("user",userid+"=="+userName);
        if(userid.equals("") || userName.equals("")){
            Toast.makeText(context,"请先填写学号和姓名",Toast.LENGTH_SHORT).show();
            return;
        }
        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！
        File outImg=new File(getExternalCacheDir(),"temp.jpg");
        if(outImg.exists()) outImg.delete();
        outImg.createNewFile();

        //复杂的Uri创建方式
        if(Build.VERSION.SDK_INT>=24)
            //这是Android 7后，更加安全的获取文件uri的方式（需要配合Provider,在Manifest.xml中加以配置）
            imgUri= FileProvider.getUriForFile(this,"com.example.myapplication.fileprovider",outImg);
        else
            imgUri= Uri.fromFile(outImg);

        //利用actionName和Extra,启动《相机Activity》
        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        registerByCameraBtn.reset();
        selectImageBtn.reset();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 1:
                //此时，相机拍照完毕
                if (resultCode == RESULT_OK) {
                    try {
                        //利用ContentResolver,查询临时文件，并使用BitMapFactory,从输入流中创建BitMap
                        //同样需要配合Provider,在Manifest.xml中加以配置
                        final InputStream is = getContentResolver().openInputStream(imgUri);
                        // 填充屏幕显示上传图片
                        final Bitmap map = BitmapFactory.decodeStream(is);
                        iv_registerImage.setImageBitmap(map);
                         //上传图片
                        new Thread(){
                            String base64Str = "";
                            @Override
                            public void run() {
                                try {
                                    Log.e("eee","编码开始");
                                     base64Str = FIleBase64Str.getBase64Str(map,is);
                                    Log.e("eee","编码成功");
                                } catch (Exception e) {
                                    Log.e("base64error","base64编码错误");
                                }
                                JSONObject jsonObject = FaceOpreationUtils.register(userid,userName,"class_9083",base64Str);
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
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

}
