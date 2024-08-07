package com.rodrigotriboni.budget.ui.signup;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.adapters.SignUpQuestionAdapter;
import com.rodrigotriboni.budget.models.ModelSignUpQuestion;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment2 extends Fragment {

    private RecyclerView recyclerView;
    private SignUpQuestionAdapter adapter;
    private SignUpViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_2, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        recyclerView = view.findViewById(R.id.signup_questions_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        List<ModelSignUpQuestion> questions = new ArrayList<>();
        Drawable bankIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_colored_bank, getContext().getTheme());
        Drawable creditcardIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_colored_credit_card, getContext().getTheme());
        Drawable cashIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_colored_money, getContext().getTheme());

        questions.add(new ModelSignUpQuestion("Bank Transfer", bankIcon));
        questions.add(new ModelSignUpQuestion("Cash", cashIcon));
        questions.add(new ModelSignUpQuestion("Debit Card", creditcardIcon));
        questions.add(new ModelSignUpQuestion("Credit Card", creditcardIcon));

        SignUpQuestionAdapter adapter = new SignUpQuestionAdapter(questions, viewModel, "fragment2");

        recyclerView.setAdapter(adapter);

        return view;
    }
}
