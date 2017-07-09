package com.lpf.flashlight;

import android.app.Application;
import android.os.PowerManager;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
    }

    private void initAd() {
//        MobileAds.initialize(this, AdmobNativeAdManager.ADMOB_APP_ID);
    }

}
