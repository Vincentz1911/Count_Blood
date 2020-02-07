package com.example.countbloodbottomnav;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import com.example.countbloodbottomnav.models.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public FileStorage IO;
    public ModelSettings settings;
    public ModelGraph graph;
    public ModelAlarm alarm;
    public ModelData data;
    public ArrayList<ModelAlarm> alarm_list;
    public ArrayList<ModelData> data_list;

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    public static SimpleDateFormat datetime = new SimpleDateFormat
            ("HH:mm EEE d. MMM", Locale.getDefault());
    public static SimpleDateFormat date = new SimpleDateFormat
            ("EEE d. MMM", Locale.getDefault());

    public String f2str(float f) {
        return String.format(Locale.getDefault(), "%.02f", (float) f); }

    public Drawable drawable() {
        return ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_blood_drop); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        createNavBar();
        createNotificationChannels();
        IO = new FileStorage(getSharedPreferences("storage", Context.MODE_PRIVATE));
        settings = IO.loadSettings();
        data_list = IO.loadData();
        graph = IO.loadGraph();
        alarm_list = IO.loadAlarms();
    }

    private void createNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.navigation_data,
                R.id.navigation_graph,
                R.id.navigation_alarm,
                R.id.navigation_settings,
                R.id.navigation_email).build();

        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(
                this, navController, appBarConfig);

        NavigationUI.setupWithNavController(navView, navController);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(
                getResources().getDrawable(R.drawable.gradient));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            fullscreen(navView);
    }

    private void fullscreen(BottomNavigationView navView) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        navView.setVisibility(View.GONE);
    }

    public void toast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);

        switch (item.getItemId()) {
            case R.id.navigation_settings:
                navController.navigate(R.id.navigation_settings);
                return true;
            case R.id.navigation_email:
                if (data_list.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) askPermissions();
                    navController.navigate(R.id.navigation_email);
                    return true;
                } else toast("No data to send!");
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
            Log.v("", "Permission is granted");
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
            Log.v("", "Permission is granted");
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel1);
                manager.createNotificationChannel(channel2);
            }
        }
    }
}