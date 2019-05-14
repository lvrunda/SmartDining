package com.biot.smartdining;

public class MenuItem {
    private String fName;
    private int cost;

    public MenuItem(String fName) {
        this.fName = fName;
        this.cost = 100;
    }

    public MenuItem(String fName, int cost) {
        this.fName = fName;
        this.cost = cost;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
