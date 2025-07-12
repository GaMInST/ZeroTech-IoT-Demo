package com.zerotechiot.eg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CircularProgressIndicator progressIndicator;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in - faster check
        sharedPreferences = getSharedPreferences("ZeroTechPrefs", MODE_PRIVATE);
        if (isUserLoggedIn()) {
            // User is already logged in, go directly to main activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        progressIndicator = findViewById(R.id.progress_indicator);

        // Pre-fill email if available
        String savedEmail = sharedPreferences.getString("user_email", "");
        if (!savedEmail.isEmpty()) {
            etEmail.setText(savedEmail);
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Enhanced validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        String countryCode = "1"; // US
        ThingHomeSdk.getUserInstance().loginWithEmail(countryCode, email, password, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    setLoadingState(false);

                    // Save login session with enhanced data
                    saveLoginSession(email, user.getUid());

                    showSuccessMessage("Login successful! Welcome back.");

                    // Navigate to main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String code, String error) {
                runOnUiThread(() -> {
                    setLoadingState(false);
                    showErrorMessage("Login failed: " + error);
                });
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);
            btnLogin.setText("Signing In...");
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
        } else {
            btnLogin.setEnabled(true);
            progressIndicator.setVisibility(View.GONE);
            btnLogin.setText("Sign In");
            etEmail.setEnabled(true);
            etPassword.setEnabled(true);
        }
    }

    private void saveLoginSession(String email, String uid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.putString("user_uid", uid);
        editor.putLong("login_timestamp", System.currentTimeMillis());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.success_green))
                .setTextColor(getResources().getColor(android.R.color.white))
                .show();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error_red))
                .setTextColor(getResources().getColor(android.R.color.white))
                .show();
    }
}
