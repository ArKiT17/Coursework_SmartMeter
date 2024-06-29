package com.smartmeter;

import com.smartmeter.database.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class Buffer {
    public static DBHelper dbHelper = new DBHelper();
    public static CounterInfo scannerInfo;
    public static ArrayList<String> months = new ArrayList<>();
    public static List<String> companyList = new ArrayList<>();

}
