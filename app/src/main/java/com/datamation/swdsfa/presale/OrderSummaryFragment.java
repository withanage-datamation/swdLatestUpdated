package com.datamation.swdsfa.presale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.adapter.OrderDetailsAdapter;
import com.datamation.swdsfa.adapter.OrderFreeIssueDetailAdapter;
import com.datamation.swdsfa.adapter.OrderReturnItemAdapter;
import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.ItemLocController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.controller.OrderDetailController;
import com.datamation.swdsfa.controller.OrderDiscController;
import com.datamation.swdsfa.controller.PreProductController;
import com.datamation.swdsfa.controller.PreSaleTaxDTController;
import com.datamation.swdsfa.controller.PreSaleTaxRGController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.discount.Discount;
import com.datamation.swdsfa.helpers.PreSalesResponseListener;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.Customer;
import com.datamation.swdsfa.model.Order;
import com.datamation.swdsfa.model.OrderDetail;
import com.datamation.swdsfa.model.OrderDisc;
import com.datamation.swdsfa.settings.ReferenceNum;
import com.datamation.swdsfa.utils.NetworkUtil;
import com.datamation.swdsfa.utils.UtilityContainer;
import com.datamation.swdsfa.view.DebtorDetailsActivity;
import com.datamation.swdsfa.view.PreSalesActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderSummaryFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener{

    public static final String SETTINGS = "PreSalesSummary";
    public static SharedPreferences localSP;
    View view;
    TextView lblGross, lblReturnQty, lblReturn, lblNetVal, lblReplacements, lblQty,lblSummaryHeader;
    SharedPref mSharedPref;
    String RefNo = null, customerName = "";
    ArrayList<OrderDetail> list;
    ArrayList<OrderDisc> discList;
    String locCode;
    FloatingActionButton fabPause, fabDiscard, fabSave,fabSaveAndUpload;
    FloatingActionMenu fam;
    MyReceiver r;
    int iTotFreeQty = 0;
    double totalMKReturn = 0;
    PreSalesActivity mainActivity;
    private Customer outlet;
    PreSalesResponseListener responseListener;
    Activity mactivity;
    private double currentLatitude, currentLongitude;
    private Customer customer;
    Location mLocation;
    ProgressDialog dialog;
    List<String> resultListOrder;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_summary, container, false);

        mSharedPref = new SharedPref(getActivity());
        mainActivity = (PreSalesActivity) getActivity();
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);
        fabSaveAndUpload = (FloatingActionButton) view.findViewById(R.id.fab4);
        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);

        lblNetVal = (TextView) view.findViewById(R.id.lblNetVal_Inv);
        lblSummaryHeader = (TextView) view.findViewById(R.id.summary_header);
        lblReturn = (TextView) view.findViewById(R.id.lbl_return_tot);
        lblReturnQty = (TextView) view.findViewById(R.id.lblReturnQty);
        lblReplacements = (TextView) view.findViewById(R.id.lblReplacement);
        lblGross = (TextView) view.findViewById(R.id.lblGross_Inv);
        lblQty = (TextView) view.findViewById(R.id.lblQty_Inv);

        resultListOrder = new ArrayList<>();

        mactivity = getActivity();
        customerName = new CustomerController(getActivity()).getCusNameByCode(SharedPref.getInstance(getActivity()).getSelectedDebCode());

        lblSummaryHeader.setText("ORDER SUMMARY - ("+customerName+")");

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });

        fabPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseinvoice();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer = new CustomerController(getActivity()).getCustomerGPS(SharedPref.getInstance(getActivity()).getSelectedDebCode());
                if (!SharedPref.getInstance(getActivity()).getGlobalVal("Latitude").equals(""))
                    currentLatitude = Double.parseDouble(SharedPref.getInstance(getActivity()).getGlobalVal("Latitude"));
                else
                    currentLatitude = 0.0;
                if (!SharedPref.getInstance(getActivity()).getGlobalVal("Longitude").equals(""))
                    currentLongitude = Double.parseDouble(SharedPref.getInstance(getActivity()).getGlobalVal("Longitude"));
                else
                    currentLongitude = 0.0;

                Location currentLocation = new Location("point Current");
                currentLocation.setLatitude(currentLatitude);
                currentLocation.setLongitude(currentLongitude);

                Location customerLocation = new Location("point Customer");

                if (!customer.getLatitude().equals("") && !customer.getLatitude().equals(null))
                    customerLocation.setLatitude(Double.parseDouble(customer.getLatitude()));
                else
                    customerLocation.setLatitude(0.0);

                if (!customer.getLongitude().equals("") && !customer.getLongitude().equals(null))
                    customerLocation.setLongitude(Double.parseDouble(customer.getLongitude()));
                else
                    customerLocation.setLongitude(0.0);
                float distance = currentLocation.distanceTo(customerLocation);
                float distance1 = customerLocation.distanceTo(currentLocation);
