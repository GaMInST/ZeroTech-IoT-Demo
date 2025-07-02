package com.zerotechiot.eg;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.ui.models.DeviceModel;

public class DeviceControlActivity extends AppCompatActivity {

    private DeviceModel device;
    private TextView deviceName;
    private TextView deviceStatusText;
    private TextView brightnessValue;
    private SwitchMaterial powerSwitch;
    private Slider brightnessSlider;
    private MaterialButton actionReading;
    private MaterialButton actionRelax;
    private MaterialButton actionFocus;
    private MaterialButton actionNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        // Get device data from intent
        String deviceId = getIntent().getStringExtra("device_id");
        String deviceNameStr = getIntent().getStringExtra("device_name");
        String deviceType = getIntent().getStringExtra("device_type");

        // Provide default values if extras are null
        if (deviceId == null)
            deviceId = "demo_device";
        if (deviceNameStr == null)
            deviceNameStr = "Demo Device";
        if (deviceType == null)
            deviceType = "light"; // Default to light type

        // Create sample device for demo
        device = new DeviceModel(deviceId, deviceNameStr, deviceType, "Living Room", "1");
        device.setOnline(true);
        device.setOn(true);
        device.setBrightness(80);

        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize views
        deviceName = findViewById(R.id.device_name);
        deviceStatusText = findViewById(R.id.device_status_text);
        brightnessValue = findViewById(R.id.brightness_value);
        powerSwitch = findViewById(R.id.power_switch);
        brightnessSlider = findViewById(R.id.brightness_slider);
        actionReading = findViewById(R.id.action_reading);
        actionRelax = findViewById(R.id.action_relax);
        actionFocus = findViewById(R.id.action_focus);
        actionNight = findViewById(R.id.action_night);
    }

    private void setupClickListeners() {
        // Power switch
        powerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            device.setOn(isChecked);
            updateUI();
            Toast.makeText(this, "Device " + (isChecked ? "turned on" : "turned off"), Toast.LENGTH_SHORT).show();
        });

        // Brightness slider
        brightnessSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                device.setBrightness((int) value);
                updateUI();
                Toast.makeText(this, "Brightness set to " + (int) value + "%", Toast.LENGTH_SHORT).show();
            }
        });

        // Quick action buttons
        actionReading.setOnClickListener(v -> {
            device.setBrightness(100);
            device.setTemperature(4000);
            updateUI();
            Toast.makeText(this, "Reading mode activated", Toast.LENGTH_SHORT).show();
        });

        actionRelax.setOnClickListener(v -> {
            device.setBrightness(60);
            device.setTemperature(2700);
            updateUI();
            Toast.makeText(this, "Relax mode activated", Toast.LENGTH_SHORT).show();
        });

        actionFocus.setOnClickListener(v -> {
            device.setBrightness(90);
            device.setTemperature(5000);
            updateUI();
            Toast.makeText(this, "Focus mode activated", Toast.LENGTH_SHORT).show();
        });

        actionNight.setOnClickListener(v -> {
            device.setBrightness(20);
            device.setTemperature(2200);
            updateUI();
            Toast.makeText(this, "Night mode activated", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI() {
        // Update device name
        deviceName.setText(device.getName());

        // Update status text
        deviceStatusText.setText(device.getStatusText() + " • " + device.getRoomName());

        // Update power switch
        powerSwitch.setChecked(device.isOn());

        // Update brightness slider and value
        brightnessSlider.setValue(device.getBrightness());
        brightnessValue.setText(device.getBrightness() + "%");

        // Enable/disable brightness control based on power state
        brightnessSlider.setEnabled(device.isOn());
        actionReading.setEnabled(device.isOn());
        actionRelax.setEnabled(device.isOn());
        actionFocus.setEnabled(device.isOn());
        actionNight.setEnabled(device.isOn());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
