package com.tarunisrani.dailykharcha.listeners;

import com.tarunisrani.dailykharcha.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public interface UserListGenerationListener {
    void onListGenerated(ArrayList<UserDetails> userDetailses);
}
