package com.tarunisrani.dailykharcha.listeners;

import com.tarunisrani.dailykharcha.model.Expense;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface ServerExpenseDataListener {
    void onServerDataRetrieved(ArrayList<Expense> expenses);
}
