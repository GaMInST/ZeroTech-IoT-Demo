package com.zerotechiot.eg.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.control.PluginControlService;
import com.thingclips.smart.control.plug.api.IPluginControlService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.panelcaller.api.AbsPanelCallerService;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.zerotechiot.eg.ui.models.DeviceModel;
import com.google.gson.Gson;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to handle real device control using Tuya SDK
 */
public class DeviceControlService {
    private static final String TAG = "DeviceControlService";
    
    private Context context;
    private IPluginControlService pluginControlService;
    private AbsPanelCallerService panelCallerService;
    private AbsBizBundleFamilyService familyService;

    public DeviceControlService(Context context) {
        this.context = context;
        initializeServices();
    }

    private void initializeServices() {
        try {
            // Initialize the control service
            BizBundleInitializer.registerService(IPluginControlService.class, new PluginControlService());
            pluginControlService = MicroServiceManager.getInstance()
                    .findServiceByInterface(IPluginControlService.class.getName());

            // Initialize the panel caller service
            panelCallerService = MicroContext.getServiceManager()
                    .findServiceByInterface(AbsPanelCallerService.class.getName());

            // Get family service
            familyService = MicroServiceManager.getInstance()
                    .findServiceByInterface(AbsBizBundleFamilyService.class.getName());

        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize services", e);
        }
    }

