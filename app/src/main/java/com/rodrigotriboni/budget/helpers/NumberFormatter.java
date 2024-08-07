package com.rodrigotriboni.budget.helpers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class NumberFormatter {
    private NumberFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public static String formatCurrency(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return currencyFormatter.format(amount);
    }

    public static String formatDecimal(double amount) {
        return decimalFormat.format(amount);
    }
}
