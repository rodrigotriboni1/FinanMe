package com.rodrigotriboni.budget.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();

    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int month) {
        selectedMonth.setValue(month);
    }
}