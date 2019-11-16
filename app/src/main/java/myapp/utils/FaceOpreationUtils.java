package myapp.utils;

import android.util.Base64;
import android.util.Log;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FaceOpreationUtils {
    // 获取Aipface的单例
    private static AipFace client = AipFace_Instance.getInstance();
    // 检索人脸
    public static JSONObject detect(String base64Str){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age,beauty");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        String imageType = "BASE64";

        // 人脸检测
        JSONObject res = client.detect(base64Str, imageType, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    // 向人脸库中注册图片
//    public static void register(String userId,String groupId,String imagePath){
//        // 传入可选参数调用接口
//        HashMap<String, String> options = new HashMap<String, String>();
//        options.put("user_info", "user's info");
//        options.put("quality_control", "NORMAL");
//        options.put("liveness_control", "LOW");
//        options.put("action_type", "REPLACE");
//        // 转base64编码码
//        String imageType = "BASE64";
//        String image = null;
//        try {
//            image = FIleBase64Str.getBase64Str(imagePath);
//        } catch (Exception e) {
//            Log.e("Base64error","注册人脸base64编码错误");
//        }
//        // 人脸注册
//        JSONObject res = client.addUser(image, imageType, groupId, userId, options);
//        try {
//            System.out.println(res.toString(2));
//        } catch (JSONException e) {
//            Log.e("faceRegisterError","人脸注册异常");
//        }
//    }
    // 向人脸库中注册图片
    public static JSONObject register(String userId,String name,String groupId,String base64Str){
        // AipFace client = AipFace_Instance.getInstance();
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("user_info", name);
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NONE");
        options.put("action_type", "APPEND");
        // 转base64编码码
        String imageType = "BASE64";
        // 人脸注册
        JSONObject res = client.addUser(base64Str, imageType, groupId, userId, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            Log.e("faceRegisterError","人脸注册异常");
        }
        return res;
    }
    // 1：n搜索
    public static JSONObject serachOneFromGroupList(String base64){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("match_threshold", "70");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NONE");
        // options.put("user_id", "233451");

        String image = base64;
        String imageType = "BASE64";
        String groupIdList = "class_9083";

        // 人脸搜索
        JSONObject res = client.search(image, imageType, groupIdList, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    // M:N
    public static JSONObject serachMUltiFromGroupList(String base64){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "10");
        options.put("match_threshold", "70");
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "10");

        String imageType = "BASE64";
        String groupIdList = "class_9083";

        // 人脸搜索 M:N 识别
        JSONObject res = client.multiSearch(base64, imageType, groupIdList, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    // 删除人脸库中的图片
    public static JSONObject delete(String faceToken)  {
        // AipFace client = AipFace_Instance.getInstance();
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();

        String userId = "user2";
        String groupId = "class_9083";
        // String faceToken = "cf8730a4e2b11de2aed087e4285bf1d1";

        // 人脸删除
        JSONObject res = client.faceDelete(userId, groupId, faceToken, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            Log.e("faceDeleteError","人脸删除异常");
        }
        return res;
    }
    // 获取用户组信息
    public static JSONObject getGroupUsers(String groupId){
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("start", "0");
        options.put("length", "50");

        // 获取用户列表
        JSONObject res = client.getGroupUsers(groupId, options);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
    // 人脸对比
    public static JSONObject faceCompare(String base64Str1,String base64Str2){
        // image1/image2也可以为url或facetoken, 相应的imageType参数需要与之对应。
        MatchRequest req1 = new MatchRequest(base64Str1, "BASE64");
        MatchRequest req2 = new MatchRequest(base64Str2, "BASE64");
        ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
        requests.add(req1);
        requests.add(req2);

        JSONObject res = client.match(requests);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}
