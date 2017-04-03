package com.tarunisrani.dailykharcha.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.UserListClickListener;
import com.tarunisrani.dailykharcha.model.UserDetails;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {


    public static final int REMOVE_OPERATION = 100;
    public static final int UNSHARE_OPERATION = 200;

    private ArrayList<UserDetails> mList = new ArrayList<>();
    private ArrayList<String> mSharedWith = new ArrayList<>();
    private UserListClickListener mListener;

    public void setClickListener(UserListClickListener listener){
        this.mListener = listener;
    }

    public void addUserDetail(UserDetails expense){
        mList.add(expense);
    }

    public void setUserDetailList(ArrayList<UserDetails> list){
        mList = list;
    }

    public void setmSharedWith(ArrayList<String> mSharedWith) {
        this.mSharedWith = mSharedWith;
    }

    public ArrayList<UserDetails> getUserDetailList(){
         return mList;
    }

    public UserDetails getItem(int position){
        return mList.get(position);
    }

    public UserDetails removeItem(int position){
        return mList.remove(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserDetails userDetails = mList.get(position);

        holder.setDetails(userDetails, mSharedWith.contains(userDetails.getUid()));
        /*if(mSharedWith.contains(userDetails.getUid())){
            holder.itemView.setBackgroundColor(Color.CYAN);
        }*/

        holder.user_list_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onUserItemClick(position, REMOVE_OPERATION);
                }
            }
        });
        holder.user_list_unshare_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onUserItemClick(position, UNSHARE_OPERATION);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public void setDetails(UserDetails userDetails, boolean alreadyShared){
            this.mUserDetails = userDetails;
            this.user_list_name.setText(userDetails.getUser_name());
            this.user_list_uid.setText(userDetails.getUid());
            this.user_list_unshare_button.setVisibility(alreadyShared?View.VISIBLE:View.GONE);
            this.user_list_remove_button.setVisibility(alreadyShared?View.GONE:View.VISIBLE);
        }

        public void setCheckmarkEnable(boolean enable){
            user_list_unshare_button.setVisibility(enable?View.VISIBLE:View.GONE);
        }

        public boolean getCheckmarkEnable(){
            return user_list_unshare_button.getVisibility() == View.VISIBLE;
        }

        public UserDetails getUserDetails(){
            return this.mUserDetails;
        }

        private TextView user_list_name;
        private TextView user_list_uid;
        private TextView user_list_unshare_button;
        private TextView user_list_remove_button;
        private UserDetails mUserDetails;

        public ViewHolder(View itemView) {
           super(itemView);
            this.user_list_name = (TextView) itemView.findViewById(R.id.user_list_name);
            this.user_list_uid = (TextView) itemView.findViewById(R.id.user_list_uid);
            this.user_list_unshare_button = (TextView) itemView.findViewById(R.id.user_list_unshare_button);
            this.user_list_remove_button = (TextView) itemView.findViewById(R.id.user_list_remove_button);

        }
    }
}
