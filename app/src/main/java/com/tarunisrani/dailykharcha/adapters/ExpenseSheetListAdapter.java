package com.tarunisrani.dailykharcha.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.ExpenseSheetListClickListener;
import com.tarunisrani.dailykharcha.model.Sheet;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 12/22/16.
 */
public class ExpenseSheetListAdapter extends RecyclerView.Adapter<ExpenseSheetListAdapter.ViewHolder> {

    public static final int MENU_OPTION_RENAME = 0;
    public static final int MENU_OPTION_REMOVE = 1;
    public static final int MENU_OPTION_SHARE = 2;
    public static final int MENU_OPTION_ANALYZE = 3;

    private ArrayList<Sheet> mList = new ArrayList<>();
    private ExpenseSheetListClickListener mListener;
    private Context mContext;

    public void setClickListener(ExpenseSheetListClickListener listener){
        this.mListener = listener;
    }

    public void addSheet(Sheet expense){
        mList.add(expense);
    }

    public void setSheetList(ArrayList<Sheet> list){
        mList = list;
    }

    public Sheet getItem(int position){
        return mList.get(position);
    }

    public Sheet removeItem(int position){
        return mList.remove(position);
    }

    public ExpenseSheetListAdapter(Context context){
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_sheet_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Sheet sheet = mList.get(position);
        holder.setDetails(sheet);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongClick(position, holder);
                }
                return true;
            }
        });

        holder.view_flipper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongClick(position, holder);
                }
                return true;
            }
        });


        holder.cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancelClick(position, holder);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onItemClick(position, holder.itemView);
                }
            }
        });

        holder.view_flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position, holder.itemView);
                }
            }
        });

        holder.ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onOkClick(position, holder.sheet_name_edittext.getText().toString(), holder);
                }
            }
        });

        holder.cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null) {
                    mListener.onCancelClick(position, holder);
                }
            }
        });

        holder.option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.option_button);
                //inflating menu from xml resource
                popup.inflate(R.menu.option_menu_1);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_RENAME, holder);
                                }
                                break;
                            case R.id.menu2:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_REMOVE, holder);
                                }
                                break;
                            case R.id.menu3:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_SHARE, holder);
                                }
                                break;
                            case R.id.menu4:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_ANALYZE, holder);
                                }
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public void setDetails(Sheet sheet){
            this.sheet_name_edittext.setText(sheet.getSheet_name());
            this.sheet_name_label.setText(sheet.getSheet_name());
            this.date_value.setText(sheet.getSheet_creation_date());
            this.amount_value.setText("Rs. "+String.valueOf(sheet.getAmount()));

        }

        private EditText sheet_name_edittext;
        private TextView sheet_name_label;
        private ViewFlipper view_flipper;
        private ImageView ok_button;
        private ImageView cancel_button;
        private TextView option_button;
        private TextView date_value;
        private TextView amount_value;

        public void hideControlPanel(){
            this.view_flipper.showNext();
//            this.sheet_name_edittext.setEnabled(false);
//            this.sheet_name_edittext.setLongClickable(true);
//            this.sheet_name_edittext.setClickable(true);
            this.ok_button.setVisibility(View.GONE);
            this.cancel_button.setVisibility(View.GONE);
        }

        public void setSheetName(String name){
            this.sheet_name_edittext.setText(name);
            this.sheet_name_label.setText(name);
        }

        public void showControlPanel(){
            this.view_flipper.showPrevious();
//            this.sheet_name_edittext.setEnabled(true);
            this.ok_button.setVisibility(View.VISIBLE);
            this.cancel_button.setVisibility(View.VISIBLE);
        }

        public boolean isEditEnabled(){
            return this.sheet_name_edittext.isEnabled() &&
                    (this.ok_button.getVisibility() == View.VISIBLE) && (this.cancel_button.getVisibility() == View.VISIBLE);
        }

        public ViewHolder(View itemView) {
           super(itemView);
            this.sheet_name_edittext = (EditText) itemView.findViewById(R.id.expense_sheet_item_name_edittext);
            this.sheet_name_label = (TextView) itemView.findViewById(R.id.expense_sheet_item_name_label);
            this.view_flipper = (ViewFlipper) itemView.findViewById(R.id.expense_sheet_item_view_flipper);
            this.ok_button = (ImageView) itemView.findViewById(R.id.expense_sheet_item_ok_button);
            this.cancel_button = (ImageView) itemView.findViewById(R.id.expense_sheet_item_cancel_button);
            this.option_button = (TextView) itemView.findViewById(R.id.expense_sheet_item_options);
            this.date_value = (TextView) itemView.findViewById(R.id.expense_sheet_item_date_value);
            this.amount_value = (TextView) itemView.findViewById(R.id.expense_sheet_item_amount_value);

            this.sheet_name_edittext.setBackground(null);

            hideControlPanel();
        }
    }
}
