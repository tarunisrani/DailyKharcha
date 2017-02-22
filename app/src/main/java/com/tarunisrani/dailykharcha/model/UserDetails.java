package com.tarunisrani.dailykharcha.model;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class UserDetails {

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

}
