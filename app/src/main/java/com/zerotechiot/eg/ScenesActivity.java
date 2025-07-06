package com.zerotechiot.eg;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.map.generalmap.ui.GeneralMapActivity;
import com.thingclips.smart.scene.api.IResultCallback;
import com.thingclips.smart.scene.business.api.IThingSceneBusinessService;
import com.thingclips.smart.scene.model.NormalScene;
import com.thingclips.smart.utils.ToastUtil;

import java.util.List;

public class ScenesActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton mAddScene;
    private MaterialButton mEditScene;
    private MaterialButton mSetLocation;
    private MaterialButton mSetMap;
    private MaterialButton mSaveMapData;

    private MaterialCardView headerCard;
    private MaterialCardView primaryActionsCard;
    private MaterialCardView locationCard;
    private MaterialCardView advancedCard;
    private MaterialCardView infoCard;

    private IThingSceneBusinessService iThingSceneBusinessService;
    private AbsBizBundleFamilyService mServiceByInterface;

    private static final int ADD_SCENE_REQUEST_CODE = 1001;
    private static final int EDIT_SCENE_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenes);

        initializeViews();
        setupToolbar();
        setupClickListeners();
        animateCardsIn();

        // Get scene business service
        iThingSceneBusinessService = MicroContext.findServiceByInterface(IThingSceneBusinessService.class.getName());
        mServiceByInterface = MicroContext.getServiceManager()
                .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
    }

    private void initializeViews() {
        // Initialize buttons
        mAddScene = findViewById(R.id.add_scene);
        mEditScene = findViewById(R.id.edit_scene);
        mSetLocation = findViewById(R.id.set_location);
        mSetMap = findViewById(R.id.set_map);
        mSaveMapData = findViewById(R.id.save_map_data);

        // Initialize cards for animations
        headerCard = findViewById(R.id.header_card);
        primaryActionsCard = findViewById(R.id.primary_actions_card);
        locationCard = findViewById(R.id.location_card);
        advancedCard = findViewById(R.id.advanced_card);
        infoCard = findViewById(R.id.info_card);
    }

    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        mAddScene.setOnClickListener(this);
        mEditScene.setOnClickListener(this);
        mSetLocation.setOnClickListener(this);
        mSetMap.setOnClickListener(this);
        mSaveMapData.setOnClickListener(this);
    }

    private void animateCardsIn() {
        // Animate cards sliding up with staggered timing
        MaterialCardView[] cards = { headerCard, primaryActionsCard, locationCard, advancedCard, infoCard };

        for (int i = 0; i < cards.length; i++) {
            MaterialCardView card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(100f);

            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay(i * 100)
                    .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.decelerate_quint))
                    .start();
        }
    }

    private void animateButtonClick(View button) {
        // Animate button press with scale
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 0.95f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 0.95f, 1.0f);

        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.success_green))
                .setTextColor(ContextCompat.getColor(this, R.color.text_inverse))
                .show();
    }

    private void showInfoMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.info_blue))
                .setTextColor(ContextCompat.getColor(this, R.color.text_inverse))
                .show();
    }

    @Override
    public void onClick(View view) {
        animateButtonClick(view);

        int id = view.getId();
        if (id == R.id.set_location) {
            setLocation();
        } else if (id == R.id.add_scene) {
            addScene();
        } else if (id == R.id.edit_scene) {
            editScene();
        } else if (id == R.id.set_map) {
            setMapClass();
        } else if (id == R.id.save_map_data) {
            saveMapData();
        }
    }

    /**
     * Edit scene, if you want to create weather-related conditional automation, you
     * need to integrate the map location business package
     * Domestic package:
     * api 'com.tuya.smart:tuyasmart-bizbundle-map_amap:x.x.x-x'
     * api 'com.tuya.smart:tuyasmart-bizbundle-location_amap:x.x.x-x'
     * International package:
     * api 'com.tuya.smart:tuyasmart-bizbundle-map_google:x.x.x-x'
     * api 'com.tuya.smart:tuyasmart-bizbundle-location_google:x.x.x-x'
     */
    private void editScene() {
        if (mServiceByInterface.getCurrentHomeId() == 0) {
            showInfoMessage("Please select a home first");
            return;
        }

        showInfoMessage("Loading existing scenes...");

        ThingHomeSdk.getSceneServiceInstance().baseService().getSimpleSceneAll(mServiceByInterface.getCurrentHomeId(),
                new IResultCallback<List<NormalScene>>() {

                    @Override
                    public void onSuccess(List<NormalScene> normalScenes) {
                        if (!normalScenes.isEmpty()) {
                            NormalScene sceneBean = normalScenes.get(0);
                            if (null != iThingSceneBusinessService) {
                                iThingSceneBusinessService.editSceneBean(ScenesActivity.this,
                                        mServiceByInterface.getCurrentHomeId(), sceneBean, EDIT_SCENE_REQUEST_CODE);
                            }
                        } else {
                            showInfoMessage("No scenes found. Create a scene first.");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        showInfoMessage("Error loading scenes: " + errorMessage);
                    }
                });
    }

    /**
     * Create scene, if you want to create weather-related conditional automation,
     * you need to integrate the map location business package
     * Domestic package:
     * api 'com.tuya.smart:tuyasmart-bizbundle-map_amap:x.x.x-x'
     * api 'com.tuya.smart:tuyasmart-bizbundle-location_amap:x.x.x-x'
     * International package:
     * api 'com.tuya.smart:tuyasmart-bizbundle-map_google:x.x.x-x'
     * api 'com.tuya.smart:tuyasmart-bizbundle-location_google:x.x.x-x'
     */
    private void addScene() {
        if (null != iThingSceneBusinessService && mServiceByInterface.getCurrentHomeId() != 0) {
            showInfoMessage("Opening scene creation...");
            iThingSceneBusinessService.addSceneBean(this, mServiceByInterface.getCurrentHomeId(),
                    ADD_SCENE_REQUEST_CODE);
        } else {
            showInfoMessage("Please select a home first");
        }
    }

    /**
     * set lng and lat use your map sdk in app
     */
    private void setLocation() {
        double lng = 120.06420814321443;
        double lat = 30.302782241301667;
        if (null != iThingSceneBusinessService) {
            iThingSceneBusinessService.setAppLocation(lng, lat);
            showSuccessMessage("Location coordinates set successfully");
        }
    }

    /**
     * Scene condition's location page
     * Note: Chinese city list default. Use it when your account is not a Chinese
     * account.
     */
    private void setMapClass() {
        if (null != iThingSceneBusinessService) {
            // TODO business map Activity
            iThingSceneBusinessService.setMapActivity(GeneralMapActivity.class);
            showSuccessMessage("Map activity configured successfully");
        }
    }

    /**
     * You can use the method to set location information after use custom map class
     * impl
     */
    private void saveMapData() {
        if (null != iThingSceneBusinessService) {
            // TODO save map data
            double lng = 120.06420814321443;
            double lat = 30.302782241301667;
            String city = "hangzhou";
            String address = "address";
            iThingSceneBusinessService.saveMapData(lng, lat, city, address);
            showSuccessMessage("Map location data saved successfully");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_SCENE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    onAddSuc(data);
                }
                break;
            case EDIT_SCENE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    onEditSuc(data);
                }
                break;
            default:
                break;
        }
    }

    /**
     * edit scene success
     *
     * @param data
     */
    private void onEditSuc(Intent data) {
        NormalScene sceneBean = (NormalScene) data.getSerializableExtra("NormalScene");
        if (null != sceneBean) {
            showSuccessMessage("Scene '" + sceneBean.getName() + "' edited successfully!");
        }
    }

    /**
     * add scene success
     *
     * @param data
     */
    private void onAddSuc(Intent data) {
        NormalScene sceneBean = (NormalScene) data.getSerializableExtra("NormalScene");
        if (null != sceneBean) {
            showSuccessMessage("Scene '" + sceneBean.getName() + "' created successfully!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}