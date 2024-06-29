package com.smartmeter.database;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBHelper extends Configs {

    private Connection dbConnection;

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

    public ArrayList<String> getAllCompaniesList() {
        ArrayList<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT DISTINCT " + Const.KEY_COMPANY + " FROM " + Const.TABLE_COUNTERSINFO +
                    " ORDER BY " + Const.KEY_COMPANY + " ASC;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                resultSet = prSt.executeQuery();

                while (resultSet.next()) {
                    result.add(resultSet.getString(Const.KEY_COMPANY));
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }

        });

        return result;
    }

    // Компанії, лічильники яких не були списані у вибраний місяць (були списані до цього місяця або не списані взагалі ніколи)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getNoneZeroCompaniesList(LocalDate day){
        ArrayList<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT DISTINCT " + Const.KEY_COMPANY +
                    " FROM " + Const.TABLE_COUNTERSINFO + " LEFT JOIN " + Const.TABLE_VALUES +
                    " ON " + Const.TABLE_COUNTERSINFO + "." + Const.KEY_ID + " = " + Const.KEY_COUNTER_ID +
                    " WHERE " + Const.TABLE_COUNTERSINFO + "." + Const.KEY_ID + " NOT IN (SELECT " + Const.KEY_COUNTER_ID +
                    " FROM " + Const.TABLE_VALUES +
                    " WHERE " + Const.KEY_DATE + " LIKE '%." + String.format("%02d", day.getMonthValue()) + "." + day.getYear() + "') ORDER BY " + Const.KEY_COMPANY + " ASC;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                resultSet = prSt.executeQuery();

                while (resultSet.next()) {
                    result.add(resultSet.getString(Const.KEY_COMPANY));
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }

        });

        return result;
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