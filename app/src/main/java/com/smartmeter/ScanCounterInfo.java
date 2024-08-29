package com.smartmeter;

public class ScanCounterInfo {
    public String company;
    public String counter;
    public int multiplier;
    public int floor;
    public int previousValue;
    public int currentValue;

    public ScanCounterInfo(String company, String counter, int multiplier, int floor, int previousValue, int currentValue) {
        this.company = company;
        this.counter = counter;
        this.multiplier = multiplier;
        this.floor = floor;
        this.previousValue = previousValue;
        this.currentValue = currentValue;
    }

    public ScanCounterInfo() {
        company = null;
        counter = null;
        multiplier = -1;
        floor = -1;
        previousValue = -1;
        currentValue = -1;
    }
}
