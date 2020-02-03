package com.example.countbloodbottomnav.models;

import java.util.Date;

public class ModelData {
    private float amount;
    private Date date;
    private int type;

    public ModelData(float amount, Date date, int type) {
        this.amount = amount;
        this.date = date;
        this.type = type;
    }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
}
