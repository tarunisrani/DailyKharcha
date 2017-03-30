package com.tarunisrani.dailykharcha.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.adapters.CustomPagerAdapter;
import com.tarunisrani.dailykharcha.dbhelper.ExpenseDataSource;
import com.tarunisrani.dailykharcha.model.Category;
import com.tarunisrani.dailykharcha.model.Expense;
import com.tarunisrani.dailykharcha.model.Sheet;

import java.util.ArrayList;

import static com.tarunisrani.dailykharcha.utils.AppConstant.INTENT_KEY_SHEET;

public class AnalyseSheetActivity extends FragmentActivity implements TabLayout.OnTabSelectedListener {

    private ViewPager mViewPager;

    private Analysis_PieChartFragment  pieChartFragment = new Analysis_PieChartFragment();
    private Analysis_BarChartFragment  barChartFragment = new Analysis_BarChartFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_layout);

        mViewPager = (ViewPager) findViewById(R.id.analysis_viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.analysis_tabLayout);


        Intent intent = getIntent();
        Sheet sheet = null;

        if(intent != null){
            sheet = intent.getParcelableExtra(INTENT_KEY_SHEET);
        }

        setPieData(sheet);

        CustomPagerAdapter adapter = new CustomPagerAdapter(getSupportFragmentManager());

        adapter.addItem(pieChartFragment, "Pie Chart");
        adapter.addItem(barChartFragment, "Bar Chart");

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2);

        tabLayout.setOnTabSelectedListener(this);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private ArrayList<Expense> fetchSheetDetail(Sheet sheet){
        ExpenseDataSource expenseDataSource = new ExpenseDataSource(this);
        ArrayList<Expense> prepopulated_expenses_list = expenseDataSource.getExpenseItems(sheet.getSheet_id());

        return prepopulated_expenses_list;

    }

    private ArrayList<Category> calculateCategorywiseSplit(ArrayList<Expense> expenseArrayList){
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        float total = 0.0f;
        for(Expense expense: expenseArrayList){
            total += (float)expense.getAmount();
            int index = categoryArrayList.indexOf(expense.getExpense_type());
            if(index !=-1){
                Category category = categoryArrayList.get(index);
                category.addValue((float)expense.getAmount());
            }else{
                categoryArrayList.add(new Category(expense.getExpense_type(), (float)expense.getAmount()));
            }
        }

        for (Category category: categoryArrayList){
            category.setCategory_percentace((category.getCategory_value()/total)*100.0f);
        }


        return categoryArrayList;
    }


    private void setPieData(Sheet sheet){

        ArrayList<Expense> prepopulated_expenses_list = fetchSheetDetail(sheet);
        ArrayList<Category> categoryArrayList = calculateCategorywiseSplit(prepopulated_expenses_list);

        pieChartFragment.setPieData(categoryArrayList);
        barChartFragment.setPieData(categoryArrayList);


    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}


