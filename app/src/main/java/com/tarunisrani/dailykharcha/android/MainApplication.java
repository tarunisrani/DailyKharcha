package com.tarunisrani.dailykharcha.android;

import android.app.Application;

import com.tarunisrani.dailykharcha.listeners.ServiceConnectionListener;
import com.tarunisrani.dailykharcha.utils.AppUtils;

/**
 * Created by tarunisrani on 3/1/17.
 */

public class MainApplication extends Application implements ServiceConnectionListener {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.performServiceStartOperation(this, this);
    }

    @Override
    public void onServiceBind(BackendService service) {

    }
}
