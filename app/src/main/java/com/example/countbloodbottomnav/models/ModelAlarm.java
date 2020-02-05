package com.example.countbloodbottomnav.models;

import java.io.Serializable;
import java.util.Date;

public class ModelAlarm implements Serializable {

    private String title, message;
    private int requestCode, icon, repeatInterval;
    private Date date;
    private boolean isRepeating;

    public ModelAlarm() { }

    public ModelAlarm(String title, String message, int requestCode, Date date, int icon,
                      boolean isRepeating, int repeatInterval) {
        this.title = title;
        this.message = message;
        this.requestCode = requestCode;
        this.date = date;
        this.icon = icon;
        this.isRepeating = isRepeating;
        this.repeatInterval = repeatInterval;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRequestCode() { return requestCode; }
    public void setRequestCode(int requestCode) { this.requestCode = requestCode; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getIcon() { return icon; }
    public void setIcon(int icon) { this.icon = icon; }

    public boolean isRepeating() { return isRepeating; }
    public void setRepeating(boolean repeating) { isRepeating = repeating; }

    public int getRepeatInterval() { return repeatInterval; }
    public void setRepeatInterval(int repeatInterval) { this.repeatInterval = repeatInterval; }
}