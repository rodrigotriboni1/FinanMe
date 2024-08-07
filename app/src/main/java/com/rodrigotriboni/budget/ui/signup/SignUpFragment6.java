package com.rodrigotriboni.budget.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.activity.LoginActivity;

import java.util.Objects;

public class SignUpFragment6 extends Fragment {

    private FirebaseAuth auth;
    private EditText signupEmail;
    private EditText signupPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup_6, container, false);

        auth = FirebaseAuth.getInstance();
        signupPassword = root.findViewById(R.id.signup_password);
        Button signupButton = root.findViewById(R.id.signup_button);
        TextView loginRedirectText = root.findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(view -> {
            String user = signupEmail.getText().toString().trim();
            String pass = signupPassword.getText().toString().trim();

            if (user.isEmpty()) {
                signupEmail.setError("Email cannot be empty");
            } else if (pass.isEmpty()) {
                signupPassword.setError("Password cannot be empty");
            } else {
                auth.createUserWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Signup Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            } else {
                                Toast.makeText(getActivity(), "Signup Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        return root;
    }
}
