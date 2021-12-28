package com.example.mybluetooth.linechart;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.example.mybluetooth.R;
import com.example.mybluetooth.util.LogUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**曲线图
 * @description
 * @param
 * @return
 * @author lutong
 * @time 2021/9/9 11:16
 */


public class ChartView {
    public static LineChart chart;
    public static XAxis xAxis; //x轴
    public static YAxis leftYAxis; //y轴
    public static YAxis rightYaxis;
    public static double num_max = 1000;
    public static double num_min = 0;

    public static List<Bean> list0 = new ArrayList<>();
    public static List<Bean> list1 = new ArrayList<>();
    public static List<Bean> list2 = new ArrayList<>();
    public static List<Bean> list00 = new ArrayList<>();
    public static List<Bean> curList = new ArrayList<>();


    /**
     * @description
     * @param chart 曲线图控件
     * @param context activity
     * @return
     * @author lutong
     * @time 2021/9/9 15:14
     */

    public static void initData(LineChart chart,Context context) {
        ChartView.chart =chart;
        //0
        for (int i = 0; i < 10; i++) {
            list0.add(new Bean("", 0));
        }//0
        for (int i = 0; i < 30; i++) {
            list1.add(new Bean("", 0));
        }//0
        for (int i = 0; i < 30; i++) {
            list2.add(new Bean("", 0));
        }

        //当无手机卡时候改为默认值
        for (int i = 0; i < 30; i++) {
            list00.add(new Bean("", 0));
        }

        initChart(context);
        curList.clear();
        curList.addAll(list0);
        if (curList != null && curList.size() > 0) {
            showLineChart(curList, "", context.getResources().getColor(R.color.color_3853e8));
        }
    }

    public static void clearList(Context mContext){
        ChartView.initChart(mContext);
        for (Bean bean : list0) {
            bean.setValue(0.0f);
        }
        if (ChartView.list0 != null && ChartView.list0.size() > 0) {
            ChartView.showLineChart(ChartView.list0, "", mContext.getResources().getColor(R.color.color_3853e8));
        }
    }
    private static String TAG="CHARTView";
    /**
     * 展示曲线
     *
     * @param dataList 数据集合
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public static void showLineChart(final List<Bean> dataList, String name, int color) {
//        Log.e(TAG, "showLineChart: "+dataList.toString());
        //自定义x轴显示元素
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int position = (int) value;
//                return getTime((int) value);
                if (position >= 0 && position < dataList.size()) {
                    return dataList.get(position).data;
                } else {
                    return "0";
                }
            }
        });
//        //自定义y轴显示元素 ，
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "";
            }
        });

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Bean data = dataList.get(i);
            Entry entry = new Entry(i, data.value);
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        //设置线的属性
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.HORIZONTAL_BEZIER);
        //设置数据
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
    }


    /**
     * 添加曲线
     */
    public static void addLine(List<Bean> dataList, String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Bean data = dataList.get(i);
            Entry entry = new Entry(i, data.value);
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.HORIZONTAL_BEZIER);
        chart.getLineData().addDataSet(lineDataSet);
        chart.invalidate();
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private static void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {


        //设置折线的属性
//      lineDataSet.setMode(LineDataSet.Mode.STEPPED); 线的样式  HORIZONTAL_BEZIER波浪线
//      lineDataSet.setHighlightEnabled(true);//设置是否显示十字线
        lineDataSet.setColor(color);        //设置线条颜色
        lineDataSet.setValueTextSize(10f);   //设置 DataSets 数据对象包含的数据的值文本的大小（单位是dp）。

        //设置折线图填充
        lineDataSet.setDrawFilled(true);//Fill填充，可以将折线图以下部分用颜色填满
        lineDataSet.setFillColor(color);//折线图填充的背景颜色
        lineDataSet.setFillAlpha(70);//折线图填充的颜色深度 默认值为85
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setLineWidth(3f);//折线的宽度
        lineDataSet.setFormSize(15.f);


        //显示点
        lineDataSet.setDrawCircleHole(true);//是否定制节点圆心的颜色，若为false，则节点为单一的同色点，若为true则可以设置节点圆心的颜色
        lineDataSet.setDrawCircles(true);  //设置是否显示节点的小圆点
        //内圈半径
        lineDataSet.setCircleHoleRadius(1.5f);
        lineDataSet.setCircleColorHole(Color.WHITE);
        // 内圈的颜色
        lineDataSet.setCircleColor(Color.BLUE);  //设置节点的圆圈颜色
        lineDataSet.setCircleRadius(4f);//设置每个坐标点的圆大小 外圈半径

        //显示内容 设置是否显示节点的值
        lineDataSet.setDrawValues(true);
        //自定义显示的点的内容
        lineDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                DecimalFormat df = new DecimalFormat(".00");
//                return df.format(value * 100) + "%";
                return (int) value + "";
            }
        });
        LineData data = new LineData(lineDataSet);

        data.setValueTextColor(Color.BLUE); //设置节点文字颜色
        data.setValueTextSize(10f);
