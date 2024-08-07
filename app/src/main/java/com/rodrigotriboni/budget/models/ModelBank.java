package com.rodrigotriboni.budget.models;

public class ModelBank {

    private String name;
    private int totalAmountIncome, totalAmountExpenses;



    public ModelBank(String name, int totalAmountIncome, int totalAmountExpenses) {
        this.name = name;
        this.totalAmountIncome = totalAmountIncome;
        this.totalAmountExpenses = totalAmountExpenses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalAmountIncome() {
        return totalAmountIncome;
    }

    public void setTotalAmountIncome(int totalAmountIncome) {
        this.totalAmountIncome = totalAmountIncome;
    }

    public int getTotalAmountExpenses() {
        return totalAmountExpenses;
    }

    public void setTotalAmountExpenses(int totalAmountExpenses) {
        this.totalAmountExpenses = totalAmountExpenses;
    }
}
