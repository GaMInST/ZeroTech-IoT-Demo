package com.zerotechiot.eg;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.thingclips.basic.split.LargeScreen;
import com.thingclips.smart.android.common.utils.L;
import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.demo_login.base.utils.LoginHelper;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.api.IThingHomeChangeListener;
import androidx.annotation.NonNull;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.bean.RoomBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.GroupBean;
import com.thingclips.smart.theme.ThingTheme;
import com.thingclips.smart.utils.ProgressUtil;
import com.thingclips.smart.utils.ToastUtil;
import com.zerotechiot.eg.ui.adapters.DeviceAdapter;
import com.zerotechiot.eg.ui.adapters.RoomAdapter;
import com.zerotechiot.eg.ui.models.DeviceModel;
import com.zerotechiot.eg.ui.models.RoomModel;
import com.zerotechiot.eg.services.DeviceControlService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements DeviceAdapter.OnDeviceClickListener, RoomAdapter.OnRoomClickListener {

    private TextView mCurrentFamilyName;
    private RouterPresenter routePresenter;
    private RecyclerView roomsRecyclerView;
    private RecyclerView devicesRecyclerView;
    private RoomAdapter roomAdapter;
    private DeviceAdapter deviceAdapter;
    private List<RoomModel> rooms = new ArrayList<>();
    private List<DeviceModel> devices = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private DeviceControlService deviceControlService;

    private IThingHomeChangeListener mHomeChangeListener = new IThingHomeChangeListener() {
        @Override
        public void onHomeAdded(long homeId) {
            requestHomeDetail(homeId);
        }

        @Override
        public void onHomeInvite(long homeId, String homeName) {

        }

        @Override
        public void onHomeRemoved(long l) {

        }

        @Override
        public void onHomeInfoChanged(long l) {

        }

        @Override
        public void onSharedDeviceList(List<DeviceBean> list) {

        }

        @Override
        public void onSharedGroupList(List<GroupBean> list) {

        }

        @Override
        public void onServerConnectSuccess() {
        }
    };

    private void requestHomeDetail(long id) {
        ThingHomeSdk.newHomeInstance(id).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {

            }

            @Override
            public void onError(String errorCode, String errorMsg) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize session management
        sharedPreferences = getSharedPreferences("ZeroTechPrefs", MODE_PRIVATE);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // TODO 此处只是演示代码，集成时请在登录成功后调用
        // This method must be called after successful login
        BizBundleInitializer.onLogin();

        Log.i("SceneMainActivity", "onCreate");
        LargeScreen.INSTANCE.modeChanged(this);

        // Initialize device control service
        deviceControlService = new DeviceControlService(this);

        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        loadSampleData();

        // sample code
        mCurrentFamilyName = findViewById(R.id.current_family_name);
        mCurrentFamilyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyDialogFragment dialogFragment = FamilyDialogFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "FamilyDialogFragment");
            }
        });
        ProgressUtil.showLoading(this, "Loading...");
        getHomeList();
        ThingHomeSdk.getHomeManagerInstance().registerThingHomeChangeListener(mHomeChangeListener);
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void logout() {
        // Clear session
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Logout from Tuya SDK
        ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                });
            }

            @Override
            public void onError(String code, String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void initializeViews() {
        mCurrentFamilyName = findViewById(R.id.current_family_name);
        roomsRecyclerView = findViewById(R.id.rooms_recycler_view);
        devicesRecyclerView = findViewById(R.id.devices_recycler_view);

        // Setup bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on home, do nothing
                return true;
            } else if (itemId == R.id.nav_rooms) {
                // Navigate to rooms page
                Intent roomsIntent = new Intent(this, RoomsActivity.class);
                startActivity(roomsIntent);
                return true;
            } else if (itemId == R.id.nav_scenes) {
                // Navigate to scenes page
                Intent scenesIntent = new Intent(this, ScenesActivity.class);
                startActivity(scenesIntent);
                return true;
            } else if (itemId == R.id.nav_automation) {
                // Navigate to automation page
                Intent automationIntent = new Intent(this, AutomationActivity.class);
                startActivity(automationIntent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to profile page
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
            return true;
            }
            return false;
        });

        // Setup floating action button
        FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
        fabAddDevice.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, DevicePairingActivity.class);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "Device Pairing feature is not available in this build.", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        });
    }

    private void setupRecyclerViews() {
        // Setup rooms RecyclerView
        roomAdapter = new RoomAdapter(this);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roomsRecyclerView.setAdapter(roomAdapter);

        // Setup devices RecyclerView
        deviceAdapter = new DeviceAdapter(new ArrayList<>(), this);
        devicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        devicesRecyclerView.setAdapter(deviceAdapter);
    }

    private void setupClickListeners() {
        // Theme switch button
        MaterialButton themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setOnClickListener(v -> showThemeSelectionDialog());

        // Logout button
        MaterialButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> showLogoutDialog());

        // Quick action buttons
        MaterialButton quickAllOn = findViewById(R.id.quick_all_on);
        quickAllOn.setOnClickListener(v -> turnAllDevicesOn());

        MaterialButton quickAllOff = findViewById(R.id.quick_all_off);
        quickAllOff.setOnClickListener(v -> turnAllDevicesOff());

        // Setup all the existing button click listeners
        setupExistingButtonListeners();
    }

    private void setupExistingButtonListeners() {
        // Device Control
        findViewById(R.id.panel).setOnClickListener(v -> {
            Intent intent = new Intent(this, DeviceControlActivity.class);
            startActivity(intent);
        });

        // Smart Scenes
        findViewById(R.id.scene).setOnClickListener(v -> {
            Intent intent = new Intent(this, ScenesActivity.class);
            startActivity(intent);
        });

        // Add Device
        findViewById(R.id.activator).setOnClickListener(v -> {
                Intent intent = new Intent(this, DevicePairingActivity.class);
                startActivity(intent);
        });

        // Multi Control - Enhanced functionality
        findViewById(R.id.control).setOnClickListener(v -> {
            showMultiControlDialog();
        });

        // Cameras
        findViewById(R.id.ipc).setOnClickListener(v -> {
            Toast.makeText(this, "Camera functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Device Store
        findViewById(R.id.mall).setOnClickListener(v -> {
            Toast.makeText(this, "Device store functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Cloud Storage
        findViewById(R.id.cloud_storage).setOnClickListener(v -> {
            Toast.makeText(this, "Cloud storage functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Message Center
        findViewById(R.id.message).setOnClickListener(v -> {
            Toast.makeText(this, "Message center functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Help & Feedback
        findViewById(R.id.feedback).setOnClickListener(v -> {
            Toast.makeText(this, "Help & feedback functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Firmware Update
        findViewById(R.id.ota).setOnClickListener(v -> {
            Toast.makeText(this, "Firmware update functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Home Management
        findViewById(R.id.family).setOnClickListener(v -> {
            showFamilyDialog();
        });

        // Device Details
        findViewById(R.id.device_detail).setOnClickListener(v -> {
            Toast.makeText(this, "Device details functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Location Services
        findViewById(R.id.location).setOnClickListener(v -> {
            Toast.makeText(this, "Location services functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Device Groups
        findViewById(R.id.groupmanager).setOnClickListener(v -> {
            Toast.makeText(this, "Device groups functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Voice Assistant
        findViewById(R.id.alexa_google_bind).setOnClickListener(v -> {
            Toast.makeText(this, "Voice assistant functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Light Scenes
        findViewById(R.id.light_scene).setOnClickListener(v -> {
            Toast.makeText(this, "Light scenes functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Share Devices
        findViewById(R.id.share).setOnClickListener(v -> {
            Toast.makeText(this, "Share devices functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Mini Apps
        findViewById(R.id.miniapp).setOnClickListener(v -> {
            Toast.makeText(this, "Mini apps functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Third Party Services
        findViewById(R.id.third_service).setOnClickListener(v -> {
            Toast.makeText(this, "Third party services functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Marketing
        findViewById(R.id.marketing).setOnClickListener(v -> {
            Toast.makeText(this, "Marketing functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Speech Recognition
        findViewById(R.id.speech).setOnClickListener(v -> {
            Toast.makeText(this, "Speech recognition functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        // Floating Action Button
        findViewById(R.id.fab_add_device).setOnClickListener(v -> {
            Intent intent = new Intent(this, DevicePairingActivity.class);
            startActivity(intent);
        });
    }

    private void showMultiControlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multi Control");
        builder.setMessage("Choose an action to perform on multiple devices:");

        String[] options = {
                "Turn All Devices On",
                "Turn All Devices Off",
                "Toggle All Lights",
                "Set All Lights to 50%",
                "Create Device Group",
                "Bulk Device Settings"
        };

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    turnAllDevicesOn();
                    break;
                case 1:
                    turnAllDevicesOff();
                    break;
                case 2:
                    toggleAllLights();
                    break;
                case 3:
                    setAllLightsToPercentage(50);
                    break;
                case 4:
                    showCreateGroupDialog();
                    break;
                case 5:
                    showBulkSettingsDialog();
                    break;
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void toggleAllLights() {
        int lightsToggled = 0;
        for (DeviceModel device : devices) {
            if ("light".equals(device.getType()) && device.isOnline()) {
                device.setOn(!device.isOn());
                lightsToggled++;
            }
        }
        deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, lightsToggled + " lights toggled", Toast.LENGTH_SHORT).show();
    }

    private void setAllLightsToPercentage(int percentage) {
        int lightsUpdated = 0;
        for (DeviceModel device : devices) {
            if ("light".equals(device.getType()) && device.isOnline()) {
                device.setBrightness(percentage);
                lightsUpdated++;
            }
        }
        deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, lightsUpdated + " lights set to " + percentage + "%", Toast.LENGTH_SHORT).show();
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Device Group");
        builder.setMessage("This feature allows you to create groups of devices for easier control.");

        // Create a list of available devices
        List<String> deviceNames = new ArrayList<>();
        for (DeviceModel device : devices) {
            if (device.isOnline()) {
                deviceNames.add(device.getName());
            }
        }

        boolean[] checkedItems = new boolean[deviceNames.size()];
        String[] deviceArray = deviceNames.toArray(new String[0]);

        builder.setMultiChoiceItems(deviceArray, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Create Group", (dialog, which) -> {
            int selectedCount = 0;
            for (boolean checked : checkedItems) {
                if (checked)
                    selectedCount++;
            }
            Toast.makeText(this, "Group created with " + selectedCount + " devices", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showBulkSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bulk Device Settings");
        builder.setMessage("Configure settings for multiple devices at once.");

        String[] options = {
                "Set All to Auto Mode",
                "Set All to Manual Mode",
                "Enable All Notifications",
                "Disable All Notifications",
                "Set All to Energy Saving"
        };

        builder.setItems(options, (dialog, which) -> {
            String action = options[which];
            Toast.makeText(this, "Applied " + action + " to all devices", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadSampleData() {
        // Load real devices and rooms from Tuya SDK
        loadRealDevices();
        loadRealRooms();
        updateHomeStats();
    }

    private void loadRealDevices() {
        Log.d("MainActivity", "Loading real devices...");
        deviceControlService.loadRealDevices(new DeviceControlService.DeviceLoadCallback() {
            @Override
            public void onSuccess(List<DeviceModel> realDevices) {
                runOnUiThread(() -> {
                    Log.d("MainActivity", "Successfully loaded " + realDevices.size() + " real devices");
                    devices.clear();
                    devices.addAll(realDevices);
                    deviceAdapter.setDevices(devices);
                    updateDeviceStats();
                    
                    if (devices.isEmpty()) {
                        // Show empty state if no devices
                        Toast.makeText(MainActivity.this, "No devices found in your home. Please add some devices first.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Loaded " + devices.size() + " real devices", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.w("MainActivity", "Failed to load real devices: " + error);
                    
                    // Only show demo devices if it's a specific error that suggests no real devices
                    if (error.contains("No devices found") || error.contains("No homes found")) {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        // Don't load fallback devices - let user know they need to add real devices
                    } else {
                        // For other errors, show the error and load fallback devices
                        Toast.makeText(MainActivity.this, "Error loading devices: " + error + ". Showing demo devices.", Toast.LENGTH_LONG).show();
                        loadFallbackDevices();
                    }
                });
            }
        });
    }

    private void loadFallbackDevices() {
        // Load sample devices as fallback with better data
        devices.clear();

        DeviceModel livingRoomLight = new DeviceModel("1", "Living Room Light", "light", "Living Room", "1");
        livingRoomLight.setOnline(true);
        livingRoomLight.setOn(true);
        livingRoomLight.setBrightness(80);
        devices.add(livingRoomLight);

        DeviceModel bedroomLight = new DeviceModel("2", "Bedroom Light", "light", "Bedroom", "2");
        bedroomLight.setOnline(true);
        bedroomLight.setOn(false);
        devices.add(bedroomLight);

        DeviceModel kitchenSwitch = new DeviceModel("3", "Kitchen Switch", "switch", "Kitchen", "3");
        kitchenSwitch.setOnline(true);
        kitchenSwitch.setOn(true);
        devices.add(kitchenSwitch);

        DeviceModel bathroomFan = new DeviceModel("4", "Bathroom Fan", "fan", "Bathroom", "4");
        bathroomFan.setOnline(false);
        bathroomFan.setOn(false);
        devices.add(bathroomFan);

        deviceAdapter.setDevices(devices);
        updateDeviceStats();
    }

    private void loadRealRooms() {
        // Load real rooms from Tuya SDK
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> list) {
                if (!list.isEmpty()) {
                    HomeBean currentHome = list.get(0);
                    loadRoomsFromHome(currentHome.getHomeId());
                } else {
                    // Create default rooms if no home exists
                    loadFallbackRooms();
                }
            }

            @Override
            public void onError(String s, String s1) {
                // Fallback to sample rooms on error
                loadFallbackRooms();
            }
        });
    }

    private void loadRoomsFromHome(long homeId) {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(@NonNull HomeBean homeBean) {
                List<RoomBean> tuyaRooms = null;
                try {
                    tuyaRooms = homeBean.getRooms();
                } catch (Exception e) {
                    // Method not available, fallback
                }
                rooms.clear();

                if (tuyaRooms != null && !tuyaRooms.isEmpty()) {
                    for (RoomBean tuyaRoom : tuyaRooms) {
                        RoomModel room = new RoomModel(
                                String.valueOf(tuyaRoom.getRoomId()),
                                tuyaRoom.getName(),
                                tuyaRoom.getName().toLowerCase().replace(" ", "_"));
                        rooms.add(room);
                    }
                } else {
                    // Create default rooms if no real rooms found
                    createDefaultRooms(homeId);
                }

                roomAdapter.setRooms(rooms);
                updateRoomStats();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                // Fallback to sample rooms on error
                loadFallbackRooms();
            }
        });
    }

    private void createDefaultRooms(long homeId) {
        // Create default rooms for the home
        String[] defaultRoomNames = { "Living Room", "Bedroom", "Kitchen", "Bathroom" };
        String[] defaultRoomIcons = { "living", "bedroom", "kitchen", "bathroom" };

        for (int i = 0; i < defaultRoomNames.length; i++) {
            RoomModel room = new RoomModel(
                    String.valueOf(i + 1),
                    defaultRoomNames[i],
                    defaultRoomIcons[i]);
            rooms.add(room);
        }

        roomAdapter.setRooms(rooms);
        updateRoomStats();
    }

    private void loadFallbackRooms() {
        // Load sample rooms as fallback
        rooms.clear();
        rooms.add(new RoomModel("1", "Living Room", "living"));
        rooms.add(new RoomModel("2", "Bedroom", "bedroom"));
        rooms.add(new RoomModel("3", "Kitchen", "kitchen"));
        rooms.add(new RoomModel("4", "Bathroom", "bathroom"));
        roomAdapter.setRooms(rooms);
        updateRoomStats();
    }

    private void updateDeviceStats() {
        int totalDevices = devices.size();
        int onlineDevices = 0;
        for (DeviceModel device : devices) {
            if (device.isOnline()) {
                onlineDevices++;
            }
        }

        // Update the welcome card stats with real data
        TextView welcomeStatsText = findViewById(R.id.welcome_stats_text);
        if (welcomeStatsText != null) {
            welcomeStatsText.setText(onlineDevices + " devices online • " + rooms.size() + " rooms active");
        }
    }

    private void updateRoomStats() {
        // Room stats are now updated in updateDeviceStats() via the welcome_stats_text
        // This method is kept for compatibility but doesn't need to do anything
    }

    private void updateHomeStats() {
        updateDeviceStats();
        updateRoomStats();
    }

    private void turnAllDevicesOn() {
        for (DeviceModel device : devices) {
            if (device.isOnline()) {
                device.setOn(true);
            }
        }
        deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All devices turned on", Toast.LENGTH_SHORT).show();
    }

    private void turnAllDevicesOff() {
        for (DeviceModel device : devices) {
            if (device.isOnline()) {
                device.setOn(false);
            }
        }
        deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All devices turned off", Toast.LENGTH_SHORT).show();
    }

    // DeviceAdapter.OnDeviceClickListener implementation
    @Override
    public void onDeviceClick(Object device) {
        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            // Launch device control activity
            Intent intent = new Intent(this, DeviceControlActivity.class);
            intent.putExtra("device_id", deviceModel.getId());
            intent.putExtra("device_name", deviceModel.getName());
            intent.putExtra("device_type", deviceModel.getType());
            startActivity(intent);
        } else if (device instanceof DeviceBean) {
            DeviceBean deviceBean = (DeviceBean) device;
            // Launch device control activity
            Intent intent = new Intent(this, DeviceControlActivity.class);
            intent.putExtra("device_id", deviceBean.getDevId());
            intent.putExtra("device_name", deviceBean.getName());
            intent.putExtra("device_type", deviceBean.getProductId());
            startActivity(intent);
        }
    }

    @Override
    public void onDeviceToggle(Object device, boolean isOn) {
        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            
            // Use device control service to toggle real device
            deviceControlService.toggleDevice(deviceModel.getId(), isOn, new DeviceControlService.DeviceToggleCallback() {
                @Override
                public void onSuccess(boolean newState) {
                    runOnUiThread(() -> {
                        deviceModel.setOn(newState);
                        deviceAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, 
                            deviceModel.getName() + " " + (newState ? "turned on" : "turned off"), 
                            Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        // Revert the toggle if it failed
                        deviceModel.setOn(!isOn);
                        deviceAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, 
                            "Failed to toggle " + deviceModel.getName() + ": " + error, 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else if (device instanceof DeviceBean) {
            DeviceBean deviceBean = (DeviceBean) device;
            // For DeviceBean, we would need to use Tuya SDK to control the device
            Toast.makeText(this, deviceBean.getName() + " " + (isOn ? "turned on" : "turned off"), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // RoomAdapter.OnRoomClickListener implementation
    @Override
    public void onRoomClick(RoomModel room) {
        Toast.makeText(this, "Clicked: " + room.getName(), Toast.LENGTH_SHORT).show();
        // Here you would typically open the room detail activity
    }

    private void getHomeList() {
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> list) {
                if (!list.isEmpty()) {
                    setCurrentFamily(list.get(0));
                } else {
                    ToastUtil.showToast(MainActivity.this, "home list is null,plz create home");
                }
                ProgressUtil.hideLoading();
                openUIBizBundle();
                for (HomeBean homeBean : list) {
                    requestHomeDetail(homeBean.getHomeId());
                }
            }

            @Override
            public void onError(String s, String s1) {
                ProgressUtil.hideLoading();
                Toast.makeText(MainActivity.this, s + "\n" + s1, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 业务包接入后必须实现家庭服务(商城业务包可以不接入)
     * you should implementation AbsBizBundleFamilyService(mall bizbundle can not
     * implementation)
     */
    public void setCurrentFamily(HomeBean homeBean) {
        mCurrentFamilyName.setText(homeBean.getName());
        AbsBizBundleFamilyService familyService = MicroServiceManager.getInstance()
                .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
        familyService.shiftCurrentFamily(homeBean.getHomeId(), homeBean.getName());
    }

    private void setDarkMode() {
        ThingTheme.INSTANCE.enableDarkMode();
    }

    private void setLightMode() {
        ThingTheme.INSTANCE.enableNormalMode();
    }

    private void setSystemDefaultMode() {
        ThingTheme.INSTANCE.enableFollowSystem();
    }

    private void showFamilyDialog() {
        // This method is called from the family name click listener
        // The actual dialog is handled by FamilyDialogFragment
        FamilyDialogFragment dialogFragment = FamilyDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "FamilyDialogFragment");
    }

    private void showThemeSelectionDialog() {
        // 选项内容
        final String[] options = { "Dark Mode", "Light Mode", "Follow System" };
        // 当前选中的选项索引
        final int[] selectedOptionIndex = { -1 };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Theme Mode");
        builder.setSingleChoiceItems(options, selectedOptionIndex[0], (dialog, which) -> {
            // 记录用户选择的选项索引
            selectedOptionIndex[0] = which;
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            // 根据用户选择的选项执行相应的操作
            switch (selectedOptionIndex[0]) {
                case 0:
                    setDarkMode();
                    break;
                case 1:
                    setLightMode();
                    break;
                case 2:
                    setSystemDefaultMode();
                    break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void openUIBizBundle() {
        // This method is no longer needed as we're using the new UI
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThingHomeSdk.getHomeManagerInstance().unRegisterThingHomeChangeListener(mHomeChangeListener);
        ThingHomeSdk.getHomeManagerInstance().onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        schemeJump(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (routePresenter != null) {
            routePresenter.route(this);
            routePresenter = null;
        }
    }

    private boolean schemeJump(Intent intent) {
        routePresenter = RouterPresenter.parser(intent);
        return routePresenter != null;
    }
}