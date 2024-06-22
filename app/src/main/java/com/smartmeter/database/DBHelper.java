package com.smartmeter.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class DBHelper extends Configs {

    Connection dbConnection;

    public DBHelper() {
        dbConnection = null;
        setDbConnection();
    }

    private void setDbConnection() {
        if (dbConnection == null)
            try {
                Class.forName("com.mysql.jdbc.Driver");
                String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
                dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
    }

//    public void test() {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                dbConnection = getDbConnection();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//
//            if (dbConnection == null) {
//                str = "error null";
//            } else {
//                str = "good, working";
//            }
//
////            Toast.makeText(this, "asd", Toast.LENGTH_LONG).show();
//        });
////        String query = "INSERT INTO " + Const.TABLE_COUNTERSINFO + "(" +
////                Const.KEY_COMPANY + ", " +
////                Const.KEY_ROOM + ", " +
////                Const.KEY_FLOOR + ", " +
////                Const.KEY_MULTIPLIER + ", " +
////                Const.KEY_COUNTER +
////                ") VALUES " +
////                "(?, ?, ?, ?, ?); ";
////        try {
////            ExecutorService executorService = Executors.newSingleThreadExecutor();
////            executorService.execute(() -> {
////                try {
////                    dbConnection = getDbConnection();
////                } catch (Exception e) {
////                    System.out.println(e.getMessage());
////                }
////            });
////
////            PreparedStatement prSt = dbConnection.prepareStatement(query);
////            prSt.setString(1, "abc");
////            prSt.setString(2, "2.14");
////            prSt.setInt(3, 2);
////            prSt.setInt(4, 1);
////            prSt.setString(5, "tyt");
////
////            prSt.executeUpdate();
////            prSt.close();
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
//    }
}