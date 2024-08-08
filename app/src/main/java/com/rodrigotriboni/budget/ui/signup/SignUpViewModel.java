package com.rodrigotriboni.budget.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<String> signupName = new MutableLiveData<>();
    private final List<String> selectedResponses = new ArrayList<>();
    private final Map<String, MutableLiveData<Integer>> selectedPositions = new HashMap<>();

    public LiveData<Integer> getSelectedPosition(String key) {
        if (!selectedPositions.containsKey(key)) {
            selectedPositions.put(key, new MutableLiveData<>(RecyclerView.NO_POSITION));
        }
        return selectedPositions.get(key);
    }

    public void setSelectedPosition(String key, int position) {
        if (!selectedPositions.containsKey(key)) {
            selectedPositions.put(key, new MutableLiveData<>(RecyclerView.NO_POSITION));
        }
        selectedPositions.get(key).setValue(position);
    }

    public void addResponse(String response) {
        selectedResponses.add(response);
    }

    public List<String> getSelectedResponses() {
        return new ArrayList<>(selectedResponses);
    }

    public void setSignupName(String name) {
        signupName.setValue(name);
    }

    public LiveData<String> getSignupName() {
        return signupName;
    }
}
