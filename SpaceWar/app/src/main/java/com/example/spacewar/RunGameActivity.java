package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class RunGameActivity extends AppCompatActivity implements View.OnTouchListener {

    ImageView m_BackgroundOne;
    ImageView m_BackgroundTwo;
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;

    ImageView m_PlayerIv;
    ImageView m_EnemyIv;
    ImageView m_GiftIv;
    ImageView m_ShotsIv;
    Button m_HomeButton;
    Button m_PauseButton;
    ImageView m_HeartOneIv;
    ImageView m_HeartTwoIv;
    ImageView m_HeartThreeIv;
    TextView m_ScoreTv;

    Player m_Player;
    GameItem m_Enemy;
    GameItem m_Gift;
    GameItem m_Shot;


    //members for onTouch listener
    FrameLayout m_FrameLayout;
    private int m_XDelta;
    private int m_YDelta;
    private int m_LastAction;

    private boolean isStart = false;
    private boolean isPause = false;

    //Rect m_PlayerRect;
    //Rect m_EnemyRect;
    //Rect m_GiftRect;
    //Rect m_ShotsRect;
    //private int m_EnemyX;
    //private int m_EnemyY;
    //private  int m_GiftX;
    //private  int m_GiftY;

    private Timer m_Timer = new Timer();
    private Handler m_Handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_game);

        //find views by id
        m_FrameLayout = findViewById(R.id.frame_layout);
        m_PlayerIv = findViewById(R.id.player_imageView);
        m_EnemyIv = findViewById(R.id.enemy_one_imageView);
        m_GiftIv = findViewById(R.id.gift_imageView);
        m_ShotsIv = findViewById(R.id.shot_imageView);
        m_HomeButton = findViewById(R.id.home_button);
        m_PauseButton = findViewById(R.id.pause_btn);
        m_HeartOneIv = findViewById(R.id.heart_one);
        m_HeartTwoIv = findViewById(R.id.heart_two);
        m_HeartThreeIv = findViewById(R.id.heart_three);
        m_ScoreTv = findViewById(R.id.score_tv);

        m_Player = new Player();
        m_Shot = new GameItem();
        m_Gift = new GameItem();
        m_Enemy = new GameItem();

        //set imageViews location
        m_Player.setRect(0,0,0,0);
        m_EnemyIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyIv.getWidth())));
        m_EnemyIv.setY(-200);
        m_Enemy.setRect((int)m_EnemyIv.getX(),
                (int)m_EnemyIv.getY(),
                (int)m_EnemyIv.getX()+m_EnemyIv.getWidth(),
                (int)m_EnemyIv.getY()+m_EnemyIv.getHeight());
        m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth())));
        m_GiftIv.setY(-200);
        m_Gift.setRect((int)m_GiftIv.getX(),
                (int)m_GiftIv.getY(),
                (int)m_GiftIv.getX()+m_GiftIv.getWidth(),
                (int)m_GiftIv.getY()+m_GiftIv.getHeight());
        m_Shot.setRect((int)m_EnemyIv.getX(),
                (int)m_EnemyIv.getY(),
                (int)m_EnemyIv.getX()+m_EnemyIv.getWidth(),
                (int)m_EnemyIv.getY()+m_EnemyIv.getHeight());

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        m_ScreenSizeX = size.x;
        m_ScreenSizeY = size.y;

        m_ScoreTv.setText(getResources().getString(R.string.score) + " 0");

        //Set listeners
        m_PlayerIv.setOnTouchListener(this);
        m_FrameLayout.setOnTouchListener(this);

        m_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        runGame();

                    }
                });
            }}, 0, 20);
    }

    private void runGame() {
        createShots();
        moveEnemies();
        sendGift();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                m_XDelta = (int) (m_PlayerIv.getX() - event.getRawX());
                m_YDelta = (int) (m_PlayerIv.getY() - event.getRawY());
                m_LastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_PlayerIv.getWidth())
                    m_PlayerIv.setX(event.getRawX() + m_XDelta);
                if (event.getRawY() + m_YDelta > 0 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_PlayerIv.getHeight())
                    m_PlayerIv.setY(event.getRawY() + m_YDelta);

                m_LastAction = MotionEvent.ACTION_MOVE;
                m_Player.setRect((int) m_PlayerIv.getY(),(int) m_PlayerIv.getX(),(int) m_PlayerIv.getY()+ m_PlayerIv.getHeight(),(int) m_PlayerIv.getX()+ m_PlayerIv.getWidth());
                break;

            case MotionEvent.ACTION_UP:
                break;

            default:
                return false;
        }
        return true;
    }

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.pause_btn:
                handelOnPauseBtnClick();
            case R.id.home_button:
                handelOnHomeBtnClick();


        }
    }

    private void handelOnHomeBtnClick() {
        m_HomeButton.setEnabled(false);

        if(isPause == false) {
            m_Timer.cancel();
            m_Timer = null;
            /*animator.pause();
            playerAnimation.stop();*/ //todo:animation

            LayoutInflater inflater = getLayoutInflater();
            //View alertLayout = inflater.inflate(R.layout.exit_dialog, null); todo:alert dialog

            AlertDialog.Builder alert = new AlertDialog.Builder(RunGameActivity.this);
            //alert.setView(alertLayout); todo: alert dialog

            alert.setCancelable(false);

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //todo: string convert
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(RunGameActivity.this, MainActivity.class);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    finish();
                }

            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() { //todo: string convert
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    m_Timer = new Timer();
                    m_Timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            m_Handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    runGame();
                                }
                            });
                        }
                    }, 0, 20);
                    /*animator.resume();
                    playerAnimation.start();*/
                }

            });

            AlertDialog dialog = alert.create();
            dialog.show();
        }
        m_HomeButton.setEnabled(true);
    }

    private void handelOnPauseBtnClick() {
        if (isStart) {
            if (isPause == false) {

                isPause = true;
                m_Timer.cancel();
                m_Timer = null;
                /*animator.pause();
                playerAnimation.stop();
                fireSound.pause();*/ //todo: animation

                m_PauseButton.setBackgroundResource(R.drawable.ic_paused); //todo: change to play botton icon
            } else {
                isPause = false;
                m_PauseButton.setBackgroundResource(R.drawable.ic_paused); //todo: change to pause botton icon
                /*animator.resume();
                playerAnimation.start();
                fireSound.start();*/ //todo: animation

                m_Timer = new Timer();
                m_Timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        m_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                runGame();
                            }
                        });
                    }
                }, 0, 20);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop(); // todo : stop music

        if (isPause == false) {

            try {
                isPause = true;

                m_Timer.cancel();
                m_Timer = null;

                /*animator.pause();
                playerAnimation.stop();*/ // todo:animatoion

                m_PauseButton.setBackgroundResource(R.drawable.ic_paused); //todo: change to play icon

            } catch (Exception e) { }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*mediaPlayer.release();
        play();*/ //todo:play the music
    }

    private void createShots()
    {
        int ivX = (int)m_ShotsIv.getX();
        int ivY = (int)m_ShotsIv.getY();

        ivY -= m_ScreenSizeY / 20;
        if (ivY < 0 ) {
            ivY = (int)m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight()/2;
            ivX = ((int)m_PlayerIv.getX() + m_PlayerIv.getWidth()/2) - m_ShotsIv.getWidth()/2;
        }
        m_ShotsIv.setX(ivX);
        m_ShotsIv.setY(ivY);

        m_Shot.setRect(ivY,ivX,ivY+m_ShotsIv.getHeight(),ivX+m_ShotsIv.getWidth());
    }


    private void sendGift() {

        checkIfTakeGifts();
        m_GiftIv.setY(m_GiftIv.getY() + m_ScreenSizeY/1000);
        if(m_GiftIv.getY() > m_ScreenSizeY)
        {
            m_GiftIv.setY(-40);
            m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth())));
        }

        m_Gift.setRect((int)m_GiftIv.getY(),
                (int)m_GiftIv.getX(),
                (int)m_GiftIv.getY()+m_GiftIv.getHeight(),
                (int)m_GiftIv.getX()+m_GiftIv.getWidth());
    }

    private void moveEnemies()
    {
        checkIfHitEnemies();
        m_EnemyIv.setY(m_EnemyIv.getY()+m_ScreenSizeY/500);
        if(m_EnemyIv.getY() > m_ScreenSizeY)
        {
            m_EnemyIv.setY(-300);
            m_EnemyIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyIv.getWidth())));
        }

        m_Enemy.setRect((int)m_EnemyIv.getY(),
                (int)m_EnemyIv.getX(),
                (int)m_EnemyIv.getY()+m_EnemyIv.getHeight(),
                (int)m_EnemyIv.getX()+m_EnemyIv.getWidth());
    }

    private void checkIfHitEnemies() {
        if (Rect.intersects(m_Shot.getRect(), m_Enemy.getRect())) {
            m_EnemyIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_EnemyIv.getWidth())));
            m_EnemyIv.setY(-300);

            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            /// todo: add score for kill enemies
            }
        }

    private void checkIfTakeGifts()
    {

        if(Rect.intersects(m_Gift.getRect(),m_Player.getRect()))
        {
            m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth())));
            m_GiftIv.setY(((int) Math.floor(Math.random() * (1000 - 100))) * -1);
             //todo: add score for take gifts
        }
    }
}
