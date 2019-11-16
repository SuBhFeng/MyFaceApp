package myapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import baidu.ai.utils.Base64Util;

public class FIleBase64Str {
    // 测试所用的将本地文件转化为base64编码
    public static String getBase64Str(String imagePath) throws Exception{
        FileInputStream fis = new FileInputStream(imagePath);
        byte[] buf = new byte[fis.available()];
        fis.read(buf);
        String base64Str = Base64Util.encode(buf);
        return base64Str;
    }
    //  压缩后上传
    public static String getBase64Str(Bitmap map,InputStream is) throws Exception{
        Log.e("count","开始："+map.getByteCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.JPEG, 5, baos);
        Log.e("count","结束："+baos.toByteArray().length);
        String base64Str = Base64Util.encode(baos.toByteArray());
        return base64Str;
    }
    // 不压缩上传
    public static String getBase64Str(InputStream is) throws Exception{
        int len = -1;
        byte[] buf = new byte[10*1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((len = is.read(buf)) != -1){
            baos.write(buf,0,len);
        }
        String base64Str = Base64Util.encode(baos.toByteArray());
        return base64Str;
    }
}


