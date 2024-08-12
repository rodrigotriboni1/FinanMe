package com.rodrigotriboni.budget.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.models.ModelBank;
import com.rodrigotriboni.budget.models.ModelBudget;

import java.util.List;


public class BudgetItemAdapter extends RecyclerView.Adapter<BudgetItemAdapter.BudgetItemViewHolder> {

    private final Context context;
    private List<ModelBudget> budgetItems;

    public BudgetItemAdapter(Context context, List<ModelBudget> budgetItems) {
        this.context = context;
        this.budgetItems = budgetItems;
    }

    public void updateItems(List<ModelBudget> newItems) {
        this.budgetItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.budget_item_layout, parent, false);
        return new BudgetItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetItemViewHolder holder, int position) {
        ModelBudget item = budgetItems.get(position);
        holder.textViewAmount.setText(String.format("$%.2f", item.amount));
        holder.textViewCategory.setText(item.category);
    }

    @Override
    public int getItemCount() {
        return budgetItems.size();
    }

    public static class BudgetItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewAmount;
        public TextView textViewCategory;

        public BudgetItemViewHolder(View itemView) {
            super(itemView);
            textViewAmount = itemView.findViewById(R.id.textView_amount);
            textViewCategory = itemView.findViewById(R.id.textView_category);
        }
    }
}