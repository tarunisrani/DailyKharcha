package com.tarunisrani.dailykharcha.customui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.AddGroupListener;
import com.tarunisrani.dailykharcha.model.Group;
import com.tarunisrani.dailykharcha.utils.SharedPreferrenceUtil;

/**
 * Created by tarunisrani on 2/21/17.
 */

public class AddGroupDialog extends Dialog implements View.OnClickListener {

    private AddGroupListener mListener;
    private EditText add_group_name_input;

    public AddGroupDialog(Context context, AddGroupListener listener) {
        super(context);
        this.mListener = listener;
    }

    protected AddGroupDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public AddGroupDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgroup_layout);

        add_group_name_input = (EditText) findViewById(R.id.add_group_name_input);
        TextView button_add = (TextView) findViewById(R.id.add_group_button_add);
        TextView button_cancel = (TextView) findViewById(R.id.add_group_button_cancel);

        button_add.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

    }

    private void performAddOperation(){

        String group_name = add_group_name_input.getText().toString();

        if(!group_name.isEmpty()){
            String user_id = new SharedPreferrenceUtil().fetchUserID(getContext());
            String user_name = new SharedPreferrenceUtil().fetchUserName(getContext());
            Group group = new Group();
            group.setOwner_id(user_id);
            group.setOwner_name(user_name);
            group.setGroup_id(user_id+"_"+group_name);
            group.setGroup_name(group_name);
            if(mListener!=null){
                mListener.onGroupAdded(group);
            }
            dismiss();
        }
    }

    private void performCancelOperation(){
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_group_button_add:
                performAddOperation();
                break;
            case R.id.add_group_button_cancel:
                performCancelOperation();
                break;
        }
    }

}