//                Log.d("<<<customer Longi<<<<", " " + customer.getLongitude());
//                Log.d("<<<customer Lati<<<<", " " + customer.getLatitude());
//                Log.d("<<<current Longi<<<<", " " + currentLongitude);
//                Log.d("<<<current Lati<<<<", " " + currentLatitude);
//                Log.d("<<<Distance<<<<", " " + distance);
                //
               // if (distance <= 50) {
                    popupFeedBack(getActivity());
//                } else {
//                    Toast.makeText(getActivity(), "You are out of customer location.Please go to customer's location to continue..", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        fabSaveAndUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer = new CustomerController(getActivity()).getCustomerGPS(SharedPref.getInstance(getActivity()).getSelectedDebCode());
                if (!SharedPref.getInstance(getActivity()).getGlobalVal("Latitude").equals(""))
                    currentLatitude = Double.parseDouble(SharedPref.getInstance(getActivity()).getGlobalVal("Latitude"));
                else
                    currentLatitude = 0.0;
                if (!SharedPref.getInstance(getActivity()).getGlobalVal("Longitude").equals(""))
                    currentLongitude = Double.parseDouble(SharedPref.getInstance(getActivity()).getGlobalVal("Longitude"));
                else
                    currentLongitude = 0.0;

                Location currentLocation = new Location("point Current");
                currentLocation.setLatitude(currentLatitude);
                currentLocation.setLongitude(currentLongitude);

                Location customerLocation = new Location("point Customer");

                if (!customer.getLatitude().equals("") && !customer.getLatitude().equals(null))
                    customerLocation.setLatitude(Double.parseDouble(customer.getLatitude()));
                else
                    customerLocation.setLatitude(0.0);

                if (!customer.getLongitude().equals("") && !customer.getLongitude().equals(null))
                    customerLocation.setLongitude(Double.parseDouble(customer.getLongitude()));
                else
                    customerLocation.setLongitude(0.0);
                float distance = currentLocation.distanceTo(customerLocation);
                float distance1 = customerLocation.distanceTo(currentLocation);
//                Log.d("<<<customer Longi<<<<", " " + customer.getLongitude());
//                Log.d("<<<customer Lati<<<<", " " + customer.getLatitude());
//                Log.d("<<<current Longi<<<<", " " + currentLongitude);
//                Log.d("<<<current Lati<<<<", " " + currentLatitude);
//                Log.d("<<<Distance<<<<", " " + distance);
                //
                // if (distance <= 50) {
                popupFeedBackforSaveAndUpload(getActivity());
