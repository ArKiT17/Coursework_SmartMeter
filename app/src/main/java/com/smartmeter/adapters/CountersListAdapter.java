package com.smartmeter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smartmeter.R;
import com.smartmeter.models.CounterInfo;

import java.util.List;

public class CountersListAdapter extends ArrayAdapter<CounterInfo> {
    private List<CounterInfo> counters;

    public CountersListAdapter(@NonNull Context context, int resource, @NonNull List<CounterInfo> objects) {
        super(context, resource, objects);
        this.counters = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.counter_list_adapter, parent, false);

        TextView itemId = convertView.findViewById(R.id.item_id);
        TextView itemCompany = convertView.findViewById(R.id.item_company);
        TextView itemCounter = convertView.findViewById(R.id.item_counter);
        TextView itemRoom = convertView.findViewById(R.id.item_room);
        TextView itemFloor = convertView.findViewById(R.id.item_floor);
        TextView itemMultiplier = convertView.findViewById(R.id.item_multiplier);

        itemId.setText(String.valueOf(counters.get(position).id));
        itemCompany.setText(counters.get(position).company);
        itemRoom.setText(counters.get(position).room);
        itemFloor.setText(String.valueOf(counters.get(position).floor));
        itemMultiplier.setText(String.valueOf(counters.get(position).multiplier));
        itemCounter.setText(counters.get(position).counter);

        return convertView;
    }
}