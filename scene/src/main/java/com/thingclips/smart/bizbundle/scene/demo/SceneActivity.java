package com.thingclips.smart.bizbundle.scene.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.map.generalmap.ui.GeneralMapActivity;
import com.thingclips.smart.scene.api.IResultCallback;
import com.thingclips.smart.scene.business.api.IThingSceneBusinessService;
import com.thingclips.smart.scene.model.NormalScene;
import com.thingclips.smart.utils.ToastUtil;

import java.util.List;

public class SceneActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialCardView mAddSceneCard;
    private MaterialCardView mEditSceneCard;
    private MaterialCardView mSetLocationCard;
    private MaterialCardView mSetMapCard;
    private ExtendedFloatingActionButton mFabCreateScene;
    private IThingSceneBusinessService iThingSceneBusinessService;
    private static final int ADD_SCENE_REQUEST_CODE = 1001;
    private static final int EDIT_SCENE_REQUEST_CODE = 1002;
    private AbsBizBundleFamilyService mServiceByInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Smart Scenes");
        }

        // Initialize views
        mAddSceneCard = findViewById(R.id.add_scene_card);
        mEditSceneCard = findViewById(R.id.edit_scene_card);
        mSetLocationCard = findViewById(R.id.set_location_card);
        mSetMapCard = findViewById(R.id.set_map_card);
        mFabCreateScene = findViewById(R.id.fab_create_scene);

        // Set click listeners
        mAddSceneCard.setOnClickListener(this);
        mEditSceneCard.setOnClickListener(this);
        mSetLocationCard.setOnClickListener(this);
        mSetMapCard.setOnClickListener(this);
        mFabCreateScene.setOnClickListener(this);

        // Get scene business service
        iThingSceneBusinessService = MicroContext.findServiceByInterface(IThingSceneBusinessService.class.getName());
        mServiceByInterface = MicroContext.getServiceManager()
                .findServiceByInterface(AbsBizBundleFamilyService.class.getName());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.add_scene_card || id == R.id.fab_create_scene) {
            addScene();
        } else if (id == R.id.edit_scene_card) {
            editScene();
        } else if (id == R.id.set_location_card) {
            setLocation();
        } else if (id == R.id.set_map_card) {
            setMapClass();
        }
    }

    /**
     * Edit scene, if you want to create weather-related conditional automation, you
     * need to integrate the map location business package
     * Domestic package:
     * api 'com.thingclips.smart:tuyasmart-bizbundle-map_amap:x.x.x-x'
     * api 'com.thingclips.smart:tuyasmart-bizbundle-location_amap:x.x.x-x'
     * 国际包：
     * api 'com.thingclips.smart:tuyasmart-bizbundle-map_google:x.x.x-x'
     * api 'com.thingclips.smart:tuyasmart-bizbundle-location_google:x.x.x-x'
     */
    private void editScene() {

        if (mServiceByInterface.getCurrentHomeId() == 0) {
            ToastUtil.shortToast(this, "Please select a home first");
            return;
        }

        ThingHomeSdk.getSceneServiceInstance().baseService().getSimpleSceneAll(mServiceByInterface.getCurrentHomeId(),
                new IResultCallback<List<NormalScene>>() {

                    @Override
                    public void onSuccess(List<NormalScene> normalScenes) {
                        if (!normalScenes.isEmpty()) {
                            NormalScene sceneBean = normalScenes.get(0);
                            if (null != iThingSceneBusinessService) {
                                iThingSceneBusinessService.editSceneBean(SceneActivity.this,
                                        mServiceByInterface.getCurrentHomeId(), sceneBean, EDIT_SCENE_REQUEST_CODE);
                            }
                        } else {
                            ToastUtil.shortToast(SceneActivity.this, "No scenes available to edit");
                        }
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        ToastUtil.shortToast(SceneActivity.this, "Failed to load scenes: " + errorMessage);
                    }
                });
    }

    /**
     * Create scene, if you want to create weather-related conditional automation,
     * you need to integrate the map location business package
     * Domestic package:
     * api 'com.thingclips.smart:tuyasmart-bizbundle-map_amap:x.x.x-x'
     * api 'com.thingclips.smart:tuyasmart-bizbundle-location_amap:x.x.x-x'
     * International package:
     * api 'com.thingclips.smart:tuyasmart-bizbundle-map_google:x.x.x-x'
     * api 'com.thingclips.smart:tuyasmart-bizbundle-location_google:x.x.x-x'
     */
    private void addScene() {
        if (null != iThingSceneBusinessService && mServiceByInterface.getCurrentHomeId() != 0) {
            iThingSceneBusinessService.addSceneBean(this, mServiceByInterface.getCurrentHomeId(),
                    ADD_SCENE_REQUEST_CODE);
        } else {
            ToastUtil.shortToast(this, "Please select a home first");
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
            ToastUtil.shortToast(this, "Location set successfully");
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
            ToastUtil.shortToast(this, "Map settings configured");
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
            ToastUtil.shortToast(this, "Scene：" + sceneBean.getName() + " edit success!");
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
            ToastUtil.shortToast(this, "Scene：" + sceneBean.getName() + " create success!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    /**
     * Setup floating action button animations
     */
    private void setupFabAnimations() {
        // Load animations
        Animation fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        Animation fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);
        
        // Show FAB with animation on activity start
        mFabCreateScene.startAnimation(fabShowAnimation);
    }
}
