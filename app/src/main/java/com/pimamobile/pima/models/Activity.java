/*
 * Copyright (c) 2016.  PIMA Mobile
 */

package com.pimamobile.pima.models;

import java.util.List;

public class Activity {
    private String date;
    private String time;
    private double salesAmount;
    private List<SoldItem> soldItems;

    public List<SoldItem> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(List<SoldItem> soldItems) {
        this.soldItems = soldItems;
    }

    public Activity(String time, double salesAmount, String date) {
        this.time = time;
        this.salesAmount = salesAmount;
        this.date = date;
    }

    public Activity(String time, List<SoldItem> soldItems, double salesAmount, String date) {
        this.time = time;
        this.soldItems = soldItems;
        this.salesAmount = salesAmount;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(double salesAmount) {
        this.salesAmount = salesAmount;
    }
}
