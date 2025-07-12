package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.zerotechiot.eg.services.DeviceControlService;
import com.zerotechiot.eg.ui.adapters.DeviceAdapter;
import com.zerotechiot.eg.ui.models.DeviceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Modern Home Activity with enhanced UX features
 * Integrates Tuya SDK for device management and control
 */
public class HomeActivity extends AppCompatActivity implements DeviceAdapter.OnDeviceClickListener {

    private static final String TAG = "HomeActivity";
    
    private RecyclerView devicesRecyclerView;
    private TextView welcomeText;
    private TextView statusOverview;
    private View emptyState;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddDevice;

    private List<DeviceModel> deviceList = new ArrayList<>();
    private long currentHomeId = -1;
    private static final int REQUEST_PAIR_DEVICE = 1001;
    private DeviceControlService deviceControlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize device control service
        deviceControlService = new DeviceControlService(this);

        initViews();
        setupNavigation();
        setupRecyclerView();
        loadHomeData();
        setupClickListeners();
    }

    private void initViews() {
        devicesRecyclerView = findViewById(R.id.devices_recycler_view);
        welcomeText = findViewById(R.id.welcome_text);
        statusOverview = findViewById(R.id.status_overview);
        emptyState = findViewById(R.id.empty_state);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        fabAddDevice = findViewById(R.id.fab_add_device);
    }

    private void setupNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.nav_rooms) {
                // Navigate to device management
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_scenes) {
                // Navigate to scenes
                Intent intent = new Intent(this, ScenesActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_automation) {
                // Navigate to automation
                Toast.makeText(this, "Automation coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to profile
                Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DeviceAdapter adapter = new DeviceAdapter(deviceList, this);
        devicesRecyclerView.setAdapter(adapter);
    }

    private void loadHomeData() {
        Log.d(TAG, "Loading real devices from Tuya SDK...");
        
        // Load real devices from Tuya SDK
        deviceControlService.loadRealDevices(new DeviceControlService.DeviceLoadCallback() {
            @Override
            public void onSuccess(List<DeviceModel> realDevices) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Successfully loaded " + realDevices.size() + " real devices");
                    deviceList.clear();
                    deviceList.addAll(realDevices);
                    updateDeviceList();
                    updateStatusOverview(realDevices.size());
                    
                    if (realDevices.isEmpty()) {
                        showEmptyState();
                        showSnackbar("No devices found. Add some devices to get started!");
                    } else {
                        hideEmptyState();
                        showSnackbar("Loaded " + realDevices.size() + " real devices");
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Failed to load real devices: " + error);
                    showError("Failed to load devices: " + error);
                    showEmptyState();
                });
            }
        });
    }

    private void updateDeviceList() {
        runOnUiThread(() -> {
            // Create and set up device adapter
            DeviceAdapter deviceAdapter = new DeviceAdapter(deviceList, this);
            devicesRecyclerView.setAdapter(deviceAdapter);

            if (deviceList.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
            }
        });
    }

    private void updateStatusOverview(int deviceCount) {
        runOnUiThread(() -> {
            int onlineCount = (int) deviceList.stream()
                    .filter(device -> device.isOnline())
                    .count();
            statusOverview.setText(String.format("%d devices online • %d total devices", onlineCount, deviceCount));
        });
    }

    private void showEmptyState() {
        runOnUiThread(() -> {
            emptyState.setVisibility(View.VISIBLE);
            devicesRecyclerView.setVisibility(View.GONE);
        });
    }

    private void hideEmptyState() {
        runOnUiThread(() -> {
            emptyState.setVisibility(View.GONE);
            devicesRecyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void setupClickListeners() {
        fabAddDevice.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, DevicePairingActivity.class);
                startActivityForResult(intent, REQUEST_PAIR_DEVICE);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "Device Pairing feature is not available in this build.", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        });
    }

    private void startDevicePairing() {
        try {
            Intent intent = new Intent(this, DevicePairingActivity.class);
            startActivityForResult(intent, REQUEST_PAIR_DEVICE);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "Device Pairing feature is not available in this build.", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
        }
    }

    private void showComingSoon(String feature) {
        Snackbar.make(findViewById(android.R.id.content), feature + " coming soon!", Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh device list when returning to the activity
        loadHomeData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAIR_DEVICE) {
            if (resultCode == RESULT_OK) {
                showSnackbar("Device paired successfully!");
                loadHomeData(); // Refresh the device list
            } else {
                showSnackbar("Device pairing cancelled or failed");
            }
        }
    }

    @Override
    public void onDeviceClick(Object device) {
        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            Log.d(TAG, "Device clicked: " + deviceModel.getName() + " (ID: " + deviceModel.getId() + ")");
            
            // Launch device control activity with real device data
            Intent intent = new Intent(this, DeviceControlActivity.class);
            intent.putExtra("device_id", deviceModel.getId());
            intent.putExtra("device_name", deviceModel.getName());
            intent.putExtra("device_type", deviceModel.getType());
            startActivity(intent);
        }
    }

    @Override
    public void onDeviceToggle(Object device, boolean isOn) {
        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            Log.d(TAG, "Toggling device " + deviceModel.getName() + " to " + (isOn ? "ON" : "OFF"));
            
            // Use device control service to toggle real device
            deviceControlService.toggleDevice(deviceModel.getId(), isOn, new DeviceControlService.DeviceToggleCallback() {
                @Override
                public void onSuccess(boolean newState) {
                    runOnUiThread(() -> {
                        deviceModel.setOn(newState);
                        showSnackbar(deviceModel.getName() + " turned " + (newState ? "ON" : "OFF"));
                        // Update the adapter to reflect the change
                        updateDeviceList();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Failed to toggle device: " + error);
                        showError("Failed to toggle " + deviceModel.getName() + ": " + error);
                        // Revert the toggle in the UI
                        deviceModel.setOn(!isOn);
                        updateDeviceList();
                    });
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Device control service cleanup is handled automatically
    }
}