//                } else {
//                    Toast.makeText(getActivity(), "You are out of customer location.Please go to customer's location to continue..", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        fabDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoEditingData();
            }
        });

        return view;
    }

    public void undoEditingData() {

        Order hed = new OrderController(getActivity()).getAllActiveOrdHed();
        outlet = new CustomerController(getActivity()).getSelectedCustomerByCode(hed.getORDER_DEBCODE());

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Do you want to discard the order?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        int result = new OrderController(getActivity()).restData(RefNo);

                        if (result > 0) {
                            new OrderDetailController(getActivity()).restData(RefNo);
                            new PreProductController(getActivity()).mClearTables();
                            mSharedPref.setDiscountClicked("0");
                            mSharedPref.setTotalValueDiscount("0");
                            mSharedPref.setValueDiscountPer("0");
                            mSharedPref.setValueDiscountRef("");
                            new SharedPref(getActivity()).setGlobalVal("KeyPayType" , "");

                        }

                        Toast.makeText(getActivity(), "Order discarded successfully..!", Toast.LENGTH_SHORT).show();

                        Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
                        intnt.putExtra("outlet", outlet);
                        startActivity(intnt);
                        getActivity().finish();


                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        dialog.dismiss();


                    }
                })
                .build();
        materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-Save primary & secondary invoice-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*/

    public void mRefreshData() {

        if (mSharedPref.getDiscountClicked().equals("0")) {
            responseListener.moveBackToCustomer_pre(1);
            Toast.makeText(getActivity(), "Please tap on Free Issue Button", Toast.LENGTH_LONG).show();
        }
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
        customerName = new CustomerController(getActivity()).getCusNameByCode(SharedPref.getInstance(getActivity()).getSelectedDebCode());

        lblSummaryHeader.setText("ORDER SUMMARY - ("+customerName+")");

        String orRefNo = new OrderController(getActivity()).getActiveRefNoFromOrders();

        int ftotQty = 0, fTotFree = 0, returnQty = 0, replacements = 0;
        double ftotAmt = 0, fTotLineDisc = 0, fTotSchDisc = 0, totalReturn = 0;
        String itemCode = "";

        locCode = "MS";//hardcode for swadeshi 2019-11-18

        list = new OrderDetailController(getActivity()).getAllOrderDetails(RefNo);
        discList = new OrderDiscController(getActivity()).getAllOrderDiscs(RefNo);

        for (OrderDetail ordDet : list) {
            if (ordDet.getFORDERDET_TYPE().equals("SA"))
                ftotAmt += Double.parseDouble(ordDet.getFORDERDET_AMT());

            //itemCode = ordDet.getFORDERDET_ITEMCODE();

            if (ordDet.getFORDERDET_TYPE().equals("SA"))
                ftotQty += Integer.parseInt(ordDet.getFORDERDET_QTY());

            if (ordDet.getFORDERDET_TYPE().equals("MR"))
                totalMKReturn += Double.parseDouble(ordDet.getFORDERDET_AMT());

            if (ordDet.getFORDERDET_TYPE().equals("MR") || ordDet.getFORDERDET_TYPE().equals("UR")) {
                totalReturn += Double.parseDouble(ordDet.getFORDERDET_AMT());
                returnQty += Double.parseDouble(ordDet.getFORDERDET_QTY());
            }

            if (ordDet.getFORDERDET_TYPE().equals("FD") || ordDet.getFORDERDET_TYPE().equals("FI"))
                fTotFree += Integer.parseInt(ordDet.getFORDERDET_QTY());
            //else
            //fTotFree += Integer.parseInt(ordDet.getFORDERDET_QTY());

            //fTotLineDisc += Double.parseDouble(ordDet.getFORDERDET_DISAMT());
            //fTotSchDisc += Double.parseDouble(ordDet.getFORDERDET_DIS_VAL_AMT());
        }

        for (OrderDisc disc : discList) {
            fTotSchDisc += Double.parseDouble(disc.getDisAmt());
        }

        Double gross = 0.0;
        Double net = 0.0;
        Discount discount = new Discount(getActivity());
        double discountValue = discount.totalDiscount(ftotAmt,SharedPref.getInstance(getActivity()).getSelectedDebCode());

        gross = ftotAmt + fTotSchDisc;
        net = gross + totalReturn - fTotSchDisc-discountValue;


        iTotFreeQty = fTotFree;
        lblQty.setText(String.valueOf(ftotQty + fTotFree));
//        lblGross.setText(String.format("%.2f", ftotAmt + fTotSchDisc + fTotLineDisc));
//        lblReturn.setText(String.format("%.2f", totalReturn));
//        lblNetVal.setText(String.format("%.2f", ftotAmt-totalReturn));

        //String sArray[] = new TaxDetController(getActivity()).calculateTaxForwardFromDebTax(mSharedPref.getSelectedDebCode(), itemCode, ftotAmt);
        //String amt = String.format("%.2f",Double.parseDouble(sArray[0]));


        lblGross.setText(String.format("%.2f", gross)); // SA type total amt + discount
        lblReturn.setText(String.format("%.2f", totalReturn)); // MR/UR type total amt
        lblNetVal.setText(String.format("%.2f", net)); // total - discount - return
        lblReturnQty.setText(String.valueOf(returnQty));
        lblReplacements.setText(String.format("%.2f" , fTotSchDisc+discountValue));

    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void saveSummaryDialog() {

        if (new OrderDetailController(getActivity()).getItemCount(RefNo) > 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptView = layoutInflater.inflate(R.layout.sales_management_van_sales_summary_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Do you want to save the invoice ?");
            alertDialogBuilder.setView(promptView);

            final ListView lvProducts_Invoice = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Inv);
            ViewGroup.LayoutParams invItmparams = lvProducts_Invoice.getLayoutParams();
            ArrayList<OrderDetail> orderItemList = null;
            orderItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "SA","");
            if(orderItemList.size()>0){
                invItmparams.height = 200;
            }else {
                invItmparams.height = 0;
            }
            lvProducts_Invoice.setLayoutParams(invItmparams);

            lvProducts_Invoice.setAdapter(new OrderDetailsAdapter(getActivity(), orderItemList, mSharedPref.getSelectedDebCode()));

            //MMS - freeissues
            ListView lvProducts_freeIssue = (ListView) promptView.findViewById(R.id.lvProducts_Summary_freeIssue);
            ViewGroup.LayoutParams params = lvProducts_freeIssue.getLayoutParams();
            ArrayList<OrderDetail> orderFreeIssueItemList = null;
            orderFreeIssueItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "FI","FD");
            if(orderFreeIssueItemList.size()>0){
                params.height = 200;
            }else {
                params.height = 0;
            }

            lvProducts_freeIssue.setLayoutParams(params);
            lvProducts_freeIssue.setAdapter(new OrderFreeIssueDetailAdapter(getActivity(), orderFreeIssueItemList, mSharedPref.getSelectedDebCode()));

            //MMS - return item
            ListView lvProducts_return = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Ret);
            ViewGroup.LayoutParams retItmparams = lvProducts_return.getLayoutParams();

            ArrayList<OrderDetail> orderReturnItemList = null;
            orderReturnItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "UR","MR");
            Log.d("**re", "saveSummaryDialog: "+orderReturnItemList.toString());
            if(orderReturnItemList.size()>0){
                retItmparams.height = 200;
            }else {
                retItmparams.height = 0;
            }
            lvProducts_return.setLayoutParams(retItmparams);

            lvProducts_return.setAdapter(new OrderReturnItemAdapter(getActivity(), orderReturnItemList, mSharedPref.getSelectedDebCode()));

            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {

                    Order ordHed = new Order();
                    ArrayList<Order> ordHedList = new ArrayList<Order>();
                    Order presale = new OrderController(getActivity()).getAllActiveOrdHed();
                    ordHed.setORDER_REFNO(RefNo);
                    ordHed.setORDER_DEBCODE(presale.getORDER_DEBCODE());
                    ordHed.setORDER_DEBNAME(presale.getORDER_DEBNAME());
                    ordHed.setORDER_ADDDATE(presale.getORDER_ADDDATE());
                    ordHed.setORDER_MANUREF(presale.getORDER_MANUREF());
                    ordHed.setORDER_REMARKS(presale.getORDER_REMARKS());
                    ordHed.setORDER_ADDMACH(presale.getORDER_ADDMACH());
                    ordHed.setORDER_ADDUSER(presale.getORDER_ADDUSER());
                    ordHed.setORDER_CURCODE(presale.getORDER_CURCODE());
                    ordHed.setORDER_CURRATE(presale.getORDER_CURRATE());
                    ordHed.setORDER_LOCCODE(presale.getORDER_LOCCODE());
                    ordHed.setORDER_CUSTELE(presale.getORDER_CUSTELE());
                    ordHed.setORDER_START_TIMESO(presale.getORDER_START_TIMESO());
                    ordHed.setORDER_CONTACT(presale.getORDER_CONTACT());
                    ordHed.setORDER_CUSADD1(presale.getORDER_CUSADD1());
                    ordHed.setORDER_CUSADD2(presale.getORDER_CUSADD2());
                    ordHed.setORDER_CUSADD3(presale.getORDER_CUSADD3());
                    ordHed.setORDER_TXNTYPE(presale.getORDER_TXNTYPE());
                    ordHed.setORDER_IS_ACTIVE(presale.getORDER_IS_ACTIVE());
                    ordHed.setORDER_IS_SYNCED(presale.getORDER_IS_SYNCED());
                    ordHed.setORDER_LOCCODE(presale.getORDER_LOCCODE());
                    ordHed.setORDER_AREACODE(presale.getORDER_AREACODE());
                    ordHed.setORDER_ROUTECODE(presale.getORDER_ROUTECODE());
                    ordHed.setORDER_COSTCODE(presale.getORDER_COSTCODE());
                    ordHed.setORDER_TAXREG(presale.getORDER_TAXREG());
                    ordHed.setORDER_TOURCODE(presale.getORDER_TOURCODE());
                    ordHed.setORDER_DELIVERY_DATE(presale.getORDER_DELIVERY_DATE());
                    ordHed.setORDER_PAYTYPE(presale.getORDER_PAYTYPE());
                    ordHed.setORDER_LATITUDE(presale.getORDER_LATITUDE());
                    ordHed.setORDER_LONGITUDE(presale.getORDER_LONGITUDE());
                    ordHed.setORDER_BPTOTALDIS("0");
                    ordHed.setORDER_BTOTALAMT("0");
                    ordHed.setORDER_TOTALTAX("0");
                    ordHed.setORDER_TOTALDIS(presale.getORDER_TOTALDIS());
                    ordHed.setORDER_TOTALAMT(lblNetVal.getText().toString());
                    ordHed.setORDER_TOTALDIS(lblReplacements.getText().toString());
                    ordHed.setORDER_TXNDATE(presale.getORDER_TXNDATE());
                    ordHed.setORDER_REPCODE(new SalRepController(getActivity()).getCurrentRepCode());
                    ordHed.setORDER_REFNO1("");
                    ordHed.setORDER_TOTQTY(lblQty.getText().toString());
                    ordHed.setORDER_TOTFREEQTY(iTotFreeQty + "");
                    ordHed.setORDER_SETTING_CODE(presale.getORDER_SETTING_CODE());
                    ordHed.setORDER_DEALCODE(presale.getORDER_DEALCODE());
                    ordHed.setORDER_TOTALMKRAMT(String.format("%.2f", totalMKReturn) + "");
                    ordHed.setORDER_TOTAL_VALUE_DISCOUNT(mSharedPref.getTotalValueDiscount());
                    ordHed.setORDER_VALUE_DISCOUNT_PER(mSharedPref.getValueDiscountPer());
                    ordHed.setORDER_VALUE_DISCOUNT_REF(mSharedPref.getValueDiscountRef());
                    ordHedList.add(ordHed);

                    if (new OrderController(getActivity()).createOrUpdateOrdHed(ordHedList) > 0) {
                        new PreProductController(getActivity()).mClearTables();
                        new OrderController(getActivity()).InactiveStatusUpdate(RefNo);
                        new OrderDetailController(getActivity()).InactiveStatusUpdate(RefNo);

                        final PreSalesActivity activity = (PreSalesActivity) getActivity();
                        /*-*-*-*-*-*-*-*-*-*-QOH update-*-*-*-*-*-*-*-*-*/

                        new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.NumVal));
                        UpdateTaxDetails(RefNo);
                        new ItemLocController(getActivity()).UpdateOrderQOH(RefNo, "-", locCode);
                        Toast.makeText(getActivity(), "Order saved successfully..!", Toast.LENGTH_SHORT).show();
                        activity.selectedReturnHed = null;
                        activity.selectedPreHed = null;
                        mSharedPref.setDiscountClicked("0");
                        mSharedPref.setTotalValueDiscount("0");
                        mSharedPref.setValueDiscountPer("0");
                        mSharedPref.setValueDiscountRef("");
                        new SharedPref(getActivity()).setGlobalVal("KeyPayType" , "");
                        UtilityContainer.ClearReturnSharedPref(getActivity());
                        outlet = new CustomerController(getActivity()).getSelectedCustomerByCode(mSharedPref.getSelectedDebCode());
                        Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
                        intnt.putExtra("outlet", outlet);
                        startActivity(intnt);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Order Save Failed..", Toast.LENGTH_SHORT).show();
                    }

                }

            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();


        } else
            Toast.makeText(getActivity(), "Add items before save ...!", Toast.LENGTH_SHORT).show();


    }

    public void saveAndUploadSummaryDialog() {

        if (new OrderDetailController(getActivity()).getItemCount(RefNo) > 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View promptView = layoutInflater.inflate(R.layout.sales_management_van_sales_summary_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Do you want to save and upload the invoice ?");
            alertDialogBuilder.setView(promptView);

            final ListView lvProducts_Invoice = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Inv);
            ViewGroup.LayoutParams invItmparams = lvProducts_Invoice.getLayoutParams();
            ArrayList<OrderDetail> orderItemList = null;
            orderItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "SA","");
            if(orderItemList.size()>0){
                invItmparams.height = 200;
            }else {
                invItmparams.height = 0;
            }
            lvProducts_Invoice.setLayoutParams(invItmparams);

            lvProducts_Invoice.setAdapter(new OrderDetailsAdapter(getActivity(), orderItemList, mSharedPref.getSelectedDebCode()));

            //MMS - freeissues
            ListView lvProducts_freeIssue = (ListView) promptView.findViewById(R.id.lvProducts_Summary_freeIssue);
            ViewGroup.LayoutParams params = lvProducts_freeIssue.getLayoutParams();
            ArrayList<OrderDetail> orderFreeIssueItemList = null;
            orderFreeIssueItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "FI","FD");
            if(orderFreeIssueItemList.size()>0){
                params.height = 200;
            }else {
                params.height = 0;
            }

            lvProducts_freeIssue.setLayoutParams(params);
            lvProducts_freeIssue.setAdapter(new OrderFreeIssueDetailAdapter(getActivity(), orderFreeIssueItemList, mSharedPref.getSelectedDebCode()));

            //MMS - return item
            ListView lvProducts_return = (ListView) promptView.findViewById(R.id.lvProducts_Summary_Dialog_Ret);
            ViewGroup.LayoutParams retItmparams = lvProducts_return.getLayoutParams();

            ArrayList<OrderDetail> orderReturnItemList = null;
            orderReturnItemList = new OrderDetailController(getActivity()).getAllItemsAddedInCurrentSale(RefNo, "UR","MR");
            Log.d("**re", "saveSummaryDialog: "+orderReturnItemList.toString());
            if(orderReturnItemList.size()>0){
                retItmparams.height = 200;
            }else {
                retItmparams.height = 0;
            }
            lvProducts_return.setLayoutParams(retItmparams);

            lvProducts_return.setAdapter(new OrderReturnItemAdapter(getActivity(), orderReturnItemList, mSharedPref.getSelectedDebCode()));

            alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                public void onClick(final DialogInterface dialog, int id) {

                    Order ordHed = new Order();
                    ArrayList<Order> ordHedList = new ArrayList<Order>();
                    Order presale = new OrderController(getActivity()).getAllActiveOrdHed();
                    ordHed.setORDER_REFNO(RefNo);
                    ordHed.setORDER_DEBCODE(presale.getORDER_DEBCODE());
                    ordHed.setORDER_DEBNAME(presale.getORDER_DEBNAME());
                    ordHed.setORDER_ADDDATE(presale.getORDER_ADDDATE());
                    ordHed.setORDER_MANUREF(presale.getORDER_MANUREF());
                    ordHed.setORDER_REMARKS(presale.getORDER_REMARKS());
                    ordHed.setORDER_ADDMACH(presale.getORDER_ADDMACH());
                    ordHed.setORDER_ADDUSER(presale.getORDER_ADDUSER());
                    ordHed.setORDER_CURCODE(presale.getORDER_CURCODE());
                    ordHed.setORDER_CURRATE(presale.getORDER_CURRATE());
                    ordHed.setORDER_LOCCODE(presale.getORDER_LOCCODE());
                    ordHed.setORDER_CUSTELE(presale.getORDER_CUSTELE());
                    ordHed.setORDER_START_TIMESO(presale.getORDER_START_TIMESO());
                    ordHed.setORDER_CONTACT(presale.getORDER_CONTACT());
                    ordHed.setORDER_CUSADD1(presale.getORDER_CUSADD1());
                    ordHed.setORDER_CUSADD2(presale.getORDER_CUSADD2());
                    ordHed.setORDER_CUSADD3(presale.getORDER_CUSADD3());
                    ordHed.setORDER_TXNTYPE(presale.getORDER_TXNTYPE());
                    ordHed.setORDER_IS_ACTIVE(presale.getORDER_IS_ACTIVE());
                    ordHed.setORDER_IS_SYNCED(presale.getORDER_IS_SYNCED());
                    ordHed.setORDER_LOCCODE(presale.getORDER_LOCCODE());
                    ordHed.setORDER_AREACODE(presale.getORDER_AREACODE());
                    ordHed.setORDER_ROUTECODE(presale.getORDER_ROUTECODE());
                    ordHed.setORDER_COSTCODE(presale.getORDER_COSTCODE());
                    ordHed.setORDER_TAXREG(presale.getORDER_TAXREG());
                    ordHed.setORDER_TOURCODE(presale.getORDER_TOURCODE());
                    ordHed.setORDER_DELIVERY_DATE(presale.getORDER_DELIVERY_DATE());
                    ordHed.setORDER_PAYTYPE(presale.getORDER_PAYTYPE());
                    ordHed.setORDER_LATITUDE(presale.getORDER_LATITUDE());
                    ordHed.setORDER_LONGITUDE(presale.getORDER_LONGITUDE());
                    ordHed.setORDER_BPTOTALDIS("0");
                    ordHed.setORDER_BTOTALAMT("0");
                    ordHed.setORDER_TOTALTAX("0");
                    ordHed.setORDER_TOTALDIS(presale.getORDER_TOTALDIS());
                    ordHed.setORDER_TOTALAMT(lblNetVal.getText().toString());
                    ordHed.setORDER_TOTALDIS(lblReplacements.getText().toString());
                    ordHed.setORDER_TXNDATE(presale.getORDER_TXNDATE());
                    ordHed.setORDER_REPCODE(new SalRepController(getActivity()).getCurrentRepCode());
                    ordHed.setORDER_REFNO1("");
                    ordHed.setORDER_TOTQTY(lblQty.getText().toString());
                    ordHed.setORDER_TOTFREEQTY(iTotFreeQty + "");
                    ordHed.setORDER_SETTING_CODE(presale.getORDER_SETTING_CODE());
                    ordHed.setORDER_DEALCODE(presale.getORDER_DEALCODE());
                    ordHed.setORDER_TOTALMKRAMT(String.format("%.2f", totalMKReturn) + "");
                    ordHed.setORDER_TOTAL_VALUE_DISCOUNT(mSharedPref.getTotalValueDiscount());
                    ordHed.setORDER_VALUE_DISCOUNT_PER(mSharedPref.getValueDiscountPer());
                    ordHed.setORDER_VALUE_DISCOUNT_REF(mSharedPref.getValueDiscountRef());
                    ordHedList.add(ordHed);

                    if (new OrderController(getActivity()).createOrUpdateOrdHed(ordHedList) > 0) {
                        new PreProductController(getActivity()).mClearTables();
                        new OrderController(getActivity()).InactiveStatusUpdate(RefNo);
                        new OrderDetailController(getActivity()).InactiveStatusUpdate(RefNo);

                        final PreSalesActivity activity = (PreSalesActivity) getActivity();
                        /*-*-*-*-*-*-*-*-*-*-QOH update-*-*-*-*-*-*-*-*-*/

                        new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.NumVal));
                        UpdateTaxDetails(RefNo);
                        new ItemLocController(getActivity()).UpdateOrderQOH(RefNo, "-", locCode);
                        Toast.makeText(getActivity(), "Order saved successfully..!", Toast.LENGTH_SHORT).show();
                        activity.selectedReturnHed = null;
                        activity.selectedPreHed = null;
                        mSharedPref.setDiscountClicked("0");
                        mSharedPref.setTotalValueDiscount("0");
                        mSharedPref.setValueDiscountPer("0");
                        mSharedPref.setValueDiscountRef("");
                        new SharedPref(getActivity()).setGlobalVal("KeyPayType" , "");
                        UtilityContainer.ClearReturnSharedPref(getActivity());
                        outlet = new CustomerController(getActivity()).getSelectedCustomerByCode(mSharedPref.getSelectedDebCode());

                        try {
                            Upload(new OrderController(getActivity()).getAllUnSyncOrdHed());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                        Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
//                        intnt.putExtra("outlet", outlet);
//                        startActivity(intnt);
//                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Order Save Failed..", Toast.LENGTH_SHORT).show();
                    }

                }

            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();


        } else
            Toast.makeText(getActivity(), "Add items before save ...!", Toast.LENGTH_SHORT).show();


    }

    public void Upload(final ArrayList<Order> orders) throws InterruptedException {

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Uploading order records");
        dialog.show();

        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            if (orders.size() > 0) {

                for (final Order c : orders) {
                    try {
                        String content_type = "application/json";
                        ApiInterface apiInterface = ApiCllient.getClient(getActivity()).create(ApiInterface.class);
                        final Handler mHandler = new Handler(Looper.getMainLooper());
                        JsonParser jsonParser = new JsonParser();
                        String orderJson = new Gson().toJson(c);
                        JsonObject objectFromString = jsonParser.parse(orderJson).getAsJsonObject();
                        JsonArray jsonArray = new JsonArray();
                        jsonArray.add(objectFromString);

                        try{

                            FileWriter writer=new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+ "DBFSFA_OrderJson.txt");
                            writer.write(jsonArray.toString());
                            writer.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        Call<String> resultCall = apiInterface.uploadOrder(jsonArray, content_type);
                        resultCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                int status = response.code();
                                Log.d(">>>response code", ">>>res " + status);
                                Log.d(">>>response message", ">>>res " + response.message());
                                Log.d(">>>response body", ">>>res " + response.body().toString());
                                int resLength = response.body().toString().trim().length();
                                String resmsg = ""+response.body().toString();

                                if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            c.setORDER_IS_SYNCED("1");
                                            addRefNoResults(c.getORDER_REFNO() +" --> Success\n",orders.size());
                                            new OrderController(getActivity()).updateIsSynced(c.getORDER_REFNO(),"1");

                                        }
                                    });
                                 } else {
                                    Log.d( ">>response"+status,""+c.getORDER_REFNO() );
                                    c.setORDER_IS_SYNCED("0");
                                    new OrderController(getActivity()).updateIsSynced(c.getORDER_REFNO(),"0");
                                    addRefNoResults(c.getORDER_REFNO() +" --> Failed\n",orders.size());
                                }

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(getActivity(), "Error response "+t.toString(), Toast.LENGTH_SHORT).show();
                                Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
                                intnt.putExtra("outlet", outlet);
                                startActivity(intnt);
                                getActivity().finish();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Toast.makeText(getActivity(), "No Records to upload !", android.widget.Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();

    }

    private void addRefNoResults(String ref, int count) {
        dialog.dismiss();
        resultListOrder.add(ref);
        if (count == resultListOrder.size()) {
            mUploadResult(resultListOrder);
        }
    }

    public void mUploadResult(List<String> messages) {
        String msg = "";
        for (String s : messages) {
            msg += s;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle("Upload Order Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
                intnt.putExtra("outlet", outlet);
                startActivity(intnt);
                getActivity().finish();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }




    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void UpdateTaxDetails(String refNo) {

        ArrayList<OrderDetail> list = new OrderDetailController(getActivity()).getAllOrderDetailsForTaxUpdate(refNo);
        new OrderDetailController(getActivity()).UpdateItemTaxInfoWithDiscount(list, mSharedPref.getSelectedDebCode());
        new PreSaleTaxRGController(getActivity()).UpdateSalesTaxRG(list, mSharedPref.getSelectedDebCode());
        new PreSaleTaxDTController(getActivity()).UpdateSalesTaxDT(list);
    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*--*-*-*-*-*-*-*-*-*-*-*-*/

    public void mPauseinvoice() {

        if (new OrderDetailController(getActivity()).getItemCount(RefNo) > 0) {
            Order hed = new OrderController(getActivity()).getAllActiveOrdHed();
            outlet = new CustomerController(getActivity()).getSelectedCustomerByCode(hed.getORDER_DEBCODE());
            Intent intnt = new Intent(getActivity(), DebtorDetailsActivity.class);
            intnt.putExtra("outlet", outlet);
            startActivity(intnt);
            getActivity().finish();
        } else
            Toast.makeText(getActivity(), "Add items before pause ...!", Toast.LENGTH_SHORT).show();
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_PRE_SUMMARY"));

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "Please install Google Play services.", Toast.LENGTH_LONG).show();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mRefreshData();
        }
    }
    /*******************************************************************/
    @Override
    public void onAttach(Activity activity) {
        this.mactivity = activity;
        super.onAttach(mactivity);
        try {
            responseListener = (PreSalesResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }
    public void popupFeedBack(final Context context) {
        final Dialog repDialog = new Dialog(context);
        repDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        repDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        repDialog.setCancelable(false);
        repDialog.setCanceledOnTouchOutside(false);
        repDialog.setContentView(R.layout.feedback_popup);

        //initializations

        final ImageView happy = (ImageView) repDialog.findViewById(R.id.emoji_happy);
        final ImageView sad = (ImageView) repDialog.findViewById(R.id.emoji_bad);
        final ImageView normal = (ImageView) repDialog.findViewById(R.id.emoji_neutral);
       // final ImageView angry = (ImageView) repDialog.findViewById(R.id.emoji_angry);

        final TextView happylbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_happy) ;
        final TextView sadlbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_sad) ;
        final TextView neutrallbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_normal) ;
       // final TextView angrylbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_angry) ;

        repDialog.findViewById(R.id.emoji_happy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<HAPPY>>","<<HAPPY");
                new OrderController(context).updateFeedback("1",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.smile));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
               // angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
               // angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });
        repDialog.findViewById(R.id.emoji_bad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<SAD>>","<<SAD");
                new OrderController(context).updateFeedback("2",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
                //angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
               // angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });
        repDialog.findViewById(R.id.emoji_neutral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<NORMAL>>", "<<NORMAL");
                new OrderController(context).updateFeedback("3",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused));
            //    angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
              //  angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });

//        repDialog.findViewById(R.id.emoji_angry).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("<<<<ANGRY>>","<<ANGRY");
//                new OrderController(context).updateFeedback("4",RefNo);
//                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
//                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
//                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
//                angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry));
//
//                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                angrylbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
//            }
//        });

        repDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    saveSummaryDialog();

                    repDialog.dismiss();

            }
        });


        repDialog.show();
    }

    public void popupFeedBackforSaveAndUpload(final Context context) {
        final Dialog repDialog = new Dialog(context);
        repDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        repDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        repDialog.setCancelable(false);
        repDialog.setCanceledOnTouchOutside(false);
        repDialog.setContentView(R.layout.feedback_popup);

        //initializations

        final ImageView happy = (ImageView) repDialog.findViewById(R.id.emoji_happy);
        final ImageView sad = (ImageView) repDialog.findViewById(R.id.emoji_bad);
        final ImageView normal = (ImageView) repDialog.findViewById(R.id.emoji_neutral);
        // final ImageView angry = (ImageView) repDialog.findViewById(R.id.emoji_angry);

        final TextView happylbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_happy) ;
        final TextView sadlbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_sad) ;
        final TextView neutrallbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_normal) ;
        // final TextView angrylbl = (TextView) repDialog.findViewById(R.id.lbl_emoji_angry) ;

        repDialog.findViewById(R.id.emoji_happy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<HAPPY>>","<<HAPPY");
                new OrderController(context).updateFeedback("1",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.smile));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
                // angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                // angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });
        repDialog.findViewById(R.id.emoji_bad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<SAD>>","<<SAD");
                new OrderController(context).updateFeedback("2",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
                //angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                // angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });
        repDialog.findViewById(R.id.emoji_neutral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("<<<<NORMAL>>", "<<NORMAL");
                new OrderController(context).updateFeedback("3",RefNo);
                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused));
                //    angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry_bw));

                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
                //  angrylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
            }
        });

