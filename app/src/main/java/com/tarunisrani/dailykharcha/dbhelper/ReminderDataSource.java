package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tarunisrani.dailykharcha.model.Reminder;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class ReminderDataSource {

    public static final String TABLE_NAME = "reminder";

    public static final String COLUMN_ID = "reminder_id";
    public static final String COLUMN_HEADING = "reminder_heading";
    public static final String COLUMN_DESCRIPTION = "reminder_description";
    public static final String COLUMN_DATE_CREATION = "reminder_date_creation";
    public static final String COLUMN_DATE_COMPLETION = "reminder_date_completion";
    public static final String COLUMN_FREQUENCY = "reminder_frequency";
    public static final String COLUMN_PRIORITY = "reminder_priority";

    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_HEADING + " text not null, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_DATE_CREATION + " text, "
            + COLUMN_DATE_COMPLETION + " text, "
            + COLUMN_FREQUENCY + " int, "
            + COLUMN_PRIORITY + " int "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String SQL_CLEAN_TABLE =
            "DELETE FROM " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_HEADING, COLUMN_DESCRIPTION,
            COLUMN_DATE_CREATION, COLUMN_DATE_COMPLETION, COLUMN_FREQUENCY, COLUMN_PRIORITY};



    public ReminderDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.executeQuery(DATABASE_CREATE);
    }

    public void dropTable(){
        databaseHelper.executeQuery(SQL_DELETE_ENTRIES);
    }

    public void cleanTable(){
        databaseHelper.executeQuery(SQL_CLEAN_TABLE);
    }

    public int createReminderEntry(Reminder reminder){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADING, reminder.getReminder_heading());
        values.put(COLUMN_DESCRIPTION, reminder.getReminder_description());
        values.put(COLUMN_DATE_CREATION, reminder.getDate_creation());
        values.put(COLUMN_DATE_COMPLETION, reminder.getReminder_date());
        values.put(COLUMN_FREQUENCY, reminder.getReminder_frequency());
        values.put(COLUMN_PRIORITY, reminder.getPriority());

        long insertId = database.insert(TABLE_NAME, null,
                values);
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        int customer_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        cursor.close();
        database.close();
        return customer_id;
    }

    public ArrayList<Reminder> getReminderList(){
        ArrayList<Reminder> customerArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            customerArrayList.add(new Reminder(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return customerArrayList;
    }
}
