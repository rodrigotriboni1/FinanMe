package com.rodrigotriboni.budget.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.ExpenseAdapter;
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

public class DetailsActivity extends AppCompatActivity {

    private ExpenseAdapter expenseAdapter;
    private List<ModelExpense> expenseList;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        RecyclerView rvExpenses = findViewById(R.id.rvTransactionList);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        rvExpenses.setAdapter(expenseAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Bank Name from Intent
        String bankName = getIntent().getStringExtra("bankName");
        if (bankName != null) {
            getSupportActionBar().setTitle(bankName);
        }

        // Set Toolbar Back Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (bankName != null) {
            fetchBankDetailsExpenses(bankName);
            Log.d("Rodrigo", "Fetching details for bank: " + bankName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchBankDetailsExpenses(String bankName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference expensesRef = database.getReference("expenses").child(bankName);

        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ModelExpense> expensesList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                        String dateString = dateSnapshot.getKey();
                        Date date;
                        try {
                            date = sdf.parse(dateString);
                        } catch (ParseException e) {
                            Log.e("fetchBankDetailsExpenses", "Error parsing date", e);
                            continue;
                        }
                        for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                            try {
                                Map<String, Object> expenseMap = (Map<String, Object>) expenseSnapshot.getValue();
                                if (expenseMap != null) {
                                    String key = expenseSnapshot.getKey();
                                    String item = (String) expenseMap.getOrDefault("item", "");
                                    String category = (String) expenseMap.getOrDefault("category", "");
                                    double amount = expenseMap.get("amount") instanceof Number ? ((Number) Objects.requireNonNull(expenseMap.get("amount"))).doubleValue() : 0.0;
                                    ModelExpense expense = new ModelExpense(dateString, item, category, amount, bankName, key);
                                    expensesList.add(expense);
                                }
                            } catch (Exception e) {
                                Log.e("fetchBankDetailsExpenses", "Error parsing expense", e);
                            }
                        }
                    }
                }
                expenseAdapter.updateExpenseList(expensesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchBankDetailsExpenses", "Failed to fetch expenses", error.toException());
            }
        });
    }



    private void setSupportActionBar(Toolbar toolbar) {
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
