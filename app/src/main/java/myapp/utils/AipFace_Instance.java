package myapp.utils;

import com.baidu.aip.face.AipFace;

public class AipFace_Instance {
    //设置APPID/AK/SK
    public static final String APP_ID = "17689512";
    public static final String API_KEY =  "UKh1KRcAYROu4GvLGF06he3V";
    public static final String SECRET_KEY = "yxv0jSmE6O7mLyrVi7sP6zrt0uqzOiGe";
    // 将构造函数私有化
    private AipFace_Instance(){}
    // 类初始化时，立即加载这个对象(没有延时加载的优势)  JVM加载类时  天然是线程安全的
    private static AipFace instance;
    // 多线程时方法同步
    public static synchronized AipFace getInstance(){
        if(instance == null){
            instance = new AipFace(APP_ID,API_KEY,SECRET_KEY);
            // 设置网络连接参数
//            instance.setConnectionTimeoutInMillis(20000);
//            instance.setSocketTimeoutInMillis(60000);
        }
        return instance;
    }
}
