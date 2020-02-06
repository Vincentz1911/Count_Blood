package com.example.countbloodbottomnav.ui.data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class DataListAdapter extends ArrayAdapter<ModelData> {

    private List<ModelData> list;
    private float high, low;

    DataListAdapter(@NonNull Context context, ArrayList<ModelData> list, float high, float low) {
        super(context, 0, list);
        this.list = list;
        this.high = high;
        this.low = low;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.datalist_item, parent, false);

        ModelData data = list.get(position);

        TextView date = listItem.findViewById(R.id.txt_date);
        date.setText(MainActivity.datetime.format(data.getDate()));

        TextView sample = listItem.findViewById(R.id.txt_sample);
        //sample.setText(Float.toString(data.getAmount()));
        sample.setText(String.format("%s", data.getAmount()));  //Float.toString(data.getAmount()));
        ImageView imageView = listItem.findViewById(R.id.listImage);

        switch (data.getType()) {
            case 0:
                imageView.setImageResource(R.drawable.ic_blood_drop);
                if (data.getAmount() > high) sample.setTextColor(Color.BLUE);
                else if (data.getAmount() < low) sample.setTextColor(Color.RED);
                else sample.setTextColor(Color.GRAY);
                listItem.setBackgroundColor(Color.WHITE);
                break;
            case 1:
                imageView.setImageResource(R.drawable.ic_rabbit);
                sample.setTextColor(Color.GRAY);
                listItem.setBackgroundColor(getContext().getResources().getColor(R.color.colorFastInsulin));
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_turtle);
                sample.setTextColor(Color.GRAY);
                listItem.setBackgroundColor(getContext().getResources().getColor(R.color.colorSlowInsulin));
                break;
        }
        return listItem;
    }
}