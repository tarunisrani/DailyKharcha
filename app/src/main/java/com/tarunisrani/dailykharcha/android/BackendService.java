package com.tarunisrani.dailykharcha.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;
import com.tarunisrani.dailykharcha.listeners.AddGroupOnServerListener;
import com.tarunisrani.dailykharcha.listeners.FirebaseListener;
import com.tarunisrani.dailykharcha.listeners.UserListGenerationListener;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.Sheet;
import com.tarunisrani.dailykharcha.model.UserDetails;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tarunisrani on 2/13/17.
 */

public class BackendService extends Service {

    private static final String TAG = "BackendService";

    public static final String FILTER_EXPENSE = "com.tarunisrani.dailykharcha.android.backendservice.expense";
    public static final String FILTER_SHEET = "com.tarunisrani.dailykharcha.android.backendservice.sheet";
    public static final String FILTER_GROUP = "com.tarunisrani.dailykharcha.android.backendservice.group";
    public static final String ACTION_ADDED = "ACTION_ADDED";
    public static final String ACTION_MODIFIED = "ACTION_MODIFIED";

    private final MyBinder mBinder = new MyBinder();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase database ;
    private DatabaseReference global_user_reference;
    private DatabaseReference global_user_details_reference;
    private DatabaseReference global_database_reference;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mAuth = FirebaseAuth.getInstance();

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

    public void initializeFirebase(String user_id){
        database = FirebaseDatabase.getInstance();
        global_user_reference = database.getReference("dailykharcha").child("users").child(user_id);
        global_user_details_reference = database.getReference("dailykharcha").child("users");
//        global_database_reference = database.getReference("dailykharcha").child("databases").child(selected_group_id);
        global_database_reference = database.getReference("dailykharcha").child("databases");
    }

    public void startListeners(String user_id){

        startAuthenticationListener();

        GroupDataSource groupDataSource = new GroupDataSource(this);
        ArrayList<Group> groupArrayList = groupDataSource.getGroupItems();

        for(Group group: groupArrayList){
            startSheetListener(group.getGroup_id());
            startExpenseListener(group.getGroup_id());
        }

//        startSheetListener(selected_group_id);
//        startExpenseListener(selected_group_id);

        startGroupListener();
        startSharedGroupListener();

    }

