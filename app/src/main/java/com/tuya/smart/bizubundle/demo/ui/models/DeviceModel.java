package com.tuya.smart.bizubundle.demo.ui.models;

public class DeviceModel {
    private String id;
    private String name;
    private String type;
    private String roomName;
    private String roomId;
    private boolean isOnline;
    private boolean isOn;
    private int brightness;
    private int temperature;
    private String status;
    private long lastSeen;

    public DeviceModel(String id, String name, String type, String roomName, String roomId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.roomName = roomName;
        this.roomId = roomId;
        this.isOnline = true;
        this.isOn = false;
        this.brightness = 100;
        this.temperature = 2700;
        this.status = "Offline";
        this.lastSeen = System.currentTimeMillis();
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    // Helper methods
    public String getStatusText() {
        if (!isOnline) {
            return "Offline";
        }
        if (!isOn) {
            return "Off";
        }

        switch (type.toLowerCase()) {
            case "light":
                return "On • " + brightness + "% brightness";
            case "switch":
                return "On";
            case "thermostat":
                return "On • " + temperature + "°C";
            default:
                return "On";
        }
    }

    public void toggle() {
        if (isOnline) {
            isOn = !isOn;
            updateStatus();
        }
    }

    private void updateStatus() {
        if (!isOnline) {
            status = "Offline";
        } else if (!isOn) {
            status = "Off";
        } else {
            status = "On";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DeviceModel that = (DeviceModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}