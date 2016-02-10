package com.pimamobile.pima.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.pimamobile.pima.utils.Calculator;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Sale implements Parcelable {

    public static final String SALES_ITEM_NAME = "item_name";
    public static final String SALES_ITEM_PRICE = "item_price";
    public static final String SALES_ITEM_TOTAL_AMOUNT = "item_total_amount";
    public static final String SALES_ITEM_NOTE = "item_note";
    private static final String SALES_ITEM_QUANTITY = "item_quantity";
    private int id;
    private String itemName;
    private String itemPrice;
    private String itemTotalAmount;
    private String itemNote;
    private List<Discount> discounts = new ArrayList<>();
    private int itemQuantity;
    private boolean isDiscount = false;

    public Sale() {
    }

    public Sale(boolean isDiscount, String discountName, String amount) {
        this.isDiscount = isDiscount;
        this.itemName = discountName;
        this.itemPrice = amount;
    }

    public Sale(String itemName, String itemPrice, String itemNote, List<Discount> discounts, int itemQuantity, boolean hasDiscount) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemNote = itemNote;
        this.discounts = discounts;
        this.itemQuantity = itemQuantity;
        this.isDiscount = hasDiscount;
    }

    public Sale(Parcel source) {
        this.itemName = source.readString();
        this.itemNote = source.readString();
        this.itemPrice = source.readString();
        this.itemTotalAmount = source.readString();
        this.itemQuantity = source.readInt();
        this.isDiscount = source.readByte() != 0;
        source.readList(discounts, List.class.getClassLoader());
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemTotalAmount() {
        Calculator calculator = new Calculator(new BigDecimal(itemPrice), new BigDecimal(itemQuantity));
        /*
        BigDecimal tempAmount = new BigDecimal(BigInteger.ZERO);
        if (discounts.size() != 0) {
            for (int i = 0; i < discounts.size(); i++) {
                tempAmount = Calculator.getPercentage(new BigDecimal(discounts.get(i).getDiscountAmount()), calculator.getProduct());
            }
        }
        */
        return calculator.getProduct().toString();
    }


    public String getItemNote() {
        return itemNote;
    }

    public void setItemNote(String itemNote) {
        this.itemNote = itemNote;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        this.isDiscount = discount;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public JSONObject getJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(SALES_ITEM_NAME, itemName);
            object.put(SALES_ITEM_PRICE, itemPrice);
            object.put(SALES_ITEM_QUANTITY, itemQuantity);
            object.put(SALES_ITEM_NOTE, itemNote);
            object.put(SALES_ITEM_TOTAL_AMOUNT, getItemTotalAmount());
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return object;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "itemName='" + itemName + '\'' +
                ", itemTotalAmount='" + itemTotalAmount + '\'' +
                '}';
    }

    public static final Parcelable.Creator<Sale> CREATOR = new Creator<Sale>() {
        @Override
        public Sale createFromParcel(Parcel source) {
            return new Sale(source);
        }

        @Override
        public Sale[] newArray(int size) {
            return new Sale[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(itemNote);
        dest.writeString(itemPrice);
        dest.writeString(itemTotalAmount);
        dest.writeInt(itemQuantity);
        dest.writeByte(((byte) (isDiscount ? 1 : 0)));
        dest.writeList(discounts);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
