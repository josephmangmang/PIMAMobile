package com.pimamobile.pima.models;


import java.util.List;

public class Group {
    private String name;
    private String totalAmount;
    private List<Sale> sales;
    private List<Discount> discounts;
    private boolean isDiscount = false;

    public Group(String name, String totalAmount) {
        this.name = name;
        this.totalAmount = totalAmount;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }


    public boolean isDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(boolean isDiscount) {
        this.isDiscount = isDiscount;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
}
