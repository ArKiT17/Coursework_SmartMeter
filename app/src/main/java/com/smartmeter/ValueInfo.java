package com.smartmeter;

public class ValueInfo {
    public int id;
    public String putDate;
    public int currentValue;
    public int previousValue;
    public int difference;

    public ValueInfo(int id, String putDate, int currentValue, int previousValue, int difference) {
        this.id = id;
        this.putDate = putDate;
        this.currentValue = currentValue;
        this.previousValue = previousValue;
        this.difference = difference;
    }
}