package com.example.countbloodbottomnav.ui.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DataFragment extends Fragment {

    //region UI
    private View view;
    private ListView listView;
    private ArrayAdapter<ModelData> adapter;
    private NumberPicker np1, np2;
    private RadioGroup radioGroup;
    private TextView txt_bsAmount, txt_bsAverage;
    private Button btn_addNew, btn_sortDate, btn_sortValue;
    private ImageButton btn_numpad;
    private ImageView btn_hide;
    private LinearLayout lay_bottom;
    //endregion
    private MainActivity MA;
    private int dataType = 0;
    private boolean isSortedDate = false, isSortedValue = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);
        MA = (MainActivity) getActivity();
        initUI();
        initOnClick();
        updateInfo();
        sortByDate();
        return view;
    }

    private void initUI() {
        lay_bottom = view.findViewById(R.id.lay_addData);

        adapter = new DataListAdapter(getContext(), MA.list, MA.settings.getHighBS(), MA.settings.getLowBS());
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        np1 = view.findViewById(R.id.np1_home);
        np1.setMaxValue(50);
        np1.setMinValue(0);
        np1.setValue((int) MA.settings.getDefaultBS());

        np2 = view.findViewById(R.id.np2_home);
        np2.setMaxValue(9);
        np2.setMinValue(0);
        np2.setValue(0);

        btn_addNew = view.findViewById(R.id.btn_add);
        btn_numpad = view.findViewById(R.id.btn_numpad);
        btn_hide = view.findViewById(R.id.btn_hide_home_input);
        btn_sortDate = view.findViewById(R.id.btn_sortDate);
        btn_sortValue = view.findViewById(R.id.btn_sortValue);

        radioGroup = view.findViewById(R.id.radioGroup);
        txt_bsAmount = view.findViewById(R.id.txt_numSamples);
        txt_bsAverage = view.findViewById(R.id.txt_avgSamples);
    }

    private void initOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { deleteSample(position); }});

        btn_sortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sortByDate(); }});

        btn_sortValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sortBySample(); }});

        btn_addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addNewSample((np1.getValue() + np2.getValue() / 10f), dataType); }
        });

        btn_numpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { numPadSample(dataType); }});

        btn_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lay_bottom.getVisibility() == View.GONE) {
                    lay_bottom.setVisibility(View.VISIBLE);
                    btn_hide.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_visibility_off_24dp, null));
                } else {
                    lay_bottom.setVisibility(View.GONE);
                    btn_hide.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_visibility_24dp, null));
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                np2.setValue(0);
                switch (checkedId) {
                    case R.id.rb_blood:
                        dataType = 0;
                        np1.setValue((int) MA.settings.getDefaultBS());
                        break;
                    case R.id.rb_fast:
                        dataType = 1;
                        np1.setValue(MA.settings.getFast());
                        break;
                    case R.id.rb_slow:
                        dataType = 2;
                        np1.setValue(MA.settings.getLongTerm());
                        break;
                }
            }
        });
    }

    private void updateInfo() {
        adapter.notifyDataSetChanged();
        float amount = 0;
        int total = 0;
        for (ModelData sample : MA.list) {
            if (sample.getType() == 0) {
                amount += sample.getAmount();
                total++;
            }
        }
        float avg = amount / total;
        txt_bsAmount.setText(getResources().getQuantityString(R.plurals.samples, total, total));
        txt_bsAverage.setText(getResources().getQuantityString(R.plurals.average,
                (int)avg, new DecimalFormat("#.##").format(avg)));
    }

    private void addNewSample(float value, int type) {
        MA.list.add(new ModelData(value, new Date(), type));
        isSortedDate = false;
        sortByDate();
        MA.IO.saveData(MA.list);
        MA.toast("New data added");
        updateInfo();
    }

    private void deleteSample(final int position) {
        ModelData data = (ModelData) listView.getItemAtPosition(position);
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Blood Measurement")
                .setMessage("Do you want to delete " + data.getDate() + " " + data.getAmount())
                //.setIcon(R.drawable.ic_action_bluetooth_connected)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MA.list.remove(position);
                        MA.IO.saveData(MA.list);
                        updateInfo();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void numPadSample(final int typeOfSample) {
        showKeyboard();
        int icon;
        switch (typeOfSample) {
            case 1:
                icon = (R.drawable.ic_rabbit);
                break;
            case 2:
                icon = (R.drawable.ic_turtle);
                break;
            default:
                icon = (R.drawable.ic_blood_drop);
                break;
        }

        final EditText input = new EditText(getContext());
        input.setRawInputType(InputType.TYPE_CLASS_PHONE);
        input.setSingleLine();
        input.requestFocus();
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Input Sample")
                .setMessage("Type in the amount:")
                .setView(input)
                .setIcon(icon)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString().replace(",", ".");
                        try {
                            addNewSample(Float.parseFloat(s), typeOfSample);
                            closeKeyboard();
                        } catch (NumberFormatException e) {
                            input.setText("");
                            closeKeyboard();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        closeKeyboard();
                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(ContextCompat.getDrawable(
                getContext(), R.drawable.btn_bg_selector));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(ContextCompat.getDrawable(
                getContext(), R.drawable.btn_bg_selector));
        //dialog.show();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) MA.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) MA.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void sortByDate() {
        if (isSortedDate) {
            isSortedDate = false;
            btn_sortDate.setText(R.string.date_sort_up);
            btn_sortValue.setText(R.string.value);
            Collections.sort(MA.list, new Comparator<ModelData>() {
                public int compare(ModelData obj1, ModelData obj2) {
                    return obj1.getDate().compareTo(obj2.getDate());
                }
            });
        } else {
            isSortedDate = true;
            btn_sortDate.setText(R.string.date_sort_down);
            btn_sortValue.setText(R.string.value);
            Collections.sort(MA.list, new Comparator<ModelData>() {
                public int compare(ModelData obj1, ModelData obj2) {
                    return obj2.getDate().compareTo(obj1.getDate());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void sortBySample() {
        if (!isSortedValue) {
            isSortedValue = true;
            btn_sortDate.setText(R.string.date);
            btn_sortValue.setText(R.string.value_sort_down);
            Collections.sort(MA.list, new Comparator<ModelData>() {
                public int compare(ModelData obj1, ModelData obj2) {
                    return Float.compare(obj2.getAmount(), obj1.getAmount());
                }
            });
        } else {
            isSortedValue = false;
            btn_sortDate.setText(R.string.date);
            btn_sortValue.setText(R.string.value_sort_up);
            Collections.sort(MA.list, new Comparator<ModelData>() {
                public int compare(ModelData obj1, ModelData obj2) {
                    return Float.compare(obj1.getAmount(), obj2.getAmount());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }
}