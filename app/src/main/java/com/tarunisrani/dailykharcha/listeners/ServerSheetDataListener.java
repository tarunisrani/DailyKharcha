package com.tarunisrani.dailykharcha.listeners;

import com.tarunisrani.dailykharcha.model.Sheet;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface ServerSheetDataListener {
    void onServerDataRetrieved(ArrayList<Sheet> sheets);
}
