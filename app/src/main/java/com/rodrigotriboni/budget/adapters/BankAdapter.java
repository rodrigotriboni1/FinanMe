package com.rodrigotriboni.budget.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.activity.DetailsActivity;
import com.rodrigotriboni.budget.models.ModelBank;
import com.rodrigotriboni.budget.helpers.NumberFormatter;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

    private final List<ModelBank> modelBankList;
    private final Context context;

    public BankAdapter(Context context, List<ModelBank> modelBankList) {
        this.modelBankList = modelBankList;
        this.context = context;
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
        holder.cvBankAccount.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("bankName", modelBank.getName());
            context.startActivity(intent);
        });
        holder.cvBankAccount.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Do you really want to delete this item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference expensesReference = FirebaseDatabase.getInstance().getReference("expenses").child(modelBank.getName());
                        expensesReference.removeValue((error, ref) -> {
                            if (error == null) {
                                DatabaseReference incomesReference = FirebaseDatabase.getInstance().getReference("incomes").child(modelBank.getName());
                                incomesReference.removeValue((incomeError, incomeRef) -> {
                                    if (incomeError == null) {
                                        modelBankList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, modelBankList.size());
                                    }
                                });
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
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

        private final TextView tvBankName;
        private final TextView tvTotalAmountExpenses;
        private final TextView tvTotalAmountIncome;
        private final CardView cvBankAccount;

        public BankViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBankName = itemView.findViewById(R.id.tvBankName);
            tvTotalAmountIncome = itemView.findViewById(R.id.tvAmountIncome);
            tvTotalAmountExpenses = itemView.findViewById(R.id.tvAmountExpenses);
            cvBankAccount = itemView.findViewById(R.id.cvBankAccount);
        }

        public void bind(ModelBank modelBank) {
            tvBankName.setText(modelBank.getName());
            String formattedAmount = NumberFormatter.formatCurrency(modelBank.getTotalAmountIncome());
            String formattedExpenses = NumberFormatter.formatCurrency(modelBank.getTotalAmountExpenses());
            tvTotalAmountIncome.setText(formattedAmount);
            tvTotalAmountExpenses.setText(formattedExpenses);
        }
    }
}
