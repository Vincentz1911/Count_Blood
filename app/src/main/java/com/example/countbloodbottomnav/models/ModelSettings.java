package com.example.countbloodbottomnav.models;

public class ModelSettings {

    private float lowBS, highBS, defaultBS;
    private int fastNormal, slowEarly, slowLate;
    private String emailRecipiant;

    public ModelSettings(float lowBS, float highBS, float defaultBS, int fast,
                         int longTerm, int longTermEve, String emailRecipiant) {
        this.lowBS = lowBS;
        this.highBS = highBS;
        this.defaultBS = defaultBS;
        this.fastNormal = fast;
        this.slowEarly = longTerm;
        this.slowLate = longTermEve;
        this.emailRecipiant = emailRecipiant;
    }

    public float getLowBS() { return lowBS; }
    public void setLowBS(float lowBS) { this.lowBS = lowBS; }

    public float getHighBS() { return highBS; }
    public void setHighBS(float highBS) { this.highBS = highBS; }

    public float getDefaultBS() { return defaultBS; }
    public void setDefaultBS(float defaultBS) { this.defaultBS = defaultBS; }

    public int getFast() { return fastNormal; }
    public void setFast(int fast) { this.fastNormal = fast; }

    public int getLongTerm() { return slowEarly; }
    public void setLongTerm(int longTerm) { this.slowEarly = longTerm; }

    public int getLongTermEve() { return slowLate; }
    public void setLongTermEve(int longTermEve) { this.slowLate = longTermEve; }

    public String getEmailRecipiant() { return emailRecipiant; }
    public void setEmailRecipiant(String emailRecipiant) { this.emailRecipiant = emailRecipiant; }
}