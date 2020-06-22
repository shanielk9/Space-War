package com.example.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.ArrayList;

public class GamePlayer {

    private Bitmap m_Icon;
    private int m_X;
    private int m_Y;
    private ArrayList<Shot> m_Shots;
    private Context m_Context;
    private Rect m_Bounds;
    private int m_MarginFromSides;
    private int m_ScreenSizeX;

    public Bitmap get_Icon() {
        return m_Icon;
    }

    public int get_X() {
        return m_X;
    }

    public int get_Y() {
        return m_Y;
    }

    public ArrayList<Shot> get_Shots() {
        return m_Shots;
    }

    public Rect get_Bounds() {
        return m_Bounds;
    }

    private int m_ScreenSizeY;

    public GamePlayer(Context context, int screenSizeX, int screenSizeY, SoundPlayer soundPlayer) {
        m_ScreenSizeX = screenSizeX;
        m_ScreenSizeY = screenSizeY;
        m_Context = context;

        m_MarginFromSides = 16;


        //VectorDrawableCompat.create(context.getResources(),R.drawable.ic_battleship1,null);
        m_Icon = getBitmapFromVectorDrawable(context,R.drawable.ic_battleship1);
        m_Icon = Bitmap.createScaledBitmap(m_Icon, 200, 200, false);

        m_X = screenSizeX/2 - m_Icon.getWidth()/2;
        m_Y = screenSizeY - m_Icon.getHeight() - m_MarginFromSides;

        m_Shots = new ArrayList<>();

        m_Bounds = new Rect(m_X, m_Y, m_X + m_Icon.getWidth(), m_Y + m_Icon.getHeight());
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

    public void fire(){
        m_Shots.add(new Shot(m_Context, m_ScreenSizeX, m_ScreenSizeY, m_X, m_Y, m_Icon.getWidth(),m_Icon.getHeight()));
    }

    public void update(int x, int y) // while user change place of the battle ship it send the new to here
    {
        if (x > 0 && x < m_ScreenSizeX - m_Icon.getWidth())
            m_X = x;
        if (y > 0 &&  y < m_ScreenSizeY - m_Icon.getHeight())
            m_Y = y;

        m_Bounds.left = x;
        m_Bounds.top = y;
        m_Bounds.right = x + m_Icon.getWidth();
        m_Bounds.bottom = y + m_Icon.getHeight();

        for (Shot s : m_Shots) {
            s.updateShotLocation();
        }

        boolean isInsideTheScreen = true;
        while (isInsideTheScreen) {
            if (m_Shots.size() != 0) {
                if (m_Shots.get(0).get_Y() < 0) {
                    m_Shots.remove(0);
                }
            }
            if (m_Shots.size() == 0 || m_Shots.get(0).get_Y() >= 0) {
                isInsideTheScreen = false;
            }
        }
    }

}
