package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tarunisrani.dailykharcha.model.Expense;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class ExpenseDataSource {

    public static final String TABLE_NAME = "expense";

    public static final String COLUMN_ID = "expense_id";
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
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SHEET_ID + " integer, "
            + COLUMN_DATE + " text, "
            + COLUMN_DETAIL + " text, "
            + COLUMN_GROUP + " text, "
            + COLUMN_AMOUNT + " float, "
            + COLUMN_EXPENSE_TYPE + " int, "
            + COLUMN_PAYMENT_TYPE + " int "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_SHEET_ID, COLUMN_DATE, COLUMN_DETAIL, COLUMN_GROUP, COLUMN_AMOUNT,
            COLUMN_EXPENSE_TYPE, COLUMN_PAYMENT_TYPE};



    public ExpenseDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.createTable(DATABASE_CREATE);
    }

    public boolean createExpenseEntry(Expense expense){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
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
        database.close();
        return insertId != -1;
    }

    public void createExpenseEntryOnServer(Expense expense) throws JSONException{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(TABLE_NAME);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(COLUMN_SHEET_ID, expense.getSheet_id());
        jsonObject.put(COLUMN_DATE, expense.getExpense_date());
        jsonObject.put(COLUMN_DETAIL, expense.getExpense_detail());
        jsonObject.put(COLUMN_GROUP, expense.getExpense_group());
        jsonObject.put(COLUMN_AMOUNT, expense.getAmount());
        jsonObject.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
        jsonObject.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());

        reference.push().setValue(jsonObject.toString());
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

    public boolean updateSheetEntry(Expense expense){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHEET_ID, expense.getSheet_id());
        values.put(COLUMN_DATE, expense.getExpense_date());
        values.put(COLUMN_DETAIL, expense.getExpense_detail());
        values.put(COLUMN_GROUP, expense.getExpense_group());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_EXPENSE_TYPE, expense.getExpense_type());
        values.put(COLUMN_PAYMENT_TYPE, expense.getPayment_type());

        long count = database.update(TABLE_NAME,
                values, COLUMN_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        database.close();

        return count == 1;
    }

    public ArrayList<Expense> getExpenseItems(long sheet_id){
        ArrayList<Expense> expenseArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_SHEET_ID + " = " + sheet_id, null,
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

    public long getTotalCost(long sheet_id){
        long amount = 0;
        String query = "SELECT SUM("+COLUMN_AMOUNT+") FROM "+TABLE_NAME + " WHERE " + COLUMN_SHEET_ID + " = " + sheet_id;
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
}
