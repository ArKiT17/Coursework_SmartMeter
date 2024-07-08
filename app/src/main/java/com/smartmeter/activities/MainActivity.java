package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;
import com.smartmeter.database.Const;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private int id;
    private LocalDate toDate;
    private LocalDate today;
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
                toDate = selectedYearMonth.atEndOfMonth();

                numberCurrentValue.setText("");
                numberPreviousValue.setText("");
                numberResultValue.setText("0");
                multiplier.setText("1");
                multiplyResult.setText("0");
                floorValue.setText("");
                ArrayList<String> noneZeroCompanies = Buffer.dbHelper.getNoneZeroCompaniesList(toDate);
                noneZeroCompanies.add(0, "");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_style_light, R.id.spinner_text_light, noneZeroCompanies);
                companies.setAdapter(adapter);
                companies.setSelection(0);
                counters.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        companies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (Buffer.scannerInfo != null)
                    return;

                List<String> noneZeroCounters = Buffer.dbHelper.getNoneZeroCountersList(toDate, adapterView.getItemAtPosition(position).toString());
                noneZeroCounters.add(0, "");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_style_light, R.id.spinner_text_light, noneZeroCounters);
                counters.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        counters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                HashMap<String, Integer> counterInfo = Buffer.dbHelper.getCounterInfo(companies.getSelectedItem().toString(), adapterView.getItemAtPosition(position).toString());
                if (!counterInfo.isEmpty()) {
                    id = counterInfo.get(Const.KEY_ID);
                    multiplier.setText(String.valueOf(counterInfo.get(Const.KEY_MULTIPLIER)));
                    floorValue.setText(String.valueOf(counterInfo.get(Const.KEY_FLOOR)));
                    numberPreviousValue.setText(String.valueOf(counterInfo.get(Const.KEY_PREVIOUS_VALUE)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        numberPreviousValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (numberCurrentValue.getText().toString().isEmpty() || numberPreviousValue.getText().toString().isEmpty())
                    multiplyResult.setText("0");
                else
                    numberResultValue.setText(String.valueOf(Integer.parseInt(numberCurrentValue.getText().toString()) - Integer.parseInt(numberPreviousValue.getText().toString())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        numberCurrentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (numberCurrentValue.getText().toString().isEmpty() || numberPreviousValue.getText().toString().isEmpty())
                    numberResultValue.setText("0");
                else
                    numberResultValue.setText(String.valueOf(Integer.parseInt(numberCurrentValue.getText().toString()) - Integer.parseInt(numberPreviousValue.getText().toString())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        numberResultValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (numberResultValue.getText().toString().isEmpty() || multiplier.getText().toString().isEmpty())
                    multiplyResult.setText("0");
                else
                    multiplyResult.setText(String.valueOf(Integer.parseInt(numberResultValue.getText().toString()) * Integer.parseInt(multiplier.getText().toString())));
            }
        });

        multiplyResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButton.setEnabled(Integer.parseInt(multiplyResult.getText().toString()) >= 0 &&
                        companies.getSelectedItemPosition() != 0 &&
                        counters.getSelectedItemPosition() != 0 &&
                        !numberPreviousValue.getText().toString().isEmpty() &&
                        !numberCurrentValue.getText().toString().isEmpty() &&
                        !numberResultValue.getText().toString().isEmpty());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addValuesToDatabase(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            builder.setMessage(getString(R.string.title_date) + ": " + toDate.format(formatter) +
                            "\n" + getString(R.string.title_company) + ": " + companies.getSelectedItem().toString() +
                            "\n" + getString(R.string.title_counter_name) + ": " + counters.getSelectedItem().toString() +
                            "\n" + getString(R.string.title_previousValue) + ": " + numberPreviousValue.getText().toString() +
                            "\n" + getString(R.string.title_current) + ": " + numberCurrentValue.getText().toString() +
                            "\n" + getString(R.string.title_difference) + ": " + multiplyResult.getText().toString())
                    .setTitle(R.string.alert_title_is_it_correct)
                    .setPositiveButton(R.string.alert_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int identifier) {
                            Buffer.dbHelper.addValue(id, toDate,
                                    Integer.parseInt(numberCurrentValue.getText().toString()),
                                    Integer.parseInt(numberPreviousValue.getText().toString()),
                                    Integer.parseInt(multiplyResult.getText().toString()));
                            id = -1;
                            Buffer.scannerInfo = null;
                            companies.setSelection(0);
                            counters.setSelection(0);
                            numberCurrentValue.setText("");
                            numberPreviousValue.setText("");
                            numberResultValue.setText("0");
                            multiplier.setText("1");
                            multiplyResult.setText("0");
                        }
                    })
                    .setNegativeButton(R.string.alert_NO, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}