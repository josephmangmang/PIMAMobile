package com.pimamobile.pima.models;

import com.pimamobile.pima.utils.Calculator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History {
    private List<Sale> sales;
    private List<Discount> discounts;
    private String totalAmount = "0";
    private String timeStamp;
    private String totalDiscount = "0";

    public History() {
    }

    public History(List<Sale> mSales, List<Discount> mDiscounts, String mTotalAmount, String mDate) {
        this.sales = mSales;
        this.discounts = mDiscounts;
        this.totalAmount = mTotalAmount;
        this.timeStamp = mDate;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public String getTotalAmount() {
        List<BigDecimal> totalItemAmount = new ArrayList<>();
        List<BigDecimal> totalItem = new ArrayList<>();
        for (int i = 0; i < sales.size(); i++) {
            totalItemAmount.add(new BigDecimal(sales.get(i).getItemTotalAmount()));
            totalItem.add(new BigDecimal(sales.get(i).getItemQuantity()));
        }
        totalAmount = Calculator.addAllData(totalItemAmount).toString();

        if (discounts.size() > 0) {
            List<BigDecimal> allDiscount = new ArrayList<>();
            for (int i = 0; i < discounts.size(); i++) {
                Calculator calculator = new Calculator(new BigDecimal(totalAmount), new BigDecimal(discounts.get(i).getDiscountAmount()));
                totalAmount = calculator.getDifference().toString();
                allDiscount.add(new BigDecimal(discounts.get(i).getDiscountAmount()));
            }
            totalDiscount = Calculator.addAllData(allDiscount).toString();
        } else {
            totalDiscount = "0";
        }
        return totalAmount;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String formatTimeStamp(String pattern) {
        Date date = new Date(timeStamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public String appendAllItem() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sales.size(); i++) {
            builder.append(sales.get(i).getItemName());
            if (sales.get(i).getItemQuantity() > 1) {
                builder.append(" ");
                builder.append("x" + sales.get(i).getItemQuantity());
            }
            builder.append(", ");
        }
        builder.append("and ");
        builder.append(sales.get(sales.size() - 1).getItemName());
        return builder.toString();
    }
}