    private void startAuthenticationListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    try {
//                        String token = AppUtils.convertSimpleStringToJWTString(generatePayLoad(user.getUid()));
//                        Log.d(TAG, "onAuthStateChanged:signed_in:" + token);

                    } catch (Exception exp){
                        exp.printStackTrace();
                    }


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        if(mAuth!=null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    private void startExpenseListener(String selected_group_id){
        Log.d("Listener", "Expense listener started");
        final DatabaseReference reference = global_database_reference.child(selected_group_id).child("expense");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildAdded", dataSnapshot.getValue().toString());
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    expense.setServer_expense_id(dataSnapshot.getKey());
                    addExpenseIfNotExist(expense);
                    publishExpenseResults(expense, ACTION_ADDED);
                }else{
                    Log.e("Expense onChildAdded", "null");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense onChildChanged", dataSnapshot.getValue().toString());
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    expense.setServer_expense_id(dataSnapshot.getKey());
                    updateExpense(expense);
                    publishExpenseResults(expense, ACTION_MODIFIED);
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

    private void startSheetListener(String selected_group_id){
        Log.d("Listener", "Sheet listener started");
        final DatabaseReference reference = global_database_reference.child(selected_group_id).child("sheet");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Sheet onChildAdded", dataSnapshot.getValue().toString());
                    Sheet sheet = dataSnapshot.getValue(Sheet.class);
                    sheet.setServer_id(dataSnapshot.getKey());
                    addSheetIfNotExist(sheet);
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
                    updateSheet(sheet);
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

    private void startGroupListener(){
        final DatabaseReference reference = global_user_reference.child("group");

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Group onChildAdded", dataSnapshot.getValue().toString());
                    Group group = dataSnapshot.getValue(Group.class);

                    addGroupIfNotExist(group);
//                    publishExpenseResults(expense, ACTION_ADDED);
                }else{
                    Log.e("Group onChildAdded", "null");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Group onChildChanged", dataSnapshot.getValue().toString());
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    expense.setServer_expense_id(dataSnapshot.getKey());
                    updateExpense(expense);
                    publishExpenseResults(expense, ACTION_MODIFIED);
                }else{
                    Log.e("Group onChildChanged", "null");
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

    private void startSharedGroupListener(){
        final DatabaseReference reference = global_user_reference.child("sharedgroup");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Shared onChildAdded", dataSnapshot.getValue().toString());
                    Group group = dataSnapshot.getValue(Group.class);
                    addGroupIfNotExist(group);
                    publishExpenseResults(group, ACTION_ADDED);
                }else{
                    Log.e("Shared onChildAdded", "null");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Shared onChildChanged", dataSnapshot.getValue().toString());
                    Group group = dataSnapshot.getValue(Group.class);
                    updateGroup(group);
                    publishExpenseResults(group, ACTION_ADDED);

                }else{
                    Log.e("Shared onChildChanged", "null");
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

    private void addSheetIfNotExist(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(!expenseSheetDataSource.isSheetEntryExist(sheet)){
            createSheetEntryInDb(sheet);
        }
    }

    private boolean createSheetEntryInDb(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        return expenseSheetDataSource.createSheetEntry(sheet);
    }

    private boolean updateSheet(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        return expenseSheetDataSource.updateSheetEntry(sheet);
    }

    private void addExpenseIfNotExist(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        if(!expenseDataSource.isExpenseEntryExist(expense)){
            createExpenseEntryInDb(expense);
        }
    }

    private boolean createExpenseEntryInDb(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        return expenseDataSource.createExpenseEntry(expense);
    }

    public boolean updateExpense(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        return expenseDataSource.updateExpenseEntry(expense);
    }

    private void addGroupIfNotExist(Group group){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        if(!groupDataSource.isGroupEntryExist(group)){
            if(createGroupEntryInDb(group)){
                startSheetListener(group.getGroup_id());
                startExpenseListener(group.getGroup_id());
            }
        }
    }

    private boolean createGroupEntryInDb(Group group){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        return groupDataSource.createGroupEntry(group);
    }

    public boolean updateGroup(Group group){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        return groupDataSource.updateGroupEntry(group);
    }

    public void createExpenseEntryOnServer(final Expense expense) {
        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);
        DatabaseReference reference = global_database_reference.child(selected_group_id).child("expense").push();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    ExpenseDataSource expenseDataSource = new ExpenseDataSource(BackendService.this);
                    Log.e("Expense", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
                    expense.setServer_expense_id(dataSnapshot.getKey());
                    if (expenseDataSource.updateExpenseEntry(expense)) {
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

    public void createGroupEntryOnServer(final Group group, final AddGroupOnServerListener listener) {
        DatabaseReference reference = global_user_reference.child("group").push();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Group", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
                    if(listener!=null){
                        listener.onGroupAddedOnServer();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.setValue(group);
    }

    public void createSharedGroupEntryOnServer(ArrayList<UserDetails> userDetailses, final Group group) {

        for(UserDetails userDetails : userDetailses){
            String user_id = userDetails.getUid();
            DatabaseReference reference = global_user_details_reference.child(user_id).child("sharedgroup").push();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                        Log.e("Group", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            reference.setValue(group);
        }


    }

    public void updateExpenseEntryOnServer(final Expense expense) {
        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);
        DatabaseReference reference = global_database_reference.child(selected_group_id).child("expense").child(expense.getServer_expense_id());
        reference.setValue(expense);
    }

    public void createSheetEntryOnServer(final Sheet sheet) throws JSONException{
        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);
        DatabaseReference reference = global_database_reference.child(selected_group_id).child("sheet").push();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(BackendService.this);

                    Log.e("Sheet", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
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

        reference.setValue(sheet);
    }

    public void updateSheetEntryOnServer(Sheet sheet){
        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);
        final DatabaseReference reference = global_database_reference.child(selected_group_id).child("sheet").child(sheet.getServer_id());

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

    private void publishExpenseResults(Group group, String action) {
        Intent intent = new Intent(FILTER_GROUP);
        intent.putExtra("GROUP", group);
        intent.putExtra("ACTION", action);
        sendBroadcast(intent);
    }

    public void performSignUp(String email, String password, final FirebaseListener listener){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(BackendService.this, "Signup Failed",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Signup", task.getException().toString());
                        }else{
                            Toast.makeText(BackendService.this, "Signup Success",
                                    Toast.LENGTH_SHORT).show();
                            if(listener!=null){
                                listener.onSignupCompleted(task.getResult().getUser());
                            }


                        }
                    }
                });

    }

    public void performSignIn(String email, String password, final FirebaseListener listener){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(BackendService.this, "SignIn Failed",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            if(listener!=null){
                                listener.onLoginCompleted(task.getResult().getUser());
                            }
                        }
                    }
                });
    }

    public void performSignInByToken(String token){
        Log.e(TAG, "Login using token");
        mAuth.signInWithCustomToken(token)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(BackendService.this, "SignIn Failed",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BackendService.this, "SignIn Success",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void performSignOut(){
        if(mAuth!=null){
            mAuth.signOut();
        }
    }

    public void performLoginOperation(String name, String user_id){

        String current_user_id = new SharedPreferrenceUtil().fetchUserID(this);
        if(current_user_id.equalsIgnoreCase(user_id)){
            //Relogin scenario
//            String group_id = user_id+"_default";
//            new SharedPreferrenceUtil().setSelectedGroup(this, group_id);
        }else{
            //Fresh login scenario
            performDBCleanOperation();
        }

        String group_id = user_id+"_default";
        new SharedPreferrenceUtil().setSelectedGroup(this, group_id);

        new SharedPreferrenceUtil().setUserName(this, name);
        new SharedPreferrenceUtil().setUserID(this, user_id);
    }

    public void performDBCleanOperation(){
        new ExpenseDataSource(this).cleanTable();
        new ExpenseSheetDataSource(this).cleanTable();
        new GroupDataSource(this).cleanTable();
    }

    public void createDefaultGroupInDB(String name, String user_id){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = new Group();
        String group_id = user_id+"_default";
        group.setGroup_id(group_id);

        if(!groupDataSource.isGroupEntryExist(group)){
            group.setGroup_name("default");
            group.setOwner_id(user_id);
            group.setOwner_name(name);
            if(groupDataSource.createGroupEntry(group)){

            }
        }

    }

    public void createDefaultGroupOnServer(String name, String user_id){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = new Group();
        String group_id = user_id+"_default";
        group.setGroup_id(group_id);

        if(!groupDataSource.isGroupEntryExist(group)){
            group.setGroup_name("default");
            group.setOwner_id(user_id);
            group.setOwner_name(name);
            createGroupEntryOnServer(group, null);
//            if(groupDataSource.createGroupEntry(group)){
//                createGroupEntryOnServer(group);
//            }
        }
        new SharedPreferrenceUtil().setSelectedGroup(this, group_id);

    }

    public boolean createGroupEntryInDB(Group group){
        GroupDataSource groupDataSource = new GroupDataSource(this);

        if(!groupDataSource.isGroupEntryExist(group)){
            if(groupDataSource.createGroupEntry(group)){
                return true;
            }else {
                Log.e("Add Group", "Error occurred while creating group.");
                return false;
            }
        }else{
            Log.e("Add Group", "Group already exist.");
            return false;
        }
    }


    public void storeUserDetails(String name, String email, String uid){

        UserDetails userDetails = new UserDetails();
        userDetails.setUser_name(name);
        userDetails.setUser_email(email);
        userDetails.setUid(uid);

        DatabaseReference reference = global_user_details_reference.child("userlist").push();
        reference.setValue(userDetails);

        reference = global_user_details_reference.child(uid).child("userdetail");
        reference.setValue(userDetails);

    }

    public void sendEmailVerification(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    public void getUserList(final UserListGenerationListener listener){

        final DatabaseReference reference = global_user_details_reference.child("userlist");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
//                    fetchUserList(reference, dataSnapshot.getChildrenCount(), listener);
                    fetchUserList(dataSnapshot, listener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        return userDetailsArrayList;
    }


    private void fetchUserList(DatabaseReference reference, final long count, final UserListGenerationListener listener){
        final String user_id = new SharedPreferrenceUtil().fetchUserID(this);
        final ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                    UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                    if(!user_id.equalsIgnoreCase(userDetails.getUid())) {
                        userDetailsArrayList.add(userDetails);
                    }
                    if(userDetailsArrayList.size() == count-1){
                        listener.onListGenerated(userDetailsArrayList);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchUserList(DataSnapshot dataSnapshot, final UserListGenerationListener listener){
        final String user_id = new SharedPreferrenceUtil().fetchUserID(this);
        final ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();

        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            UserDetails userDetails = iterator.next().getValue(UserDetails.class);
            if(!user_id.equalsIgnoreCase(userDetails.getUid())) {
                userDetailsArrayList.add(userDetails);
            }
        }

        listener.onListGenerated(userDetailsArrayList);

    }

    @Override
    public void onDestroy() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        super.onDestroy();
    }
}
