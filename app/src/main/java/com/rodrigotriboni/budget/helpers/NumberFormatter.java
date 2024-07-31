package com.rodrigotriboni.budget.helpers;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public static String format(double number) {
        return currencyFormat.format(number);
    }
    public static String format(float number) {
        return currencyFormat.format(number);
    }
}