//        repDialog.findViewById(R.id.emoji_angry).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("<<<<ANGRY>>","<<ANGRY");
//                new OrderController(context).updateFeedback("4",RefNo);
//                happy.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.happiness_bw));
//                sad.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.sad_bw));
//                normal.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.confused_bw));
//                angry.setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.angry));
//
//                happylbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                sadlbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                neutrallbl.setTextColor(getActivity().getResources().getColor(R.color.half_black));
//                angrylbl.setTextColor(getActivity().getResources().getColor(R.color.achievecolor));
//            }
//        });

        repDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveAndUploadSummaryDialog();

                repDialog.dismiss();

            }
        });


        repDialog.show();
    }

    //-----------kaveesha ---27/11/2020---------------To get GPS location using google play service-------------------------
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation!=null) {
            mSharedPref.setGlobalVal("Longitude", String.valueOf(mLocation.getLongitude()));
            mSharedPref.setGlobalVal("Latitude", String.valueOf(mLocation.getLatitude()));
        }

        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location!=null) {
            mSharedPref.setGlobalVal("Longitude", String.valueOf(location.getLongitude()));
            mSharedPref.setGlobalVal("Latitude", String.valueOf(location.getLatitude()));
        }

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                getActivity().finish();

            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getContext().checkSelfPermission(permission)  == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }


    public void stopLocationUpdates()
    {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

}
