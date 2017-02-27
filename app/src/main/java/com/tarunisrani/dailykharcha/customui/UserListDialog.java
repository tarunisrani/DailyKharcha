package com.tarunisrani.dailykharcha.customui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
        TextView button_cancel = (TextView) findViewById(R.id.user_list_button_cancel);

        button_ok.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

        adapter = new UserListAdapter();

    }

    public void showUserList(ArrayList<UserDetails> userList){


        adapter.setUserDetailList(userList);
        adapter.setClickListener(this);
        user_list_list_view.setAdapter(adapter);
        user_list_list_view.setHasFixedSize(true);
        LinearLayoutManager linearLayout =new LinearLayoutManager(getContext());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        user_list_list_view.setLayoutManager(linearLayout);
        adapter.notifyDataSetChanged();
    }

    private void performOkOperation(){
        ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();
        for(int index = 0; index<adapter.getItemCount(); index++) {
            UserListAdapter.ViewHolder viewHolder = (UserListAdapter.ViewHolder) user_list_list_view.findViewHolderForAdapterPosition(index);
            if (viewHolder.getCheckmarkEnable()) {
                userDetailsArrayList.add(viewHolder.getUserDetails());
            }
        }

        if(mListListener!=null){
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
    public void onItemClick(int index, UserListAdapter.ViewHolder holder) {
        holder.setCheckmarkEnable(!holder.getCheckmarkEnable());
    }
}
