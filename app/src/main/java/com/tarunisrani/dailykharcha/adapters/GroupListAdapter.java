package com.tarunisrani.dailykharcha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.model.Group;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 4/7/16.
 */
public class GroupListAdapter extends ArrayAdapter {

    private ArrayList<Group> mList;


    public GroupListAdapter(Context context, int resource, ArrayList<Group> objects) {
        super(context, resource, objects);
        mList = objects;

    }

    private View getCustomView(int position,
                              ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.group_list_item_layout, parent, false);

        TextView group_list_name = (TextView) layout.findViewById(R.id.group_list_name);
        TextView group_list_uid = (TextView) layout.findViewById(R.id.group_list_uid);

        Group group = mList.get(position);
        group_list_name.setText(group.getGroup_name());
        group_list_uid.setText(group.getOwner_id());

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position == mList.size()-1){
            View layout = inflater.inflate(R.layout.group_list_last_item_layout, parent, false);

            TextView group_list_add_button = (TextView) layout.findViewById(R.id.group_list_add_button);

            return layout;
        }else{
            View layout = inflater.inflate(R.layout.group_list_item_layout, parent, false);


            TextView group_list_name = (TextView) layout.findViewById(R.id.group_list_name);
            TextView group_list_owner_name = (TextView) layout.findViewById(R.id.group_list_owner_name);
            TextView group_list_uid = (TextView) layout.findViewById(R.id.group_list_uid);

            Group group = mList.get(position);
            group_list_name.setText(group.getGroup_name());
            group_list_owner_name.setText(group.getOwner_name());
            group_list_uid.setText(group.getOwner_id());


            return layout;
        }


    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return getCustomView(position, parent);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.group_list_item_layout, parent, false);

        TextView group_list_name = (TextView) layout.findViewById(R.id.group_list_name);
        TextView group_list_owner_name = (TextView) layout.findViewById(R.id.group_list_owner_name);
        TextView group_list_uid = (TextView) layout.findViewById(R.id.group_list_uid);

        Group group = mList.get(position);
        group_list_name.setText(group.getGroup_name());
        group_list_owner_name.setText(group.getOwner_name());
        group_list_uid.setVisibility(View.GONE);
//        group_list_uid.setText(group.getOwner_id());

        return layout;
    }

    public int getIDPosition(String uid){
        for(int index=0;index<mList.size();index++){
            if(mList.get(index).getGroup_id().equalsIgnoreCase(uid)){
                return index;
            }
        }
        return -1;
    }

    public int getItemCount(){
        return mList.size();
    }

    @Override
    public Group getItem(int position) {
        return mList.get(position);
    }
}
