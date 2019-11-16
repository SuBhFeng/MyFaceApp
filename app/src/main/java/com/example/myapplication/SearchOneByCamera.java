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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import myapp.ui.LoginButton;
import myapp.ui.MyImageView;
import myapp.ui.MyImageView2;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import myapp.utils.ImgLocation;
import myapp.utils.ParseJson;

public class SearchOneByCamera extends AppCompatActivity {
    Handler handler;
    Context context;
    private MyImageView2 miv;
    private Uri imgUri;
    private TextView tv_searchInfo;
    private LoginButton searchByCameraBtn;
    private LoginButton searchByImageBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchonebycamera);
        context = this;
        miv = findViewById(R.id.miv);
        tv_searchInfo = findViewById(R.id.tv_searchInfo);
        searchByCameraBtn = findViewById(R.id.searchByCameraBtn);
        searchByCameraBtn.setBtnString("拍照搜索");
        searchByImageBtn = findViewById(R.id.searchByImageBtn);
        searchByImageBtn.setBtnString("选择照片搜索");
        searchByCameraBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                searchByCameraBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                searchByCamera();
                searchByCameraBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });
        searchByImageBtn.setAnimationButtonListener(new LoginButton.OnAnimationButtonClickListener() {
            @Override
            public void onAnimationStart() {
                searchByImageBtn.start();
            }

            @Override
            public void onAnimationFinish() {
                selectImage();
                searchByImageBtn.reset();
            }

            @Override
            public void onAnimationCancel() {

            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString("searchRes");
                try {

                    JSONObject jsonObject = new JSONObject(res);
                     /*
                        此处需判断是否检测成功
                     */
                    String err_msg = jsonObject.getString("error_msg");
                    if(err_msg.equals("SUCCESS")){
                        List<ImgLocation> locations = ParseJson.parseLocationByJSObj4Select(jsonObject);
                        miv.drawRect(locations);
                        miv.invalidate();
                        String str1 = "";
                        for (ImgLocation location : locations) {
                            str1 += location.getMsg();
                        }
                        tv_searchInfo.setText(str1);
                    }else{
                       tv_searchInfo.setText(err_msg);
                    }

                } catch (JSONException e) {
                    Log.e("SearchJson","SearchJson解析错误");
                }
            }
        };

    }


    public void selectImage() {
        Intent intent = new Intent(context, SearchByImage.class);
        context.startActivity(intent);
    }

    public void searchByCamera() {
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
                        miv.setImageBitmap(map);
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
                                JSONObject res = FaceOpreationUtils.serachMUltiFromGroupList(base64Str);
                                // 向handler发送返回对象
                                Message message = handler.obtainMessage();
                                Bundle bundle =new Bundle();
                                bundle.putString("searchRes",res.toString());
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


}
