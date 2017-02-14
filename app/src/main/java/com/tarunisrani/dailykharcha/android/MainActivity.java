package com.tarunisrani.dailykharcha.android;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private BackendService backendService;

    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button_todo = (Button) findViewById(R.id.button_todo);
        Button button_reminder = (Button) findViewById(R.id.button_reminder);
        Button button_daily_expense = (Button) findViewById(R.id.button_daily_expense);

        button_todo.setOnClickListener(this);
        button_reminder.setOnClickListener(this);
        button_daily_expense.setOnClickListener(this);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
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
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, BackendService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
//        startService(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            BackendService.MyBinder b = (BackendService.MyBinder) binder;
            backendService = b.getService();
            backendService.startService(b.getmIntent());
            Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            backendService = null;
            Toast.makeText(MainActivity.this, "onServiceDisconnected", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private void openToDoScreen(){
//        startActivity(new Intent(this, TodoActivity.class));

//        myRef.setValue("A");
//        myRef.push().child("A");
//        myRef.push().setValue("B");

    }

    private void openReminderScreen(){
//        startActivity(new Intent(this, ReminderActivity.class));

//        myRef.child("A").child("B").setValue("C");

    }

    private void openDailyExpenseScreen(){
        startActivity(new Intent(this, ExpenseActivity.class));
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
        }
    }
}
