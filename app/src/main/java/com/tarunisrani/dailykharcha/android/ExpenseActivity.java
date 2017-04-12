package com.tarunisrani.dailykharcha.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.ExpenseSheetListAdapter;
import com.tarunisrani.dailykharcha.adapters.GroupListAdapter;
import com.tarunisrani.dailykharcha.customui.AddGroupDialog;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseSheetDataSource;
import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;
import com.tarunisrani.dailykharcha.listeners.AddGroupListener;
import com.tarunisrani.dailykharcha.listeners.AddGroupOnServerListener;
import com.tarunisrani.dailykharcha.listeners.ExpenseSheetListClickListener;
import com.tarunisrani.dailykharcha.listeners.ServiceConnectionListener;
import com.tarunisrani.dailykharcha.listeners.UserListGenerationListener;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.Sheet;
import com.tarunisrani.dailykharcha.model.UserDetails;
import com.tarunisrani.dailykharcha.utils.AppConstant;
import com.tarunisrani.dailykharcha.utils.AppUtils;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import static com.tarunisrani.dailykharcha.R.id.button_add_new_expense_sheet;
import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_SHARED_USER_LIST;
import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_SHEET;
import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_USER_DETAIL_LIST;
import static com.tarunisrani.dailykharcha.utils.AppUtils.getService;

public class ExpenseActivity extends AppCompatActivity implements View.OnClickListener, ExpenseSheetListClickListener, AdapterView.OnItemSelectedListener, ServiceConnectionListener {

    private RecyclerView expenses_sheet_list_view;
    private ExpenseSheetListAdapter sheetListAdapter;

    private ArrayList<Sheet> prepopulatedSheetList;

    private String selectedGroupid;
    private Spinner group_list_spinner;
    private ProgressBar user_list_progressbar;
    private ImageView button_share_all_expense_sheet;
    private ImageView button_remove_group;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equalsIgnoreCase(BackendService.FILTER_SHEET)){
                Sheet sheet = intent.getParcelableExtra(INTENT_KEY_SHEET);
                String action = intent.getStringExtra(AppConstant.INTENT_KEY_ACTION);
                Log.e("Sheet "+action, sheet.toString());
                fetchSheetList();
            }else if(intent.getAction().equalsIgnoreCase(BackendService.FILTER_GROUP)){
                Group group = intent.getParcelableExtra(AppConstant.INTENT_KEY_GROUP);
                String action = intent.getStringExtra(AppConstant.INTENT_KEY_ACTION);
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
        button_share_all_expense_sheet = (ImageView) findViewById(R.id.button_share_all_expense_sheet);
        button_remove_group = (ImageView) findViewById(R.id.button_remove_group);

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
        button_remove_group.setOnClickListener(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.expense_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.daily_kharcha_logo);



        fetchSheetList();
    }

    private void prepareListOfGroups(){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        ArrayList<Group> groups = groupDataSource.getGroupItems();
        groups.add(new Group());

//        ArrayAdapter<String> group_list_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        GroupListAdapter groupListAdapter = new GroupListAdapter(this, R.layout.group_list_item_layout, groups);

        group_list_spinner.setAdapter(groupListAdapter);

        String selected_group_id = new SharedPreferrenceUtil().fetchSelectedGroupID(this);

        int index = groupListAdapter.getIDPosition(selected_group_id);

        group_list_spinner.setSelection(index != -1 ? index : 0);


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
        intent.putExtra(INTENT_KEY_SHEET, sheet);
        startActivityForResult(intent, 100);
    }

    private void openLoginScreen(){
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();

        AppUtils.openNewScreen(this, LoginActivity.class, true, null);
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
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        ArrayList<Expense> expenseItems = expenseDataSource.getExpenseItems(sheet.getSheet_id());
        if(expenseItems.size()>0){
            if(expenseDataSource.removeExpenseEntry(expenseItems)){
                AppUtils.getService().removeExpenseEntryFromServer(expenseItems);
                if(expenseSheetDataSource.removeSheetEntry(sheet)){
                    Log.e("Remove Sheet", "SUCCESSFUL");
                    AppUtils.getService().removeSheetEntryFromServer(sheet);
                    return true;
                }else{
                    Log.e("Remove Sheet", "FAILURE");
                }
            }
        }else{
            if(expenseSheetDataSource.removeSheetEntry(sheet)){
                Log.e("Remove Sheet", "SUCCESSFUL");
                AppUtils.getService().removeSheetEntryFromServer(sheet);
                return true;
            }else{
                Log.e("Remove Sheet", "FAILURE");
            }
        }

        return false;
    }

    private void performEditOperation(int position, ExpenseSheetListAdapter.ViewHolder viewHolder){
        viewHolder.showControlPanel();
    }

    private void performRemoveOperation(final int position){

        final Sheet sheet = sheetListAdapter.getItem(position);

        String message = "Are you sure you want to delete "+sheet.getSheet_name();

        AppUtils.showAlertDialog(this, "Confirmation", message, true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSheet(sheet, position);
            }
        }, null);


    }

    private void removeSheet(Sheet sheet, int position){

        if(removeSheet(sheet)){
            sheetListAdapter.removeItem(position);
            sheetListAdapter.notifyDataSetChanged();
        }
    }


    private void performShareOperation(int position){

    }

    private void performShareOperation(){

        user_list_progressbar.setVisibility(View.VISIBLE);

        AppUtils.getService().getUserList(new UserListGenerationListener() {
            @Override
            public void onListGenerated(ArrayList<UserDetails> userDetailses) {
                openShareGroupScreen(userDetailses);
            }
        });
    }

    private void performRemoveGroupOperation(){
        AppUtils.showAlertDialog(this, "Confirmation", "Are you sure you want to delete this group?", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeGroup();
            }
        }, null);

    }

    private void removeGroup(){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        if(group.getOwner_id().equalsIgnoreCase(new SharedPreferrenceUtil().fetchUserID(this))){
            if(AppUtils.getService().removeGroupFromDB(group)){
                AppUtils.getService().removeGroupFromServer(group);
                prepareListOfGroups();
            }
        }
    }

    private void openShareGroupScreen(ArrayList<UserDetails> userDetailsArrayList){

        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        ArrayList<String> sharedwith = group.getSharedwith();

        user_list_progressbar.setVisibility(View.GONE);

        /*UserListDialog dialog = new UserListDialog(this, new SelectedUserListListener() {
            @Override
            public void onSharedUserListGenerated(ArrayList<UserDetails> userDetailses) {
                performDBEntryForSharedUser(userDetailses);
                performServerEntryForSharedUser(userDetailses);
            }
        });
        dialog.setTitle("Choose user");
        dialog.show();
        dialog.showUserList(userDetailsArrayList, sharedwith);*/

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(INTENT_KEY_USER_DETAIL_LIST, userDetailsArrayList);
        bundle.putStringArrayList(INTENT_KEY_SHARED_USER_LIST, sharedwith);

        AppUtils.openNewScreen(this, ShareGroupActivity.class, false, bundle);


    }

    private void performServerEntryForSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        group.setShared(true);
        AppUtils.getService().createSharedGroupEntryOnServer(userDetailses, group);
    }

    private void performDBEntryForSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        group.setShared(true);
        group.setSharedwith(generateUIDList(userDetailses));
