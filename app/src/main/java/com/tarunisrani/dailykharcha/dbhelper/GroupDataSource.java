package com.tarunisrani.dailykharcha.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.GroupShare;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class GroupDataSource {

    public static final String TABLE_NAME = "groups";

    public static final String COLUMN_ID = "group_id";
    public static final String COLUMN_SERVER_ID = "group_server_id";
    public static final String COLUMN_NAME = "group_name";
    public static final String COLUMN_OWNER_ID = "group_owner_id";
    public static final String COLUMN_OWNER_NAME = "group_owner_name";
    public static final String COLUMN_IS_SHARED = "group_is_shared";

    private DatabaseHelper databaseHelper;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + "( "
            + COLUMN_ID + " text primary key, "
            + COLUMN_SERVER_ID + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_OWNER_ID + " text, "
            + COLUMN_OWNER_NAME + " text, "
            + COLUMN_IS_SHARED + " INTEGER DEFAULT 0 "
            +");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String SQL_CLEAN_TABLE =
            "DELETE FROM " + TABLE_NAME;

    private String[] allColumns = {COLUMN_ID, COLUMN_SERVER_ID, COLUMN_NAME, COLUMN_OWNER_ID, COLUMN_OWNER_NAME, COLUMN_IS_SHARED};

    private Context mContext;

    public GroupDataSource(Context context) {
        mContext = context;
        databaseHelper = DatabaseHelper.getmInstance(context);
        databaseHelper.executeQuery(DATABASE_CREATE);
    }

    public void dropTable(){
        databaseHelper.executeQuery(SQL_DELETE_ENTRIES);
    }

    public void cleanTable(){
        databaseHelper.executeQuery(SQL_CLEAN_TABLE);
    }

    public boolean createGroupEntry(Group group){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, group.getGroup_id());
        values.put(COLUMN_SERVER_ID, group.getServerid());
        values.put(COLUMN_NAME, group.getGroup_name());
        values.put(COLUMN_OWNER_ID, group.getOwner_id());
        values.put(COLUMN_OWNER_NAME, group.getOwner_name());
        values.put(COLUMN_IS_SHARED, group.isShared());

        long insertId = database.insert(TABLE_NAME, null,
                values);
        database.close();
        return insertId != -1 ;
    }


    public boolean updateGroupEntry(Group group){
        Log.i("Updating database", group.toString());

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, group.getGroup_id());
        values.put(COLUMN_SERVER_ID, group.getServerid());
        values.put(COLUMN_NAME, group.getGroup_name());
        values.put(COLUMN_OWNER_ID, group.getOwner_id());
        values.put(COLUMN_OWNER_NAME, group.getOwner_name());
        values.put(COLUMN_IS_SHARED, group.isShared());

        long count = database.update(TABLE_NAME,
                values, COLUMN_ID + " = " + DatabaseUtils.sqlEscapeString(group.getGroup_id()), null);
        database.close();


        GroupShareDataSource groupShareDataSource = new GroupShareDataSource(mContext);
        ArrayList<String> userlist = group.getSharedwith();
        for(String user: userlist){
            GroupShare groupShare = new GroupShare();
            groupShare.setGroup_id(group.getGroup_id());
            groupShare.setUser_id(user);
            groupShareDataSource.createGroupShareEntry(groupShare);
        }

        return (count > 0);
    }

    public ArrayList<Group> getGroupItems(){
        ArrayList<Group> expenseArrayList = new ArrayList<>();
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, null, null,
                null, null, null);
        cursor.moveToFirst();
        GroupShareDataSource groupShareDataSource = new GroupShareDataSource(mContext);
        while(!cursor.isAfterLast()){
            Group group = new Group(cursor);
            ArrayList<GroupShare> groupShareArrayList = groupShareDataSource.getGroupShareItems(group.getGroup_id());
            group.setSharedwith(generateIdList(groupShareArrayList));
            expenseArrayList.add(group);
            cursor.moveToNext();
        }
        database.close();
        cursor.close();
        return expenseArrayList;
    }

    public Group getGroup(String group_id){

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + DatabaseUtils.sqlEscapeString(group_id), null,
                null, null, null);
        cursor.moveToFirst();

        Group group = new Group(cursor);
        cursor.moveToNext();

        GroupShareDataSource groupShareDataSource = new GroupShareDataSource(mContext);
        ArrayList<GroupShare> groupShareArrayList = groupShareDataSource.getGroupShareItems(group_id);
        group.setSharedwith(generateIdList(groupShareArrayList));

        database.close();
        cursor.close();
        return group;
    }

    private ArrayList<String> generateIdList(ArrayList<GroupShare> list){
        ArrayList<String> idlist = new ArrayList<>();
        for (GroupShare groupShare: list){
            idlist.add(groupShare.getUser_id());
        }
        return idlist;
    }

    public boolean removeGroupEntry(Group group){
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long count = database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(group.getGroup_id())});
        database.close();

        return count > 0;
    }

    public boolean isGroupEntryExist(Group group){
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                allColumns, COLUMN_ID + " = " + DatabaseUtils.sqlEscapeString(group.getGroup_id()), null,
                null, null, null);

        boolean exist = cursor.getCount()>0;

        database.close();
        cursor.close();
        return exist;
    }
}
