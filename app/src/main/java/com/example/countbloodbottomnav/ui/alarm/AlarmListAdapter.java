package com.example.countbloodbottomnav.ui.alarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelAlarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AlarmListAdapter extends ArrayAdapter<ModelAlarm> {

    private List<ModelAlarm> list;

    EventListener listener;

    public interface EventListener { void cancelAlarm(ModelAlarm alarm, int position);}

    AlarmListAdapter(@NonNull Context context, ArrayList<ModelAlarm> list, EventListener listener) {
        super(context, 0, list);
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (convertView == null) listItem = LayoutInflater.from(getContext())
                .inflate(R.layout.alarmlist_item, parent,false);

        final ModelAlarm alarm = list.get(position);

        ImageView imageRepeat = listItem.findViewById(R.id.listImageRepeat);
        if (alarm.getRepeat() == 0) {
            if (alarm.getDate().before(new Date()))
                imageRepeat.setImageResource(R.drawable.ic_timer_off_36dp);
            else imageRepeat.setImageResource(R.drawable.ic_timer_36dp);
        } else imageRepeat.setImageResource(R.drawable.ic_autorenew_36p);

        ImageView imageView = listItem.findViewById(R.id.listImage);
        imageView.setImageResource(MainActivity.rb_icon[alarm.getType()]);

        TextView date = listItem.findViewById(R.id.txt_date);
        date.setText(MainActivity.datetime.format(alarm.getDate()));

        ImageButton btn_delete = listItem.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(v -> listener.cancelAlarm(alarm, position));

        return listItem;
    }
}
