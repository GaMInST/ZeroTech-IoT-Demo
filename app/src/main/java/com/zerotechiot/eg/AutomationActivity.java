package com.zerotechiot.eg;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zerotechiot.eg.ui.adapters.AutomationAdapter;
import com.zerotechiot.eg.ui.models.AutomationModel;
import com.zerotechiot.eg.utils.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class AutomationActivity extends AppCompatActivity implements AutomationAdapter.OnAutomationClickListener {

    private RecyclerView automationRecyclerView;
    private AutomationAdapter automationAdapter;
    private List<AutomationModel> automations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automation);

        initializeViews();
        setupToolbar();
        loadAutomationData();
        setupClickListeners();
    }

    private void initializeViews() {
        // Initialize RecyclerView
        automationRecyclerView = findViewById(R.id.automation_recycler_view);
        automationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        automationAdapter = new AutomationAdapter(this);
        automationRecyclerView.setAdapter(automationAdapter);

        // Setup floating action button
        FloatingActionButton fabAddAutomation = findViewById(R.id.fab_add_automation);
        fabAddAutomation.setOnClickListener(v -> {
            NotificationHelper.showSmartNotification(this, "Create automation feature coming soon!");
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Automation");
        }
    }

    private void loadAutomationData() {
        // Load sample automations
        automations.clear();
        automations.add(new AutomationModel("1", "Sunset Routine", "Turn on lights at sunset", "sunset", true));
        automations.add(
                new AutomationModel("2", "Motion Detection", "Turn on lights when motion detected", "motion", false));
        automations.add(new AutomationModel("3", "Temperature Control", "Adjust AC based on temperature", "temperature",
                false));
        automations.add(new AutomationModel("4", "Away Mode", "Security mode when away", "away", false));
        automations.add(new AutomationModel("5", "Energy Saving", "Optimize energy usage", "energy", false));

        automationAdapter.setAutomations(automations);
    }

    private void setupClickListeners() {
        automationAdapter.setOnAutomationClickListener(this);
    }

    @Override
    public void onAutomationClick(AutomationModel automation) {
        // Toggle automation
        automation.setActive(!automation.isActive());
        automationAdapter.notifyDataSetChanged();
        NotificationHelper.showSmartNotification(this,
                "Automation '" + automation.getName() + "' " +
                        (automation.isActive() ? "activated" : "deactivated") + "!");
    }

    @Override
    public void onAutomationLongClick(AutomationModel automation) {
        // Show automation options
        NotificationHelper.showSmartNotification(this,
                "Automation options for '" + automation.getName() + "' coming soon!");
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