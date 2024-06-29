package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private int id;
    private String toDate;
    private LocalDate today;
    private LocalDate selectedDay;
    private String selectedMonthZero;
    private int selectedYear;

    private Spinner companies;
    private Spinner counters;
    private EditText numberCurrentValue;
    private EditText numberPreviousValue;
    private EditText numberResultValue;
    private TextView multiplier;
    private TextView multiplyResult;
    private TextView floorValue;
    private Button saveButton;
    private Spinner dateList;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        id = -1;
        Buffer.scannerInfo = null;

        companies = findViewById(R.id.companies);
        counters = findViewById(R.id.counters);
        numberCurrentValue = findViewById(R.id.numberCurrentValue);
        numberPreviousValue = findViewById(R.id.numberPreviousValue);
        numberResultValue = findViewById(R.id.numberResultValue);
        multiplier = findViewById(R.id.multiplier);
        multiplyResult = findViewById(R.id.multiplyResult);
        floorValue = findViewById(R.id.floorValue);
        saveButton = findViewById(R.id.saveButton);
        dateList = findViewById(R.id.dateList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        Buffer.months.clear();
        Buffer.months.addAll(Arrays.asList(
                getString(R.string.january),
                getString(R.string.february),
                getString(R.string.march),
                getString(R.string.april),
                getString(R.string.may),
                getString(R.string.june),
                getString(R.string.july),
                getString(R.string.august),
                getString(R.string.september),
                getString(R.string.october),
                getString(R.string.november),
                getString(R.string.december)
        ));

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_style_dark, R.id.spinner_text_dark, Buffer.months);
        dateList.setAdapter(adapter);

        today = LocalDate.now();
        dateList.setSelection(today.getMonthValue() - 1);

        if (Buffer.companyList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert_error_database));

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    System.exit(1);
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finishAffinity();
                    System.exit(1);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        dateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if ((Buffer.months.indexOf(dateList.getSelectedItem()) + 1 == 12) && (today.getMonthValue() == 1))
                    selectedYear = today.getYear() - 1;
                else
                    selectedYear = today.getYear();

                YearMonth selectedYearMonth = YearMonth.of(selectedYear, (Buffer.months.indexOf(dateList.getSelectedItem()) + 1));
                selectedDay = selectedYearMonth.atEndOfMonth();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                toDate = selectedDay.format(formatter);

                numberCurrentValue.setText("");
                numberPreviousValue.setText("");
                numberResultValue.setText("0");
                multiplier.setText("1");
                multiplyResult.setText("0");
                floorValue.setText("");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_style_light, R.id.spinner_text_light, Buffer.dbHelper.getNoneZeroCompaniesList(selectedDay));
                companies.setAdapter(adapter);
                companies.setSelection(0);
                counters.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}