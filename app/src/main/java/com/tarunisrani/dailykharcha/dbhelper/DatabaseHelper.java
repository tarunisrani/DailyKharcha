package com.tarunisrani.dailykharcha.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tarunisrani on 12/21/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dailykharcha.db";
    private static final int DATABASE_VERSION = 1;

    public static DatabaseHelper mInstance;

    public static DatabaseHelper getmInstance(Context context){
        if(mInstance == null){
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void createTable(String sql_query){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql_query);
        database.close();
    }
}
