package com.example.spacewar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private SharedPreferences m_SharedPref;
    private SoundPlayer m_SoundPlayer;

    private Thread m_GameThread;
    private int m_ScreenSizeX, m_ScreenSizeY;

    private int m_Timer = 0;

    private boolean m_IsPlaying;
    private boolean m_IsAlive;
    private GamePlayer m_Player;
    private int m_XDelta;
    private int m_YDelta;

    private ArrayList<Shot> m_Shots;
    private ArrayList<Enemy> m_Enemies;
    public static int m_EnemyDestroyed = 0;
    public static int m_score = 0;

    private int m_LastAction;
    private Paint m_Paint;
    private Canvas m_Canvas;
    private SurfaceHolder m_SurfaceHolder;

    public GameView(Context context, int ScreenSizeX, int ScreenSizeY) {
        super(context);

        m_ScreenSizeX = ScreenSizeX;
        m_ScreenSizeY = ScreenSizeY;

        m_SoundPlayer = new SoundPlayer(context);
        m_Paint = new Paint();
        m_SurfaceHolder = getHolder();

        reset();
    }

    void reset() {
        m_score = 0;
        m_Player = new GamePlayer(getContext(), m_ScreenSizeX, m_ScreenSizeY, m_SoundPlayer);
        m_Shots = new ArrayList<>();
        m_Enemies = new ArrayList<>();

        m_IsAlive = true;
    }

    @Override
    public void run() {
        while (m_IsPlaying) {
            if (m_IsAlive) {
                update();
                draw();
                control();
            }
        }
    }

    private void draw() {
        if (m_SurfaceHolder.getSurface().isValid()) {
            m_Canvas = m_SurfaceHolder.lockCanvas();
            m_Canvas.drawColor(Color.BLACK);
            m_Canvas.drawBitmap(m_Player.get_Icon(), m_Player.get_X(), m_Player.get_Y(), m_Paint);
            for (Shot l : m_Player.get_Shots()) {
                m_Canvas.drawBitmap(l.get_Icon(), l.get_X(), l.get_Y(), m_Paint);
            }
            for (Enemy e : m_Enemies) {
                m_Canvas.drawBitmap(e.get_Icon(), e.get_X(), e.get_Y(), m_Paint);
            }
            drawScore();
            if (!m_IsAlive) {
                drawGameOver();
            }
            m_SurfaceHolder.unlockCanvasAndPost(m_Canvas);
        }
    }

    private void control() {
        try {
            if (m_Timer == 10000) {
                m_Timer = 0;
            }
            m_GameThread.sleep(20);
            m_Timer += 20;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        //m_Player.update();
        if (m_Timer % 200 == 0) {
            m_Player.fire();
        }

        for (Enemy m : m_Enemies) {
            m.update();

            if (Rect.intersects(m.get_Bounds(), m_Player.get_Bounds())) {
                m.destroy();
                m_IsAlive = false; //todo : add number of lives
                /*if (SCORE>mSP.getHighScore()){
                    mNewHighScore = true;
                    mSP.saveHighScore(SCORE, METEOR_DESTROYED, ENEMY_DESTROYED);
                }*/ // todo: add shared preference
            }

            for (Shot s : m_Player.get_Shots()) {
                if (Rect.intersects(s.get_Bounds(), m.get_Bounds())) {
                    m.hit();
                    s.hit();
                }
            }
        }

        boolean deleting = true;
        for (Enemy e : m_Enemies) {
            e.update();
            if (Rect.intersects(e.get_Bounds(), m_Player.get_Bounds())) {
                e.destroy();
                m_IsAlive = false;//todo:add lives
                /*if (m_score>=mSP.getHighScore()){
                    mSP.saveHighScore(SCORE, METEOR_DESTROYED, ENEMY_DESTROYED);
                }*/ //todo : add shared preference
            }

            for (Shot s : m_Player.get_Shots()) {
                if (Rect.intersects(e.get_Bounds(), s.get_Bounds())) {
                    e.hit();
                    s.hit();
                }
            }
        }

        while (deleting) {
            if (m_Enemies.size() != 0) {
                if (m_Enemies.get(0).get_Y() > m_ScreenSizeY) {
                    m_Enemies.remove(0);
                }
            }

            if (m_Enemies.size() == 0 || m_Enemies.get(0).get_Y() <= m_ScreenSizeY) {
                deleting = false;
            }
        }
        if (m_Timer % 2000 == 0) {
            m_Enemies.add(new Enemy(getContext(), m_ScreenSizeX, m_ScreenSizeY, m_SoundPlayer, 1, 1, 20)); //todo:add kill score
        }

    }

    void drawScore() {
        Paint score = new Paint();
        score.setTextSize(30);
        score.setColor(Color.WHITE);
        m_Canvas.drawText("Score : " + m_score, 100, 50, score);
    }

    void drawGameOver() {
        Paint gameOver = new Paint();
        gameOver.setTextSize(100);
        gameOver.setTextAlign(Paint.Align.CENTER);
        gameOver.setColor(Color.WHITE);
        m_Canvas.drawText("GAME OVER", m_ScreenSizeX / 2, m_ScreenSizeY / 2, gameOver);
        Paint highScore = new Paint();
        highScore.setTextSize(50);
        highScore.setTextAlign(Paint.Align.CENTER);
        highScore.setColor(Color.WHITE);
        /*if (mNewHighScore){
            mCanvas.drawText("New High Score : " + mSP.getHighScore(), mScreenSizeX / 2, (mScreenSizeY / 2) + 60, highScore);
            Paint enemyDestroyed = new Paint();
            enemyDestroyed.setTextSize(50);
            enemyDestroyed.setTextAlign(Paint.Align.CENTER);
            enemyDestroyed.setColor(Color.WHITE);
            mCanvas.drawText("Enemy Destroyed : " + mSP.getEnemyDestroyed(), mScreenSizeX / 2, (mScreenSizeY / 2) + 120, enemyDestroyed);
            Paint meteorDestroyed = new Paint();
            meteorDestroyed.setTextSize(50);
            meteorDestroyed.setTextAlign(Paint.Align.CENTER);
            meteorDestroyed.setColor(Color.WHITE);
            mCanvas.drawText("Meteor Destroyed : " + mSP.getMeteorDestroyed(), mScreenSizeX / 2, (mScreenSizeY / 2) + 180, meteorDestroyed);
        }*/

    }

    public void actionDown(int x,int y)
    {
        m_XDelta = (int) (m_Player.get_X() - x);
        m_YDelta = (int) (m_Player.get_Y() - y);
    }

    public void actionMove(int x,int y)
    {
        m_Player.update((int) x + m_XDelta, (int) y + m_YDelta);
    }

    public void pause() {
        Log.d("GameThread", "Main");
        m_IsPlaying = false;
        try {
            m_GameThread.join();
            m_SoundPlayer.pause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        m_IsPlaying = true;
        m_SoundPlayer.resume();
        m_GameThread = new Thread(this);
        m_GameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown((int)event.getRawX(),(int)event.getRawY());
                m_LastAction = MotionEvent.ACTION_DOWN;
                return true;

            case MotionEvent.ACTION_MOVE:
                actionMove((int)event.getRawX(),(int)event.getRawY());
                m_LastAction = MotionEvent.ACTION_MOVE;
                return true;
        }

        return super.onTouchEvent(event);
    }
}