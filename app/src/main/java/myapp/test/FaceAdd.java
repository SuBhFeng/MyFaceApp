package myapp.test;

import baidu.ai.utils.HttpUtil;
import baidu.ai.utils.GsonUtils;
import myapp.utils.FIleBase64Str;

import java.util.*;

/**
 * 人脸注册
 */
public class FaceAdd {

    public static String add() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        try {
            // 图片base64编码
            String base64Str = FIleBase64Str.getBase64Str("E:\\MicrosoftEdgeDownloads\\test2.jpg");
            Map<String, Object> map = new HashMap<>();
            // map.put("image", "上传图片的base64编码");
            map.put("image", base64Str);
            map.put("group_id", "class_9083");
            map.put("user_id", "user2");
            map.put("user_info", "abc");
            map.put("liveness_control", "NONE");
            map.put("image_type", "BASE64");
            map.put("quality_control", "LOW");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            // String accessToken = AuthService.getAuth();
            String accessToken = "24.8e6379507a7df5641270e9a81c4547ce.2592000.1575458859.282335-17689512";

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
