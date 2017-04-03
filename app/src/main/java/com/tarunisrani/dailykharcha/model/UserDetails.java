package com.tarunisrani.dailykharcha.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class UserDetails implements Parcelable{

    public UserDetails(){

    }

    protected UserDetails(Parcel in) {
        user_name = in.readString();
        user_email = in.readString();
        uid = in.readString();
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    private String user_name;
    private String user_email;
    private String uid;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_name);
        dest.writeString(user_email);
        dest.writeString(uid);
    }

    @Override
    public String toString() {
        return user_name;
    }

    public UserDetails clone(){
        UserDetails userDetails = new UserDetails();
        userDetails.setUid(uid);
        userDetails.setUser_email(user_email);
        userDetails.setUser_name(user_name);
        return userDetails;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof UserDetails){
            return ((UserDetails) o).getUid().equals(uid);
        }
        return false;
    }
}
