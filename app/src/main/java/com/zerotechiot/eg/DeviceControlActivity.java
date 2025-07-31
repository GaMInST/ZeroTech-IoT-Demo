package com.zerotechiot.eg;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide; // Added Glide import
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.service.MicroServiceManager;
// import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer; // Not used
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
// import com.thingclips.smart.control.PluginControlService; // Not used
// import com.thingclips.smart.control.plug.api.IPluginControlService; // Not used
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
// import com.thingclips.smart.panel.caller.api.AbsPanelCallerService; // Temporarily commented out
import com.zerotechiot.eg.services.DeviceControlService;

import java.util.ArrayList;
import java.util.List;

public class DeviceControlActivity extends AppCompatActivity {
    private static final String TAG = "DeviceControlActivity";
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DeviceAdapter adapter;
    private DeviceControlService deviceControlService;
    // private AbsPanelCallerService panelCallerService; // Temporarily commented out
    private AbsBizBundleFamilyService familyService;
    
    private List<DeviceBean> deviceList = new ArrayList<>();
    private long currentHomeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        initializeServices();
        setupViews();
        loadDevices();
    }

    private void initializeServices() {
        try {
            // Initialize device control service
            deviceControlService = new DeviceControlService(this);
            
            // Initialize panel caller service - Temporarily commented out
            // panelCallerService = MicroContext.getServiceManager()
            //         .findServiceByInterface(AbsPanelCallerService.class.getName());
            
            // Get family service
            familyService = MicroServiceManager.getInstance()
                    .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
                    
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize services", e);
        }
    }

    private void setupViews() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Device Control");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadDevices);

        // Setup recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void loadDevices() {
        if (familyService == null) {
            Toast.makeText(this, "Family service not available", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            return;
        }

        currentHomeId = familyService.getCurrentHomeId();
        if (currentHomeId == 0) {
            Toast.makeText(this, "No home selected", Toast.LENGTH_SHORT).show();
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            return;
        }
        
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(true);
        ThingHomeSdk.newHomeInstance(currentHomeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                if (homeBean != null && homeBean.getDeviceList() != null) {
                    deviceList.clear();
                    deviceList.addAll(homeBean.getDeviceList());
                    
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        
                        if (deviceList.isEmpty()) {
                            Toast.makeText(DeviceControlActivity.this, 
                                "No devices found. Add devices to your home first.", 
                                Toast.LENGTH_LONG).show();
                        } else {
                            // Toast.makeText(DeviceControlActivity.this, 
                            //     "Found " + deviceList.size() + " devices", 
                            //     Toast.LENGTH_SHORT).show(); // Optional: can be noisy
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(DeviceControlActivity.this, 
                            "No devices found in this home", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String code, String error) {
                Log.e(TAG, "Failed to load devices: " + code + " - " + error);
                runOnUiThread(() -> {
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(DeviceControlActivity.this, 
                        "Failed to load devices: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_device_control, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            DeviceBean device = deviceList.get(position);
            holder.bind(device);
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }

        class DeviceViewHolder extends RecyclerView.ViewHolder {
            private MaterialCardView cardView;
            private ImageView deviceIcon;
            private TextView deviceName;
            private TextView deviceStatus;
            private Chip deviceType;
            private Chip onlineStatus;

            public DeviceViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.deviceCard);
                deviceIcon = itemView.findViewById(R.id.deviceIcon);
                deviceName = itemView.findViewById(R.id.deviceName);
                deviceStatus = itemView.findViewById(R.id.deviceStatus);
                deviceType = itemView.findViewById(R.id.deviceType);
                onlineStatus = itemView.findViewById(R.id.onlineStatus);
            }

            public void bind(DeviceBean device) {
                // Set device name
                deviceName.setText(device.getName());
                
                // Set device status
                if (device.getIsOnline()) {
                    deviceStatus.setText("Online");
                    onlineStatus.setText("Online");
                } else {
                    deviceStatus.setText("Offline");
                    onlineStatus.setText("Offline");
                }

                // Set device type
                String deviceTypeText = getDeviceTypeName(device.getProductId());
                deviceType.setText(deviceTypeText);

                // Load device icon using Glide
                if (device.getIconUrl() != null && !device.getIconUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(device.getIconUrl())
                            .placeholder(R.drawable.ic_device) 
                            .error(R.drawable.ic_device)       
                            .into(deviceIcon);
                } else {
                    deviceIcon.setImageResource(R.drawable.ic_device); 
                }

                cardView.setOnClickListener(v -> {
                    if (device.getIsOnline()) {
                        launchDeviceControl(device.getDevId());
                    } else {
                        Toast.makeText(DeviceControlActivity.this, 
                            "Device is offline", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private String getDeviceTypeName(String productId) {
                if (productId == null) return "Unknown";
                
                String lowerProductId = productId.toLowerCase();
                if (lowerProductId.contains("light") || lowerProductId.contains("bulb")) {
                    return "Light";
                } else if (lowerProductId.contains("switch") || lowerProductId.contains("outlet")) {
                    return "Switch";
                } else if (lowerProductId.contains("lock")) {
                    return "Lock";
                } else if (lowerProductId.contains("camera") || lowerProductId.contains("ipc")) {
                    return "Camera";
                } else if (lowerProductId.contains("sensor")) {
                    return "Sensor";
                } else if (lowerProductId.contains("thermostat")) {
                    return "Thermostat";
                } else if (lowerProductId.contains("curtain")) {
                    return "Curtain";
                } else if (lowerProductId.contains("fan")) {
                    return "Fan";
                } else if (lowerProductId.contains("vacuum") || lowerProductId.contains("sweeper")) {
                    return "Vacuum";
                }
                
                return "Device";
            }
        }
    }

    private void launchDeviceControl(String deviceId) {
        // Temporarily disabled: Panel BizBundle dependency is missing.
        Toast.makeText(this, "Device panel navigation is temporarily disabled. Please add the Panel BizBundle dependency.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Attempted to launch panel for device: " + deviceId + " (PanelCallerService is disabled)");
        
        // Original logic (commented out):
        // if (panelCallerService != null) {
        //     Log.d(TAG, "Launching device control panel for device: " + deviceId);
        //     panelCallerService.goPanelWithCheckAndTip(this, deviceId); 
        // } else {
        //     Toast.makeText(this, "Device control panel service not available. Please ensure Panel BizBundle is included.", Toast.LENGTH_LONG).show();
        //     Log.e(TAG, "PanelCallerService is null. Cannot launch panel.");
        // }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Device control service cleanup is handled automatically if it implements any lifecycle interfaces
    }
}
