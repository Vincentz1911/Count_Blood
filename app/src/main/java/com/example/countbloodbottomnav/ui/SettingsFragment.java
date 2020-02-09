package com.example.countbloodbottomnav.ui;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SettingsFragment extends Fragment {

    //region UI
    private View view;
    private Button btn_reset, btn_generate;
    private ImageButton btn_save;
    private EditText inputEmail;
    private NumberPicker spDBS, spHigh, spHighDec, spLow, spLowDec, spLong, spLongEve, spShort;
    //endregion
    private MainActivity MA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        MA = ((MainActivity) getActivity());
        initUI();
        btn_save.setOnClickListener(v -> saveSettings());
        btn_reset.setOnClickListener(v -> resetList());
        btn_generate.setOnClickListener(v -> generateList());
        return view;
    }

    private void initUI() {
        btn_save = view.findViewById(R.id.btn_saveSettings);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_generate = view.findViewById(R.id.btn_generatelist);

        inputEmail = view.findViewById(R.id.input_defaultEmail);
        inputEmail.setText(MA.settings.getEmailRecipiant());

        spDBS = view.findViewById(R.id.spn_defaultBS);
        spDBS.setMinValue(0);
        spDBS.setMaxValue(20);
        spDBS.setValue((int) MA.settings.getDefaultBS());

        spShort = view.findViewById(R.id.spn_ShortTerm);
        spShort.setMinValue(0);
        spShort.setMaxValue(30);
        spShort.setValue(MA.settings.getFast());

        spLong = view.findViewById(R.id.spn_LongTerm);
        spLong.setMinValue(0);
        spLong.setMaxValue(30);
        spLong.setValue(MA.settings.getLongTerm());

        spLongEve = view.findViewById(R.id.spn_LongTermEve);
        spLongEve.setMinValue(0);
        spLongEve.setMaxValue(30);
        spLongEve.setValue(MA.settings.getLongTermEve());

        spHigh = view.findViewById(R.id.spn_HighBS);
        spHigh.setMinValue(0);
        spHigh.setMaxValue(20);
        spHigh.setValue((int) MA.settings.getHighBS());

        spLow = view.findViewById(R.id.spn_LowBS);
        spLow.setMinValue(0);
        spLow.setMaxValue(20);
        spLow.setValue((int) MA.settings.getLowBS());

        spHighDec = view.findViewById(R.id.spn_HighBSDec);
        spHighDec.setMinValue(0);
        spHighDec.setMaxValue(9);
        spHighDec.setValue((int) ((MA.settings.getHighBS() % 1) * 10));

        spLowDec = view.findViewById(R.id.spn_LowBSDec);
        spLowDec.setMinValue(0);
        spLowDec.setMaxValue(9);
        spLowDec.setValue((int) ((MA.settings.getLowBS() % 1) * 10));
    }

    private void saveSettings() {
        MA.settings.setDefaultBS(spDBS.getValue());
        MA.settings.setFast(spShort.getValue());
        MA.settings.setLongTerm(spLong.getValue());
        MA.settings.setLongTermEve(spLongEve.getValue());
        MA.settings.setHighBS(spHigh.getValue() + (float) spHighDec.getValue() / 10);
        MA.settings.setLowBS(spLow.getValue() + (float) spLowDec.getValue() / 10);
        MA.settings.setEmailRecipiant(inputEmail.getText().toString());
        MA.IO.saveSettings(MA.settings);
        MA.toast("Settings saved");
    }

    private void resetList() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete All Blood samples")
                .setMessage("Do you want to reset the data_list? This cannot be undone!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Reset", (dialog, which) -> {
                    MA.data_list = new ArrayList<>();
                    MA.IO.saveData(MA.data_list);
                    MA.toast("List have been reset");
                })
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .show();
    }

    private void generateList() {
        Random rnd = new Random();
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < 365; i++) {
            c.add(Calendar.DATE, -1);
            c.set(Calendar.HOUR_OF_DAY, 6);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            //Glucose test results tpd = test per day; tr= test result
            for (int tpd = 0; tpd < rnd.nextInt(5) + 2; tpd++) {
                float tr = ((rnd.nextInt(100) + 30) / 10f);
                if (tr > 10) tr = ((rnd.nextInt(150) + 30) / 10f);

                c.set(Calendar.HOUR_OF_DAY, rnd.nextInt(18)+6);
                c.set(Calendar.MINUTE, rnd.nextInt(60));
                c.set(Calendar.SECOND, rnd.nextInt(60));
                MA.data_list.add(new ModelData(tr, c.getTime(), 0));
            }

            //Fast-acting insulin
            c.set(Calendar.HOUR_OF_DAY, 7);
            c.set(Calendar.MINUTE, rnd.nextInt(60));
            MA.data_list.add(new ModelData(MA.settings.getFast(), c.getTime(), 1));
            c.set(Calendar.HOUR_OF_DAY, 12);
            c.set(Calendar.MINUTE, rnd.nextInt(60));
            MA.data_list.add(new ModelData(MA.settings.getFast(), c.getTime(), 1));
            c.set(Calendar.HOUR_OF_DAY, 18);
            c.set(Calendar.MINUTE, rnd.nextInt(60));
            MA.data_list.add(new ModelData(MA.settings.getFast(), c.getTime(), 1));

            //Slow-acting insulin
            c.set(Calendar.HOUR_OF_DAY, 6);
            c.set(Calendar.MINUTE, rnd.nextInt(60));
            MA.data_list.add(new ModelData(MA.settings.getLongTerm(), c.getTime(), 2));
            c.set(Calendar.HOUR_OF_DAY, 21);
            c.set(Calendar.MINUTE, rnd.nextInt(60));
            MA.data_list.add(new ModelData(MA.settings.getLongTerm(), c.getTime(), 2));
        }
        MA.IO.saveData(MA.data_list);
        MA.toast("Random Data have been generated");
    }
}