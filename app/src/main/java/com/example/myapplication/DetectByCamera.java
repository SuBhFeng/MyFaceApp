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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ParseJson;

public class DetectByCamera extends AppCompatActivity {

    Context context;
    private Uri imgUri;
    private MyImageView iv_registerImage;
    private TextView tv_dbc;
    private Handler handler;
    private LoginButton detectByCameraBtn;
    private LoginButton detectByImageBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detectbycamera);
        context = this;
        iv_registerImage = findViewById(R.id.iv_registerImage);
        tv_dbc = findViewById(R.id.tv_dbc);
        detectByCameraBtn = findViewById(R.id.detectByCameraBtn);
        detectByCameraBtn.setBtnString("拍照检测");
        detectByImageBtn = findViewById(R.id.detectByImageBtn);
        detectByImageBtn.setBtnString("选择照片检测");

        detectByCameraBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                detectByCameraBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                detectByCamera();
                detectByCameraBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        detectByImageBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                detectByImageBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                selectImage();
                detectByImageBtn.reset();
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
                     /*
                        此处需判断是否检测成功
                     */
                     String err_msg = jsonObject.getString("error_msg");
                     if(err_msg.equals("SUCCESS")){
                         int[] info = ParseJson.parseLocationByJSObj4Detect(jsonObject);
                         iv_registerImage.setBeginX(info[0]);
                         iv_registerImage.setBeginY(info[1]);
                         iv_registerImage.setEndX(info[0]+info[2]);
                         iv_registerImage.setEndY(info[1]+info[3]);
                         double[] datas = ParseJson.parseAgeAndScoreByJSObj(jsonObject);
                         iv_registerImage.setMsg("年龄："+datas[1]+"\r\n美丽值："+datas[0]);
                         iv_registerImage.invalidate();
                         tv_dbc.setText("美丽评分："+datas[0]+"年龄："+datas[1]);
                     }else{
                         tv_dbc.setText(err_msg);
                     }

                } catch (JSONException e) {
                    Log.e("DetectJson","DetectJson解析错误");
                }
            }
        };
    }


    public void detectByCamera() {
        //删除并创建临时文件，用于保存拍照后的照片
        //android 6以后，写Sdcard是危险权限，需要运行时申请，但此处使用的是"关联目录"，无需！
        File outImg=new File(getExternalCacheDir(),"temp.jpg");
        if(outImg.exists()) outImg.delete();
        try {
            outImg.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                                JSONObject res = FaceOpreationUtils.detect(base64Str);
                                // 向handler发送返回对象
                                Message message = handler.obtainMessage();
                                Bundle bundle =new Bundle();
                                bundle.putString("detectRes",res.toString());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }


    public void selectImage() {
        Intent intent = new Intent(context, DetectByImage.class);
        context.startActivity(intent);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        detectByImageBtn.reset();
        detectByCameraBtn.reset();
    }
}