//        AppUtils.getService().createSharedGroupEntryOnServer(userDetailses, group);
        groupDataSource.updateGroupEntry(group);
    }

    private ArrayList<String> generateUIDList(ArrayList<UserDetails> userDetailses){
        ArrayList<String> list = new ArrayList<>();
        for(UserDetails userDetails:userDetailses){
            list.add(userDetails.getUid());
        }
        return list;
    }

    private void performAnalysisOperation(int position){
        Sheet sheet = sheetListAdapter.getItem(position);
//        Intent intent = new Intent(this, AnalyseSheetActivity.class);
//        intent.putExtra(INTENT_KEY_SHEET, sheet);
//        startActivity(intent);

        Bundle bundle = new Bundle();
        bundle.putParcelable(INTENT_KEY_SHEET, sheet);
        AppUtils.openNewScreen(this, AnalyseSheetActivity.class, false, bundle);
    }

    private boolean clearAllEditEnabledItems(){
        int count =0;
        for(int index = 0; index<sheetListAdapter.getItemCount(); index++) {
            ExpenseSheetListAdapter.ViewHolder viewHolder = (ExpenseSheetListAdapter.ViewHolder)expenses_sheet_list_view.findViewHolderForAdapterPosition(index);
            if(viewHolder!=null && viewHolder.isEditEnabled()){
                viewHolder.hideControlPanel();
                count++;
            }
        }
        return count>0;
    }

    private void performAddGroupOperation(){
        AddGroupDialog dialog = new AddGroupDialog(this, new AddGroupListener() {

            @Override
            public void onGroupAdded(final Group group) {
                if(AppUtils.getService().createGroupEntryInDBIfNotExist(group)){
                    user_list_progressbar.setVisibility(View.VISIBLE);
                    Log.d("Add Group", "Group created successfully.");
                    AppUtils.getService().createGroupEntryOnServer(group, new AddGroupOnServerListener() {
                        @Override
                        public void onGroupAddedOnServer() {
                            user_list_progressbar.setVisibility(View.GONE);
                            prepareListOfGroups();
                            AppUtils.getService().startGroupRelatedListeners(group);
                        }
                    });
                }
            }
        });
        dialog.setTitle("Enter Group Detail");
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case button_add_new_expense_sheet:
                addNewSheetInList();
                break;
            case R.id.button_share_all_expense_sheet:
                performShareOperation();
                break;
            case R.id.button_remove_group:
                performRemoveGroupOperation();
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
        AppUtils.performServiceStartOperation(this, this);
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
        if(position == ((GroupListAdapter)parent.getAdapter()).getItemCount()-1){
            performAddGroupOperation();
            group_list_spinner.setSelection(((GroupListAdapter)parent.getAdapter()).getIDPosition(selectedGroupid));
        }else{
            Group selected_group = ((GroupListAdapter)parent.getAdapter()).getItem(position);
            selectedGroupid = selected_group.getGroup_id();
            new SharedPreferrenceUtil().setSelectedGroup(this, selectedGroupid);
            fetchSheetList();

            String group_id = new SharedPreferrenceUtil().fetchUserID(this) + "_default";

            if(!selected_group.getOwner_id().equalsIgnoreCase(new SharedPreferrenceUtil().fetchUserID(this))){
                button_share_all_expense_sheet.setVisibility(View.GONE);
                button_remove_group.setVisibility(View.GONE);
            }else{
                button_share_all_expense_sheet.setVisibility(View.VISIBLE);
                button_remove_group.setVisibility(View.VISIBLE);
            }

            if(selected_group.getGroup_id().equalsIgnoreCase(group_id)){
                button_remove_group.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.performUnbindService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                AppUtils.getService().performSignOut();
                openLoginScreen();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onServiceBind(BackendService service) {

    }
}
