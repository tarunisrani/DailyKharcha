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
        server_expense_id = in.readString();
        id = in.readString();
        sheet_id = in.readString();
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

    public String getSheet_id() {
        return sheet_id;
    }

    public void setSheet_id(String sheet_id) {
        this.sheet_id = sheet_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServer_expense_id() {
        return server_expense_id;
    }

    public void setServer_expense_id(String server_expense_id) {
        this.server_expense_id = server_expense_id;
//        this.server_expense_id = this.server_expense_id.replaceAll("-", "\\-");
    }

    private String expense_date;
    private String expense_detail;
    private String expense_group = "";
    private String expense_type;
    private String id;
    private String sheet_id;
    private String server_expense_id;
    private String payment_type;
    private double amount;

    public Expense(){

    }

    public Expense(Cursor cursor){
        this.id = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_ID));
        this.server_expense_id = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_ID_SERVER));
        this.sheet_id = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_SHEET_ID));
        this.expense_date = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_DATE));
        this.expense_detail = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_DETAIL));
        this.expense_group = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_GROUP));
        this.expense_type = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_EXPENSE_TYPE));
        this.payment_type = cursor.getString(cursor.getColumnIndex(ExpenseDataSource.COLUMN_PAYMENT_TYPE));
        this.amount = cursor.getDouble(cursor.getColumnIndex(ExpenseDataSource.COLUMN_AMOUNT));
    }

    public void updateExpense(Expense expense){
        this.id = expense.getId();
        this.server_expense_id = expense.getServer_expense_id();
        this.sheet_id = expense.getSheet_id();
        this.expense_date = expense.getExpense_date();
        this.expense_detail = expense.getExpense_detail();
        this.expense_group = expense.getExpense_group();
        this.expense_type = expense.getExpense_type();
        this.payment_type = expense.getPayment_type();
        this.amount = expense.getAmount();
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
        dest.writeString(server_expense_id);
        dest.writeString(id);
        dest.writeString(sheet_id);
        dest.writeDouble(amount);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Expense){
            if(((Expense)o).getServer_expense_id()!=null && this.server_expense_id!=null) {
                return ((Expense) o).getServer_expense_id().equalsIgnoreCase(this.server_expense_id);
            }else{
                return ((Expense) o).getId() == this.id;
            }
        }
        return super.equals(o);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("id = "+this.id +"\t");
        builder.append("server_expense_id = "+this.server_expense_id +"\t");
        builder.append("sheet_id = "+this.sheet_id +"\t");
        builder.append("expense_date = "+this.expense_date +"\t");
        builder.append("expense_detail = "+this.expense_detail +"\t");
        builder.append("expense_group = "+this.expense_group +"\t");
        builder.append("expense_type = "+this.expense_type +"\t");
        builder.append("payment_type = "+this.payment_type +"\t");
        builder.append("amount = "+this.amount +"\t");

        return builder.toString();
    }
}
