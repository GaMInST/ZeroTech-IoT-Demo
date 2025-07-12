package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressIndicator = findViewById(R.id.progress_indicator);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void performRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

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
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Show loading state
        setLoadingState(true);

        String countryCode = "1"; // US
        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "", countryCode, 1,
                new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        runOnUiThread(() -> {
                            setLoadingState(false);
                            showErrorMessage("Failed to send verification code: " + error);
                        });
                    }

                    @Override
                    public void onSuccess() {
                        // For simplicity, we are not handling verification code in this demo.
                        // In a real app, you would ask the user to input the code.
                        // We will proceed with registration directly.
                        ThingHomeSdk.getUserInstance().registerAccountWithEmail(countryCode, email, password,
                                "", new IRegisterCallback() {
                                    @Override
                                    public void onSuccess(User user) {
                                        runOnUiThread(() -> {
                                            setLoadingState(false);
                                            showSuccessMessage("Registration successful! Please sign in.");
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        });
                                    }

                                    @Override
                                    public void onError(String code, String error) {
                                        runOnUiThread(() -> {
                                            setLoadingState(false);
                                            showErrorMessage("Registration failed: " + error);
                                        });
                                    }
                                });
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            btnRegister.setEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);
            btnRegister.setText("Creating Account...");
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
            etConfirmPassword.setEnabled(false);
        } else {
            btnRegister.setEnabled(true);
            progressIndicator.setVisibility(View.GONE);
            btnRegister.setText("Create Account");
            etEmail.setEnabled(true);
            etPassword.setEnabled(true);
            etConfirmPassword.setEnabled(true);
        }
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
