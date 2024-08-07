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

public class SignUpFragment1 extends Fragment {

    private FirebaseAuth auth;
    private EditText signupName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup_1, container, false);

        auth = FirebaseAuth.getInstance();
        signupName = root.findViewById(R.id.signup_name);
        TextView loginRedirectText = root.findViewById(R.id.loginRedirectText);

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        return root;
    }
}
