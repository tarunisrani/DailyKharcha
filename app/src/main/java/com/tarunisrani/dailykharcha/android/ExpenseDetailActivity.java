package com.tarunisrani.dailykharcha.android;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.ExpenseListAdapter;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.listeners.ExpenseListClickListener;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.model.Sheet;
import com.tarunisrani.dailykharcha.utils.AppUtils;

import java.util.ArrayList;

public class ExpenseDetailActivity extends AppCompatActivity implements View.OnClickListener, ExpenseListClickListener {

    private ExpenseListAdapter expenseListAdapter;
    private Sheet sheet;
    private TextView total_amount_value;

    private double sheet_amount = 0;

    private ArrayList<Expense> prepopulated_expenses_list;
    private ArrayList<Expense> new_added_expenses_list = new ArrayList<>();


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Expense expense = intent.getParcelableExtra("EXPENSE");
            String action = intent.getStringExtra("ACTION");

            Log.e("Expense "+action, expense.toString());

            if(action.equalsIgnoreCase(BackendService.ACTION_ADDED)){
                fetchSheetDetail();
            }else if(action.equalsIgnoreCase(BackendService.ACTION_MODIFIED)){
                fetchSheetDetail();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_detail_layout);

        Intent intent = getIntent();
        if(intent != null){
            sheet = intent.getParcelableExtra("SHEET");
        }

        expenseListAdapter = new ExpenseListAdapter(this);

        ImageView button_add_expense_item = (ImageView) findViewById(R.id.button_add_expense_item);
        ImageView expense_detail_sync_button = (ImageView) findViewById(R.id.expense_detail_sync_button);
        ImageView button_submit_expense_sheet = (ImageView) findViewById(R.id.button_submit_expense_sheet);
        total_amount_value = (TextView) findViewById(R.id.total_amount_value);
        TextView expense_detail_sheet_name = (TextView) findViewById(R.id.expense_detail_sheet_name);
        RecyclerView expenses_sheet_add_item_view = (RecyclerView) findViewById(R.id.expenses_sheet_add_item_view);


        expense_detail_sheet_name.setText(sheet.getSheet_name());

        expenses_sheet_add_item_view.setAdapter(expenseListAdapter);

        expenses_sheet_add_item_view.setHasFixedSize(true);
        LinearLayoutManager linearLayout =new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        expenses_sheet_add_item_view.setLayoutManager(linearLayout);
        button_add_expense_item.setOnClickListener(this);
        expense_detail_sync_button.setOnClickListener(this);
        button_submit_expense_sheet.setOnClickListener(this);

        expenseListAdapter.setClickListener(this);

