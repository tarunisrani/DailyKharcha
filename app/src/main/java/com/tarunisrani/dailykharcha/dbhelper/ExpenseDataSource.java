package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.listeners.ServerExpenseDataListener;
import com.tarunisrani.dailykharcha.model.Expense;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class ExpenseDataSource {

    public static final String TABLE_NAME = "expense";

    public static final String COLUMN_ID = "expense_id";
    public static final String COLUMN_ID_SERVER = "expense_id_server";
    public static final String COLUMN_SHEET_ID = "expense_sheet_id";
    public static final String COLUMN_DATE = "expense_date";
    public static final String COLUMN_DETAIL = "expense_detail";
    public static final String COLUMN_GROUP = "expense_group";
    public static final String COLUMN_AMOUNT = "expense_amount";
    public static final String COLUMN_EXPENSE_TYPE = "expense_type";
    public static final String COLUMN_PAYMENT_TYPE = "payment_type";

    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_ID + " text primary key, "
            + COLUMN_ID_SERVER + " text, "
            + COLUMN_SHEET_ID + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_DETAIL + " text, "
            + COLUMN_GROUP + " text, "
            + COLUMN_AMOUNT + " float, "
            + COLUMN_EXPENSE_TYPE + " int, "
            + COLUMN_PAYMENT_TYPE + " int "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String SQL_CLEAN_TABLE =
            "DELETE FROM " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_ID_SERVER, COLUMN_SHEET_ID, COLUMN_DATE, COLUMN_DETAIL, COLUMN_GROUP, COLUMN_AMOUNT,
            COLUMN_EXPENSE_TYPE, COLUMN_PAYMENT_TYPE};


    private ValueEventListener valueEventListener;


    public ExpenseDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.executeQuery(DATABASE_CREATE);
    }

    public void dropTable(){
        databaseHelper.executeQuery(SQL_DELETE_ENTRIES);
    }

    public void cleanTable(){
        databaseHelper.executeQuery(SQL_CLEAN_TABLE);
    }

    public boolean createExpenseEntry(Expense expense){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, expense.getId());
        values.put(COLUMN_SHEET_ID, expense.getSheet_id());
        values.put(COLUMN_ID_SERVER, expense.getServer_expense_id());
        values.put(COLUMN_DATE, expense.getExpense_date());
        values.put(COLUMN_DETAIL, expense.getExpense_detail());
        values.put(COLUMN_GROUP, expense.getExpense_group());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
        values.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());

        long insertId = database.insert(TABLE_NAME, null,
                values);
        database.close();
        return insertId != -1 ;
    }



    public int createExpenseEntry(ArrayList<Expense> expenseArrayList){
        int count = 0;
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        for(Expense expense : expenseArrayList) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SHEET_ID, expense.getSheet_id());
            values.put(COLUMN_DATE, expense.getExpense_date());
            values.put(COLUMN_DETAIL, expense.getExpense_detail());
            values.put(COLUMN_GROUP, expense.getExpense_group());
            values.put(COLUMN_AMOUNT, expense.getAmount());
            values.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
            values.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());
            long insertId = database.insert(TABLE_NAME, null,
                    values);
            count += insertId != -1?1:0;
        }
        database.close();
        return count;
    }

    public boolean updateExpenseEntry(Expense expense){
        Log.i("Updating database", expense.toString());

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, expense.getId());
        values.put(COLUMN_SHEET_ID, expense.getSheet_id());
        values.put(COLUMN_ID_SERVER, expense.getServer_expense_id());
        values.put(COLUMN_DATE, expense.getExpense_date());
        values.put(COLUMN_DETAIL, expense.getExpense_detail());
        values.put(COLUMN_GROUP, expense.getExpense_group());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
        values.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());

        long count = database.update(TABLE_NAME,
                values, COLUMN_ID + " = " + DatabaseUtils.sqlEscapeString(expense.getId()), null);
        database.close();

        return (count > 0);
    }

    public boolean updateSheetEntryWithServerID(Expense expense){
        Log.i("Updating database", expense.toString());


        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, expense.getId());
        values.put(COLUMN_SHEET_ID, expense.getSheet_id());
//        values.put(COLUMN_NAME, expense.getServer_expense_id());
        values.put(COLUMN_DATE, expense.getExpense_date());
        values.put(COLUMN_DETAIL, expense.getExpense_detail());
        values.put(COLUMN_GROUP, expense.getExpense_group());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
        values.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());

        long count = 0;
        try {
            database.update(TABLE_NAME,
                    values, COLUMN_ID_SERVER + " = " + DatabaseUtils.sqlEscapeString(expense.getServer_expense_id()), null);
        }catch (SQLException exp){
            exp.printStackTrace();
        }
        database.close();

        return count > 0;
    }

    public ArrayList<Expense> getExpenseItems(String sheet_id){
        ArrayList<Expense> expenseArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_SHEET_ID + " = " + DatabaseUtils.sqlEscapeString(sheet_id), null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            expenseArrayList.add(new Expense(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return expenseArrayList;
    }

    public void getExpenseItemsFromServer(final long sheet_id, final boolean oneTimeFetch, final ServerExpenseDataListener listener){
        removeSync(sheet_id);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference(TABLE_NAME);
        /*valueEventListener = reference.child(sheet_id+"").addValueEventListener(new ValueEventListener() {
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
                    reference.child(sheet_id + "").removeEventListener(this);
                }
                listener.onServerDataRetrieved(expenseArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void removeSync(long sheet_id){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(TABLE_NAME);
        if(valueEventListener!=null) {
            reference.child(sheet_id + "").removeEventListener(valueEventListener);
        }

    }

    public ArrayList<Expense> getExpenseItems(){
        ArrayList<Expense> expenseArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            expenseArrayList.add(new Expense(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return expenseArrayList;
    }

    public long getTotalCost(String sheet_id){
        long amount = 0;
        String query = "SELECT SUM("+COLUMN_AMOUNT+") FROM "+TABLE_NAME + " WHERE " + COLUMN_SHEET_ID + " = '" + sheet_id +"'";
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            amount = cursor.getLong(0);
        }
        database.close();
        cursor.close();
        return amount;
    }

    public boolean removeExpenseEntry(Expense expense){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        database.close();

        return count > 0;
    }

    public boolean removeExpenseEntry(ArrayList<Expense> expense_list){
        long count = 0;
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        for(Expense expense: expense_list){
            count += database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        }
        database.close();
        return count > 0;
    }

    public boolean isExpenseEntryExist(Expense expense){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + DatabaseUtils.sqlEscapeString(expense.getId()), null,
                null, null, null);

        boolean exist = cursor.getCount()>0;

        database.close();
        cursor.close();
        return exist;
    }
}
