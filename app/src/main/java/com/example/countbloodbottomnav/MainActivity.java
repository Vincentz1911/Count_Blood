package com.example.countbloodbottomnav;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.countbloodbottomnav.models.ModelData;
import com.example.countbloodbottomnav.models.ModelGraph;
import com.example.countbloodbottomnav.models.ModelAlarm;
import com.example.countbloodbottomnav.models.ModelSettings;

public class MainActivity extends AppCompatActivity {

    public FileStorage IO;
    public ModelGraph graph;
    public ModelSettings settings;
    public ArrayList<ModelData> list;
    public ArrayList<ModelAlarm> alarmlist;

    private NavController navController;
    private ActionBar sab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sab = getSupportActionBar();
        setupNavBar();

        IO = new FileStorage(getSharedPreferences("shared preferences", Context.MODE_PRIVATE));
        list = IO.loadData();
        settings = IO.loadSettings();
        graph = IO.loadGraph();
        alarmlist = IO.loadAlarms();
    }

    private void setupNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_settings, R.id.navigation_send_mail).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(navView, navController);

        sab.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
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
        sab.hide();
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
        switch (item.getItemId()) {
            case R.id.navigation_settings:
                navController.navigate(R.id.navigation_settings);
                return true;
            case R.id.navigation_send_mail:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) askPermissions();
                navController.navigate(R.id.navigation_send_mail);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermissions() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            Log.v("", "Permission is granted");
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            Log.v("", "Permission is granted");
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}