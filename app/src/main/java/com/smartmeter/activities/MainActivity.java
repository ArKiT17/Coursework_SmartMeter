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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.smartmeter.Buffer;
import com.smartmeter.CaptureAct;
import com.smartmeter.R;
import com.smartmeter.models.CounterInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
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

        dateList.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_style_dark, R.id.spinner_text_dark, Buffer.months));

        today = LocalDate.now();
        dateList.setSelection(today.getMonthValue() - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        if (!Buffer.dbHelper.hasDbConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert_error_database));

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                    System.exit(1);
                }
            }).setOnCancelListener(dialog -> {
                finishAffinity();
                System.exit(1);
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        dateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if ((dateList.getSelectedItemPosition() + 1 == 12) && (today.getMonthValue() == 1))
                    selectedYear = today.getYear() - 1;
                else
                    selectedYear = today.getYear();

                YearMonth selectedYearMonth = YearMonth.of(selectedYear, (dateList.getSelectedItemPosition() + 1));
                toDate = selectedYearMonth.atEndOfMonth();

                numberCurrentValue.setText("");
                numberPreviousValue.setText("");
                numberResultValue.setText("0");
                multiplier.setText("1");
                multiplyResult.setText("0");
                floorValue.setText("");
                setSpaceAdapter(companies, Buffer.dbHelper.getNoneZeroCompaniesList(toDate));
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
                if (Buffer.scannerInfo != null) {
                    Buffer.scannerInfo = null;
                    return;
                }

                setSpaceAdapter(counters, Buffer.dbHelper.getNoneZeroCountersList(toDate, adapterView.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        counters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CounterInfo counterInfo = Buffer.dbHelper.getCounterInfo(companies.getSelectedItem().toString(), adapterView.getItemAtPosition(position).toString());
                numberCurrentValue.setEnabled(!counters.getSelectedItem().toString().isEmpty());
                numberPreviousValue.setEnabled(!counters.getSelectedItem().toString().isEmpty());
                if (counterInfo != null) {
                    id = counterInfo.id;
                    multiplier.setText(String.valueOf(counterInfo.multiplier));
                    floorValue.setText(String.valueOf(counterInfo.floor));
                    numberPreviousValue.setText(String.valueOf(counterInfo.currentValue));
                } else {
                    id = -1;
                    multiplier.setText("1");
                    floorValue.setText("");
                    numberPreviousValue.setText("");
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

    public void ScannerButton(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.scan_prompt));
        options.setBeepEnabled(false);
        options.setTorchEnabled(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_title_error));
        builder.setPositiveButton(getString(R.string.alert_CLOSE), null);

        int counterId;
        try {
            counterId = Integer.parseInt(result.getContents());
        } catch (Exception e) {
            builder.setMessage(getString(R.string.scan_error_line));
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        if (!Buffer.dbHelper.counterExists(counterId)) {
            builder.setMessage(getString(R.string.scan_error_line));
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        if (Buffer.dbHelper.counterHasWrittenDown(counterId, toDate)) {
            builder.setMessage(getString(R.string.scan_this_counter_has_already_been_written_down));
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        Buffer.scannerInfo = Buffer.dbHelper.getCounterInfo(counterId);

        ArrayList<String> tmpCompanies = Buffer.dbHelper.getNoneZeroCompaniesList(toDate);
        setSpaceAdapter(companies, tmpCompanies);
        companies.setSelection(tmpCompanies.indexOf(Buffer.scannerInfo.company));

        ArrayList<String> tmpCounters = Buffer.dbHelper.getNoneZeroCountersList(toDate, Buffer.scannerInfo.company);
        setSpaceAdapter(counters, tmpCounters);
        counters.setSelection(tmpCounters.indexOf(Buffer.scannerInfo.counter));
    });

    private void setSpaceAdapter(Spinner spinner, List<String> items) {
        items.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_style_light, R.id.spinner_text_light, items);
        spinner.setAdapter(adapter);
    }
}