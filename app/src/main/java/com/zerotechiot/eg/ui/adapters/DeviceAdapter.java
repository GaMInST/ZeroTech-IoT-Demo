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
        private View statusIndicator;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.device_card);
            deviceIcon = itemView.findViewById(R.id.device_icon);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceStatus = itemView.findViewById(R.id.device_status);
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

            if (device.isOnline()) {
                deviceStatus.setText(device.isOn() ? "On" : "Off");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
            }

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
        }

        private void bindDeviceBean(DeviceBean device) {
            deviceName.setText(device.getName());

            if (device.getIsOnline()) {
                deviceStatus.setText("Online");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_online);
            } else {
                deviceStatus.setText("Offline");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline);
            }

            // Set device icon based on product ID or type
            String productId = device.getProductId();
            if (productId != null && productId.contains("light")) {
                deviceIcon.setImageResource(R.drawable.ic_light_bulb);
            } else if (productId != null && productId.contains("switch")) {
                deviceIcon.setImageResource(R.drawable.ic_switch);
            } else if (productId != null && productId.contains("fan")) {
                deviceIcon.setImageResource(R.drawable.ic_fan);
            } else {
                deviceIcon.setImageResource(R.drawable.ic_device_default);
            }
        }
    }
}
