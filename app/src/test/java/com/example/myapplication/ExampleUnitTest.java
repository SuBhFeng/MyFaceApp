package com.example.myapplication;

import android.app.backup.FileBackupHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;

import myapp.test.AuthService;
import myapp.test.FaceAdd;
import myapp.test.FaceDetect;
import myapp.utils.BitmapCutUtils;
import myapp.utils.FIleBase64Str;
import myapp.utils.FaceOpreationUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        System.out.println("aaaa");
    }
    @Test
    public void test01(){
        try {
            Socket socket = new Socket("www.baidu.com",80);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            os.write("GET / HTTP/1.1\n".getBytes());
            os.write("Host: www.baidu.com\n\n".getBytes());
            byte[] buff = new byte[1024];
            is.read(buff);
            System.out.print(new String(buff));
            os.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void base64Test01() throws Exception{
        InputStream inputStream=new FileInputStream("E:\\MicrosoftEdgeDownloads\\timg.jpg");
        OutputStream outputStream=new FileOutputStream("E:\\MicrosoftEdgeDownloads\\a.jpg");

        //文件读入缓存并编码
        byte[] buf=new byte[inputStream.available()];
        inputStream.read(buf);
        //编码
        String s=new String(Base64.getEncoder().encode(buf));
        System.out.println(s);
        //解码，并写入文件
        byte[] buf1= Base64.getDecoder().decode(s);
        outputStream.write(buf1);

        outputStream.close();
        inputStream.close();
    }
    @Test
    public void test03() throws Exception{
        String path = "E:\\MicrosoftEdgeDownloads\\timg.jpg";
        OkHttpClient client = new OkHttpClient();
        // 上传文件域的请求体部分
        RequestBody fileBody = RequestBody.create(new File(path), MediaType.parse("image/jpeg"));
        // 整个上传额请求体部分(普通表单+文件上传域)
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title","kk kk")
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("avatar","timg.jpg",fileBody)
                .build();
        // 构造网页请求
        Request request = new Request.Builder()
                .url("http://localhost:8000/upload")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    // 获取Access_Token
    @Test
    public void getAccess_Tocken(){
        AuthService.getAuth();
        // FaceDetect.faceDetect("E:\\MicrosoftEdgeDownloads\\test.jpg");
    }
    // 上传图片至人脸库
    @Test
    public void uploadImage(){
        // FaceAdd.add();

//        String s = "";
//        try {
//             s = FIleBase64Str.getBase64Str("E:\\MicrosoftEdgeDownloads\\test.jpg");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        FaceOpreationUtils.detect(s);
        // 获取组里的所有用户
        FaceOpreationUtils.getGroupUsers("class_9083");
    }
    // 删除人脸库中的指定图片
    @Test
    public void deleteImage(){
        FaceOpreationUtils.delete("cf8730a4e2b11de2aed087e4285bf1d1");
    }
    @Test
    public void base64Test() throws Exception {
        try {
            InputStream is = new FileInputStream("E:\\MicrosoftEdgeDownloads\\test.jpg");
            System.out.println(FIleBase64Str.getBase64Str(is));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void parseJson(){
        String jsonStr = "{\n" +
                "  \"result\": {\n" +
                "    \"face_num\": 1,\n" +
                "    \"face_list\": [\n" +
                "\t{\n" +
                "      \"beauty\": 55.61,\n" +
                "      \"liveness\": {\"livemapscore\": 0.33},\n" +
                "      \"angle\": {\n" +
                "        \"roll\": 32.28,\n" +
                "        \"pitch\": 11.45,\n" +
                "        \"yaw\": -52.64\n" +
                "      },\n" +
                "      \"face_token\": \"cdb14ebb8928e9d8bb5aa85e2e8d3251\",\n" +
                "      \"location\": {\n" +
                "        \"top\": -5.35,\n" +
                "        \"left\": 342.29,\n" +
                "        \"rotation\": 28,\n" +
                "        \"width\": 329,\n" +
                "        \"height\": 315\n" +
                "      },\n" +
                "      \"face_probability\": 1,\n" +
                "      \"age\": 23\n" +
                "    }]\n" +
                "  },\n" +
                "  \"log_id\": 8445056575996,\n" +
                "  \"error_msg\": \"SUCCESS\",\n" +
                "  \"cached\": 0,\n" +
                "  \"error_code\": 0,\n" +
                "  \"timestamp\": 1572956790\n" +
                "}";
        try {
            JSONObject object = new JSONObject(jsonStr);
            JSONObject rusultObj = object.getJSONObject("result");
            JSONArray face_list = rusultObj.getJSONArray("face_list");
            JSONObject face_list_0 = face_list.getJSONObject(0);
            double beauty = face_list_0.getDouble("beauty");
            String face_token = face_list_0.getString("face_token");
            int age = face_list_0.getInt("age");
            System.out.println(beauty+"=="+face_token+"=="+age);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void search() throws Exception {
        String base64Str = FIleBase64Str.getBase64Str("E:\\MicrosoftEdgeDownloads\\test1.jpg");
        FaceOpreationUtils.serachMUltiFromGroupList(base64Str);
    }
    @Test
    public void compare(){
        try {
            InputStream is = new FileInputStream("E:\\MicrosoftEdgeDownloads\\test1.jpg");
            String str1 = FIleBase64Str.getBase64Str(is);
            is = new FileInputStream("E:\\MicrosoftEdgeDownloads\\test1.jpg");
            String str2 = FIleBase64Str.getBase64Str(is);
            FaceOpreationUtils.faceCompare(str1,str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}