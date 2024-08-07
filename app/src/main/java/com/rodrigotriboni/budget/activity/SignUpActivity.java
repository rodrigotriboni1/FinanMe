package com.rodrigotriboni.budget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.rodrigotriboni.budget.R;
import com.rodrigotriboni.budget.ui.signup.*;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private final int[] progressMap = {0, 16,32, 48, 64, 80, 100};
    private int currentProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.progressBar);

        ExtendedFloatingActionButton fabContinue = findViewById(R.id.extended_fab);
        fabContinue.setOnClickListener(view -> navigateToNextFragment());

        findViewById(R.id.toolbar_back_button).setOnClickListener(view -> navigateToPreviousFragment());

        showSignupFragment1();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this::updateProgressBasedOnBackStack);
    }

    private void navigateToNextFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        if (currentFragment instanceof SignUpFragment1) {
            showSignupFragment2();
            updateProgress(progressMap[1]);
        } else if (currentFragment instanceof SignUpFragment2) {
            showSignupFragment3();
            updateProgress(progressMap[2]);
        } else if (currentFragment instanceof SignUpFragment3) {
            showSignupFragment4();
            updateProgress(progressMap[3]);
        } else if (currentFragment instanceof SignUpFragment4) {
            showSignupFragment5();
            updateProgress(progressMap[4]);
        } else if (currentFragment instanceof SignUpFragment5) {
            showSignupFragment6();
            updateProgress(progressMap[5]);
        } else if (currentFragment instanceof SignUpFragment6) {
            updateProgress(progressMap[6]);
            Toast.makeText(this, "Sign-up process complete", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void navigateToPreviousFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            currentProgress = Math.max(0, currentProgress - 1);
            updateProgress(progressMap[currentProgress]);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void showSignupFragment1() {
        SignUpFragment1 signupFragment1 = new SignUpFragment1();
        replaceFragment(signupFragment1);
        currentProgress = 0;
        updateProgress(progressMap[currentProgress]);
    }

    private void showSignupFragment2() {
        SignUpFragment2 signupFragment2 = new SignUpFragment2();
        replaceFragment(signupFragment2);
        currentProgress = 1;
        updateProgress(progressMap[currentProgress]);
    }

    private void showSignupFragment3() {
        SignUpFragment3 signupFragment3 = new SignUpFragment3();
        replaceFragment(signupFragment3);
        currentProgress = 2;
        updateProgress(progressMap[currentProgress]);
    }

    private void showSignupFragment4() {
        SignUpFragment4 signupFragment4 = new SignUpFragment4();
        replaceFragment(signupFragment4);
        currentProgress = 3;
        updateProgress(progressMap[currentProgress]);
    }

    private void showSignupFragment5() {
        SignUpFragment5 signupFragment5 = new SignUpFragment5();
        replaceFragment(signupFragment5);
        currentProgress = 4;
        updateProgress(progressMap[currentProgress]);
    }

    private void showSignupFragment6() {
        SignUpFragment6 signupFragment6 = new SignUpFragment6();
        replaceFragment(signupFragment6);
        currentProgress = 5;
        updateProgress(progressMap[currentProgress]);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    private void updateProgressBasedOnBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if (backStackEntryCount >= 0 && backStackEntryCount < progressMap.length) {
            updateProgress(progressMap[backStackEntryCount]);
        }
    }
}
