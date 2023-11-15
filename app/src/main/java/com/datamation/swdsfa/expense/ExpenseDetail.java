package com.datamation.swdsfa.expense;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datamation.swdsfa.adapter.SalesExpenseDetailAdapter;
import com.datamation.swdsfa.adapter.SalesExpenseGridDetails;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.controller.ExpenseController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.model.Expense;
import com.datamation.swdsfa.settings.ReferenceNum;

import com.datamation.swdsfa.controller.DayExpDetController;
import com.datamation.swdsfa.controller.DayExpHedController;
import com.datamation.swdsfa.controller.ReasonController;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.DayExpDet;
import com.datamation.swdsfa.model.DayExpHed;
import com.datamation.swdsfa.view.ActivityHome;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
;import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ExpenseDetail extends Fragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    public static String DebCODE, RESULT;
    View view;
    EditText Txndate, Amount, ExpCode, Remark, RefNo;
    ArrayList<Expense> list2;
    Button btnAdd;
    ListView lv_invent_load;
    ReferenceNum referenceNum;
    ArrayList<DayExpDet> loadlist;
    int seqno = 0;
    String sExpenseCode;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    FloatingActionMenu fam;
    SharedPref sharedPref;
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

    private double latitude;
    private double longitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.expense_hed_responsive_layout, container, false);
        //setHasOptionsMenu(true);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        getActivity().setTitle("Expence Details");

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        seqno = 0;
        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        // fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);

        RefNo = (EditText) view.findViewById(R.id._refNo);
        Txndate = (EditText) view.findViewById(R.id._date);
        Remark = (EditText) view.findViewById(R.id._remrk);
        Amount = (EditText) view.findViewById(R.id._amount);
        ExpCode = (EditText) view.findViewById(R.id._expcode);
        ImageButton ReSearch = (ImageButton) view.findViewById(R.id.reason_search);

        referenceNum = new ReferenceNum(getActivity());
        RefNo.setText(referenceNum.getCurrentRefNo(getResources().getString(R.string.ExpenseNumVal)));
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        lv_invent_load = (ListView) view.findViewById(R.id.lv_loading_sum);
        btnAdd.setOnClickListener(this);
        //fatchData();

        sharedPref = SharedPref.getInstance(getActivity());
        Log.v("Lati in Expense>>>>>", sharedPref.getGlobalVal("Latitude"));
        Log.v("Longi in Expense>>>>", sharedPref.getGlobalVal("Longitude"));
        ReSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.reason_search:
                        ExpenseDetailsDialogbox("");
                        break;
                    default:
                        break;
                }

            }
        });

        /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

        lv_invent_load.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                DayExpDet fexpdet = loadlist.get(position);
                deleteOrderDialog(getActivity(), fexpdet.getEXPDET_REFNO().toString(), fexpdet.getEXPDET_EXPCODE().toString());
                return false;
            }
        });

        /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

        lv_invent_load.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                clearTextFields();
                btnAdd.setText("UPDATE");
                DayExpDet fnondet = loadlist.get(position);
                Amount.setText(fnondet.getEXPDET_AMOUNT());
                ExpCode.setText(new ReasonController(getActivity()).getReasonByReaCode(fnondet.getEXPDET_EXPCODE()));
                sExpenseCode = fnondet.getEXPDET_EXPCODE();
            }
        });

        currentDate();

        fam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });
        fam.setClosedOnTouchOutside(true);

