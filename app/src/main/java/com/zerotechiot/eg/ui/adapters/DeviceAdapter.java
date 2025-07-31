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

import com.bumptech.glide.Glide; // Added Glide import
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.DeviceModel;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<?> devices; // Can be List<DeviceModel> or List<DeviceBean>
    private Context context; // Keep context if needed for other things, though Glide uses holder's context
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(Object device);
        void onDeviceToggle(Object device, boolean isOn);
    }

    public DeviceAdapter(List<?> devices, OnDeviceClickListener listener) {
        this.devices = devices;
        this.listener = listener;
        // this.context = context; // Store context if passed and needed for other operations
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext(); // Initialize context from parent
        View view = LayoutInflater.from(context).inflate(R.layout.item_device_card, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Object device = devices.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    public void setDevices(List<?> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView deviceIcon;
        private TextView deviceName;
        private TextView deviceStatus;
        private TextView roomName;
        private View statusIndicator;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.device_card);
            deviceIcon = itemView.findViewById(R.id.device_icon);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceStatus = itemView.findViewById(R.id.device_status);
            roomName = itemView.findViewById(R.id.room_name);
            statusIndicator = itemView.findViewById(R.id.status_indicator);

            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceClick(devices.get(position));
                }
            });
        }

        public void bind(Object device) {
            if (device instanceof DeviceModel) {
                bindDeviceModel((DeviceModel) device);
            } else if (device instanceof DeviceBean) {
                bindDeviceBean((DeviceBean) device);
            }
        }

        private void bindDeviceModel(DeviceModel device) {
            deviceName.setText(device.getName());
            roomName.setText(device.getRoomName());

            if (device.isOnline()) {
                deviceStatus.setText(device.getStatusText());
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
            }

            // Use Glide to load the icon from URL
            if (device.getIconUrl() != null && !device.getIconUrl().isEmpty()) {
                Glide.with(itemView.getContext()) // itemView.getContext() is safer
                        .load(device.getIconUrl())
                        .placeholder(R.drawable.ic_device_default) // Replace with your placeholder
                        .error(R.drawable.ic_device_default)       // Replace with your error drawable
                        .into(deviceIcon);
            } else {
                // Fallback to a default local icon if URL is null or empty, or use type-based like before
                deviceIcon.setImageResource(R.drawable.ic_device_default); // Default icon
            }
        }

        private void bindDeviceBean(DeviceBean device) {
            deviceName.setText(device.getName());
            roomName.setText("Unknown Room"); // Tuya DeviceBean doesn't always have readily available room info

            if (device.getIsOnline()) {
                deviceStatus.setText("Online");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
            }

            // Use Glide to load the icon from URL
            if (device.getIconUrl() != null && !device.getIconUrl().isEmpty()) {
                Glide.with(itemView.getContext()) // itemView.getContext() is safer
                        .load(device.getIconUrl())
                        .placeholder(R.drawable.ic_device_default) // Replace with your placeholder
                        .error(R.drawable.ic_device_default)       // Replace with your error drawable
                        .into(deviceIcon);
            } else {
                // Fallback to a default local icon if URL is null or empty
                deviceIcon.setImageResource(R.drawable.ic_device_default); // Default icon
            }
        }
    }
}
