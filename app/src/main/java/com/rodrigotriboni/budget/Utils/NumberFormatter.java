package com.rodrigotriboni.budget.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00"); // Format for two decimal places

    public static String formatCurrency(double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return currencyFormatter.format(amount);
    }

    public static String formatDecimal(double amount) {
        return decimalFormat.format(amount);
    }
}