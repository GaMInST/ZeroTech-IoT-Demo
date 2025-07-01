package com.tuya.smart.bizubundle.demo.ui.models;

import java.util.ArrayList;
import java.util.List;

public class RoomModel {
    private String id;
    private String name;
    private String type;
    private int deviceCount;
    private int onlineDeviceCount;
    private List<DeviceModel> devices;
    private boolean isActive;
    private long lastActivity;

    public RoomModel(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.deviceCount = 0;
        this.onlineDeviceCount = 0;
        this.devices = new ArrayList<>();
        this.isActive = false;
        this.lastActivity = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getOnlineDeviceCount() {
        return onlineDeviceCount;
    }

    public void setOnlineDeviceCount(int onlineDeviceCount) {
        this.onlineDeviceCount = onlineDeviceCount;
    }

    public List<DeviceModel> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceModel> devices) {
        this.devices = devices;
        updateDeviceCounts();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    // Helper methods
    public void addDevice(DeviceModel device) {
        devices.add(device);
        updateDeviceCounts();
    }

    public void removeDevice(DeviceModel device) {
        devices.remove(device);
        updateDeviceCounts();
    }

    public void updateDeviceCounts() {
        deviceCount = devices.size();
        onlineDeviceCount = (int) devices.stream()
                .filter(DeviceModel::isOnline)
                .count();
        isActive = onlineDeviceCount > 0;
    }

    public String getStatusText() {
        if (deviceCount == 0) {
            return "No devices";
        }
        if (onlineDeviceCount == 0) {
            return deviceCount + " devices offline";
        }
        return onlineDeviceCount + " of " + deviceCount + " devices online";
    }

    public boolean hasActiveDevices() {
        return devices.stream().anyMatch(device -> device.isOnline() && device.isOn());
    }

    public void turnAllDevicesOn() {
        devices.stream()
                .filter(DeviceModel::isOnline)
                .forEach(device -> device.setOn(true));
    }

    public void turnAllDevicesOff() {
        devices.stream()
                .filter(DeviceModel::isOnline)
                .forEach(device -> device.setOn(false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoomModel roomModel = (RoomModel) o;
        return id.equals(roomModel.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}