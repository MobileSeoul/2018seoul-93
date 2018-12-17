package com.minsung.examples.trafficlight;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;

/**
 * Created by minsung on 2018-10-01.
 */

public class AdvertiseCallbackListener extends AdvertiseCallback {
    boolean flag;
    AdvertiseCallbackListener(boolean flag){
        this.flag = flag;
    }

    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        super.onStartSuccess(settingsInEffect);
    }

    public void onStartSuccessF(AdvertiseSettings settingsInEffect) {
        super.onStartSuccess(settingsInEffect);
        flag = true;
    }
}
