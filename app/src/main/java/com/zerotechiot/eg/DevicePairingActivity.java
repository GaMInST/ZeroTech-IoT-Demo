package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DevicePairingActivity extends AppCompatActivity {

    private TextView statusText;
    private Button retryButton;
    private Button backButton;
    private Button manualButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Device");
        }

        // Initialize views
        statusText = findViewById(R.id.status_text);
        retryButton = findViewById(R.id.retry_button);
        backButton = findViewById(R.id.back_button);
        manualButton = findViewById(R.id.manual_button);

        // Setup click listeners
        retryButton.setOnClickListener(v -> attemptDevicePairing());
        backButton.setOnClickListener(v -> finish());
        manualButton.setOnClickListener(v -> showManualInstructions());

        // Initial attempt
        attemptDevicePairing();
    }

    private void attemptDevicePairing() {
        statusText.setText("Attempting to connect to device pairing service...");
        retryButton.setVisibility(View.GONE);
        manualButton.setVisibility(View.GONE);

        try {
            // Try to launch the BizBundle's DeviceActivatorActivity
            Intent intent = new Intent();
            intent.setClassName(this, "com.tuya.smart.bizbundle.activator.demo.DeviceActivatorActivity");
            startActivity(intent);
            finish();
        } catch (Exception e) {
            // If the activator activity is not available, show a message
            statusText.setText(
                    "Device pairing service is not available in this demo version.\n\n" +
                            "Please use the Tuya Smart app for device pairing or try the manual setup option.");
            retryButton.setVisibility(View.VISIBLE);
            retryButton.setText("Try Again");
            manualButton.setVisibility(View.VISIBLE);

            // Log the error for debugging
            e.printStackTrace();
        }
    }

    private void showManualInstructions() {
        statusText.setText(
                "Manual Device Setup Instructions:\n\n" +
                        "1. Make sure your device is in pairing mode\n" +
                        "2. Connect to the device's WiFi network\n" +
                        "3. Open the Tuya Smart app\n" +
                        "4. Follow the app's pairing instructions\n\n" +
                        "Note: This demo version has limited pairing capabilities.");
        retryButton.setVisibility(View.VISIBLE);
        retryButton.setText("Try Auto Pairing");
        manualButton.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


