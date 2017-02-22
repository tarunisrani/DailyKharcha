package com.tarunisrani.dailykharcha.listeners;

import com.tarunisrani.dailykharcha.adapters.UserListAdapter;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface UserListClickListener {
    void onItemClick(int index, UserListAdapter.ViewHolder holder);
}
