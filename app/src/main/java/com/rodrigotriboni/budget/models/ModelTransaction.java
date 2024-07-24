package com.rodrigotriboni.budget.models;

import java.util.Date;

public class ModelTransaction {
    private int id;
    private double amount;
    private String description;
    private Date date;

    public ModelTransaction(int id, double amount, String description, Date date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}