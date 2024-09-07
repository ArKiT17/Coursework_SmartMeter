package com.smartmeter.database;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.smartmeter.CounterInfo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
                Log.e("Error", Log.getStackTraceString(e));
            }
    }

    public boolean hasDbConnection() {
        try {
            setDbConnection();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ArrayList<String> getAllCompaniesList() {
        ArrayList<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT DISTINCT " + Const.KEY_COMPANY + " FROM " + Const.TABLE_COUNTERINFO +
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
    public ArrayList<String> getNoneZeroCompaniesList(LocalDate day) {
        ArrayList<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT DISTINCT " + Const.KEY_COMPANY +
                    " FROM " + Const.TABLE_COUNTERINFO + " LEFT JOIN " + Const.TABLE_VALUE +
                    " ON " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " = " + Const.KEY_COUNTER_ID +
                    " WHERE " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " NOT IN (SELECT " + Const.KEY_COUNTER_ID +
                    " FROM " + Const.TABLE_VALUE +
                    " WHERE " + Const.KEY_DATE + " LIKE '" + day.getYear() + "-" + String.format("%02d", day.getMonthValue()) + "-%') ORDER BY " + Const.KEY_COMPANY + " ASC;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                resultSet = prSt.executeQuery();

                while (resultSet.next()) {
                    result.add(resultSet.getString(Const.KEY_COMPANY));
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return result;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Лічильники, які не були списані у вибраний місяць (були списані до цього місяця або не списані взагалі ніколи)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getNoneZeroCountersList(LocalDate day, String company) {
        ArrayList<String> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<ArrayList<String>> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT DISTINCT " + Const.KEY_COUNTER +
                    " FROM " + Const.TABLE_COUNTERINFO + " LEFT JOIN " + Const.TABLE_VALUE +
                    " ON " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " = " + Const.KEY_COUNTER_ID +
                    " WHERE " + Const.KEY_COMPANY + " = ? AND " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " NOT IN (SELECT " + Const.KEY_COUNTER_ID +
                    " FROM " + Const.TABLE_VALUE +
                    " WHERE " + Const.KEY_DATE + " LIKE '%." + String.format("%02d", day.getMonthValue()) + "." + day.getYear() + "')";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setString(1, company);
                resultSet = prSt.executeQuery();

                while (resultSet.next()) {
                    result.add(resultSet.getString(Const.KEY_COUNTER));
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return result;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CounterInfo getCounterInfo(String company, String counter) {
        CounterInfo result = new CounterInfo();
        if (company.isEmpty() || counter.isEmpty())
            return null;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<CounterInfo> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + ", " + Const.KEY_MULTIPLIER + ", " + Const.KEY_FLOOR + ", COALESCE(" + Const.KEY_PREVIOUS_VALUE + ", 0) AS " + Const.KEY_PREVIOUS_VALUE +
                    " FROM " + Const.TABLE_COUNTERINFO + " LEFT JOIN " + Const.TABLE_VALUE + " ON " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " = " + Const.TABLE_VALUE + "." + Const.KEY_COUNTER_ID +
                    " WHERE " + Const.KEY_COMPANY + " = ? AND " + Const.KEY_COUNTER + " = ?;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setString(1, company);
                prSt.setString(2, counter);
                resultSet = prSt.executeQuery();

                if (resultSet.next()) {
                    result.id = resultSet.getInt(Const.KEY_ID);
                    result.floor = resultSet.getInt(Const.KEY_FLOOR);
                    result.multiplier = resultSet.getInt(Const.KEY_MULTIPLIER);
                    result.previousValue = resultSet.getInt(Const.KEY_PREVIOUS_VALUE);
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return result;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CounterInfo getCounterInfo(int counterId) {
        CounterInfo scanCounter = new CounterInfo();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<CounterInfo> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT " + Const.KEY_COMPANY + ", " + Const.KEY_COUNTER + ", " + Const.KEY_MULTIPLIER + ", " + Const.KEY_FLOOR + ", COALESCE(" + Const.KEY_PREVIOUS_VALUE + ", 0) AS " + Const.KEY_PREVIOUS_VALUE +
                    " FROM " + Const.TABLE_COUNTERINFO + " LEFT JOIN " + Const.TABLE_VALUE + " ON " + Const.TABLE_COUNTERINFO + "." + Const.KEY_ID + " = " + Const.TABLE_VALUE + "." + Const.KEY_COUNTER_ID +
                    " WHERE " + Const.KEY_COUNTER_ID + " = ?;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setInt(1, counterId);
                resultSet = prSt.executeQuery();

                if (resultSet.next()) {
                    scanCounter.company = resultSet.getString(Const.KEY_COMPANY);
                    scanCounter.counter = resultSet.getString(Const.KEY_COUNTER);
                    scanCounter.multiplier = resultSet.getInt(Const.KEY_MULTIPLIER);
                    scanCounter.floor = resultSet.getInt(Const.KEY_FLOOR);
                    scanCounter.previousValue = resultSet.getInt(Const.KEY_PREVIOUS_VALUE);
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return scanCounter;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CounterInfo> getAllCountersList() {
        List<CounterInfo> result = new ArrayList<>();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<CounterInfo>> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT " + Const.KEY_ID + ", " + Const.KEY_COMPANY + ", " + Const.KEY_ROOM + ", " + Const.KEY_FLOOR + ", " + Const.KEY_MULTIPLIER + ", " + Const.KEY_COUNTER +
                    " FROM " + Const.TABLE_COUNTERINFO + ";";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                resultSet = prSt.executeQuery();

                while (resultSet.next()) {
                    result.add(new CounterInfo(
                            resultSet.getInt(Const.KEY_ID),
                            resultSet.getString(Const.KEY_COMPANY),
                            resultSet.getString(Const.KEY_ROOM),
                            resultSet.getInt(Const.KEY_FLOOR),
                            resultSet.getInt(Const.KEY_MULTIPLIER),
                            resultSet.getString(Const.KEY_COUNTER),
                            -1, -1
                    ));
                }
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return result;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean counterExists(int counterId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT * FROM " + Const.TABLE_COUNTERINFO +
                    " WHERE " + Const.KEY_ID + " = ?;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setInt(1, counterId);
                resultSet = prSt.executeQuery();

                return resultSet.next();
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return false;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean counterHasWrittenDown(int counterId, LocalDate date) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(() -> {
            setDbConnection();

            ResultSet resultSet = null;
            String query = "SELECT * FROM " + Const.TABLE_VALUE +
                    " WHERE " + Const.KEY_COUNTER_ID + " = ? AND " + Const.KEY_DATE + " = ?;";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setInt(1, counterId);
                prSt.setDate(2, Date.valueOf(String.valueOf(date)));
                resultSet = prSt.executeQuery();

                return resultSet.next();
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            return false;
        });

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNewCounter(String company, String room, int floor, int multiplier, String counter) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            setDbConnection();

            String query = "INSERT INTO " + Const.TABLE_COUNTERINFO + "(" +
                    Const.KEY_COMPANY + ", " +
                    Const.KEY_ROOM + ", " +
                    Const.KEY_FLOOR + ", " +
                    Const.KEY_MULTIPLIER + ", " +
                    Const.KEY_COUNTER + ") VALUES (?, ?, ?, ?, ?);";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setString(1, company);
                prSt.setString(2, room);
                prSt.setInt(3, floor);
                prSt.setInt(4, multiplier);
                prSt.setString(5, counter);
                prSt.executeUpdate();
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addValue(int counterId, LocalDate date, int curValue, int prevValue, int difference) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            setDbConnection();

            String query = "INSERT INTO " + Const.TABLE_VALUE + "(" +
                    Const.KEY_COUNTER_ID + ", " +
                    Const.KEY_DATE + ", " +
                    Const.KEY_CURRENT_VALUE + ", " +
                    Const.KEY_PREVIOUS_VALUE + ", " +
                    Const.KEY_DIFFERENCE + ") VALUES (?, ?, ?, ?, ?);";
            try {
                PreparedStatement prSt = dbConnection.prepareStatement(query);
                prSt.setInt(1, counterId);
                prSt.setDate(2, Date.valueOf(String.valueOf(date)));
                prSt.setInt(3, curValue);
                prSt.setInt(4, prevValue);
                prSt.setInt(5, difference);
                prSt.executeUpdate();
            } catch (SQLException e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
        });
    }
}