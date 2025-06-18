package com.tuya.smart.bizubundle.control.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.service.MicroServiceManager;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.control.PluginControlService;
import com.thingclips.smart.control.plug.api.IPluginControlService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.panelcaller.api.AbsPanelCallerService;
import com.thingclips.smart.sdk.bean.DeviceBean;

public class DeviceManagementActivity extends AppCompatActivity {

    private SimpleDevListAdapter adapter;
    private IPluginControlService pluginControlService;
    private AbsPanelCallerService panelCallerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);

        // Initialize the control service
        BizBundleInitializer.registerService(IPluginControlService.class, new PluginControlService());
        pluginControlService = MicroServiceManager.getInstance()
                .findServiceByInterface(IPluginControlService.class.getName());

        // Initialize the panel caller service
        panelCallerService = MicroContext.getServiceManager()
                .findServiceByInterface(AbsPanelCallerService.class.getName());

        initView();
        loadData();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("设备管理");

        RecyclerView rvList = findViewById(R.id.rvList);
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleDevListAdapter(this);
        rvList.setAdapter(adapter);

        adapter.setOnItemClickListener(new SimpleDevListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeviceBean item, int position) {
                String devId = item.getDevId();
                // Launch device management panel
                if (panelCallerService != null) {
                    panelCallerService.goPanelWithCheckAndTip(DeviceManagementActivity.this, devId);
                } else {
                    Toast.makeText(DeviceManagementActivity.this, "Panel service not available", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void loadData() {
        AbsBizBundleFamilyService familyService = getService();
        if (familyService == null || familyService.getCurrentHomeId() == 0) {
            Toast.makeText(this, "请先选择家庭", Toast.LENGTH_SHORT).show();
            return;
        }

        ThingHomeSdk.newHomeInstance(familyService.getCurrentHomeId()).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(final HomeBean homeBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setData(homeBean.getDeviceList());
                    }
                });
            }

            @Override
            public void onError(String code, String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DeviceManagementActivity.this,
                                "获取设备列表失败: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private AbsBizBundleFamilyService getService() {
        return MicroServiceManager.getInstance().findServiceByInterface(AbsBizBundleFamilyService.class.getName());
    }

    public static void goControl(Activity activity) {
        Intent intent = new Intent(activity, DeviceManagementActivity.class);
        activity.startActivity(intent);
    }
}