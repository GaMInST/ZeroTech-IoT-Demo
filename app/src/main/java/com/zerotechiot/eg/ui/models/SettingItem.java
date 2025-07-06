package com.zerotechiot.eg.ui.models;

public class SettingItem {
    private String title;
    private String subtitle;
    private Type type;
    private int iconRes;
    private boolean isEnabled;

    public enum Type {
        SECTION_HEADER,
        NAVIGATION,
        SWITCH,
        CHECKBOX
    }

    public SettingItem(String title, String subtitle, Type type) {
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.isEnabled = true;
    }

    public SettingItem(String title, String subtitle, Type type, int iconRes) {
        this(title, subtitle, type);
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}