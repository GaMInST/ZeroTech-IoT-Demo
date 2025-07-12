package com.zerotechiot.eg.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.SceneModel;

import java.util.List;

public class SceneAdapter extends RecyclerView.Adapter<SceneAdapter.SceneViewHolder> {

    private List<SceneModel> scenes;
    private OnSceneClickListener listener;

    public interface OnSceneClickListener {
        void onSceneClick(SceneModel scene);
        void onSceneLongClick(SceneModel scene);
        void onSceneToggle(SceneModel scene, boolean enabled);
    }

    public SceneAdapter(List<SceneModel> scenes, OnSceneClickListener listener) {
        this.scenes = scenes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SceneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scene_card, parent, false);
        return new SceneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SceneViewHolder holder, int position) {
        SceneModel scene = scenes.get(position);
        holder.bind(scene);
    }

    @Override
    public int getItemCount() {
        return scenes != null ? scenes.size() : 0;
    }

    public void setScenes(List<SceneModel> scenes) {
        this.scenes = scenes;
        notifyDataSetChanged();
    }

    class SceneViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView sceneIcon;
        private TextView sceneName;
        private TextView sceneDescription;
        private TextView sceneSummary;
        private SwitchMaterial sceneToggle;
        private View executeButton;

        public SceneViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.scene_card);
            sceneIcon = itemView.findViewById(R.id.scene_icon);
            sceneName = itemView.findViewById(R.id.scene_name);
            sceneDescription = itemView.findViewById(R.id.scene_description);
            sceneSummary = itemView.findViewById(R.id.scene_summary);
            sceneToggle = itemView.findViewById(R.id.scene_toggle);
            executeButton = itemView.findViewById(R.id.execute_button);

            // Set up click listeners
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSceneClick(scenes.get(position));
                }
            });

            cardView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSceneLongClick(scenes.get(position));
                    return true;
                }
                return false;
            });

            // Set up toggle listener
            sceneToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSceneToggle(scenes.get(position), isChecked);
                }
            });

            // Set up execute button listener
            executeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSceneClick(scenes.get(position));
                }
            });
        }

        public void bind(SceneModel scene) {
            sceneName.setText(scene.getName());
            
            if (scene.getDescription() != null && !scene.getDescription().isEmpty()) {
                sceneDescription.setVisibility(View.VISIBLE);
                sceneDescription.setText(scene.getDescription());
            } else {
                sceneDescription.setVisibility(View.GONE);
            }

            sceneSummary.setText(scene.getSummary());

            // Set scene icon based on background/type
            setSceneIcon(scene.getBackground());

            // Update toggle state (without triggering listener)
            sceneToggle.setOnCheckedChangeListener(null);
            sceneToggle.setChecked(scene.isEnabled());
            sceneToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSceneToggle(scenes.get(position), isChecked);
                }
            });

            // Enable/disable execute button based on scene state
            executeButton.setEnabled(scene.canExecute());
        }

        private void setSceneIcon(String background) {
            Context context = itemView.getContext();
            
            switch (background.toLowerCase()) {
                case "morning":
                    sceneIcon.setImageResource(R.drawable.ic_sunrise);
                    break;
                case "night":
                    sceneIcon.setImageResource(R.drawable.ic_moon);
                    break;
                case "movie":
                    sceneIcon.setImageResource(R.drawable.ic_movie);
                    break;
                case "away":
                    sceneIcon.setImageResource(R.drawable.ic_home);
                    break;
                case "security":
                    sceneIcon.setImageResource(R.drawable.ic_security);
                    break;
                case "party":
                    sceneIcon.setImageResource(R.drawable.ic_party);
                    break;
                case "work":
                    sceneIcon.setImageResource(R.drawable.ic_work);
                    break;
                case "relax":
                    sceneIcon.setImageResource(R.drawable.ic_relax);
                    break;
                default:
                    sceneIcon.setImageResource(R.drawable.ic_scene);
                    break;
            }
        }
    }
} 