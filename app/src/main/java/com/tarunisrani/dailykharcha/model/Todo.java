package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;

import com.tarunisrani.dailykharcha.dbhelper.TodoDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Todo{

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDate_completion() {
        return date_completion;
    }

    public void setDate_completion(String date_completion) {
        this.date_completion = date_completion;
    }

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

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getTask_group() {
        return task_group;
    }

    public void setTask_group(String task_group) {
        this.task_group = task_group;
    }

    public String getTask_heading() {
        return task_heading;
    }

    public void setTask_heading(String task_heading) {
        this.task_heading = task_heading;
    }

    private String task_heading;
    private String task_description;
    private String date_creation;
    private String date_completion;
    private String task_group;
    private boolean completed;
    private int priority;

    public Todo(){

    }

    public Todo(Cursor cursor){
        this.task_heading = cursor.getString(cursor.getColumnIndex(TodoDataSource.COLUMN_HEADING));
        this.task_description = cursor.getString(cursor.getColumnIndex(TodoDataSource.COLUMN_DISCRIPTION));
        this.date_creation = cursor.getString(cursor.getColumnIndex(TodoDataSource.COLUMN_DATE_CREATION));
        this.date_completion = cursor.getString(cursor.getColumnIndex(TodoDataSource.COLUMN_DATE_COMPLETE));
        this.task_group = cursor.getString(cursor.getColumnIndex(TodoDataSource.COLUMN_GROUP));
        this.completed = cursor.getInt(cursor.getColumnIndex(TodoDataSource.COLUMN_COMPLETED)) == 1;
        this.priority = cursor.getInt(cursor.getColumnIndex(TodoDataSource.COLUMN_PRIORITY));
    }
}
