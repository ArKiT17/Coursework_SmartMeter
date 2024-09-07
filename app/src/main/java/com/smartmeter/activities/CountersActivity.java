package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.CounterInfo;
import com.smartmeter.CountersListAdapter;
import com.smartmeter.R;

import java.util.ArrayList;
import java.util.List;

public class CountersActivity extends AppCompatActivity {
    private ListView listCounters;
    private EditText searchBar;
    private List<CounterInfo> counters = new ArrayList<>();
    private final List<CounterInfo> filteredCounters = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counters);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listCounters = findViewById(R.id.listCounters);
        searchBar = findViewById(R.id.searchBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filteredCounters.clear();
                if (searchBar.getText().toString().isEmpty()) {
                    CountersListAdapter adapter = new CountersListAdapter(CountersActivity.this, R.layout.counter_list_adapter, counters);
                    listCounters.setAdapter(adapter);
                } else {
                    String filter = searchBar.getText().toString().toLowerCase();
                    for (CounterInfo tmpCounter : counters) {
                        if (tmpCounter.company.toLowerCase().contains(filter) ||
                                tmpCounter.counter.toLowerCase().contains(filter) ||
                                tmpCounter.room.toLowerCase().contains(filter) ||
                                String.valueOf(tmpCounter.id).contains(filter) ||
                                String.valueOf(tmpCounter.room).contains(filter))
                            filteredCounters.add(tmpCounter);
                    }
                    CountersListAdapter adapter = new CountersListAdapter(CountersActivity.this, R.layout.counter_list_adapter, filteredCounters);
                    listCounters.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        counters = Buffer.dbHelper.getAllCountersList();
        searchBar.setText("");

        listCounters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Buffer.activeCounter = (CounterInfo)adapterView.getItemAtPosition(i);
//                Intent editCounterPage = new Intent(CountersActivity.this, EditCounterActivity.class);
//                startActivity(editCounterPage);
            }
        });
    }

    public void addCounterClick(View view) {
        Intent addCounterPage = new Intent(this, AddCounter.class);
        startActivity(addCounterPage);
    }

    public void clearSearchBar(View view) {
        searchBar.setText("");
    }
}