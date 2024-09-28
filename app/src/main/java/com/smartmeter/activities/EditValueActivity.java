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
import android.widget.ImageButton;
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
import com.smartmeter.models.CounterInfo;
import com.smartmeter.R;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EditValueActivity extends AppCompatActivity {
    private int idValue;

    private Spinner eCompanies;
    private Spinner eCounters;
    private EditText eNumberCurrentValue;
    private EditText eNumberPreviousValue;
    private EditText eNumberResultValue;
    private TextView eMultiplier;
    private TextView eMultiplyResult;
    private TextView eFloorValue;
    private TextView eDate;
    private Button eSaveButton;
    private ImageButton eDeleteButton;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_value);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idValue = -1;
        Buffer.scannerInfo = null;

        eCompanies = findViewById(R.id.eCompanies);
        eCounters = findViewById(R.id.eCounters);
        eNumberCurrentValue = findViewById(R.id.eNumberCurrentValue);
        eNumberPreviousValue = findViewById(R.id.eNumberPreviousValue);
        eNumberResultValue = findViewById(R.id.eNumberResultValue);
        eMultiplier = findViewById(R.id.eMultiplier);
        eMultiplyResult = findViewById(R.id.eMultiplyResult);
        eFloorValue = findViewById(R.id.eFloorValue);
        eDate = findViewById(R.id.eDate);
        eSaveButton = findViewById(R.id.eSaveButton);
        eDeleteButton = findViewById(R.id.eDeleteButton);
    }

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
        } else {
            if (Buffer.scannerInfo == null)
                setSpaceAdapter(eCompanies, Buffer.dbHelper.getAllCompaniesList());
        }

        eCompanies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (Buffer.scannerInfo != null) {
                    Buffer.scannerInfo = null;
                    return;
                }
                setSpaceAdapter(eCounters, Buffer.dbHelper.getAllCountersList(adapterView.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        eCounters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                eDeleteButton.setEnabled(!eCounters.getSelectedItem().toString().isEmpty());
                eNumberCurrentValue.setEnabled(!eCounters.getSelectedItem().toString().isEmpty());
                eNumberPreviousValue.setEnabled(!eCounters.getSelectedItem().toString().isEmpty());
                if (eCounters.getSelectedItem().toString().isEmpty()) {
                    idValue = -1;
                    eNumberCurrentValue.setText("");
                    eNumberPreviousValue.setText("");
                    eMultiplier.setText("1");
                    eFloorValue.setText("");
                    eDate.setText("");
                    return;
                }
                CounterInfo selectedCounter;
                selectedCounter = Buffer.dbHelper.getLastValueInfo(eCompanies.getSelectedItem().toString(), adapterView.getItemAtPosition(position).toString());
                if (selectedCounter.id == -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditValueActivity.this);
                    builder.setTitle(R.string.alert_values_not_found)
                            .setMessage(R.string.alert_values_not_found_content)
                            .setNeutralButton(R.string.alert_OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int identifier) {
                                    idValue = -1;
                                    Buffer.scannerInfo = null;
                                    eCompanies.setSelection(0);
                                }
                            })
                            .setOnCancelListener(dialog -> {
                                idValue = -1;
                                Buffer.scannerInfo = null;
                                eCompanies.setSelection(0);
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    idValue = selectedCounter.id;
                    eNumberCurrentValue.setText(String.valueOf(selectedCounter.currentValue));
                    eNumberPreviousValue.setText(String.valueOf(selectedCounter.previousValue));
                    eMultiplier.setText(String.valueOf(selectedCounter.multiplier));
                    eFloorValue.setText(String.valueOf(selectedCounter.floor));
                    eDate.setText(selectedCounter.lastDate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        eNumberPreviousValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (eNumberCurrentValue.getText().toString().isEmpty() || eNumberPreviousValue.getText().toString().isEmpty())
                    eNumberResultValue.setText("0");
                else
                    eNumberResultValue.setText(String.valueOf(Integer.parseInt(eNumberCurrentValue.getText().toString()) - Integer.parseInt(eNumberPreviousValue.getText().toString())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        eNumberCurrentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (eNumberCurrentValue.getText().toString().isEmpty() || eNumberPreviousValue.getText().toString().isEmpty())
                    eNumberResultValue.setText("0");
                else
                    eNumberResultValue.setText(String.valueOf(Integer.parseInt(eNumberCurrentValue.getText().toString()) - Integer.parseInt(eNumberPreviousValue.getText().toString())));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        eNumberResultValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                eMultiplyResult.setText(String.valueOf(Integer.parseInt(eNumberResultValue.getText().toString()) * Integer.parseInt(eMultiplier.getText().toString())));
            }
        });

        eMultiplyResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                eSaveButton.setEnabled(
                        Integer.parseInt(eMultiplyResult.getText().toString()) >= 0 &&
                                !eNumberCurrentValue.getText().toString().isEmpty() &&
                                !eNumberPreviousValue.getText().toString().isEmpty()
                );
            }
        });
    }

    private void setSpaceAdapter(Spinner spinner, List<String> items) {
        items.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditValueActivity.this, R.layout.spinner_style_light, R.id.spinner_text_light, items);
        spinner.setAdapter(adapter);
    }

    public void eUpdateValuesInDatabase(View view) {
        try {
            boolean updated = Buffer.dbHelper.updateValue(idValue,
                    Integer.parseInt(eNumberCurrentValue.getText().toString()),
                    Integer.parseInt(eNumberPreviousValue.getText().toString()),
                    Integer.parseInt(eMultiplyResult.getText().toString()));
            Toast.makeText(EditValueActivity.this, updated ? R.string.toast_success : R.string.toast_error, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException exception) {
            Toast.makeText(this, R.string.toast_error, Toast.LENGTH_LONG).show();
        }
        Buffer.scannerInfo = null;
        eCompanies.setSelection(0);
    }

    public void eDeleteValueButton(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_are_you_sure)
                    .setTitle(R.string.alert_title_warning)
                    .setPositiveButton(R.string.alert_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            boolean deleted = Buffer.dbHelper.deleteValue(idValue);
                            idValue = -1;
                            Toast.makeText(EditValueActivity.this, deleted ? R.string.toast_success : R.string.toast_error, Toast.LENGTH_SHORT).show();
                            eCompanies.setSelection(0);
                        }
                    })
                    .setNegativeButton(R.string.alert_NO, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(EditValueActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void eScannerButton(View view) {
        ScanOptions options = new ScanOptions();
        options.setPrompt(getString(R.string.scan_prompt));
        options.setBeepEnabled(false);
        options.setTorchEnabled(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_title_error))
                .setNeutralButton(R.string.alert_OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int identifier) {
                        idValue = -1;
                        Buffer.scannerInfo = null;
                        eCompanies.setSelection(0);
                    }
                })
                .setOnCancelListener(dialog -> {
                    idValue = -1;
                    Buffer.scannerInfo = null;
                    eCompanies.setSelection(0);
                });

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

        Buffer.scannerInfo = Buffer.dbHelper.getLastValueInfo(counterId);
        if (Buffer.scannerInfo.id == -1) {
            builder.setTitle(R.string.alert_values_not_found)
                    .setMessage(R.string.alert_values_not_found_content);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        ArrayList<String> tmpCompanies = Buffer.dbHelper.getAllCompaniesList();
        setSpaceAdapter(eCompanies, tmpCompanies);
        eCompanies.setSelection(tmpCompanies.indexOf(Buffer.scannerInfo.company));

        ArrayList<String> tmpCounters = Buffer.dbHelper.getAllCountersList(Buffer.scannerInfo.company);
        setSpaceAdapter(eCounters, tmpCounters);
        eCounters.setSelection(tmpCounters.indexOf(Buffer.scannerInfo.counter));
    });
}