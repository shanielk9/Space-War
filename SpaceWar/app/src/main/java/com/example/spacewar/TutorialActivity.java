package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

public class TutorialActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    ImageView m_Player;
    ImageView m_Enemy;
    Button m_SkipBtn;
    ViewGroup m_RelativeLayout;
    RelativeLayout.LayoutParams parms;
    private int xDelta;
    private int yDelta;
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        m_ScreenSizeX = Resources.getSystem().getDisplayMetrics().widthPixels;
        m_ScreenSizeY = Resources.getSystem().getDisplayMetrics().heightPixels;

        m_RelativeLayout = (RelativeLayout) findViewById((R.id.relative_layout));
        m_RelativeLayout.setOnTouchListener(this);

        m_Enemy = findViewById(R.id.enemy_imageView);

        m_Player = findViewById(R.id.player_imageView);
        m_Player.setOnTouchListener(this);

        m_SkipBtn = findViewById(R.id.skip_btn);
        m_SkipBtn.setOnClickListener(this);

        dropEnemy();
    }

    private void dropEnemy() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                Random r = new Random();
                int x;
                Animation enemyAnim;
                try {
                    while(true) {
                        x = r.nextInt(m_ScreenSizeX)-m_Enemy.getWidth();
                        m_Enemy.setX(x);
                        m_Enemy.setY(0);
                        sleep(850);
                        enemyAnim = AnimationUtils.loadAnimation(TutorialActivity.this,R.anim.up_to_down);
                        m_Enemy.setAnimation(enemyAnim);

                        while (m_Enemy.getY() < m_ScreenSizeY-m_Enemy.getHeight()/2) {
                            sleep(5);
                            m_Enemy.setY(m_Enemy.getY()+1);}
                        }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                parms = (RelativeLayout.LayoutParams) m_Player.getLayoutParams();

                xDelta = x - parms.leftMargin;
                yDelta = y - parms.topMargin;
                break;

            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) m_Player.getLayoutParams();
                if(x-xDelta > 0 && x-xDelta < m_ScreenSizeX-m_Player.getWidth())
                {params1.leftMargin = x - xDelta;}
                if( y - yDelta > 0 &&  y - yDelta < m_ScreenSizeY-m_Player.getHeight())
                {params1.topMargin = y - yDelta;}
                m_Player.setLayoutParams(params1);
                break;
        }
        m_RelativeLayout.invalidate();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.skip_btn:
                Intent intent =  new Intent(TutorialActivity.this, GameActivity.class);
                startActivity(intent);
        }
    }
}