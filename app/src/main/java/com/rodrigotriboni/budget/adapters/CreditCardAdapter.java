package com.rodrigotriboni.budget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.models.CreditCard;

import java.util.List;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder> {

    private List<CreditCard> creditCardList;

    public static class CreditCardViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardNumber, tvCardHolderName, tvExpiryDate;

        public CreditCardViewHolder(View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.tv_card_number);
            tvCardHolderName = itemView.findViewById(R.id.tv_card_holder_name);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
        }
    }

    public CreditCardAdapter(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList;
    }

    @NonNull
    @Override
    public CreditCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_credit_card, parent, false);
        return new CreditCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCardViewHolder holder, int position) {
        CreditCard creditCard = creditCardList.get(position);
        holder.tvCardNumber.setText(creditCard.getCardNumber());
        holder.tvCardHolderName.setText(creditCard.getCardHolderName());
        holder.tvExpiryDate.setText(creditCard.getExpiryDate());
    }

    @Override
    public int getItemCount() {
        return creditCardList.size();
    }
}
