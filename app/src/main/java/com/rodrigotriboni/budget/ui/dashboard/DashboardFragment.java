package com.rodrigotriboni.budget.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.ExpenseAdapter;
import com.rodrigotriboni.budget.databinding.FragmentDashboardBinding;
import com.rodrigotriboni.budget.helpers.SharedViewModel;
import com.rodrigotriboni.budget.models.ModelExpense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ExpenseAdapter expenseAdapter;
    private DashboardViewModel dashboardViewModel;
    private SharedViewModel sharedViewModel;

    private final Calendar selectedCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        RecyclerView rvTransactionList = root.findViewById(R.id.rvTransactionList);
        rvTransactionList.setLayoutManager(new LinearLayoutManager(getContext()));
        expenseAdapter = new ExpenseAdapter(new ArrayList<>());
        rvTransactionList.setAdapter(expenseAdapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer monthPosition) {
                selectedCalendar.set(Calendar.MONTH, monthPosition);
                loadExpensesFromFirebase();
            }
        });

        return root;
    }

    private void loadExpensesFromFirebase() {
        DatabaseReference expensesRef = FirebaseDatabase.getInstance().getReference("expenses");
        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ModelExpense> expensesList = new ArrayList<>();
                int selectedMonth = sharedViewModel.getSelectedMonth().getValue();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, selectedMonth);

                // Iterate through all banks under "expenses"
                for (DataSnapshot bankSnapshot : snapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();

                    // Iterate through all categories for the bank
                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        // Iterate through all dates for the category
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            String dateString = dateSnapshot.getKey();
                            // Parse date string to get the month
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            Date date;
                            try {
                                date = sdf.parse(dateString);
                            } catch (ParseException e) {
                                Log.e("loadExpensesFromFirebase", "Error parsing date", e);
                                continue; // Skip this expense if parsing fails
                            }
                            Calendar expenseDate = Calendar.getInstance();
                            expenseDate.setTime(date);

                            // Check if the expense date matches the selected month
                            if (expenseDate.get(Calendar.MONTH) == selectedMonth) {
                                // Iterate through all expense entries for the date
                                for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                                    try {
                                        Map<String, Object> expenseMap = (Map<String, Object>) expenseSnapshot.getValue();
                                        if (expenseMap != null) {
                                            ModelExpense expense = mapToModelExpense(expenseMap);
                                            expensesList.add(expense);
                                        }
                                    } catch (Exception e) {
                                        Log.e("loadExpensesFromFirebase", "Error parsing expense", e);
                                    }
                                }
                            }
                        }
                    }
                }
                expenseAdapter.updateExpenseList(expensesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadExpensesFromFirebase", "Database error", error.toException());
            }
        });
    }

    private ModelExpense mapToModelExpense(Map<String, Object> expenseMap) {
        String date = (String) expenseMap.getOrDefault("date", "");
        String item = (String) expenseMap.getOrDefault("item", "");
        String category = (String) expenseMap.getOrDefault("category", "");
        double amount = expenseMap.get("amount") instanceof Number ? ((Number) Objects.requireNonNull(expenseMap.get("amount"))).doubleValue() : 0.0;
        String bank = (String) expenseMap.getOrDefault("bank", "");
        return new ModelExpense(date, item, category, amount, bank);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}