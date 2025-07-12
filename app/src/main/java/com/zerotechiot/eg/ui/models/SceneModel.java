package com.zerotechiot.eg.ui.models;

import java.util.List;

public class SceneModel {
    private String id;
    private String name;
    private String description;
    private String background;
    private int conditionCount;
    private int actionCount;
    private boolean enabled;
    private List<SceneCondition> conditions;
    private List<SceneAction> actions;
    private long lastExecuted;

    public SceneModel(String id, String name, String background, int conditionCount, int actionCount) {
        this.id = id;
        this.name = name;
        this.background = background;
        this.conditionCount = conditionCount;
        this.actionCount = actionCount;
        this.enabled = true;
        this.lastExecuted = 0;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getConditionCount() {
        return conditionCount;
    }

    public void setConditionCount(int conditionCount) {
        this.conditionCount = conditionCount;
    }

    public int getActionCount() {
        return actionCount;
    }

    public void setActionCount(int actionCount) {
        this.actionCount = actionCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<SceneCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<SceneCondition> conditions) {
        this.conditions = conditions;
        this.conditionCount = conditions != null ? conditions.size() : 0;
    }

    public List<SceneAction> getActions() {
        return actions;
    }

    public void setActions(List<SceneAction> actions) {
        this.actions = actions;
        this.actionCount = actions != null ? actions.size() : 0;
    }

    public long getLastExecuted() {
        return lastExecuted;
    }

    public void setLastExecuted(long lastExecuted) {
        this.lastExecuted = lastExecuted;
    }

    // Helper methods
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        if (conditionCount > 0) {
            summary.append(conditionCount).append(" condition");
            if (conditionCount > 1) summary.append("s");
        }
        if (actionCount > 0) {
            if (summary.length() > 0) summary.append(" • ");
            summary.append(actionCount).append(" action");
            if (actionCount > 1) summary.append("s");
        }
        return summary.toString();
    }

    public boolean hasConditions() {
        return conditionCount > 0;
    }

    public boolean hasActions() {
        return actionCount > 0;
    }

    public boolean canExecute() {
        return enabled && hasActions();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneModel that = (SceneModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Inner classes for scene components
    public static class SceneCondition {
        private String id;
        private String type; // "device", "time", "location", etc.
        private String deviceId; // for device conditions
        private String property; // "power", "temperature", etc.
        private String operator; // "equals", "greater_than", etc.
        private String value;

        public SceneCondition(String id, String type, String deviceId, String property, String operator, String value) {
            this.id = id;
            this.type = type;
            this.deviceId = deviceId;
            this.property = property;
            this.operator = operator;
            this.value = value;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }
        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class SceneAction {
        private String id;
        private String deviceId;
        private String action; // "turn_on", "turn_off", "set_brightness", etc.
        private String property; // "power", "brightness", "color", etc.
        private String value;

        public SceneAction(String id, String deviceId, String action, String property, String value) {
            this.id = id;
            this.deviceId = deviceId;
            this.action = action;
            this.property = property;
            this.value = value;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getProperty() { return property; }
        public void setProperty(String property) { this.property = property; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}