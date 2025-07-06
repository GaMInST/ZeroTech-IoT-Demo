package com.zerotechiot.eg;

import android.app.AlertDialog;
import android.content.Intent;
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

        // TODO 此处只是演示代码，集成时请在登录成功后调用
        // This method must be called after successful login
        BizBundleInitializer.onLogin();

        Log.i("SceneMainActivity", "onCreate");
        LargeScreen.INSTANCE.modeChanged(this);

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
        logout.setOnClickListener(v -> {
            ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
                @Override
                public void onSuccess() {
                    // 演示代码
                    // demo use only start
                    LoginHelper.reLogin(MainActivity.this, false);
                    // demo use only end

                    // 退出成功后必须调用此方法
                    // This method must be called on exit.
                    BizBundleInitializer.onLogout(MainActivity.this);
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                    L.e("tuya logout", errorCode + " " + errorMsg);
                }
            });
        });

        // Quick action buttons
        MaterialButton quickAllOn = findViewById(R.id.quick_all_on);
        quickAllOn.setOnClickListener(v -> turnAllDevicesOn());

        MaterialButton quickAllOff = findViewById(R.id.quick_all_off);
        quickAllOff.setOnClickListener(v -> turnAllDevicesOff());

        // Setup all the existing button click listeners
        setupExistingButtonListeners();
    }

    private void setupExistingButtonListeners() {
        TuyaModuleIntegrationService integrationService = new TuyaModuleIntegrationService(this);

        // Device Control
        MaterialButton panel = findViewById(R.id.panel);
        panel.setOnClickListener(v -> integrationService.launchDeviceControl());

        // Smart Scenes
        MaterialButton scene = findViewById(R.id.scene);
        scene.setOnClickListener(v -> integrationService.launchSceneManagement());

        // Device Pairing
        MaterialButton activator = findViewById(R.id.activator);
        activator.setOnClickListener(v -> integrationService.launchDevicePairing());

        // Multi Control
        MaterialButton control = findViewById(R.id.control);
        control.setOnClickListener(v -> integrationService.launchDeviceManagement());

        // Camera Control
        MaterialButton ipc = findViewById(R.id.ipc);
        ipc.setOnClickListener(v -> integrationService.launchIPCControl());

        // Device Store
        MaterialButton mall = findViewById(R.id.mall);
        mall.setOnClickListener(v -> integrationService.launchMall());

        // Cloud Storage
        MaterialButton cloudStorage = findViewById(R.id.cloud_storage);
        cloudStorage.setOnClickListener(v -> integrationService.launchCloudStorage());

        // Message Center
        MaterialButton message = findViewById(R.id.message);
        message.setOnClickListener(v -> integrationService.launchMessageCenter());

        // Help & Feedback
        MaterialButton feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(v -> integrationService.launchFeedback());

        // Firmware Update
        MaterialButton ota = findViewById(R.id.ota);
        ota.setOnClickListener(v -> integrationService.launchOTAUpdates());

        // Home Management
        MaterialButton family = findViewById(R.id.family);
        family.setOnClickListener(v -> integrationService.launchFamilyManagement());

        // Device Details
        MaterialButton deviceDetail = findViewById(R.id.device_detail);
        deviceDetail.setOnClickListener(v -> integrationService.launchDeviceDetails());

        // Location Services
        MaterialButton location = findViewById(R.id.location);
        location.setOnClickListener(v -> integrationService.launchLocationServices());

        // Device Groups
        MaterialButton groupManager = findViewById(R.id.groupmanager);
        groupManager.setOnClickListener(v -> integrationService.launchGroupManagement());

        // Voice Assistant
        MaterialButton alexaGoogleBind = findViewById(R.id.alexa_google_bind);
        alexaGoogleBind.setOnClickListener(v -> integrationService.launchVoiceAssistant());

        // Light Scenes
        MaterialButton lightScene = findViewById(R.id.light_scene);
        lightScene.setOnClickListener(v -> integrationService.launchLightScenes());

        // Share Devices
        MaterialButton share = findViewById(R.id.share);
        share.setOnClickListener(v -> integrationService.launchShare());

        // Mini Apps
        MaterialButton miniapp = findViewById(R.id.miniapp);
        miniapp.setOnClickListener(v -> integrationService.launchMiniApps());

        // Third Party Services
        MaterialButton thirdService = findViewById(R.id.third_service);
        thirdService.setOnClickListener(v -> integrationService.launchThirdPartyServices());

        // Marketing
        MaterialButton marketing = findViewById(R.id.marketing);
        marketing.setOnClickListener(v -> integrationService.launchMarketing());

        // Speech Recognition
        MaterialButton speech = findViewById(R.id.speech);
        speech.setOnClickListener(v -> integrationService.launchSpeechRecognition());
    }

    private void loadSampleData() {
        // Load real devices from Tuya SDK instead of dummy data
        loadRealDevices();
        loadRealRooms();
    }

    private void loadRealDevices() {
        // Get the current home and load real devices
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> list) {
                if (!list.isEmpty()) {
                    HomeBean currentHome = list.get(0);
                    loadDevicesFromHome(currentHome.getHomeId());
                } else {
                    // Fallback to sample data if no homes exist
                    loadFallbackDevices();
                }
            }

            @Override
            public void onError(String s, String s1) {
                // Fallback to sample data on error
                loadFallbackDevices();
            }
        });
    }

    private void loadDevicesFromHome(long homeId) {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(@NonNull HomeBean homeBean) {
                List<DeviceBean> tuyaDevices = homeBean.getDeviceList();
                devices.clear();

                if (tuyaDevices != null && !tuyaDevices.isEmpty()) {
                    for (DeviceBean tuyaDevice : tuyaDevices) {
                        // Get room information from the home
                        String roomName = "Unknown Room";
                        String roomId = "0";

                        // Note: DeviceBean doesn't have getRoomId() method, so we use default room info
                        // In a real implementation, you would need to map devices to rooms differently

                        DeviceModel device = new DeviceModel(
                                tuyaDevice.getDevId(),
                                tuyaDevice.getName(),
                                tuyaDevice.getProductId(),
                                roomName,
                                roomId);
                        device.setOnline(tuyaDevice.getIsOnline());
                        device.setOn(tuyaDevice.getIsOnline() && tuyaDevice.getIsOnline());
                        devices.add(device);
                    }
                } else {
                    // Load fallback devices if no real devices found
                    loadFallbackDevices();
                }

                deviceAdapter.setDevices(devices);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                // Fallback to sample data on error
                loadFallbackDevices();
            }
        });
    }

    private void loadFallbackDevices() {
        // Load sample devices as fallback
        devices.clear();
        devices.add(new DeviceModel("1", "Living Room Light", "light", "Living Room", "1"));
        devices.add(new DeviceModel("2", "Bedroom Light", "light", "Bedroom", "2"));
        devices.add(new DeviceModel("3", "Kitchen Switch", "switch", "Kitchen", "3"));
        devices.add(new DeviceModel("4", "Bathroom Fan", "fan", "Bathroom", "4"));

        // Set some devices as online and on
        devices.get(0).setOnline(true);
        devices.get(0).setOn(true);
        devices.get(0).setBrightness(80);

        devices.get(1).setOnline(true);
        devices.get(1).setOn(false);

        devices.get(2).setOnline(true);
        devices.get(2).setOn(true);

        devices.get(3).setOnline(false);

        deviceAdapter.setDevices(devices);
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
                    // Fallback to sample rooms
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
                    // Load fallback rooms if no real rooms found
                    loadFallbackRooms();
                }

                roomAdapter.setRooms(rooms);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                // Fallback to sample rooms on error
                loadFallbackRooms();
            }
        });
    }

    private void loadFallbackRooms() {
        // Load sample rooms as fallback
        rooms.clear();
        rooms.add(new RoomModel("1", "Living Room", "living"));
        rooms.add(new RoomModel("2", "Bedroom", "bedroom"));
        rooms.add(new RoomModel("3", "Kitchen", "kitchen"));
        rooms.add(new RoomModel("4", "Bathroom", "bathroom"));
        roomAdapter.setRooms(rooms);
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
            deviceModel.setOn(isOn);
            Toast.makeText(this, deviceModel.getName() + " " + (isOn ? "turned on" : "turned off"), Toast.LENGTH_SHORT)
                    .show();
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