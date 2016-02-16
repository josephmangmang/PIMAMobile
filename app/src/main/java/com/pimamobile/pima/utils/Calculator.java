package com.pimamobile.pima.utils;


import android.util.Log;

import com.pimamobile.pima.models.Discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Calculator {
    private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;
    private static final int DECIMALS = 2;
    private static final int EXTRA_DECIMALS = 4;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final String TAG = "Calculator";

    private BigDecimal mAmountOne;
    private BigDecimal mAmountTwo;


    public Calculator() {
        this(new BigDecimal("0"), new BigDecimal("0"));

    }

    public Calculator(BigDecimal amountOne, BigDecimal amountTwo) {
        mAmountOne = amountOne;
        mAmountTwo = amountTwo;
    }

    public static BigDecimal addAllData(List<BigDecimal> totalItemAmount) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < totalItemAmount.size(); i++) {
            sum = sum.add(totalItemAmount.get(i));
        }
        return sum;
    }

    // add
    public BigDecimal getSum() {
        return mAmountOne.add(mAmountTwo);
    }

    // subtract
    public BigDecimal getDifference() {
        return mAmountOne.subtract(mAmountTwo);
    }

    // divide
    public BigDecimal getQuotient() {
        return mAmountOne.divide(mAmountTwo, DECIMALS, RoundingMode.HALF_UP);
    }

    // Multiply
    public BigDecimal getProduct() {
        return mAmountOne.multiply(mAmountTwo);
    }


    public BigDecimal getItemAmount(BigDecimal price, BigDecimal quantity, BigDecimal percentDiscount) {
        BigDecimal totalAmount = price.multiply(quantity);
        BigDecimal percentage = getPercentage(percentDiscount, totalAmount);
        return totalAmount.subtract(percentage);
    }

    // pecentage in decimal = percent / 100 * of
    public static BigDecimal getPercentage(BigDecimal percent, BigDecimal of) {
        return percent.divide(ONE_HUNDRED).multiply(of);
    }

    public static BigDecimal rounded(BigDecimal mRoundedNumber) {
        return mRoundedNumber.setScale(DECIMALS, ROUNDING_MODE);
    }

    public static BigDecimal addAmount(BigDecimal amountOne, BigDecimal amountTwo) {
        return amountOne.add(amountTwo);
    }

    public static BigDecimal deductAmount(BigDecimal amountOne, BigDecimal amountTwo) {
        return amountOne.subtract(amountTwo);
    }
}
