package com.tarunisrani.dailykharcha.android;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BackendService backendService;

//    private DatabaseReference myRef;
//    private FirebaseAuth mAuth;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_todo = (Button) findViewById(R.id.button_todo);
        Button button_reminder = (Button) findViewById(R.id.button_reminder);
        Button button_daily_expense = (Button) findViewById(R.id.button_daily_expense);
        Button button_logout = (Button) findViewById(R.id.button_logout);

        button_todo.setOnClickListener(this);
        button_reminder.setOnClickListener(this);
        button_daily_expense.setOnClickListener(this);
        button_logout.setOnClickListener(this);



        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                HashMap value = (HashMap) dataSnapshot.getValue();

                Log.d(TAG, "Value is: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
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

    private void performLogOutOperation(){
        firebaseAuth.signOut();
    }


    @Override
    protected void onResume() {
        super.onResume();
        performServiceStartOperation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(AppUtils.getService()!=null && isServiceRunning(AppUtils.getService().getClass())) {
            try {
                unbindService(mConnection);
            }catch (IllegalArgumentException exp){
                exp.printStackTrace();
            }
        }
    }



    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            BackendService.MyBinder b = (BackendService.MyBinder) binder;
            backendService = b.getService();
            AppUtils.setService(backendService);
            backendService.startService(b.getmIntent());
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();

            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();
            checkUserStatus();
        }

        public void onServiceDisconnected(ComponentName className) {
            backendService = null;
            AppUtils.setService(backendService);
            Toast.makeText(MainActivity.this, "onServiceDisconnected", Toast.LENGTH_SHORT)
                    .show();
        }
    };


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void openToDoScreen(){
//        startActivity(new Intent(this, TodoActivity.class));

//        myRef.setValue("A");
//        myRef.push().child("A");
//        myRef.push().setValue("B");
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void openReminderScreen(){
//        startActivity(new Intent(this, ReminderActivity.class));

//        myRef.child("A").child("B").setValue("C");
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void openDailyExpenseScreen(){
        startActivity(new Intent(this, ExpenseActivity.class));
        finish();
    }

    private void performLoggedInOperation(){
//        performServiceStartOperation();
//        AppUtils.getService().performLoginOperation(user.getUid());
        AppUtils.getService().startListeners(user.getUid());
        openDailyExpenseScreen();
        finish();
    }

    private void openLoginScreen(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void performServiceStartOperation(){
        if(AppUtils.getService()==null) {
            Intent intent = new Intent(this, BackendService.class);
            bindService(intent, mConnection,
                    Context.BIND_AUTO_CREATE);
        }else{
            checkUserStatus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_todo:
                openToDoScreen();
                break;
            case R.id.button_reminder:
                openReminderScreen();
                break;
            case R.id.button_daily_expense:
                openDailyExpenseScreen();
                break;
            case R.id.button_logout:
                performLogOutOperation();
                break;
        }
    }
}
