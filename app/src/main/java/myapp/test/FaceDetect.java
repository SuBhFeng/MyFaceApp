package myapp.test;

import android.util.Log;

import baidu.ai.utils.HttpUtil;
import baidu.ai.utils.GsonUtils;

import java.io.Console;
import java.io.FileOutputStream;
import java.util.*;

import baidu.ai.utils.GsonUtils;
import baidu.ai.utils.HttpUtil;
import myapp.utils.FIleBase64Str;

/**
 * 人脸检测与属性分析
 */
public class FaceDetect {

    public static String faceDetect(String imagePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        try {
            // 图片base64编码
            String base64Str = FIleBase64Str.getBase64Str(imagePath);
            Map<String, Object> map = new HashMap<>();
            map.put("image", base64Str);
            map.put("face_field", "faceshape,facetype");
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.8e6379507a7df5641270e9a81c4547ce.2592000.1575458859.282335-17689512";

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            // System.out.println(result);
            Log.e("msg",result);
            return result;
        } catch (Exception e) {
            Log.e("error",e.toString());
            for (int i=0;i<10;i++) {
                Log.e("msg","上传失败——————————————————————————————");
            }
        }
        return null;
    }
}
