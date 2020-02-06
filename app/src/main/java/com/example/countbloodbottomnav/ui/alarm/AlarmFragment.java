package com.example.countbloodbottomnav.ui.alarm;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelAlarm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AlarmFragment extends Fragment {

    //region UI
    private View view;
    private LinearLayout lay_bottom;
    private ListView listView;
    private ImageView btn_hide;
    private TextView txt_repeatAlarm, txt_dateAlarm;
    private ImageButton btn_setDate, btn_setRepeat, btn_setAlarm;
    private NumberPicker np_hour, np_minute;
    private RadioGroup rg_type;
    private AlarmListAdapter adapter;
    //endregion
    private MainActivity MA;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private Calendar cal = Calendar.getInstance();
    private String[] txt_repeat = {"Never", "5 Minutes", "Hourly", "3 Hours", "Daily", "Weekly"};
    private int yS, mS, dS;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        MA = (MainActivity) getActivity();
        cal.setTime(Objects.requireNonNull(MA).alarm.getDate());
        initUI();
        initOnClick();
        return view;
    }

    private void initUI() {
        lay_bottom = view.findViewById(R.id.lay_bottom);
        btn_hide = view.findViewById(R.id.btn_hide_alarm_input);
        btn_setDate = view.findViewById(R.id.btn_setdate);
        btn_setRepeat = view.findViewById(R.id.btn_setrepeat);
        btn_setAlarm = view.findViewById(R.id.btn_setalarm);
        txt_repeatAlarm = view.findViewById(R.id.txt_repeat_alarm);
        txt_dateAlarm = view.findViewById(R.id.txt_date_alarm);
        rg_type = view.findViewById(R.id.rg_alarmtype);

        adapter = new AlarmListAdapter(Objects.requireNonNull(getContext()), MA.alarm_list);
        listView = view.findViewById(R.id.listview_alarm);
        listView.setAdapter(adapter);

        np_hour = view.findViewById(R.id.alarm_hour);
        np_hour.setMaxValue(23);
        np_hour.setMinValue(0);
        np_hour.setValue(cal.get(Calendar.HOUR_OF_DAY));
        np_hour.setFormatter((int value) -> String.format(Locale.getDefault(),"%02d", value));

        np_minute = view.findViewById(R.id.alarm_minutes);
        np_minute.setMaxValue(59);
        np_minute.setMinValue(0);
        np_minute.setValue(cal.get(Calendar.MINUTE));
        np_minute.setFormatter((int value) -> String.format(Locale.getDefault(),"%02d", value));

        updateAlarmView();
    }

    private void initOnClick() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            LinearLayout drawer = view.findViewById(R.id.lay_drawer);
            if (drawer.getVisibility() == View.GONE) drawer.setVisibility(View.VISIBLE);
            else drawer.setVisibility(View.GONE);

            //TODO add functionality to item click (edit / delete)
        });

        btn_setAlarm.setOnClickListener(v -> setAlarm(createAlarm()));

        btn_setRepeat.setOnClickListener(v -> {
            MA.alarm.setRepeat((MA.alarm.getRepeat() + 1) % txt_repeat.length);
            updateAlarmView();
        });

        btn_setDate.setOnClickListener(v -> createDatePicker(yS, mS, dS));

        onDateSetListener = (view, y, m, d) -> {
            cal.set(y, m, d);
            MA.alarm.setDate(cal.getTime());
        };

        btn_hide.setOnClickListener(v -> {
            if (lay_bottom.getVisibility() == View.GONE) {
                lay_bottom.setVisibility(View.VISIBLE);
                btn_hide.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_visibility_off_24dp, null));
            } else {
                lay_bottom.setVisibility(View.GONE);
                btn_hide.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_visibility_24dp, null));
            }
        });
    }

    private void updateAlarmView() {
        adapter.notifyDataSetChanged();
        txt_dateAlarm.setText(MainActivity.datetime.format(MA.alarm.getDate()));
        txt_repeatAlarm.setText(txt_repeat[MA.alarm.getRepeat()]);
    }

    private void createDatePicker(int yS, int mS, int dS) {
        DatePickerDialog dialog = new DatePickerDialog(
                Objects.requireNonNull(getContext()),
                R.style.customDateDialog,
                onDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private ModelAlarm createAlarm() {
        cal.set(Calendar.HOUR_OF_DAY, np_hour.getValue());
        cal.set(Calendar.MINUTE, np_minute.getValue());
        cal.set(Calendar.SECOND, 0);

        switch (rg_type.getCheckedRadioButtonId()) {
            case R.id.alarm_bloodSample:
                return new ModelAlarm("Blood Sugar",
                        "This is a reminder to test your blood sugar level.",
                        10, cal.getTime(), R.drawable.ic_blood_drop, MA.alarm.getRepeat());
            case R.id.alarm_fastInsulin:
                return new ModelAlarm("Short-Acting Insulin",
                        "This is a reminder to take your short-acting insulin.",
                        10, cal.getTime(), R.drawable.ic_rabbit, MA.alarm.getRepeat());
            case R.id.alarm_slowInsulin:
                return new ModelAlarm("Long-Acting Insulin",
                        "This is a reminder to take your long-acting insulin.",
                        10, cal.getTime(), R.drawable.ic_turtle, MA.alarm.getRepeat());
            default:
                return new ModelAlarm("Count Blood", "This is just a regular alarm.",
                        10, cal.getTime(), R.drawable.ic_notifications, MA.alarm.getRepeat());
        }
    }

    private void setAlarm(ModelAlarm alarm) {
        Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);
        alertIntent.putExtra("Title", alarm.getTitle());
        alertIntent.putExtra("Message", alarm.getMessage());
        alertIntent.putExtra("RequestCode", alarm.getRequestCode());
        alertIntent.putExtra("Icon", alarm.getIcon());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                alarm.getRequestCode(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getContext()).getSystemService(Context.ALARM_SERVICE);

        int[] repeat_int = {0, 5, 60, 180, 1440, 10080};
        if (alarmManager != null && alarm.getRepeat() > 0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(),
                    repeat_int[alarm.getRepeat()], pendingIntent);
        } else if (alarmManager != null)
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getDate().getTime(), pendingIntent);

        MA.alarm_list.add(alarm);
        MA.IO.saveAlarms(MA.alarm_list);
        updateAlarmView();
    }
}
