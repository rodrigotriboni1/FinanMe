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

public class GeminiFlashChat {
    public void getResponse(String query, ResponseCallback callback) {
        GenerativeModelFutures model = getModel();

        Content content = new Content.Builder().
                addText(query).
                addText("If the month and year are not provided, I will display the following message: 'No data available for this period.'\\n\\nIf the user requests a general summary, I will display:\\n\\nOverall Summary:\\n\\nTotal Expenses: Over the past few months, your average monthly expenses amounted to $[Average Total Amount].\\nTop Spending Categories: The categories where you consistently spend the most include:\\n[Category 1]\\n[Category 2]\\n[Category 3]\\nIncomes:\\nYour average monthly income is around $[Average Income Amount], with the most common sources being:\\n\\n[Income Source 1]\\n[Income Source 2]\\nSpending Patterns:\\nYour spending patterns indicate that you tend to allocate a significant portion of your budget towards [Category 1]. Consider reviewing these expenses to identify potential savings.\\n\\nSavings and Goals:\\nIf you haven’t set any financial goals yet, now might be a good time to start. Clear goals like saving for a big purchase, an emergency fund, or paying off debt can guide your spending decisions.\\n\\nAdditional Tips:\\n\\nMonitor Trends: Regularly reviewing your spending and income trends can help you stay on track and make informed decisions.\\nBudgeting: Using a budgeting tool can help you manage your finances more effectively and ensure you’re allocating your money where it matters most.\\nIf the month and year are provided, I will display the following detailed summary:\\n\\n\\\"I’ve analyzed your expenses and incomes for [Month] [Year]. Here’s a detailed summary:\\n\\nTotal Expenses:\\nYour total expenses for [Month] [Year] amounted to $[Total Amount].\\n\\nTop Categories:\\nThe three categories where you spent the most were:\\n\\n[Category 1]: $[Amount]\\n[Category 2]: $[Amount]\\n[Category 3]: $[Amount]\\nIncomes:\\nHere’s a breakdown of your recorded incomes for [Month] [Year]:\\n\\n[Income Source 1]: $[Amount]\\n[Income Source 2]: $[Amount]\\nTrends:\\nUnfortunately, I don’t have access to your expenses from the previous month ([Previous Month] [Year]) for comparison. However, if you can provide them, I’d be happy to identify any trends.\\n\\nRecommendations:\\n\\n[Category 1]: A significant portion of your budget goes to [Category 1]. You might consider [specific suggestions, e.g., meal prepping, exploring cheaper alternatives].\\n[Category 2]: This category indicates [brief description of spending pattern]. Ensure these expenses are necessary and look for ways to optimize or reduce them.\\n[Category 3]: Since this is a broad category, try to identify recurring expenses within it. Cutting back on non-essential items could help you save more.\\nAdditional Tips:\\n\\nTrack Your Spending: Use a budgeting app or spreadsheet to monitor your expenses closely. This will help you pinpoint areas where you can improve.\\nSet Financial Goals: Clear goals like saving for a big purchase or paying off debt can motivate you to adjust your spending habits.\\nIf you’d like to analyze your expenses for a different month or need further financial advice, I’m here to help.").
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
        String apiKey = BuildConfigApi.apiKey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT,
                BlockThreshold.ONLY_HIGH);

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.9f;
        configBuilder.topK = 16;
        configBuilder.topP = 0.1f;
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                apiKey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );

        return GenerativeModelFutures.from(gm);
    }
}