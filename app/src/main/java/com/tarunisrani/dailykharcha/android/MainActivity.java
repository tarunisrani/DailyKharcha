package com.tarunisrani.dailykharcha.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.ServiceConnectionListener;
import com.tarunisrani.dailykharcha.utils.AppConstant;
import com.tarunisrani.dailykharcha.utils.AppUtils;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BackendService backendService;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(intent!=null) {
            String group_id = intent.getStringExtra(AppConstant.INTENT_KEY_GROUP);
            if(group_id!=null && !group_id.isEmpty()){
                new SharedPreferrenceUtil().setSelectedGroup(this, group_id);
            }
        }

        FireBaseInitialization();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

    }

    private void FireBaseInitialization() {
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.VALUE, "Firebase");
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(MainActivity.this);
        analytics.logEvent("Firebase", params);
    }

    private void checkUserStatus(){

        if(user!=null){
            Log.e("User", "Logged in "+user.getUid());
            performLoggedInOperation();
        }else{
            Log.e("User", "Logged out");
            openLoginScreen();
        }

    }


    @Override
    protected void onResume() {
        AppUtils.performServiceStartOperation(this, new ServiceConnectionListener() {
            @Override
            public void onServiceBind(BackendService service) {
                checkUserStatus();
            }
        });

//        checkUserStatus();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.performUnbindService(this);
    }

    private void openDailyExpenseScreen(){
        Intent intent = new Intent(this, ExpenseActivity.class);
        startActivity(intent);
        finish();
    }

    private void performLoggedInOperation(){
//        performServiceStartOperation();
//        AppUtils.getService().performLoginOperation(user.getUid());
        AppUtils.getService().initializeFirebase(user.getUid());
        AppUtils.getService().startListeners(user.getUid());
        openDailyExpenseScreen();
        finish();
    }

    private void openLoginScreen(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_toolbar, menu);
        return true;
    }
}
