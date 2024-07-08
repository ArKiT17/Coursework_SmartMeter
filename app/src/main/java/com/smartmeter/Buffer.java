package com.smartmeter;

import com.smartmeter.database.DBHelper;

import java.util.ArrayList;

public class Buffer {
    public static DBHelper dbHelper = new DBHelper();
    public static CounterInfo scannerInfo = null;
    public static ArrayList<String> months = new ArrayList<>();
    public static ArrayList<String> companyList = new ArrayList<>();

}
