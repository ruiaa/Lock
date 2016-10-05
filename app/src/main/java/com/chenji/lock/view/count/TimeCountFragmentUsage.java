package com.chenji.lock.view.count;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chenji.lock.R;
import com.chenji.lock.Util.MyApplication;
import com.chenji.lock.Util.Util;
import com.chenji.lock.model.SqlInfo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TimeCountFragmentUsage extends Fragment {

    private String packageName;
    private float max=0;

    public TimeCountFragmentUsage() {
        // Required empty public constructor
    }

    public static TimeCountFragmentUsage newInstance(String packageName) {
        TimeCountFragmentUsage fragment = new TimeCountFragmentUsage();
        Bundle args = new Bundle();
        args.putString(SqlInfo.PACKAGE, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName = getArguments().getString(SqlInfo.PACKAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_count_usage, container, false);
        //显示图标
        ImageView appIcon = (ImageView) view.findViewById(R.id.count_usage_icon);
        appIcon.setImageDrawable(MyApplication.dataResolver.getIcon(packageName));


        ArrayList<HashMap<String, Float>> arrayList = MyApplication.dataResolver.getTheAppUsage(packageName);

        //条形统计图
        BarChart barChart = (BarChart) view.findViewById(R.id.count_usage_chart);
        showBarChart(barChart,getBarData(arrayList) );
        barChart.moveViewToX(arrayList.size()-1);

       /* //折线统计图
        LineChart lineChart=(LineChart)view.findViewById(R.id.count_usage_chart);
        showLineChart(lineChart,getLineData(arrayList));
*/
        return view;
    }




    private void showBarChart(BarChart barChart, BarData barData) {

        barChart.setDescription("");// 数据描述

        // 如果没有数据的时候,会显示这个,类似ListView的EmptyView
        //barChart.setNoDataTextDescription("You need to provide data for the chart.");



        barChart.setPinchZoom(true);//





//整体
        barChart.setData(barData); // 设置数据
        barChart.setVisibleXRangeMaximum(7);
        barChart.animateX(1000); // 立即执行的动画,x轴
        barChart.setDrawBarShadow(false);//设置条形背景
//      barChart.setBackgroundColor();// 设置背景
        barChart.setDrawBorders(false);  ////是否在折线图上添加边框
        //barChart.setDrawGridBackground(true); // 是否显示表格颜色
        //barChart.setGridBackgroundColor(Color.RED); // 表格的的颜色，在这里是是给颜色设置一个透明度


//监听
        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setDragDecelerationEnabled(true);//手指滑动抛掷图表后继续减速滚动


//样式


//图例
        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        //mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(0f);// 字体
        //mLegend.setTextColor(Color.BLACK);// 颜色


//x轴设定
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(Util.getDimen(R.dimen.text_sss));
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

//y轴  左边设定
        YAxis leftAxis=barChart.getAxisLeft();
        leftAxis.setZeroLineColor(R.color.text_red);
        leftAxis.setAxisLineColor(R.color.text_red);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Util.getColor(R.color.text_red));
        leftAxis.setGridLineWidth(1.2f);
        leftAxis.setZeroLineWidth(1.2f);
        leftAxis.setAxisLineWidth(1.2f);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(Util.getDimen(R.dimen.text_sss));
        leftAxis.setSpaceBottom(0);
        leftAxis.setLabelCount(7, true);
        leftAxis.setAxisMinValue(0);

        if (getMax(max)<6){
            leftAxis.setAxisMaxValue(getMax(max));
            leftAxis.setValueFormatter(new MyYAxisMinuteFormatter());
        }else {
            leftAxis.setAxisMaxValue(getMax(max));
            leftAxis.setValueFormatter(new MyYAxisValueFormatter());
        }


//y轴  右边设定
        YAxis rightAxis=barChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setZeroLineColor(R.color.text_red);
        rightAxis.setAxisLineColor(R.color.text_red);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setDrawLabels(false);

    }


    private BarData getBarData(ArrayList<HashMap<String, Float>> arrayList) {
        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<BarEntry> yValues = new ArrayList<>();
        int i = 0;
        if (!arrayList.isEmpty()) {
            while (i<7-arrayList.size()){
                xValues.add("");
                yValues.add(new BarEntry(0, i));
                i = i + 1;
            }
            for (HashMap<String, Float> hashMap : arrayList) {
                for (Map.Entry<String, Float> entry : hashMap.entrySet()) {
                    xValues.add(entry.getKey());
                    yValues.add(new BarEntry(entry.getValue(), i));
                    i = i + 1;
                    if (max<entry.getValue()){
                        max=entry.getValue();
                    }
                }
            }

          /*  while (i<15){
                i=i+1;
                xValues.add("34/"+i);
                yValues.add(new BarEntry(i, i));
            }*/
        }

        BarDataSet barDataSet = new BarDataSet(yValues, null);
        barDataSet.setColor(Util.getColor(R.color.use_time_image));
        barDataSet.setValueTextSize(Util.getDimen(R.dimen.text_sss));
        if (getMax(max)<6){
            barDataSet.setValueFormatter(new MyValueMinuteFormatter());
        }else {
            barDataSet.setValueFormatter(new MyValueFormatter());
        }


        return new BarData(xValues, barDataSet);
    }


    private int getMax(float max){
        if (max<0.9){
            return 1;
        }else if (max<1.8){
            return 2;
        }else if (max<5.5){
            return 6;
        }else if (max<11.5){
            return 12;
        }else if (max<17.5){
            return 18;
        }else if (max<24){
            return 24;
        }else {
            return 24;
        }
    }

    private class MyYAxisMinuteFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisMinuteFormatter () {
            mFormat = new DecimalFormat("#0.#"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return mFormat.format(value*60) + Util.getString(R.string.minute); // e.g. append a dollar-sign
        }
    }
    public class MyValueMinuteFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueMinuteFormatter() {
            mFormat = new DecimalFormat("##0.#"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value*60); // e.g. append a dollar-sign
        }
    }

    private class MyYAxisValueFormatter implements YAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyYAxisValueFormatter () {
            mFormat = new DecimalFormat("#0.#"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            // write your logic here
            // access the YAxis object to get more information
            return mFormat.format(value) + Util.getString(R.string.hour); // e.g. append a dollar-sign
        }
    }

    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("##0.#"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value); // e.g. append a dollar-sign
        }
    }

    {
     /*   private void showLineChart(LineChart lineChart, LineData lineData) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        lineChart.setDescription("");// 数据描述

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        //lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        //lineChart.setDrawGridBackground(true); // 是否显示表格颜色
        //lineChart.setGridBackgroundColor(Color.RED); // 表格的的颜色，在这里是是给颜色设置一个透明度

        // enable touch gestures
        lineChart.setTouchEnabled(false); // 设置是否可以触摸

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);//

        //lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineChart.setData(lineData); // 设置数据

        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        // mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(12f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
//      mLegend.setTypeface(mTf);// 字体

        lineChart.animateX(500); // 立即执行的动画,x轴
    }

    private LineData getLineData(ArrayList<HashMap<String, Float>> arrayList) {

        // x轴y轴的数据
        List<String> xValues = new ArrayList<String>();
        List<Entry> yValues = new ArrayList<Entry>();
        int i = 0;
        if (!arrayList.isEmpty()) {
            for (HashMap<String, Float> hashMap : arrayList) {
                for (Map.Entry<String, Float> entry : hashMap.entrySet()) {
                    xValues.add(entry.getKey());
                    yValues.add(new Entry(entry.getValue(), i));
                    i = i + 1;
                }
            }
        }


        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, null);//显示在比例图上"测试折线图"
        // mLineDataSet.setFillAlpha(110);
        // mLineDataSet.setFillColor(Color.RED);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleRadius(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        // create a data object with the datasets
        return new LineData(xValues, lineDataSet);
    }*/

    }
}
