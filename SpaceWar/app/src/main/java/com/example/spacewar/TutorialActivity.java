package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    ImageView m_Player;
    ImageView m_Enemy;
    ImageView m_Gift;
    ImageView m_Shots;
    Rect m_PlayerRect;
    Rect m_EnemyRect;
    Rect m_GiftRect;
    Rect m_ShotsRect;
    Button m_SkipBtn;
    TextView m_TutorialTv;
    ImageView m_BackgroundOne;
    ImageView m_BackgroundTwo;
    FrameLayout m_FrameLayout;

    private boolean m_IsKnowToMove;
    private boolean m_IsKnowToKill;
    private boolean m_IsKnowToTakeGift;
    private int m_GiftsCollectedCounter;
    private int m_EnemiesKilledCounter;
    private int m_PlayerMovedCounter;

    private int m_XDelta;
    private int m_YDelta;
    private int m_LastAction;
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;
    private Timer m_Timer = new Timer();
    private Handler m_Handler = new Handler();
    private int m_EnemyX;
    private int m_EnemyY;
    private  int m_GiftX;
    private  int m_GiftY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //Get screen size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        m_ScreenSizeX = size.x;
        m_ScreenSizeY = size.y;

        //Find view by id
        m_FrameLayout = findViewById(R.id.frame_layout);
        m_TutorialTv = findViewById(R.id.tutorial_tv);
        m_Enemy = findViewById(R.id.enemy_imageView);
        m_Player = findViewById(R.id.player_imageView);
        m_Shots = findViewById(R.id.shot_imageView);
        m_Gift = findViewById(R.id.gift_imageView);
        m_BackgroundOne = findViewById(R.id.background_one);
        m_BackgroundTwo = findViewById(R.id.background_two);
        m_SkipBtn = findViewById(R.id.skip_btn);

        m_TutorialTv.setText("Move the space ship");

        //Set listeners
        m_Player.setOnTouchListener(this);
        m_SkipBtn.setOnClickListener(this);
        m_FrameLayout.setOnTouchListener(this);

        //Set player info
        m_PlayerRect = new Rect(0,0,0,0);

        //Set enemy location
        m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
        m_EnemyY = - 200;
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);
        m_EnemyRect = new Rect(m_EnemyX,m_EnemyY,m_EnemyX+m_Enemy.getWidth(),m_EnemyY+m_Enemy.getHeight());

        //set gift location
        m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));
        m_GiftY = - 200;
        m_Gift.setX(m_GiftX);
        m_Gift.setY(m_GiftY);
        m_GiftRect = new Rect(m_GiftX,m_GiftY,m_GiftX+m_Gift.getWidth(),m_GiftY+m_Gift.getHeight());

        //set shot info
        m_ShotsRect = new Rect(m_EnemyX,m_EnemyY,m_EnemyX+m_Enemy.getWidth(),m_EnemyY+m_Enemy.getHeight());

        //Moving background animation
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float height = m_BackgroundOne.getHeight();
                final float translationY = height * progress;
                m_BackgroundOne.setTranslationY(translationY);
                m_BackgroundTwo.setTranslationY(translationY - height);
                m_BackgroundTwo.setTranslationY(translationY);
                m_BackgroundOne.setTranslationY(translationY - height);
            }
        });
        animator.start();

        //set boolean
        m_IsKnowToMove = false;
        m_IsKnowToKill = false;
        m_IsKnowToTakeGift = false;
        m_GiftsCollectedCounter = 0;
        m_EnemiesKilledCounter = 0;
        m_PlayerMovedCounter = 0;

        m_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        createShots();
                        if(m_IsKnowToMove)
                            moveEnemies();
                        if(m_IsKnowToKill)
                            sendGift();
                        if(m_IsKnowToTakeGift) {
                            m_Timer.cancel();
                            Intent intent = new Intent(TutorialActivity.this, GameActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }, 0, 20);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                m_XDelta = (int) (m_Player.getX() - event.getRawX());
                m_YDelta = (int) (m_Player.getY() - event.getRawY());
                m_LastAction = MotionEvent.ACTION_DOWN;

                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_Player.getWidth())
                    m_Player.setX(event.getRawX() + m_XDelta);
                if (event.getRawY() + m_YDelta > 0 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_Player.getHeight())
                    m_Player.setY(event.getRawY() + m_YDelta);

                m_LastAction = MotionEvent.ACTION_MOVE;

                m_PlayerRect.left = (int)m_Player.getX();
                m_PlayerRect.top = (int)m_Player.getY();
                m_PlayerRect.right = (int)m_Player.getX()+m_Player.getWidth();
                m_PlayerRect.bottom = (int)m_Player.getY()+m_Player.getHeight();

                break;

            case MotionEvent.ACTION_UP:
                if(!m_IsKnowToMove) {
                    m_PlayerMovedCounter++;
                    if(m_PlayerMovedCounter == 4)
                    {
                        m_IsKnowToMove = true;
                        m_TutorialTv.setText("Kill the Enemy");
                    }
                }
                break;

            default:
                return false;
        }
        return true;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.skip_btn:
                Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                startActivity(intent);
        }
    }

    private void createShots()
    {
        int ivX = (int)m_Shots.getX();
        int ivY = (int)m_Shots.getY();

        ivY -= m_ScreenSizeY / 20;
        if (ivY < 0 ) {
            ivY = (int)m_Player.getY() - m_Player.getHeight() + m_Shots.getHeight()/2;
            ivX = ((int)m_Player.getX() + m_Player.getWidth()/2) - m_Shots.getWidth()/2;
        }
        m_Shots.setX(ivX);
        m_Shots.setY(ivY);

        m_ShotsRect.left = ivX;
        m_ShotsRect.top = ivY;
        m_ShotsRect.right = ivX+m_Shots.getWidth();
        m_ShotsRect.bottom = ivY+m_Shots.getHeight();
    }


    private void sendGift() {

        checkIfTakeGifts();
        m_GiftY += m_ScreenSizeY/1000;
        if(m_GiftY > m_ScreenSizeY)
        {
            m_GiftY = -40;
            m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));
        }
        m_Gift.setX(m_GiftX);
        m_Gift.setY(m_GiftY);

        m_GiftRect.left = m_GiftX;
        m_GiftRect.top = m_GiftY;
        m_GiftRect.right = m_GiftX+m_Gift.getWidth();
        m_GiftRect.bottom = m_GiftY+m_Gift.getHeight();
    }

    private void moveEnemies()
    {
        checkIfHitEnemies();
        m_EnemyY += m_ScreenSizeY/500;
        if(m_EnemyY > m_ScreenSizeY)
        {
            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
        }
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);

        m_EnemyRect.left = m_EnemyX;
        m_EnemyRect.top = m_EnemyY;
        m_EnemyRect.right = m_EnemyX+m_Enemy.getWidth();
        m_EnemyRect.bottom = m_EnemyY+m_Enemy.getHeight();

    }

    private void checkIfHitEnemies() {
        if (Rect.intersects(m_ShotsRect, m_EnemyRect)) {
            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));

            m_Enemy.setX(m_EnemyX);
            m_Enemy.setY(m_EnemyY);

            m_Shots.setX((int) m_Player.getY() - m_Player.getHeight() + m_Shots.getHeight() / 2);
            m_Shots.setY(((int) m_Player.getX() + m_Player.getWidth() / 2) - m_Shots.getWidth() / 2);

            if (!m_IsKnowToKill) {
                m_EnemiesKilledCounter++;
                if (m_EnemiesKilledCounter == 3) {
                    m_IsKnowToKill = true;
                    m_TutorialTv.setText("Take the gifts");
                }
            }
        }
    }

    private void checkIfTakeGifts()
    {

        if(Rect.intersects(m_GiftRect,m_PlayerRect))
        {
            m_GiftY = ((int) Math.floor(Math.random() * (1000 - 100))) * -1;
            m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));

            m_Gift.setX(m_GiftX);
            m_Gift.setY(m_GiftY);

            if(!m_IsKnowToTakeGift) {
                m_GiftsCollectedCounter++;
                if(m_GiftsCollectedCounter == 3) {
                    m_IsKnowToTakeGift = true;
                    m_TutorialTv.setText("Finish!");
                }
            }
        }
    }
}