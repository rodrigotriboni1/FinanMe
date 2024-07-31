package com.rodrigotriboni.budget.models;

public class ModelExpense {
    private String date;
    private String item;
    private String category;
    private double amount;
    private String bank;

    // No-argument constructor required for Firebase
    public ModelExpense() {
    }

    public ModelExpense(String date, String item, String category, double amount, String bank) {
        this.date = date;
        this.item = item;
        this.category = category;
        this.amount = amount;
        this.bank = bank;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
