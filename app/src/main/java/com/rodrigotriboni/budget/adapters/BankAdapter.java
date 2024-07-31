package com.rodrigotriboni.budget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.helpers.NumberFormatter;
import com.rodrigotriboni.budget.models.ModelBank;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private final List<ModelBank> modelBankList;

    public BankAdapter(List<ModelBank> modelBankList) {
        this.modelBankList = modelBankList;
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_account, parent, false);
        return new BankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        ModelBank modelBank = modelBankList.get(position);
        holder.bind(modelBank);
    }

    @Override
    public int getItemCount() {
        return modelBankList.size();
    }

    public void updateBankList(List<ModelBank> newModelBankList) {
        modelBankList.clear();
        modelBankList.addAll(newModelBankList);
        notifyDataSetChanged();
    }

    static class BankViewHolder extends RecyclerView.ViewHolder {

        private final TextView bankNameTextView;
        private final TextView totalAmountTextView;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            bankNameTextView = itemView.findViewById(R.id.tvBankName);
            totalAmountTextView = itemView.findViewById(R.id.tvAmount);
        }

        public void bind(ModelBank modelBank) {
            bankNameTextView.setText(modelBank.getName());
            String formattedAmount = NumberFormatter.format(modelBank.getTotalAmount());
            totalAmountTextView.setText(formattedAmount);
        }
    }
}
