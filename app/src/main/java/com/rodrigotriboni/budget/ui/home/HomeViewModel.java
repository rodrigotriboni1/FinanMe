package com.rodrigotriboni.budget.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rodrigotriboni.budget.adapters.CreditCardAdapter;
import com.rodrigotriboni.budget.models.CreditCard;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<CreditCard>> creditCardList;

    public HomeViewModel() {
        creditCardList = new MutableLiveData<>();
        loadCreditCards();
    }

    public LiveData<List<CreditCard>> getCreditCardList() {
        return creditCardList;
    }

    private void loadCreditCards() {
        List<CreditCard> cards = new ArrayList<>();
        cards.add(new CreditCard("**** **** **** 1234", "Rodrigo M Triboni", "12/23"));
        cards.add(new CreditCard("**** **** **** 5678", "Rodrigo M Triboni", "11/24"));
        cards.add(new CreditCard("**** **** **** 5678", "Rodrigo M Triboni", "11/24"));
        creditCardList.setValue(cards);
    }
}
