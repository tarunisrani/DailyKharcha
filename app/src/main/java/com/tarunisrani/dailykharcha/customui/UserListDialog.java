package com.tarunisrani.dailykharcha.customui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.UserListAdapter;
import com.tarunisrani.dailykharcha.listeners.SelectedUserListListener;
import com.tarunisrani.dailykharcha.listeners.UserListClickListener;
import com.tarunisrani.dailykharcha.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 2/21/17.
 */

public class UserListDialog extends Dialog implements View.OnClickListener, UserListClickListener {

    private RecyclerView user_list_list_view;
    private UserListAdapter adapter;
    private SelectedUserListListener mListListener;
    private TextView label_no_user;
    private TextView button_cancel;
    private ArrayList<UserDetails> shareWithUserList;
    private AutoCompleteTextView user_list_input_name;

    public UserListDialog(Context context, SelectedUserListListener listListener) {
        super(context);
        this.mListListener = listListener;
    }

    protected UserListDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public UserListDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);

        user_list_list_view = (RecyclerView) findViewById(R.id.user_list_list_view);
        TextView button_ok = (TextView) findViewById(R.id.user_list_button_ok);
        user_list_input_name = (AutoCompleteTextView) findViewById(R.id.user_list_input_name);
        button_cancel = (TextView) findViewById(R.id.user_list_button_cancel);
        label_no_user = (TextView) findViewById(R.id.label_no_user);

        button_ok.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

        adapter = new UserListAdapter();

    }

    public void setShareWithUserList(ArrayList<UserDetails> userList){
        this.shareWithUserList = userList;
    }

    private ArrayList<String> generateListOfUserNames(ArrayList<UserDetails> userList){
        ArrayList<String> result = new ArrayList<>();
        for(UserDetails userDetails: userList){
            result.add(userDetails.getUser_name());
        }
        return result;
    }

    public void showUserList(ArrayList<UserDetails> userList, ArrayList<String> sharedWithUserList){

        if(userList.size() >0){

            this.shareWithUserList = userList;

            ArrayAdapter<String> expense_type_adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, generateListOfUserNames(userList));

            user_list_input_name.setAdapter(expense_type_adapter);

            label_no_user.setVisibility(View.GONE);
            user_list_list_view.setVisibility(View.VISIBLE);
            adapter.setUserDetailList(userList);
            adapter.setmSharedWith(sharedWithUserList);
            adapter.setClickListener(this);
            user_list_list_view.setAdapter(adapter);
            user_list_list_view.setHasFixedSize(true);
            LinearLayoutManager linearLayout =new LinearLayoutManager(getContext());
            linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
            user_list_list_view.setLayoutManager(linearLayout);
            adapter.notifyDataSetChanged();
        }else{
            button_cancel.setVisibility(View.GONE);
        }
    }

    private void performOkOperation(){
        ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();
        for(int index = 0; index<adapter.getItemCount(); index++) {
            UserListAdapter.ViewHolder viewHolder = (UserListAdapter.ViewHolder) user_list_list_view.findViewHolderForAdapterPosition(index);
            if (viewHolder.getCheckmarkEnable()) {
                userDetailsArrayList.add(viewHolder.getUserDetails());
            }
        }

        if(mListListener!=null && userDetailsArrayList.size()>0){
            mListListener.onSharedUserListGenerated(userDetailsArrayList);
        }

        dismiss();
    }

    private void performCancelOperation(){
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_list_button_ok:
                performOkOperation();
                break;
            case R.id.user_list_button_cancel:
                performCancelOperation();
                break;
        }
    }

    @Override
    public void onUserItemClick(int index, int operation) {

    }

//    @Override
//    public void onItemClick(int index, UserListAdapter.ViewHolder holder) {
//        holder.setCheckmarkEnable(!holder.getCheckmarkEnable());
//    }
}
