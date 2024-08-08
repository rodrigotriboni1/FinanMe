package com.rodrigotriboni.budget.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.activity.MainActivity;
import com.rodrigotriboni.budget.models.ModelUserSignUpData;

import java.util.List;

public class SignUpFragment6 extends Fragment {

    private SignUpViewModel viewModel;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private EditText signup_email;
    private EditText signup_password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_6, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        signup_email = view.findViewById(R.id.signup_email);
        signup_password = view.findViewById(R.id.signup_password);
        Button signup_button = view.findViewById(R.id.signup_button);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signup_button.setOnClickListener(v -> {
            String email = signup_email.getText().toString().trim();
            String password = signup_password.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String name = viewModel.getSignupName().getValue();
                                List<String> responses = viewModel.getSelectedResponses();
                                saveUserData(user.getUid(), email, name, responses);
                            }
                        } else {
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }

    private void saveUserData(String userId, String email, String name, List<String> responses) {
        ModelUserSignUpData modelUserSignUpData = new ModelUserSignUpData(email, responses, name);

        databaseReference.child(userId).setValue(modelUserSignUpData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "User data saved.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Failed to save user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}