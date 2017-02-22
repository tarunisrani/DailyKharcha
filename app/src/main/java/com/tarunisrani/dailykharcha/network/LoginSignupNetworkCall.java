package com.tarunisrani.dailykharcha.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.listeners.FirebaseListener;
import com.tarunisrani.dailykharcha.listeners.ServerLoginListener;
import com.tarunisrani.dailykharcha.listeners.ServerSignupListener;
import com.tarunisrani.dailykharcha.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method.POST;

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

    public void createNewUserInServer(Context context, String name, String email, String uid, final ServerSignupListener listener){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", email);
            jsonObject.put("user_name", name);
            jsonObject.put("uid", uid);
        }catch(JSONException exp){
            exp.printStackTrace();
        }

        String url = "http://funstuff.co.in/dailykharcha/submit.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                listener.onSignupCompleted(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
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



