package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tarunisrani.dailykharcha.model.Todo;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class TodoDataSource {

    public static final String TABLE_NAME = "todo";

    public static final String COLUMN_ID = "todo_id";
    public static final String COLUMN_HEADING = "todo_heading";
    public static final String COLUMN_DISCRIPTION = "todo_discription";
    public static final String COLUMN_DATE_CREATION = "todo_date_creation";
    public static final String COLUMN_DATE_COMPLETE = "todo_date_complete";
    public static final String COLUMN_GROUP = "todo_group";
    public static final String COLUMN_COMPLETED = "todo_completed";
    public static final String COLUMN_PRIORITY = "todo_priority";

    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_HEADING + " text not null, "
            + COLUMN_DISCRIPTION + " text, "
            + COLUMN_DATE_CREATION + " text, "
            + COLUMN_DATE_COMPLETE + " text, "
            + COLUMN_GROUP + " text, "
            + COLUMN_COMPLETED + " int, "
            + COLUMN_PRIORITY + " int "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String SQL_CLEAN_TABLE =
            "DELETE FROM " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_HEADING, COLUMN_DATE_CREATION, COLUMN_DATE_COMPLETE,
            COLUMN_DISCRIPTION, COLUMN_GROUP, COLUMN_COMPLETED, COLUMN_PRIORITY};



    public TodoDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.executeQuery(DATABASE_CREATE);
    }

    public void dropTable(){
        databaseHelper.executeQuery(SQL_DELETE_ENTRIES);
    }

    public void cleanTable(){
        databaseHelper.executeQuery(SQL_CLEAN_TABLE);
    }

    public int createTodoEntry(Todo todo) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADING, todo.getTask_heading());
        values.put(COLUMN_DATE_CREATION, todo.getDate_creation());
        values.put(COLUMN_DATE_COMPLETE, todo.getDate_completion());
        values.put(COLUMN_DISCRIPTION, todo.getTask_description());
        values.put(COLUMN_GROUP, todo.getTask_group());
        values.put(COLUMN_COMPLETED, todo.isCompleted()?1:0);
        values.put(COLUMN_PRIORITY, todo.getPriority());
        long insertId = database.insert(TABLE_NAME, null,
                values);
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + insertId, null,
                null, null, null);
        int todo_id = -1;
        if (cursor.moveToFirst()){
            todo_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        database.close();
        return todo_id;
    }

    public ArrayList<Todo> getTodoList(){
        ArrayList<Todo> companyArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            companyArrayList.add(new Todo(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        database.close();
        return companyArrayList;
    }
}
