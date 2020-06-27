package com.example.spacewar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {

    EditText m_NameEt;
    Button m_OkBtn;
    TextView m_PlayerScore;

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

    String score;
    List<PlayerCard> top10Players;

    private boolean mIsMusic,mIsVibrate,mIsSound;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //get Extras
        Intent intent = getIntent();
        score = String.valueOf(intent.getIntExtra("Score",0));
        mIsMusic = intent.getBooleanExtra("Music",false);
        mIsVibrate = intent.getBooleanExtra("Vibrate",false);
        mIsSound = intent.getBooleanExtra("Sound",false);

        playSound(R.raw.game_over);

        m_NameEt = findViewById(R.id.name_edit_text);
        m_OkBtn = findViewById(R.id.ok_btn);
        m_PlayerScore = findViewById(R.id.score_text_view);

        m_OkBtn.setOnClickListener(this);
        m_PlayerScore.setText(score);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ok_btn: {
                checkIfNeedToInitializeScoreOnSP();
                playSound(R.raw.click_electronic);
                vibrate();
            }
        }
    }

    private void vibrate() {
        if(mIsVibrate)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(500);
            }
        }
    }

    private void playSound(int sound) {
        if(mIsSound){
            MediaPlayer pressSound = MediaPlayer.create(GameOverActivity.this, sound);
            pressSound.setVolume(30,30);
            pressSound.start();
        }
    }

    private void checkIfNeedToInitializeScoreOnSP() {
        if(!m_NameEt.getText().toString().isEmpty()) {
            initializeList();
            int currScore = Integer.parseInt(m_PlayerScore.getText().toString());
            String name = m_NameEt.getText().toString();

            if (top10Players.get(9).get_Score() < currScore) {
                top10Players.remove(9);
                top10Players.add(new PlayerCard(currScore, name));
                Collections.sort(top10Players, new Comparator<PlayerCard>() {
                    @Override
                    public int compare(PlayerCard o1, PlayerCard o2) {
                        return Integer.compare(o2.get_Score(), o1.get_Score());
                    }
                });
                addNewPreference();
            }

            Intent intent = new Intent(GameOverActivity.this, HighScoreActivity.class);
            intent.putExtra("Music", mIsMusic);
            intent.putExtra("CallIntent","GameOver");
            startActivity(intent);
            this.finish();
        }
        else
        {
            Toast.makeText(GameOverActivity.this, getResources().getString(R.string.enter_your_name_msg), Toast.LENGTH_LONG).show();
        }
    }

    private void addNewPreference() {

        SharedPreferences.Editor editorFirstPlaceScore = firstPlaceScore.edit();
        SharedPreferences.Editor editorSecondPlaceScore = secondPlaceScore.edit();
        SharedPreferences.Editor editorThirdPlaceScore = thirdPlaceScore.edit();
        SharedPreferences.Editor editorFourPlaceScore = fourPlaceScore.edit();
        SharedPreferences.Editor editorFivePlaceScore = fivePlaceScore.edit();
        SharedPreferences.Editor editorSixPlaceScore = sixPlaceScore.edit();
        SharedPreferences.Editor editorSevenPlaceScore = sevenPlaceScore.edit();
        SharedPreferences.Editor editorEightPlaceScore = eightPlaceScore.edit();
        SharedPreferences.Editor editorNinePlaceScore = ninePlaceScore.edit();
        SharedPreferences.Editor editorTenPlaceScore = tenPlaceScore.edit();

        editorFirstPlaceScore.putInt("FIRST_PLACE_S", top10Players.get(0).get_Score());
        editorFirstPlaceScore.commit();
        editorSecondPlaceScore.putInt("SEC_PLACE_S", top10Players.get(1).get_Score());
        editorSecondPlaceScore.commit();
        editorThirdPlaceScore.putInt("THIRD_PLACE_S", top10Players.get(2).get_Score());
        editorThirdPlaceScore.commit();
        editorFourPlaceScore.putInt("FOUR_PLACE_S",top10Players.get(3).get_Score());
        editorFourPlaceScore.commit();
        editorFivePlaceScore.putInt("FIVE_PLACE_S", top10Players.get(4).get_Score());
        editorFivePlaceScore.commit();
        editorSixPlaceScore.putInt("SIX_PLACE_S", top10Players.get(5).get_Score());
        editorSixPlaceScore.commit();
        editorSevenPlaceScore.putInt("SEVEN_PLACE_S",top10Players.get(6).get_Score());
        editorSevenPlaceScore.commit();
        editorEightPlaceScore.putInt("EIGHT_PLACE_S", top10Players.get(7).get_Score());
        editorEightPlaceScore.commit();
        editorNinePlaceScore.putInt("NINE_PLACE_S", top10Players.get(8).get_Score());
        editorNinePlaceScore.commit();
        editorTenPlaceScore.putInt("TEN_PLACE_S", top10Players.get(9).get_Score());
        editorTenPlaceScore.commit();

        SharedPreferences.Editor editorFirstPlaceName = firstPlaceName.edit();
        SharedPreferences.Editor editorSecondPlaceName = secondPlaceName.edit();
        SharedPreferences.Editor editorThirdPlaceName = thirdPlaceName.edit();
        SharedPreferences.Editor editorFourPlaceName = fourPlaceName.edit();
        SharedPreferences.Editor editorFivePlaceName = fivePlaceName.edit();
        SharedPreferences.Editor editorSixPlaceName = sixPlaceName.edit();
        SharedPreferences.Editor editorSevenPlaceName = sevenPlaceName.edit();
        SharedPreferences.Editor editorEightPlaceName = eightPlaceName.edit();
        SharedPreferences.Editor editorNinePlaceName = ninePlaceName.edit();
        SharedPreferences.Editor editorTenPlaceName = tenPlaceName.edit();

        editorFirstPlaceName.putString("FIRST_PLACE_N", top10Players.get(0).get_Name());
        editorFirstPlaceName.commit();
        editorSecondPlaceName.putString("SEC_PLACE_N", top10Players.get(1).get_Name());
        editorSecondPlaceName.commit();
        editorThirdPlaceName.putString("THIRD_PLACE_N", top10Players.get(2).get_Name());
        editorThirdPlaceName.commit();
        editorFourPlaceName.putString("FOUR_PLACE_N",top10Players.get(3).get_Name());
        editorFourPlaceName.commit();
        editorFivePlaceName.putString("FIVE_PLACE_N", top10Players.get(4).get_Name());
        editorFivePlaceName.commit();
        editorSixPlaceName.putString("SIX_PLACE_N", top10Players.get(5).get_Name());
        editorSixPlaceName.commit();
        editorSevenPlaceName.putString("SEVEN_PLACE_N",top10Players.get(6).get_Name());
        editorSevenPlaceName.commit();
        editorEightPlaceName.putString("EIGHT_PLACE_N", top10Players.get(7).get_Name());
        editorEightPlaceName.commit();
        editorNinePlaceName.putString("NINE_PLACE_N", top10Players.get(8).get_Name());
        editorNinePlaceName.commit();
        editorTenPlaceName.putString("TEN_PLACE_N", top10Players.get(9).get_Name());
        editorTenPlaceName.commit();


    }

    private void initializeList() {
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

    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
