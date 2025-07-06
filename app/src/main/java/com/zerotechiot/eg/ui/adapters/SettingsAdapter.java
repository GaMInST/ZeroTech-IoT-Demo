package com.zerotechiot.eg.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SettingItem> settings = new ArrayList<>();
    private OnItemClickListener listener;

    private static final int VIEW_TYPE_SECTION_HEADER = 0;
    private static final int VIEW_TYPE_NAVIGATION = 1;

    public interface OnItemClickListener {
        void onItemClick(SettingItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSettings(List<SettingItem> settings) {
        this.settings = settings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER:
                View headerView = inflater.inflate(R.layout.item_setting_section_header, parent, false);
                return new SectionHeaderViewHolder(headerView);
            case VIEW_TYPE_NAVIGATION:
            default:
                View navigationView = inflater.inflate(R.layout.item_setting_navigation, parent, false);
                return new NavigationViewHolder(navigationView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SettingItem item = settings.get(position);

        if (holder instanceof SectionHeaderViewHolder) {
            ((SectionHeaderViewHolder) holder).bind(item);
        } else if (holder instanceof NavigationViewHolder) {
            ((NavigationViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    @Override
    public int getItemViewType(int position) {
        SettingItem item = settings.get(position);
        switch (item.getType()) {
            case SECTION_HEADER:
                return VIEW_TYPE_SECTION_HEADER;
            case NAVIGATION:
            default:
                return VIEW_TYPE_NAVIGATION;
        }
    }

    class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;

        public SectionHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.section_title);
        }

        public void bind(SettingItem item) {
            titleText.setText(item.getTitle());
        }
    }

    class NavigationViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView subtitleText;
        private ImageView arrowIcon;

        public NavigationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.setting_title);
            subtitleText = itemView.findViewById(R.id.setting_subtitle);
            arrowIcon = itemView.findViewById(R.id.setting_arrow);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(settings.get(position));
                }
            });
        }

        public void bind(SettingItem item) {
            titleText.setText(item.getTitle());
            subtitleText.setText(item.getSubtitle());
            itemView.setEnabled(item.isEnabled());

            if (item.getIconRes() != 0) {
                arrowIcon.setImageResource(item.getIconRes());
            }
        }
    }
}