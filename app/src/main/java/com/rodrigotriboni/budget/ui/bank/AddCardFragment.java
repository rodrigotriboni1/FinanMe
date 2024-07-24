package com.rodrigotriboni.budget.ui.bank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rodrigotriboni.budget.R;

public class AddCardFragment extends Fragment {

    private EditText etCardNumber;
    private EditText etCardHolderName;
    private EditText etExpiryDate;
    private Button btnSaveCard;

    private AddCardViewModel addCardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_card, container, false);

        etCardNumber = view.findViewById(R.id.et_card_number);
        etCardHolderName = view.findViewById(R.id.et_card_holder_name);
        etExpiryDate = view.findViewById(R.id.et_expiry_date);
        btnSaveCard = view.findViewById(R.id.btn_save_card);

        addCardViewModel = new ViewModelProvider(this).get(AddCardViewModel.class);

        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });

        return view;
    }

    private void saveCard() {
        String cardNumber = etCardNumber.getText().toString().trim();
        String cardHolderName = etCardHolderName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();

        if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(cardHolderName) || TextUtils.isEmpty(expiryDate)) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        addCardViewModel.setCreditCard(cardNumber, cardHolderName, expiryDate);
        Toast.makeText(getActivity(), "Card Saved", Toast.LENGTH_SHORT).show();
    }
}
