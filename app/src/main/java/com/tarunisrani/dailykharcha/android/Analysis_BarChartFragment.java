package com.tarunisrani.dailykharcha.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.model.Category;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 4/6/16.
 */
public class Analysis_BarChartFragment extends Fragment {


    final int coloList[] = {Color.RED, Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN};

    private BarChart analysis_barchart;
    private ArrayList<Category> categoryArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analysis_barchart_layout, container, false);

        analysis_barchart = (BarChart)view.findViewById(R.id.analysis_barchart);

        generateView();

        return view;
    }

    public void setPieData(ArrayList<Category> categoryArrayList){
        this.categoryArrayList = categoryArrayList;
    }

    public void generateView(){

        ArrayList<BarEntry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();


        int counter = 0;
        for (Category category: categoryArrayList){
            yValues.add(new BarEntry(counter+1, category.getCategory_value(), category.getCategory_name()));
            xValues.add(category.getCategory_name());
            colors.add(coloList[counter%coloList.length]);
            counter++;
        }

        BarDataSet dataSet = new BarDataSet(yValues, "Expenditure Details");

        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
//        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        analysis_barchart.setData(data);

        analysis_barchart.getLegend().setEnabled(true);
        analysis_barchart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        analysis_barchart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        analysis_barchart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        analysis_barchart.setDescription(null);
        analysis_barchart.invalidate();
    }


}
