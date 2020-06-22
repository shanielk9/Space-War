package com.example.spacewar;

public class Player extends GameItem {
    private int score;

    public Player() {
        score = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
