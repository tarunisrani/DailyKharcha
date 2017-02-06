package com.tarunisrani.dailykharcha.listeners;

import android.view.View;

import com.tarunisrani.dailykharcha.adapters.ExpenseSheetListAdapter;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface ExpenseSheetListClickListener {
    void onItemClick(int index, View view);
    void onCancelClick(int index, ExpenseSheetListAdapter.ViewHolder viewHolder);
    void onMenuClick(int position, int index, ExpenseSheetListAdapter.ViewHolder viewHolder);
    void onOkClick(int index, String string, ExpenseSheetListAdapter.ViewHolder viewHolder);
    void onLongClick(int index, ExpenseSheetListAdapter.ViewHolder viewHolder);
}
