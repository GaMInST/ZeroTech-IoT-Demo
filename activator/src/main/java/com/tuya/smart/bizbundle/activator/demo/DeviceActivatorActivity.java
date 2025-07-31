package com.tuya.smart.bizbundle.activator.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.thingclips.smart.activator.plug.mesosphere.ThingDeviceActivatorManager;
import com.thingclips.smart.activator.plug.mesosphere.api.IThingDeviceActiveListener;
import com.thingclips.smart.activator.scan.qrcode.ScanManager;

import android.content.Intent;

public class DeviceActivatorActivity extends AppCompatActivity {
    private static final int INFO_MESSAGE = 1;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private EditText infoEt;
    private MaterialButton btnScanQr;
    private MaterialButton btnManualSetup;
    private MaterialButton btnQuickStart;
    private FloatingActionButton fabHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_activator);
        
        initializeViews();
        setupToolbar();
        setupButtonListeners();
    }

    private void initializeViews() {
        infoEt = findViewById(R.id.et_info);
        btnScanQr = findViewById(R.id.btn_scan_qr);
        btnManualSetup = findViewById(R.id.btn_manual_setup);
        btnQuickStart = findViewById(R.id.btn_quick_start);
        fabHelp = findViewById(R.id.fab_help);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add Device");
        }
    }

    private void setupButtonListeners() {
        // QR Code Scanner Button
        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMessage("🔘 QR Code Scanner button clicked");
                logMessage("📱 Current activity: " + DeviceActivatorActivity.this.getClass().getSimpleName());
                logMessage("🔍 Starting QR Code Scanner...");
                checkCameraPermissionAndScan();
            }
        });

        // Manual Setup Button
        btnManualSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMessage("Starting Manual Device Setup...");
                actionConfig(v);
            }
        });

        // Quick Start Guide Button
        btnQuickStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMessage("Opening Quick Start Guide...");
                showQuickStartGuide();
            }
        });

        // Help FAB
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logMessage("Opening Help...");
                showHelp();
            }
        });
    }

    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            logMessage("Requesting camera permission...");
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                CAMERA_PERMISSION_REQUEST);
        } else {
            logMessage("Camera permission granted, launching scanner...");
            actionScan(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                logMessage("Camera permission granted! Launching QR scanner...");
                actionScan(null);
            } else {
                logMessage("❌ Camera permission denied. QR scanning unavailable.");
                Toast.makeText(this, "Camera permission required for QR scanning", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INFO_MESSAGE) {
                if (msg.obj != null) {
                    logMessage(msg.obj.toString());
                } else {
                    Toast.makeText(DeviceActivatorActivity.this, "Message is null", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void logMessage(String message) {
        infoEt.append(message + "\n");
        // Auto-scroll to bottom
        infoEt.post(new Runnable() {
            @Override
            public void run() {
                int scrollAmount = infoEt.getLayout().getLineTop(infoEt.getLineCount()) - infoEt.getHeight();
                if (scrollAmount > 0) {
                    infoEt.scrollTo(0, scrollAmount);
                }
            }
        });
    }

    public void actionConfig(View view) {
        logMessage("Initializing Tuya Device Activator...");
        
        // Start the Tuya device activation process
        ThingDeviceActivatorManager.INSTANCE.startDeviceActiveAction(this);

        // Add listener for device activation events
        ThingDeviceActivatorManager.INSTANCE.addListener(new IThingDeviceActiveListener() {
            @Override
            public void onDevicesAdd(List<String> list) {
                StringBuilder str = new StringBuilder();
                for (String id : list) {
                    str.append("✅ Device added successfully! ID: " + id).append("\n");
                }
                Message msg = Message.obtain();
                msg.what = INFO_MESSAGE;
                msg.obj = str.toString();
                mHandler.sendMessage(msg);
                
                // Show success toast
                Toast.makeText(DeviceActivatorActivity.this, "Device added successfully!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRoomDataUpdate() {
                logMessage("🔄 Room data updated - please refresh room data");
                Toast.makeText(DeviceActivatorActivity.this, "Room data updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOpenDevicePanel(String deviceId) {
                logMessage("📱 Opening device panel for: " + deviceId);
                Toast.makeText(DeviceActivatorActivity.this, "Opening device panel: " + deviceId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actionScan(View view) {
        logMessage("🔍 Opening QR Code Scanner...");
        
        try {
            // Check if ScanManager is available
            if (ScanManager.INSTANCE == null) {
                logMessage("❌ ScanManager is null - SDK not properly initialized");
                Toast.makeText(this, "QR Scanner not available - SDK issue", Toast.LENGTH_LONG).show();
                return;
            }
            
            logMessage("✅ ScanManager found, launching scanner...");
            
            // Launch the Tuya QR code scanner with better error handling
            ScanManager.INSTANCE.openScan(this);
            logMessage("QR Scanner launched successfully - scan your device's QR code");
            
            // Add a small delay to ensure the scanner has time to initialize
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logMessage("📱 QR Scanner should now be visible - point camera at QR code");
                }
            }, 1000);
            
        } catch (Exception e) {
            logMessage("❌ Error launching QR scanner: " + e.getMessage());
            logMessage("Stack trace: " + e.getStackTrace()[0].toString());
            Toast.makeText(this, "Error launching QR scanner: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Try alternative approach
            tryAlternativeQRScanner();
        }
    }
    
    private void tryAlternativeQRScanner() {
        logMessage("🔄 Trying alternative QR scanner approach...");
        
        try {
            // Try to launch the scanner activity directly
            Intent intent = new Intent();
            intent.setClassName(this, "com.thingclips.smart.activator.scan.qrcode.ScanActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            logMessage("✅ Alternative QR scanner launched");
        } catch (Exception e2) {
            logMessage("❌ Alternative QR scanner also failed: " + e2.getMessage());
            Toast.makeText(this, "QR Scanner unavailable - please use manual setup", Toast.LENGTH_LONG).show();
        }
    }

    private void showQuickStartGuide() {
        logMessage("📖 Quick Start Guide:");
        logMessage("1. Ensure your device is powered on");
        logMessage("2. Make sure you're connected to 2.4GHz WiFi");
        logMessage("3. Keep your phone close to the device");
        logMessage("4. Follow the on-screen instructions");
        logMessage("5. Wait for the device to connect");
        
        Toast.makeText(this, "Quick Start Guide displayed in log", Toast.LENGTH_SHORT).show();
    }

    private void showHelp() {
        logMessage("❓ Help Information:");
        logMessage("• Use QR Code Scanner for devices with QR codes");
        logMessage("• Use Manual Setup for devices without QR codes");
        logMessage("• Ensure device is in pairing mode");
        logMessage("• Check your WiFi connection");
        logMessage("• Contact support if issues persist");
        
        Toast.makeText(this, "Help information displayed in log", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}