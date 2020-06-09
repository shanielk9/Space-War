package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class BufferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BufferActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
                startActivity(intent);

            }
        }, 4000);

    }
}
