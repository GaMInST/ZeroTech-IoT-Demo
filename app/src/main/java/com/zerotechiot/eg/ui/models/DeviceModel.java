package com.zerotechiot.eg.ui.models;

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
    private String iconUrl; // Added field for icon URL

    public DeviceModel(String id, String name, String type, String roomName, String roomId, String iconUrl) { // Added iconUrl to constructor
        this.id = id;
        this.name = name;
        this.type = type;
        this.roomName = roomName;
        this.roomId = roomId;
        this.isOnline = true; // Default, can be updated
        this.isOn = false;    // Default, can be updated
        this.brightness = 100;
        this.temperature = 2700;
        this.status = "Offline"; // Default, can be updated
        this.lastSeen = System.currentTimeMillis();
        this.iconUrl = iconUrl; // Assign iconUrl
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
        updateStatus(); // Update status when online state changes
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
        updateStatus(); // Update status when power state changes
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        updateStatus(); // Status might depend on brightness for some devices
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
        updateStatus(); // Status might depend on temperature
    }

    public String getStatus() {
        // Consider consolidating status updates here or relying on getStatusText()
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

    // Getter and Setter for iconUrl
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    // Helper methods
    public String getStatusText() {
        if (!isOnline) {
            return "Offline";
        }
        if (!isOn) {
            return "Off";
        }

        if (type == null) {
            return "On"; // Default for unknown type but on
        }

        // It's safer to use a default and then specify for known types
        String currentStatus = "On";
        switch (type.toLowerCase()) {
            case "light":
                currentStatus = "On • " + brightness + "%"; // Simplified brightness status
                break;
            // case "switch": // "On" is already default
            //     break;
            case "thermostat":
                currentStatus = "On • " + temperature + "°C";
                break;
            // Add other device types as needed
        }
        return currentStatus;
    }

    public void toggle() {
        if (isOnline) {
            setOn(!isOn); // This will call updateStatus()
        }
    }

    private void updateStatus() {
        // This method will now reflect the more detailed status from getStatusText()
        // or be simplified if getStatusText() is the primary source of truth for display
        if (!isOnline) {
            status = "Offline";
        } else if (!isOn) {
            status = "Off";
        } else {
            // Re-evaluate how 'status' field is used vs getStatusText()
            // For now, let's keep it simple for the 'status' field.
            // getStatusText() will provide the more detailed display string.
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
