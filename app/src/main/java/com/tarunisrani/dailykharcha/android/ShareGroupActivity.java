package com.tarunisrani.dailykharcha.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.AutoCompleteAdapter;
import com.tarunisrani.dailykharcha.adapters.UserListAdapter;
import com.tarunisrani.dailykharcha.dbhelper.GroupDataSource;
import com.tarunisrani.dailykharcha.listeners.UserListClickListener;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.model.UserDetails;
import com.tarunisrani.dailykharcha.utils.AppUtils;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

import java.util.ArrayList;

import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_BUNDLE;
import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_SHARED_USER_LIST;
import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_USER_DETAIL_LIST;

public class ShareGroupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, UserListClickListener {

    private RecyclerView user_list;
    private AutoCompleteTextView user_name_input;
    private AutoCompleteAdapter autoCompleteAdapter;
    private UserListAdapter userListAdapter;

    private ArrayList<UserDetails> userDetailses;
    private ArrayList<UserDetails> userDetailsesShared;
    private ArrayList<UserDetails> userDetailsesUnShared;
    private ArrayList<String> sharedWith;

    private String selectedGroupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_share_layout);

        selectedGroupid = new SharedPreferrenceUtil().fetchSelectedGroupID(this);

        userDetailsesUnShared = new ArrayList<>();

        user_list = (RecyclerView) findViewById(R.id.group_share_user_list);
        user_name_input = (AutoCompleteTextView) findViewById(R.id.group_share_user_name_input);
        ImageView group_share_cancel = (ImageView) findViewById(R.id.group_share_cancel);
        ImageView group_share_submit = (ImageView) findViewById(R.id.group_share_submit);

        group_share_cancel.setOnClickListener(this);
        group_share_submit.setOnClickListener(this);


        Intent intent = getIntent();
        if(intent!=null){
            processBundle(intent.getBundleExtra(INTENT_KEY_BUNDLE));
        }
    }

    private void processBundle(Bundle bundle){
        userDetailses = bundle.getParcelableArrayList(INTENT_KEY_USER_DETAIL_LIST);
        sharedWith = bundle.getStringArrayList(INTENT_KEY_SHARED_USER_LIST);

        showUserList(userDetailses, sharedWith);
    }

    private ArrayList<UserDetails> generateListOfUsersAlreadyInGroup(ArrayList<UserDetails> userList, ArrayList<String> list){
        ArrayList<UserDetails> result = new ArrayList<>();
        for(String str: list){
            for(UserDetails userDetails: userList){
                if(userDetails.getUid().equalsIgnoreCase(str)){
                    result.add(userDetails);
                }
            }
        }

        return result;
    }


    private void showUserList(ArrayList<UserDetails> userList, ArrayList<String> sharedWithUserList){

        if(userList.size() >0){

            autoCompleteAdapter = new AutoCompleteAdapter(this, userList);
            userListAdapter = new UserListAdapter();

            userDetailsesShared = generateListOfUsersAlreadyInGroup(userList, sharedWithUserList);

            userListAdapter.setUserDetailList(userDetailsesShared);
            userListAdapter.setmSharedWith(sharedWith);
            userListAdapter.setClickListener(this);

            user_name_input.setAdapter(autoCompleteAdapter);
            user_name_input.setThreshold(1);

            user_name_input.setOnItemClickListener(this);

            user_list.setHasFixedSize(true);
            user_list.setAdapter(userListAdapter);
            LinearLayoutManager linearLayout =new LinearLayoutManager(this);
            linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
            user_list.setLayoutManager(linearLayout);
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    private void performSubmitOperation(){
        if(performDBEntryForSharedUser(userDetailsesShared)) {
            performServerEntryForSharedUser(userDetailsesShared);
            performServerEntryForUnSharedUser(userDetailsesUnShared);
            finish();
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void performCancelOperation(){
        onBackPressed();
    }

    private void performRemoveOperation(int index){
//        Toast.makeText(this, "Remove", Toast.LENGTH_SHORT).show();
        userDetailsesShared.remove(index);
        userListAdapter.notifyDataSetChanged();
    }

    private void performUnshareOperation(int index){
//        Toast.makeText(this, "Unshare", Toast.LENGTH_SHORT).show();
        userDetailsesUnShared.add(userDetailsesShared.get(index).clone());
        userDetailsesShared.remove(index);

        userListAdapter.notifyDataSetChanged();
    }

    private void performServerEntryForSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        group.setShared(true);
        AppUtils.getService().createSharedGroupEntryOnServer(userDetailses, group);
    }

    private void performServerEntryForUnSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        AppUtils.getService().createUnSharedGroupEntryOnServer(userDetailses, group);
    }

    private boolean performDBEntryForSharedUser(ArrayList<UserDetails> userDetailses){
        GroupDataSource groupDataSource = new GroupDataSource(this);
        Group group = groupDataSource.getGroup(selectedGroupid);
        group.setShared(true);
        group.setSharedwith(generateUIDList(userDetailses));
//        AppUtils.getService().createSharedGroupEntryOnServer(userDetailses, group);
        return groupDataSource.updateGroupEntry(group);
    }

    private ArrayList<String> generateUIDList(ArrayList<UserDetails> userDetailses){
        ArrayList<String> list = new ArrayList<>();
        for(UserDetails userDetails:userDetailses){
            list.add(userDetails.getUid());
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.group_share_submit:
                performSubmitOperation();
                break;
            case R.id.group_share_cancel:
                performCancelOperation();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(this, (parent.getAdapter().getItem(position)).toString(), Toast.LENGTH_SHORT).show();
        UserDetails userDetails = (UserDetails) parent.getAdapter().getItem(position);
        if(!userDetailsesShared.contains(userDetails)){
            userDetailsesShared.add(userDetails);
            userListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onUserItemClick(int index, int operation) {
        if(operation == UserListAdapter.REMOVE_OPERATION){
            performRemoveOperation(index);
        }else if(operation == UserListAdapter.UNSHARE_OPERATION){
            performUnshareOperation(index);
        }
    }
}
