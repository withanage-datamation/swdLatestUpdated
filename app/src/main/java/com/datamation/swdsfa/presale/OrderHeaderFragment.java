package com.datamation.swdsfa.presale;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datamation.swdsfa.adapter.CustomerDebtAdapter;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.FItenrDetController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.controller.OrderDetailController;
import com.datamation.swdsfa.controller.OutstandingController;
import com.datamation.swdsfa.controller.RouteDetController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.helpers.PreSalesResponseListener;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.FddbNote;
import com.datamation.swdsfa.model.Order;
import com.datamation.swdsfa.view.ActivityHome;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.settings.ReferenceNum;
import com.datamation.swdsfa.view.DebtorDetailsActivity;
import com.datamation.swdsfa.view.PreSalesActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
//import com.bit.sfa.Settings.SharedPreferencesClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class OrderHeaderFragment extends Fragment implements DatePickerDialog.OnDateSetListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener{

    View view;
    private FloatingActionButton next;
    //public static EditText ordno, date, mNo, deldate, remarks;
    public String LOG_TAG = "OrderHeaderFragment";
    TextView lblCustomerName, outStandingAmt, lastBillAmt,lblPreRefno, deliveryDate;
    EditText  currnentDate,txtManual,txtRemakrs, txtRoute;
    MyReceiver r;
    SharedPref pref;
    PreSalesResponseListener preSalesResponseListener;
    PreSalesActivity activity;
    ImageButton img_bdate;
    Calendar Scalendar;
    int year, month , day;
    DatePickerDialog datePickerDialog;
    Spinner spPayment_Type;
    String formattedDate;
    String address = "No Address";
    double latitude = 0.0;
    double longitude = 0.0;
    Location mLocation;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    public OrderHeaderFragment() {
        // Required empty public constructor
    }

    public static OrderHeaderFragment newInstance() {
        OrderHeaderFragment fragment = new OrderHeaderFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_frag_promo_sale_header, container, false);
        activity = (PreSalesActivity)getActivity();
        next = (FloatingActionButton) view.findViewById(R.id.fab);
        pref = SharedPref.getInstance(getActivity());
        setHasOptionsMenu(true);
        final Date d = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); //change this
         formattedDate = simpleDateFormat.format(d);
        ActivityHome home = new ActivityHome();
        ReferenceNum referenceNum = new ReferenceNum(getActivity());
     //   localSP = new SharedPreferencesClass();

        Scalendar = Calendar.getInstance();
        Scalendar.add(Calendar.DAY_OF_YEAR , 1);
        Date tomorrow = Scalendar.getTime();

        next = (FloatingActionButton) view.findViewById(R.id.fab);

        lblCustomerName = (TextView) view.findViewById(R.id.customerName);
        outStandingAmt = (TextView) view.findViewById(R.id.lbl_Inv_outstanding_amt);
        lastBillAmt = (TextView) view.findViewById(R.id.lbl_inv_lastbill);
        lblPreRefno = (TextView) view.findViewById(R.id.invoice_no);
        currnentDate = (EditText) view.findViewById(R.id.lbl_InvDate);
        txtManual = (EditText) view.findViewById(R.id.txt_InvManual);
        txtRemakrs = (EditText) view.findViewById(R.id.txt_InvRemarks);
        txtRoute = (EditText)view.findViewById(R.id.txt_route);
        img_bdate = (ImageButton)view.findViewById(R.id.imgbtn_DeliveryDate);
        deliveryDate = (TextView)view.findViewById(R.id.txt_deliveryDate);
        spPayment_Type = (Spinner)view.findViewById(R.id.paytype_spinner) ;

        activity.selectedRetDebtor = activity.selectedDebtor;

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

       // lblCustomerName.setText(pref.getSelectedDebName());
        lblCustomerName.setText(new CustomerController(getActivity()).getCusNameByCode(pref.getSelectedDebCode()));
        //txtRoute.setText(new RouteController(getActivity()).getRouteNameByCode(pref.getSelectedDebRouteCode()));
        if(!new FItenrDetController(getActivity()).getRouteFromItenary(formattedDate).equals("")) {
            txtRoute.setText(new FItenrDetController(getActivity()).getRouteFromItenary(formattedDate));
        }else{
            txtRoute.setText(new RouteDetController(getActivity()).getRouteCodeByDebCode(pref.getSelectedDebCode()));
        }

        currnentDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        deliveryDate.setText(simpleDateFormat.format(tomorrow));
        outStandingAmt.setText(String.format("%,.2f", new OutstandingController(getActivity()).getDebtorBalance(pref.getSelectedDebCode())));
        txtRemakrs.setEnabled(true);
        txtManual.setEnabled(true);

        //select Delivery Date
        year  = Scalendar.get(Calendar.YEAR);
        month = Scalendar.get(Calendar.MONTH);
        day   = Scalendar.get(Calendar.DAY_OF_MONTH);

        /*already a header exist*/
        if (activity.selectedPreHed != null) {
            txtManual.setText(activity.selectedPreHed.getORDER_MANUREF());
            txtRemakrs.setText(activity.selectedPreHed.getORDER_REMARKS());
            lblPreRefno.setText(activity.selectedPreHed.getORDER_REFNO());
            //mSaveInvoiceHeader();
        } else { /*No header*/

            lblPreRefno.setText(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal)));
            //lblPreRefno.setText("/0001");
        }

        if (new OrderController(getActivity()).isAnyActiveOrderHed(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal))))
        {
            Order hed = new OrderController(getActivity()).getAllActiveOrdHed();

            txtManual.setText(hed.getORDER_MANUREF());
            txtRemakrs.setText(hed.getORDER_REMARKS());
        }

        /*Select Delivery Date*/
        img_bdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog = datePickerDialog.newInstance(OrderHeaderFragment.this,year,month,day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setAccentColor(Color.parseColor("#0072BA"));
                datePickerDialog.setTitle("");
                datePickerDialog.show(getActivity().getFragmentManager(),"DatePickerDialog");

            }
        });

        /*create payment Type*/
        List<String> listPayType  = new ArrayList<String>();

        listPayType.add("Cash");
        listPayType.add("Credit");
        listPayType.add("Other");

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, listPayType);
       dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spPayment_Type.setAdapter(dataAdapter1);

                spPayment_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       new SharedPref(getActivity()).setGlobalVal("KeyPayType" , spPayment_Type.getSelectedItem().toString());
                        Log.v("PAYMENT TYPE", spPayment_Type.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

        outStandingAmt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.customer_debtor, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Customer outstanding...");
                alertDialogBuilder.setView(promptView);

                final ListView listView = (ListView) promptView.findViewById(R.id.lvCusDebt);
                ArrayList<FddbNote> list = new OutstandingController(getActivity()).getDebtInfo(SharedPref.getInstance(getActivity()).getSelectedDebCode());
                listView.setAdapter(new CustomerDebtAdapter(getActivity(), list));

                alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lblCustomerName.getText().toString().equals("")|| lblPreRefno.getText().toString().equals("")||txtRoute.getText().toString().equals(""))
                {
                    Log.d("<<<lblCustomerName<<<<", " " + lblCustomerName.getText().toString());
                    Log.d("<<<lblPreRefno<<<<", " " + lblPreRefno.getText().toString());
                    Log.d("<<<txtRoute<<<<", " " + txtRoute.getText().toString());
                    preSalesResponseListener.moveBackToCustomer_pre(0);
                    Toast.makeText(getActivity(), "Can not proceed without Route...", Toast.LENGTH_LONG).show();
  checkdate();              }
                else
                {
                    //preSalesResponseListener.moveNextToCustomer_pre(1);
                   if(lblPreRefno.length() == 12){//check reference number
                       SaveSalesHeader();
                   }
                   else
                   {
                       Toast.makeText(getActivity(), "Synchronize is not successful.Please sync before save order header...", Toast.LENGTH_LONG).show();
                   }

                }

            }
        });

        return view;
    }
     /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/



    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_close, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.close:
                if(new OrderDetailController(getActivity()).isAnyActiveOrders()){
                    MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                            .content("You have active orders. Cannot back without complete.")
                            .positiveText("OK")
                            .positiveColor(getResources().getColor(R.color.material_alert_positive_button))
//                            .negativeText("No")
//                            .negativeColor(getResources().getColor(R.color.material_alert_negative_button))

                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    super.onNeutral(dialog);
                                }
                            })
                            .build();
                    materialDialog.setCancelable(false);
                    materialDialog.setCanceledOnTouchOutside(false);
                    materialDialog.show();
                }else{
                    MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                            .content("Do you want to back?")
                            .positiveText("Yes")
                            .positiveColor(getResources().getColor(R.color.material_alert_positive_button))
                            .negativeText("No")
                            .negativeColor(getResources().getColor(R.color.material_alert_negative_button))

                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    Intent back = new Intent(getActivity(), DebtorDetailsActivity.class);
                                    back.putExtra("outlet",new CustomerController(getActivity()).getSelectedCustomerByCode(SharedPref.getInstance(getActivity()).getSelectedDebCode()));
                                    startActivity(back);
                                    getActivity().finish();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    super.onNeutral(dialog);
                                }
                            })
                            .build();
                    materialDialog.setCancelable(false);
                    materialDialog.setCanceledOnTouchOutside(false);
                    materialDialog.show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    public void SaveSalesHeader() {

        if (lblPreRefno.getText().length() > 0)
        {
            Order hed =new Order();
            hed.setORDER_REFNO(lblPreRefno.getText().toString());
            hed.setORDER_DEBCODE(pref.getSelectedDebCode());
            hed.setORDER_DEBNAME(new CustomerController(getActivity()).getCusNameByCode(pref.getSelectedDebCode()));
            hed.setORDER_TXNDATE(currnentDate.getText().toString());
            hed.setORDER_DELIVERY_DATE(deliveryDate.getText().toString());
            hed.setORDER_PAYTYPE(new SharedPref(getActivity()).getGlobalVal("KeyPayType"));
            hed.setORDER_ROUTECODE(txtRoute.getText().toString().trim());
            hed.setORDER_MANUREF(txtManual.getText().toString());
            hed.setORDER_REMARKS(txtRemakrs.getText().toString());
            hed.setORDER_IS_ACTIVE("1");
            hed.setORDER_ADDDATE(currentTime());
            hed.setORDER_ADDMACH(new SalRepController(getActivity()).getMacId());
            hed.setORDER_START_TIMESO(currentTime());
            hed.setORDER_REPCODE(new SalRepController(getActivity()).getCurrentRepCode().trim());
            hed.setORDER_LONGITUDE(""+pref.getGlobalVal("Longitude"));
            hed.setORDER_LATITUDE(""+pref.getGlobalVal("Latitude"));
            hed.setORDER_AREACODE(new CustomerController(getActivity()).getAreaByDebCode(pref.getSelectedDebCode()));
            if (!new SalRepController(getActivity()).getDealCode().trim().equals(""))
            {
                hed.setORDER_DEALCODE(new SalRepController(getActivity()).getDealCode().trim());
            }
            hed.setORDER_ADDRESS(address);
            hed.setORDER_ADDUSER(new SalRepController(getActivity()).getCurrentRepCode().trim());
            hed.setORDER_TOTAL_VALUE_DISCOUNT(pref.getTotalValueDiscount());
            hed.setORDER_VALUE_DISCOUNT_PER(pref.getValueDiscountPer());
            hed.setORDER_VALUE_DISCOUNT_REF(pref.getValueDiscountRef());

            activity.selectedPreHed = hed;

            ArrayList<Order> ordHedList=new ArrayList<Order>();
            OrderController ordHedDS =new OrderController(getActivity());
            ordHedList.add(hed);

            if (ordHedDS.createOrUpdateOrdHed(ordHedList)>0)
            {
                preSalesResponseListener.moveNextToCustomer_pre(1);
                Toast.makeText(getActivity(),"Order Header Saved...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void mRefreshHeader() {

        if (SharedPref.getInstance(getActivity()).getGlobalVal("PrekeyCustomer").equals("Y")) {

        currnentDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            if(!new FItenrDetController(getActivity()).getRouteFromItenary(formattedDate).equals("")) {
                txtRoute.setText(new FItenrDetController(getActivity()).getRouteFromItenary(formattedDate));
            }else{
                txtRoute.setText(new RouteDetController(getActivity()).getRouteCodeByDebCode(pref.getSelectedDebCode()));
            }
        //deldate.setEnabled(true);
        txtRemakrs.setEnabled(true);
        txtManual.setEnabled(true);
        lblCustomerName.setText(new CustomerController(getActivity()).getCusNameByCode(pref.getSelectedDebCode()));
        lblPreRefno.setText(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal)));
       // String debCode= new SharedPref(getActivity()).getGlobalVal("PrekeyCusCode");

        if (activity.selectedPreHed != null) {
            //if (home.selectedDebtor == null)
               // home.selectedDebtor = new FmDebtorDS(getActivity()).getSelectedCustomerByCode(home.selectedOrdHed.getFORDHED_DEB_CODE());

//                cusName.setText(home.selectedDebtor.getCusName());
//                ordno.setText(home.selectedOrdHed.getORDHED_REFNO());
//                deldate.setText(home.selectedOrdHed.getORDHED_DELV_DATE());
//                mNo.setText(home.selectedOrdHed.getORDHED_MANU_REF());
//                remarks.setText(home.selectedOrdHed.getORDHED_REMARKS());

        } else {

            lblPreRefno.setText(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal)));
            //deldate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            SaveSalesHeader();
        }

        } else {
            Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
            txtRemakrs.setEnabled(false);
            txtManual.setEnabled(false);
        }

    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new OrderHeaderFragment.MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_HEADER"));
        checkdate();

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "Please install Google Play services.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        Date dateToday = null;
        Date date2 = null;

        try {

            dateToday = sdf.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            date2 = sdf.parse(year + "-" + (monthOfYear + 1) + "-" + (dayOfMonth));

        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date2.equals(dateToday) || date2.after(dateToday)) {
            deliveryDate.setText(year + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", (dayOfMonth)));
        }
        else {
            Toast.makeText(getActivity(),"Can't set previous date as a delivery date.Please enter valide date",Toast.LENGTH_LONG).show();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OrderHeaderFragment.this.mRefreshHeader();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            preSalesResponseListener = (PreSalesResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }

    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void checkdate() {
        int i = android.provider.Settings.Global.getInt(getActivity().getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
        /* If option is selected */
        if (i > 0)
        {



            /* if not selected */
        } else {
            Toast.makeText(getActivity(), "Date is wrong..Please correct!!!", android.widget.Toast.LENGTH_LONG).show();
            /* Show Date/time settings dialog */
            startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
        }

    }

    //--------------kaveesha ---------------27/11/2020--------------To get GPS location using Google play service-----------------------
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
            pref.setGlobalVal("Longitude", String.valueOf(mLocation.getLongitude()));
            pref.setGlobalVal("Latitude", String.valueOf(mLocation.getLatitude()));
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
            pref.setGlobalVal("Longitude", String.valueOf(location.getLongitude()));
            pref.setGlobalVal("Latitude", String.valueOf(location.getLatitude()));
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
