package com.example.countbloodbottomnav.ui.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelAlarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AlarmListAdapter extends ArrayAdapter<ModelAlarm> {

    private List<ModelAlarm> list;

    AlarmListAdapter(@NonNull Context context, ArrayList<ModelAlarm> list) {
        super(context, 0, list);
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.alarmlist_item, parent, false);

        ModelAlarm alarm = list.get(position);

        ImageView imageRepeat = listItem.findViewById(R.id.listImageRepeat);
        if (alarm.getRepeatInterval() == 0) {
            if (alarm.getDate().before(new Date()))
                imageRepeat.setImageResource(R.drawable.ic_timer_off_36dp);
            else imageRepeat.setImageResource(R.drawable.ic_timer_36dp);
        } else imageRepeat.setImageResource(R.drawable.ic_autorenew_36p);

        ImageView imageView = listItem.findViewById(R.id.listImage);
        imageView.setImageResource(alarm.getIcon());

        TextView date = listItem.findViewById(R.id.txt_date);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm EEE d MMM");
        date.setText(formatter.format(alarm.getDate()));

        return listItem;
    }
}