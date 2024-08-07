package com.rodrigotriboni.budget.analyzer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodrigotriboni.budget.pojos.ResponseCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class GeminiFlash {
    private GenerativeModelFutures getModel() {
        String apiKey = BuildConfig.apiKey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.ONLY_HIGH);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 1f;
        configBuilder.topK = 64;
        configBuilder.topP = 0.95f;
        configBuilder.maxOutputTokens = 8192;
        configBuilder.responseMimeType = "application/json";
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                apiKey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );

        return GenerativeModelFutures.from(gm);
    }

    public void getResponse(String query, ResponseCallback callback) {
        GenerativeModelFutures model = getModel();
        Content content = new Content.Builder()
                .addText(query)
                .addText("Expense Classification:\n\nGroup expenses by category: Parse the JSON data to group expenses by their category.\nCalculate the total spent per category: Sum up the amounts for each category to get the total expenditure.\nIncome Identification:\n\nExtract and categorize incomes: Parse the JSON data to identify income entries.\nCalculate total income: Sum up all identified income amounts.\nTemporal Analysis:\n\nAnalyze spending patterns over time: Use the date field to analyze expenses over days, months, and years.\nDetect peaks and correlate with events: Identify significant peaks in spending and correlate them with specific events or time periods.\nDeviation Detection:\n\nIdentify significant increases in expenses: Compare expenses over different periods to detect significant increases.\nSuggest possible reasons for changes: Provide possible explanations for these changes, such as holidays, special events, or unexpected costs.\n\nIdentify what bank this transaction was born.\nSavings Suggestions:\n\nProvide actionable tips: Based on the analysis, offer tips for reducing spending in categories with high expenditures or unexpected increases.\nPerformance Report:\n\nGenerate charts and tables: Visualize the data to show expense distribution, temporal trends, and savings suggestions.\nHighlight key insights: Summarize the most important findings in an easily digestible format.\nContinuous Monitoring:\n\nPeriodic analyses: Set up a schedule for regular expense analyses (e.g., monthly, quarterly).\nReal-time alerts: Implement a system to send alerts for significant deviations or unexpected expenses.\nIntegration with Bank Account PDF:\n\nIdentify bank account information: Extract bank account data from PDF statements.\nCross-reference with JSON data: Ensure that expenses and incomes in the JSON data match the transactions in the bank PDF.\nHere’s how the JSON structure can be expanded to include incomes with dates in the format dd/MM/yyyy:\n\n{\n    \"Expenses\": [\n        {\n            \"date\": \"11/05/2024\",\n            \"item\": \"Latam Airlin*Dkzbrc019 - Installment 2/4\",\n            \"category\": \"Travel\",\n            \"amount\": 76.49,\n            \"bank\": \"nubank\"\n        },\n        {\n            \"date\": \"12/05/2024\",\n            \"item\": \"Groceries\",\n            \"category\": \"Food\",\n            \"amount\": 50.00,\n            \"bank\": \"nubank\"\n        }\n    ],\n    \"Incomes\": [\n        {\n            \"date\": \"10/05/2024\",\n            \"item\": \"Salary\",\n            \"category\": \"Job\",\n            \"amount\": 1500.00,\n            \"bank\": \"itau\"\n        },\n        {\n            \"date\": \"15/05/2024\",\n            \"item\": \"Freelance Work\",\n            \"category\": \"Job\",\n            \"amount\": 300.00,\n            \"bank\": \"itau\"\n        }\n    ]\n}\n")
                .build();

        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callback.onResponse(resultText);

                try {
                    JSONObject jsonObject = new JSONObject(resultText);

                    // Process expenses
                    JSONArray expensesArray = jsonObject.getJSONArray("Expenses");
                    processTransactions(expensesArray, "expenses");

                    // Process incomes
                    JSONArray incomesArray = jsonObject.getJSONArray("Incomes");
                    processTransactions(incomesArray, "incomes");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

    private void processTransactions(JSONArray jsonArray, String transactionType) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String bank = jsonObject.optString("bank", "unknown");
                DatabaseReference bankReference = FirebaseDatabase.getInstance().getReference(transactionType).child(bank);

                bankReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot existingDataSnapshot = task.getResult();
                        Map<String, Map<String, Map<String, Object>>> categorizedData = new HashMap<>();

                        if (existingDataSnapshot.exists()) {
                            for (DataSnapshot categorySnapshot : existingDataSnapshot.getChildren()) {
                                String category = sanitizeKey(categorySnapshot.getKey());
                                Map<String, Map<String, Object>> existingCategoryData = (Map<String, Map<String, Object>>) categorySnapshot.getValue();
                                categorizedData.put(category, existingCategoryData != null ? existingCategoryData : new HashMap<>());
                            }
                        }

                        for (int j = 0; j < jsonArray.length(); j++) {
                            try {
                                JSONObject transactionObject = jsonArray.getJSONObject(j);
                                String category = sanitizeKey(transactionObject.optString("category", "Unknown"));

                                double amount = transactionObject.optDouble("amount");
                                if (Double.isNaN(amount) || Double.isInfinite(amount)) {
                                    Log.e("InvalidAmount", "Skipping invalid amount: " + amount);
                                    continue;
                                }

                                String date = transactionObject.optString("date").replace("/", "-");

                                Map<String, Object> data = parseJsonObject(transactionObject);

                                categorizedData.putIfAbsent(category, new HashMap<>());
                                Map<String, Map<String, Object>> dateMap = categorizedData.get(category);
                                dateMap.putIfAbsent(date, new HashMap<>());
                                dateMap.get(date).put(bankReference.push().getKey(), data);

                            } catch (JSONException e) {
                                Log.e("JSONParsingError", "Error parsing JSON object at index " + j, e);
                            }
                        }

                        bankReference.setValue(categorizedData)
                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "Data merged and updated successfully"))
                                .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Failed to update data", e));

                    } else {
                        Log.e("FirebaseRead", "Failed to read existing data", task.getException());
                    }
                });
            } catch (JSONException e) {
                Log.e("JSONParsingError", "Error parsing JSON object at index " + i, e);
            }
        }
    }

    private Map<String, Object> parseJsonObject(JSONObject jsonObject) throws JSONException {
        Map<String, Object> data = new HashMap<>();
        data.put("date", jsonObject.optString("date"));
        data.put("item", jsonObject.optString("item"));
        data.put("amount", jsonObject.optDouble("amount"));
        data.put("bank", jsonObject.optString("bank"));
        return data;
    }

    private String sanitizeKey(String key) {
        return key.replaceAll("[./#$\\[\\]]", "_");
    }

}
