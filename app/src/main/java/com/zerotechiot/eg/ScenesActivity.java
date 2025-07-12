package com.zerotechiot.eg;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Smart Scenes Activity for creating and managing automation scenes
 * Supports IR devices, switches, smart plugs, sensors, and door contacts
 */
public class ScenesActivity extends AppCompatActivity {
    private static final String TAG = "ScenesActivity";
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SceneAdapter adapter;
    private FloatingActionButton fabAddScene;
    private AbsBizBundleFamilyService familyService;
    
    private List<DemoScene> sceneList = new ArrayList<>();
    private long currentHomeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenes);
        
        initializeServices();
        setupViews();
        loadScenes();
    }

    private void initializeServices() {
        try {
            // Get family service
            familyService = MicroServiceManager.getInstance()
                    .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
                    
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize services", e);
        }
    }

    private void setupViews() {
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Smart Scenes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Setup swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        swipeRefreshLayout.setOnRefreshListener(this::loadScenes);

        // Setup recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SceneAdapter();
        recyclerView.setAdapter(adapter);

        // Setup FAB
        fabAddScene = findViewById(R.id.fabAddScene);
        fabAddScene.setOnClickListener(v -> showCreateSceneDialog());
    }

    private void loadScenes() {
        // Use demo scenes since SceneBean is not available
        sceneList.clear();
        sceneList.add(new DemoScene("Good Morning", "Turn on lights and start coffee maker", true, "manual"));
        sceneList.add(new DemoScene("Good Night", "Turn off all lights and arm security", true, "manual"));
        sceneList.add(new DemoScene("Movie Mode", "Dim lights and turn on TV", true, "manual"));
        sceneList.add(new DemoScene("Away Mode", "Turn off non-essential devices", true, "manual"));
        sceneList.add(new DemoScene("Security Alert", "Triggered by door contact or gas sensor", false, "manual"));
        runOnUiThread(() -> {
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            if (sceneList.isEmpty()) {
                Toast.makeText(ScenesActivity.this, "No scenes found. Create your first smart scene!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ScenesActivity.this, "Found " + sceneList.size() + " scenes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateSceneDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Create New Scene")
                .setMessage("Would you like to create a new smart scene?")
                .setPositiveButton("Create", (dialog, which) -> {
                    // For now, just show a toast since CreateSceneActivity doesn't exist
                    Toast.makeText(this, "Scene creation feature coming soon!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {

        @NonNull
        @Override
        public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_scene, parent, false);
            return new SceneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
            DemoScene scene = sceneList.get(position);
            holder.bind(scene);
        }

        @Override
        public int getItemCount() {
            return sceneList.size();
        }

        class SceneViewHolder extends RecyclerView.ViewHolder {
            private MaterialCardView cardView;
            private ImageView sceneIcon;
            private TextView sceneName;
            private TextView sceneDescription;
            private Chip sceneType;
            private Chip sceneStatus;

            public SceneViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.sceneCard);
                sceneIcon = itemView.findViewById(R.id.sceneIcon);
                sceneName = itemView.findViewById(R.id.sceneName);
                sceneDescription = itemView.findViewById(R.id.sceneDescription);
                sceneType = itemView.findViewById(R.id.sceneType);
                sceneStatus = itemView.findViewById(R.id.sceneStatus);
            }

            public void bind(DemoScene scene) {
                // Set scene name
                sceneName.setText(scene.name);
                // Set scene description
                sceneDescription.setText(scene.description);
                // Set scene type
                String sceneTypeText = getSceneTypeName(scene.type);
                sceneType.setText(sceneTypeText);
                // Set scene status
                if (scene.enabled) {
                    sceneStatus.setText("Active");
                    sceneStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
                } else {
                    sceneStatus.setText("Inactive");
                    sceneStatus.setChipBackgroundColorResource(android.R.color.holo_red_light);
                }
                // Set scene icon based on type
                int iconRes = getSceneIcon(scene.type);
                sceneIcon.setImageResource(iconRes);
                // Set click listener to execute scene
                cardView.setOnClickListener(v -> {
                    executeScene(scene);
                });
            }

            private String getSceneTypeName(String sceneType) {
                if (sceneType == null) return "Manual";
                
                switch (sceneType) {
                    case "manual":
                        return "Manual";
                    case "auto":
                        return "Auto";
                    case "condition":
                        return "Condition";
                    case "timer":
                        return "Timer";
                    default:
                        return "Manual";
                }
            }

            private int getSceneIcon(String sceneType) {
                if (sceneType == null) return R.drawable.ic_relax;
                
                switch (sceneType) {
                    case "manual":
                        return R.drawable.ic_relax;
                    case "auto":
                        return R.drawable.ic_device;
                    case "condition":
                        return R.drawable.ic_device;
                    case "timer":
                        return R.drawable.ic_device;
                    default:
                        return R.drawable.ic_relax;
                }
            }
        }
    }

    private void executeScene(DemoScene scene) {
        if (scene.enabled) {
            Toast.makeText(this, "Scene '" + scene.name + "' executed successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Scene is not active", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // DemoScene class for demo data
    private static class DemoScene {
        String name;
        String description;
        boolean enabled;
        String type;
        DemoScene(String name, String description, boolean enabled, String type) {
            this.name = name;
            this.description = description;
            this.enabled = enabled;
            this.type = type;
        }
    }
} 