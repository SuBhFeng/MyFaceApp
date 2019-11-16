package myapp.utils;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseJson {
    // 获取人脸检测的年龄和美丽评分信息
    public static double[] parseAgeAndScoreByJSObj(JSONObject jsonObject){
        try {
            JSONObject rusultObj = jsonObject.getJSONObject("result");
            JSONArray face_list = rusultObj.getJSONArray("face_list");
            JSONObject face_list_0 = face_list.getJSONObject(0);
            double beauty = face_list_0.getDouble("beauty");
            int age = face_list_0.getInt("age");
            return new double[]{beauty,age};
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 用于解析人脸检测返回的信息
    public static int[] parseLocationByJSObj4Detect(JSONObject jsonObject){
        try {
            JSONObject rusultObj = jsonObject.getJSONObject("result");
            JSONArray face_list = rusultObj.getJSONArray("face_list");
            JSONObject face_list_0 = face_list.getJSONObject(0);
            JSONObject location = face_list_0.getJSONObject("location");
            double top = location.getDouble("top");
            double left = location.getDouble("left");
            int width = location.getInt("width");
            int height = location.getInt("height");
            int rotation = location.getInt("rotation");
            return new int[]{(int) left,(int) top,width,height,rotation};
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 用于解析上传人脸库返回的信息
    public static int[] parseLocationByJSObj4Upload(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONObject location = result.getJSONObject("location");
            double top = location.getDouble("top");
            double left = location.getDouble("left");
            int width = location.getInt("width");
            int height = location.getInt("height");
            int rotation = location.getInt("rotation");
            return new int[]{(int) left,(int) top,width,height,rotation};
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    // 解析识别人脸库中人脸的返回位置信息
    public static List<ImgLocation> parseLocationByJSObj4Select(JSONObject jsonObject){
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray faceList = result.getJSONArray("face_list");
            // 存储location
            List<ImgLocation> imgLocations = new ArrayList<ImgLocation>();
            for (int i = 0; i < faceList.length(); i++) {
                JSONObject location = faceList.getJSONObject(i).getJSONObject("location");
                double top = location.getDouble("top");
                double left = location.getDouble("left");
                int width = location.getInt("width");
                int height = location.getInt("height");
                JSONArray userList = faceList.getJSONObject(i).getJSONArray("user_list");
                String name = "";
                String id = "";
                String group = "";
                double score = 0;
                if(userList.length() > 0){
                    JSONObject user1 = userList.getJSONObject(0);
                    name = user1.getString("user_info");
                    id = user1.getString("user_id");
                    group = user1.getString("group_id");
                    score = user1.getDouble("score");
                }else{
                    name = "未找到该人脸";
                }
                ImgLocation imgLocation = new ImgLocation((int) left,(int) top,(int) (left+width),(int) (top+height),
                        "姓名："+name+"\r\nid："+id+"\r\ngroup："+group+"\r\n美丽值："+score);
                imgLocations.add(imgLocation);
            }
            return imgLocations;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
