package com.smartmeter.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.smartmeter.Buffer;
import com.smartmeter.models.CounterInfo;
import com.smartmeter.ExcelGenerator;
import com.smartmeter.R;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExportMailActivity extends AppCompatActivity {
    private EditText mail;
    private Spinner month;
    private Spinner year;
    private CheckBox setAsDefault;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_mail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mail = findViewById(R.id.enterMail);
        month = findViewById(R.id.monthsList);
        year = findViewById(R.id.yearsList);
        setAsDefault = findViewById(R.id.checkboxSetAsDefault);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalDate today = LocalDate.now();
        mail.setText(Buffer.dbHelper.getDefaultMail());

        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(this, R.layout.spinner_style_light, R.id.spinner_text_light, Buffer.months);
        month.setAdapter(stringAdapter);
        month.setSelection(today.getMonthValue() - 1);

        int lastYear = Buffer.dbHelper.getFirstYear();
        String[] years = new String[today.getYear() - lastYear + 1];
        for (int i = 0; i < today.getYear() - lastYear + 1; i++) {
            years[i] = String.valueOf(today.getYear() - i);
        }
        stringAdapter = new ArrayAdapter<String>(this, R.layout.spinner_style_light, R.id.spinner_text_light, years);
        year.setAdapter(stringAdapter);
    }

    public void sendClick(View view) {
        if (setAsDefault.isChecked()) {
            setAsDefault.setChecked(false);
            Buffer.dbHelper.setDefaultMail(mail.getText().toString());
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        String normalMonth = String.format("%02d", Buffer.months.indexOf(month.getSelectedItem().toString()) + 1);

        ArrayList<CounterInfo> exportInfo = Buffer.dbHelper.getExportInfo(normalMonth, year.getSelectedItem().toString());

        ExcelGenerator excel = new ExcelGenerator(this,month.getSelectedItem().toString() + " " + year.getSelectedItem().toString());
        excel.createHeader(
                getString(R.string.excel_id),
                getString(R.string.excel_company_name),
                getString(R.string.excel_room_code),
                getString(R.string.excel_floor),
                getString(R.string.excel_multiplier),
                getString(R.string.excel_counter_code),
                getString(R.string.excel_inventory_date),
                getString(R.string.excel_current_value),
                getString(R.string.excel_previous_value),
                getString(R.string.excel_difference),
                getString(R.string.excel_total)
        );

        excel.fill(exportInfo);

        String fileName = getString(R.string.title_counters) + " " + month.getSelectedItem().toString() + " " + year.getSelectedItem().toString() + ".xls";
        excel.save(fileName);

        finish();
    }
}
