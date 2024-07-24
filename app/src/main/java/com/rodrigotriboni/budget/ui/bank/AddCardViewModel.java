package com.rodrigotriboni.budget.ui.bank;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rodrigotriboni.budget.models.ModelCreditCard;

public class AddCardViewModel extends ViewModel {

    private MutableLiveData<ModelCreditCard> creditCard;

    public AddCardViewModel() {
        creditCard = new MutableLiveData<>();
    }

    public LiveData<ModelCreditCard> getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String cardNumber, String cardHolderName, String expiryDate) {
        ModelCreditCard newCard = new ModelCreditCard(cardNumber, cardHolderName, expiryDate);
        creditCard.setValue(newCard);
    }
}
