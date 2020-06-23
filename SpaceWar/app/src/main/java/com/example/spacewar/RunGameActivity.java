package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class RunGameActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private static final int GIFT_VALUE = 15;
    private static final int ENEMY_ONE_VALUE = 20;
    private static final int ENEMY_TWO_VALUE = 20;
    private static final int ENEMY_THREE_VALUE = 20;
    private static final int ENEMY_FOUR_VALUE = 20;
    private static final int ENEMY_FIVE_VALUE = 20;
    private static final int ENEMY_SIX_VALUE = 20;
    private static final int MAX_ENEMY_LVL_ONE = 20;
    private static final int MAX_ENEMY_LVL_TWO = 35;
    private static final int MAX_ENEMY_LVL_THREE = 50;

    ImageView m_BackgroundOne;
    ImageView m_BackgroundTwo;
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;

    LinearLayout m_SettingsLayout;
    ImageView m_PlayerIv;
    ImageView m_EnemyOneIv;
    ImageView m_EnemyTwoIv;
    ImageView m_EnemyThreeIv;
    ImageView m_EnemyFourIv;
    ImageView m_EnemyFiveIv;
    ImageView m_EnemySixIv;
    ImageView m_GiftIv;
    ImageView m_ShotsIv;
    Button m_HomeButton;
    Button m_PauseButton;
    ImageView m_HeartOneIv;
    ImageView m_HeartTwoIv;
    ImageView m_HeartThreeIv;
    TextView m_ScoreTv;
    TextView m_LevelTv;

    Player m_Player;
    ValueGameItem m_Enemy_one;
    ValueGameItem m_Enemy_two;
    ValueGameItem m_Enemy_three;
    ValueGameItem m_Enemy_four;
    ValueGameItem m_Enemy_five;
    ValueGameItem m_Enemy_six;
    ValueGameItem m_Gift;
    GameItem m_Shot;

    int m_CurrLevel;
    int m_EnemiesCounter;

    //members for onTouch listener
    FrameLayout m_FrameLayout;
    private int m_XDelta;
    private int m_YDelta;
    private int m_LastAction;

    private boolean isStart = true;
    private boolean isPause = false;
    private boolean isMove = false;
    private boolean isPlay = true;

    //music and vibrate
    Vibrator v;

    private Timer m_Timer = new Timer();
    private Handler m_Handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_game);

        //find views by id
        m_SettingsLayout = findViewById(R.id.linear_settings_layout);
        m_FrameLayout = findViewById(R.id.frame_layout);
        m_PlayerIv = findViewById(R.id.player_imageView);
        m_EnemyOneIv = findViewById(R.id.enemy_one_imageView);
        m_EnemyTwoIv = findViewById(R.id.enemy_two_imageView);
        m_EnemyThreeIv = findViewById(R.id.enemy_three_imageView);
        m_EnemyFourIv = findViewById(R.id.enemy_four_imageView);
        m_EnemyFiveIv = findViewById(R.id.enemy_five_imageView);
        m_EnemySixIv = findViewById(R.id.enemy_six_imageView);
        m_GiftIv = findViewById(R.id.gift_imageView);
        m_ShotsIv = findViewById(R.id.shot_imageView);
        m_HomeButton = findViewById(R.id.home_btn);
        m_PauseButton = findViewById(R.id.pause_btn);
        m_HeartOneIv = findViewById(R.id.heart_one);
        m_HeartTwoIv = findViewById(R.id.heart_two);
        m_HeartThreeIv = findViewById(R.id.heart_three);
        m_ScoreTv = findViewById(R.id.score_tv);
        m_LevelTv = findViewById(R.id.next_level_tv);

        //initialize objects
        m_Player = new Player();
        m_Shot = new GameItem();
        m_Gift = new ValueGameItem(GIFT_VALUE);
        m_Enemy_one = new ValueGameItem(ENEMY_ONE_VALUE);
        m_Enemy_two = new ValueGameItem(ENEMY_TWO_VALUE);
        m_Enemy_three = new ValueGameItem(ENEMY_THREE_VALUE);
        m_Enemy_four = new ValueGameItem(ENEMY_FOUR_VALUE);
        m_Enemy_five = new ValueGameItem(ENEMY_FIVE_VALUE);
        m_Enemy_six = new ValueGameItem(ENEMY_SIX_VALUE);

        //set imageViews location
       initializeNextLevel();

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        m_ScreenSizeX = size.x;
        m_ScreenSizeY = size.y;
        m_CurrLevel = 1;

        m_ScoreTv.setText(getResources().getString(R.string.score) + " 0");

        //Set listeners
        m_PlayerIv.setOnTouchListener(this);
        m_PauseButton.setOnClickListener(this);
        m_HomeButton.setOnClickListener(this);
        m_FrameLayout.setOnTouchListener(this);

        //set music and vibrate
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

    private void initializeNextLevel() {
        m_EnemiesCounter = 0;
        m_Player.setRect(0,0,0,0);
        m_Shot.setRect((int) m_EnemyOneIv.getX(),
                (int) m_EnemyOneIv.getY(),
                (int) m_EnemyOneIv.getX()+ m_EnemyOneIv.getWidth(),
                (int) m_EnemyOneIv.getY()+ m_EnemyOneIv.getHeight());

        setEnemyState(m_EnemyOneIv,m_Enemy_one,-200);
        setEnemyState(m_EnemyTwoIv,m_Enemy_two, -400);
        setEnemyState(m_EnemyThreeIv,m_Enemy_three, -550);
        setEnemyState(m_GiftIv,m_Gift,-200);

        if(m_CurrLevel > 1)
        {
            setEnemyState(m_EnemyFourIv,m_Enemy_four,-700);
            setEnemyState(m_EnemyFiveIv,m_Enemy_five,-600);
        }

        if(m_CurrLevel > 2)
        {
            setEnemyState(m_EnemySixIv,m_Enemy_six,-1000);
        }
    }

    private void setEnemyState(ImageView enemyIv, ValueGameItem enemy,int dist) {
        enemyIv.setVisibility(View.VISIBLE);
        enemyIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIv.getWidth())));
        enemyIv.setY(dist);
        enemy.setRect((int) enemyIv.getX(),
                (int) enemyIv.getY(),
                (int) enemyIv.getX()+ enemyIv.getWidth(),
                (int) enemyIv.getY()+ enemyIv.getHeight());
    }


    private void runGame() {
        createShots();

        moveEnemies(m_EnemyOneIv,m_Enemy_one);
        moveEnemies(m_EnemyTwoIv,m_Enemy_two);
        moveEnemies(m_EnemyThreeIv,m_Enemy_three);
        sendGift();

        if(m_CurrLevel > 1)
        {
            moveEnemies(m_EnemyFourIv,m_Enemy_four);
            moveEnemies(m_EnemyFiveIv,m_Enemy_five);
        }

        if(m_CurrLevel > 2)
        {
            moveEnemies(m_EnemySixIv,m_Enemy_six);
        }
        checkIfLevelEnd();

    }

    private void checkIfLevelEnd() {
        if(isPlay) {
            switch (m_CurrLevel) {
                case 1:
                    if (m_EnemiesCounter >= MAX_ENEMY_LVL_ONE) {
                        m_CurrLevel++;
                        initializeNextLevel();
                        m_LevelTv.setText(getResources().getString(R.string.level_two));
                        handelOnPauseBtnClick();
                        break;
                    }
                case 2:
                    if (m_EnemiesCounter >= MAX_ENEMY_LVL_TWO) {
                        m_CurrLevel++;
                        initializeNextLevel();
                        m_LevelTv.setText(getResources().getString(R.string.level_three));
                        handelOnPauseBtnClick();
                        break;
                    }
                case 3:
                    if (m_EnemiesCounter >= MAX_ENEMY_LVL_THREE) {
                        Intent intent = new Intent(RunGameActivity.this, WinningActivity.class);
                        startActivity(intent);
                        this.finish();
                    }
            }
        }
        else {
            Intent intent = new Intent(RunGameActivity.this, GameOverActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(!isPause){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                m_XDelta = (int) (m_PlayerIv.getX() - event.getRawX());
                m_YDelta = (int) (m_PlayerIv.getY() - event.getRawY());
                m_LastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                isMove = true;
                if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_PlayerIv.getWidth())
                    m_PlayerIv.setX(event.getRawX() + m_XDelta);
                if (event.getRawY() + m_YDelta > m_ScreenSizeY/4 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_PlayerIv.getHeight() - m_SettingsLayout.getHeight() - 60)
                    m_PlayerIv.setY(event.getRawY() + m_YDelta);

                m_LastAction = MotionEvent.ACTION_MOVE;
                m_Player.setRect((int) m_PlayerIv.getY(),(int) m_PlayerIv.getX(),(int) m_PlayerIv.getY()+ m_PlayerIv.getHeight(),(int) m_PlayerIv.getX()+ m_PlayerIv.getWidth());
                break;

            case MotionEvent.ACTION_UP:
                isMove = false;
                break;

            default:
                return false;
        }}
        else
        {isMove = false;}
        return true;
    }

    public void onClick(View v)
    {
        if(!isMove){
        switch (v.getId()) {
            case R.id.pause_btn:
                handelOnPauseBtnClick();
                break;
            case R.id.home_btn:
                handelOnHomeBtnClick();
                break;
            }
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
            View alertLayout = inflater.inflate(R.layout.exit_dialog, null);

            AlertDialog.Builder alert = new AlertDialog.Builder(RunGameActivity.this);
            alert.setView(alertLayout);

            alert.setCancelable(false);

            alert.setPositiveButton(getResources().getString(R.string.yes_str), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(RunGameActivity.this, MainActivity.class);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    finish();
                }

            });

            alert.setNegativeButton(getResources().getString(R.string.no_str), new DialogInterface.OnClickListener() {
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
                    playerAnimation.start();*///todo: animation
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

                m_PauseButton.setBackgroundResource(R.drawable.ic_play);
            } else {
                isPause = false;
                m_PauseButton.setBackgroundResource(R.drawable.ic_paused);
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

                m_PauseButton.setBackgroundResource(R.drawable.ic_play);

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

    private void moveEnemies(ImageView enemyIV, ValueGameItem enemy)
    {
        checkIfHitEnemies(enemyIV,enemy);
        checkIfPlayerEnemyCollision(enemyIV,enemy);
        enemyIV.setY(enemyIV.getY()+m_ScreenSizeY/500);
        if(enemyIV.getY() > m_ScreenSizeY)
        {
            m_EnemiesCounter ++;
            enemyIV.setY(-300);
            enemyIV.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIV.getWidth())));
        }

        enemy.setRect((int) enemyIV.getY(),
                (int) enemyIV.getX(),
                (int) enemyIV.getY()+ enemyIV.getHeight(),
                (int) enemyIV.getX()+ enemyIV.getWidth());
    }

    private void checkIfHitEnemies(ImageView enemyIV, ValueGameItem enemy) {
        if (Rect.intersects(m_Shot.getRect(), enemy.getRect())) {

            m_EnemiesCounter++;
            enemyIV.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIV.getWidth())));
            enemyIV.setY(-300);

            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            m_Player.setScore(m_Player.getScore()+enemy.getValue());
            m_ScoreTv.setText(getResources().getString(R.string.score) + m_Player.getScore());
            }
        }

    private void checkIfTakeGifts()
    {

        if(Rect.intersects(m_Gift.getRect(),m_Player.getRect()))
        {
            m_GiftIv.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - m_GiftIv.getWidth())));
            m_GiftIv.setY(((int) Math.floor(Math.random() * (1000 - 100))) * -1);

            m_Player.setScore(m_Player.getScore()+m_Gift.getValue());
            m_ScoreTv.setText(getResources().getString(R.string.score) + m_Player.getScore());
        }
    }

    private void checkIfPlayerEnemyCollision(ImageView enemyIV, ValueGameItem enemy)
    {
        if (Rect.intersects(m_Player.getRect(), enemy.getRect())){
            m_Player.hit();

            enemyIV.setX((int) Math.floor(Math.random() * (m_ScreenSizeX - enemyIV.getWidth())));
            enemyIV.setY(-300);
            m_ShotsIv.setX((int) m_PlayerIv.getY() - m_PlayerIv.getHeight() + m_ShotsIv.getHeight() / 2);
            m_ShotsIv.setY(((int) m_PlayerIv.getX() + m_PlayerIv.getWidth() / 2) - m_ShotsIv.getWidth() / 2);

            if(m_Player.getLives() == 0)
            {
                m_HeartOneIv.setImageResource(R.drawable.ic_hearts_gray);

                Intent intent = new Intent(RunGameActivity.this, GameOverActivity.class);
                startActivity(intent);
                this.finish();
            }

            if(m_Player.getLives() == 1) {
                m_HeartTwoIv.setImageResource(R.drawable.ic_hearts_gray);
            }
            if(m_Player.getLives() == 2) {
                m_HeartThreeIv.setImageResource(R.drawable.ic_hearts_gray);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(1000);
            }

        }
    }


}
