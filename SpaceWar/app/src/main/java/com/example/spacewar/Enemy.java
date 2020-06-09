package com.example.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaPlayer;

import java.util.Random;

import static com.example.spacewar.GameView.m_EnemyDestroyed;
import static com.example.spacewar.GameView.m_score;

public class Enemy {

    private Bitmap m_Icon;
    private int m_X, m_Y; //Enemy location
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;
    private Rect m_Bounds; //to ask Eran
    private int m_MaxX;
    private int m_MaxY;
    private int m_HP;
    private int m_Speed;
    private boolean m_IsTurnning;
    private int m_EnemyKillScore;

    public Enemy(Context context, int screenSizeX, int screenSizeY, Bitmap icon, int lives, int killScore) {
        m_ScreenSizeX = screenSizeX;
        m_ScreenSizeY = screenSizeY;

        m_HP = lives;
        m_EnemyKillScore = killScore;
        m_Icon = icon;

        Random random = new Random();
        m_Speed = random.nextInt(3) + 1;

        m_MaxX = screenSizeX - m_Icon.getWidth();
        m_MaxY = screenSizeY - m_Icon.getHeight();

        m_X = random.nextInt(m_MaxX);
        m_Y = 0 - m_Icon.getHeight();

        if(m_X < m_MaxX)
        {
            m_IsTurnning = true;
        }
        else {
            m_IsTurnning = false;
        }

        m_Bounds = new Rect(m_X, m_Y, m_X + m_Icon.getWidth(), m_Y + m_Icon.getHeight());
    }

    public void hit(){
        if (--m_HP == 0){
            m_score += m_EnemyKillScore;
            m_EnemyDestroyed++;
            destroy();
        }else{
            // TODO : find a way to put hit music - logic and UI do not know each other!
        }
    }

    public void destroy(){
        m_Y = m_ScreenSizeY + 1;
        m_Icon = null;
        // TODO : find a way to put destroy music - logic and UI do not know each other!
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

    public Rect get_Bounds() {
        return m_Bounds;
    }
}
