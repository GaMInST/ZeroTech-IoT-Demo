package com.zerotechiot.eg;

/**
 * Production configuration for the ZeroTech Smart Home App
 * This class contains all production-ready settings and configurations
 */
public class ProductionConfig {

    // App Information
    public static final String APP_NAME = "ZeroTech Smart Home";
    public static final String APP_VERSION = "1.1.0";
    public static final int APP_VERSION_CODE = 2;

    // Feature Flags
    public static final boolean ENABLE_DEBUG_MODE = false;
    public static final boolean ENABLE_ANALYTICS = true;
    public static final boolean ENABLE_CRASH_REPORTING = true;
    public static final boolean ENABLE_PUSH_NOTIFICATIONS = true;

    // UI Configuration
    public static final boolean ENABLE_DARK_MODE = true;
    public static final boolean ENABLE_ANIMATIONS = true;
    public static final boolean ENABLE_HAPTIC_FEEDBACK = true;

    // Performance Settings
    public static final int MAX_DEVICES_PER_HOME = 100;
    public static final int MAX_ROOMS_PER_HOME = 20;
    public static final int MAX_SCENES_PER_HOME = 50;
    public static final int MAX_AUTOMATIONS_PER_HOME = 30;

    // Network Configuration
    public static final int REQUEST_TIMEOUT_SECONDS = 30;
    public static final int CONNECTION_TIMEOUT_SECONDS = 15;
    public static final boolean ENABLE_SSL_PINNING = true;

    // Security Settings
    public static final boolean ENABLE_BIOMETRIC_AUTH = true;
    public static final boolean ENABLE_TWO_FACTOR_AUTH = true;
    public static final boolean ENABLE_DEVICE_ENCRYPTION = true;

    // Privacy Settings
    public static final boolean ENABLE_DATA_COLLECTION = true;
    public static final boolean ENABLE_LOCATION_SERVICES = true;
    public static final boolean ENABLE_CAMERA_PERMISSIONS = true;

    // Integration Settings
    public static final boolean ENABLE_ALEXA_INTEGRATION = true;
    public static final boolean ENABLE_GOOGLE_HOME_INTEGRATION = true;
    public static final boolean ENABLE_APPLE_HOMEKIT_INTEGRATION = false; // iOS only

    // Cloud Services
    public static final boolean ENABLE_CLOUD_BACKUP = true;
    public static final boolean ENABLE_CLOUD_SYNC = true;
    public static final boolean ENABLE_REMOTE_ACCESS = true;

    // Device Management
    public static final boolean ENABLE_DEVICE_SHARING = true;
    public static final boolean ENABLE_DEVICE_GROUPS = true;
    public static final boolean ENABLE_DEVICE_SCHEDULES = true;

    // Automation Features
    public static final boolean ENABLE_GEO_FENCING = true;
    public static final boolean ENABLE_TIME_BASED_AUTOMATION = true;
    public static final boolean ENABLE_CONDITION_BASED_AUTOMATION = true;

    // Scene Features
    public static final boolean ENABLE_CUSTOM_SCENES = true;
    public static final boolean ENABLE_SCENE_SHARING = true;
    public static final boolean ENABLE_SCENE_SCHEDULING = true;

    // Voice Control
    public static final boolean ENABLE_VOICE_COMMANDS = true;
    public static final boolean ENABLE_SPEECH_RECOGNITION = true;
    public static final boolean ENABLE_VOICE_FEEDBACK = true;

    // Camera Features
    public static final boolean ENABLE_LIVE_STREAMING = true;
    public static final boolean ENABLE_RECORDING = true;
    public static final boolean ENABLE_MOTION_DETECTION = true;
    public static final boolean ENABLE_FACE_RECOGNITION = false;

    // Energy Management
    public static final boolean ENABLE_ENERGY_MONITORING = true;
    public static final boolean ENABLE_ENERGY_OPTIMIZATION = true;
    public static final boolean ENABLE_ENERGY_REPORTS = true;

    // Safety Features
    public static final boolean ENABLE_SMOKE_DETECTOR = true;
    public static final boolean ENABLE_CARBON_MONOXIDE_DETECTOR = true;
    public static final boolean ENABLE_WATER_LEAK_DETECTOR = true;

