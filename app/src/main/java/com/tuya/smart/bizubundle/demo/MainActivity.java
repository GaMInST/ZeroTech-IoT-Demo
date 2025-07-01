package com.tuya.smart.bizubundle.demo;

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
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.GroupBean;
import com.thingclips.smart.theme.ThingTheme;
import com.thingclips.smart.utils.ProgressUtil;
import com.thingclips.smart.utils.ToastUtil;
import com.tuya.smart.bizubundle.demo.ui.adapters.DeviceAdapter;
import com.tuya.smart.bizubundle.demo.ui.adapters.RoomAdapter;
import com.tuya.smart.bizubundle.demo.ui.models.DeviceModel;
import com.tuya.smart.bizubundle.demo.ui.models.RoomModel;

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
            // Handle bottom navigation item selection
            return true;
        });

        // Setup floating action button
        FloatingActionButton fabAddDevice = findViewById(R.id.fab_add_device);
        fabAddDevice.setOnClickListener(v -> {
            // Handle add device action
            Toast.makeText(this, "Add Device", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerViews() {
        // Setup rooms RecyclerView
        roomAdapter = new RoomAdapter(this);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roomsRecyclerView.setAdapter(roomAdapter);

        // Setup devices RecyclerView
        deviceAdapter = new DeviceAdapter();
        deviceAdapter.setOnDeviceClickListener(this);
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
        // Device Control
        MaterialButton panel = findViewById(R.id.panel);
        panel.setOnClickListener(v -> {
            // Navigate to device control
            Intent intent = new Intent(this, DeviceControlActivity.class);
            startActivity(intent);
        });

        // Smart Scenes
        MaterialButton scene = findViewById(R.id.scene);
        scene.setOnClickListener(v -> {
            // Navigate to scenes
            Intent intent = new Intent();
            intent.setClassName(this, "com.tuya.smart.bizbundle.scene.demo.SceneActivity");
            startActivity(intent);
        });

        // Device Pairing
        MaterialButton activator = findViewById(R.id.activator);
        activator.setOnClickListener(v -> {
            // Navigate to device pairing
            Intent intent = new Intent(this, DevicePairingActivity.class);
            startActivity(intent);
        });

        // Multi Control
        MaterialButton control = findViewById(R.id.control);
        control.setOnClickListener(v -> {
            // Navigate to multi control
            Toast.makeText(this, "Multi control feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Camera Control
        MaterialButton ipc = findViewById(R.id.ipc);
        ipc.setOnClickListener(v -> {
            // Navigate to camera control
            Toast.makeText(this, "Camera control feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Device Store
        MaterialButton mall = findViewById(R.id.mall);
        mall.setOnClickListener(v -> {
            // Navigate to device store
            Toast.makeText(this, "Device store feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Cloud Storage
        MaterialButton cloudStorage = findViewById(R.id.cloud_storage);
        cloudStorage.setOnClickListener(v -> {
            // Navigate to cloud storage
            Toast.makeText(this, "Cloud storage feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Message Center
        MaterialButton message = findViewById(R.id.message);
        message.setOnClickListener(v -> {
            // Navigate to message center
            Toast.makeText(this, "Message center feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Help & Feedback
        MaterialButton feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(v -> {
            // Navigate to help & feedback
            Toast.makeText(this, "Help & feedback feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Firmware Update
        MaterialButton ota = findViewById(R.id.ota);
        ota.setOnClickListener(v -> {
            // Navigate to firmware update
            Toast.makeText(this, "Firmware update feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Home Management
        MaterialButton family = findViewById(R.id.family);
        family.setOnClickListener(v -> {
            // Navigate to home management
            Toast.makeText(this, "Home management feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Device Details
        MaterialButton deviceDetail = findViewById(R.id.device_detail);
        deviceDetail.setOnClickListener(v -> {
            // Navigate to device details
            Toast.makeText(this, "Device details feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Location Services
        MaterialButton location = findViewById(R.id.location);
        location.setOnClickListener(v -> {
            // Navigate to location services
            Toast.makeText(this, "Location services feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Device Groups
        MaterialButton groupManager = findViewById(R.id.groupmanager);
        groupManager.setOnClickListener(v -> {
            // Navigate to device groups
            Toast.makeText(this, "Device groups feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Voice Assistant
        MaterialButton alexaGoogleBind = findViewById(R.id.alexa_google_bind);
        alexaGoogleBind.setOnClickListener(v -> {
            // Navigate to voice assistant
            Toast.makeText(this, "Voice assistant feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Light Scenes
        MaterialButton lightScene = findViewById(R.id.light_scene);
        lightScene.setOnClickListener(v -> {
            // Navigate to light scenes
            Toast.makeText(this, "Light scenes feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Share Devices
        MaterialButton share = findViewById(R.id.share);
        share.setOnClickListener(v -> {
            // Navigate to share devices
            Toast.makeText(this, "Share devices feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Mini Apps
        MaterialButton miniapp = findViewById(R.id.miniapp);
        miniapp.setOnClickListener(v -> {
            // Navigate to mini apps
            Toast.makeText(this, "Mini apps feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Third Party Services
        MaterialButton thirdService = findViewById(R.id.third_service);
        thirdService.setOnClickListener(v -> {
            // Navigate to third party services
            Toast.makeText(this, "Third party services feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Marketing
        MaterialButton marketing = findViewById(R.id.marketing);
        marketing.setOnClickListener(v -> {
            // Navigate to marketing
            Toast.makeText(this, "Marketing feature coming soon", Toast.LENGTH_SHORT).show();
        });

        // Speech Recognition
        MaterialButton speech = findViewById(R.id.speech);
        speech.setOnClickListener(v -> {
            // Navigate to speech recognition
            Toast.makeText(this, "Speech recognition feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSampleData() {
        // Load sample rooms
        rooms.clear();
        rooms.add(new RoomModel("1", "Living Room", "living"));
        rooms.add(new RoomModel("2", "Bedroom", "bedroom"));
        rooms.add(new RoomModel("3", "Kitchen", "kitchen"));
        rooms.add(new RoomModel("4", "Bathroom", "bathroom"));
        roomAdapter.setRooms(rooms);

        // Load sample devices
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
    public void onDeviceClick(DeviceModel device) {
        // Launch device control activity
        Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra("device_id", device.getId());
        intent.putExtra("device_name", device.getName());
        intent.putExtra("device_type", device.getType());
        startActivity(intent);
    }

    @Override
    public void onDeviceToggle(DeviceModel device, boolean isOn) {
        device.setOn(isOn);
        Toast.makeText(this, device.getName() + " " + (isOn ? "turned on" : "turned off"), Toast.LENGTH_SHORT).show();
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