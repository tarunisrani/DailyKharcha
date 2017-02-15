package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.listeners.ServerExpenseDataListener;
import com.tarunisrani.dailykharcha.model.Sheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class ExpenseSheetDataSource {

    public static final String TABLE_NAME = "sheet";

    public static final String COLUMN_ID = "sheet_id";
    public static final String COLUMN_ID_SERVER = "sheet_id_server";
    public static final String COLUMN_NAME = "sheet_name";
    public static final String COLUMN_DATE = "sheet_date";
    public static final String COLUMN_AMOUNT = "expense_amount";

    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ID_SERVER + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_AMOUNT + " float "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_ID_SERVER, COLUMN_DATE, COLUMN_NAME, COLUMN_AMOUNT, COLUMN_AMOUNT};

    private ValueEventListener valueEventListener;

    public ExpenseSheetDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.createTable(DATABASE_CREATE);
    }

    public long createSheetEntry(Sheet sheet){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_SERVER, sheet.getServer_id());
        values.put(COLUMN_DATE, sheet.getSheet_creation_date());
        values.put(COLUMN_NAME, sheet.getSheet_name());
        values.put(COLUMN_AMOUNT, sheet.getAmount());

        long insertId = database.insert(TABLE_NAME, null,
                values);
        database.close();
        return insertId;
    }

    public boolean isSheetEntryExist(Sheet sheet){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID_SERVER + " = " + DatabaseUtils.sqlEscapeString(sheet.getServer_id()), null,
                null, null, null);

        boolean exist = cursor.getCount()>0;

        database.close();
        cursor.close();
        return exist;
    }

    public void createSheetEntryOnServer(final Sheet sheet) throws JSONException{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(TABLE_NAME);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(COLUMN_DATE, sheet.getSheet_creation_date());
        jsonObject.put(COLUMN_NAME, sheet.getSheet_name());
        jsonObject.put(COLUMN_AMOUNT, sheet.getAmount());


        /*reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null && dataSnapshot.getValue()!=null) {
                    Log.e("Expense", dataSnapshot.getKey() + "  --  " + dataSnapshot.getValue().toString());
                    sheet.setServer_id(dataSnapshot.getKey());
                    if (updateSheetEntry(sheet)) {
                        Log.d("Update", "Successfully updated server sheet id");
                    } else {
                        Log.d("Update", "Failed to update server sheet id");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        reference.push().setValue(sheet);
    }

    public ArrayList<Sheet> getSheetList(){
        ArrayList<Sheet> sheetArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            sheetArrayList.add(new Sheet(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return sheetArrayList;
    }

    public void getSheetItemsFromServer(final boolean oneTimeFetch, final ServerExpenseDataListener listener){
//        removeSync();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(TABLE_NAME);
        /*valueEventListener = reference.child("sheets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Expense> expenseArrayList = new ArrayList<>();
                if (dataSnapshot!=null) {
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot dataSnapshot_temp = iterator.next();
                        Expense expense = dataSnapshot_temp.getValue(Expense.class);
                        expense.setServer_expense_id(dataSnapshot_temp.getKey());
                        expenseArrayList.add(expense);
                        Log.e("Expense", dataSnapshot_temp.getKey() + "  --  " + dataSnapshot_temp.getValue().toString());
                    }

                }
                if(oneTimeFetch) {
                    reference.child("sheets").removeEventListener(this);
                }
                listener.onServerDataRetrieved(expenseArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void removeSync(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(TABLE_NAME);
        if(valueEventListener!=null) {
            reference.child("sheets").removeEventListener(valueEventListener);
        }

    }

    public ArrayList<Sheet> getSheetListFromServer(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(TABLE_NAME);
        ArrayList<Sheet> sheetArrayList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
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



        return sheetArrayList;
    }

    public ArrayList<Sheet> getSheet(int id){
        ArrayList<Sheet> saleItemArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            saleItemArrayList.add(new Sheet(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return saleItemArrayList;
    }

    public boolean updateSheetEntry(Sheet sheet){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_SERVER, sheet.getServer_id());
        values.put(COLUMN_DATE, sheet.getSheet_creation_date());
        values.put(COLUMN_NAME, sheet.getSheet_name());
        values.put(COLUMN_AMOUNT, sheet.getAmount());

        long count = database.update(TABLE_NAME,
                values, COLUMN_ID + " = " + sheet.getSheet_id(), null);
        database.close();

        return count == 1;
    }

    public boolean updateSheetEntryWithServerId(Sheet sheet){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_SERVER, sheet.getServer_id());
        values.put(COLUMN_DATE, sheet.getSheet_creation_date());
        values.put(COLUMN_NAME, sheet.getSheet_name());
        values.put(COLUMN_AMOUNT, sheet.getAmount());

        long count = database.update(TABLE_NAME,
                values, COLUMN_ID_SERVER + " = " + DatabaseUtils.sqlEscapeString(sheet.getServer_id()), null);
        database.close();

        return count == 1;
    }

    public boolean removeSheetEntry(Sheet sheet){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(sheet.getSheet_id())});
        database.close();

        return count > 0;
    }
}
