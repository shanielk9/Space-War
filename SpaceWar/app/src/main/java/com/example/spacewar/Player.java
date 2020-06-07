package com.example.spacewar;

public class Player {
    private int m_Score;
    private String m_Name;
    private int m_MedalResID;

    public Player(int score, String name, int medal) {
        this.m_Score = score;
        this.m_Name = name;
        this.m_MedalResID = medal;
    }

    public int get_Score() {
        return m_Score;
    }

    public String get_Name() {
        return m_Name;
    }

    public int get_MedalResId() {
        return m_MedalResID;
    }

    public void set_MedalResID(int medal) {
        this.m_MedalResID = medal;
    }

    public void set_Score(int score) {
        this.m_Score = score;
    }

    public void set_Name(String name) {
        this.m_Name = name;
    }
}
