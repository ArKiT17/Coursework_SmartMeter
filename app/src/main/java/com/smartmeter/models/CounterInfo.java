package com.smartmeter.models;

public class CounterInfo implements Comparable<CounterInfo> {
    public int id;
    public String company;
    public String room;
    public int floor;
    public int multiplier;
    public String counter;
    public int previousValue;
    public int currentValue;
    public String lastDate;
    public int difference;

    public CounterInfo(int id, String company, String room, int floor, int multiplier, String counter, int previousValue, int currentValue, String lastDate, int difference) {
        this.id = id;
        this.company = company;
        this.room = room;
        this.floor = floor;
        this.multiplier = multiplier;
        this.counter = counter;
        this.previousValue = previousValue;
        this.currentValue = currentValue;
        this.lastDate = lastDate;
        this.difference = difference;
    }

    public CounterInfo() {
        id = -1;
        company = null;
        room = null;
        floor = -1;
        multiplier = -1;
        counter = null;
        previousValue = -1;
        currentValue = -1;
        lastDate = null;
        difference = -1;
    }

    @Override
    public int compareTo(CounterInfo o) {
        return this.company.compareTo(o.company);
    }
}
