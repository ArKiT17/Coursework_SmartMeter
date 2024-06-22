package com.smartmeter.database;

public class Const {
    public static final String KEY_ID = "id";


    // Table with info about counters - countersInfo
    public static final String TABLE_COUNTERSINFO = "countersInfo";
    public static final String KEY_COMPANY = "company";
    public static final String KEY_ROOM = "room";
    public static final String KEY_FLOOR = "floor";
    public static final String KEY_MULTIPLIER = "multiplier";
    public static final String KEY_COUNTER = "counter";


    // Main table - counterValues
    public static final String TABLE_VALUES = "counterValues";
    public static final String KEY_COUNTER_ID = "counter_id";
    public static final String KEY_DATE = "put_date";
    public static final String KEY_PREVIOUS_VALUE = "previous_value";
    public static final String KEY_CURRENT_VALUE = "current_value";
    public static final String KEY_DIFFERENCE = "difference";


    // Buffer table - bufferTable
    public static final String TABLE_BUFFER = "bufferTable";
    public static final String KEY_DEFAULT_MAIL = "defaultMail";
    public static final String KEY_ARCHIVED_YEAR = "archivedYear";
}
