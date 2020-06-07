package com.example.spacewar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    boolean isSoundCheck = true;
    boolean isVibrateCheck = true;
    Vibrator v;

    VideoView m_VideoView;
    MediaPlayer m_MediaPlayer;
    int m_VideoCurrPosition;

    Button m_PlayButton;
    Button m_LeaderBoardButton;

    String[] settings = {"Sound", "Music", "Vibrate"};
    boolean[] checkedItems = {true, true, true};

    HomeWatcher mHomeWatcher;
    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon = new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon,Context.BIND_AUTO_CREATE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_PlayButton = findViewById(R.id.PlayGame);
        m_PlayButton.setOnClickListener(this);

        m_LeaderBoardButton = findViewById(R.id.HighScore);
        m_LeaderBoardButton.setOnClickListener(this);

        m_VideoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.spacewar_background);
        m_VideoView.setVideoURI(uri);
        m_VideoView.start();

        m_VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                              @Override
                                              public void onPrepared(MediaPlayer mp) {
                                                  m_MediaPlayer = mp;
                                                  m_MediaPlayer.setLooping(true);
                                                  m_MediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                                                  if(m_VideoCurrPosition != 0) {
                                                      m_MediaPlayer.seekTo(m_VideoCurrPosition);
                                                      m_MediaPlayer.start();
                                                  }
                                              }
                                          }
        );
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

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_VideoCurrPosition = m_MediaPlayer.getCurrentPosition();
        m_VideoView.pause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_VideoView.start();

        //music
        if (mServ != null) {
            mServ.resumeMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_MediaPlayer.release();
        m_MediaPlayer = null;
//music
        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }

    @Override
    public void onClick(View v) {
        setSoundAndVibrateOnClick();

        switch (v.getId())
        {
            case R.id.HighScore:
                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                startActivity(intent);
                break;
            case R.id.PlayGame:
                Intent intent1 = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        setSoundAndVibrateOnClick();

        switch (item.getItemId()) {
            case R.id.setting:
                createAlertDialogForSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createAlertDialogForSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings:");

// add a checkbox list
        builder.setMultiChoiceItems(settings, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // user checked or unchecked a box
                setSoundAndVibrateOnClick();

                switch(which)
                {
                    case 0: //Sound
                        isSoundCheck = isChecked;
                        break;
                    case 1://Music
                        if(isChecked)
                            mServ.startMusic();
                        else
                            mServ.stopMusic();
                        break;
                    case 2://Vibtate
                        isVibrateCheck = isChecked;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value");
                }
            }
        });

// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setSoundAndVibrateOnClick()
    {
        if(isSoundCheck)
        {
            MediaPlayer pressSound = MediaPlayer.create(MainActivity.this, R.raw.press_sound);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
        if(isVibrateCheck)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }
}
