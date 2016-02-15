package com.pimamobile.pima.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.pimamobile.pima.utils.Calculator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class History implements Parcelable {
    private List<Sale> sales;
    private List<Discount> discounts;
    private String totalAmount = "0";
    private long timeStamp;
    private String totalDiscount = "0";
    private String receiptNumber;

    public History() {
    }

    public History(List<Sale> mSales, List<Discount> mDiscounts, long timeStamp) {
        this.sales = mSales;
        this.discounts = mDiscounts;
        this.timeStamp = timeStamp;
    }

    public History(List<Sale> mSales, List<Discount> mDiscounts, long timeStamp, String receiptNumber) {
        this.sales = mSales;
        this.discounts = mDiscounts;
        this.timeStamp = timeStamp;
        this.receiptNumber = receiptNumber;
    }

    public History(Parcel source) {
        source.readList(sales, List.class.getClassLoader());
        source.readList(discounts, List.class.getClassLoader());
        this.totalAmount = source.readString();
        this.timeStamp = source.readLong();
        this.totalDiscount = source.readString();
        this.receiptNumber = source.readString();
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

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCalculateTotalAmount() {
        List<BigDecimal> totalItemAmount = new ArrayList<>();
        List<BigDecimal> totalItem = new ArrayList<>();
        for (int i = 0; i < sales.size(); i++) {
            totalItemAmount.add(new BigDecimal(sales.get(i).calculateItemTotalAmount()));
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

    public String getCalculateTotalDiscountAmount() {
        if (discounts.size() > 0) {
            List<BigDecimal> allDiscount = new ArrayList<>();
            for (int i = 0; i < discounts.size(); i++) {
                allDiscount.add(new BigDecimal(discounts.get(i).getDiscountAmount()));
            }
            totalDiscount = Calculator.addAllData(allDiscount).toString();
        } else {
            totalDiscount = "0";
        }
        return totalDiscount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
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
        if (sales.size() > 1) {
            builder.append("and ");
            builder.append(sales.get(sales.size() - 1).getItemName());
        }
        return builder.toString();
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(sales);
        dest.writeList(discounts);
        dest.writeString(totalAmount);
        dest.writeLong(timeStamp);
        dest.writeString(totalDiscount);
        dest.writeString(receiptNumber);
    }
}
