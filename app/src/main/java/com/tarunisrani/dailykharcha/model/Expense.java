package com.tarunisrani.dailykharcha.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Expense implements Parcelable {

    protected Expense(Parcel in) {
        expense_date = in.readString();
        expense_detail = in.readString();
        expense_group = in.readString();
        expense_type = in.readString();
        payment_type = in.readString();
        id = in.readInt();
        sheet_id = in.readLong();
        amount = in.readDouble();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getExpense_date() {
        return expense_date;
    }

    public void setExpense_date(String expense_date) {
        this.expense_date = expense_date;
    }

    public String getExpense_detail() {
        return expense_detail;
    }

    public String getExpense_group() {
        return expense_group;
    }

    public void setExpense_group(String expense_group) {
        this.expense_group = expense_group;
    }

    public void setExpense_detail(String expense_detail) {
        this.expense_detail = expense_detail;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public void setExpense_type(String expense_type) {
        this.expense_type = expense_type;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public long getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(long sheet_id) {
        this.sheet_id = sheet_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String expense_date;
    private String expense_detail;
    private String expense_group;
    private String expense_type;
    private int id;
    private long sheet_id;
    private String payment_type;
    private double amount;

    public Expense(){

    }

    public Expense(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(ExpenseDataSource.COLUMN_ID));
        this.sheet_id = cursor.getLong(cursor.getColumnIndex(ExpenseDataSource.COLUMN_SHEET_ID));
        this.expense_date = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_DATE));
        this.expense_detail = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_DETAIL));
        this.expense_group = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_DETAIL));
        this.expense_type = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_EXPENSE_TYPE));
        this.payment_type = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_PAYMENT_TYPE));
        this.amount = cursor.getDouble(cursor.getColumnIndex(ExpenseDataSource.COLUMN_AMOUNT));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(expense_date);
        dest.writeString(expense_detail);
        dest.writeString(expense_group);
        dest.writeString(expense_type);
        dest.writeString(payment_type);
        dest.writeInt(id);
        dest.writeLong(sheet_id);
        dest.writeDouble(amount);
    }
}
