package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Group implements Parcelable{


    protected Group(Parcel in) {
        group_id = in.readString();
        group_name = in.readString();
        owner_id = in.readString();
        owner_name = in.readString();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

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

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    private String group_id;
    private String group_name;
    private String owner_id;
    private String owner_name;

    public Group(){
    }

    public Group(Cursor cursor){
        this.group_id = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_ID));
        this.group_name = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_NAME));
        this.owner_id = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_OWNER_ID));
        this.owner_name = cursor.getString(cursor.getColumnIndex(GroupDataSource.COLUMN_OWNER_NAME));
    }

    public void updateExpense(Group expense){
        this.group_id = expense.getGroup_id();
        this.group_name = expense.getGroup_name();
        this.owner_id = expense.getOwner_id();
        this.owner_name = expense.getOwner_name();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(group_id);
        dest.writeString(group_name);
        dest.writeString(owner_id);
        dest.writeString(owner_name);
    }
}
