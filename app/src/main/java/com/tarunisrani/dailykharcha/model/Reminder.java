package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;

import com.tarunisrani.dailykharcha.dbhelper.ReminderDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Reminder {

    public String getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(String date_creation) {
        this.date_creation = date_creation;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getReminder_date() {
        return reminder_date;
    }

    public void setReminder_date(String reminder_date) {
        this.reminder_date = reminder_date;
    }

    public String getReminder_description() {
        return reminder_description;
    }

    public void setReminder_description(String reminder_description) {
        this.reminder_description = reminder_description;
    }

    public int getReminder_frequency() {
        return reminder_frequency;
    }

    public void setReminder_frequency(int reminder_frequency) {
        this.reminder_frequency = reminder_frequency;
    }

    public String getReminder_heading() {
        return reminder_heading;
    }

    public void setReminder_heading(String reminder_heading) {
        this.reminder_heading = reminder_heading;
    }

    private String reminder_heading;
    private String reminder_description;
    private String date_creation;
    private String reminder_date;
    private int reminder_frequency;
    private int priority;

    public Reminder(){

    }

    public Reminder(Cursor cursor){
        this.reminder_heading = cursor.getString(cursor.getColumnIndex(ReminderDataSource.COLUMN_HEADING));
        this.reminder_description = cursor.getString(cursor.getColumnIndex(ReminderDataSource.COLUMN_DESCRIPTION));
        this.date_creation = cursor.getString(cursor.getColumnIndex(ReminderDataSource.COLUMN_DATE_CREATION));
        this.reminder_date = cursor.getString(cursor.getColumnIndex(ReminderDataSource.COLUMN_DATE_COMPLETION));
        this.reminder_frequency = cursor.getInt(cursor.getColumnIndex(ReminderDataSource.COLUMN_FREQUENCY));
        this.priority = cursor.getInt(cursor.getColumnIndex(ReminderDataSource.COLUMN_PRIORITY));
    }
}
