package com.example.spacewar;

public class ValueEnemyItem extends GameItem{
    private int value;

    public ValueEnemyItem(int value, int speed) {
        super(speed);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
