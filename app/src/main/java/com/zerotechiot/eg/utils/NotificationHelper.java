package com.zerotechiot.eg.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NotificationHelper {

    private static final int NOTIFICATION_DURATION = 2000; // 2 seconds

    public static void showSmartNotification(Context context, String message) {
        showSmartNotification(context, message, false);
    }

    public static void showSmartNotification(Context context, String message, boolean isError) {
        // Use Snackbar for better UX
        View rootView = getRootView(context);
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);

            if (isError) {
                snackbar.setBackgroundTint(context.getResources().getColor(android.R.color.holo_red_dark));
            }

            snackbar.show();
        } else {
            // Fallback to Toast if root view not found
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showActionCompleted(Context context, String action) {
        showSmartNotification(context, action + " completed", false);
    }

    public static void showActionFailed(Context context, String action) {
        showSmartNotification(context, action + " failed", true);
    }

    public static void showSliderValueChanged(Context context, String control, int value) {
        // Only show notification when slider interaction ends, not during dragging
        showSmartNotification(context, control + " set to " + value, false);
    }

    public static void showDeviceStateChanged(Context context, String deviceName, boolean isOn) {
        showSmartNotification(context, deviceName + " turned " + (isOn ? "on" : "off"), false);
    }

    public static void showModeActivated(Context context, String modeName) {
        showSmartNotification(context, modeName + " mode activated", false);
    }

    private static View getRootView(Context context) {
        if (context instanceof android.app.Activity) {
            return ((android.app.Activity) context).findViewById(android.R.id.content);
        }
        return null;
    }
}