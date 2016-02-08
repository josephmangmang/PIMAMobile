package com.pimamobile.pima.models;


public class LibraryItem {
    public static final int ALL_ITEMS = 40;
    public static final int DISCOUNTS = 41;
    public static final int CATEGORY = 42;

    private String name;
    private int type;

    public LibraryItem(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
