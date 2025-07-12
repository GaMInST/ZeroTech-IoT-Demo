package com.zerotechiot.eg;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.router.UrlBuilder;
import com.thingclips.smart.api.service.RedirectService;
import com.thingclips.smart.api.service.RouteEventListener;
import com.thingclips.smart.api.service.ServiceEventListener;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.thingpackconfig.PackConfig;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TuyaSmartApp extends Application {

    private static final String TAG = "TuyaSmartApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "TuyaSmartApp onCreate started");

        // Initialize Tuya SDK
        ThingHomeSdk.init(this, "xxft3fqw93d375ucppkn", "k8u9edtefgrwcmkqaesra9gmgmpuh8uy");

        try {
            // Initialize database and clear any corrupted data
            initializeDatabase();

            Log.d(TAG, "Adding PackConfig value delegate");
            PackConfig.addValueDelegate(AppConfig.class);

            Log.d(TAG, "Initializing BizBundleInitializer");
            BizBundleInitializer.init(this, new RouteEventListener() {
                @Override
                public void onFaild(int errorCode, UrlBuilder urlBuilder) {
                    Log.e(TAG, "Route failed: " + urlBuilder.target + " error: " + errorCode);
                }
            }, new ServiceEventListener() {
                @Override
                public void onFaild(String serviceName) {
                    Log.e(TAG, "Service not implement: " + serviceName);
                }
            });

            Log.d(TAG, "BizBundleInitializer initialized successfully");

            // Register family service
            Log.d(TAG, "Registering family service");
            BizBundleInitializer.registerService(AbsBizBundleFamilyService.class, new BizBundleFamilyServiceImpl());

            // Set up URL interceptor
            Log.d(TAG, "Setting up URL interceptor");
            RedirectService service = MicroContext.getServiceManager()
                    .findServiceByInterface(RedirectService.class.getName());
            if (service != null) {
                service.registerUrlInterceptor(new RedirectService.UrlInterceptor() {
                    @Override
                    public void forUrlBuilder(UrlBuilder urlBuilder,
                            RedirectService.InterceptorCallback interceptorCallback) {
                        interceptorCallback.onContinue(urlBuilder);
                    }
                });
            }

            Log.d(TAG, "TuyaSmartApp onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error during TuyaSmartApp initialization", e);
            // Don't throw the exception to prevent app crash
            // Instead, log it and continue with basic functionality
        }
    }

    private void initializeDatabase() {
        try {
            // Clear any corrupted database files
            String[] databaseFiles = {
                    "tuya_smart.db",
                    "tuya_smart.db-journal",
                    "tuya_smart.db-wal",
                    "tuya_smart.db-shm"
            };

            for (String dbFile : databaseFiles) {
                try {
                    java.io.File file = getDatabasePath(dbFile);
                    if (file.exists() && file.length() == 0) {
                        file.delete();
                        Log.d(TAG, "Deleted corrupted database file: " + dbFile);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Could not check/delete database file: " + dbFile, e);
                }
            }

            Log.d(TAG, "Database initialization completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during database initialization", e);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "Installing MultiDex");
        MultiDex.install(this);
    }

}
