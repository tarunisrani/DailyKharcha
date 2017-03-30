package com.tarunisrani.dailykharcha.listeners;

import com.tarunisrani.dailykharcha.android.BackendService;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface ServiceConnectionListener {
    void onServiceBind(BackendService service);
}
