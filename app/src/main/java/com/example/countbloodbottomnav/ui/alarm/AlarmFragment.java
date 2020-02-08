package com.example.countbloodbottomnav.ui.alarm;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AlarmFragment extends Fragment implements AlarmListAdapter.EventListener {

    //TODO add functionality to item click (edit / delete)

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
    private AlarmManager alarmManager;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private Calendar cal = Calendar.getInstance();
    private String[] txt_repeat = {"Never", "5 Minutes", "Hourly", "3 Hours", "Daily", "Weekly"};
    private int openPos;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        MA = (MainActivity) getActivity();
        if (MA.alarm == null) MA.alarm = new ModelAlarm(new Date());
        initUI();
        initOnClick();
        updateAlarmView();
        //new AsyncUpdateView().execute();
        return view;
    }

    private void initUI() {
        np_hour = view.findViewById(R.id.alarm_hour);
        np_hour.setMaxValue(23);
        np_hour.setMinValue(0);
        np_hour.setValue(cal.get(Calendar.HOUR_OF_DAY));
        np_hour.setFormatter((int value) -> String.format(Locale.getDefault(), "%02d", value));

        np_minute = view.findViewById(R.id.alarm_minutes);
        np_minute.setMaxValue(59);
        np_minute.setMinValue(0);
        np_minute.setValue(cal.get(Calendar.MINUTE));
        np_minute.setFormatter((int value) -> String.format(Locale.getDefault(), "%02d", value));

        lay_bottom = view.findViewById(R.id.lay_bottom);
        btn_hide = view.findViewById(R.id.btn_hide_alarm_input);
        btn_setDate = view.findViewById(R.id.btn_setdate);
        btn_setRepeat = view.findViewById(R.id.btn_setrepeat);
        btn_setAlarm = view.findViewById(R.id.btn_setalarm);
        txt_repeatAlarm = view.findViewById(R.id.txt_repeat_alarm);
        txt_dateAlarm = view.findViewById(R.id.txt_date_alarm);
        rg_type = view.findViewById(R.id.rg_alarmtype);

        adapter = new AlarmListAdapter(getContext(), MA.alarm_list, this);
        listView = view.findViewById(R.id.listview_alarm);
        listView.setAdapter(adapter);
    }


    //If the last open drawer is open then close it
    View oldDrawer;
    void closeDrawer(View drawer){

        if (oldDrawer !=null && oldDrawer.getVisibility() == View.VISIBLE) oldDrawer.setVisibility(View.GONE);
        if (drawer.getVisibility() == View.GONE && oldDrawer != drawer) drawer.setVisibility(View.VISIBLE);
        //else drawer.setVisibility(View.GONE);
        oldDrawer = drawer;
        updateAlarmView();

    }

    private void initOnClick() {
        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            MA.alarm = (ModelAlarm) listView.getItemAtPosition(position);
            closeDrawer(itemView.findViewById(R.id.lay_drawer));
//            LinearLayout drawer = itemView.findViewById(R.id.lay_drawer);
//            if (drawer.getVisibility() == View.GONE) {
//                drawer.setVisibility(View.VISIBLE);
//                MA.alarm = (ModelAlarm) listView.getItemAtPosition(position);
//            } else {
//                drawer.setVisibility(View.GONE);
//                MA.alarm = new ModelAlarm(new Date());
//            }

        });

        btn_setAlarm.setOnClickListener(v -> {
            createAlarm();
            setAlarm();
            updateAlarmView();
        });

        btn_setRepeat.setOnClickListener(v -> {
            MA.alarm.setRepeat((MA.alarm.getRepeat() + 1) % txt_repeat.length);
            updateAlarmView();
        });

        btn_setDate.setOnClickListener(v -> createDatePicker());

        onDateSetListener = (view, y, m, d) -> {
            cal.set(y, m, d);
            MA.alarm.setDate(cal.getTime());
            updateAlarmView();
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

        rg_type.setOnCheckedChangeListener((group, checkedId) -> MA.alarm.setType(rbType()));
    }

    private int rbType() {
        switch (rg_type.getCheckedRadioButtonId()) {
            case R.id.alarm_bloodSample:
                return 0;
            case R.id.alarm_fastInsulin:
                return 1;
            case R.id.alarm_slowInsulin:
                return 2;
            case R.id.alarm_other:
                return 3;
            default:
                return 0;
        }
    }

    @Override
    public void cancelAlarm(ModelAlarm alarm, int position) {
        int[] repeat_int = {0, 5, 60, 180, 1440, 10080};

        Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);
        alertIntent.putExtra("Title", alarm.getTitle());
        alertIntent.putExtra("Message", alarm.getMessage());
        alertIntent.putExtra("RequestCode", alarm.getRequestCode());
        alertIntent.putExtra("Icon", MainActivity.rb_icon[alarm.getType()]);
        alertIntent.putExtra("Repeat", repeat_int[alarm.getRepeat()]);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                alarm.getRequestCode(),
                alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext()
                .getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        MA.alarm_list.remove(position);
        MA.IO.saveAlarms(MA.alarm_list);
        updateAlarmView();
    }

    private class AsyncUpdateView extends AsyncTask<AlarmFragment, Void, Void> {
        @Override
        protected Void doInBackground(AlarmFragment... alarmFragments) {
            AlarmFragment.this.updateAlarmView();
            return null;
        }
    }

    private void updateAlarmView() {
        cal.setTime(MA.alarm.getDate());
        np_hour.setValue(cal.get(Calendar.HOUR_OF_DAY));
        np_minute.setValue(cal.get(Calendar.MINUTE));
        txt_dateAlarm.setText(MainActivity.date.format(MA.alarm.getDate()));
        txt_repeatAlarm.setText(txt_repeat[MA.alarm.getRepeat()]);
        adapter.notifyDataSetChanged();
    }

    private void createDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                Objects.requireNonNull(getContext()),
                onDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.getDatePicker().updateDate(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void createAlarm() {
        String[] txt_title = {"Blood Sugar Measure", "Short-Acting Insulin",
                "Long-Acting Insulin", "Count Blood Notification", "Daily", "Weekly"};
        String[] txt_message = {"This is a reminder to test your blood sugar level.",
                "This is a reminder to take your short-acting insulin.",
                "This is a reminder to take your long-acting insulin.",
                "This is just a regular alarm. Maybe customize it?"};

        cal.set(Calendar.HOUR_OF_DAY, np_hour.getValue());
        cal.set(Calendar.MINUTE, np_minute.getValue());
        cal.set(Calendar.SECOND, 0);

        MA.alarm.setTitle(txt_title[MA.alarm.getType()]);
        MA.alarm.setMessage(txt_message[MA.alarm.getType()]);
        MA.alarm.setDate(cal.getTime());
        MA.alarm.setType(MA.alarm.getType());

        //LOADS, SETS AND SAVES REQUESTCODE FOR NOTIFICATIONS
        int requestCode = MA.IO.loadRequestCode() + 1;
        MA.alarm.setRequestCode(requestCode);
        MA.IO.saveRequestCode(requestCode);
    }

    private void setAlarm() {
        int[] repeat_int = {0, 5, 60, 180, 1440, 10080};

        Intent alertIntent = new Intent(getContext(), AlarmReceiver.class);
        alertIntent.putExtra("Title", MA.alarm.getTitle());
        alertIntent.putExtra("Message", MA.alarm.getMessage());
        alertIntent.putExtra("RequestCode", MA.alarm.getRequestCode());
        alertIntent.putExtra("Icon", MainActivity.rb_icon[MA.alarm.getType()]);
        alertIntent.putExtra("Repeat", repeat_int[MA.alarm.getRepeat()]);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                MA.alarm.getRequestCode(),
                alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null && MA.alarm.getRepeat() > 0) {

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    MA.alarm.getDate().getTime(),
                    repeat_int[MA.alarm.getRepeat()] * 1000, // * 60
                    pendingIntent);

        } else if (alarmManager != null)
            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    MA.alarm.getDate().getTime(),
                    pendingIntent);


        MA.alarm_list.add(MA.alarm);
        MA.IO.saveAlarms(MA.alarm_list);
    }
}
//    void onEvent(int data) {
//        doSomething(data);
//    }

