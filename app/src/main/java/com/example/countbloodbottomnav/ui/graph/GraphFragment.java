package com.example.countbloodbottomnav.ui.graph;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelData;
import com.example.countbloodbottomnav.models.ModelGraphData;
import com.example.countbloodbottomnav.models.ModelGraph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class GraphFragment extends Fragment {

    //region UI
    private View view;
    private LinearLayout lay_bottom, lay_graph;
    private TextView txt_fromDate, txt_toDate;
    private ImageButton btn_fromDate, btn_toDate, btn_frombk, btn_fromfw, btn_tobk, btn_tofw;
    private ImageView btn_hide;
    private Switch sw_blood, sw_average, sw_short, sw_long;
    private RadioGroup rg_timeframe;
    //endregion
    private MainActivity MA;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private Date fromDate, toDate;
    private boolean isFromDate = true;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        MA = ((MainActivity) getActivity());
        initUI();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            hideAndSeek();
        if (MA.graph == null) today();
        else updateView();
        initOnClick();
        return view;
    }

    private void initUI() {
        lay_bottom = view.findViewById(R.id.layout_controls);
        lay_graph = view.findViewById(R.id.layout_graph);
        txt_fromDate = view.findViewById(R.id.txt_fromDate);
        txt_toDate = view.findViewById(R.id.txt_toDate);
        btn_fromDate = view.findViewById(R.id.button_datefrom);
        btn_frombk = view.findViewById(R.id.btn_fromback);
        btn_fromfw = view.findViewById(R.id.btn_fromforward);
        btn_toDate = view.findViewById(R.id.button_dateto);
        btn_tobk = view.findViewById(R.id.btn_toback);
        btn_tofw = view.findViewById(R.id.btn_toforward);
        btn_hide = view.findViewById(R.id.btn_hide_graph_input);
        sw_blood = view.findViewById(R.id.switch_blood);
        sw_average = view.findViewById(R.id.switch_average);
        sw_short = view.findViewById(R.id.switch_shortterm);
        sw_long = view.findViewById(R.id.switch_longterm);
        rg_timeframe = view.findViewById(R.id.rg_timeframe);
    }

    private void initOnClick() {

        btn_fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromDate = true;
                createDatePicker();
            }
        });

        btn_toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromDate = false;
                createDatePicker();
            }
        });

        btn_frombk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDate(-1, true);
            }
        });
        btn_fromfw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDate(1, true);
            }
        });
        btn_tobk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDate(-1, false);
            }
        });
        btn_tofw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDate(1, false);
            }
        });

        rg_timeframe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveGraphSetup();
            }
        });

        btn_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAndSeek();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                Calendar cal = Calendar.getInstance();
                cal.set(y, m, d, 0, 0, 0);
                if (isFromDate) fromDate = cal.getTime();
                else {
                    cal.add(Calendar.HOUR_OF_DAY, 24);
                    toDate = cal.getTime();
                }
                saveGraphSetup();
            }
        };
    }

    private void today() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        fromDate = cal.getTime();
        cal.add(Calendar.HOUR_OF_DAY, 24);
        toDate = cal.getTime();

        saveGraphSetup();
    }

    private void addToDate(int days, boolean isfrom) {
        Calendar cal = Calendar.getInstance();
        if (isfrom) {
            cal.setTime(fromDate);
            cal.add(Calendar.DATE, days);
            fromDate = cal.getTime();
        } else {
            cal.setTime(toDate);
            cal.add(Calendar.DATE, days);
            toDate = cal.getTime();
        }
        saveGraphSetup();
    }

    private void hideAndSeek() {
        // Shows graph controls if button pushed else hides controls
        if (lay_bottom.getVisibility() == View.GONE) {
            lay_bottom.setVisibility(View.VISIBLE);
            btn_hide.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.ic_visibility_off_24dp, null));

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                lay_bottom.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
            }
        } else {
            lay_bottom.setVisibility(View.GONE);
            btn_hide.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.ic_visibility_24dp, null));

        }
    }

    private void createDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                Objects.requireNonNull(getContext()),
                R.style.customDateDialog,
                onDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.RED)));
        dialog.show();
    }

    private void saveGraphSetup() {
        MA.graph = createGraphSetup();
        MA.IO.saveGraphSetup(MA.graph);
        updateView();
    }

    private void updateView() {
        fromDate = MA.graph.getStart();
        toDate = MA.graph.getEnd();

        txt_fromDate.setText(formatter.format(MA.graph.getStart()));
        txt_toDate.setText(formatter.format(MA.graph.getEnd()));
        sw_blood.setChecked(MA.graph.isBlood());
        sw_average.setChecked(MA.graph.isAverage());
        sw_short.setChecked(MA.graph.isFast());
        sw_long.setChecked(MA.graph.isSlow());
        rg_timeframe.check(MA.graph.getId_radioGroup());

        lay_graph.removeAllViewsInLayout();
        //MA.graph.setList();
        lay_graph.addView(new GraphView(getActivity(), MA.graph));
    }

    private ModelGraph createGraphSetup() {
        //Creates modelGraph for grid and layout
        ModelGraph mgs;
        switch (rg_timeframe.getCheckedRadioButtonId()) {
            case R.id.rb_day:
                String[] xDay = {"00:00", "06:00", "12:00", "18:00", "24:00"};
                mgs = new ModelGraph(24, 6, 25, 5, xDay, 1);
                break;
            case R.id.rb_week:
                String[] xWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"};
                mgs = new ModelGraph(28, 4, 25, 5, xWeek, 7);
                break;
            case R.id.rb_month:
                String[] xMonth = {"Week1", "Week2", "Week3", "Week4", "Week5"};
                mgs = new ModelGraph(31, 7, 25, 5, xMonth, 31);
                break;
            case R.id.rb_quarter:
                String[] xQuarter = {"W0", "W2", "W4", "W6", "W8", "W10", "W12", "W14", "W9", "W10", "W11", "W12", "W13", "W14", "W15"};
                mgs = new ModelGraph(39, 6, 25, 5, xQuarter, 91);
                break;
            case R.id.rb_year:
                String[] xYear = {"J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "J"};
                mgs = new ModelGraph(36, 3, 25, 5, xYear, 365);
                break;
            default:
                mgs = new ModelGraph();
        }

        mgs.setStart(fromDate);
        mgs.setEnd(toDate);
        mgs.setBlood(sw_blood.isChecked());
        mgs.setAverage(sw_average.isChecked());
        mgs.setFast(sw_short.isChecked());
        mgs.setSlow(sw_long.isChecked());
        mgs.setId_radioGroup(rg_timeframe.getCheckedRadioButtonId());
        mgs.setList(createGraphDataList(mgs.getNumDays()));
        return mgs;
    }

    private ArrayList<ModelGraphData> createGraphDataList(int days) {
        ArrayList<ModelGraphData> list = new ArrayList<>();

        //Compares dates to get correct data from date interval
        for (ModelData data : MA.list) {
            if (data.getDate().compareTo(fromDate) > 0 && data.getDate().compareTo(toDate) < 0) {

                //sets time of day (tod)
                Calendar cal = Calendar.getInstance();
                cal.setTime(data.getDate());
                float x = 0, tod = ((cal.get(Calendar.HOUR_OF_DAY) / 24f) +
                        (cal.get(Calendar.MINUTE) / (24f * 60)) +
                        (cal.get(Calendar.SECOND) / (24f * 60 * 60)));

                //Spread out daily data depending on the timeframe
                if (days == 1) x = tod;
                else if (days == 7) x = ((cal.get(Calendar.DAY_OF_WEEK) - 1 + tod) / 7f);
                else if (days == 31) x = ((cal.get(Calendar.DAY_OF_MONTH) - 1 + tod) / 31f);
                else if (days == 91) x = (((cal.get(Calendar.DAY_OF_YEAR) - 1) % 91) / 91f);
                else if (days == 365) x = ((cal.get(Calendar.DAY_OF_YEAR) - 1 + tod) / 365f);

                //Adds data to list if checked and correct type
                if (sw_blood.isChecked() && data.getType() == 0)
                    list.add(new ModelGraphData(x, data.getAmount(), data.getType()));
                if (sw_short.isChecked() && data.getType() == 1)
                    list.add(new ModelGraphData(x, data.getAmount(), data.getType()));
                if (sw_long.isChecked() && data.getType() == 2)
                    list.add(new ModelGraphData(x, data.getAmount(), data.getType()));
            }
        }

        //Sorts list so draw insulin first and blood on top
        Collections.sort(list, new Comparator<ModelGraphData>() {
            public int compare(ModelGraphData obj1, ModelGraphData obj2) {
                return Integer.compare(obj2.getP(), obj1.getP()); }});

        return list;
    }
}