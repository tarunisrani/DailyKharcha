package com.tarunisrani.dailykharcha.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.utils.AppConstant;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseAddItemActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText expense_add_item_date;
    private EditText expense_add_item_detail;
    private EditText expense_add_item_amount;
    private Spinner expense_add_item_expense_type_spinner;
    private Spinner expense_add_item_payment_type_spinner;
    private TextView expense_add_item_submit;
    private TextView expense_add_item_reset;
    private TextView expense_add_item_edit;

    private ArrayList<String> payment_type_list;
    private ArrayList<String> expense_type_list;

    private String selected_expense_type = "";
    private String selected_payment_type = "";

    public static final int OPEN_MODE_EDIT = 1;
    public static final int OPEN_MODE_NEW = 2;

    private int open_mode = OPEN_MODE_NEW;

    private String expense_id = null;
    private String server_expense_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_add_item_layout);

        expense_add_item_date = (EditText) findViewById(R.id.expense_add_item_date);
        expense_add_item_detail = (EditText) findViewById(R.id.expense_add_item_detail);
        expense_add_item_amount = (EditText) findViewById(R.id.expense_add_item_amount);
        expense_add_item_expense_type_spinner = (Spinner) findViewById(R.id.expense_add_item_expense_type_spinner);
        expense_add_item_payment_type_spinner = (Spinner) findViewById(R.id.expense_add_item_payment_type_spinner);
        expense_add_item_submit = (TextView) findViewById(R.id.expense_add_item_submit);
        expense_add_item_reset = (TextView) findViewById(R.id.expense_add_item_reset);
        expense_add_item_edit = (TextView) findViewById(R.id.expense_add_item_edit);

        expense_type_list = new ArrayList<>();
        expense_type_list.add("Grocery");
        expense_type_list.add("Vegetables");
        expense_type_list.add("Household");
        expense_type_list.add("Daily Needs");
        expense_type_list.add("Party");
        expense_type_list.add("Gift");
        expense_type_list.add("Miscellaneous");

        ArrayAdapter<String> expense_type_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, expense_type_list);

        expense_add_item_expense_type_spinner.setAdapter(expense_type_adapter);
        expense_add_item_expense_type_spinner.setOnItemSelectedListener(this);

        payment_type_list = new ArrayList<>();
        payment_type_list.add("Cash");
        payment_type_list.add("Card");
        payment_type_list.add("Netbanking");

        ArrayAdapter<String> payment_type_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, payment_type_list);

        expense_add_item_payment_type_spinner.setAdapter(payment_type_adapter);
        expense_add_item_payment_type_spinner.setOnItemSelectedListener(this);


        Intent intent = getIntent();
        if(intent!=null){
            Expense expense = intent.getParcelableExtra(AppConstant.INTENT_KEY_EXPENSE);
            boolean editable = intent.getBooleanExtra(AppConstant.INTENT_KEY_EDITABLE, false);
            processIntent(expense);
            if(editable){
                performEditOperation();
            }
        }

        expense_add_item_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    showDatePicker();
                }
            }
        });

        expense_add_item_submit.setOnClickListener(this);
        expense_add_item_edit.setOnClickListener(this);
        expense_add_item_date.setOnClickListener(this);
        expense_add_item_reset.setOnClickListener(this);
    }

    private void processIntent(Expense expense){
        if(expense!=null) {

            expense_id = expense.getId();
            server_expense_id = expense.getServer_expense_id();

            expense_add_item_date.setText(expense.getExpense_date());
            expense_add_item_date.setEnabled(false);
            expense_add_item_detail.setText(expense.getExpense_detail());
            expense_add_item_detail.setEnabled(false);
            expense_add_item_amount.setText(String.valueOf(expense.getAmount()));
            expense_add_item_amount.setEnabled(false);
            expense_add_item_expense_type_spinner.setSelection(expense_type_list.indexOf(expense.getExpense_type()));
            expense_add_item_expense_type_spinner.setEnabled(false);
            expense_add_item_payment_type_spinner.setSelection(payment_type_list.indexOf(expense.getPayment_type()));
            expense_add_item_payment_type_spinner.setEnabled(false);

            expense_add_item_submit.setVisibility(View.GONE);
            expense_add_item_reset.setVisibility(View.GONE);
            expense_add_item_edit.setVisibility(View.VISIBLE);
        }

    }

    private void showDatePicker() {

        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        final String prevDate = expense_add_item_date.getText().toString();

        Dialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int dob_year, int dob_monthOfYear, int dob_dayOfMonth) {
                expense_add_item_date.setText(String.format("%02d/%02d/%d", dob_dayOfMonth, dob_monthOfYear + 1, dob_year));
            }
        }, year, month, day);
        d.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                expense_add_item_date.setText(prevDate);
            }
        });

        d.setCancelable(true);
        d.show();
    }

    private void performSubmitOperation(){
        String date = expense_add_item_date.getText().toString();
        String detail = expense_add_item_detail.getText().toString();
        String amount = expense_add_item_amount.getText().toString();

        if(!date.isEmpty() && !detail.isEmpty() && !amount.isEmpty()) {

            Intent intent = new Intent();

            Expense expense = new Expense();
            Log.e("expense_id: ", ""+expense_id);
            expense.setId(expense_id);
            expense.setExpense_date(date);
            expense.setExpense_detail(detail);
            expense.setServer_expense_id(server_expense_id);
            try {
                expense.setAmount(Double.parseDouble(amount));
            } catch(NumberFormatException exp){
                expense.setAmount(0);
            }
            expense.setExpense_type(selected_expense_type);
            expense.setPayment_type(selected_payment_type);
            intent.putExtra(AppConstant.INTENT_KEY_EXPENSE, expense);
            setResult(200, intent);
            finish();
        }
    }

    private void performEditOperation(){
        expense_add_item_submit.setVisibility(View.VISIBLE);
        expense_add_item_reset.setVisibility(View.VISIBLE);
        expense_add_item_edit.setVisibility(View.GONE);

        expense_add_item_date.setEnabled(true);
        expense_add_item_detail.setEnabled(true);
        expense_add_item_amount.setEnabled(true);
        expense_add_item_expense_type_spinner.setEnabled(true);
        expense_add_item_payment_type_spinner.setEnabled(true);


        open_mode = OPEN_MODE_EDIT;
    }

    private void performResetOperation(){
        expense_add_item_date.setText("");
        expense_add_item_detail.setText("");
        expense_add_item_amount.setText("");
        expense_add_item_expense_type_spinner.setSelection(0);
        expense_add_item_payment_type_spinner.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expense_add_item_submit:
                performSubmitOperation();
                break;
            case R.id.expense_add_item_edit:
                performEditOperation();
                break;
            case R.id.expense_add_item_reset:
                performResetOperation();
                break;
            case R.id.expense_add_item_date:
                showDatePicker();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected_item = (String)parent.getSelectedItem();
        switch (parent.getId()){
            case R.id.expense_add_item_expense_type_spinner:
                selected_expense_type = selected_item;
                break;
            case R.id.expense_add_item_payment_type_spinner:
                selected_payment_type = selected_item;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
