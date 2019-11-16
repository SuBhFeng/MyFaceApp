package myapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapCutUtils {
   public static Bitmap[] cutBitMap(String path,int number){
        Bitmap[] bitmaps = new Bitmap[number];
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.e("bitmap","bitmap------>"+bitmap.getByteCount());
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        int width = srcWidth / number;
        for(int i=0;i<number-1;i++){
            bitmaps[i] = Bitmap.createBitmap(bitmap,i*width,0,width,srcHeight);
            Log.e("bitmap","bitmaps[i]------>"+bitmaps[i].getByteCount());
        }
        // 最后一份剩下的全部
        bitmaps[number-1] = Bitmap.createBitmap(bitmap,(number-1)*width,0,width,srcHeight);
        Log.e("bitmap","bitmaps[number-1]------>" + bitmaps[number - 1].getByteCount());
        return bitmaps;
   }
    public  Bitmap[] cutBitMap2(String path,int number,int v){
        int index = -1;
        Bitmap[] bitmaps = new Bitmap[number*v];
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.e("bitmap","bitmap------>"+bitmap.getByteCount());
        int srcHeight = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getHeight() : bitmap.getWidth();
        int srcWidth = bitmap.getWidth() > srcHeight ? srcHeight : bitmap.getWidth();
        int width = srcWidth / number;
        int height = srcHeight / v;
        for(int i=0;i<number;i++){
            for (int j = 0; j < v-1; j++) {
                bitmaps[++index] = Bitmap.createBitmap(bitmap,i*width,j*height,width,height);
                Log.e("bitmap","bitmaps"+index+"------>"+bitmaps[index].getByteCount());
            }
        }
        for (int i = 0; i < number-1; i++) {
            bitmaps[++index] = Bitmap.createBitmap(bitmap,i*width,(v-1)*height,width,srcHeight-(v-1)*height);
        }
        for (int j = 0; j < v-1; j++) {
            bitmaps[++index] = Bitmap.createBitmap(bitmap,(number-1)*width,j*height,srcWidth-(number-1)*width,height);
        }
        bitmaps[++index] = Bitmap.createBitmap(bitmap,(number-1)*width,(v-1)*height,srcWidth-(number-1)*width,srcHeight-(v-1)*height);
        return bitmaps;
    }

}
