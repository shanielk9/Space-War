package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    TextView stage_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        stage_tv = findViewById(R.id.stage_tv);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.sequantial);
        stage_tv.startAnimation(anim);

    }
}
