package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TutorialActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    ImageView m_Player;
    ImageView m_Enemy;
    ImageView m_Gift;
    ImageView m_Shots;
    ImageView m_Asteroid;
    Rect m_PlayerRect;
    Rect m_EnemyRect;
    Rect m_AsteroidRect;
    Rect m_GiftRect;
    Rect m_ShotsRect;
    Button m_SkipBtn;
    TextView m_TutorialTv;
    ImageView m_BackgroundOne;
    ImageView m_BackgroundTwo;
    FrameLayout m_FrameLayout;

    private boolean m_IsLearning;
    private boolean m_IsKnowToMove;
    private boolean m_IsKnowToKill;
    private boolean m_IsKnowToTakeGift;
    private boolean m_IsKnowToAvoidAsteroid;
    private int m_GiftsCollectedCounter;
    private int m_AsteroidsPassedCounter;
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
    private int m_AsteroidX;
    private int m_AsteroidY;
    private  int m_GiftX;
    private  int m_GiftY;

    //music and vibrate;
    private MusicService mServ;
    HomeWatcher mHomeWatcher;
    private boolean mIsBound = false;
    private boolean mIsMusic,mIsVibrate,mIsSound;
    private Vibrator v;

    //animations
    private Animation m_AnimationBlink;
    private AnimationDrawable m_EnemyOneAnim;

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
        m_Asteroid = findViewById(R.id.asteroid_imageView);
        m_Player = findViewById(R.id.player_imageView);
        m_Shots = findViewById(R.id.shot_imageView);
        m_Gift = findViewById(R.id.gift_imageView);
        m_BackgroundOne = findViewById(R.id.background_one);
        m_BackgroundTwo = findViewById(R.id.background_two);
        m_SkipBtn = findViewById(R.id.skip_btn);

        //Set listeners
        m_Player.setOnTouchListener(this);
        m_SkipBtn.setOnClickListener(this);
        m_FrameLayout.setOnTouchListener(this);

        //Set player info
        m_PlayerRect = new Rect(0,0,0,0);

        //Set enemy location
        m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
        m_EnemyY = -200;
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);
        m_EnemyRect = new Rect(m_EnemyX,m_EnemyY,m_EnemyX+m_Enemy.getWidth(),m_EnemyY+m_Enemy.getHeight());

        //Set asteroid location
        m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()));
        m_AsteroidY = -200;
        m_Asteroid.setX(m_AsteroidX);
        m_Asteroid.setY(m_AsteroidY);
        m_AsteroidRect = new Rect(m_AsteroidX,m_AsteroidY,m_AsteroidX+m_Asteroid.getWidth(),m_AsteroidY+m_Asteroid.getHeight());

        //set gift location
        m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));
        m_GiftY = -200;
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

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //initialize animation
        m_AnimationBlink = AnimationUtils.loadAnimation(this,R.anim.hit_spaceship_blink);
        m_EnemyOneAnim =  (AnimationDrawable) m_Enemy.getDrawable();
        m_EnemyOneAnim.start();



        //getExtras
        Intent intent = getIntent();
        mIsMusic = intent.getBooleanExtra("Music",false);
        mIsVibrate = intent.getBooleanExtra("Vibrate",false);
        mIsSound = intent.getBooleanExtra("Sound",false);

        //Music background
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

        //set boolean
        m_IsLearning = true;
        m_IsKnowToMove = false;
        m_IsKnowToKill = false;
        m_IsKnowToTakeGift = false;
        m_IsKnowToAvoidAsteroid = false;
        m_GiftsCollectedCounter = 0;
        m_AsteroidsPassedCounter = 0;
        m_EnemiesKilledCounter = 0;
        m_PlayerMovedCounter = 0;

        m_TutorialTv.setText(getResources().getString(R.string.move_space) +"\n" + m_PlayerMovedCounter + "/4");

        m_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(m_IsLearning) {
                            createShots();
                            if (m_IsKnowToMove)
                                moveEnemies();
                            if (m_IsKnowToKill)
                                moveAsteroid();
                            if (m_IsKnowToAvoidAsteroid)
                                sendGift();
                            if (m_IsKnowToTakeGift) {
                                Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                                intent.putExtra("Sound", mIsSound);
                                intent.putExtra("Vibrate", mIsVibrate);
                                intent.putExtra("Music", mIsMusic);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                TutorialActivity.this.finish();
                                m_IsLearning = false;
                                m_Timer.cancel();
                            }
                        }
                    }
                });
            }
        }, 0, 20);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                m_XDelta = (int) (m_Player.getX() - event.getRawX());
                m_YDelta = (int) (m_Player.getY() - event.getRawY());
                m_LastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getRawX() + m_XDelta > 0 && event.getRawX() + m_XDelta < m_ScreenSizeX - m_Player.getWidth())
                    m_Player.setX(event.getRawX() + m_XDelta);
                if (event.getRawY() + m_YDelta > m_ScreenSizeY/2 && event.getRawY() + m_YDelta < m_ScreenSizeY - m_Player.getHeight())
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
                    m_TutorialTv.setText(getResources().getString(R.string.move_space) + "\n" + m_PlayerMovedCounter + "/4");

                    if(m_PlayerMovedCounter == 4)
                    {
                        m_IsKnowToMove = true;
                        m_TutorialTv.setText(getResources().getString(R.string.kill_enemies) +"\n" + m_EnemiesKilledCounter + "/3");
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
                vibrate();
                playSound(R.raw.click_electronic);
                Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                intent.putExtra("Sound",mIsSound);
                intent.putExtra("Vibrate",mIsVibrate);
                intent.putExtra("Music",mIsMusic);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                TutorialActivity.this.finish();
                m_IsLearning = false;
                m_Timer.cancel();
        }
    }

    private void createShots()
    {
        int ivX = (int)m_Shots.getX();
        int ivY = (int)m_Shots.getY();

        ivY -= m_ScreenSizeY / 40;
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
        m_GiftY += m_ScreenSizeY/400;
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
        checkIfEnemyPlayerCollision();
        m_EnemyY += m_ScreenSizeY/500;
        if(m_EnemyY > m_ScreenSizeY)
        {
            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()*2));
        }
        m_Enemy.setX(m_EnemyX);
        m_Enemy.setY(m_EnemyY);

        m_EnemyRect.left = m_EnemyX;
        m_EnemyRect.top = m_EnemyY;
        m_EnemyRect.right = m_EnemyX+m_Enemy.getWidth();
        m_EnemyRect.bottom = m_EnemyY+m_Enemy.getHeight();

    }

    private void checkIfEnemyPlayerCollision()
    {
        if (Rect.intersects(m_PlayerRect,m_EnemyRect)){
            vibrate();
            playSound(R.raw.crash_sound);
            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));
            m_Enemy.setX(m_EnemyX);
            m_Enemy.setY(m_EnemyY);

            m_EnemyRect.left = m_EnemyX;
            m_EnemyRect.top = m_EnemyY;
            m_EnemyRect.right = m_EnemyX+m_Enemy.getWidth();
            m_EnemyRect.bottom = m_EnemyY+m_Enemy.getHeight();

            Toast.makeText(TutorialActivity.this,getResources().getString(R.string.collision_alert_tutorial),Toast.LENGTH_SHORT).show();
            m_Player.startAnimation(m_AnimationBlink);

        }
    }

    private void moveAsteroid() {
        checkifAsteroidPlayerCollision();
        m_AsteroidY += m_ScreenSizeY/400;
        if(m_AsteroidY > m_ScreenSizeY)
        {
            m_AsteroidY = -40;
            m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()));

            if (!m_IsKnowToAvoidAsteroid) {
                m_AsteroidsPassedCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.avoid_asteroids) + "\n" + m_AsteroidsPassedCounter + "/2");
                if (m_AsteroidsPassedCounter == 2) {
                    m_IsKnowToAvoidAsteroid = true;
                    m_TutorialTv.setText(getResources().getString(R.string.take_gifts) + "\n" + m_GiftsCollectedCounter + "/3");
                }
            }

        }
        m_Asteroid.setX(m_AsteroidX);
        m_Asteroid.setY(m_AsteroidY);

        m_AsteroidRect.left = m_AsteroidX;
        m_AsteroidRect.top = m_AsteroidY;
        m_AsteroidRect.right = m_AsteroidX+m_Asteroid.getWidth();
        m_AsteroidRect.bottom = m_AsteroidY+m_Asteroid.getHeight();

    }

    private void checkifAsteroidPlayerCollision()
    {
        if (Rect.intersects(m_PlayerRect,m_AsteroidRect)){
            vibrate();
            playSound(R.raw.crash_sound);
            m_AsteroidY = -40;
            m_AsteroidX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Asteroid.getWidth()));
            m_Asteroid.setX(m_AsteroidX);
            m_Asteroid.setY(m_AsteroidY);

            m_AsteroidRect.left = m_AsteroidX;
            m_AsteroidRect.top = m_AsteroidY;
            m_AsteroidRect.right = m_AsteroidX+m_Asteroid.getWidth();
            m_AsteroidRect.bottom = m_AsteroidY+m_Asteroid.getHeight();

            Toast.makeText(TutorialActivity.this,getResources().getString(R.string.collision_alert_tutorial),Toast.LENGTH_SHORT).show();
            m_Player.startAnimation(m_AnimationBlink);
        }
    }

    private void checkIfHitEnemies() {
        if (Rect.intersects(m_ShotsRect, m_EnemyRect)) {
            playSound(R.raw.explode);
            m_EnemyY = -300;
            m_EnemyX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Enemy.getWidth()));

            m_Enemy.setX(m_EnemyX);
            m_Enemy.setY(m_EnemyY);

            m_Shots.setX((int) m_Player.getY() - m_Player.getHeight() + m_Shots.getHeight() / 2);
            m_Shots.setY(((int) m_Player.getX() + m_Player.getWidth() / 2) - m_Shots.getWidth() / 2);

            if (!m_IsKnowToKill) {
                m_EnemiesKilledCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.kill_enemies) + "\n" + m_EnemiesKilledCounter + "/3");
                if (m_EnemiesKilledCounter == 3) {
                    m_IsKnowToKill = true;
                    m_TutorialTv.setText(getResources().getString(R.string.avoid_asteroids)+"\n" + m_AsteroidsPassedCounter + "/2");
                }
            }
        }
    }

    private void checkIfTakeGifts()
    {

        if(Rect.intersects(m_GiftRect,m_PlayerRect))
        {
            playSound(R.raw.gift_sound);
            m_GiftY = ((int) Math.floor(Math.random() * (1000 - 100))) * -1;
            m_GiftX = (int) Math.floor(Math.random() * (m_ScreenSizeX - m_Gift.getWidth()));

            m_Gift.setX(m_GiftX);
            m_Gift.setY(m_GiftY);

            if(!m_IsKnowToTakeGift) {
                m_GiftsCollectedCounter++;
                m_TutorialTv.setText(getResources().getString(R.string.take_gifts) + "\n" + m_GiftsCollectedCounter + "/3");

                if(m_GiftsCollectedCounter == 3) {
                    m_IsKnowToTakeGift = true;
                    m_TutorialTv.setText(getResources().getString(R.string.finish_tutorial));
                }
            }
        }
    }

    private ServiceConnection Scon = new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
            if(!mIsMusic && mServ != null)
            {
                mServ.stopMusic();
            }
            else
                {
                    mServ.changeMusic(R.raw.app_music_2);
                }
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }


    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }



    protected void onPause() {
        super.onPause();

        try {
            m_IsLearning = false;
            m_Timer.cancel();
            m_Timer = null;

        } catch (Exception e) { }

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null && mIsMusic) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_IsLearning = true;
        m_Timer = new Timer();
        m_Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                m_Handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(m_IsLearning) {
                            createShots();
                            if (m_IsKnowToMove)
                                moveEnemies();
                            if (m_IsKnowToKill)
                                moveAsteroid();
                            if (m_IsKnowToAvoidAsteroid)
                                sendGift();
                            if (m_IsKnowToTakeGift) {
                                Intent intent = new Intent(TutorialActivity.this, RunGameActivity.class);
                                intent.putExtra("Sound", mIsSound);
                                intent.putExtra("Vibrate", mIsVibrate);
                                intent.putExtra("Music", mIsMusic);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                TutorialActivity.this.finish();
                                m_IsLearning = false;
                                m_Timer.cancel();
                            }
                        }
                    }
                });
            }
        }, 0, 20);

        //music
        if (mServ != null&& mIsMusic) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //music
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }

    private void vibrate() {
        if(mIsVibrate)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    private void playSound(int sound) {
        if(mIsSound){
            MediaPlayer pressSound = MediaPlayer.create(TutorialActivity.this, sound);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
    }
}