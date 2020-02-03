package com.example.countbloodbottomnav.ui;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.countbloodbottomnav.MainActivity;
import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

class SendMailFragment extends Fragment {

    //region UI
    private Button btn_send;
    private EditText input_recipient, input_mail;
    private View view;
    //endregion
    private MainActivity MA;
    private File csvFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_send_mail, container, false);
        MA = ((MainActivity) getActivity());
        initUI();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { writeCSV(); sendEmail(); }});
        return view;
    }

    private void initUI() {
        btn_send = view.findViewById(R.id.btn_send_mail);
        input_mail = view.findViewById(R.id.input_mail);
        input_mail.setText(mail());
        input_recipient = view.findViewById(R.id.input_send_to);
        input_recipient.setText(MA.settings.getEmailRecipiant());
    }

    private String mail() {
        Date first = MA.list.get(0).getDate();
        Date last = MA.list.get(MA.list.size() - 1).getDate();
        return "I hereby send my diabetes data, ranging from " + first + " to " + last +
                " with a total of " + MA.list.size() + " entries";
    }

    private void writeCSV() {
        String[] dataType = {"Bloodsugar", "FastInsulin", "SlowInsulin"};
        csvFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "DiabetesDataList.txt");
        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile))) {
            for (ModelData sample : MA.list)
                csvWriter.println(sample.getDate() + ", " + sample.getAmount() + ", " + dataType[sample.getType()]);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void sendEmail() {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());

        Intent emailIntent = new Intent(Intent.ACTION_SEND)
        .setType("vnd.android.cursor.dir/email")
        .putExtra(Intent.EXTRA_EMAIL, input_recipient.getText().toString())
        .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(csvFile))
        .putExtra(Intent.EXTRA_SUBJECT, "Glucose and Insulin results")
        .putExtra(Intent.EXTRA_TEXT, input_mail.getText().toString());

        MA.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