    // Support Features
    public static final boolean ENABLE_IN_APP_SUPPORT = true;
    public static final boolean ENABLE_REMOTE_DIAGNOSTICS = true;
    public static final boolean ENABLE_FIRMWARE_UPDATES = true;

    /**
     * Check if a feature is enabled
     */
    public static boolean isFeatureEnabled(String featureName) {
        switch (featureName.toLowerCase()) {
            case "debug_mode":
                return ENABLE_DEBUG_MODE;
            case "analytics":
                return ENABLE_ANALYTICS;
            case "crash_reporting":
                return ENABLE_CRASH_REPORTING;
            case "push_notifications":
                return ENABLE_PUSH_NOTIFICATIONS;
            case "dark_mode":
                return ENABLE_DARK_MODE;
            case "animations":
                return ENABLE_ANIMATIONS;
            case "haptic_feedback":
                return ENABLE_HAPTIC_FEEDBACK;
            case "biometric_auth":
                return ENABLE_BIOMETRIC_AUTH;
            case "two_factor_auth":
                return ENABLE_TWO_FACTOR_AUTH;
            case "device_encryption":
                return ENABLE_DEVICE_ENCRYPTION;
            case "data_collection":
                return ENABLE_DATA_COLLECTION;
            case "location_services":
                return ENABLE_LOCATION_SERVICES;
            case "camera_permissions":
                return ENABLE_CAMERA_PERMISSIONS;
            case "alexa_integration":
                return ENABLE_ALEXA_INTEGRATION;
            case "google_home_integration":
                return ENABLE_GOOGLE_HOME_INTEGRATION;
            case "cloud_backup":
                return ENABLE_CLOUD_BACKUP;
            case "cloud_sync":
                return ENABLE_CLOUD_SYNC;
            case "remote_access":
                return ENABLE_REMOTE_ACCESS;
            case "device_sharing":
                return ENABLE_DEVICE_SHARING;
            case "device_groups":
                return ENABLE_DEVICE_GROUPS;
            case "device_schedules":
                return ENABLE_DEVICE_SCHEDULES;
            case "geo_fencing":
                return ENABLE_GEO_FENCING;
            case "time_based_automation":
                return ENABLE_TIME_BASED_AUTOMATION;
            case "condition_based_automation":
                return ENABLE_CONDITION_BASED_AUTOMATION;
            case "custom_scenes":
                return ENABLE_CUSTOM_SCENES;
            case "scene_sharing":
                return ENABLE_SCENE_SHARING;
            case "scene_scheduling":
                return ENABLE_SCENE_SCHEDULING;
            case "voice_commands":
                return ENABLE_VOICE_COMMANDS;
            case "speech_recognition":
                return ENABLE_SPEECH_RECOGNITION;
            case "voice_feedback":
                return ENABLE_VOICE_FEEDBACK;
            case "live_streaming":
                return ENABLE_LIVE_STREAMING;
            case "recording":
                return ENABLE_RECORDING;
            case "motion_detection":
                return ENABLE_MOTION_DETECTION;
            case "face_recognition":
                return ENABLE_FACE_RECOGNITION;
            case "energy_monitoring":
                return ENABLE_ENERGY_MONITORING;
            case "energy_optimization":
                return ENABLE_ENERGY_OPTIMIZATION;
            case "energy_reports":
                return ENABLE_ENERGY_REPORTS;
            case "smoke_detector":
                return ENABLE_SMOKE_DETECTOR;
            case "carbon_monoxide_detector":
                return ENABLE_CARBON_MONOXIDE_DETECTOR;
            case "water_leak_detector":
                return ENABLE_WATER_LEAK_DETECTOR;
            case "in_app_support":
                return ENABLE_IN_APP_SUPPORT;
            case "remote_diagnostics":
                return ENABLE_REMOTE_DIAGNOSTICS;
            case "firmware_updates":
                return ENABLE_FIRMWARE_UPDATES;
            default:
                return false;
        }
    }

    /**
     * Get app information string
     */
    public static String getAppInfo() {
        return APP_NAME + " v" + APP_VERSION + " (Build " + APP_VERSION_CODE + ")";
    }

    /**
     * Check if app is in production mode
     */
    public static boolean isProductionMode() {
        return !ENABLE_DEBUG_MODE;
    }
}