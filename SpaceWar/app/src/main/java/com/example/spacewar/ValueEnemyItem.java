package com.example.spacewar;

public class ValueEnemyItem extends GameItem{
    private int value;

    public ValueEnemyItem(int value, int speed) {
        super(speed);
        setValue(value);
    }

    public int getValue() {
        setValue(value);
        return value;
    }

    public void setValue(int value) {
        this.value = value+(int)Math.floor((1000-super.getSpeed())/100); //bonus for killing fast enemies or collect fast gift
    }
}
