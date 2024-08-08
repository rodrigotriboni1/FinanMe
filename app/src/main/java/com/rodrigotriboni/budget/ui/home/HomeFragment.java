package com.rodrigotriboni.budget.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.activity.BudgetActivity;
import com.rodrigotriboni.budget.activity.TalkGeminiActivity;
import com.rodrigotriboni.budget.databinding.FragmentHomeBinding;
import com.rodrigotriboni.budget.helpers.NumberFormatter;
import com.rodrigotriboni.budget.helpers.SharedViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private DatabaseReference expensesRef, incomeRef, usersRef;
    private final Calendar selectedCalendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), monthPosition -> {
            selectedCalendar.set(Calendar.MONTH, monthPosition);
            fetchAndDisplayIncome();
            fetchAndDisplayExpenses();
        });



        LinearLayout tvMoreTalkGemini = root.findViewById(R.id.tvMoreTalkGemini);
        setupClickListener(tvMoreTalkGemini, TalkGeminiActivity.class);

        LinearLayout tvMoreBudget = root.findViewById(R.id.tvMoreBudget);
        setupClickListener(tvMoreBudget, BudgetActivity.class);

        binding.tvUsername.setVisibility(View.GONE);


        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        firebaseAuth = FirebaseAuth.getInstance();
        fetchAndDisplayUserName();
        return root;
    }

    private void setupClickListener(View view, final Class<?> activityClass) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), activityClass);
            startActivity(intent);
        });
    }

    private void fetchAndDisplayExpenses() {
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses");

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalExpenses = 0;

                SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
                String selectedMonth = sdf.format(selectedCalendar.getTime());

                for (DataSnapshot bankSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            String date = dateSnapshot.getKey();

                            if (date.substring(3).equals(selectedMonth)) {
                                for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                                    Double amount = expenseSnapshot.child("amount").getValue(Double.class);
                                    if (amount != null && amount >= 0) {
                                        totalExpenses += amount;
                                    }
                                }
                            }
                        }
                    }
                }

                if (binding != null) {
                    String formattedCurrency = NumberFormatter.formatCurrency(totalExpenses);
                    binding.tvExpensesAmount.setText(formattedCurrency);
                    binding.tvExpensesAmount.setVisibility(totalExpenses > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void fetchAndDisplayIncome() {
        incomeRef = FirebaseDatabase.getInstance().getReference("incomes");

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalIncome = 0;

                SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
                String selectedMonth = sdf.format(selectedCalendar.getTime());

                for (DataSnapshot bankSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            String date = dateSnapshot.getKey();

                            if (date.substring(3).equals(selectedMonth)) {
                                for (DataSnapshot incomeSnapshot : dateSnapshot.getChildren()) {
                                    Double amount = incomeSnapshot.child("amount").getValue(Double.class);
                                    if (amount != null) {
                                        totalIncome += amount;
                                    }
                                }
                            }
                        }
                    }
                }

                if (binding != null) {
                    String formattedCurrency = NumberFormatter.formatCurrency(totalIncome);
                    binding.tvIncomeAmount.setText(formattedCurrency);
                    binding.tvIncomeAmount.setVisibility(totalIncome > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void fetchAndDisplayUserName() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    if (binding != null) {
                        binding.tvUsername.setVisibility(View.VISIBLE);
                        TextView tvUsername = binding.getRoot().findViewById(R.id.tvUsername);
                        tvUsername.setText(String.format("Hi, %s", userName));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}