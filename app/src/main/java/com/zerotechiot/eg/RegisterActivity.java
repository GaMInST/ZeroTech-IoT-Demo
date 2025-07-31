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
import com.google.android.material.textfield.TextInputLayout; // Added for verificationCodeLayout
import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword, etVerificationCode; // Added etVerificationCode
    private Button btnRegister;
    private TextView tvLogin;
    private CircularProgressIndicator progressIndicator;
    private TextInputLayout verificationCodeLayout; // Added for verificationCodeLayout
    private TextInputLayout confirmPasswordLayout; // To hide it later

    private enum RegistrationState {
        INITIAL, // Expecting email, password, confirm password
        AWAITING_VERIFICATION // Expecting verification code
    }
    private RegistrationState currentState = RegistrationState.INITIAL;
    private String currentEmail; // To store email after sending code
    private String currentPassword; // To store password after sending code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
        updateUIForCurrentState(false); // Set initial UI state
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressIndicator = findViewById(R.id.progress_indicator);
        
        // Initialize new views for verification code
        verificationCodeLayout = findViewById(R.id.verification_code_layout);
        etVerificationCode = findViewById(R.id.et_verification_code);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout); // Initialize confirm password layout
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState == RegistrationState.INITIAL) {
                    requestVerificationCode();
                } else { // currentState == RegistrationState.AWAITING_VERIFICATION
                    performRegistrationWithCode();
                }
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

    private void requestVerificationCode() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInitialInputs(email, password, confirmPassword)) {
            return;
        }

        currentEmail = email;
        currentPassword = password;

        updateUIForCurrentState(true); // Show loading state

        // Using "1" for US as country code, and "1" for email registration type, as in original code
        String countryCode = "1"; 
        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(currentEmail, "", countryCode, 1,
                new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        runOnUiThread(() -> {
                            updateUIForCurrentState(false);
                            showErrorMessage("Failed to send verification code: " + error);
                        });
                    }

                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            currentState = RegistrationState.AWAITING_VERIFICATION;
                            updateUIForCurrentState(false);
                            showSuccessMessage("Verification code sent to " + currentEmail);
                            etVerificationCode.requestFocus();
                        });
                    }
                });
    }

    private void performRegistrationWithCode() {
        String verificationCode = etVerificationCode.getText().toString().trim();

        if (verificationCode.isEmpty()) {
            etVerificationCode.setError("Verification code is required");
            etVerificationCode.requestFocus();
            return;
        }
        // Add more validation for code if needed (e.g., length)

        updateUIForCurrentState(true); // Show loading state

        String countryCode = "1"; // As used before
        ThingHomeSdk.getUserInstance().registerAccountWithEmail(countryCode, currentEmail, currentPassword,
                verificationCode, new IRegisterCallback() {
                    @Override
                    public void onSuccess(User user) {
                        runOnUiThread(() -> {
                            updateUIForCurrentState(false); // Reset loading
                            showSuccessMessage("Registration successful! Please sign in.");
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finishAffinity(); // Finish this and any parent login activities
                        });
                    }

                    @Override
                    public void onError(String code, String error) {
                        runOnUiThread(() -> {
                            updateUIForCurrentState(false);
                            showErrorMessage("Registration failed: " + error);
                        });
                    }
                });
    }
    
    private boolean validateInitialInputs(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void updateUIForCurrentState(boolean isLoading) {
        progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);

        if (isLoading) {
            if (currentState == RegistrationState.INITIAL) {
                btnRegister.setText("Sending Code...");
            } else {
                btnRegister.setText("Verifying & Registering...");
            }
            etEmail.setEnabled(false);
            etPassword.setEnabled(false);
            etConfirmPassword.setEnabled(false);
            etVerificationCode.setEnabled(false);
        } else { // Not loading
            etEmail.setEnabled(currentState == RegistrationState.INITIAL);
            etPassword.setEnabled(currentState == RegistrationState.INITIAL);
            etConfirmPassword.setEnabled(currentState == RegistrationState.INITIAL);
            confirmPasswordLayout.setVisibility(currentState == RegistrationState.INITIAL ? View.VISIBLE : View.GONE);


            if (currentState == RegistrationState.INITIAL) {
                btnRegister.setText("Send Verification Code");
                verificationCodeLayout.setVisibility(View.GONE);
                etVerificationCode.setEnabled(false);
            } else { // AWAITING_VERIFICATION
                btnRegister.setText("Verify and Register");
                verificationCodeLayout.setVisibility(View.VISIBLE);
                etVerificationCode.setEnabled(true);
            }
        }
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.success_green)) // Make sure this color is defined
                .setTextColor(getResources().getColor(android.R.color.white))
                .show();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error_red)) // Make sure this color is defined
                .setTextColor(getResources().getColor(android.R.color.white))
                .show();
    }
}
