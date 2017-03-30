package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.database.ValueEventListener;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.GroupShare;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class GroupShareDataSource {

    public static final String TABLE_NAME = "group_share";

    public static final String COLUMN_GROUP_ID = "group_share_group_id";
    public static final String COLUMN_USER_ID = "group_share_user_id";


    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_GROUP_ID + " text, "
            + COLUMN_USER_ID + " text "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String SQL_CLEAN_TABLE =
            "DELETE FROM " + TABLE_NAME;

    private String[] allColumns = {COLUMN_GROUP_ID, COLUMN_USER_ID};


    private ValueEventListener valueEventListener;


    public GroupShareDataSource(Context context) {
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.executeQuery(DATABASE_CREATE);
    }

    public void dropTable(){
        databaseHelper.executeQuery(SQL_DELETE_ENTRIES);
    }

    public void cleanTable(){
        databaseHelper.executeQuery(SQL_CLEAN_TABLE);
    }

    public boolean createGroupShareEntry(GroupShare groupShare){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, groupShare.getGroup_id());
        values.put(COLUMN_USER_ID, groupShare.getUser_id());

        long insertId = database.insert(TABLE_NAME, null,
                values);
        database.close();
        return insertId != -1 ;
    }

    public boolean removeGroupShareEntry(GroupShare groupShare){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_GROUP_ID + " = " + DatabaseUtils.sqlEscapeString(groupShare.getGroup_id())
                        + " and " + COLUMN_USER_ID + " = " + DatabaseUtils.sqlEscapeString(groupShare.getUser_id()), null);
        database.close();
        return count > 0 ;
    }

    public boolean removeGroupShareEntry(String group_id){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_GROUP_ID + " = " + DatabaseUtils.sqlEscapeString(group_id), null);
        database.close();
        return count > 0 ;
    }

    public boolean createGroupShareEntry(GroupShare groupShare, SQLiteDatabase database){
//        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, groupShare.getGroup_id());
        values.put(COLUMN_USER_ID, groupShare.getUser_id());

        long insertId = database.insert(TABLE_NAME, null,
                values);
//        database.close();
        return insertId != -1 ;
    }

    public ArrayList<GroupShare> getGroupShareItems(String group_id){
        ArrayList<GroupShare> groupshareArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_GROUP_ID + " = " + DatabaseUtils.sqlEscapeString(group_id), null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            groupshareArrayList.add(new GroupShare(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return groupshareArrayList;
    }

    public ArrayList<Group> getGroupShareItems(){
        ArrayList<Group> groupshareArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            groupshareArrayList.add(new Group(cursor));
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return groupshareArrayList;
    }

    public boolean removeExpenseEntry(GroupShare groupShare){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_GROUP_ID + " = " + DatabaseUtils.sqlEscapeString(groupShare.getGroup_id())
                + " and " + COLUMN_USER_ID + " = " + DatabaseUtils.sqlEscapeString(groupShare.getUser_id()), null);
        database.close();

        return count > 0;
    }
}
