package com.rodrigotriboni.budget.models;

import android.graphics.drawable.Drawable;

public class ModelSignUpQuestion {
    private String questionText;
    private Drawable icon;

    public ModelSignUpQuestion(String questionText, Drawable icon) {
        this.questionText = questionText;
        this.icon = icon;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}