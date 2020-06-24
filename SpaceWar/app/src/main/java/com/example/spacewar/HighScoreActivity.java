package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {
    VideoView m_VideoView;
    MediaPlayer m_MediaPlayer;
    int m_VideoCurrPosition;
    RecyclerView m_RecyclerView;
    LinearLayout m_HighScoreLayout;
    TextView m_HighScoreTv;
    Animation upToDown,downToUp;

    SharedPreferences firstPlaceScore;
    SharedPreferences secondPlaceScore;
    SharedPreferences thirdPlaceScore;
    SharedPreferences fourPlaceScore;
    SharedPreferences fivePlaceScore;
    SharedPreferences sixPlaceScore;
    SharedPreferences sevenPlaceScore;
    SharedPreferences eightPlaceScore;
    SharedPreferences ninePlaceScore;
    SharedPreferences tenPlaceScore;

    SharedPreferences firstPlaceName;
    SharedPreferences secondPlaceName;
    SharedPreferences thirdPlaceName;
    SharedPreferences fourPlaceName;
    SharedPreferences fivePlaceName;
    SharedPreferences sixPlaceName;
    SharedPreferences sevenPlaceName;
    SharedPreferences eightPlaceName;
    SharedPreferences ninePlaceName;
    SharedPreferences tenPlaceName;

    List<PlayerCard> top10Players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        //Animation start activity
        m_HighScoreLayout = findViewById(R.id.high_score_layout);
        m_HighScoreTv = findViewById(R.id.high_score_tv);

        upToDown = AnimationUtils.loadAnimation(this,R.anim.up_to_down);
        downToUp = AnimationUtils.loadAnimation(this,R.anim.down_to_up);
        m_HighScoreLayout.setAnimation(downToUp);
        m_HighScoreTv.setAnimation(upToDown);

        //Put video on background
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

        firstPlaceScore = getSharedPreferences("FIRST_PLACE_S", Context.MODE_PRIVATE);
        secondPlaceScore = getSharedPreferences("SEC_PLACE_S", Context.MODE_PRIVATE);
        thirdPlaceScore = getSharedPreferences("THIRD_PLACE_S", Context.MODE_PRIVATE);
        fourPlaceScore = getSharedPreferences("FOUR_PLACE_S", Context.MODE_PRIVATE);
        fivePlaceScore = getSharedPreferences("FIVE_PLACE_S", Context.MODE_PRIVATE);
        sixPlaceScore = getSharedPreferences("SIX_PLACE_S", Context.MODE_PRIVATE);
        sevenPlaceScore = getSharedPreferences("SEVEN_PLACE_S", Context.MODE_PRIVATE);
        eightPlaceScore = getSharedPreferences("EIGHT_PLACE_S", Context.MODE_PRIVATE);
        ninePlaceScore = getSharedPreferences("NINE_PLACE_S", Context.MODE_PRIVATE);
        tenPlaceScore = getSharedPreferences("TEN_PLACE_S", Context.MODE_PRIVATE);

        firstPlaceName = getSharedPreferences("FIRST_PLACE_N", Context.MODE_PRIVATE);
        secondPlaceName = getSharedPreferences("SEC_PLACE_N", Context.MODE_PRIVATE);
        thirdPlaceName = getSharedPreferences("THIRD_PLACE_N", Context.MODE_PRIVATE);
        fourPlaceName = getSharedPreferences("FOUR_PLACE_N", Context.MODE_PRIVATE);
        fivePlaceName = getSharedPreferences("FIVE_PLACE_N", Context.MODE_PRIVATE);
        sixPlaceName = getSharedPreferences("SIX_PLACE_N", Context.MODE_PRIVATE);
        sevenPlaceName = getSharedPreferences("SEVEN_PLACE_N", Context.MODE_PRIVATE);
        eightPlaceName = getSharedPreferences("EIGHT_PLACE_N", Context.MODE_PRIVATE);
        ninePlaceName = getSharedPreferences("NINE_PLACE_N", Context.MODE_PRIVATE);
        tenPlaceName = getSharedPreferences("TEN_PLACE_N", Context.MODE_PRIVATE);

        top10Players = new ArrayList<>();

        top10Players.add(new PlayerCard(firstPlaceScore.getInt("FIRST_PLACE_S",0), firstPlaceName.getString("FIRST_PLACE_N"," ")));
        top10Players.add(new PlayerCard(secondPlaceScore.getInt("SEC_PLACE_S",0), secondPlaceName.getString("SEC_PLACE_N"," ")));
        top10Players.add(new PlayerCard(thirdPlaceScore.getInt("THIRD_PLACE_S",0), thirdPlaceName.getString("THIRD_PLACE_N"," ")));
        top10Players.add(new PlayerCard(fourPlaceScore.getInt("FOUR_PLACE_S",0), fourPlaceName.getString("FOUR_PLACE_N"," ")));
        top10Players.add(new PlayerCard(fivePlaceScore.getInt("FIVE_PLACE_S",0), fivePlaceName.getString("FIVE_PLACE_N"," ")));
        top10Players.add(new PlayerCard(sixPlaceScore.getInt("SIX_PLACE_S",0), sixPlaceName.getString("SIX_PLACE_N"," ")));
        top10Players.add(new PlayerCard(sevenPlaceScore.getInt("SEVEN_PLACE_S",0), sevenPlaceName.getString("SEVEN_PLACE_N"," ")));
        top10Players.add(new PlayerCard(eightPlaceScore.getInt("EIGHT_PLACE_S",0), eightPlaceName.getString("EIGHT_PLACE_N"," ")));
        top10Players.add(new PlayerCard(ninePlaceScore.getInt("NINE_PLACE_S",0), ninePlaceName.getString("NINE_PLACE_N"," ")));
        top10Players.add(new PlayerCard(tenPlaceScore.getInt("TEN_PLACE_S",0), tenPlaceName.getString("TEN_PLACE_N"," ")));


        PlayerAdapter playerAdapter = new PlayerAdapter(top10Players);
        m_RecyclerView.setAdapter(playerAdapter);



        }
    }
