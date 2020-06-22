package com.example.spacewar;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.example.spacewar.R;

public class SoundPlayer implements Runnable {

    private Thread mSoundThread;
    private volatile boolean mIsPlaying;
    private SoundPool mSoundPool;
    private int mExplodeId, mLaserId, mCrashId,mGiftTakeId;
    private boolean mIsLaserPlaying, mIsExplodePlaying, mIsCrashPlaying, mIsGiftPlaying;

    public SoundPlayer(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        mExplodeId = mSoundPool.load(context, R.raw.explode, 1);
        mLaserId = mSoundPool.load(context, R.raw.laser_shot, 1);
        mCrashId = mSoundPool.load(context, R.raw.crash_sound, 1);
        mGiftTakeId = mSoundPool.load(context, R.raw.gift_sound, 1);
    }

    @Override
    public void run() {
        while (mIsPlaying){
            if (mIsLaserPlaying){
                mSoundPool.play(mLaserId, 1, 1, 1, 0, 1f);
                mIsLaserPlaying = false;
            }

            if (mIsExplodePlaying){
                mSoundPool.play(mExplodeId, 1, 1, 1, 0, 1);
                mIsExplodePlaying = false;
            }

            if (mIsCrashPlaying){
                mSoundPool.play(mCrashId, 1, 1, 1, 0, 1);
                mIsCrashPlaying = false;
            }

            if (mIsGiftPlaying){
                mSoundPool.play(mGiftTakeId, 1, 1, 1, 0, 1);
                mIsGiftPlaying = false;
            }
        }
    }

    public void playCrash(){
        mIsCrashPlaying = true;
    }

    public void playLaser(){
        mIsLaserPlaying = true;
    }

    public void playExplode(){
        mIsExplodePlaying = true;
    }

    public void playGift(){
        mIsGiftPlaying = true;
    }

    public void resume(){
        mIsPlaying = true;
        mSoundThread = new Thread(this);
        mSoundThread.start();
    }

    public void pause() throws InterruptedException {
        mIsPlaying = false;
        mSoundThread.join();
    }
}
