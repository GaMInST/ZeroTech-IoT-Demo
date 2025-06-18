package com.tuya.smart.bizubundle.control.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.control.PluginControlService;
import com.thingclips.smart.control.plug.api.IPluginControlService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;

public class ControlManagerActivity extends AppCompatActivity {

    private IPluginControlService pluginControlService;
    private TextView tvDeviceCount;
    private TextView tvOnlineCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_manager);

        // Initialize the control service
        BizBundleInitializer.registerService(IPluginControlService.class, new PluginControlService());
        pluginControlService = MicroServiceManager.getInstance()
                .findServiceByInterface(IPluginControlService.class.getName());

        initViews();
        loadDeviceStats();
    }

    private void initViews() {
        tvDeviceCount = findViewById(R.id.tvDeviceCount);
        tvOnlineCount = findViewById(R.id.tvOnlineCount);

        // Multi-control button
        findViewById(R.id.btnGoMultiControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlDevListActivity.goControl(ControlManagerActivity.this);
            }
        });

        // Individual device control button
        findViewById(R.id.btnIndividualControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IndividualDeviceControlActivity.goControl(ControlManagerActivity.this);
            }
        });

        // Device management button
        findViewById(R.id.btnDeviceManagement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceManagementActivity.goControl(ControlManagerActivity.this);
            }
        });

        // Refresh button
        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDeviceStats();
                Toast.makeText(ControlManagerActivity.this, "设备状态已刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDeviceStats() {
        AbsBizBundleFamilyService familyService = getService();
        if (familyService == null || familyService.getCurrentHomeId() == 0) {
            tvDeviceCount.setText("设备数量: 0");
            tvOnlineCount.setText("在线设备: 0");
            return;
        }

        ThingHomeSdk.newHomeInstance(familyService.getCurrentHomeId()).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(final HomeBean homeBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int totalDevices = homeBean.getDeviceList().size();
                        int onlineDevices = 0;

                        for (com.thingclips.smart.sdk.bean.DeviceBean device : homeBean.getDeviceList()) {
                            if (device.getIsOnline()) {
                                onlineDevices++;
                            }
                        }

                        tvDeviceCount.setText("设备数量: " + totalDevices);
                        tvOnlineCount.setText("在线设备: " + onlineDevices);
                    }
                });
            }

            @Override
            public void onError(String code, String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDeviceCount.setText("设备数量: 获取失败");
                        tvOnlineCount.setText("在线设备: 获取失败");
                        Toast.makeText(ControlManagerActivity.this, "获取设备信息失败: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private AbsBizBundleFamilyService getService() {
        return MicroServiceManager.getInstance().findServiceByInterface(AbsBizBundleFamilyService.class.getName());
    }
}