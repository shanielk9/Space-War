package com.example.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Shot {

    private Bitmap m_Icon;
    private int m_X, m_Y; //shot location
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;
    private Rect m_Bounds;

    public Shot(Context context, int screenSizeX, int screenSizeY, int spaceShipX, int spaceShipY, int SpaceShipLenght, int SpaceShipHight) {
        m_ScreenSizeX = screenSizeX;
        m_ScreenSizeY = screenSizeY;

        m_X = spaceShipX + SpaceShipLenght / 2;
        m_Y = spaceShipY + SpaceShipHight / 2;

        m_Icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.laser_blue);
        m_Icon = Bitmap.createScaledBitmap(m_Icon, 50, 50, false);

        m_Bounds = new Rect(m_X, m_Y, m_X + m_Icon.getWidth(), m_Y + m_Icon.getHeight());
    }

    public void updateShotLocation() {
        m_Y -= m_Icon.getHeight() - 10; // goes up and stay at the same X line
        m_Bounds.left = m_X;
        m_Bounds.top = m_Y;
        m_Bounds.right = m_X + m_Icon.getWidth();
        m_Bounds.bottom = m_Y + m_Icon.getHeight();
    }

    public void hit() {
        m_Y = 0 - m_Icon.getHeight();
    }

    public Bitmap get_Icon() {
        return m_Icon;
    }

    public int get_X() {
        return m_X;
    }

    public int get_Y() {
        return m_Y;
    }

    public int get_ScreenSizeX() {
        return m_ScreenSizeX;
    }

    public int get_ScreenSizeY() {
        return m_ScreenSizeY;
    }

    public Rect get_Bounds() {
        return m_Bounds;
    }
}