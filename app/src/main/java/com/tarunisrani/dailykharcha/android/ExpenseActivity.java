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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.ExpenseSheetListAdapter;
import com.tarunisrani.dailykharcha.adapters.GroupListAdapter;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;
import com.tarunisrani.dailykharcha.listeners.ExpenseSheetListClickListener;
import com.tarunisrani.dailykharcha.listeners.SelectedUserListListener;
import com.tarunisrani.dailykharcha.listeners.UserListGenerationListener;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.Sheet;
import com.tarunisrani.dailykharcha.model.UserDetails;
import com.tarunisrani.dailykharcha.utils.AppUtils;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import static com.tarunisrani.dailykharcha.R.id.button_logout;
import static com.tarunisrani.dailykharcha.utils.AppUtils.getService;

public class ExpenseActivity extends AppCompatActivity implements View.OnClickListener, ExpenseSheetListClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView expenses_sheet_list_view;
    private ExpenseSheetListAdapter sheetListAdapter;

    private ArrayList<Sheet> prepopulatedSheetList;

    private String selectedGroupid;
    private Spinner group_list_spinner;
    private ProgressBar user_list_progressbar;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equalsIgnoreCase(BackendService.FILTER_SHEET)){
                Sheet sheet = intent.getParcelableExtra("SHEET");
                String action = intent.getStringExtra("ACTION");
                Log.e("Sheet "+action, sheet.toString());
                fetchSheetList();
            }else if(intent.getAction().equalsIgnoreCase(BackendService.FILTER_GROUP)){
                Group group = intent.getParcelableExtra("GROUP");
                String action = intent.getStringExtra("ACTION");
                Log.e("Group "+action, group.toString());
                prepareListOfGroups();
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_layout);

        selectedGroupid = new SharedPreferrenceUtil().fetchSelectedGroupID(this);

        sheetListAdapter = new ExpenseSheetListAdapter(this);

        ImageView button_add_new_expense_sheet = (ImageView) findViewById(R.id.button_add_new_expense_sheet);
        ImageView button_share_all_expense_sheet = (ImageView) findViewById(R.id.button_share_all_expense_sheet);
        Button button_logout = (Button) findViewById(R.id.button_logout);

        group_list_spinner = (Spinner) findViewById(R.id.group_list_spinner);
        group_list_spinner.setOnItemSelectedListener(this);

        prepareListOfGroups();

        user_list_progressbar = (ProgressBar) findViewById(R.id.user_list_progressbar);
        expenses_sheet_list_view = (RecyclerView) findViewById(R.id.expenses_sheet_list_view);

        expenses_sheet_list_view.setAdapter(sheetListAdapter);
        sheetListAdapter.setClickListener(this);

        expenses_sheet_list_view.setHasFixedSize(true);
        LinearLayoutManager linearLayout =new LinearLayoutManager(this);
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        expenses_sheet_list_view.setLayoutManager(linearLayout);
        button_add_new_expense_sheet.setOnClickListener(this);
        button_share_all_expense_sheet.setOnClickListener(this);
        button_logout.setOnClickListener(this);

        fetchSheetList();
    }

    private void prepareListOfGroups(){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        ArrayList<Group> groups = groupDataSource.getGroupItems();

//        ArrayAdapter<String> group_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        GroupListAdapter groupListAdapter = new GroupListAdapter(this, R.layout.group_list_item_layout, groups);

        group_list_spinner.setAdapter(groupListAdapter);

        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);

        int index = groupListAdapter.getIDPosition(selected_group_id);
        group_list_spinner.setSelection(index!=-1?index:0);
//        for(Group group : groups){
//            group_list_adapter.add(group.getGroup_id());
//        }
    }


    private void fetchSheetList(){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        prepopulatedSheetList = expenseSheetDataSource.getSheetList(selectedGroupid);
        for(Sheet sheet: prepopulatedSheetList){
            Log.e("Sheet details", sheet.toString());
        }
        sheetListAdapter.setSheetList(prepopulatedSheetList);
        sheetListAdapter.notifyDataSetChanged();
    }

    private void addNewSheetInList(){
        Sheet sheet = new Sheet();
        sheet.setSheet_id(AppUtils.generateUniqueKey(this));
        sheet.setSheet_name("Sheet - " + (sheetListAdapter.getItemCount() + 1));
        sheet.setGroup_id(selectedGroupid);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        sheet.setSheet_creation_date(String.format("%02d/%02d/%d", day, month, year));

        if(addSheetInList(sheet)){
            try{
                getService().createSheetEntryOnServer(sheet);
            } catch (JSONException exp){
                exp.printStackTrace();
            }
        }

    }

    private boolean addSheetInList(Sheet sheet){
        boolean sheet_id = createNewSheetInDb(sheet);

        if(sheet_id){
//            sheet.setSheet_id(sheet_id);
            sheetListAdapter.addSheet(sheet);
            sheetListAdapter.notifyDataSetChanged();
        }

        return sheet_id;
    }

    private boolean createNewSheetInDb(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        return expenseSheetDataSource.createSheetEntry(sheet);
    }

    private boolean checkIfSheetEntryExist(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        return expenseSheetDataSource.isSheetEntryExist(sheet);
    }

    private void openSheetScreen(Sheet sheet){
        Intent intent = new Intent(this, ExpenseDetailActivity.class);
        intent.putExtra("SHEET", sheet);
        startActivityForResult(intent, 100);
    }

    private void openLoginScreen(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean updateSheetDetails(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(expenseSheetDataSource.updateSheetEntry(sheet)){
            Log.e("Update Sheet", "SUCCESSFUL");
            return true;
        }else{
            Log.e("Update Sheet", "FAILURE");
            return false;
        }
    }

    private boolean updateSheetDetailsWithServerID(Sheet sheet){
        ExpenseSheetDataSource expenseSheetDataSource = new ExpenseSheetDataSource(this);
        if(expenseSheetDataSource.updateSheetEntryWithServerId(sheet)){
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

    private void performShareOperation(){

        user_list_progressbar.setVisibility(View.VISIBLE);

        AppUtils.getService().getUserList(new UserListGenerationListener() {
            @Override
            public void onListGenerated(ArrayList<UserDetails> userDetailses) {
                showUserListDialog(userDetailses);
            }
        });


    }

    private void showUserListDialog(ArrayList<UserDetails> userDetailsArrayList){
        user_list_progressbar.setVisibility(View.GONE);
        UserListDialog dialog = new UserListDialog(this, new SelectedUserListListener() {
            @Override
            public void onSharedUserListGenerated(ArrayList<UserDetails> userDetailses) {
                performServerEntryForSharedUser(userDetailses);
            }
        });
        dialog.setTitle("Choose user");
        dialog.show();
        dialog.showUserList(userDetailsArrayList);
    }

    private void performServerEntryForSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        AppUtils.getService().createSharedGroupEntryOnServer(userDetailses, group);
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
            case R.id.button_share_all_expense_sheet:
                performShareOperation();
                break;
            case button_logout:
                AppUtils.getService().performSignOut();
                openLoginScreen();
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
        if(updateSheetDetails(sheet)){
            viewHolder.hideControlPanel();
            viewHolder.setSheetName(string);

            getService().updateSheetEntryOnServer(sheet);

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

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackendService.FILTER_SHEET);
        intentFilter.addAction(BackendService.FILTER_GROUP);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Group selected_group = ((GroupListAdapter)parent.getAdapter()).getItem(position);
        selectedGroupid = selected_group.getGroup_id();
        new SharedPreferrenceUtil().setSelectedGroup(this, selectedGroupid);
        fetchSheetList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
