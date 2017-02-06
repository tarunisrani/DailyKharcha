package com.tarunisrani.dailykharcha.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.ExpenseSheetListAdapter;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.listeners.ExpenseSheetListClickListener;
import com.tarunisrani.dailykharcha.model.Sheet;

import org.json.JSONException;

import java.util.Calendar;

public class ExpenseActivity extends AppCompatActivity implements View.OnClickListener, ExpenseSheetListClickListener {

    private RecyclerView expenses_sheet_list_view;
    private ExpenseSheetListAdapter sheetListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_layout);

        sheetListAdapter = new ExpenseSheetListAdapter(this);

        ImageView button_add_new_expense_sheet = (ImageView) findViewById(R.id.button_add_new_expense_sheet);
        ImageView button_sync_all_expense_sheet = (ImageView) findViewById(R.id.button_sync_all_expense_sheet);
        expenses_sheet_list_view = (RecyclerView) findViewById(R.id.expenses_sheet_list_view);

        expenses_sheet_list_view.setAdapter(sheetListAdapter);
        sheetListAdapter.setClickListener(this);

        expenses_sheet_list_view.setHasFixedSize(true);
        LinearLayoutManager linearLayout =new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        expenses_sheet_list_view.setLayoutManager(linearLayout);
        button_add_new_expense_sheet.setOnClickListener(this);
        button_sync_all_expense_sheet.setOnClickListener(this);

        fetchSheetList();
    }

    private void fetchSheetList(){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        sheetListAdapter.setSheetList(expenseSheetDataSource.getSheetList());
        sheetListAdapter.notifyDataSetChanged();
    }

    private void addNewSheetInList(){
        Sheet sheet = new Sheet();
        sheet.setSheet_name("Sheet - " + (sheetListAdapter.getItemCount() + 1));
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        sheet.setSheet_creation_date(String.format("%02d/%02d/%d", day, month, year));

        long sheet_id = createNewSheetInDb(sheet);

        if(sheet_id != -1){
            sheet.setSheet_id(sheet_id);
            sheetListAdapter.addSheet(sheet);
            sheetListAdapter.notifyDataSetChanged();
        }
    }

    private long createNewSheetInDb(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        long result = expenseSheetDataSource.createSheetEntry(sheet);
        if(result!=-1){
            try{
                expenseSheetDataSource.createSheetEntryOnServer(sheet);
            } catch (JSONException exp){
                exp.printStackTrace();
            }
        }
        return result;
    }

    private void openSheetScreen(Sheet sheet){
        Intent intent = new Intent(this, ExpenseDetailActivity.class);
        intent.putExtra("SHEET", sheet);
        startActivityForResult(intent, 100);
    }

    private boolean updateSheetName(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(expenseSheetDataSource.updateSheetEntry(sheet)){
            Log.e("Update Sheet", "SUCCESSFUL");
            return true;
        }else{
            Log.e("Update Sheet", "FAILURE");
            return false;
        }
    }

    private boolean removeSheet(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(expenseSheetDataSource.removeSheetEntry(sheet)){
            Log.e("Remove Sheet", "SUCCESSFUL");
            return true;
        }else{
            Log.e("Remove Sheet", "FAILURE");
            return false;
        }
    }

    private void performEditOperation(int position, ExpenseSheetListAdapter.ViewHolder viewHolder){
        viewHolder.showControlPanel();
    }

    private void performRemoveOperation(final int position){

        final Sheet sheet = sheetListAdapter.getItem(position);

        String message = "Are you sure you want to delete "+sheet.getSheet_name();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmation");
        alert.setMessage(message);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(removeSheet(sheet)){
                    sheetListAdapter.removeItem(position);
                    sheetListAdapter.notifyDataSetChanged();
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

    private void performShareOperation(int position){

    }

    private void performAnalysisOperation(int position){
        Sheet sheet = sheetListAdapter.getItem(position);
        Intent intent = new Intent(this, AnalyseSheetActivity.class);
        intent.putExtra("SHEET", sheet);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_add_new_expense_sheet:
                addNewSheetInList();
                break;
            case R.id.button_sync_all_expense_sheet:

                break;
        }
    }

    @Override
    public void onItemClick(int index, View view) {
        openSheetScreen(sheetListAdapter.getItem(index));
    }

    @Override
    public void onOkClick(int index, String string, ExpenseSheetListAdapter.ViewHolder viewHolder) {
        Sheet sheet = sheetListAdapter.getItem(index);
        sheet.setSheet_name(string);
        if(updateSheetName(sheet)){
            viewHolder.hideControlPanel();
            viewHolder.setSheetName(string);
        }
    }

    @Override
    public void onCancelClick(int index, ExpenseSheetListAdapter.ViewHolder viewHolder) {
        viewHolder.hideControlPanel();
    }

    @Override
    public void onLongClick(int index, ExpenseSheetListAdapter.ViewHolder viewHolder) {
        viewHolder.showControlPanel();
    }

    @Override
    public void onMenuClick(int position, int index, ExpenseSheetListAdapter.ViewHolder viewHolder) {
        switch (index){
            case ExpenseSheetListAdapter.MENU_OPTION_RENAME:
                performEditOperation(position, viewHolder);
                break;
            case ExpenseSheetListAdapter.MENU_OPTION_REMOVE:
                performRemoveOperation(position);
                break;
            case ExpenseSheetListAdapter.MENU_OPTION_SHARE:
                performShareOperation(position);
                break;
            case ExpenseSheetListAdapter.MENU_OPTION_ANALYZE:
                performAnalysisOperation(position);
                break;
        }
    }

    /*private void clearAllEditEnabledItems(){
        for(int index = 0; index<sheetListAdapter.getItemCount(); index++) {
            ExpenseSheetListAdapter.ViewHolder viewHolder = (ExpenseSheetListAdapter.ViewHolder)expenses_sheet_list_view.findViewHolderForAdapterPosition(index);
            viewHolder.hideControlPanel();
        }
    }*/

    private boolean clearAllEditEnabledItems(){
        int count =0;
        for(int index = 0; index<sheetListAdapter.getItemCount(); index++) {
            ExpenseSheetListAdapter.ViewHolder viewHolder = (ExpenseSheetListAdapter.ViewHolder)expenses_sheet_list_view.findViewHolderForAdapterPosition(index);
            if(viewHolder.isEditEnabled()){
                viewHolder.hideControlPanel();
                count++;
            }
        }
        return count>0;
    }

    @Override
    public void onBackPressed() {
        if(!clearAllEditEnabledItems()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            fetchSheetList();
        }
    }
}
