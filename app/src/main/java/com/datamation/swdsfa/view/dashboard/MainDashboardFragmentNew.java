package com.datamation.swdsfa.view.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.datamation.swdsfa.OtherUploads.UploadSalRef;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.controller.DashboardController;
import com.datamation.swdsfa.controller.FItenrDetController;
import com.datamation.swdsfa.controller.ItemController;
import com.datamation.swdsfa.controller.ItemTarDetController;
import com.datamation.swdsfa.controller.RouteDetController;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.DayTargetD;
import com.datamation.swdsfa.model.FinvDetL3;
import com.datamation.swdsfa.model.Item;
import com.datamation.swdsfa.model.TargetCat;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kaveesha - 23-03-2022
 */
public class MainDashboardFragmentNew extends Fragment {

    private static final String LOG_TAG = MainDashboardFragmentNew.class.getSimpleName();

    private HorizontalBarChart cumulativeLineChart;
    private NumberFormat numberFormat = NumberFormat.getInstance();
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa", Locale.getDefault());

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());

    private ArrayList<Double> targetValues;
    private List<Double> achievementValues;
    String type,category,tranType,itemcode = "";
    int cases,pieces;
    SharedPref pref;
    PieChart pieChart;
    BarChart chart,groupBarChart ;
    BarDataSet barDataSet1, barDataSet2;
    ArrayList<BarEntry> barEntries,barAchieveEntries,barTargetEntries;
    double precentage,target,achievement,TotalTonnage = 0,tonnage;
    String changeDateFrom,changeDateTo;
    ImageView menu;
    ArrayList<String> targetItemList;
    CheckBox chCategoryView;
    RadioGroup radioGroup,radioGroupCat;
    RadioButton rdCase,rdPiece,rdTonnage,rdValue,rdSales,rdInvoice,rdOrderGp,rdInvoiceGp;
    public DatePickerDialog datePickerDialogfrom,datePickerDialogTo;
    DecimalFormat df = new DecimalFormat("####0.00");
    TextView fromDate,toDate,itemName;
    RadioGroup radioGroup1,radioGroup2;
    SearchableSpinner spTargetItem;
    ImageView btnFromDate ,btnToDate;
    LinearLayout row_categorySelection;
    LinearLayout row_transactionSelection;




