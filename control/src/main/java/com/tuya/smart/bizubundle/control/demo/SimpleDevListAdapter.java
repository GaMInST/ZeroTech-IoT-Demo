package com.tuya.smart.bizubundle.control.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.control.PluginControlService;
import com.thingclips.smart.control.plug.api.IPluginControlService;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Device list adapter on a simple style.
 */
public class SimpleDevListAdapter extends RecyclerView.Adapter<SimpleDevListAdapter.SimpleDevViewHolder> {
    private List<DeviceBean> mData = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;

    private Context context;
    private IPluginControlService pluginControlService;

    public SimpleDevListAdapter(Context context) {
        this.context = context;
        BizBundleInitializer.registerService(IPluginControlService.class, new PluginControlService());
        pluginControlService = MicroServiceManager.getInstance()
                .findServiceByInterface(IPluginControlService.class.getName());
    }

    @NonNull
    @Override
    public SimpleDevViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_simple_dev, parent, false);
        return new SimpleDevViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleDevViewHolder viewHolder, int position) {
        final DeviceBean bean = mData.get(position);
        if (bean == null) {
            return;
        }

        // Set device name
        viewHolder.tvName.setText(bean.getName());

        // Set device status
        String devId = bean.getDevId();
        String statusText = "";

        if (bean.getIsOnline()) {
            statusText = "在线";
            viewHolder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            statusText = "离线";
            viewHolder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Add device type information
        String deviceType = getDeviceTypeName(bean.getProductId());
        if (deviceType != null && !deviceType.isEmpty()) {
            statusText += " | " + deviceType;
        }

        viewHolder.tvStatus.setText(statusText);

        // Set multi-control support status
        Boolean bool = pluginControlService.isDeviceSupportMultiControl(devId);
        if (bool) {
            viewHolder.tvMultiControl.setText("支持多控");
            viewHolder.tvMultiControl.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            viewHolder.tvMultiControl.setText("不支持多控");
            viewHolder.tvMultiControl.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    public void setData(List<DeviceBean> beans) {
        mData.clear();
        mData.addAll(beans);
        notifyDataSetChanged();
    }

    private String getDeviceTypeName(String productId) {
        if (productId == null)
            return "";

        // Common device type mapping
        if (productId.contains("light") || productId.contains("bulb")) {
            return "智能灯泡";
        } else if (productId.contains("switch") || productId.contains("outlet")) {
            return "智能开关";
        } else if (productId.contains("curtain") || productId.contains("blind")) {
            return "智能窗帘";
        } else if (productId.contains("thermostat") || productId.contains("temp")) {
            return "温控器";
        } else if (productId.contains("camera") || productId.contains("ipc")) {
            return "智能摄像头";
        } else if (productId.contains("lock") || productId.contains("door")) {
            return "智能门锁";
        } else if (productId.contains("sensor") || productId.contains("detector")) {
            return "传感器";
        } else {
            return "智能设备";
        }
    }

    static class SimpleDevViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStatus;
        TextView tvMultiControl;

        public SimpleDevViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMultiControl = itemView.findViewById(R.id.tvMultiControl);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DeviceBean item, int position);
    }
}
