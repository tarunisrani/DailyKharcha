package com.tarunisrani.dailykharcha.listeners;

import android.view.View;

import com.tarunisrani.dailykharcha.adapters.ExpenseListAdapter;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface ExpenseListClickListener {
    void onItemClick(int index, View view);
    void onCancelClick(int index, ExpenseListAdapter.ViewHolder viewHolder);
    void onMenuClick(int position, int index, ExpenseListAdapter.ViewHolder viewHolder);
    void onOkClick(int index, String string, ExpenseListAdapter.ViewHolder viewHolder);
    void onLongClick(int index, ExpenseListAdapter.ViewHolder viewHolder);
}
