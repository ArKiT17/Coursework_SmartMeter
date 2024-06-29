package com.smartmeter;

public class CounterInfo {
    public int id;
    public String company;
    public String room;
    public int floor;
    public int multiplier;
    public String counter;

    CounterInfo(int id, String company, String room, int floor, int multiplier, String counter) {
        this.id = id;
        this.company = company;
        this.room = room;
        this.floor = floor;
        this.multiplier = multiplier;
        this.counter = counter;
    }
}
