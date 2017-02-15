package com.tarunisrani.dailykharcha.utils;

import com.tarunisrani.dailykharcha.android.BackendService;

/**
 * Created by tarunisrani on 2/14/17.
 */

public class AppUtils {
    public static BackendService getService() {
        return mService;
    }

    public static void setService(BackendService mService) {
        AppUtils.mService = mService;
    }

    private static BackendService mService = null;

}
