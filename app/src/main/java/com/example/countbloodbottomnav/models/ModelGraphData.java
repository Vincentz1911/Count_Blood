package com.example.countbloodbottomnav.models;

public class ModelGraphData {

    private float x;
    private float y;
    private int p;

    public ModelGraphData(float x, float y, int p) {
        this.x = x;
        this.y = y;
        this.p = p;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public int getP() { return p; }
    public void setP(int p) { this.p = p; }
}
