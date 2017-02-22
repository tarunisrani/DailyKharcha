package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;

import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Group {


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    private String group_id;
    private String group_name;
    private String owner_id;

    public Group(){
    }

    public Group(Cursor cursor){
        this.group_id = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_ID));
        this.group_name = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_NAME));
        this.owner_id = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_NAME));
    }

    public void updateExpense(Group expense){
        this.group_id = expense.getGroup_id();
        this.group_name = expense.getGroup_name();
        this.owner_id = expense.getOwner_id();
    }


}
