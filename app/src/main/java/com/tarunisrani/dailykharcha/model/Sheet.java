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

    public long getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(long sheet_id) {
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
    private long sheet_id;
    private double amount;

    public Sheet(){

    }

    public Sheet(Cursor cursor){
        this.sheet_id = cursor.getLong(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_ID));
        this.sheet_name = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_NAME));
        this.sheet_creation_date = cursor.getString(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_DATE));
        this.amount = cursor.getDouble(cursor.getColumnIndex(ExpenseSheetDataSource.COLUMN_AMOUNT));
    }

    protected Sheet(Parcel in) {
        sheet_name = in.readString();
        sheet_creation_date = in.readString();
        sheet_id = in.readLong();
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
        dest.writeString(sheet_creation_date);
        dest.writeLong(sheet_id);
        dest.writeDouble(amount);
    }
}
