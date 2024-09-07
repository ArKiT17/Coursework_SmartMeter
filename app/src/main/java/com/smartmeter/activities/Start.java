package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;

public class Start extends AppCompatActivity {
    private Button startButton;
    private Button countersButton;
    private Button valuesButton;
    private Button exportButton;
    private Button historyButton;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        startButton = findViewById(R.id.startButton);
        countersButton = findViewById(R.id.companiesButton);
        valuesButton = findViewById(R.id.valuesButton);
        exportButton = findViewById(R.id.exportButton);
        historyButton = findViewById(R.id.historyButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startButton.setEnabled(true);
        countersButton.setEnabled(true);
        valuesButton.setEnabled(true);
        exportButton.setEnabled(true);
        historyButton.setEnabled(true);
    }

    public void startButtonClick(View view) {
        startButton.setEnabled(false);

        Intent startPage = new Intent(this, MainActivity.class);
        startActivity(startPage);
    }

    public void countersButton(View view) {
        countersButton.setEnabled(false);

        Intent editPage = new Intent(this, CountersActivity.class);
        startActivity(editPage);
    }

    public void valuesButtonClick(View view) {
        valuesButton.setEnabled(false);

        Intent editValuePage = new Intent(this, EditValueActivity.class);
        startActivity(editValuePage);
    }

    public void exportToExcel(View view) {
        exportButton.setEnabled(false);

        Intent exportPage = new Intent(this, ExportMailActivity.class);
        startActivity(exportPage);
    }

    public void historyButtonClick(View view) {
        historyButton.setEnabled(false);

        Intent historyPage = new Intent(this, ReviewValuesActivity.class);
        startActivity(historyPage);
    }
}