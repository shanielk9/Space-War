package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class WinningActivity extends AppCompatActivity {

    LinearLayout m_TxtLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning);

        m_TxtLayout = findViewById(R.id.txt_layout);
        Animation layoutScaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        m_TxtLayout.setAnimation(layoutScaleUp);
    }
}
