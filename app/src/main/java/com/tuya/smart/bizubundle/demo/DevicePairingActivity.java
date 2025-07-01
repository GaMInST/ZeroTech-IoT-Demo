package com.tuya.smart.bizubundle.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DevicePairingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Directly launch the BizBundle's DeviceActivatorActivity
        Intent intent = new Intent();
        intent.setClassName(this, "com.tuya.smart.bizbundle.activator.demo.DeviceActivatorActivity");
        startActivity(intent);
        finish();
    }
}