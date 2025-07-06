package com.zerotechiot.eg.ui.models;

public class AutomationModel {
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean isActive;

    public AutomationModel(String id, String name, String description, String icon, boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isActive = isActive;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}