package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    VideoView m_VideoView;
    MediaPlayer m_MediaPlayer;
    int m_VideoCurrPosition;
    RecyclerView m_RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        m_VideoView = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.main_vid_background);
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

        m_RecyclerView = findViewById(R.id.recycler_view);
        m_RecyclerView.setHasFixedSize(true);
        m_RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<PlayerCard> top10Players = new ArrayList<>();
        top10Players.add(new PlayerCard(100,"Shani",R.drawable.ic_first));
        top10Players.add(new PlayerCard(90,"Inbar",R.drawable.ic_second_prize));
        top10Players.add(new PlayerCard(80,"Shain",R.drawable.ic_third_prize));
        top10Players.add(new PlayerCard(70,"Menahem",R.drawable.ic_four));
        top10Players.add(new PlayerCard(60,"Tamir",R.drawable.ic_five));
        top10Players.add(new PlayerCard(50,"Matan",R.drawable.ic_six));
        top10Players.add(new PlayerCard(40,"AvivA",R.drawable.ic_seven));
        top10Players.add(new PlayerCard(30,"HenGIVDKFFJG",R.drawable.ic_eight));
        top10Players.add(new PlayerCard(20,"Amit",R.drawable.ic_nine));
        top10Players.add(new PlayerCard(10,"Dan",R.drawable.ic_ten));

        PlayerAdapter playerAdapter = new PlayerAdapter(top10Players);
        m_RecyclerView.setAdapter(playerAdapter);
        }
    }