    /**
     * Load real devices from Tuya SDK
     */
    public void loadRealDevices(DeviceLoadCallback callback) {
        Log.d(TAG, "Starting to load real devices from Tuya account...");
        
        // First check if we have a family service and current home
        if (familyService == null) {
            Log.e(TAG, "Family service is null");
            callback.onError("Family service not available");
            return;
        }
        
        long currentHomeId = familyService.getCurrentHomeId();
        if (currentHomeId == 0) {
            Log.w(TAG, "No current home selected, trying to get home list...");
            // Try to get the first available home
            ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
                @Override
                public void onSuccess(List<HomeBean> list) {
                    if (list != null && !list.isEmpty()) {
                        Log.d(TAG, "Found " + list.size() + " homes, using first one");
                        loadDevicesFromHome(list.get(0).getHomeId(), callback);
                    } else {
                        Log.w(TAG, "No homes found");
                        callback.onError("No homes found. Please create a home first.");
                    }
                }

                @Override
                public void onError(String s, String s1) {
                    Log.e(TAG, "Failed to get home list: " + s + " - " + s1);
                    callback.onError("Failed to get home list: " + s1);
                }
            });
        } else {
            Log.d(TAG, "Using current home ID: " + currentHomeId);
            loadDevicesFromHome(currentHomeId, callback);
        }
    }
    
    private void loadDevicesFromHome(long homeId, DeviceLoadCallback callback) {
        Log.d(TAG, "Loading devices from home ID: " + homeId);
        
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                List<DeviceBean> tuyaDevices = homeBean.getDeviceList();
                List<DeviceModel> deviceModels = new ArrayList<>();

                Log.d(TAG, "Home detail loaded successfully");
                Log.d(TAG, "Found " + (tuyaDevices != null ? tuyaDevices.size() : 0) + " devices");

                if (tuyaDevices != null && !tuyaDevices.isEmpty()) {
                    for (DeviceBean tuyaDevice : tuyaDevices) {
                        Log.d(TAG, "Processing device: " + tuyaDevice.getName() + " (ID: " + tuyaDevice.getDevId() + ")");
                        DeviceModel deviceModel = convertToDeviceModel(tuyaDevice, homeBean);
                        deviceModels.add(deviceModel);
                    }
                    Log.d(TAG, "Successfully converted " + deviceModels.size() + " devices");
                    callback.onSuccess(deviceModels);
                } else {
                    Log.w(TAG, "No devices found in home");
                    callback.onError("No devices found in your home. Please add some devices first.");
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.e(TAG, "Failed to load home detail: " + errorCode + " - " + errorMsg);
                callback.onError("Failed to load devices: " + errorMsg);
            }
        });
    }

    /**
     * Convert Tuya DeviceBean to our DeviceModel
     */
    private DeviceModel convertToDeviceModel(DeviceBean tuyaDevice, HomeBean homeBean) {
        // Get room information
        String roomName = "Unknown Room";
        String roomId = "0";

        try {
            List<com.thingclips.smart.home.sdk.bean.RoomBean> rooms = homeBean.getRooms();
            if (rooms != null && !rooms.isEmpty()) {
                // For now, assign to first room. In production, you'd map devices to rooms properly
                roomName = rooms.get(0).getName();
                roomId = String.valueOf(rooms.get(0).getRoomId());
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not get room information", e);
        }

        DeviceModel device = new DeviceModel(
                tuyaDevice.getDevId(),
                tuyaDevice.getName(),
                getDeviceTypeFromProductId(tuyaDevice.getProductId()),
                roomName,
                roomId
        );
        
        device.setOnline(tuyaDevice.getIsOnline());
        // For now, assume device is on if online. In real implementation, you'd get actual state
        device.setOn(tuyaDevice.getIsOnline());
        
        return device;
    }

    /**
     * Determine device type from product ID
     */
    private String getDeviceTypeFromProductId(String productId) {
        if (productId == null) return "unknown";
        
        String lowerProductId = productId.toLowerCase();
        
        // IR devices
        if (lowerProductId.contains("ir") || lowerProductId.contains("remote") || lowerProductId.contains("controller")) {
            return "ir";
        }
        
        // Switches and smart plugs
        if (lowerProductId.contains("switch") || lowerProductId.contains("outlet") || lowerProductId.contains("plug")) {
            return "switch";
        }
        
        // ZigBee hubs
        if (lowerProductId.contains("zigbee") || lowerProductId.contains("hub") || lowerProductId.contains("gateway")) {
            return "hub";
        }
        
        // Gas sensors
        if (lowerProductId.contains("gas") || lowerProductId.contains("sensor") || lowerProductId.contains("detector")) {
            return "sensor";
        }
        
        // Door contacts
        if (lowerProductId.contains("door") || lowerProductId.contains("contact") || lowerProductId.contains("magnet")) {
            return "contact";
        }
        
        // Lights
        if (lowerProductId.contains("light") || lowerProductId.contains("bulb") || lowerProductId.contains("lamp")) {
            return "light";
        }
        
        // Fans
        if (lowerProductId.contains("fan")) {
            return "fan";
        }
        
        // Curtains
        if (lowerProductId.contains("curtain") || lowerProductId.contains("blind")) {
            return "curtain";
        }
        
        // Thermostats
        if (lowerProductId.contains("thermostat") || lowerProductId.contains("temp")) {
            return "thermostat";
        }
        
        // Cameras
        if (lowerProductId.contains("camera") || lowerProductId.contains("ipc")) {
            return "camera";
        }
        
        // Locks
        if (lowerProductId.contains("lock")) {
            return "lock";
        }
        
        return "unknown";
    }

    /**
     * Toggle device power state using panel caller service
     */
    public void toggleDevice(String deviceId, boolean isOn, DeviceToggleCallback callback) {
        Log.d(TAG, "Toggling device " + deviceId + " to " + (isOn ? "ON" : "OFF"));
        
        // Since direct device control APIs are not available in this SDK version,
        // we'll use the panel caller service to launch the device control panel
        // and let the user control the device through the native Tuya interface
        if (panelCallerService != null && context instanceof android.app.Activity) {
            panelCallerService.goPanelWithCheckAndTip((android.app.Activity) context, deviceId);
            // For now, we'll assume success since the panel will handle the actual control
            callback.onSuccess(isOn);
        } else {
            callback.onError("Device control panel not available");
        }
    }

    /**
     * Launch device control panel
     */
    public void launchDeviceControl(String deviceId) {
        if (panelCallerService != null && context instanceof android.app.Activity) {
            panelCallerService.goPanelWithCheckAndTip((android.app.Activity) context, deviceId);
        } else {
            Toast.makeText(context, "Device control not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get device statistics
     */
    public void getDeviceStats(DeviceStatsCallback callback) {
        if (familyService == null || familyService.getCurrentHomeId() == 0) {
            callback.onError("No home selected");
            return;
        }

        ThingHomeSdk.newHomeInstance(familyService.getCurrentHomeId())
                .getHomeDetail(new IThingHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean homeBean) {
                        List<DeviceBean> devices = homeBean.getDeviceList();
                        int totalDevices = devices.size();
                        int onlineDevices = 0;

                        for (DeviceBean device : devices) {
                            if (device.getIsOnline()) {
                                onlineDevices++;
                            }
                        }

                        callback.onSuccess(totalDevices, onlineDevices);
                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        callback.onError("Failed to get device stats: " + errorMsg);
                    }
                });
    }

    // Callback interfaces
    public interface DeviceLoadCallback {
        void onSuccess(List<DeviceModel> devices);
        void onError(String error);
    }

    public interface DeviceToggleCallback {
        void onSuccess(boolean newState);
        void onError(String error);
    }

    public interface DeviceStatsCallback {
        void onSuccess(int totalDevices, int onlineDevices);
        void onError(String error);
    }
} 