package com.zerotechiot.eg.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.DeviceModel;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<DeviceModel> devices = new ArrayList<>();
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(DeviceModel device);

        void onDeviceToggle(DeviceModel device, boolean isOn);
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.listener = listener;
    }

    public void setDevices(List<DeviceModel> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device_card, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceModel device = devices.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private ImageView deviceIcon;
        private TextView deviceName;
        private TextView deviceStatus;
        private SwitchMaterial powerSwitch;
        private View statusIndicator;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceIcon = itemView.findViewById(R.id.device_icon);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceStatus = itemView.findViewById(R.id.device_status);
            powerSwitch = itemView.findViewById(R.id.device_toggle);
            statusIndicator = itemView.findViewById(R.id.status_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceClick(devices.get(position));
                }
            });

            if (powerSwitch != null) {
                powerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onDeviceToggle(devices.get(position), isChecked);
                    }
                });
            }
        }

        public void bind(DeviceModel device) {
            deviceName.setText(device.getName());
            deviceStatus.setText(device.isOnline() ? "Online" : "Offline");

            // Set device icon based on type
            switch (device.getType()) {
                case "light":
                    deviceIcon.setImageResource(R.drawable.ic_light_bulb);
                    break;
                case "switch":
                    deviceIcon.setImageResource(R.drawable.ic_switch);
                    break;
                case "fan":
                    deviceIcon.setImageResource(R.drawable.ic_fan);
                    break;
                default:
                    deviceIcon.setImageResource(R.drawable.ic_device_default);
                    break;
            }

            // Set status indicator
            statusIndicator.setBackgroundResource(
                    device.isOnline() ? R.drawable.status_indicator_online : R.drawable.status_indicator_offline);

            // Set power switch state
            powerSwitch.setChecked(device.isOn());
            powerSwitch.setEnabled(device.isOnline());
        }
    }
}
