package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Sheet implements Parcelable {

    public String getSheet_name() {
        return sheet_name;
    }

    public void setSheet_name(String sheet_name) {
        this.sheet_name = sheet_name;
    }

    public String getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(String sheet_id) {
        this.sheet_id = sheet_id;
    }

    public String getSheet_creation_date() {
        return sheet_creation_date;
    }

    public void setSheet_creation_date(String sheet_creation_date) {
        this.sheet_creation_date = sheet_creation_date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private String sheet_name;
    private String sheet_creation_date;

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    private String server_id;
    private String sheet_id;
    private String group_id;
    private double amount;

    public Sheet(){

    }

    public Sheet(Cursor cursor){
        this.sheet_id = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_ID));
        this.server_id = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_ID_SERVER));
        this.group_id = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_GROUP));
        this.sheet_name = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_NAME));
        this.sheet_creation_date = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_DATE));
        this.amount = cursor.getDouble(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_AMOUNT));
    }

    public void updateSheet(Sheet sheet){
        this.sheet_id = sheet.getSheet_id();
        this.server_id = sheet.getServer_id();
        this.group_id = sheet.getGroup_id();
        this.sheet_name = sheet.getSheet_name();
        this.sheet_creation_date = sheet.getSheet_creation_date();
        this.amount = sheet.getAmount();
    }

    protected Sheet(Parcel in) {
        sheet_name = in.readString();
        server_id = in.readString();
        group_id = in.readString();
        sheet_creation_date = in.readString();
        sheet_id = in.readString();
        amount = in.readDouble();
    }

    public static final Creator<Sheet> CREATOR = new Creator<Sheet>() {
        @Override
        public Sheet createFromParcel(Parcel in) {
            return new Sheet(in);
        }

        @Override
        public Sheet[] newArray(int size) {
            return new Sheet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sheet_name);
        dest.writeString(server_id);
        dest.writeString(group_id);
        dest.writeString(sheet_creation_date);
        dest.writeString(sheet_id);
        dest.writeDouble(amount);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(sheet_id);
        stringBuilder.append("\t");
        stringBuilder.append(server_id);
        stringBuilder.append("\t");
        stringBuilder.append(group_id);
        stringBuilder.append("\t");
        stringBuilder.append(sheet_name);
        stringBuilder.append("\t");
        stringBuilder.append(sheet_creation_date);
        stringBuilder.append("\t");
        stringBuilder.append(amount);

        return stringBuilder.toString();
    }
}
