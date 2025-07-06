package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.ui.adapters.SettingsAdapter;
import com.zerotechiot.eg.ui.models.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView settingsRecyclerView;
    private SettingsAdapter settingsAdapter;
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial notificationsSwitch;
    private SwitchMaterial autoConnectSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupToolbar();
        loadSettings();
    }

    private void initializeViews() {
        settingsRecyclerView = findViewById(R.id.settings_recycler_view);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        autoConnectSwitch = findViewById(R.id.auto_connect_switch);

        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsAdapter = new SettingsAdapter();
        settingsRecyclerView.setAdapter(settingsAdapter);

        setupSwitchListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }
    }

    private void setupSwitchListeners() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle dark mode toggle
            showSnackbar("Dark mode " + (isChecked ? "enabled" : "disabled"));
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle notifications toggle
            showSnackbar("Notifications " + (isChecked ? "enabled" : "disabled"));
        });

        autoConnectSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle auto connect toggle
            showSnackbar("Auto connect " + (isChecked ? "enabled" : "disabled"));
        });
    }

    private void loadSettings() {
        List<SettingItem> settings = new ArrayList<>();

        // Account Settings
        settings.add(new SettingItem("Account", "Manage your account settings", SettingItem.Type.SECTION_HEADER));
        settings.add(new SettingItem("Profile", "Edit your profile information", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Security", "Password and security settings", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Privacy", "Privacy and data settings", SettingItem.Type.NAVIGATION));

        // Device Settings
        settings.add(new SettingItem("Devices", "Manage your smart devices", SettingItem.Type.SECTION_HEADER));
        settings.add(new SettingItem("Device Management", "Add, remove, and configure devices",
                SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Automation", "Create and manage automations", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Scenes", "Manage your smart scenes", SettingItem.Type.NAVIGATION));

        // App Settings
        settings.add(new SettingItem("App", "Application preferences", SettingItem.Type.SECTION_HEADER));
        settings.add(new SettingItem("Language", "Change app language", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Units", "Temperature and measurement units", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Time Zone", "Set your time zone", SettingItem.Type.NAVIGATION));

        // Support
        settings.add(new SettingItem("Support", "Get help and support", SettingItem.Type.SECTION_HEADER));
        settings.add(new SettingItem("Help Center", "Browse help articles", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("Contact Support", "Get in touch with our team", SettingItem.Type.NAVIGATION));
        settings.add(new SettingItem("About", "App version and information", SettingItem.Type.NAVIGATION));

        settingsAdapter.setSettings(settings);
        settingsAdapter.setOnItemClickListener(this::onSettingItemClick);
    }

    private void onSettingItemClick(SettingItem item) {
        switch (item.getTitle()) {
            case "Profile":
                showSnackbar("Profile settings coming soon");
                break;
            case "Security":
                showSnackbar("Security settings coming soon");
                break;
            case "Privacy":
                showSnackbar("Privacy settings coming soon");
                break;
            case "Device Management":
                // Launch device management
                break;
            case "Automation":
                showSnackbar("Automation settings coming soon");
                break;
            case "Scenes":
                showSnackbar("Scene settings coming soon");
                break;
            case "Language":
                showSnackbar("Language settings coming soon");
                break;
            case "Units":
                showSnackbar("Unit settings coming soon");
                break;
            case "Time Zone":
                showSnackbar("Time zone settings coming soon");
                break;
            case "Help Center":
                showSnackbar("Help center coming soon");
                break;
            case "Contact Support":
                showSnackbar("Contact support coming soon");
                break;
            case "About":
                showSnackbar("About page coming soon");
                break;
        }
    }

    private void showSnackbar(String message) {
        com.google.android.material.snackbar.Snackbar.make(
                findViewById(android.R.id.content),
                message,
                com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}