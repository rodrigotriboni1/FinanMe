package com.rodrigotriboni.budget.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.activity.LoginActivity;
import com.rodrigotriboni.budget.activity.SignUpActivity;

public class SignUpFragment1 extends Fragment {

    private EditText signupName;
    private SignUpViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup_1, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        signupName = root.findViewById(R.id.signup_name);
        TextView loginRedirectText = root.findViewById(R.id.loginRedirectText);
        viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        signupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                validateInput();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSignupName(s.toString().trim());
                validateInput();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return root;
    }

    private void validateInput() {
        String input = signupName.getText().toString().trim();
        boolean isValid = !input.isEmpty();

        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).setContinueButtonEnabled(isValid);
        }
    }
}
