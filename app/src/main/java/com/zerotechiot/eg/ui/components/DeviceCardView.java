package com.zerotechiot.eg.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.zerotechiot.eg.R;

/**
 * Custom device card view for displaying smart home devices
 * Features status indicators, device icons, and modern glassmorphic design
 */
public class DeviceCardView extends ConstraintLayout {

    public enum DeviceStatus {
        ONLINE, OFFLINE, ERROR, WARNING
    }

    private GlassmorphicCardView cardView;
    private ImageView deviceIcon;
    private TextView deviceName;
    private TextView deviceStatus;
    private View statusIndicator;
    private ImageView quickActionIcon;

    private DeviceStatus currentStatus = DeviceStatus.OFFLINE;
    private String deviceType = "";
    private boolean showStatusIndicator = true;

    public DeviceCardView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public DeviceCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.view_device_card, this, true);

        // Initialize views
        cardView = findViewById(R.id.device_card);
        deviceIcon = findViewById(R.id.device_icon);
        deviceName = findViewById(R.id.device_name);
        deviceStatus = findViewById(R.id.device_status);
        statusIndicator = findViewById(R.id.status_indicator);
        quickActionIcon = findViewById(R.id.quick_action_icon);

        // Read custom attributes
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DeviceCardView);
            int statusValue = ta.getInteger(R.styleable.DeviceCardView_deviceStatus, 1);
            currentStatus = DeviceStatus.values()[statusValue];
            deviceType = ta.getString(R.styleable.DeviceCardView_deviceType);
            showStatusIndicator = ta.getBoolean(R.styleable.DeviceCardView_showStatusIndicator, true);
            ta.recycle();
        }

        // Apply initial state
        updateStatusIndicator();
        updateStatusText();

        // Set click listener for quick actions
        quickActionIcon.setOnClickListener(v -> {
            if (onQuickActionClickListener != null) {
                onQuickActionClickListener.onQuickActionClick(this);
            }
        });

        // Set long click listener for device details
        setOnLongClickListener(v -> {
            if (onDeviceLongClickListener != null) {
                onDeviceLongClickListener.onDeviceLongClick(this);
                return true;
            }
            return false;
        });
    }

    /**
     * Set device information
     */
    public void setDeviceInfo(String name, String type, DeviceStatus status) {
        deviceName.setText(name);
        deviceType = type;
        setDeviceStatus(status);
        updateDeviceIcon();
    }

    /**
     * Set device status and update UI
     */
    public void setDeviceStatus(DeviceStatus status) {
        this.currentStatus = status;
        updateStatusIndicator();
        updateStatusText();
        updateCardGlow();
    }

    /**
     * Update status indicator appearance
     */
    private void updateStatusIndicator() {
        if (!showStatusIndicator) {
            statusIndicator.setVisibility(GONE);
            return;
        }

        statusIndicator.setVisibility(VISIBLE);
        switch (currentStatus) {
            case ONLINE:
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
                break;
            case OFFLINE:
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
                break;
            case ERROR:
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
                break;
            case WARNING:
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
                break;
        }
    }

    /**
     * Update status text
     */
    private void updateStatusText() {
        switch (currentStatus) {
            case ONLINE:
                deviceStatus.setText(R.string.status_online);
                deviceStatus.setTextColor(getResources().getColor(R.color.device_online));
                break;
            case OFFLINE:
                deviceStatus.setText(R.string.status_offline);
                deviceStatus.setTextColor(getResources().getColor(R.color.device_offline));
                break;
            case ERROR:
                deviceStatus.setText(R.string.status_error);
                deviceStatus.setTextColor(getResources().getColor(R.color.device_error));
                break;
            case WARNING:
                deviceStatus.setText(R.string.status_warning);
                deviceStatus.setTextColor(getResources().getColor(R.color.device_warning));
                break;
        }
    }

    /**
     * Update card glow based on status
     */
    private void updateCardGlow() {
        switch (currentStatus) {
            case ONLINE:
                cardView.setGlowColor(getResources().getColor(R.color.device_online));
                cardView.setGlowIntensity(1.0f);
                break;
            case OFFLINE:
                cardView.setGlowColor(getResources().getColor(R.color.device_offline));
                cardView.setGlowIntensity(0.3f);
                break;
            case ERROR:
                cardView.setGlowColor(getResources().getColor(R.color.device_error));
                cardView.setGlowIntensity(0.8f);
                break;
            case WARNING:
                cardView.setGlowColor(getResources().getColor(R.color.device_warning));
                cardView.setGlowIntensity(0.6f);
                break;
        }
    }

    /**
     * Update device icon based on type
     */
    private void updateDeviceIcon() {
        // This would be implemented based on device type mapping
        // For now, using a default icon
        deviceIcon.setImageResource(R.drawable.ic_device_default);
    }

    /**
     * Set quick action icon and visibility
     */
    public void setQuickActionIcon(int iconRes, boolean visible) {
        if (visible) {
            quickActionIcon.setVisibility(VISIBLE);
            quickActionIcon.setImageResource(iconRes);
        } else {
            quickActionIcon.setVisibility(GONE);
        }
    }

    // Click listeners
    private OnQuickActionClickListener onQuickActionClickListener;
    private OnDeviceLongClickListener onDeviceLongClickListener;

    public interface OnQuickActionClickListener {
        void onQuickActionClick(DeviceCardView deviceCard);
    }

    public interface OnDeviceLongClickListener {
        void onDeviceLongClick(DeviceCardView deviceCard);
    }

    public void setOnQuickActionClickListener(OnQuickActionClickListener listener) {
        this.onQuickActionClickListener = listener;
    }

    public void setOnDeviceLongClickListener(OnDeviceLongClickListener listener) {
        this.onDeviceLongClickListener = listener;
    }

    // Getters
    public DeviceStatus getDeviceStatus() {
        return currentStatus;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceName() {
        return deviceName.getText().toString();
    }
}
