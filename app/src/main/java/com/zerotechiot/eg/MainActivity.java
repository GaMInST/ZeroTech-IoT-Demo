package com.zerotechiot.eg;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
// import android.view.animation.Animation; // Unused
// import android.view.animation.AnimationUtils; // Unused
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
// import com.google.android.material.switchmaterial.SwitchMaterial; // Unused

import com.thingclips.basic.split.LargeScreen;
// import com.thingclips.smart.android.common.utils.L; // Using android.util.Log
import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.api.MicroContext; // CORRECTED IMPORT
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
// import com.thingclips.smart.demo_login.base.utils.LoginHelper; // Not directly used
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.api.IThingHomeChangeListener;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.bean.RoomBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.panelcaller.api.AbsPanelCallerService;
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

    private static final String TAG = "MainActivity"; // Added TAG for logging

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

    private final IThingHomeChangeListener mHomeChangeListener = new IThingHomeChangeListener() {
        @Override
        public void onHomeAdded(long homeId) {
            requestHomeDetail(homeId);
        }

        @Override
        public void onHomeInvite(long homeId, String homeName) {
            // TODO: Handle home invitation
        }

        @Override
        public void onHomeRemoved(long l) {
            // TODO: Handle home removal
        }

        @Override
        public void onHomeInfoChanged(long l) {
            // TODO: Handle home info change
        }

        @Override
        public void onSharedDeviceList(List<DeviceBean> list) {
            // TODO: Handle shared device list change
        }

        @Override
        public void onSharedGroupList(List<GroupBean> list) {
            // TODO: Handle shared group list change
        }

        @Override
        public void onServerConnectSuccess() {
            // TODO: Handle server connection success
        }
    };

    private void requestHomeDetail(long id) {
        ThingHomeSdk.newHomeInstance(id).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                // Optionally update UI or local data with home details
                Log.d(TAG, "Successfully fetched details for homeId: " + id);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.e(TAG, "Error fetching home details for homeId: " + id + ". Code: " + errorCode + ", Msg: " + errorMsg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("ZeroTechPrefs", MODE_PRIVATE);

        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        BizBundleInitializer.onLogin();

        Log.i(TAG, "onCreate");
        LargeScreen.INSTANCE.modeChanged(this);

        deviceControlService = new DeviceControlService(this);

        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        loadInitialData(); // Renamed from loadSampleData for clarity

        mCurrentFamilyName = findViewById(R.id.current_family_name);
        mCurrentFamilyName.setOnClickListener(v -> {
            FamilyDialogFragment dialogFragment = FamilyDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "FamilyDialogFragment");
        });

        ProgressUtil.showLoading(this, "Loading...");
        getHomeList();
        ThingHomeSdk.getHomeManagerInstance().registerThingHomeChangeListener(mHomeChangeListener);
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finishAffinity(); // Finish all activities in the task
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
        // mCurrentFamilyName initialized in onCreate after view setup
        roomsRecyclerView = findViewById(R.id.rooms_recycler_view);
        devicesRecyclerView = findViewById(R.id.devices_recycler_view);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_rooms) {
                startActivity(new Intent(this, RoomsActivity.class));
                return true;
            } else if (itemId == R.id.nav_scenes) {
                startActivity(new Intent(this, com.thingclips.smart.bizbundle.scene.demo.SceneActivity.class));
                return true;
            } else if (itemId == R.id.nav_automation) {
                startActivity(new Intent(this, AutomationActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
        fabAddDevice.setOnClickListener(v -> {
            try {
                startActivity(new Intent(this, DevicePairingActivity.class));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "Device Pairing feature is not available.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "DevicePairingActivity not found", e);
            }
        });
    }

    private void setupRecyclerViews() {
        roomAdapter = new RoomAdapter(this);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roomsRecyclerView.setAdapter(roomAdapter);

        deviceAdapter = new DeviceAdapter(new ArrayList<>(), this); // Initialize with empty list
        devicesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        devicesRecyclerView.setAdapter(deviceAdapter);
    }

    private void setupClickListeners() {
        MaterialButton themeSwitch = findViewById(R.id.theme_switch);
        themeSwitch.setOnClickListener(v -> showThemeSelectionDialog());

        MaterialButton logoutBtn = findViewById(R.id.logout); // Changed variable name to avoid conflict
        logoutBtn.setOnClickListener(v -> showLogoutDialog());

        setupExistingButtonListeners();
    }

    private void setupExistingButtonListeners() {
        findViewById(R.id.panel).setOnClickListener(v -> 
            startActivity(new Intent(this, DeviceControlActivity.class))
        );

        findViewById(R.id.scene).setOnClickListener(v -> 
            startActivity(new Intent(this, com.thingclips.smart.bizbundle.scene.demo.SceneActivity.class))
        );

        findViewById(R.id.activator).setOnClickListener(v -> 
            startActivity(new Intent(this, DevicePairingActivity.class))
        );

        findViewById(R.id.control).setOnClickListener(v -> showMultiControlDialog());

        findViewById(R.id.ipc).setOnClickListener(v -> 
            Toast.makeText(this, "Camera functionality coming soon", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.mall).setOnClickListener(v -> 
            Toast.makeText(this, "Device store functionality coming soon", Toast.LENGTH_SHORT).show()
        );
    }

    private void showMultiControlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multi Control");
        builder.setMessage("Choose an action to perform on multiple devices:");

        String[] options = {
                "Turn All Devices On", "Turn All Devices Off", "Toggle All Lights",
                "Set All Lights to 50%", "Create Device Group", "Bulk Device Settings"
        };

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: turnAllDevicesOn(); break;
                case 1: turnAllDevicesOff(); break;
                case 2: toggleAllLights(); break;
                case 3: setAllLightsToPercentage(50); break;
                case 4: showCreateGroupDialog(); break;
                case 5: showBulkSettingsDialog(); break;
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void toggleAllLights() {
        int lightsToggled = 0;
        for (DeviceModel device : devices) {
            if ("light".equals(device.getType()) && device.isOnline()) {
                device.setOn(!device.isOn()); // This is a local change, actual control needs SDK call
                lightsToggled++;
            }
        }
        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, lightsToggled + " lights toggled (local UI update only)", Toast.LENGTH_SHORT).show();
    }

    private void setAllLightsToPercentage(int percentage) {
        int lightsUpdated = 0;
        for (DeviceModel device : devices) {
            if ("light".equals(device.getType()) && device.isOnline()) {
                device.setBrightness(percentage); // Local change
                lightsUpdated++;
            }
        }
        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, lightsUpdated + " lights set to " + percentage + "% (local UI update only)", Toast.LENGTH_SHORT).show();
    }

    private void showCreateGroupDialog() {
        // Placeholder for group creation UI
        Toast.makeText(this, "Group creation UI coming soon", Toast.LENGTH_SHORT).show();
    }

    private void showBulkSettingsDialog() {
        // Placeholder for bulk settings UI
        Toast.makeText(this, "Bulk settings UI coming soon", Toast.LENGTH_SHORT).show();
    }

    private void loadInitialData() {
        loadRealDevices();
        loadRealRooms();
        // updateHomeStats(); // Called within loadRealDevices/Rooms callbacks
    }

    private void loadRealDevices() {
        Log.d(TAG, "Loading real devices from Tuya account...");
        if (deviceControlService == null) {
            deviceControlService = new DeviceControlService(this);
        }
        deviceControlService.loadRealDevices(new DeviceControlService.DeviceLoadCallback() {
            @Override
            public void onSuccess(List<DeviceModel> realDevices) {
                runOnUiThread(() -> {
                    Log.d(TAG, "Successfully loaded " + realDevices.size() + " real devices");
                    devices.clear();
                    devices.addAll(realDevices);
                    if (deviceAdapter != null) {
                        deviceAdapter.setDevices(devices);
                    }
                    updateDeviceStats(); 

                    if (devices.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No devices found. Please add devices.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Loaded " + devices.size() + " devices.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.w(TAG, "Failed to load real devices: " + error);
                    Toast.makeText(MainActivity.this, "Error loading devices: " + error + ". Showing demo devices.", Toast.LENGTH_LONG).show();
                    loadFallbackDevices();
                });
            }
        });
    }

    private void loadFallbackDevices() {
        devices.clear();
        DeviceModel livingRoomLight = new DeviceModel("1", "Living Room Light", "light", "Living Room", "1", null);
        livingRoomLight.setOnline(true); livingRoomLight.setOn(true); livingRoomLight.setBrightness(80);
        devices.add(livingRoomLight);

        DeviceModel bedroomLight = new DeviceModel("2", "Bedroom Light", "light", "Bedroom", "2", null);
        bedroomLight.setOnline(true); bedroomLight.setOn(false);
        devices.add(bedroomLight);

        if (deviceAdapter != null) deviceAdapter.setDevices(devices);
        updateDeviceStats();
    }

    private void loadRealRooms() {
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeList) {
                if (homeList != null && !homeList.isEmpty()) {
                    HomeBean currentHome = homeList.get(0); // Assuming first home
                    loadRoomsFromHome(currentHome.getHomeId());
                } else {
                    Log.w(TAG, "No homes found, loading fallback rooms.");
                    loadFallbackRooms();
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.e(TAG, "Error querying home list: " + errorCode + " | " + errorMsg);
                loadFallbackRooms();
            }
        });
    }

    private void loadRoomsFromHome(long homeId) {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(@NonNull HomeBean homeBean) {
                rooms.clear();
                List<RoomBean> tuyaRooms = homeBean.getRooms();
                if (tuyaRooms != null && !tuyaRooms.isEmpty()) {
                    for (RoomBean tuyaRoom : tuyaRooms) {
                        rooms.add(new RoomModel(String.valueOf(tuyaRoom.getRoomId()), tuyaRoom.getName(), tuyaRoom.getName().toLowerCase().replace(" ", "_")));
                    }
                } else {
                    Log.w(TAG, "No rooms in home " + homeId + ", creating default rooms.");
                    createDefaultRooms(homeId); // Or loadFallbackRooms();
                }
                if (roomAdapter != null) roomAdapter.setRooms(rooms);
                updateRoomStats(); 
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.e(TAG, "Error getting home detail: " + errorCode + " | " + errorMsg);
                loadFallbackRooms();
            }
        });
    }

    private void createDefaultRooms(long homeId) {
        // This might be better handled by loadFallbackRooms if no specific defaults are needed for a homeId
        Log.d(TAG, "Creating default rooms for homeId: " + homeId);
        loadFallbackRooms(); // Simplified to use common fallback
    }

    private void loadFallbackRooms() {
        rooms.clear();
        rooms.add(new RoomModel("1", "Living Room", "living_room_icon"));
        rooms.add(new RoomModel("2", "Bedroom", "bedroom_icon"));
        if (roomAdapter != null) roomAdapter.setRooms(rooms);
        updateRoomStats();
    }

    private void updateDeviceStats() {
        int onlineDevices = 0;
        for (DeviceModel device : devices) {
            if (device.isOnline()) onlineDevices++;
        }
        TextView welcomeStatsText = findViewById(R.id.welcome_stats_text);
        if (welcomeStatsText != null) {
            welcomeStatsText.setText(onlineDevices + " devices online • " + rooms.size() + " rooms active");
        }
    }

    private void updateRoomStats() {
        // updateDeviceStats already updates room count in welcome_stats_text
        // If separate room stats UI elements exist, update them here.
        Log.d(TAG, rooms.size() + " rooms currently loaded.");
         // Ensure device stats are also up-to-date as it shows room count
        if (findViewById(R.id.welcome_stats_text) != null) { 
            updateDeviceStats();
        }
    }

    // Removed updateHomeStats() as its calls are integrated into specific load methods

    private void turnAllDevicesOn() {
        for (DeviceModel device : devices) {
            if (device.isOnline()) device.setOn(true); // Local change, SDK call needed for real control
        }
        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All devices turned on (local UI update only)", Toast.LENGTH_SHORT).show();
    }

    private void turnAllDevicesOff() {
        for (DeviceModel device : devices) {
            if (device.isOnline()) device.setOn(false); // Local change
        }
        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged();
        Toast.makeText(this, "All devices turned off (local UI update only)", Toast.LENGTH_SHORT).show();
    }

    // DeviceAdapter.OnDeviceClickListener implementation
    @Override
    public void onDeviceClick(Object device) {
        AbsPanelCallerService panelCallerService = MicroContext.getServiceManager()
                .findServiceByInterface(AbsPanelCallerService.class.getName());

        if (panelCallerService == null) {
            Toast.makeText(this, "Panel service not available. Please ensure Panel BizBundle is included.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "PanelCallerService is null. Cannot launch panel.");
            return;
        }

        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            Log.d(TAG, "DeviceModel clicked: " + deviceModel.getName() + " ID: " + deviceModel.getId());
            panelCallerService.goPanelWithCheckAndTip(this, deviceModel.getId()); // CORRECTED CALL
        } else if (device instanceof DeviceBean) {
            DeviceBean deviceBean = (DeviceBean) device;
            Log.d(TAG, "DeviceBean clicked: " + deviceBean.getName() + " ID: " + deviceBean.getDevId());
            panelCallerService.goPanel(this, deviceBean); // CORRECTED CALL (pass object)
        } else {
            Log.w(TAG, "Clicked item is of unknown type: " + (device != null ? device.getClass().getName() : "null"));
            Toast.makeText(this, "Cannot open panel for this item type.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeviceToggle(Object device, boolean isOn) {
        if (deviceControlService == null) { 
            Log.e(TAG, "DeviceControlService is null in onDeviceToggle");
            Toast.makeText(this, "Device control service not available.", Toast.LENGTH_SHORT).show();
            return; 
        }
        if (device instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) device;
            deviceControlService.toggleDevice(deviceModel.getId(), isOn, new DeviceControlService.DeviceToggleCallback() {
                @Override
                public void onSuccess(boolean newState) {
                    runOnUiThread(() -> {
                        deviceModel.setOn(newState);
                        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, deviceModel.getName() + " " + (newState ? "turned on" : "turned off"), Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        // Revert UI change on error
                        // deviceModel.setOn(!isOn); // Already handled by adapter potentially, or handled by next data load
                        if (deviceAdapter != null) deviceAdapter.notifyDataSetChanged(); // Refresh to show actual state
                        Toast.makeText(MainActivity.this, "Failed to toggle " + deviceModel.getName() + ": " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else if (device instanceof DeviceBean) {
            DeviceBean deviceBean = (DeviceBean) device;
            // Example: Using DeviceControlService for DeviceBean as well, assuming it can handle it or adapt
            // This part might need specific SDK calls depending on how DeviceControlService is implemented for DeviceBeans
            Log.d(TAG, "Toggling DeviceBean: " + deviceBean.getName() + ". SDK implementation needed for full control.");
            Toast.makeText(this, "Toggling " + deviceBean.getName() + " (DeviceBean, requires SDK control)", Toast.LENGTH_SHORT).show();
            // For a real toggle, you'd use Tuya SDK's device control methods here, perhaps via deviceControlService
        }
    }

    @Override
    public void onRoomClick(RoomModel room) {
        Toast.makeText(this, "Clicked: " + room.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to RoomDetailActivity or similar
    }

    private void getHomeList() {
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeList) {
                ProgressUtil.hideLoading();
                if (homeList != null && !homeList.isEmpty()) {
                    setCurrentFamily(homeList.get(0)); // Set current family to the first one
                    // Optionally, loop through all homes to request details if needed for other purposes
                    // for (HomeBean homeBean : homeList) { requestHomeDetail(homeBean.getHomeId()); }
                } else {
                    ToastUtil.showToast(MainActivity.this, "Home list is empty. Please create a home.");
                    Log.w(TAG, "Home list is null or empty.");
                }
                // openUIBizBundle(); // Considered obsolete
            }

            @Override
            public void onError(String s, String s1) {
                ProgressUtil.hideLoading();
                Toast.makeText(MainActivity.this, "Failed to get home list: " + s + "\n" + s1, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error getting home list: " + s + " | " + s1);
            }
        });
    }

    public void setCurrentFamily(HomeBean homeBean) {
        if (mCurrentFamilyName != null) {
            mCurrentFamilyName.setText(homeBean.getName());
        }
        AbsBizBundleFamilyService familyService = MicroContext.getServiceManager() // CORRECTED to MicroContext
                .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
        if (familyService != null) {
            familyService.shiftCurrentFamily(homeBean.getHomeId(), homeBean.getName());
        } else {
            Log.e(TAG, "AbsBizBundleFamilyService not found.");
        }
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
        FamilyDialogFragment dialogFragment = FamilyDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "FamilyDialogFragment");
    }

    private void showThemeSelectionDialog() {
        final String[] options = { "Dark Mode", "Light Mode", "Follow System" };
        int currentSelection = -1;
        // TODO: Determine current theme selection from ThingTheme or SharedPreferences

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Theme Mode");
        builder.setSingleChoiceItems(options, currentSelection, (dialog, which) -> {
            switch (which) {
                case 0: setDarkMode(); break;
                case 1: setLightMode(); break;
                case 2: setSystemDefaultMode(); break;
            }
            dialog.dismiss(); // Dismiss after selection
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // private void openUIBizBundle() { /* Obsolete */ }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ThingHomeSdk.getHomeManagerInstance() != null) {
             ThingHomeSdk.getHomeManagerInstance().unRegisterThingHomeChangeListener(mHomeChangeListener);
             ThingHomeSdk.getHomeManagerInstance().onDestroy(); // Ensure this is the correct lifecycle call for the SDK
        }
       
        if (routePresenter != null) {
            routePresenter = null; // Clean up
        }
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
