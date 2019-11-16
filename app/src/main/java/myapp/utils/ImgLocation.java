package myapp.utils;

public class ImgLocation {
    private int beginX = 0;
    private int beginY = 0;
    private int endX = 0;
    private int endY = 0;
    private String msg = "";

    public ImgLocation(int beginX, int beginY, int endX, int endY, String msg) {
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
        this.msg = msg;
    }

    public int getBeginX() {
        return beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public String getMsg() {
        return msg;
    }

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
}
