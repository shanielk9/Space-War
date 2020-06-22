package com.example.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.Random;

import static com.example.spacewar.GameView.m_EnemyDestroyed;
import static com.example.spacewar.GameView.m_score;

public class Enemy {

    private Bitmap m_Icon;
    private int m_X, m_Y; //Enemy location
    private int m_ScreenSizeX;
    private int m_ScreenSizeY;
    private int m_EnemiesArray[];
    private Rect m_Bounds; //to ask Eran
    private int m_MaxX;
    private int m_MaxY;
    private int m_HP;
    private int m_Speed;
    private int m_EnemyKillScore;

    public Enemy(Context context, int screenSizeX, int screenSizeY, SoundPlayer sp, int iconBounds, int lives, int killScore) {
        m_ScreenSizeX = screenSizeX;
        m_ScreenSizeY = screenSizeY;

        m_HP = lives;
        m_EnemyKillScore = killScore;

        m_EnemiesArray = new int[]{R.drawable.ic_alien, R.drawable.ic_golem, R.drawable.ic_inhabitants,R.drawable.ic_octopus};
        Random random = new Random();

        m_Icon = getBitmapFromVectorDrawable(context, m_EnemiesArray[random.nextInt(iconBounds)]);
        m_Icon = Bitmap.createScaledBitmap(m_Icon, 150, 150, false);

        m_Speed = random.nextInt(3) + 1;

        m_MaxX = screenSizeX - m_Icon.getWidth();
        m_MaxY = screenSizeY - m_Icon.getHeight();

        m_X = random.nextInt(m_MaxX);
        m_Y = 0 - m_Icon.getHeight();

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
        // TODO : find a way to put destroy music - logic and UI do not know each other!
    }

    public void update(){
        m_Y += 7 * m_Speed;

        m_Bounds.left = m_X;
        m_Bounds.top = m_Y;
        m_Bounds.right = m_X + m_Icon.getWidth();
        m_Bounds.bottom = m_Y + m_Icon.getHeight();
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


    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
