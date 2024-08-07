package com.rodrigotriboni.budget.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.helpers.NumberFormatter;
import com.rodrigotriboni.budget.models.ModelExpense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<ModelExpense> expenseList;
    private final Context context;

    public ExpenseAdapter(Context context, List<ModelExpense> expenseList) {
        this.expenseList = expenseList;
        this.context = context;
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
        holder.tvAmount.setText(NumberFormatter.formatCurrency(expense.getAmount()));
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir")
                    .setMessage("Deseja realmente excluir este item?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("expenses").child(expense.getBank()).child(expense.getKey());
                        databaseReference.removeValue((error, ref) -> {
                            if (error == null) {
                                expenseList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, expenseList.size());
                            }
                        });
                    })
                    .setNegativeButton("Não", null)
                    .show();
            return true;
        });
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
        this.expenseList.clear();
        this.expenseList.addAll(expenseList);
        notifyDataSetChanged();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvDate;
        TextView tvDescription;
        TextView tvAmount;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
