package com.tarunisrani.dailykharcha.network;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.listeners.FirebaseListener;
import com.tarunisrani.dailykharcha.listeners.ServerLoginListener;
import com.tarunisrani.dailykharcha.listeners.ServerSignupListener;
import com.tarunisrani.dailykharcha.utils.AppUtils;

/**
 * Created by tarunisrani on 1/31/17.
 */
public class LoginSignupNetworkCall {

    public void createNewUserInFirebase(final Context context, final String name, final String email, String password, final ServerSignupListener listener){
        AppUtils.getService().performSignUp(email, password, new FirebaseListener() {
            @Override
            public void onSignupCompleted(FirebaseUser user) {
                if(user!=null){
//                    createNewUserInServer(context, name, email, user.getUid(), listener);
                    listener.onSignupCompleted(user);
                }
            }

            @Override
            public void onLoginCompleted(FirebaseUser user) {

            }
        });
    }

    public void loginUser(final String email, String password, final ServerLoginListener listener){
        AppUtils.getService().performSignIn(email, password, new FirebaseListener() {
            @Override
            public void onSignupCompleted(FirebaseUser user) {

            }

            @Override
            public void onLoginCompleted(FirebaseUser user) {
                if(user!=null){
                    listener.onLoginCompleted(user);
                }
            }
        });
    }

}



