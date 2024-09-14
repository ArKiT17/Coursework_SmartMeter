package com.smartmeter.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.smartmeter.Buffer;
import com.smartmeter.R;

public class EditCounterActivity extends AppCompatActivity {
    private EditText textCompany;
    private EditText textRoom;
    private EditText textFloor;
    private EditText textMultiplier;
    private EditText textCounter;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_counter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textCompany = findViewById(R.id.textCompany);
        textRoom = findViewById(R.id.textRoom);
        textFloor = findViewById(R.id.textFloor);
        textMultiplier = findViewById(R.id.textMultiplier);
        textCounter = findViewById(R.id.textCounter);

        textCompany.setText(Buffer.activeCounter.company);
        textRoom.setText(Buffer.activeCounter.room);
        textFloor.setText(String.valueOf(Buffer.activeCounter.floor));
        textMultiplier.setText(String.valueOf(Buffer.activeCounter.multiplier));
        textCounter.setText(Buffer.activeCounter.counter);
    }

    public void deleteOnClick(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.alert_are_you_sure)
                    .setTitle(R.string.alert_title_warning)
                    .setPositiveButton(R.string.alert_YES, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Buffer.activeCounter != null) {
                                boolean deleted = Buffer.dbHelper.deleteCounter(Buffer.activeCounter.id);
                                Buffer.activeCounter = null;
                                Toast.makeText(EditCounterActivity.this, deleted ? R.string.toast_success : R.string.toast_error, Toast.LENGTH_SHORT).show();
                                finish();
                            } else
                                Toast.makeText(EditCounterActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.alert_NO, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(EditCounterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCounterOnClick(View view) {
        try {
            boolean updated = Buffer.dbHelper.updateCounter(Buffer.activeCounter.id, textCompany.getText().toString(), textRoom.getText().toString(),
                    Integer.parseInt(textFloor.getText().toString()), Integer.parseInt(textMultiplier.getText().toString()),
                    textCounter.getText().toString());
            Toast.makeText(EditCounterActivity.this, updated ? R.string.toast_success : R.string.toast_error, Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException exception) {
            Toast.makeText(this, R.string.toast_counter_not_found, Toast.LENGTH_LONG).show();
        }
    }
}