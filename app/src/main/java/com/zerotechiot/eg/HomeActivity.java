package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Modern Home Activity with enhanced UX features
 * Integrates Tuya SDK for device management and control
 */
public class HomeActivity extends AppCompatActivity {

    private RecyclerView devicesRecyclerView;
    private TextView welcomeText;
    private TextView statusOverview;
    private View emptyState;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddDevice;

    private List<DeviceBean> deviceList = new ArrayList<>();
    private long currentHomeId = -1;
    private static final int REQUEST_PAIR_DEVICE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                return true;
            } else if (itemId == R.id.nav_rooms) {
                showComingSoon("Rooms");
                return true;
            } else if (itemId == R.id.nav_scenes) {
                showComingSoon("Scenes");
                return true;
            } else if (itemId == R.id.nav_automation) {
                showComingSoon("Automation");
                return true;
            } else if (itemId == R.id.nav_profile) {
                showComingSoon("Profile");
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        // For now, we'll just set up the layout manager
        // The adapter will be implemented in the next step
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void loadHomeData() {
        currentHomeId = 10000; // Default home ID

        if (currentHomeId > 0) {
            loadDevices();
        } else {
            showEmptyState();
        }
    }

    private void loadDevices() {
        ThingHomeSdk.newHomeInstance(currentHomeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(@NonNull HomeBean homeBean) {
                List<DeviceBean> devices = homeBean.getDeviceList();
                if (devices != null && !devices.isEmpty()) {
                    deviceList.clear();
                    deviceList.addAll(devices);
                    updateDeviceList();
                    updateStatusOverview(devices.size());
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                showError("Failed to load devices: " + errorMsg);
                showEmptyState();
            }
        });
    }

    private void updateDeviceList() {
        runOnUiThread(() -> {
            // TODO: Update adapter when implemented
            if (deviceList.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
                // For now, just show a success message
                showSnackbar("Loaded " + deviceList.size() + " devices");
            }
        });
    }

    private void updateStatusOverview(int deviceCount) {
        runOnUiThread(() -> {
            int onlineCount = (int) deviceList.stream()
                    .filter(device -> device.getIsOnline())
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
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(this, "Device Pairing feature is not available in this build.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void showComingSoon(String feature) {
        showSnackbar(feature + " coming soon!");
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentHomeId > 0) {
            loadDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAIR_DEVICE && resultCode == RESULT_OK) {
            // Device was paired, reload device list
            loadDevices();
        }
    }
}
