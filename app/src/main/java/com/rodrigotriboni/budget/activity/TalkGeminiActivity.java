package com.rodrigotriboni.budget.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.analyzer.GeminiProChat;
import com.rodrigotriboni.budget.helpers.SharedViewModel;
import com.rodrigotriboni.budget.helpers.SpinnerUtil;
import com.rodrigotriboni.budget.pojos.ResponseCallback;

import java.util.Objects;

public class TalkGeminiActivity extends AppCompatActivity {

    private SharedViewModel sharedViewModel;

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
        }

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sharedViewModel.setSelectedMonth(position);
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

            model.getResponse(query, new ResponseCallback() {
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
    }
}
