package com.example.countbloodbottomnav.models;

import java.util.ArrayList;
import java.util.Date;

public class ModelGraph {

    private int xGrid, xDiv, yGrid, yDiv, numDays;
    private String[] xText;
    private Date start, end;
    private ArrayList<ModelGraphData> list;
    private int id_radioGroup;
    private boolean blood, average, fast, slow;

    public ModelGraph() { }

    public ModelGraph(int xGrid, int xDiv, int yGrid, int yDiv, String[] xText, int numDays) {
        this.xGrid = xGrid;
        this.xDiv = xDiv;
        this.yGrid = yGrid;
        this.yDiv = yDiv;
        this.numDays = numDays;
        this.xText = xText;
    }

    public int getxGrid() { return xGrid; }
    public void setxGrid(int xGrid) { this.xGrid = xGrid; }

    public int getxDiv() { return xDiv; }
    public void setxDiv(int xDiv) { this.xDiv = xDiv; }

    public int getyGrid() { return yGrid; }
    public void setyGrid(int yGrid) { this.yGrid = yGrid; }

    public int getyDiv() { return yDiv; }
    public void setyDiv(int yDiv) { this.yDiv = yDiv; }

    public String[] getxText() { return xText; }
    public void setxText(String[] xText) { this.xText = xText; }

    public int getNumDays() { return numDays; }
    public void setNumDays(int numDays) { this.numDays = numDays; }

    public Date getStart() { return start; }
    public void setStart(Date start) { this.start = start; }

    public Date getEnd() { return end; }
    public void setEnd(Date end) { this.end = end; }

    public ArrayList<ModelGraphData> getList() { return list; }
    public void setList(ArrayList<ModelGraphData> list) { this.list = list; }

    public int getId_radioGroup() { return id_radioGroup; }
    public void setId_radioGroup(int id_radioGroup) { this.id_radioGroup = id_radioGroup; }

    public boolean isBlood() { return blood; }
    public void setBlood(boolean blood) { this.blood = blood; }

    public boolean isAverage() { return average; }
    public void setAverage(boolean average) { this.average = average; }

    public boolean isFast() { return fast; }
    public void setFast(boolean fast) { this.fast = fast; }

    public boolean isSlow() { return slow; }
    public void setSlow(boolean slow) { this.slow = slow; }
}