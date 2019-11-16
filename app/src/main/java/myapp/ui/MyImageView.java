package myapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;


public class MyImageView extends ImageView {

    private int beginX = 0;
    private int beginY = 0;
    private int endX = 0;
    private int endY = 0;
    private String msg = "";

    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }

    public void setBeginY(int beginY) {
        this.beginY = beginY;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStrokeWidth(12);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(beginX,beginY,endX,endY,paint);
        paint.setStyle(Paint.Style.FILL);
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(40);
        textPaint.setColor(Color.RED);
        textPaint.setAntiAlias(true);
        StaticLayout layoutopen = new StaticLayout(msg, textPaint, (int) 300 , Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        //这里的参数300，表示字符串的长度，当满300时，就会换行，也可以使用“\r\n”来实现换行
        canvas.save();
        canvas.translate((beginX+endX)/3, endY);
        layoutopen.draw(canvas);
        canvas.restore();
        // canvas.drawText(msg,endX,(beginY+endY)/3,textPaint);
    }
}
