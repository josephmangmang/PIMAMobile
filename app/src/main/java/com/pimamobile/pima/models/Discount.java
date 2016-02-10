package com.pimamobile.pima.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Discount implements Parcelable {
    public static final String DISCOUNT_NAME = "discount_name";
    public static final String DISCOUNT_AMOUNT = "discount_amount";
    public static final String DISCOUNT_IS_PERCENTAGE = "discount_is_percentage";
    private String discountName;
    private String discountAmount;
    private boolean isPercentage;
    private boolean applyToItem;

    public Discount() {

    }

    public Discount(String discountName, String discountAmount, boolean isPercentage) {

        this.isPercentage = isPercentage;
        this.discountAmount = discountAmount;
        this.discountName = discountName;
    }

    public Discount(Parcel source) {
        this.discountAmount = source.readString();
        this.discountName = source.readString();
        this.isPercentage = source.readByte() != 0;
        this.applyToItem = source.readByte() != 0;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setIsPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public boolean isApplyToItem() {
        return applyToItem;
    }

    public void setApplyToItem(boolean applyToItem) {
        this.applyToItem = applyToItem;
    }
    public JSONObject getJsonObject(){
        JSONObject object = new JSONObject();
        try {
            object.put(DISCOUNT_NAME, discountName);
            object.put(DISCOUNT_AMOUNT, discountAmount);
            object.put(DISCOUNT_IS_PERCENTAGE, isPercentage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(discountAmount);
        dest.writeString(discountName);
        dest.writeByte((byte) (isPercentage ? 1 : 0));
        dest.writeByte((byte) (applyToItem ? 1 : 0));
    }

    public static final Parcelable.Creator<Discount> CREATOR = new Creator<Discount>() {
        @Override
        public Discount createFromParcel(Parcel source) {
            return new Discount(source);
        }

        @Override
        public Discount[] newArray(int size) {
            return new Discount[size];
        }
    };
}
