package com.smartmeter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ValuesListAdapter extends ArrayAdapter<ValueInfo> {
    private List<ValueInfo> values;

    public ValuesListAdapter(@NonNull Context context, int resource, @NonNull List<ValueInfo> objects) {
        super(context, resource, objects);
        this.values = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.values_list_adapter, parent, false);

        TextView valueId = convertView.findViewById(R.id.value_id);
        TextView valuePutDate = convertView.findViewById(R.id.value_putDate);
        TextView valueCurrentValue = convertView.findViewById(R.id.value_currentValue);
        TextView valuePreviousValue = convertView.findViewById(R.id.value_previousValue);
        TextView valueDifference = convertView.findViewById(R.id.value_difference);

        valueId.setText(String.valueOf(values.get(position).id));
        valuePutDate.setText(values.get(position).putDate);
        valueCurrentValue.setText(String.valueOf(values.get(position).currentValue));
        valuePreviousValue.setText(String.valueOf(values.get(position).previousValue));
        valueDifference.setText(String.valueOf(values.get(position).difference));

        return convertView;
    }
}