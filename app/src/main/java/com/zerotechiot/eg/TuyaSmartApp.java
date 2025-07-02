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

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TuyaSmartApp extends Application {

    private static final String TAG = "TuyaSmartApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "TuyaSmartApp onCreate started");

        try {
            // // Please don't change the order.
            // // Please do not change the initialization order
            // FrescoManager.initFresco(this);
            // ThingHomeSdk.init(this);
            // ThingWrapper.init(this, new RouteEventListener() {
            // @Override
            // public void onFaild(int errorCode, UrlBuilder urlBuilder) {
            // // urlBuilder.target is a router address, urlBuilder.params is a router
            // params
            // //No response when clicked indicates route is not implemented, need to
            // implement here, urlBuilder.target target route, urlBuilder.params route
            // parameters
            // Log.e("router not implement", urlBuilder.target + " : " +
            // urlBuilder.params.toString());
            // }
            // }, new ServiceEventListener() {
            // @Override
            // public void onFaild(String serviceName) {
            // Log.e("service not implement", serviceName);
            // }
            // });
            // ThingThemeInitializer.INSTANCE.init(this);
            // ThingOptimusSdk.init(this);

            Log.d(TAG, "Adding PackConfig value delegate");
            PackConfig.addValueDelegate(AppConfig.class);

            // todo replace the above code with the following code
            // todo replace the above code with the following code
            Log.d(TAG, "Initializing BizBundleInitializer");
            BizBundleInitializer.init(this, new RouteEventListener() {
                @Override
                public void onFaild(int errorCode, UrlBuilder urlBuilder) {
                    // urlBuilder.target is a router address, urlBuilder.params is a router params
                    // No response when clicked indicates route is not implemented, need to
                    // implement here, urlBuilder.target target route, urlBuilder.params route
                    // parameters
                    Log.e(TAG, "Route failed: " + urlBuilder.target + " error: " + errorCode);
                }
            }, new ServiceEventListener() {
                @Override
                public void onFaild(String serviceName) {
                    Log.e(TAG, "Service not implement: " + serviceName);
                }
            });

            Log.d(TAG, "BizBundleInitializer initialized successfully");

            // If your application does not provide in-app theme mode switching
            // functionality, then force set a mode at startup, you can enable the following
            // code
            // NightModeUtil.INSTANCE.setAppNightMode(AppUiMode.MODE_FOLLOW_SYSTEM);

            // register family service，mall bizbundle don't have to implement it.
            // Register family service, mall business package does not need to register this
            // service
            Log.d(TAG, "Registering family service");
            BizBundleInitializer.registerService(AbsBizBundleFamilyService.class, new BizBundleFamilyServiceImpl());

            // Intercept existing routes and jump to custom implementation pages with
            // parameters
            // Intercept existing routes and jump to custom implementation pages with
            // parameters
            Log.d(TAG, "Setting up URL interceptor");
            RedirectService service = MicroContext.getServiceManager()
                    .findServiceByInterface(RedirectService.class.getName());
            service.registerUrlInterceptor(new RedirectService.UrlInterceptor() {
                @Override
                public void forUrlBuilder(UrlBuilder urlBuilder,
                        RedirectService.InterceptorCallback interceptorCallback) {
                    // Such as:
                    // Intercept the event of clicking the panel right menu and jump to the custom
                    // page with the parameters of urlBuilder
                    // For example: intercept the event of clicking the panel top right button, jump
                    // to custom page with urlBuilder parameters
                    // if (urlBuilder.target.equals("panelAction") &&
                    // urlBuilder.params.getString("action").equals("gotoPanelMore")) {
                    // interceptorCallback.interceptor("interceptor");
                    // Log.e("interceptor", urlBuilder.params.toString());
                    // } else {
                    interceptorCallback.onContinue(urlBuilder);
                    // }
                }
            });

            Log.d(TAG, "TuyaSmartApp onCreate completed successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error during TuyaSmartApp initialization", e);
            throw e;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(TAG, "Installing MultiDex");
        MultiDex.install(this);
    }

}
