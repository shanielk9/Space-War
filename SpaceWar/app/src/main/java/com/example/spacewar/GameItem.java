package com.example.spacewar;

import android.graphics.Rect;

public class GameItem {
    private int speed;
    private Rect rect;

    public GameItem() {
        rect = new Rect();
    }

    Rect getRect(){
        return rect;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setRect(int top, int left, int bottom, int right){
        rect.top = top;
        rect.left = left;
        rect.bottom=bottom;
        rect.right=right;
    }
}
