package com.tarunisrani.dailykharcha.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.tarunisrani.dailykharcha.R;
import com.tarunisrani.dailykharcha.model.Category;

import java.util.ArrayList;

/**
 * Created by tarunisrani on 4/6/16.
 */
public class Analysis_PieChartFragment extends Fragment {


    final int coloList[] = {Color.RED, Color.BLUE, Color.GREEN, Color.GRAY, Color.CYAN};

    private PieChart analysis_piechart;
    private ArrayList<Category> categoryArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.analysis_piechart_layout, container, false);

        analysis_piechart = (PieChart)view.findViewById(R.id.analysis_piechart);

        generateView();

        return view;
    }

    public void setPieData(ArrayList<Category> categoryArrayList){
        this.categoryArrayList = categoryArrayList;
    }

    public void generateView(){

        ArrayList<PieEntry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();


        int counter = 0;
        for (Category category: categoryArrayList){
            yValues.add(new PieEntry(category.getCategory_value(), category.getCategory_name()));
            xValues.add(category.getCategory_name());
            colors.add(coloList[counter%coloList.length]);
            counter++;
        }

        PieDataSet dataSet = new PieDataSet(yValues, "Expenditure Details");
        dataSet.setSliceSpace(0f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
//        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        analysis_piechart.setData(data);

        analysis_piechart.getLegend().setEnabled(true);
        analysis_piechart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        analysis_piechart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        analysis_piechart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        analysis_piechart.setDescription(null);
        analysis_piechart.setCenterText("Expenditure Details");
        analysis_piechart.invalidate();
    }


}