//    private IOnDashboardFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainDashboardFragment.
     */
    public static MainDashboardFragmentNew newInstance() {
//        MainDashboardFragment fragment = new MainDashboardFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new MainDashboardFragmentNew();
    }

    public MainDashboardFragmentNew() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_dashboard, container, false);

        pref = SharedPref.getInstance(getActivity());

        menu = (ImageView) rootView.findViewById(R.id.menu);
        cumulativeLineChart = (HorizontalBarChart) rootView.findViewById(R.id.dashboard_hBarChart);
        pieChart = rootView.findViewById(R.id.piechart);
        targetValues = new ArrayList<Double>();
        cumulativeLineChart.setDrawGridBackground(false);
        cumulativeLineChart.setPinchZoom(true);

        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(true);

        chart = rootView.findViewById(R.id.barChart);
        groupBarChart = rootView.findViewById(R.id.groupBarChart);
        chCategoryView = rootView.findViewById(R.id.chCategoryWise);
        radioGroup = rootView.findViewById(R.id.groupradio);
        radioGroupCat = rootView.findViewById(R.id.catGroupRadio);
        rdCase = rootView.findViewById(R.id.rdCase);
        rdPiece = rootView.findViewById(R.id.rdPieces);
        rdTonnage = rootView.findViewById(R.id.rdTonnage);
        rdValue = rootView.findViewById(R.id.rdValue);
        rdOrderGp = rootView.findViewById(R.id.rdOrder);
        rdInvoiceGp = rootView.findViewById(R.id.rdInvoice);
        row_categorySelection = rootView.findViewById(R.id.row_categorySelection);
        row_transactionSelection = rootView.findViewById(R.id.row_transactionSelection);

        if(chCategoryView.isChecked()){
            groupBarChart.setVisibility(View.VISIBLE);
            chart.setVisibility(View.GONE);
            row_categorySelection.setVisibility(View.VISIBLE);
            row_transactionSelection.setVisibility(View.VISIBLE);
        }else{
            chart.setVisibility(View.VISIBLE);
            groupBarChart.setVisibility(View.GONE);
            row_categorySelection.setVisibility(View.INVISIBLE);
            row_transactionSelection.setVisibility(View.GONE);
        }

        chCategoryView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chCategoryView.isChecked()){
                    groupBarChart.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                    row_categorySelection.setVisibility(View.VISIBLE);
                    row_transactionSelection.setVisibility(View.VISIBLE);
                }else{
                    chart.setVisibility(View.VISIBLE);
                    groupBarChart.setVisibility(View.GONE);
                    row_categorySelection.setVisibility(View.INVISIBLE);
                    row_transactionSelection.setVisibility(View.GONE);

                }
            }
        });

        if(rdOrderGp.isChecked()){
            tranType = "Order";
        }else if(rdInvoiceGp.isChecked()){
            tranType = "Invoice";
        }

        radioGroupCat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rdOrderGp.isChecked()){
                    tranType = "Order";
                    if(rdCase.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Case",tranType);
                    }else if(rdPiece.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Piece",tranType);
                    }else if(rdTonnage.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Tonnage",tranType);
                    }else if(rdValue.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Value",tranType);
                    }
                }else if(rdInvoiceGp.isChecked()){
                    tranType = "Invoice";
                    if(rdCase.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Case",tranType);
                    }else if(rdPiece.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Piece",tranType);
                    }else if(rdTonnage.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Tonnage",tranType);
                    }else if(rdValue.isChecked()){
                        getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Value",tranType);
                    }
                }
            }
        });


        if(rdCase.isChecked()){
            getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Case",tranType);
        }else if(rdPiece.isChecked()){
            getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Piece",tranType);
        }else if(rdTonnage.isChecked()){
            getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Tonnage",tranType);
        }else if(rdValue.isChecked()){
            getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Value",tranType);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rdCase.isChecked()){
                    getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Case",tranType);
                }else if(rdPiece.isChecked()){
                    getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Piece",tranType);
                }else if(rdTonnage.isChecked()){
                    getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Tonnage",tranType);
                }else if(rdValue.isChecked()){
                    getMonthAchievement(new DashboardController(getActivity()).getTargetCategories(),"Value",tranType);
                }
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TargetDetailDialog(getActivity());
            }
        });

        double dailyAchieve = new DashboardController(getActivity()).getDailyAchievement();
        double monthlyAchieve = new DashboardController(getActivity()).getMonthAchievement();
        double monthlyTarget = new DashboardController(getActivity()).getRepTarget();
        double monthlyBalance = monthlyTarget - monthlyAchieve;
        if(monthlyBalance<0){
            monthlyBalance = 0;
        }
        double dailyTarget = new DashboardController(getActivity()).getRepTarget()/30;
        double monthlyAchieveInv = 0;
        if(!SharedPref.getInstance(getActivity()).getTMInvSale().equals("0")) {
            monthlyAchieveInv = Double.parseDouble(SharedPref.getInstance(getActivity()).getTMInvSale());
        }else{
            monthlyAchieveInv = 0;
        }
        double monthlyReturn = 0;
        if(!SharedPref.getInstance(getActivity()).getTMReturn().equals("0")) {
            monthlyReturn = Double.parseDouble(SharedPref.getInstance(getActivity()).getTMReturn());
        }else{
            monthlyReturn = 0;
        }
        double monthlyBalanceInv = monthlyTarget - monthlyAchieveInv;

        //bar chart

        List<Integer> entries = new ArrayList<>();

        if(monthlyTarget == 0){
            entries.add(0);
        }else{
            entries.add((int)monthlyTarget);
        }

        if(monthlyAchieve == 0){
            entries.add(0);
        }else{
            entries.add((int)monthlyAchieve);
        }

        if(monthlyAchieveInv == 0){
            entries.add(0);
        }else{
            entries.add((int)monthlyAchieveInv);
        }

        if(monthlyReturn == 0){
            entries.add(0);
        }else{
            entries.add((int)monthlyReturn);
        }

        if(monthlyBalanceInv == 0){
            entries.add(0);
        }else{
            entries.add((int)monthlyBalanceInv);
        }


        List<String> labels = new ArrayList<>();

        labels.add("Booking Target");
        labels.add("Booking");
        labels.add("RD Invoice");
        labels.add("Cancellations");
        labels.add("RD Balance");


        create_graph(labels,entries);

        //---------------------------- horizontal bar chart ----------------------------------------------------------------------

        int nonprd = 0;
        nonprd = new DashboardController(getActivity()).getNonPrdCount();
        int ordcount = new DashboardController(getActivity()).getProductiveCount();
        String route = "";
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        String curdate = curYear+"-"+ String.format("%02d", curMonth) + "-" + String.format("%02d", curDate);
        if(!new FItenrDetController(getActivity()).getRouteFromItenary(curdate).equals("")) {
            route = new DashboardController(getActivity()).getRoute();
        }else{
            route = new RouteDetController(getActivity()).getRouteCodeByDebCode(SharedPref.getInstance(getActivity()).getSelectedDebCode());
        }
        int outlets = new DashboardController(getActivity()).getOutletCount(route);
        int notVisit = outlets - (ordcount+nonprd);
        if(notVisit > 0){
            notVisit = outlets - (ordcount+nonprd);
        }else{
            notVisit = 0;
        }
        Log.d(">>>route",">>>"+route);
        Log.d(">>>outlets",">>>"+outlets);
        Log.d(">>>notVisit",">>>"+notVisit);
        Log.d(">>>visit",">>>"+ordcount);


        // PREPARING THE ARRAY LIST OF BAR ENTRIES



        barchart(ordcount,notVisit,nonprd,"visit("+ordcount+")","not visit("+notVisit+")","nonproductive("+nonprd+")");
      //  setHorizontalBarchart(3,outlets);


        //--------------------------------------- Pie Chart -----------------------------------------------------------------
        create_pie_Chart(Double.parseDouble(pref.getAchievement()),Double.parseDouble(pref.getTarget()));



        return rootView;
    }

    public void barchart(int visit,int notVisit,int nonpro,String labelVisit,String labelNotVisit,String labelNonPro) {

        cumulativeLineChart.animateXY(2000, 2000);
        cumulativeLineChart.setDrawGridBackground(false);
        cumulativeLineChart.setDrawBorders(false);
        cumulativeLineChart.setDrawValueAboveBar(false);
        cumulativeLineChart.getAxisLeft().setDrawGridLines(false);
        cumulativeLineChart.getAxisLeft().setEnabled(false);
        cumulativeLineChart.getAxisRight().setEnabled(false);
        cumulativeLineChart.invalidate();
        cumulativeLineChart.getDescription().setEnabled(false);
        cumulativeLineChart.setFitBars(false);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f, (float)nonpro));
        barEntries.add(new BarEntry(2f,  (float)notVisit));
        barEntries.add(new BarEntry(3f, (float) visit) );


        // TO ADD THE VALUES IN X-AXIS
        ArrayList<String> xAxisName = new ArrayList<>();

        xAxisName.add(labelNonPro);
        xAxisName.add(labelNonPro);
        xAxisName.add(labelNotVisit);
        xAxisName.add(labelVisit);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Values");
        barDataSet.setColors(new int[]{ContextCompat.getColor(getActivity(), R.color.visit_not_visited),
                ContextCompat.getColor(getActivity(), R.color.theme_color_dark),
                ContextCompat.getColor(getActivity(), R.color.main_green_stroke_color)});

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        barData.setValueTextSize(0f);
        cumulativeLineChart.setBackgroundColor(Color.TRANSPARENT);
        cumulativeLineChart.setDrawGridBackground(false);
        Legend l = cumulativeLineChart.getLegend();
        l.setTextSize(10f);
        l.setFormSize(10f);
        //To set components of x axis
        XAxis xAxis = cumulativeLineChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisName));
        xAxis.setDrawGridLines(true);
        xAxis.setDrawGridLinesBehindData(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
    //    xAxis.setLabelCount(3,true);
        cumulativeLineChart.setData(barData);
    }


    public void createGroupChart(ArrayList<BarEntry> achieveEntries,ArrayList<BarEntry> TargetEntries){

        barDataSet1 = new BarDataSet(achieveEntries, "Achievement");
        // barDataSet1 = new BarDataSet(getMonthAchievement(new DashboardController(getActivity()).getTargetCategories()), "First Set");
        barDataSet1.setColor(getActivity().getResources().getColor(R.color.material_alert_negative_button));
        barDataSet2 = new BarDataSet(TargetEntries, "Target");
        barDataSet2.setColor(getActivity().getResources().getColor(R.color.green));


        BarData data = new BarData(barDataSet1, barDataSet2);
        groupBarChart.setData(data);
        groupBarChart.getDescription().setEnabled(false);
        XAxis xAxis = groupBarChart.getXAxis();

        ArrayList<String> xValues = new DashboardController(getActivity()).getItemCategories();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0.6f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelCount(xValues.size());

        groupBarChart.setDragEnabled(true);
        //groupBarChart.setVisibleXRangeMaximum(xValues.size());


        float barSpace = 0.1f;
        float groupSpace = 0.5f;

        data.setBarWidth(0.15f);

        Legend l = groupBarChart.getLegend();

        groupBarChart.getXAxis().setAxisMinimum(0);
    //    groupBarChart.getXAxis().setAxisMaximum(0+groupBarChart.getBarData().getGroupWidth(groupSpace,barSpace) * xValues.size());
       // groupBarChart.getAxisLeft().setAxisMinimum(0);

        groupBarChart.animate();
        groupBarChart.groupBars(0, groupSpace, barSpace);
        groupBarChart.setFitBars(true);
        groupBarChart.invalidate();
    }

    public void create_pie_Chart(double daily_Achieve, double daily_Target){

        ArrayList<PieEntry> pieChartValues = new ArrayList<>();


        if(daily_Achieve == 0 || daily_Target == 0){
            precentage = 0;
        }else {
            precentage = (daily_Achieve / daily_Target) * 100;
        }

        pieChart.getDescription().setText(pref.getItemName() + "    -    " + pref.getType() + "    -    " + pref.getCategory());
        pieChart.setCenterText(Math.round(precentage) + " %");
        pieChart.setCenterTextSize(36);
        pieChart.setCenterTextColor(Color.BLUE);
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setHoleRadius(75f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.animateX(400);
        pieChart.setDrawSliceText(true);
        pieChart.setDrawRoundedSlices(true);


        pieChartValues.add(0, new PieEntry((float)daily_Target));
        pieChartValues.add(1,new PieEntry((float)daily_Achieve));

        PieDataSet dataSet = new PieDataSet(pieChartValues, "(-values-)");

        ArrayList title = new ArrayList();

        title.add("Target");
        title.add("Achievement");

        PieData dataPie = new PieData(dataSet);
        pieChart.setData(dataPie);
        dataSet.setColors(new int[]{ContextCompat.getColor(getActivity(), R.color.light_green),
                ContextCompat.getColor(getActivity(),R.color.green )});
        pieChart.animateXY(3000, 3000);
        pieChart.setRenderer(new RoundedSlicesPieChartRenderer(pieChart, pieChart.getAnimator(), pieChart.getViewPortHandler()));

        //Clear
        pref.setType("");
        pref.setCategory("");
        pref.setItemName("");

    }

    public void create_graph(List<String> graph_label, List<Integer> graph_values) {

        try {
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true);
            chart.getDescription().setEnabled(false);
            chart.setPinchZoom(true);

            chart.setDrawGridBackground(false);


            YAxis yAxis = chart.getAxisLeft();

            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setAxisMinimum(0f);


            YAxis axisRight = chart.getAxisRight();
            axisRight.setAxisMinimum(0f);    //Get the label on the left y-axis

            chart.getAxisRight().setEnabled(true);
            chart.getAxisLeft().setEnabled(true);


            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setCenterAxisLabels(false);
            xAxis.setDrawGridLines(false);

            xAxis.setPosition(XAxis.XAxisPosition.TOP);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(graph_label));
            xAxis.setLabelCount(graph_label.size());

            List<BarEntry> yVals1 = new ArrayList<BarEntry>();

            for (int i = 0; i < graph_values.size(); i++) {
                yVals1.add(new BarEntry(i, graph_values.get(i)));
            }


            BarDataSet set1;

            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                // create 2 datasets with different types
                set1 = new BarDataSet(yVals1, "-values-");

                set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
                set1.setColors(new int[]{ContextCompat.getColor(getActivity(), R.color.red_error),
                        ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button),
                        ContextCompat.getColor(getActivity(), R.color.rd_invoice),
                        ContextCompat.getColor(getActivity(), R.color.half_black),
                        ContextCompat.getColor(getActivity(), R.color.theme_color_dark)});
                set1.setDrawValues(false);
                chart.animateY(2000);
                chart.setDrawGridBackground(false);
                chart.getXAxis().setDrawGridLines(false);

                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);


                BarData data = new BarData(dataSets);
                data.setDrawValues(true);
                chart.setData(data);


            }

            chart.setFitBars(true);

            Legend l = chart.getLegend();
            l.setFormSize(12f); // set the size of the legend forms/shapes
            l.setForm(Legend.LegendForm.SQUARE); // set what type of form/shape should be used

            l.setTextSize(10f);
            l.setTextColor(Color.BLACK);
            l.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            l.setYEntrySpace(5f); // set the space between the legend entries on the y-axis

            chart.invalidate();

            chart.animateY(2000);

        } catch (Exception ignored) {
        }
    }


    private SpannableString generateCenterSpannableText() {

        Double d = precentage;
        String[] div = d.toString().split("\\.");
        div[0].length();   // Before Decimal Count

        if(precentage == 0){
            String p = Math.round(precentage) + " %" ;
            SpannableString s = new SpannableString(p);
            s.setSpan(new RelativeSizeSpan(4f), 0, 3, 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 , 0);
            s.setSpan(new RelativeSizeSpan(.8f), 0, 3, 0);
            return s;
        }else if(precentage > 0){
            String p = Math.round(precentage) + " %" ;
            SpannableString s = new SpannableString(p);
            s.setSpan(new RelativeSizeSpan(4f), 0, 3, 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 , 0);
            s.setSpan(new RelativeSizeSpan(.8f), 0, 3, 0);
            return s;
        }


//        else if(div[0].length() == 1){
//            String p = Math.round(precentage) + " %" ;
//            SpannableString s = new SpannableString(p);
//            s.setSpan(new RelativeSizeSpan(3f), 0, 3, 0);
//            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
//            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 , 0);
//            s.setSpan(new RelativeSizeSpan(.8f), 0, 3, 0);
//            return s;
//        }else if(div[0].length() == 2){
//            String p = Math.round(precentage) + " %" ;
//            SpannableString s = new SpannableString(p);
//            s.setSpan(new RelativeSizeSpan(4f), 0, 3, 0);
//            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
//            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 , 0);
//            s.setSpan(new RelativeSizeSpan(.8f), 0, 3, 0);
//            return s;
//        }else if(div[0].length() > 2){
//            String p = Math.round(precentage) + " %" ;
//            SpannableString s = new SpannableString(p);
//            s.setSpan(new RelativeSizeSpan(4f), 0, 3, 0);
//            s.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, 0);
//            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 3 , 0);
//            s.setSpan(new RelativeSizeSpan(.8f), 0, 3, 0);
//            return s;
//        }
        return null;
    }

    public class RoundedSlicesPieChartRenderer extends PieChartRenderer {
        public RoundedSlicesPieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);

            chart.setDrawRoundedSlices(true);
        }

        @Override
        protected void drawDataSet(Canvas c, IPieDataSet dataSet) {
            float angle = 0;
            float rotationAngle = mChart.getRotationAngle();

            float phaseX = mAnimator.getPhaseX();
            float phaseY = mAnimator.getPhaseY();

            final RectF circleBox = mChart.getCircleBox();

            final int entryCount = dataSet.getEntryCount();
            final float[] drawAngles = mChart.getDrawAngles();
            final MPPointF center = mChart.getCenterCircleBox();
            final float radius = mChart.getRadius();
            final boolean drawInnerArc = mChart.isDrawHoleEnabled() && !mChart.isDrawSlicesUnderHoleEnabled();
            final float userInnerRadius = drawInnerArc
                    ? radius * (mChart.getHoleRadius() / 100.f)
                    : 0.f;
            final float roundedRadius = (radius - (radius * mChart.getHoleRadius() / 100f)) / 2f;
            final RectF roundedCircleBox = new RectF();

            int visibleAngleCount = 0;
            for (int j = 0; j < entryCount; j++) {
                // draw only if the value is greater than zero
                if ((Math.abs(dataSet.getEntryForIndex(j).getY()) > Utils.FLOAT_EPSILON)) {
                    visibleAngleCount++;
                }
            }

            final float sliceSpace = visibleAngleCount <= 1 ? 0.f : getSliceSpace(dataSet);
            final Path pathBuffer = new Path();
            final RectF mInnerRectBuffer = new RectF();

            for (int j = 0; j < entryCount; j++) {
                float sliceAngle = drawAngles[j];
                float innerRadius = userInnerRadius;

                Entry e = dataSet.getEntryForIndex(j);

                // draw only if the value is greater than zero
                if (!(Math.abs(e.getY()) > Utils.FLOAT_EPSILON)) {
                    angle += sliceAngle * phaseX;
                    continue;
                }

                // Don't draw if it's highlighted, unless the chart uses rounded slices
                if (mChart.needsHighlight(j) && !drawInnerArc) {
                    angle += sliceAngle * phaseX;
                    continue;
                }

                final boolean accountForSliceSpacing = sliceSpace > 0.f && sliceAngle <= 180.f;

                mRenderPaint.setColor(dataSet.getColor(j));

                final float sliceSpaceAngleOuter = visibleAngleCount == 1 ?
                        0.f :
                        sliceSpace / (Utils.FDEG2RAD * radius);
                final float startAngleOuter = rotationAngle + (angle + sliceSpaceAngleOuter / 2.f) * phaseY;
                float sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter) * phaseY;

                if (sweepAngleOuter < 0.f) {
                    sweepAngleOuter = 0.f;
                }

                pathBuffer.reset();

                float arcStartPointX = center.x + radius * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                float arcStartPointY = center.y + radius * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

                if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                    // Android is doing "mod 360"
                    pathBuffer.addCircle(center.x, center.y, radius, Path.Direction.CW);
                } else {
                    if (drawInnerArc) {
                        float x = center.x + (radius - roundedRadius) * (float) Math.cos(startAngleOuter * Utils.FDEG2RAD);
                        float y = center.y + (radius - roundedRadius) * (float) Math.sin(startAngleOuter * Utils.FDEG2RAD);

                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);
                        pathBuffer.arcTo(roundedCircleBox, startAngleOuter - 180, 180);
                    }

                    pathBuffer.arcTo(
                            circleBox,
                            startAngleOuter,
                            sweepAngleOuter
                    );
                }

                // API < 21 does not receive floats in addArc, but a RectF
                mInnerRectBuffer.set(
                        center.x - innerRadius,
                        center.y - innerRadius,
                        center.x + innerRadius,
                        center.y + innerRadius);

                if (drawInnerArc && (innerRadius > 0.f || accountForSliceSpacing)) {

                    if (accountForSliceSpacing) {
                        float minSpacedRadius =
                                calculateMinimumRadiusForSpacedSlice(
                                        center, radius,
                                        sliceAngle * phaseY,
                                        arcStartPointX, arcStartPointY,
                                        startAngleOuter,
                                        sweepAngleOuter);

                        if (minSpacedRadius < 0.f)
                            minSpacedRadius = -minSpacedRadius;

                        innerRadius = Math.max(innerRadius, minSpacedRadius);
                    }

                    final float sliceSpaceAngleInner = visibleAngleCount == 1 || innerRadius == 0.f ?
                            0.f :
                            sliceSpace / (Utils.FDEG2RAD * innerRadius);
                    final float startAngleInner = rotationAngle + (angle + sliceSpaceAngleInner / 2.f) * phaseY;
                    float sweepAngleInner = (sliceAngle - sliceSpaceAngleInner) * phaseY;
                    if (sweepAngleInner < 0.f) {
                        sweepAngleInner = 0.f;
                    }
                    final float endAngleInner = startAngleInner + sweepAngleInner;

                    if (sweepAngleOuter >= 360.f && sweepAngleOuter % 360f <= Utils.FLOAT_EPSILON) {
                        // Android is doing "mod 360"
                        pathBuffer.addCircle(center.x, center.y, innerRadius, Path.Direction.CCW);
                    } else {
                        float x = center.x + (radius - roundedRadius) * (float) Math.cos(endAngleInner * Utils.FDEG2RAD);
                        float y = center.y + (radius - roundedRadius) * (float) Math.sin(endAngleInner * Utils.FDEG2RAD);

                        roundedCircleBox.set(x - roundedRadius, y - roundedRadius, x + roundedRadius, y + roundedRadius);

                        pathBuffer.arcTo(roundedCircleBox, endAngleInner, 180);
                        pathBuffer.arcTo(mInnerRectBuffer, endAngleInner, -sweepAngleInner);
                    }
                } else {

                    if (sweepAngleOuter % 360f > Utils.FLOAT_EPSILON) {
                        if (accountForSliceSpacing) {

                            float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;

                            float sliceSpaceOffset =
                                    calculateMinimumRadiusForSpacedSlice(
                                            center,
                                            radius,
                                            sliceAngle * phaseY,
                                            arcStartPointX,
                                            arcStartPointY,
                                            startAngleOuter,
                                            sweepAngleOuter);

                            float arcEndPointX = center.x +
                                    sliceSpaceOffset * (float) Math.cos(angleMiddle * Utils.FDEG2RAD);
                            float arcEndPointY = center.y +
                                    sliceSpaceOffset * (float) Math.sin(angleMiddle * Utils.FDEG2RAD);

                            pathBuffer.lineTo(
                                    arcEndPointX,
                                    arcEndPointY);

                        } else {
                            pathBuffer.lineTo(
                                    center.x,
                                    center.y);
                        }
                    }

                }

                pathBuffer.close();

                mBitmapCanvas.drawPath(pathBuffer, mRenderPaint);

                angle += sliceAngle * phaseX;
            }

            MPPointF.recycleInstance(center);
        }
    }

    public void TargetDetailDialog(final Context context) {
        final Dialog targetDetDialog = new Dialog(context);
        targetDetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        targetDetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        targetDetDialog.setCancelable(false);
        targetDetDialog.setCanceledOnTouchOutside(false);
        targetDetDialog.setContentView(R.layout.target_details);

        //initializations

        spTargetItem = (SearchableSpinner) targetDetDialog.findViewById(R.id.spTargetItem) ;
        itemName = (TextView) targetDetDialog.findViewById(R.id.targetItemName) ;
        btnFromDate = (ImageView) targetDetDialog.findViewById(R.id.image_view_date_select_from) ;
        btnToDate = (ImageView) targetDetDialog.findViewById(R.id.image_view_date_select_to) ;
        fromDate = (TextView) targetDetDialog.findViewById(R.id.fromDate) ;
        toDate = (TextView) targetDetDialog.findViewById(R.id.toDate) ;
        rdSales = (RadioButton) targetDetDialog.findViewById(R.id.rdSalesOrder) ;
        rdInvoice = (RadioButton) targetDetDialog.findViewById(R.id.rdInvoice) ;
        radioGroup1 = (RadioGroup) targetDetDialog.findViewById(R.id.groupradio) ;
        radioGroup2 = (RadioGroup) targetDetDialog.findViewById(R.id.groupradioCategory) ;

        final RadioButton rdCase = (RadioButton) targetDetDialog.findViewById(R.id.rdCase) ;
        final RadioButton rdPiece = (RadioButton) targetDetDialog.findViewById(R.id.rdPieces) ;
        final RadioButton rdTonnage = (RadioButton) targetDetDialog.findViewById(R.id.rdTonnage) ;
        final RadioButton rdValue = (RadioButton) targetDetDialog.findViewById(R.id.rdValue) ;



        //------------------------------- Set from date --------------------------------------------------------
        btnFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialogfrom = new DatePickerDialog();
                datePickerDialogfrom.setThemeDark(false);
                datePickerDialogfrom.showYearPickerFirst(false);
                datePickerDialogfrom.setAccentColor(Color.parseColor("#0072BA"));
                datePickerDialogfrom.show(getActivity().getFragmentManager(),"DatePickerDialog");
                datePickerDialogfrom.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        // String date =  "Select Date : " + day + " - " + month + " - " + year;
                        String datesaveFrom = "";
                        if(String.valueOf(monthOfYear+1).length()<2 && String.valueOf(dayOfMonth).length()<2){
                            datesaveFrom = "" + year + "-" + "0"+(monthOfYear+1) + "-" + "0"+dayOfMonth ;
                            changeDateFrom = "" +(monthOfYear+1) + "/" + "0" + dayOfMonth + "/" + year;
                            // changeDateFrom = "" + "0"+(monthOfYear+1) + "/" + "0" + dayOfMonth + "/" + year;
                        }else{
                            if(String.valueOf(monthOfYear+1).length()<2){
                                datesaveFrom = "" + year + "-" +"0"+(monthOfYear+1) +"-" + dayOfMonth ;
                                changeDateFrom = "" + (monthOfYear+1) + "/" + dayOfMonth +"/" + year ;
                                //  changeDateFrom = "" + "0" + (monthOfYear+1) + "/" + dayOfMonth +"/" + year ;
                            }else if(String.valueOf(dayOfMonth).length()<2){
                                datesaveFrom = "" + year + "-" + (monthOfYear+1) + "-" + "0"+dayOfMonth ;
                                changeDateFrom = "" + (monthOfYear+1) + "/" + "0"+dayOfMonth + "/" + year ;
                            }else{
                                datesaveFrom = "" + year + "-" +(monthOfYear+1) + "-" + dayOfMonth ;
                                changeDateFrom = "" + (monthOfYear+1) + "/" + dayOfMonth + "/" + year ;
                            }
                        }

                        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
                        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));

                        if ((curMonth == (monthOfYear +1)) && (curYear == year)) {
                            fromDate.setText(""+datesaveFrom);
                        }else{
                            Toast.makeText(context,"You can't choose previous or future month date. ",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        // ------------------------- Set to date --------------------------------------------------------------
        btnToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialogTo = new DatePickerDialog();
                datePickerDialogTo.setThemeDark(false);
                datePickerDialogTo.showYearPickerFirst(false);
                datePickerDialogTo.setAccentColor(Color.parseColor("#0072BA"));
                datePickerDialogTo.show(getActivity().getFragmentManager(),"DatePickerDialog");
                datePickerDialogTo.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        //String date =  "Select Date : " + day + " - " + month + " - " + year;
                        String datesaveTo = "";
                        if(String.valueOf(monthOfYear+1).length()<2 && String.valueOf(dayOfMonth).length()<2){
                            datesaveTo = "" + year + "-" + "0"+(monthOfYear+1) + "-" + "0"+dayOfMonth ;
                            changeDateTo = "" + (monthOfYear+1) + "/" + "0" + dayOfMonth + "/" + year;
                            //  changeDateTo = "" + "0"+(monthOfYear+1) + "/" + "0" + dayOfMonth + "/" + year;
                        }else{
                            if(String.valueOf(monthOfYear+1).length()<2){
                                datesaveTo = "" + year + "-" +"0"+(monthOfYear+1) +"-" + dayOfMonth ;
                                changeDateTo = "" + (monthOfYear+1) + "/" + dayOfMonth +"/" + year ;
                                //        changeDateTo = "" + "0" + (monthOfYear+1) + "/" + dayOfMonth +"/" + year ;
                            }else if(String.valueOf(dayOfMonth).length()<2){
                                datesaveTo = "" + year + "-" + (monthOfYear+1) + "-" + "0"+dayOfMonth ;
                                changeDateTo = "" + (monthOfYear+1) + "/" + "0"+dayOfMonth + "/" + year ;
                            }else{
                                datesaveTo = "" + year + "-" +(monthOfYear+1) + "-" + dayOfMonth ;
                                changeDateTo = "" + (monthOfYear+1) + "/" + dayOfMonth + "/" + year ;
                            }
                        }

                        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
                        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));

                        if ((curMonth == (monthOfYear +1)) && (curYear == year)) {
                            toDate.setText(""+datesaveTo);
                        }else{
                            Toast.makeText(context,"You can't choose previous or future month date. ",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        //----------------------------------Select Sales or Invoice-----------------------------------------------------------------
        if(rdSales.isChecked()){
            type = "Order";
            pref.setType("Order");
        }else if(rdInvoice.isChecked()){
            type = "Invoice";
            pref.setType("Invoice");
        }else{
            type = "";
        }
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rdSales.isChecked()){
                    type = "Order";
                    pref.setType("Order");
                }else if(rdInvoice.isChecked()){
                    type = "Invoice";
                    pref.setType("Invoice");
                }else{
                    type = "";
                }
            }
        });

        //------------------------------Category selection--------------------------------------------------------
        if(rdCase.isChecked()){
            category = "Case";
            pref.setCategory("Case");
        }else if(rdPiece.isChecked()){
            category = "Piece";
            pref.setCategory("Piece");
        }else if(rdTonnage.isChecked()){
            category = "Tonnage";
            pref.setCategory("Tonnage");
        }else if(rdValue.isChecked()){
            category = "Value";
            pref.setCategory("Value");
        }else{
            category = "";
        }

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rdCase.isChecked()){
                    category = "Case";
                    pref.setCategory("Case");
                }else if(rdPiece.isChecked()){
                    category = "Piece";
                    pref.setCategory("Piece");
                }else if(rdTonnage.isChecked()){
                    category = "Tonnage";
                    pref.setCategory("Tonnage");
                }else if(rdValue.isChecked()){
                    category = "Value";
                    pref.setCategory("Value");
                }
            }
        });


        // set target item data into the spinner
        targetItemList = new ItemController(context).getItems();
        targetItemList.add(0, "-SELECT-");

        ArrayAdapter<String> adaptercur = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, targetItemList);
        adaptercur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTargetItem.setAdapter(adaptercur);

        spTargetItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!spTargetItem.getSelectedItem().equals("-SELECT-")){
                    itemName.setText(spTargetItem.getSelectedItem().toString().split("- :")[1].trim());
                    itemcode = spTargetItem.getSelectedItem().toString().split("- :")[0].trim();
                    pref.setItemName(spTargetItem.getSelectedItem().toString().split("- :")[1].trim());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        targetDetDialog.findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!type.equals("") && !category.equals("") && !spTargetItem.getSelectedItem().equals("-SELECT-")) {

                    achievement = 0.00;
                    TotalTonnage = 0.00;

                    if (itemcode.equals("0000")) {// For All Sub Brands

                        target = new DashboardController(context).getTargetForAll(fromDate.getText().toString(), toDate.getText().toString());

                        if (type.equals("Order")) {// To Get Order Achievement

                            if (category.equals("Tonnage")) {

                            } else {
                                if (new DashboardController(context).isAnyOrderForAll(category, fromDate.getText().toString(), toDate.getText().toString())) {
                                    achievement = new DashboardController(context).getAllOrderAchievement(category, fromDate.getText().toString(), toDate.getText().toString());
                                } else {
                                    Toast.makeText(context, "No data for display", Toast.LENGTH_LONG).show();
                                    achievement = 0.00;
                                }

                                create_pie_Chart(achievement, target);
                                pref.setAchievement("" + achievement);
                                pref.setTarget("" + target);
                            }

                        } else if (type.equals("Invoice")) {//To Get Invoice Achievement

                            if (category.equals("Tonnage")) {

                            } else {

                                if (new DashboardController(context).isAnyInvoiceForAll(category, fromDate.getText().toString(), toDate.getText().toString())) {
                                    achievement = new DashboardController(context).getAllInvoiceAchievement(category, fromDate.getText().toString(), toDate.getText().toString());
                                } else {
                                    Toast.makeText(context, "No data for display", Toast.LENGTH_LONG).show();
                                    achievement = 0.00;
                                }

                                create_pie_Chart(achievement, target);
                                pref.setAchievement("" + achievement);
                                pref.setTarget("" + target);
                            }
                        }
                    } else { // For Selected Sub Brand

                    ArrayList<Item> list = new DashboardController(context).getItemsBySbrand(itemcode);

                    target = new DashboardController(context).getTargetBySubCode(fromDate.getText().toString(), toDate.getText().toString(), itemcode);

                    if (type.equals("Order")) {// To Get Order Achievement

                        if (category.equals("Tonnage")) {

                            for (Item item : list) {
                                tonnage = getTonnage("Order", item.getFITEM_ITEM_CODE());
                                TotalTonnage = TotalTonnage + tonnage;
                            }

                            create_pie_Chart(TotalTonnage, target);
                            pref.setAchievement("" + TotalTonnage);
                            pref.setTarget("" + target);

                        } else {

                            if (new DashboardController(context).isAnyOrder(category, itemcode, fromDate.getText().toString(), toDate.getText().toString())) {
                                achievement = new DashboardController(context).getOrderAchievement(category, itemcode, fromDate.getText().toString(), toDate.getText().toString());
                            } else {
                                Toast.makeText(context, "No data for display", Toast.LENGTH_LONG).show();
                                achievement = 0.00;
                            }

                            create_pie_Chart(achievement, target);
                            pref.setAchievement("" + achievement);
                            pref.setTarget("" + target);
                        }

                    } else if (type.equals("Invoice")) {//To Get Invoice Achievement

                        if (category.equals("Tonnage")) {

//                            for (Item item : list) {
//                                tonnage = getTonnage("Order", item.getFITEM_ITEM_CODE());
//                                TotalTonnage = TotalTonnage + tonnage;
//                            }

//                            create_pie_Chart(TotalTonnage, target);
//                            pref.setAchievement("" + TotalTonnage);
//                            pref.setTarget("" + target);

                        } else {

                            if (new DashboardController(context).isAnyInvoice(category, itemcode, fromDate.getText().toString(), toDate.getText().toString())) {
                                achievement = new DashboardController(context).getInvoiceAchievement(category, itemcode, fromDate.getText().toString(), toDate.getText().toString());
                            } else {
                                Toast.makeText(context, "No data for display", Toast.LENGTH_LONG).show();
                                achievement = 0.00;
                            }

                            create_pie_Chart(achievement, target);
                            pref.setAchievement("" + achievement);
                            pref.setTarget("" + target);
                        }
                    }
                }
                    targetDetDialog.dismiss();
                }else{
                    targetDetDialog.dismiss();
                }

            }
        });


        targetDetDialog.show();
    }


    public double getTonnage(String type,String itemcode) {
        double tonnage = 0.0;
        String unitsPerCase = "";
        DashboardController dashboardController = new DashboardController(getActivity());
        ArrayList<Item> itemTonnageList = new ArrayList<>();
        itemTonnageList = dashboardController.getTonnage(type,fromDate.getText().toString(),toDate.getText().toString(),itemcode);//get ItemCode ItemWeight Qty as list
        for (Item item : itemTonnageList) {// filter item list
            String weight = item.getFITEM_UNITCODE().split("\\*")[0].trim(); // filter only weight part
            if (item.getFITEM_UNITCODE().split("\\*").length > 1) //check split array size
                unitsPerCase = item.getFITEM_UNITCODE().split("\\*")[1].trim();// get units per case

            double qty = Double.parseDouble(item.getFITEM_QOH());//get item quantity

//            String weightWithoutUOM = weight.replaceAll("\\D+", "");//get weight removing string

            String newWeight = "";
            if (weight.contains("g")) {
                newWeight = weight.replace("g", "");
            } else if (weight.contains("G")) {
                newWeight = weight.replace("G", "");
            } else if (weight.contains("ml")) {
                newWeight = weight.replace("ml", "");
            } else if (weight.contains("ML")) {
                newWeight = weight.replace("ML", "");
            }

            if (!weight.equals("") && !unitsPerCase.equals(""))
                try {
                    tonnage += (Double.parseDouble(newWeight) * qty) / (1000 * 1000);//tonnage sum
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        Log.d(">>tonnage",">>tonnage"+tonnage);
        return tonnage;

    }

    public double getMonthlyTonnage(String catcode,String transactionType) {
        double tonnage = 0.0;
        String unitsPerCase = "";
        DashboardController dashboardController = new DashboardController(getActivity());
        ArrayList<Item> itemTonnageList = new ArrayList<>();
        itemTonnageList = dashboardController.getMonthlyTonnage(catcode,transactionType);//get ItemCode ItemWeight Qty as list
        for (Item item : itemTonnageList) {// filter item list
            String weight = item.getFITEM_UNITCODE().split("\\*")[0].trim(); // filter only weight part
            if (item.getFITEM_UNITCODE().split("\\*").length > 1) //check split array size
                unitsPerCase = item.getFITEM_UNITCODE().split("\\*")[1].trim();// get units per case

            double qty = Double.parseDouble(item.getFITEM_QOH());//get item quantity

//            String weightWithoutUOM = weight.replaceAll("\\D+", "");//get weight removing string

            String newWeight = "";
            if (weight.contains("g")) {
                newWeight = weight.replace("g", "");
            } else if (weight.contains("G")) {
                newWeight = weight.replace("G", "");
            } else if (weight.contains("ml")) {
                newWeight = weight.replace("ml", "");
            } else if (weight.contains("ML")) {
                newWeight = weight.replace("ML", "");
            }

            if (!weight.equals("") && !unitsPerCase.equals(""))
                try {
                    tonnage += (Double.parseDouble(newWeight) * qty) / (1000 * 1000);//tonnage sum
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        Log.d(">>tonnage",">>tonnage"+tonnage);
        return tonnage;

    }

    //---------------------------------------------- Group Chart -------------------------------------------------------------------------

    public void getMonthAchievement( ArrayList<TargetCat> categories,String type,String tranType) {

        barAchieveEntries = new ArrayList<>();
        barTargetEntries = new ArrayList<>();

        double monthlyCatAchieve, monthlyCatTarget = 0.00;

        if(tranType.equals("Order")){

            if(type.equals("Tonnage")){
                for(TargetCat cat : categories){
                    monthlyCatAchieve = getMonthlyTonnage(cat.getTarCatCode().trim(),tranType);
                    barAchieveEntries.add(new BarEntry(cat.getId(), (float) monthlyCatAchieve));
                }
            }else{
                for(TargetCat cat : categories){
                    monthlyCatAchieve = new DashboardController(getActivity()).getMonthlyOrderAchievement(cat.getTarCatCode().trim(),type);
                    barAchieveEntries.add(new BarEntry(cat.getId(), (float) monthlyCatAchieve));
                }
            }

        }else if(tranType.equals("Invoice")){

            if(type.equals("Tonnage")){
                for(TargetCat cat : categories){
                    monthlyCatAchieve = getMonthlyTonnage(cat.getTarCatCode().trim(),tranType);
                    barAchieveEntries.add(new BarEntry(cat.getId(), (float) monthlyCatAchieve));
                }
            }else{
                for(TargetCat cat : categories){
                    monthlyCatAchieve = new DashboardController(getActivity()).getMonthlyInvoiceAchievement(cat.getTarCatCode().trim(),type);
                    barAchieveEntries.add(new BarEntry(cat.getId(), (float) monthlyCatAchieve));
                }
            }
        }


        for(TargetCat cat : categories){
            monthlyCatTarget = new DashboardController(getActivity()).getMonthlyTarget(cat.getTarCatCode().trim(),type);
            barTargetEntries.add(new BarEntry(cat.getId(), (float) monthlyCatTarget));
        }

        createGroupChart(barAchieveEntries,barTargetEntries);

    }

}