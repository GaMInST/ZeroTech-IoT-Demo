package com.zerotechiot.eg;

import androidx.annotation.Keep;

/**
 * Application layer proxy configuration
 */
@Keep
public class AppConfig {
    /**
     * Switch for dark mode support in theme color
     */
    public static final boolean is_darkMode_support = true;
    /**
     * Switch for Huawei package support
     */
    public static final boolean is_huawei_pkg = false;

    /**
     * List of hotspot prefixes supported for AP mode
     */
    public static final String ap_mode_ssid = "SmartLife";
    /**
     * Whether to support Bluetooth device pairing
     */
    public static final boolean is_need_ble_support = true;

    /**
     * Whether to support Bluetooth Mesh device pairing
     */
    public static final boolean is_need_blemesh_support = true;

    /**
     * Whether to support scan pairing in the top right corner of the list page
     */
    public static final boolean is_scan_support = true;

}


