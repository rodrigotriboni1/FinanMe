package com.rodrigotriboni.budget.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.BudgetItemAdapter;
import com.rodrigotriboni.budget.models.ModelBudget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BudgetActivity extends AppCompatActivity {


    private BudgetItemAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EditText editTextAmount;
        Spinner spinnerCategory;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_budget);

        editTextAmount = findViewById(R.id.editText_amount);
        spinnerCategory = findViewById(R.id.spinner_category);
        Button buttonAddItem = findViewById(R.id.button_add_item);
        RecyclerView recyclerViewBudget = findViewById(R.id.recyclerView_budget);

        databaseReference = FirebaseDatabase.getInstance().getReference("budget_items");

        recyclerViewBudget.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BudgetItemAdapter(this, new ArrayList<>());
        recyclerViewBudget.setAdapter(adapter);
        loadDataFromFirebase();

        buttonAddItem.setOnClickListener(view -> {
            String amountString = editTextAmount.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            if (!amountString.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountString);
                    ModelBudget item = new ModelBudget(amount, category);
                    databaseReference.push().setValue(item);
                    editTextAmount.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(BudgetActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(BudgetActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelBudget> modelBudgets = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelBudget item = snapshot.getValue(ModelBudget.class);
                    modelBudgets.add(item);
                }
                adapter.updateItems(modelBudgets);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BudgetActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}