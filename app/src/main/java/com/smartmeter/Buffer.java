package com.smartmeter;

import com.smartmeter.database.DBHelper;
import com.smartmeter.models.CounterInfo;

import java.util.ArrayList;

public class Buffer {
    public static DBHelper dbHelper = new DBHelper();
    public static CounterInfo scannerInfo = null;
    public static CounterInfo activeCounter;
    public static ArrayList<String> months = new ArrayList<>();
}
