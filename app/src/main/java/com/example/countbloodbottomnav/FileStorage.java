package com.example.countbloodbottomnav;

import android.content.SharedPreferences;

import com.example.countbloodbottomnav.models.ModelAlarm;
import com.example.countbloodbottomnav.models.ModelData;
import com.example.countbloodbottomnav.models.ModelGraph;
import com.example.countbloodbottomnav.models.ModelSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class FileStorage {

    private SharedPreferences sp;

    FileStorage(SharedPreferences sharedPreferences) {
        this.sp = sharedPreferences;
    }

    public void saveData(ArrayList<ModelData> list) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("samplelist", new Gson().toJson(list)).apply();
    }

    ArrayList<ModelData> loadData() {
        String json = sp.getString("samplelist", null);
        Type type = new TypeToken<ArrayList<ModelData>>() {
        }.getType();
        ArrayList<ModelData> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public void saveSettings(ModelSettings settings) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("settings", new Gson().toJson(settings)).apply();
    }

    ModelSettings loadSettings() {
        String json = sp.getString("settings", null);
        Type type = new TypeToken<ModelSettings>() {
        }.getType();
        ModelSettings settings = new Gson().fromJson(json, type);
        if (settings == null) settings = new ModelSettings(4f, 10f, 6f,
                8, 15, 10, "recipient@mail.com");
        return settings;
    }

    public void saveAlarms(ArrayList<ModelAlarm> list) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("alarmlist", new Gson().toJson(list)).apply();
    }

    ArrayList<ModelAlarm> loadAlarms() {
        String json = sp.getString("alarmlist", null);
        Type type = new TypeToken<ArrayList<ModelAlarm>>() {
        }.getType();
        ArrayList<ModelAlarm> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public void saveGraphSetup(ModelGraph graph) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("graph", new Gson().toJson(graph)).apply();
    }

    ModelGraph loadGraph() {
        String json = sp.getString("graph", null);
        Type type = new TypeToken<ModelGraph>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }
}