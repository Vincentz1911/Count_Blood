package com.example.countbloodbottomnav.ui.notifications;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelAlarm;

import java.util.Calendar;

public class NotificationsFragment extends Fragment {

    private MainActivity MA;

    private ListView lst_alarm;
    private Button btn_setAlarm;
    private NumberPicker np_hour, np_minute;
    private RadioGroup rg_type;
    private TextView txt_nextAlarm;
    private Spinner sp_repeatinterval;
    private View view;
    private AlarmListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        MA = ((MainActivity) getActivity());
        initUI();
        return view;
    }

    private void initUI() {
        txt_nextAlarm = view.findViewById(R.id.txt_nextalarm);
        lst_alarm = view.findViewById(R.id.list_alarmlist);
        adapter = new AlarmListAdapter(getContext(), MA.alarmlist);
        lst_alarm.setAdapter(adapter);

        np_hour = view.findViewById(R.id.alarm_hour);
        np_hour.setMaxValue(23);
        np_hour.setMinValue(0);
        np_hour.setValue(12);
        np_hour.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) { return String.format("%02d", value); }});

        np_minute = view.findViewById(R.id.alarm_minutes);
        np_minute.setMaxValue(59);
        np_minute.setMinValue(0);
        np_minute.setValue(0);
        np_minute.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        rg_type = view.findViewById(R.id.rg_alarmtype);

        String[] spinnerText = {"Never" ,"5 Minutes", "Hourly", "3 Hours", "Daily", "Weekly"};
        sp_repeatinterval = view.findViewById(R.id.spn_repeat);
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, spinnerText);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_repeatinterval.setAdapter(spnAdapter);

        btn_setAlarm = view.findViewById(R.id.btn_setalarm);
        btn_setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setAlarm(getAlarm()); }});

    }

    private ModelAlarm getAlarm() {
        boolean isRepeat;
        int[] spinnerValues = {0, 5, 60, 180, 1440, 10080};
        int sp_position = sp_repeatinterval.getSelectedItemPosition();
        isRepeat = sp_position != 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, np_hour.getValue());
        c.set(Calendar.MINUTE, np_minute.getValue());
        c.set(Calendar.SECOND, 0);

        switch (rg_type.getCheckedRadioButtonId()) {
            case R.id.alarm_bloodSample:
                return new ModelAlarm("Blood Sugar",
                        "This is a reminder to test your blood sugar level.",
                        10, c.getTime(), R.drawable.ic_blood_drop,
                        isRepeat, spinnerValues[sp_position]);
            case R.id.alarm_fastInsulin:
                return new ModelAlarm("Short-Acting Insulin",
                        "This is a reminder to take your short-acting insulin.",
                        10, c.getTime(), R.drawable.ic_rabbit,
                        isRepeat, spinnerValues[sp_position]);

            case R.id.alarm_slowInsulin:
                return new ModelAlarm("Long-Acting Insulin",
                        "This is a reminder to take your long-acting insulin.",
                        10, c.getTime(), R.drawable.ic_turtle,
                        isRepeat, spinnerValues[sp_position]);

            case R.id.alarm_other:
                return new ModelAlarm("Count Blood",
                        "This is just a regular alarm.",
                        10, c.getTime(), R.drawable.ic_notifications_black_24dp,
                        isRepeat, spinnerValues[sp_position]);
        }
        return null;
    }

    private void setAlarm(ModelAlarm alarm) {

        Intent alertIntent = new Intent(getContext(), Notification_receiver.class);
        alertIntent.putExtra("Title", alarm.getTitle());
        alertIntent.putExtra("Message", alarm.getMessage());
        alertIntent.putExtra("RequestCode", alarm.getRequestCode());
        alertIntent.putExtra("Icon", alarm.getIcon());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                alarm.getRequestCode(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        if (alarm.getRepeatInterval() > 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(),
                    alarm.getRepeatInterval(), pendingIntent);
        } else alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(), pendingIntent);

        MA.alarmlist.add(alarm);
        adapter.notifyDataSetChanged();
    }
}
