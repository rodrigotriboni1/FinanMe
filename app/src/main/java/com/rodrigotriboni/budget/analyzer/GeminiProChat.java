package com.rodrigotriboni.budget.analyzer;

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
import com.rodrigotriboni.budget.pojos.ResponseCallback;

import java.util.Collections;
import java.util.concurrent.Executor;

public class GeminiProChat {
    public void getResponse(String query, ResponseCallback callback) {
        GenerativeModelFutures model = getModel();

        Content content = new Content.Builder().
                addText(query).
                addText("I’ve analyzed your expenses for {Month} {Year}. Here’s a detailed summary:\n\nTotal Expenses and Total Incomes:\nYour total expenses for {Month} {Year} amounted to ${TotalAmount}.\n\nTop Categories:\nThe three categories where you spent the most were:\n\n{Category1}: ${Amount1}\n{Category2}: ${Amount2}\n{Category3}: ${Amount3}\n\nTrends:\nUnfortunately, I don’t have access to your expenses from the previous month ({PreviousMonth} {Year}) for comparison. However, if you can provide them, I’d be happy to identify any trends.\n\nRecommendations:\n\n{Category1}: It seems a significant portion of your budget goes to {Category1}. You might consider {specificSuggestions1, e.g., meal prepping, exploring cheaper alternatives}.\n{Category2}: This category indicates {briefDescription2 of spending pattern}. Ensure these expenses are necessary and look for ways to optimize or reduce them.\n{Category3}: Since this is a broad category, try to identify recurring expenses within it. Cutting back on non-essential items could help you save more.\n\nAdditional Tips:\n\nTrack your spending: Consider using a budgeting app or a spreadsheet to keep a closer eye on your expenses, which will help you identify areas where you can improve.\nSet financial goals: Clear goals like saving for a big purchase or paying off debt can motivate you to adjust your spending accordingly.\n\nIf you’d like to analyze your expenses for a different month or need further assistance, feel free to reach out!\n").
                build();
        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callback.onResponse(resultText);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                throwable.printStackTrace();
                callback.onError(throwable);
            }
        }, executor);

    }
    private GenerativeModelFutures getModel() {
        String apiKey = BuildConfig.apiKey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.ONLY_HIGH);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-pro",
                apiKey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );

        return GenerativeModelFutures.from(gm);
    }
}