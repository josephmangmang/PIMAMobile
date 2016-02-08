package com.pimamobile.pima.models;


public class Item {
    private int id;

    private int position;
    private String itemName;
    private String itemPrice;
    private String itemCategory;

    public void setId(int id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public Item(){

    }
    public Item(int id, String itemName, String itemCategory, String itemPrice) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;

    }

    public Item(String itemName, String itemCategory, String itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;

    }


    public String getItemCategory() {
        return itemCategory;
    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
