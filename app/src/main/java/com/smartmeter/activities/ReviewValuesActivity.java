package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;
import com.smartmeter.adapters.ValuesListAdapter;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReviewValuesActivity extends AppCompatActivity {
    private Spinner companies;
    private Spinner counters;
    private ListView values;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_values);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        companies = findViewById(R.id.companyChooseList);
        counters = findViewById(R.id.counterChooseList);
        values = findViewById(R.id.listValues);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setSpaceAdapter(companies, Buffer.dbHelper.getAllCompaniesList());

        companies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                setSpaceAdapter(counters, Buffer.dbHelper.getAllCountersList(adapterView.getItemAtPosition(position).toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        counters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ValuesListAdapter adapter = new ValuesListAdapter(
                        ReviewValuesActivity.this, R.layout.values_list_adapter,
                        Buffer.dbHelper.getAllValuesList(companies.getSelectedItem().toString(), counters.getSelectedItem().toString()));
                values.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setSpaceAdapter(Spinner spinner, List<String> items) {
        items.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ReviewValuesActivity.this, R.layout.spinner_style_light_small, R.id.spinner_text_light, items);
        spinner.setAdapter(adapter);
    }
}