package com.rodrigotriboni.budget.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.helpers.NumberFormatter;
import com.rodrigotriboni.budget.models.ModelExpense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<ModelExpense> expenseList;

    public ExpenseAdapter(List<ModelExpense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_transactions_status_transfer, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_transactions_status_sell, parent, false);
        }
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ModelExpense expense = expenseList.get(position);
        holder.tvCategory.setText(expense.getCategory());
        holder.tvDate.setText(expense.getDate());
        holder.tvDescription.setText(expense.getItem());
        holder.tvAmount.setText(NumberFormatter.format(expense.getAmount()));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return expenseList.get(position).getAmount() < 0 ? 1 : 0;
    }

    public void updateExpenseList(List<ModelExpense> expenseList) {
        this.expenseList = expenseList;
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvDescription, tvAmount;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
