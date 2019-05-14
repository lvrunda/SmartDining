package com.biot.smartdining;

public class DiningHallItem {
    // Store the name of the dining hall
    private String dName;
    // Store the number of people
    private int pCount;

    private String id;

    public DiningHallItem(String dName, int pCount, String id) {
        this.dName = dName;
        this.pCount = pCount;
        this.id = id;
    }

    public DiningHallItem(String dName, int pCount) {
        this.dName = dName;
        this.pCount = pCount;
    }

    public String getdName() {
        return dName;
    }

    public int getpCount() {
        return pCount;
    }

    public String getId() {
        return id;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public void setpCount(int pCount) {
        this.pCount = pCount;
    }


}
