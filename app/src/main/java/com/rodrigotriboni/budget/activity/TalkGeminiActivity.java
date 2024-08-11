package com.rodrigotriboni.budget.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.analyzer.GeminiProChat;
import com.rodrigotriboni.budget.helpers.SharedViewModel;
import com.rodrigotriboni.budget.helpers.SpinnerUtil;
import com.rodrigotriboni.budget.pojos.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TalkGeminiActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;
    private String selectedMonthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_talk_gemini);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        TextInputEditText queryEditText = findViewById(R.id.queryEditText);
        Button sendQueryButton = findViewById(R.id.sendPromptButton);
        TextView responseTextView = findViewById(R.id.modelResponseTextView);
        ProgressBar progressBar = findViewById(R.id.sendPromptProgressBar);
        Spinner monthSpinner = findViewById(R.id.month_spinner);

        SpinnerUtil.setupMonthSpinner(monthSpinner, this, this);

        int selectedMonth = getIntent().getIntExtra("SELECTED_MONTH", -1);
        if (selectedMonth != -1) {
            sharedViewModel.setSelectedMonth(selectedMonth);
            monthSpinner.setSelection(selectedMonth);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            selectedMonthYear = String.format(Locale.getDefault(), "%02d-%d", selectedMonth + 1, year);
        }

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedViewModel.setSelectedMonth(position);
                int year = Calendar.getInstance().get(Calendar.YEAR);
                selectedMonthYear = String.format(Locale.getDefault(), "%02d-%d", position + 1, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        sendQueryButton.setOnClickListener(v -> {
            GeminiProChat model = new GeminiProChat();

            String query = queryEditText.getText().toString();
            progressBar.setVisibility(View.VISIBLE);

            responseTextView.setText("");
            queryEditText.setText("");

            loadExpensesAndIncomeFromFirebase(jsonData -> {
                String modifiedQuery = query + ", Data: " + jsonData + "]";
                model.getResponse(modifiedQuery, new ResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        responseTextView.setText(response);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(TalkGeminiActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            });
        });
    }

    private void loadExpensesAndIncomeFromFirebase(FirebaseDataCallback callback) {
        DatabaseReference expensesRef = FirebaseDatabase.getInstance().getReference("expenses");
        JSONObject combinedData = new JSONObject();

        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    JSONArray expensesArray = new JSONArray();

                    for (DataSnapshot bankSnapshot : snapshot.getChildren()) {
                        String bank = bankSnapshot.getKey();

                        for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                            for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                                String dateString = dateSnapshot.getKey();

                                for (DataSnapshot expenseSnapshot : dateSnapshot.getChildren()) {
                                    try {
                                        Map<String, Object> expenseMap = (Map<String, Object>) expenseSnapshot.getValue();
                                        if (expenseMap != null) {
                                            JSONObject expenseObject = new JSONObject();
                                            expenseObject.put("bank", bank);
                                            expenseObject.put("item", expenseMap.get("item"));
                                            expenseObject.put("category", expenseMap.get("category"));
                                            expenseObject.put("amount", expenseMap.get("amount"));
                                            expenseObject.put("date", dateString);
                                            expensesArray.put(expenseObject);
                                        }
                                    } catch (Exception e) {
                                        Log.e("loadExpensesFromFirebase", "Error parsing expense", e);
                                    }
                                }
                            }
                        }
                    }
                    combinedData.put("expenses", expensesArray);
                    loadIncomesFromFirebase(incomeData -> {
                        try {
                            combinedData.put("incomes", incomeData);
                            callback.onDataReady(combinedData.toString());
                        } catch (JSONException e) {
                            Log.e("loadIncomesFromFirebase", "Error combining data", e);
                        }
                    });
                } catch (JSONException e) {
                    Log.e("loadExpensesFromFirebase", "Error creating JSON", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadExpensesFromFirebase", "Database error", error.toException());
            }
        });
    }

    private void loadIncomesFromFirebase(FirebaseDataCallback callback) {
        DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("incomes");

        JSONArray incomesArray = new JSONArray();

        incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot bankSnapshot : snapshot.getChildren()) {
                    String bank = bankSnapshot.getKey();

                    for (DataSnapshot categorySnapshot : bankSnapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : categorySnapshot.getChildren()) {
                            String dateString = dateSnapshot.getKey();

                            for (DataSnapshot incomeSnapshot : dateSnapshot.getChildren()) {
                                try {
                                    Map<String, Object> incomeMap = (Map<String, Object>) incomeSnapshot.getValue();
                                    if (incomeMap != null) {
                                        JSONObject incomeObject = new JSONObject();
                                        incomeObject.put("bank", bank);
                                        incomeObject.put("item", incomeMap.get("item"));
                                        incomeObject.put("category", incomeMap.get("category"));
                                        incomeObject.put("amount", incomeMap.get("amount"));
                                        incomeObject.put("date", dateString);
                                        incomesArray.put(incomeObject);
                                    }
                                } catch (Exception e) {
                                    Log.e("loadIncomesFromFirebase", "Error parsing income", e);
                                }
                            }
                        }
                    }
                }
                callback.onDataReady(incomesArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("loadIncomesFromFirebase", "Database error", error.toException());
            }
        });
    }


    interface FirebaseDataCallback {
        void onDataReady(Object data);
    }
}
