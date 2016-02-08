/*
 * Copyright (c) 2016.  PIMA Mobile
 */

package com.pimamobile.pima.models;

public class SoldItem {
    private int id;
    private String itemName;
    private String amount;

    public SoldItem(int id, String itemName, String amount) {
        this.id = id;
        this.itemName = itemName;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
