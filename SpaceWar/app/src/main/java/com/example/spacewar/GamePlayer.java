package com.example.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;

public class GamePlayer {

    private Bitmap m_Icon;
    private int m_X;
    private int m_Y;
    private ArrayList<Shot> m_Shots;
    private Context m_Context;
    private Rect m_Bounds;
    private int m_MarginFromSides;
    private int m_ScreenSizeX;

    public Bitmap getM_Icon() {
        return m_Icon;
    }

    public int getM_X() {
        return m_X;
    }

    public int getM_Y() {
        return m_Y;
    }

    public ArrayList<Shot> getM_Shots() {
        return m_Shots;
    }

    public Rect getM_Bounds() {
        return m_Bounds;
    }

    private int m_ScreenSizeY;

    public GamePlayer(Context context, int screenSizeX, int screenSizeY, Bitmap icon) {
        m_ScreenSizeX = screenSizeX;
        m_ScreenSizeY = screenSizeY;
        m_Context = context;

        m_MarginFromSides = 16;
        m_Icon = icon;

        m_X = screenSizeX/2 - m_Icon.getWidth()/2;
        m_Y = screenSizeY - m_Icon.getHeight() - m_MarginFromSides;

        m_Shots = new ArrayList<>();

        m_Bounds = new Rect(m_X, m_Y, m_X + m_Icon.getWidth(), m_Y + m_Icon.getHeight());
    }

    public void fire(){
        m_Shots.add(new Shot(m_Context, m_ScreenSizeX, m_ScreenSizeY, m_X, m_Y, m_Icon.getWidth(),m_Icon.getHeight()));
    }

    public void update(int x, int y) // while user change place of the battle ship it send the new to here
    {
        x = m_X;
        y=m_Y;
        m_Bounds.left = x;
        m_Bounds.top = y;
        m_Bounds.right = x + m_Icon.getWidth();
        m_Bounds.bottom = y + m_Icon.getHeight();

        for (Shot s : m_Shots) {
            s.updateShotLocation();
        }

        boolean isInsideTheScreen = true;
        while (isInsideTheScreen) {
            if (m_Shots.size() != 0) {
                if (m_Shots.get(0).get_Y() < 0) {
                    m_Shots.remove(0);
                }
            }
            if (m_Shots.size() == 0 || m_Shots.get(0).get_Y() >= 0) {
                isInsideTheScreen = false;
            }
        }
    }

}
