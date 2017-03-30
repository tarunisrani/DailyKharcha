package com.tarunisrani.dailykharcha.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarunisrani on 3/6/17.
 */

public class AutoCompleteAdapter extends ArrayAdapter<UserDetails> {

    private ArrayList<UserDetails> mList;

    public AutoCompleteAdapter(Context context, ArrayList<UserDetails> objects){
//        this(context, R.layout.user_list_item_layout, R.id.user_list_name, convertList(objects));
        this(context, R.layout.user_list_item_layout, R.id.user_list_name, objects);
        mList = objects;
    }

    public AutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<UserDetails> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    private static ArrayList<String> convertList(ArrayList<UserDetails> list){
        ArrayList<String> result = new ArrayList<>();
        for(UserDetails userDetails: list){
            result.add(userDetails.getUser_name());
        }
        return result;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item_layout, parent, false);
//        UserDetails userDetails = mList.get(position);
//        TextView user_list_name = (TextView) view.findViewById(R.id.user_list_name);
//        TextView user_list_uid = (TextView) view.findViewById(R.id.user_list_uid);
//        user_list_name.setText(getItem(position));
//        user_list_uid.setText(userDetails.getUid());
//
        return super.getDropDownView(position, convertView, parent);
//        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item_layout, parent, false);
//        UserDetails userDetails = mList.get(position);
        UserDetails userDetails = getItem(position);
        TextView user_list_name = (TextView) view.findViewById(R.id.user_list_name);
        TextView user_list_uid = (TextView) view.findViewById(R.id.user_list_uid);

        user_list_name.setText(userDetails.getUser_name());
        user_list_uid.setText(userDetails.getUid());

//        user_list_uid.setVisibility(View.GONE);


//        return super.getView(position, convertView, parent);
        return view;
    }
}
