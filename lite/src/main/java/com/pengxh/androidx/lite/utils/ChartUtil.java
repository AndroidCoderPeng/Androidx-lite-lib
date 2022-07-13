package com.pengxh.androidx.lite.utils;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pengxh.androidx.lite.R;

import java.util.List;

/**
 * MPAndroidChart初始化类
 */
public class ChartUtil {

    public static void initLineChart(Context context, LineChart chart) {
        chart.setNoDataText("无数据，无法渲染...");
        chart.setNoDataTextColor(R.color.red);
        chart.getPaint(Chart.PAINT_INFO).setTextSize(DeviceSizeUtil.sp2px(context, 14f));
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.animateY(1200, Easing.EaseInOutQuad);
        //设置样式
        YAxis rightAxis = chart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.setScaleXEnabled(true); //X轴可缩放
        chart.setScaleYEnabled(false); //Y轴不可缩放
        //设置x轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(ColorUtil.convertColor(context, R.color.lib_text_color));
        xAxis.setTextSize(DeviceSizeUtil.sp2px(context, 10f));
        xAxis.setLabelCount(7, true);
        xAxis.setDrawLabels(true); //绘制标签  指x轴上的对应数值
        xAxis.setDrawAxisLine(true); //是否绘制轴线
        xAxis.setDrawGridLines(false); //设置x轴上每个点对应的线
        xAxis.setGranularity(1f); //禁止放大后x轴标签重绘
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setExtraBottomOffset(5f);  //解决X轴显示不完全问题
        //去掉描述
        chart.getDescription().setEnabled(false);
        //设置图例
        setChartLegend(chart.getLegend());
    }

    public static void initPieChart(Context context, PieChart chart) {
        chart.setNoDataText("无数据，无法渲染...");
        chart.setNoDataTextColor(R.color.red);
        chart.getPaint(Chart.PAINT_INFO).setTextSize(DeviceSizeUtil.sp2px(context, 14f));
        chart.setUsePercentValues(false); //百分比数字显示
        chart.getDescription().setEnabled(false);
        chart.setDragDecelerationFrictionCoef(0.95f); //图表转动阻力摩擦系数[0,1]
        chart.setBackgroundColor(Color.WHITE); //设置图表背景色
        chart.setRotationAngle(0f);
        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.animateY(1200, Easing.EaseInOutQuad); // 设置图表展示动画效果
        chart.setDrawEntryLabels(false); //不显示分类标签
        chart.setDrawHoleEnabled(false); //圆环显示
        chart.setDrawCenterText(false); //圆环中心文字
        chart.setEntryLabelColor(R.color.blue); //图表文本字体颜色
        chart.setEntryLabelTextSize(12f);
        //设置图表上下左右的偏移，类似于外边距，可以控制饼图大小
        chart.setExtraOffsets(7.5f, 2.5f, 7.5f, 2.5f);
        //设置图例位置
        setChartLegend(chart.getLegend());
    }

    public static void initBarChart(Context context, BarChart chart, List<String> barLabels) {
        chart.setNoDataText("无数据，无法渲染...");
        chart.setNoDataTextColor(R.color.red);
        if (barLabels.isEmpty()) {
            chart.clearValues();
            return;
        }
        chart.getPaint(Chart.PAINT_INFO).setTextSize(DeviceSizeUtil.sp2px(context, 14f));
        chart.animateY(1200, Easing.EaseInOutQuad);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setScaleEnabled(false);
        //去掉描述
        chart.getDescription().setEnabled(false);
        //去掉图例
        chart.getLegend().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(ColorUtil.convertColor(context, R.color.lib_text_color));
        xAxis.setDrawLabels(true); //绘制标签  指x轴上的对应数值
        xAxis.setLabelCount(barLabels.size()); // 设置x轴上的标签个数
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return barLabels.get((int) value);
            }
        });
        xAxis.setDrawAxisLine(true); //是否绘制轴线
        xAxis.setDrawGridLines(false); //设置x轴上每个点对应的线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-45); //X轴标签斜45度
        chart.setExtraRightOffset(5f); //解决X轴显示不完全问题
        //设置样式
        YAxis rightAxis = chart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
    }

    public static void initHorizontalBarChart(Context context, HorizontalBarChart chart, List<String> barLabels) {
        chart.setNoDataText("无数据，无法渲染...");
        chart.setNoDataTextColor(R.color.red);
        if (barLabels.isEmpty()) {
            chart.clearValues();
            return;
        }
        chart.getPaint(Chart.PAINT_INFO).setTextSize(DeviceSizeUtil.sp2px(context, 14f));
        chart.animateY(1200, Easing.EaseInOutQuad);
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setScaleEnabled(false);
        //去掉描述
        chart.getDescription().setEnabled(false);
        //去掉图例
        chart.getLegend().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(ColorUtil.convertColor(context, R.color.lib_text_color));
        xAxis.setDrawLabels(true); //绘制标签  指x轴上的对应数值
        xAxis.setLabelCount(barLabels.size()); // 设置x轴上的标签个数
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return barLabels.get((int) value);
            }
        });
        xAxis.setDrawAxisLine(true); //是否绘制轴线
        xAxis.setDrawGridLines(false); //设置x轴上每个点对应的线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setExtraRightOffset(5f); //解决X轴显示不完全问题
        //设置样式
        YAxis rightAxis = chart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
    }

    private static void setChartLegend(Legend legend) {
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //图例是否自动换行
        legend.setWordWrapEnabled(true);
    }
}
