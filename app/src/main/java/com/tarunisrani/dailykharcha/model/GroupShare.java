package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.tarunisrani.dailykharcha.dbhelper.GroupShareDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class GroupShare implements Parcelable{

    protected GroupShare(Parcel in) {
        group_id = in.readString();
        user_id = in.readString();
    }

    public static final Creator<GroupShare> CREATOR = new Creator<GroupShare>() {
        @Override
        public GroupShare createFromParcel(Parcel in) {
            return new GroupShare(in);
        }

        @Override
        public GroupShare[] newArray(int size) {
            return new GroupShare[size];
        }
    };

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    private String group_id;
    private String user_id;

    public GroupShare(){
    }

    public GroupShare(Cursor cursor){
        this.group_id = cursor.getString(cursor.getColumnIndex(GroupShareDataSource.COLUMN_GROUP_ID));
        this.user_id = cursor.getString(cursor.getColumnIndex(GroupShareDataSource.COLUMN_USER_ID));
    }

    public void updateExpense(GroupShare expense){
        this.group_id = expense.getGroup_id();
        this.user_id = expense.getUser_id();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(group_id);
        dest.writeString(user_id);
    }
}
