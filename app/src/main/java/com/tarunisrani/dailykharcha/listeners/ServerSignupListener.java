package com.tarunisrani.dailykharcha.listeners;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by tarunisrani on 2/20/17.
 */

public interface ServerSignupListener {
    void onSignupCompleted(FirebaseUser user);
    void onSignupFailed();
}
