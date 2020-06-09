package com.example.spacewar;

import android.content.SharedPreferences;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GameView extends Surface implements Runnable {

    private SharedPreferences m_SharedPref;

    private SurfaceHolder m_SurfaceHolder;
    private Thread m_GameThread;
    private int m_ScreenSizeX,m_ScreenSizeY;

    private int m_Timer = 0;

    private boolean m_IsPlaying;
    private boolean m_IsAlive;
    private PlayerCard m_Player;

    private ArrayList<Shot> m_Shots;
    private ArrayList<Enemy> m_Enemy;
    public static int m_EnemyDestroyed = 0;
    public static int m_score = 0;

    public GameView(@NonNull SurfaceControl from) {
        super(from);
    }

    @Override
    public void run() {

    }
}
