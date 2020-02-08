package com.example.countbloodbottomnav.models;

import java.io.Serializable;
import java.util.Date;

public class ModelAlarm implements Serializable {

    private String title, message;
    private int requestCode, type, repeat;
    private Date date;

    public ModelAlarm(Date date) {
        this.date = date;
    }

    public ModelAlarm(String title, String msg, int requestCode, Date date, int type, int repeat) {
        this.title = title;
        this.message = msg;
        this.requestCode = requestCode;
        this.date = date;
        this.type = type;
        this.repeat = repeat;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRequestCode() { return requestCode; }
    public void setRequestCode(int requestCode) { this.requestCode = requestCode; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getRepeat() { return repeat; }
    public void setRepeat(int repeat) { this.repeat = repeat; }
}