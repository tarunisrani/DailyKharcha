package com.tarunisrani.dailykharcha.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.model.Sheet;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 2/13/17.
 */

public class BackendService extends Service {

    public static final String FILTER_EXPENSE = "com.tarunisrani.dailykharcha.android.backendservice.expense";
    public static final String FILTER_SHEET = "com.tarunisrani.dailykharcha.android.backendservice.sheet";
    public static final String ACTION_ADDED = "ACTION_ADDED";
    public static final String ACTION_MODIFIED = "ACTION_MODIFIED";

    private final MyBinder mBinder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSheetListener();
        startExpenseListener();
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT)
                .show();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind", Toast.LENGTH_SHORT)
                .show();
        mBinder.setmIntent(intent);
        return mBinder;
    }

    class MyBinder extends Binder {
        public Intent getmIntent() {
            return mIntent;
        }

        public void setmIntent(Intent mIntent) {
            this.mIntent = mIntent;
        }

        private Intent mIntent;

        BackendService getService() {
            return BackendService.this;
        }
    }

    private void startExpenseListener(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("expense");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Service Expense", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Service Expense", "null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Service Expense", "onCancelled");
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildAdded", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Expense onChildAdded", "null");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildChanged", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Expense onChildChanged", "null");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildRemoved", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Expense onChildRemoved", "null");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildMoved", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Expense onChildMoved", "null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void startSheetListener(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("sheet");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Sheet> sheetArrayList = new ArrayList<>();
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Service Sheet", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Service Sheet", "null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Service Sheet", "onCancelled");
            }
        });
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Sheet onChildAdded", dataSnapshot.getValue().toString());
                    Sheet sheet = dataSnapshot.getValue(Sheet.class);
                    sheet.setServer_id(dataSnapshot.getKey());
                    publishSheetResults(sheet, ACTION_ADDED);
                }else{
                    Log.e("Sheet onChildAdded", "null");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Sheet onChildChanged", dataSnapshot.getValue().toString());
                    Sheet sheet = dataSnapshot.getValue(Sheet.class);
                    sheet.setServer_id(dataSnapshot.getKey());
                    publishSheetResults(sheet, ACTION_MODIFIED);
                }else{
                    Log.e("Sheet onChildChanged", "null");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Sheet onChildRemoved", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Sheet onChildRemoved", "null");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Sheet onChildMoved", dataSnapshot.getValue().toString());
                }else{
                    Log.e("Sheet onChildMoved", "null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void createExpenseEntryOnServer(final Expense expense) throws JSONException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("expense").push();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    ExpenseDataSource expenseDataSource = new ExpenseDataSource(BackendService.this);
                    Log.e("Expense", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
                    expense.setServer_expense_id(dataSnapshot.getKey());
                    if (expenseDataSource.updateExpenseEntry(expense)!=-1) {
                        Log.d("Update", "Successfully updated server expense id");
                    } else {
                        Log.d("Update", "Failed to update server expense id");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.setValue(expense);
    }

    public void updateExpenseEntryOnServer(final Expense expense) throws JSONException{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("expense").child(expense.getSheet_id()+"").child(expense.getServer_expense_id());
        reference.setValue(expense);
    }

    public void createSheetEntryOnServer(final Sheet sheet) throws JSONException{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("sheet");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(BackendService.this);
                    Log.e("Expense", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
                    sheet.setServer_id(dataSnapshot.getKey());
                    if (expenseSheetDataSource.updateSheetEntry(sheet)) {
                        Log.d("Update", "Successfully updated server sheet id");
                    } else {
                        Log.d("Update", "Failed to update server sheet id");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.push().setValue(sheet);
    }

    public void updateSheetEntryOnServer(Sheet sheet){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("sheet").child(sheet.getServer_id());
        reference.setValue(sheet);
    }

    private void publishSheetResults(Sheet sheet, String action) {
        Intent intent = new Intent(FILTER_SHEET);
        intent.putExtra("SHEET", sheet);
        intent.putExtra("ACTION", action);
        sendBroadcast(intent);
    }

    private void publishExpenseResults(Expense expense, String action) {
        Intent intent = new Intent(FILTER_EXPENSE);
        intent.putExtra("EXPENSE", expense);
        intent.putExtra("ACTION", action);
        sendBroadcast(intent);
    }


}