//        fabPause.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //mPauseExpense();
//            }
//        });

        fabSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSummaryDialog(getActivity());
            }
        });

        fabDiscard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                undoEditingData(getActivity(), RefNo.getText().toString());
            }
        });


        return view;
    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    private void currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Txndate.setText(dateFormat.format(date));
    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    public void ExpenseDetailsDialogbox(String searchword) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.nonprod_retailer_search);
        final SearchView search = (SearchView) dialog.findViewById(R.id.rt_search);
        final ListView locList = (ListView) dialog.findViewById(R.id.rt_product_items);
        dialog.setCancelable(true);

        list2 = new ExpenseController(getActivity()).getAllExpense("");
        locList.clearTextFilter();
        locList.setAdapter(new SalesExpenseDetailAdapter(getActivity(), list2));
        locList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpCode.setText(list2.get(position).getFEXPENSE_NAME());
                sExpenseCode = list2.get(position).getFEXPENSE_CODE();
                dialog.dismiss();
            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                locList.clearTextFilter();
                list2 = new ExpenseController(getActivity()).getAllExpense(newText);
                locList.setAdapter(new SalesExpenseDetailAdapter(getActivity(), list2));
                return false;
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {

            case R.id.btn_add:

                btnAdd.setText("ADD");

                if (Amount.getText().length() > 0 && ExpCode.getText().length() > 0) {

                    DayExpDet expdet = new DayExpDet();
                    DayExpDetController expDetDS = new DayExpDetController(getActivity());
                    ArrayList<DayExpDet> ExpDetList = new ArrayList<DayExpDet>();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();

                    try {

                        seqno++;
                        expdet.setEXPDET_REFNO(RefNo.getText() + "");
                        expdet.setEXPDET_EXPCODE(sExpenseCode);
                        expdet.setEXPDET_AMOUNT(Amount.getText() + "");
                        expdet.setEXPDET_TXNDATE(dateFormat.format(date));

                        ExpDetList.add(expdet);

                        if (expDetDS.createOrUpdateExpenseDet(ExpDetList) > 0) {
                            clearTextFields();
                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_LONG).show();
                            fatchData();

                        } else {
                            Toast.makeText(getActivity(), "record save Unsuccessful", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), "Added Not successfully", Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    public void fatchData() {
        try {
            DayExpDetController detDS = new DayExpDetController(getActivity());
            //loadlist = detDS.getAllExpDetails(referenceNum.getCurrentRefNo(getResources().getString(R.string.ExpenseNumVal)));
            loadlist = detDS.getAllExpDetails(RefNo.getText().toString());
            lv_invent_load.setAdapter(new SalesExpenseGridDetails(getActivity(), loadlist));
        } catch (NullPointerException e) {
            Log.v("Loading Error", e.toString());
        }
    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    public void clearTextFields() {
        Remark.setText("");
        Amount.setText("");
        ExpCode.setText("");
        sExpenseCode = "";

    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    private void saveSummaryDialog(final Context context) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Are you sure you want to save this entry?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (new DayExpDetController(getActivity()).getAllExpDetails(RefNo.getText() + "").size() > 0) {

                            DayExpHed exphed = new DayExpHed();
                            ArrayList<DayExpHed> ExpHedList = new ArrayList<DayExpHed>();

                            String refno = RefNo.getText().toString();
                            exphed.setEXP_REFNO(RefNo.getText() + "");
                            exphed.setEXP_REPCODE(new SalRepController(getActivity()).getCurrentRepCode().trim());
                            exphed.setEXP_TXNDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            exphed.setEXP_REMARK(Remark.getText() + "");
                            exphed.setEXP_ADDDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            exphed.setEXP_ACTIVESTATE("0");
                            exphed.setEXP_IS_SYNCED("0");
                            exphed.setEXP_LATITUDE(String.valueOf(latitude));
                            //exphed.setEXP_LATITUDE(SharedPref.getInstance(getActivity()).getGlobalVal("Latitude").equals("***") ? "0.00" : SharedPref.getInstance(getActivity()).getGlobalVal("Latitude"));
                            exphed.setEXP_LONGITUDE(String.valueOf(longitude));
                           // exphed.setEXP_LONGITUDE(SharedPref.getInstance(getActivity()).getGlobalVal("Longitude").equals("***") ? "0.00" : SharedPref.getInstance(getActivity()).getGlobalVal("Longitude"));
//                            exphed.setEXP_LATITUDE( sharedPref.getGlobalVal("Latitude").equals("") ? "0.00" : sharedPref.getGlobalVal("Latitude"));
//                            exphed.setEXP_LONGITUDE(sharedPref.getGlobalVal("Longitude").equals("") ? "0.00" : sharedPref.getGlobalVal("Longitude"));

                            // exphed.setEXP_ADDRESS(localSP.getString("GPS_Address", "").toString());
//                            exphed.setEXP_REPNAME(new SalRepController(getActivity()).getSaleRep(new SalRepController(getActivity()).getCurrentRepCode()).getNAME());
                            exphed.setEXP_REPNAME(new SalRepController(getActivity()).getSaleRep(exphed.getEXP_REPCODE()).getNAME());
                            exphed.setEXP_ADDMACH(sharedPref.getMacAddress());
                            exphed.setEXP_AREACODE("non");
                            exphed.setEXP_COSTCODE("000");
                            exphed.setEXP_TOTAMT(new DayExpDetController(getActivity()).getTotalExpenseSumReturns(refno));
                            ExpHedList.add(exphed);

                            if (new DayExpHedController(getActivity()).createOrUpdateDayExpHed(ExpHedList) > 0) {


                                referenceNum.NumValueUpdate(getResources().getString(R.string.ExpenseNumVal));
                                Toast.makeText(getActivity(), "Successfully saved Expense. ", Toast.LENGTH_LONG).show();
//                        UtilityContainer.mLoadFragment(new FragmentTools(), getActivity());
                                Intent intent = new Intent(getActivity(), ActivityHome.class);
                                startActivity(intent);
                                getActivity().finish();
                            }

                        } else {
                            Toast.makeText(getActivity(), "No Data For Save", Toast.LENGTH_LONG).show();

                        }

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

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    private void deleteOrderDialog(final Context context, final String refno, final String expcode) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int count = new DayExpDetController(context).DeleteRec(refno, expcode);
                if (count > 0) {
                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();

                    fatchData();
                    clearTextFields();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    private void undoEditingData(final Context context, final String RefNo) {

        final DayExpHedController expHED = new DayExpHedController(getActivity());
        final DayExpDetController expDET = new DayExpDetController(getActivity());

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Are you sure you want to Undo this entry?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ReferenceNum referenceNum = new ReferenceNum(getActivity());

                        try {
                            //expHED.undoExpHedByID(referenceNum.getCurrentRefNo(getResources().getString(R.string.ExpenseNumVal))); // FExpHed
                            expHED.undoExpHedByID(RefNo); // FExpHed
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            //expDET.ExpDetByID(referenceNum.getCurrentRefNo(getResources().getString(R.string.ExpenseNumVal))); // FExpDet
                            expDET.ExpDetByID(RefNo); // FExpDet
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                UtilityContainer.mLoadFragment(new FragmentTools(), getActivity());
                        Intent intent = new Intent(getActivity(), ActivityHome.class);
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getActivity(), "Undo Success", Toast.LENGTH_LONG).show();

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


    /*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*/

    public void onTaskCompleted(String result) {
        try {
            if (!result.equals("") || !result.equals("No Address")) {


                // activity.selectedexpHed.setEXP_ADDRESS(result);

            }
        } catch (Exception e) {
            Log.v("Selected OrdHed", e.toString());
        }
    }

    // ---------------kaveesha --------- 27/11/2020 ---------- To get GPS location using google play service --------------
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
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
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
//            mSharedPref.setGlobalVal("Longitude", String.valueOf(mLocation.getLongitude()));
//            mSharedPref.setGlobalVal("Latitude", String.valueOf(mLocation.getLatitude()));
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

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


    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

//    public void mPauseExpense() {
//
//        if (new DayExpDetController(getActivity()).getExpenceCount(RefNo.getText().toString().trim()) > 0)
//        {
//            UtilityContainer.mLoadFragment(new FragmentTools(), getActivity());
//            getActivity().finish();
//        }
//
//        else
//        {
//            Toast.makeText(getActivity(), "Add expenses before pause ...!", Toast.LENGTH_SHORT).show();
//        }
//
//    }

}
