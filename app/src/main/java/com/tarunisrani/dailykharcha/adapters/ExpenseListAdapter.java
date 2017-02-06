package com.tarunisrani.dailykharcha.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.listeners.ExpenseListClickListener;
import com.tarunisrani.dailykharcha.model.Expense;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by tarunisrani on 12/22/16.
 */
public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder> {

    public static final int MENU_OPTION_EDIT = 0;
    public static final int MENU_OPTION_REMOVE = 1;

    private ArrayList<Expense> mList = new ArrayList<>();
    private ExpenseListClickListener mListener;
    private Context mContext;

    public ExpenseListAdapter(Context context){
        mContext = context;
    }

    public void setClickListener(ExpenseListClickListener listener){
        this.mListener = listener;
    }

    public void addExpense(Expense expense){
        mList.add(expense);
    }

    public double updateExpense(Expense expense){
        Expense expense1 = getItemById(expense);
        double amount = 0;
        if(expense1!=null){
            amount = expense1.getAmount();
            expense1.setPayment_type(expense.getPayment_type());
            expense1.setExpense_date(expense.getExpense_date());
            expense1.setExpense_type(expense.getExpense_type());
            expense1.setAmount(expense.getAmount());
            expense1.setExpense_detail(expense.getExpense_detail());
            expense1.setExpense_group(expense.getExpense_group());
            expense1.setSheet_id(expense.getSheet_id());
        }
        return amount;
    }

    private Expense getItemById(Expense expense){
        for (Expense exp: mList){
            if(exp.getId() == expense.getId()){
                return exp;
            }
        }
        return null;
    }

    public void sortList(){
        Collections.sort(mList, new CustomComparator());
    }

    public void setExpenseList(ArrayList<Expense> list){
        mList = list;
    }

    public ArrayList<Expense> getExpenseList(){
         return mList;
    }

    public Expense getItem(int position){
        return mList.get(position);
    }

    public Expense removeItem(int position){
        return mList.remove(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Expense expense = mList.get(position);
        holder.setDetails(expense);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null) {
                    mListener.onLongClick(position, holder);
                }
                return true;
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

        holder.expense_item_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.expense_item_options);
                //inflating menu from xml resource
                popup.inflate(R.menu.option_menu_2);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_EDIT, holder);
                                }
                                break;
                            case R.id.menu2:
                                if(mListener!=null) {
                                    mListener.onMenuClick(position, MENU_OPTION_REMOVE, holder);
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

        public void setDetails(Expense expense){
            this.expense_item_detail.setText(expense.getExpense_detail());
            this.expense_item_date_value.setText(expense.getExpense_date());
            this.expense_item_amount_value.setText(String.valueOf(expense.getAmount()));
        }

        private TextView expense_item_detail;
        private TextView expense_item_options;
        private TextView expense_item_date_value;
        private TextView expense_item_amount_value;

        public ViewHolder(View itemView) {
           super(itemView);
            this.expense_item_detail = (TextView) itemView.findViewById(R.id.expense_item_detail);
            this.expense_item_amount_value = (TextView) itemView.findViewById(R.id.expense_item_amount_value);
            this.expense_item_date_value = (TextView) itemView.findViewById(R.id.expense_item_date_value);
            this.expense_item_options = (TextView) itemView.findViewById(R.id.expense_item_options);

        }
    }

    public class CustomComparator implements Comparator<Expense> {
        @Override
        public int compare(Expense o1, Expense o2) {
            int code = 0;
            try {
                DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
                Date date1 = df.parse(o1.getExpense_date());
                Date date2 = df.parse(o2.getExpense_date());
                code = date1.compareTo(date2);
                if(code == 0){
                    code = o1.getId() - o2.getId();
                }
            } catch (ParseException exp){
                exp.printStackTrace();
            }

            return code;
        }
    }
}
