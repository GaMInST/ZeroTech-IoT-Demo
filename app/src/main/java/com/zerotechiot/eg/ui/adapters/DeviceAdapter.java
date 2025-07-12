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
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.zerotechiot.eg.R;
import com.zerotechiot.eg.ui.models.DeviceModel;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<?> devices; // Can be List<DeviceModel> or List<DeviceBean>
    private Context context;
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onDeviceClick(Object device);
        void onDeviceToggle(Object device, boolean isOn);
    }

    public DeviceAdapter(List<?> devices, OnDeviceClickListener listener) {
        this.devices = devices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_card, parent, false);
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
        private SwitchMaterial deviceToggle;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.device_card);
            deviceIcon = itemView.findViewById(R.id.device_icon);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceStatus = itemView.findViewById(R.id.device_status);
            roomName = itemView.findViewById(R.id.room_name);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            deviceToggle = itemView.findViewById(R.id.device_toggle);

            // Set up click listeners
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceClick(devices.get(position));
                }
            });

            // Set up toggle listener
            deviceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceToggle(devices.get(position), isChecked);
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

            // Update status and indicator
            if (device.isOnline()) {
                deviceStatus.setText(device.getStatusText());
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
                deviceToggle.setEnabled(true);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
                deviceToggle.setEnabled(false);
            }

            // Update toggle state (without triggering listener)
            deviceToggle.setOnCheckedChangeListener(null);
            deviceToggle.setChecked(device.isOn());
            deviceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceToggle(devices.get(position), isChecked);
                }
            });

            // Set device icon based on type
            switch (device.getType().toLowerCase()) {
                case "light":
                    deviceIcon.setImageResource(R.drawable.ic_light);
                    break;
                case "switch":
                case "plug":
                    deviceIcon.setImageResource(R.drawable.ic_switch);
                    break;
                case "ir":
                case "remote":
                case "controller":
                    deviceIcon.setImageResource(R.drawable.ic_remote);
                    break;
                case "hub":
                case "gateway":
                case "zigbee":
                    deviceIcon.setImageResource(R.drawable.ic_hub);
                    break;
                case "sensor":
                case "gas":
                case "detector":
                    deviceIcon.setImageResource(R.drawable.ic_sensor);
                    break;
                case "contact":
                case "door":
                case "magnet":
                    deviceIcon.setImageResource(R.drawable.ic_contact);
                    break;
                case "fan":
                    deviceIcon.setImageResource(R.drawable.ic_fan);
                    break;
                case "curtain":
                case "blind":
                    deviceIcon.setImageResource(R.drawable.ic_curtain);
                    break;
                case "thermostat":
                case "temp":
                    deviceIcon.setImageResource(R.drawable.ic_thermostat);
                    break;
                case "camera":
                case "ipc":
                    deviceIcon.setImageResource(R.drawable.ic_camera);
                    break;
                case "lock":
                    deviceIcon.setImageResource(R.drawable.ic_lock);
                    break;
                default:
                    deviceIcon.setImageResource(R.drawable.ic_device);
                    break;
            }
        }

        private void bindDeviceBean(DeviceBean device) {
            deviceName.setText(device.getName());
            roomName.setText("Unknown Room"); // Tuya DeviceBean doesn't have room info directly

            if (device.getIsOnline()) {
                deviceStatus.setText("Online");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
                deviceToggle.setEnabled(true);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
                deviceToggle.setEnabled(false);
            }

            // For DeviceBean, we don't have direct access to power state
            // This would need to be implemented with actual device control
            deviceToggle.setOnCheckedChangeListener(null);
            deviceToggle.setChecked(device.getIsOnline()); // Assume online = on for now
            deviceToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeviceToggle(devices.get(position), isChecked);
                }
            });

            // Set device icon based on product ID or type
            String productId = device.getProductId();
            if (productId != null && productId.contains("light")) {
                deviceIcon.setImageResource(R.drawable.ic_light_bulb);
            } else if (productId != null && productId.contains("switch")) {
                deviceIcon.setImageResource(R.drawable.ic_switch);
            } else if (productId != null && productId.contains("fan")) {
                deviceIcon.setImageResource(R.drawable.ic_fan);
            } else if (productId != null && productId.contains("curtain")) {
                deviceIcon.setImageResource(R.drawable.ic_curtain);
            } else if (productId != null && productId.contains("thermostat")) {
                deviceIcon.setImageResource(R.drawable.ic_thermostat);
            } else if (productId != null && productId.contains("camera")) {
                deviceIcon.setImageResource(R.drawable.ic_camera);
            } else if (productId != null && productId.contains("lock")) {
                deviceIcon.setImageResource(R.drawable.ic_lock);
            } else if (productId != null && productId.contains("sensor")) {
                deviceIcon.setImageResource(R.drawable.ic_sensor);
            } else {
                deviceIcon.setImageResource(R.drawable.ic_device_default);
            }
        }
    }
}
