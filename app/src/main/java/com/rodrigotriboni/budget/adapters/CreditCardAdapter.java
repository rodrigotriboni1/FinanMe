package com.rodrigotriboni.budget.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.models.ModelCreditCard;

import java.util.List;

public class CreditCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CARD = 0;
    private static final int VIEW_TYPE_ADD_CARD = 1;
    private FragmentActivity activity;
    private List<ModelCreditCard> modelCreditCardList;

    public static class CreditCardViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCardNumber, tvCardHolderName, tvExpiryDate;

        public CreditCardViewHolder(View itemView) {
            super(itemView);
            tvCardNumber = itemView.findViewById(R.id.tv_card_number);
            tvCardHolderName = itemView.findViewById(R.id.tv_card_holder_name);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
        }
    }

    public static class AddCardViewHolder extends RecyclerView.ViewHolder {
        public CardView cvCreditCard;

        public AddCardViewHolder(View itemView) {
            super(itemView);
            cvCreditCard = itemView.findViewById(R.id.cvCreditCard);
        }
    }

    public CreditCardAdapter(FragmentActivity activity, List<ModelCreditCard> modelCreditCardList) {
        this.activity = activity;
        this.modelCreditCardList = modelCreditCardList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == modelCreditCardList.size()) {
            return VIEW_TYPE_ADD_CARD;
        }
        return VIEW_TYPE_CARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD_CARD) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_add_credit_card, parent, false);
            return new AddCardViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_credit_card, parent, false);
            return new CreditCardViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CreditCardViewHolder) {
            ModelCreditCard modelCreditCard = modelCreditCardList.get(position);
            ((CreditCardViewHolder) holder).tvCardNumber.setText(modelCreditCard.getCardNumber());
            ((CreditCardViewHolder) holder).tvCardHolderName.setText(modelCreditCard.getCardHolderName());
            ((CreditCardViewHolder) holder).tvExpiryDate.setText(modelCreditCard.getExpiryDate());
        } else if (holder instanceof AddCardViewHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle "Add Card" view click
                    Log.d("CreditCardAdapter", "Add Card clicked");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return modelCreditCardList.size() + 1;
    }
}
