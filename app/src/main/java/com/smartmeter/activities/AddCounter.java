package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;

import java.util.ArrayList;

public class AddCounter extends AppCompatActivity {
    private Spinner companies;
    private EditText newCompany;
    private EditText newRoom;
    private EditText newFloor;
    private EditText newMultiplier;
    private EditText newCounter;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_counter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        companies = findViewById(R.id.companyList);
        newCompany = findViewById(R.id.newCompany);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<String> tmpCompanyList = Buffer.dbHelper.getAllCompaniesList();
        tmpCompanyList.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style_light, R.id.spinner_text_light, tmpCompanyList);
        companies.setAdapter(adapter);

        companies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newCompany.setText(companies.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void addNewCounterOnClick(View view) {
        try {
            newCompany = findViewById(R.id.newCompany);
            newRoom = findViewById(R.id.newRoom);
            newFloor = findViewById(R.id.newFloor);
            newMultiplier = findViewById(R.id.newMultiplier);
            newCounter = findViewById(R.id.newCounter);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.title_new_company) + ": " + newCompany.getText().toString() +
                            "\n" + getString(R.string.title_newRoom) + ": " + newRoom.getText().toString() +
                            "\n" + getString(R.string.title_newFloor) + ": " + newFloor.getText().toString() +
                            "\n" + getString(R.string.title_newMultiplier) + ": " + newMultiplier.getText().toString() +
                            "\n" + getString(R.string.title_newCounter) + ": " + newCounter.getText().toString())
                    .setTitle(getString(R.string.alert_title_is_it_correct))
                    .setPositiveButton(getString(R.string.alert_YES), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Buffer.dbHelper.addNewCounter(
                                    newCompany.getText().toString(),
                                    newRoom.getText().toString(),
                                    Integer.parseInt(newFloor.getText().toString()),
                                    Integer.parseInt(newMultiplier.getText().toString()),
                                    newCounter.getText().toString()
                            );
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.alert_NO), null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(AddCounter.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}