//        if (mode == null) {
//            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
//            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        } else {
//            lineDataSet.setMode(mode);
//        }

    }

    public static void initChart(Context context) {
        //是否展示网格线
        chart.setDrawGridBackground(false);
        //是否显示边界
        chart.setDrawBorders(false);
        //是否可以拖动
        chart.setDragEnabled(false);
        // 可缩放
        chart.setScaleEnabled(false);
        //是否有触摸事件
        chart.setTouchEnabled(false);
        //设置滚动时的速度快慢
//        chart.setDragDecelerationFrictionCoef(0.9f);
        //两指放大缩小
        chart.setPinchZoom(false);
//        chart.setScaleXEnabled(false);   //设置X轴是否能够放大
//        chart.setScaleYEnabled(false);  //设置Y轴是否能够放大
        //chart.setScaleEnabled(true);    // 是否可以缩放 x和y轴, 默认是true


        //x轴动画
        chart.animateX(0);//设置为0代表没有闪屏状态
        //无数据时展示
        chart.setNoDataText("暂无数据");
        //背景色
//        chart.setBackgroundColor(context.getResources().getColor(R.color.white));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            chart.setOutlineSpotShadowColor(context.getResources().getColor(R.color.color_3853e8));
        }

        //表格右下角的小标签描述隐藏
        Description description = new Description();
        description.setText("标识");
        description.setTextColor(Color.BLUE);
        description.setEnabled(false);//隐藏
        chart.setDescription(description);

        xAxis = chart.getXAxis(); //x轴
        leftYAxis = chart.getAxisLeft(); //y轴
        rightYaxis = chart.getAxisRight();






//        xAxis.setTextSize(13);
//        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setDrawGridLines(false); //是否显示网格
        xAxis.setAxisLineColor(Color.parseColor("#00ffffff"));
//        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//将坐标值设置在底部
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1f); //设置是否一个格子显示一条数据，如果不设置这个属性，就会导致X轴数据重复并且错乱的问题



        //设置X轴分割数量
//        xAxis.setLabelCount(6, false);

        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisLineColor(Color.parseColor("#F0F0F0"));// Y坐标轴颜色，设置为透明
//        leftYAxis.setTextColor(Color.parseColor("#00ffffff")); //坐标轴数值
        leftYAxis.setGridColor(Color.parseColor("#F0F0F0"));    // 横着的 网格线颜色，默认GRAY
//        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//-30 -120
        leftYAxis.setGridLineWidth(1);//设置y轴对应的线宽
        leftYAxis.setDrawGridLines(false);//坐标线 网格线
        leftYAxis.setLabelCount(10); //设置y轴分割数量
        leftYAxis.setAxisMaximum((float) num_max);

        leftYAxis.setAxisMinimum((float) num_min);

        leftYAxis.setInverted(false);//如果设置为true，该轴将被反转，这意味着最高值将在底部，顶部的最低值。
//        leftYAxis.setAxisMaxValue(220f);// 设置y轴最大值
//        leftYAxis.setAxisMinValue(40f);//设置y轴最小值
        // 不一定要从0开始
//        leftYAxis.setStartAtZero(false);

        //去除右侧Y轴
        rightYaxis.setEnabled(false);
        rightYaxis.setAxisMinimum(0f);
        rightYaxis.setDrawGridLines(false);

        /***折线图例 标签 设置***/
        Legend legend = chart.getLegend(); //图例
        // 设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm
        legend.setEnabled(false);// 是否绘制图例
        legend.setForm(Legend.LegendForm.LINE); //左下角表格名称前面的小标志

        legend.setTextSize(11f);
        legend.setTextColor(R.color.color_3853e8); //左下角表格名称
        legend.setYEntrySpace(20);  // 设置垂直图例间间距，默认0
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);

    }

}