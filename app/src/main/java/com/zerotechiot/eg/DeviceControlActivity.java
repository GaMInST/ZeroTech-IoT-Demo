package com.zerotechiot.eg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.ui.models.DeviceModel;
import com.zerotechiot.eg.utils.NotificationHelper;

public class DeviceControlActivity extends AppCompatActivity {

    private DeviceModel device;
    private TextView deviceName;
    private TextView deviceStatusText;
    private TextView brightnessValue;
    private TextView colorTempValue;
    private SwitchMaterial powerSwitch;
    private Slider brightnessSlider;
    private Slider colorTempSlider;
    private MaterialButton actionReading;
    private MaterialButton actionRelax;
    private MaterialButton actionFocus;
    private MaterialButton actionNight;
    private View deviceStatusCard;
    private View primaryControlCard;
    private View brightnessControlCard;
    private View colorTempControlCard;
    private View quickActionsCard;

    // Animation flags
    private boolean isSliderDragging = false;
    private boolean isPowerChanging = false;

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
        device.setTemperature(2700);

        initializeViews();
        setupClickListeners();
        updateUI();
        animateCardsIn();
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
        colorTempValue = findViewById(R.id.color_temp_value);
        powerSwitch = findViewById(R.id.power_switch);
        brightnessSlider = findViewById(R.id.brightness_slider);
        colorTempSlider = findViewById(R.id.color_temp_slider);
        actionReading = findViewById(R.id.action_reading);
        actionRelax = findViewById(R.id.action_relax);
        actionFocus = findViewById(R.id.action_focus);
        actionNight = findViewById(R.id.action_night);

        // Get card views for animations
        deviceStatusCard = findViewById(R.id.device_status_card);
        primaryControlCard = findViewById(R.id.primary_control_card);
        brightnessControlCard = findViewById(R.id.brightness_control_card);
        colorTempControlCard = findViewById(R.id.color_temp_control_card);
        quickActionsCard = findViewById(R.id.quick_actions_card);
    }

    private void setupClickListeners() {
        // Power switch with animation
        powerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isPowerChanging) {
                isPowerChanging = true;
                animatePowerSwitch(isChecked);
                device.setOn(isChecked);
                updateUI();

                // Show notification only when action is completed
                NotificationHelper.showDeviceStateChanged(this, device.getName(), isChecked);

                // Reset flag after animation
                powerSwitch.postDelayed(() -> isPowerChanging = false, 300);
            }
        });

        // Brightness slider with smart notifications
        brightnessSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                device.setBrightness((int) value);
                updateUI();

                // Only show notification when user stops dragging
                if (!isSliderDragging) {
                    NotificationHelper.showSliderValueChanged(this, "Brightness", (int) value);
                }
            }
        });

        brightnessSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
                isSliderDragging = true;
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                isSliderDragging = false;
                // Show notification when user stops dragging
                NotificationHelper.showSliderValueChanged(DeviceControlActivity.this, "Brightness",
                        (int) slider.getValue());
            }
        });

        // Color temperature slider with smart notifications
        colorTempSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                device.setTemperature((int) value);
                updateUI();

                // Only show notification when user stops dragging
                if (!isSliderDragging) {
                    NotificationHelper.showSliderValueChanged(this, "Color temperature", (int) value);
                }
            }
        });

        colorTempSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
                isSliderDragging = true;
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                isSliderDragging = false;
                // Show notification when user stops dragging
                NotificationHelper.showSliderValueChanged(DeviceControlActivity.this, "Color temperature",
                        (int) slider.getValue());
            }
        });

        // Quick action buttons with animations
        actionReading.setOnClickListener(v -> {
            animateButtonClick(v);
            device.setBrightness(100);
            device.setTemperature(4000);
            updateUI();
            NotificationHelper.showModeActivated(this, "Reading");
        });

        actionRelax.setOnClickListener(v -> {
            animateButtonClick(v);
            device.setBrightness(60);
            device.setTemperature(2700);
            updateUI();
            NotificationHelper.showModeActivated(this, "Relax");
        });

        actionFocus.setOnClickListener(v -> {
            animateButtonClick(v);
            device.setBrightness(90);
            device.setTemperature(5000);
            updateUI();
            NotificationHelper.showModeActivated(this, "Focus");
        });

        actionNight.setOnClickListener(v -> {
            animateButtonClick(v);
            device.setBrightness(20);
            device.setTemperature(2200);
            updateUI();
            NotificationHelper.showModeActivated(this, "Night");
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

        // Update color temperature slider and value
        colorTempSlider.setValue(device.getTemperature());
        colorTempValue.setText(device.getTemperature() + "K");

        // Enable/disable controls based on power state
        boolean isOn = device.isOn();
        brightnessSlider.setEnabled(isOn);
        colorTempSlider.setEnabled(isOn);
        actionReading.setEnabled(isOn);
        actionRelax.setEnabled(isOn);
        actionFocus.setEnabled(isOn);
        actionNight.setEnabled(isOn);

        // Animate state changes
        animateControlStates(isOn);
    }

    private void animateCardsIn() {
        // Animate cards sliding up with staggered timing
        View[] cards = { deviceStatusCard, primaryControlCard, brightnessControlCard, colorTempControlCard,
                quickActionsCard };

        for (int i = 0; i < cards.length; i++) {
            View card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(100f);

            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(i * 100)
                    .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.decelerate_quint))
                    .start();
        }
    }

    private void animatePowerSwitch(boolean isOn) {
        // Animate the power switch with scale and color changes
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(powerSwitch, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(powerSwitch, "scaleY", 1.0f, 1.2f, 1.0f);

        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    private void animateButtonClick(View button) {
        // Animate button press with scale
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 0.95f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 0.95f, 1.0f);

        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private void animateControlStates(boolean isOn) {
        // Animate control states with alpha and scale
        View[] controls = { brightnessSlider, colorTempSlider, actionReading, actionRelax, actionFocus, actionNight };

        for (View control : controls) {
            float targetAlpha = isOn ? 1.0f : 0.5f;
            float targetScale = isOn ? 1.0f : 0.95f;

            control.animate()
                    .alpha(targetAlpha)
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .setDuration(200)
                    .start();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
