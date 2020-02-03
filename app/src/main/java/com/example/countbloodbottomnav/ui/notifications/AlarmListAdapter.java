package com.example.countbloodbottomnav.ui.notifications;

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
import java.util.List;

class AlarmListAdapter extends ArrayAdapter<ModelAlarm> {

    private List<ModelAlarm> list;

    AlarmListAdapter(@NonNull Context context, ArrayList<ModelAlarm> list) { super(context, 0, list);
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) listItem = LayoutInflater.from(getContext())
                .inflate(R.layout.alarmlist_item, parent,false);

        ModelAlarm alarm = list.get(position);

        ImageView imageView = listItem.findViewById(R.id.listImage);
        imageView.setImageResource(alarm.getIcon());

        TextView date = listItem.findViewById(R.id.txt_date);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        date.setText(formatter.format(alarm.getDate()));

        if (alarm.getRepeatInterval()>0)
        {
            ImageView imageRepeat = listItem.findViewById(R.id.listImageRepeat);
            imageRepeat.setImageResource(R.drawable.ic_autorenew_black_24dp);

            TextView interval = listItem.findViewById(R.id.txt_repeatinterval);
            interval.setText(""+ alarm.getRepeatInterval());
        }
        return listItem;
    }
}