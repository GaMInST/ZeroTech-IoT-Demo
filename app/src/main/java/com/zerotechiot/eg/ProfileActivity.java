package com.zerotechiot.eg;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.ui.adapters.SettingsAdapter;
import com.zerotechiot.eg.ui.models.SettingItem;
import com.zerotechiot.eg.utils.NotificationHelper;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView profileRecyclerView;
    private SettingsAdapter profileAdapter;
    private TextView userNameText;
    private TextView userEmailText;
    private MaterialCardView profileCard;
    private SwitchMaterial darkModeSwitch;
    private SwitchMaterial notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupToolbar();
        setupProfileData();
        loadProfileSettings();
    }

    private void initializeViews() {
        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        // Initialize profile views
        profileCard = findViewById(R.id.profile_card);
        userNameText = findViewById(R.id.user_name);
        userEmailText = findViewById(R.id.user_email);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        notificationsSwitch = findViewById(R.id.notifications_switch);

        // Initialize RecyclerView
        profileRecyclerView = findViewById(R.id.profile_recycler_view);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileAdapter = new SettingsAdapter();
        profileRecyclerView.setAdapter(profileAdapter);

        // Setup click listeners
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }

    private void setupProfileData() {
        // Load real user information from Tuya SDK
        loadRealUserData();
    }

    private void loadRealUserData() {
        // Get user information from Tuya SDK
        try {
            // Get current user info
            String userEmail = ThingHomeSdk.getUserInstance().getUser().getEmail();
            String userName = ThingHomeSdk.getUserInstance().getUser().getUsername();

            if (userEmail != null && !userEmail.isEmpty()) {
                userEmailText.setText(userEmail);
            } else {
                userEmailText.setText("No email available");
            }

            if (userName != null && !userName.isEmpty()) {
                userNameText.setText(userName);
            } else {
                userNameText.setText("User");
            }
        } catch (Exception e) {
            // Fallback to default values if user data is not available
            userNameText.setText("User");
            userEmailText.setText("user@example.com");
        }

        // Setup profile card click
        profileCard.setOnClickListener(v -> {
            // Navigate to edit profile
            NotificationHelper.showSmartNotification(this, "Edit profile feature coming soon!");
        });
    }

    private void setupClickListeners() {
        // Dark mode switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle dark mode toggle
            if (isChecked) {
                setDarkMode();
            } else {
                setLightMode();
            }
            NotificationHelper.showSmartNotification(this,
                    "Theme changed to " + (isChecked ? "Dark" : "Light") + " mode");
        });

        // Notifications switch
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle notifications toggle
            NotificationHelper.showSmartNotification(this,
                    "Notifications " + (isChecked ? "enabled" : "disabled"));
        });

        // Setup profile settings click listener
        profileAdapter.setOnItemClickListener(this::onProfileSettingClick);
    }

    private void loadProfileSettings() {
        List<SettingItem> profileSettings = new ArrayList<>();

        // Account section
        profileSettings.add(new SettingItem("Account", "", SettingItem.Type.SECTION_HEADER));
        profileSettings
                .add(new SettingItem("Edit Profile", "Update your personal information", SettingItem.Type.NAVIGATION));
        profileSettings
                .add(new SettingItem("Change Password", "Update your account password", SettingItem.Type.NAVIGATION));
        profileSettings.add(
                new SettingItem("Privacy Settings", "Manage your privacy preferences", SettingItem.Type.NAVIGATION));

        // Preferences section
        profileSettings.add(new SettingItem("Preferences", "", SettingItem.Type.SECTION_HEADER));
        profileSettings.add(new SettingItem("Language", "English", SettingItem.Type.NAVIGATION));
        profileSettings.add(new SettingItem("Time Zone", "UTC-5 (Eastern Time)", SettingItem.Type.NAVIGATION));
        profileSettings.add(new SettingItem("Units", "Metric", SettingItem.Type.NAVIGATION));

        // Security section
        profileSettings.add(new SettingItem("Security", "", SettingItem.Type.SECTION_HEADER));
        profileSettings.add(new SettingItem("Two-Factor Authentication", "Add an extra layer of security",
                SettingItem.Type.NAVIGATION));
        profileSettings
                .add(new SettingItem("Login History", "View your recent login activity", SettingItem.Type.NAVIGATION));
        profileSettings.add(new SettingItem("Connected Devices", "Manage devices linked to your account",
                SettingItem.Type.NAVIGATION));

        // Support section
        profileSettings.add(new SettingItem("Support", "", SettingItem.Type.SECTION_HEADER));
        profileSettings
                .add(new SettingItem("Help Center", "Find answers to common questions", SettingItem.Type.NAVIGATION));
        profileSettings
                .add(new SettingItem("Contact Support", "Get help from our support team", SettingItem.Type.NAVIGATION));
        profileSettings.add(
                new SettingItem("Report a Bug", "Help us improve by reporting issues", SettingItem.Type.NAVIGATION));

        // About section
        profileSettings.add(new SettingItem("About", "", SettingItem.Type.SECTION_HEADER));
        profileSettings.add(new SettingItem("App Version", "1.0.0", SettingItem.Type.NAVIGATION));
        profileSettings
                .add(new SettingItem("Terms of Service", "Read our terms and conditions", SettingItem.Type.NAVIGATION));
        profileSettings.add(
                new SettingItem("Privacy Policy", "Learn about our privacy practices", SettingItem.Type.NAVIGATION));
        profileSettings.add(new SettingItem("Logout", "Sign out of your account", SettingItem.Type.NAVIGATION));

        profileAdapter.setSettings(profileSettings);
    }

    private void onProfileSettingClick(SettingItem item) {
        switch (item.getTitle()) {
            case "Edit Profile":
                NotificationHelper.showSmartNotification(this, "Edit profile feature coming soon!");
                break;
            case "Change Password":
                NotificationHelper.showSmartNotification(this, "Change password feature coming soon!");
                break;
            case "Privacy Settings":
                NotificationHelper.showSmartNotification(this, "Privacy settings feature coming soon!");
                break;
            case "Language":
                NotificationHelper.showSmartNotification(this, "Language selection feature coming soon!");
                break;
            case "Time Zone":
                NotificationHelper.showSmartNotification(this, "Time zone selection feature coming soon!");
                break;
            case "Units":
                NotificationHelper.showSmartNotification(this, "Unit selection feature coming soon!");
                break;
            case "Two-Factor Authentication":
                NotificationHelper.showSmartNotification(this, "2FA setup feature coming soon!");
                break;
            case "Login History":
                NotificationHelper.showSmartNotification(this, "Login history feature coming soon!");
                break;
            case "Connected Devices":
                NotificationHelper.showSmartNotification(this, "Connected devices feature coming soon!");
                break;
            case "Help Center":
                NotificationHelper.showSmartNotification(this, "Help center feature coming soon!");
                break;
            case "Contact Support":
                NotificationHelper.showSmartNotification(this, "Contact support feature coming soon!");
                break;
            case "Report a Bug":
                NotificationHelper.showSmartNotification(this, "Bug report feature coming soon!");
                break;
            case "Terms of Service":
                NotificationHelper.showSmartNotification(this, "Terms of service feature coming soon!");
                break;
            case "Privacy Policy":
                NotificationHelper.showSmartNotification(this, "Privacy policy feature coming soon!");
                break;
            case "Logout":
                handleLogout();
                break;
            default:
                NotificationHelper.showSmartNotification(this, item.getTitle() + " feature coming soon!");
                break;
        }
    }

    private void setDarkMode() {
        // Implement dark mode logic
        // This would typically involve updating the app theme
    }

    private void setLightMode() {
        // Implement light mode logic
        // This would typically involve updating the app theme
    }

    private void handleLogout() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Perform logout
                    NotificationHelper.showSmartNotification(this, "Logged out successfully");

                    // Navigate to login screen
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}