        fetchSheetDetail();
//        performSyncOperation(false);

    }

    private void fetchSheetDetail(){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        prepopulated_expenses_list = expenseDataSource.getExpenseItems(sheet.getSheet_id());

        /*for(Expense expense: prepopulated_expenses_list){
            Log.d("Expense Item", expense.toString());
        }*/

        expenseListAdapter.setExpenseList(prepopulated_expenses_list);
        expenseListAdapter.sortList();
        expenseListAdapter.notifyDataSetChanged();
        sheet_amount = expenseDataSource.getTotalCost(sheet.getSheet_id());
        total_amount_value.setText(String.valueOf(sheet_amount));
    }

    private void openAddExpenseItemScreen(){
        Intent intent = new Intent(this, ExpenseAddItemActivity.class);
        startActivityForResult(intent, 100);
    }

    private void openExpenseItemDetailScreen(Expense expense, boolean editable){
        Intent intent = new Intent(this, ExpenseAddItemActivity.class);
        intent.putExtra("EXPENSE", expense);
        intent.putExtra("EDITABLE", editable);
        startActivityForResult(intent, 100);
    }

    private void updateDbOperation(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        if(expenseDataSource.updateExpenseEntry(expense)){
//            updateExpenseSheet();
            /*try {
                expense.setId(insertId);
                expenseDataSource.updateExpenseEntryOnServer(expense);
            }catch (JSONException exp){
                exp.printStackTrace();
            }*/
        }else{
            Log.e("ExpenseDetail", "Error while submitting expense");
        }
    }

    private void createNewDbOperation(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);

        if(expenseDataSource.createExpenseEntry(expense)){
//            updateExpenseSheet();
            /*try {
                expense.setId(insertId);
                expenseDataSource.createExpenseEntryOnServer(expense);
            }catch (JSONException exp){
                exp.printStackTrace();
            }*/

            AppUtils.getService().createExpenseEntryOnServer(expense);

        }else{
            Log.e("ExpenseDetail", "Error while submitting expense");
        }
    }

    private void performSubmitOperation(){
        onBackPressed();
    }



    private void curateExpenseList(ArrayList<Expense> expensesList){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        for(Expense expense: expensesList){
            Log.d("Server ID", expense.getServer_expense_id()+"");
            if(containsServerId(prepopulated_expenses_list, expense)){
                Log.d("Updating", expense.getServer_expense_id()+"");
                int index = prepopulated_expenses_list.indexOf(expense);
                Expense temp_expense = prepopulated_expenses_list.get(index);
                temp_expense.updateExpense(expense);
                if(expenseDataSource.updateSheetEntryWithServerID(temp_expense)){
                    Log.d("Update Success", "Successfully updated "+temp_expense.getServer_expense_id());
                }else{
                    Log.e("Update Failure", "Failed to update "+temp_expense.getServer_expense_id());
                }

            }else{
                Log.d("Adding", expense.getServer_expense_id()+"");
                prepopulated_expenses_list.add(expense);
                if(expenseDataSource.createExpenseEntry(expense)){
                    Log.d("Add Success", "Successfully added "+expense.getServer_expense_id());
                }else{
                    Log.e("Add Failure", "Failed to add "+expense.getServer_expense_id());
                }
            }
        }
    }

    private boolean containsServerId(ArrayList<Expense> expensesList, Expense expense){
        for(Expense expense1: expensesList){
            if(expense1.getServer_expense_id().equalsIgnoreCase(expense.getServer_expense_id())){
                return true;
            }
        }
        return false;
    }

    private void performEditOperation(int position, ExpenseListAdapter.ViewHolder viewHolder){
        openExpenseItemDetailScreen(expenseListAdapter.getItem(position), true);
    }

    private void performRemoveOperation(final int position){
        final Expense expense = expenseListAdapter.getItem(position);

        String message = "Are you sure you want to delete expense item";

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmation");
        alert.setMessage(message);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(removeExpense(expense)){
                    expenseListAdapter.removeItem(position);
                    expenseListAdapter.notifyDataSetChanged();

                    sheet_amount -= expense.getAmount();
                    total_amount_value.setText(String.valueOf(sheet_amount));
                }
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private boolean removeExpense(Expense expense){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        if(expenseDataSource.removeExpenseEntry(expense)){
            Log.e("Remove Expense", "SUCCESSFUL");
            return true;
        }else{
            Log.e("Remove Expense", "FAILURE");
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_expense_item:
                openAddExpenseItemScreen();
                break;
            case R.id.button_submit_expense_sheet:
                performSubmitOperation();
                break;
            case R.id.expense_detail_sync_button:
//                performSyncOperation(true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 200 && data!=null){
            Expense expense = data.getParcelableExtra("EXPENSE");
            expense.setSheet_id(sheet.getSheet_id());
            Log.e("expense.getId(): ", "" + expense.getId());
            if(expense.getId()!=null){
                updateDbOperation(expense);
                double amount = expenseListAdapter.updateExpense(expense);
                sheet_amount -= amount;
            }else{
                expense.setId(AppUtils.generateUniqueKey(this));
                createNewDbOperation(expense);
                new_added_expenses_list.add(expense);
                expenseListAdapter.addExpense(expense);
            }

            expenseListAdapter.sortList();
            expenseListAdapter.notifyDataSetChanged();
            sheet_amount += expense.getAmount();
            total_amount_value.setText(String.valueOf(sheet_amount));

        }
    }

    @Override
    public void onItemClick(int index, View view) {
        openExpenseItemDetailScreen(expenseListAdapter.getItem(index), false);
    }

    @Override
    public void onCancelClick(int index, ExpenseListAdapter.ViewHolder viewHolder) {

    }

    @Override
    public void onMenuClick(int position, int index, ExpenseListAdapter.ViewHolder viewHolder) {
        switch (index){
            case ExpenseListAdapter.MENU_OPTION_EDIT:
                performEditOperation(position, viewHolder);
                break;
            case ExpenseListAdapter.MENU_OPTION_REMOVE:
                performRemoveOperation(position);
                break;
        }
    }

    @Override
    public void onOkClick(int index, String string, ExpenseListAdapter.ViewHolder viewHolder) {

    }

    @Override
    public void onLongClick(int index, ExpenseListAdapter.ViewHolder viewHolder) {

    }

    @Override
    public void onBackPressed() {
        sheet.setAmount(sheet_amount);
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(expenseSheetDataSource.updateSheetEntry(sheet)){
        }else{
            Log.e("ExpenseDetail", "Error while submitting Sheet");
        }
        setResult(200);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(BackendService.FILTER_EXPENSE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
