package com.example.countbloodbottomnav.ui.data;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.models.ModelData;
import com.example.countbloodbottomnav.R;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class DataFragment extends Fragment {

    //region UI
    private View view;
    private LinearLayout lay_bottom;
    private ListView listView;
    private TextView txt_bsAmount, txt_bsAverage;
    private ImageView btn_hide;
    private ImageButton btn_numpad, btn_addNew;
    private Button btn_sortDate, btn_sortValue;
    private NumberPicker np1, np2;
    private RadioGroup rg_dataType;
    private ArrayAdapter<ModelData> adapter_data;
    //endregion
    private MainActivity MA;
    private boolean isSortedDate = false, isSortedValue = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);
        MA = (MainActivity) getActivity();
        initUI();
        initOnClick();
        return view;
    }

    private void initUI() {
        lay_bottom = view.findViewById(R.id.lay_addData);
        btn_addNew = view.findViewById(R.id.btn_add);
        btn_numpad = view.findViewById(R.id.btn_numpad);
        btn_hide = view.findViewById(R.id.btn_hide_home_input);
        btn_sortDate = view.findViewById(R.id.btn_sortDate);
        btn_sortValue = view.findViewById(R.id.btn_sortValue);
        txt_bsAmount = view.findViewById(R.id.txt_numSamples);
        txt_bsAverage = view.findViewById(R.id.txt_avgSamples);
        rg_dataType = view.findViewById(R.id.radioGroup);

        adapter_data = new DataListAdapter(Objects.requireNonNull(getContext()), MA.data_list, MA.settings.getHighBS(), MA.settings.getLowBS());
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter_data);

        np1 = view.findViewById(R.id.np1_home);
        np1.setMaxValue(50);
        np1.setMinValue(0);
        np1.setValue((int) MA.settings.getDefaultBS());

        np2 = view.findViewById(R.id.np2_home);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setValue(0);

        updateView();
        sortByDate();
    }

    private void initOnClick() {
        listView.setOnItemClickListener((parent, view, position, id) -> deleteSample(position));

        btn_sortDate.setOnClickListener(v -> sortByDate());

        btn_sortValue.setOnClickListener(v -> sortBySample());

        btn_addNew.setOnClickListener(v -> addNewSample((np1.getValue() + np2.getValue() / 10f)));

        btn_numpad.setOnClickListener(v -> numPadSample());

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

        rg_dataType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_blood: np1.setValue((int) MA.settings.getDefaultBS()); break;
                case R.id.rb_fast: np1.setValue(MA.settings.getFast()); break;
                case R.id.rb_slow: np1.setValue(MA.settings.getLongTerm()); break;
            }
            np2.setValue(0);
        });
    }

    private void updateView() {
        adapter_data.notifyDataSetChanged();
        float amount = 0;
        int total = 0;
        for (ModelData sample : MA.data_list) {
            if (sample.getType() == 0) {
                amount += sample.getAmount();
                total++;
            }
        }
        txt_bsAmount.setText(MA.f2str(total));
        txt_bsAverage.setText(MA.f2str(amount / total));
    }

    private void addNewSample(float value) {
        switch (rg_dataType.getCheckedRadioButtonId()) {
            case R.id.rb_blood: MA.data_list.add(new ModelData(value, new Date(), 0)); break;
            case R.id.rb_fast: MA.data_list.add(new ModelData(value, new Date(), 1)); break;
            case R.id.rb_slow: MA.data_list.add(new ModelData(value, new Date(), 2)); break;
        }
        isSortedDate = false;
        sortByDate();
        MA.IO.saveData(MA.data_list);
        MA.toast("New data added");
        updateView();
    }

    private void deleteSample(final int position) {
        ModelData data = (ModelData) listView.getItemAtPosition(position);
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Blood Measurement")
                .setMessage("Do you want to delete " + data.getDate() + " " + data.getAmount())
                //.setIcon(R.drawable.ic_action_bluetooth_connected)
                .setPositiveButton("OK", (dialog, which) -> {
                    MA.data_list.remove(position);
                    MA.IO.saveData(MA.data_list);
                    updateView();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                }).show();
    }

    private void numPadSample() {
        showKeyboard();
        final EditText input = new EditText(getContext());
        input.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        input.setSingleLine();
        input.requestFocus();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Input Sample")
                .setMessage("Type in the amount:")
                .setView(input)
                .setIcon((int)rg_dataType.getCheckedRadioButtonId())
                .setPositiveButton("Add", (dialog12, which) -> {
                    String s = input.getText().toString().replace(",", ".");
                    try {
                        addNewSample(Float.parseFloat(s));
                        closeKeyboard();
                    } catch (NumberFormatException e) {
                        input.setText("");
                        closeKeyboard();
                    }
                })
                .setNegativeButton("Cancel", (dialog1, which) -> closeKeyboard()).show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(ContextCompat.getDrawable(
                Objects.requireNonNull(getContext()), R.drawable.btn_bg_selector));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(ContextCompat.getDrawable(
                getContext(), R.drawable.btn_bg_selector));
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) MA.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) MA.getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void sortByDate() {
        if (isSortedDate) {
            isSortedDate = false;
            btn_sortDate.setText(R.string.date_sort_up);
            btn_sortValue.setText(R.string.value);
            Collections.sort(MA.data_list, (obj1, obj2) -> obj1.getDate().compareTo(obj2.getDate()));
        } else {
            isSortedDate = true;
            btn_sortDate.setText(R.string.date_sort_down);
            btn_sortValue.setText(R.string.value);
            Collections.sort(MA.data_list, (obj1, obj2) -> obj2.getDate().compareTo(obj1.getDate()));
        }
        adapter_data.notifyDataSetChanged();
    }

    private void sortBySample() {
        if (!isSortedValue) {
            isSortedValue = true;
            btn_sortDate.setText(R.string.date);
            btn_sortValue.setText(R.string.value_sort_down);
            Collections.sort(MA.data_list, (obj1, obj2) -> Float.compare(obj2.getAmount(), obj1.getAmount()));
        } else {
            isSortedValue = false;
            btn_sortDate.setText(R.string.date);
            btn_sortValue.setText(R.string.value_sort_up);
            Collections.sort(MA.data_list, (obj1, obj2) -> Float.compare(obj1.getAmount(), obj2.getAmount()));
        }
        adapter_data.notifyDataSetChanged();
    }
}