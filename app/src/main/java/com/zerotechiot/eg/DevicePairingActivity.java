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
// It's good practice to import com.google.android.material.button.MaterialButton if that's what's used in XML
// However, findViewById will return a View, which can be cast to Button if MaterialButton extends Button.

public class DevicePairingActivity extends AppCompatActivity {

    private TextView statusText;
    private Button retryButton;
    private Button backButton;
    private Button manualButton;
    private Button scanQrButton; // Added for QR scan button

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
        scanQrButton = findViewById(R.id.scan_qr_button); // Initialize scanQrButton

        // Setup click listeners
        retryButton.setOnClickListener(v -> attemptDevicePairing());
        backButton.setOnClickListener(v -> finish());
        manualButton.setOnClickListener(v -> showManualInstructions());
        scanQrButton.setOnClickListener(v -> {
            // Placeholder for Tuya SDK QR Code Scanning logic
            Toast.makeText(DevicePairingActivity.this, "QR Scan button clicked!", Toast.LENGTH_SHORT).show();
            // TODO: 1. Check/Request Camera Permissions
            // TODO: 2. Initialize and start Tuya QR Code Scanner
            // TODO: 3. Handle scan result (token) from Tuya SDK
            // TODO: 4. Use the token to provision the device via Tuya SDK
        });

        // Initial attempt (or remove if QR scan is the primary method now)
        attemptDevicePairing();
    }

    private void attemptDevicePairing() {
        statusText.setText("Attempting to connect to device pairing service...");
        retryButton.setVisibility(View.GONE);
        manualButton.setVisibility(View.GONE);
        scanQrButton.setVisibility(View.VISIBLE); // Make sure QR button is visible if this is a fallback

        try {
            // Try to launch the BizBundle's DeviceActivatorActivity
            Intent intent = new Intent();
            intent.setClassName(this, "com.tuya.smart.bizbundle.activator.demo.DeviceActivatorActivity");
            startActivity(intent);
            finish(); // Finish this activity if Tuya's activity is launched
        } catch (Exception e) {
            // If the activator activity is not available, show a message
            statusText.setText(
                    "Device pairing service is not available in this demo version.\n\n" +
                            "Please use the Tuya Smart app for device pairing, try the manual setup option, or use QR Scan.");
            retryButton.setVisibility(View.VISIBLE);
            retryButton.setText("Try Auto Pairing");
            manualButton.setVisibility(View.VISIBLE);
            scanQrButton.setVisibility(View.VISIBLE); // Ensure QR button is visible

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
        scanQrButton.setVisibility(View.VISIBLE); // Ensure QR button is visible
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
