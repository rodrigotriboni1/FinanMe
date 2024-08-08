package com.rodrigotriboni.budget.models;

import java.util.List;

public class ModelUserSignUpData {

    private String email;
    private List<String> responses;
    private String name;

    public ModelUserSignUpData() {
        // Default constructor required for calls to DataSnapshot.getValue(ModelUserSignUpData.class)
    }

    public ModelUserSignUpData(String email, List<String> responses, String name) {
        this.email = email;
        this.responses = responses;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
