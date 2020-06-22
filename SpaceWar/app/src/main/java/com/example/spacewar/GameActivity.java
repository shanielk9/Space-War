package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    TextView stage_tv;
    GameView m_GameView;

    //for on touch event
    private int m_XDelta;
    private int m_YDelta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        m_GameView = new GameView(this, point.x, point.y);
        setContentView(m_GameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_GameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_GameView.pause();
    }
}
