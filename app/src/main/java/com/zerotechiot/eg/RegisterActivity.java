package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                String countryCode = "1"; // US
                ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "", countryCode, 1,
                        new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Toast.makeText(RegisterActivity.this, "Failed to send verification code: " + error,
                                        Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(RegisterActivity.this, "Registration successful",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onError(String code, String error) {
                                                Toast.makeText(RegisterActivity.this, "Registration failed: " + error,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });
    }
}
