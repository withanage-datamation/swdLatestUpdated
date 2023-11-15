package com.datamation.swdsfa.nonproductive;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.adapter.NonPrdDetails;
import com.datamation.swdsfa.adapter.NonProductiveReasonAdapter;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.DayNPrdDetController;
import com.datamation.swdsfa.controller.DayNPrdHedController;
import com.datamation.swdsfa.controller.ReasonController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.Customer;
import com.datamation.swdsfa.model.DayNPrdDet;
import com.datamation.swdsfa.model.DayNPrdHed;
import com.datamation.swdsfa.model.Reason;
import com.datamation.swdsfa.settings.ReferenceNum;
import com.datamation.swdsfa.view.DebtorDetailsActivity;
import com.datamation.swdsfa.view.NonProductiveActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/*created by rashmi*/
public class NonProductiveDetail extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener{

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    View view;
    EditText  Retailer, Remark, RefNo;
    ImageButton ReSearch;
    TextView Txndate,TxtReason;
    ArrayList<Reason> list2;
    Button btnAdd;
    ListView lv_invent_load;
    ReferenceNum referenceNum;
    ArrayList<DayNPrdDet> loadlist;
    String sRefno,Start_Time;
    FloatingActionMenu fam;
    FloatingActionButton fabPause, fabDiscard, fabSave;
    SharedPref mSharedPref;
    Activity thisActivity;
    NonProductiveActivity mainActivity;
    private Customer outlet;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;


    MyReceiver r;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //view = inflater.inflate(R.layout.non_test_layout, container, false);
        view = inflater.inflate(R.layout.non_productive_details_responsive_layout, container, false);

        //final ActivityHome activity = (ActivityHome) getActivity();
        thisActivity = getActivity();
        mainActivity = (NonProductiveActivity)getActivity();
        lv_invent_load = (ListView) view.findViewById(R.id.lv_loading_sum);
        RefNo = (EditText) view.findViewById(R.id._nRefNo);
        Txndate = (TextView) view.findViewById(R.id._ndate);
        Remark = (EditText) view.findViewById(R.id._nremrk);
        Retailer = (EditText) view.findViewById(R.id._nRetailer);
        TxtReason = (TextView) view.findViewById(R.id._nReason);
        ReSearch = (ImageButton) view.findViewById(R.id.reason_search);
        referenceNum = new ReferenceNum(getActivity());

        btnAdd = (Button) view.findViewById(R.id.btn_add);
        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        //fabPause = (FloatingActionButton) view.findViewById(R.id.fab2);
        fabDiscard = (FloatingActionButton) view.findViewById(R.id.fab3);
        fabSave = (FloatingActionButton) view.findViewById(R.id.fab1);

        mSharedPref = SharedPref.getInstance(getActivity());
        RefNo.setText(referenceNum.getCurrentRefNo(getResources().getString(R.string.nonprdVal)));
        Retailer.setText(mSharedPref.getSelectedDebName());

        Start_Time = currentTime();

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

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAdd.setText("ADD");
                if (Retailer.getText().length() != 0 && TxtReason.getText().length() != 0) {

                    DayNPrdDet nondet = new DayNPrdDet();
                    DayNPrdDetController nonDetDS = new DayNPrdDetController(getActivity());
                    ArrayList<DayNPrdDet> NONDetList = new ArrayList<DayNPrdDet>();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();

                    try {
                        nondet.setNONPRDDET_REFNO(RefNo.getText() + "");
                        nondet.setNONPRDDET_REPCODE(new SalRepController(getActivity()).getCurrentRepCode().trim());
                        nondet.setNONPRDDET_TXNDATE(dateFormat.format(date));
                        nondet.setNONPRDDET_REASON(TxtReason.getText().toString() + "");
                        nondet.setNONPRDDET_REMARK(Remark.getText().toString() + "");
                        nondet.setNONPRDDET_REASON_CODE(new ReasonController(getActivity()).getReaCodeByName(TxtReason.getText().toString() + ""));
                        nondet.setNONPRDDET_IS_SYNCED("0");
                        NONDetList.add(nondet);

                        if (nonDetDS.createOrUpdateNonPrdDet(NONDetList) > 0) {
                            clearTextFields();
                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_LONG).show();
                            fatchData(RefNo.getText().toString().trim() + "");

                        } else {
                            Toast.makeText(getActivity(), "Addition unsuccessfully", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), "Added Not successfully", Toast.LENGTH_LONG).show();
                }

            }
        });

        ReSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ReasonDetailsDialogbox("");
            }
        });


        fam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fam.isOpened()) {
                    fam.close(true);
                }
            }
        });

        fabSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSummaryDialog(getActivity());
            }
        });

        fabDiscard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                undoEditingData(getActivity());
            }
        });



		/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_invent_load.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //new InvDetController(getActivity()).restFreeIssueData(RefNo);
                deleteNonProDialog(position);
                return true;
            }
        });

		/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_invent_load.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                clearTextFields();
                btnAdd.setText("UPDATE");

                DayNPrdDet fnondet = loadlist.get(position);
                CustomerController customer = new CustomerController(getActivity());
//                Customer cus = customer.getSelectedCustomerByCode(fnondet.getNonprddet_d)
//                Retailer.setText(new CustomerController.getS(fnondet.getNONPRDDET_DEBCODE()));
//                DebCODE = fnondet.getNONPRDDET_DEBCODE().toString();
                TxtReason.setText(fnondet.getNONPRDDET_REASON());

            }
        });

        currentDate();
        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void currentDate() {
        Txndate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/


    public void ReasonDetailsDialogbox(String searchword) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.nonprod_retailer_search);
        final SearchView search = (SearchView) dialog.findViewById(R.id.rt_search);
        final ListView locList = (ListView) dialog.findViewById(R.id.rt_product_items);
        dialog.setCancelable(true);

        list2 = new ReasonController(getActivity()).getAllNonPrdReasons();
        locList.clearTextFilter();
        locList.setAdapter(new NonProductiveReasonAdapter(getActivity(), list2));
        locList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TxtReason.setText(list2.get(position).getFREASON_NAME());
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
                ReasonController locds = new ReasonController(getActivity());
                locList.clearTextFilter();
                list2 = locds.getDebDetails(newText);
                locList.setAdapter(new NonProductiveReasonAdapter(getActivity(), list2));
                return false;
            }
        });
        dialog.show();
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void fatchData(String sRefno) {

        try {

            DayNPrdDetController detDS = new DayNPrdDetController(getActivity());
            loadlist = detDS.getAllnonprdDetails(sRefno);
            lv_invent_load.setAdapter(new NonPrdDetails(getActivity(), loadlist));
        } catch (NullPointerException e) {
            Log.v("Loading Error", e.toString());
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void clearTextFields() {
        //Remark.setText("");
       // Retailer.setText("");
        TxtReason.setText("");
      //  DebCODE = "";

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

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
                        super.onPositive(dialog);
                        //ActivityHome activity = (ActivityHome) getActivity();
                        DayNPrdDetController deb = new DayNPrdDetController(getActivity());
                        if (deb.getAllnonprdDetails(RefNo.getText() + "").size() > 0) {
                            // new AddressAyncTask(getActivity(),this).execute();

                            DayNPrdHed nonhed = new DayNPrdHed();
                            ArrayList<DayNPrdHed> NONHedList = new ArrayList<DayNPrdHed>();
                            DayNPrdHedController nonHedDS = new DayNPrdHedController(getActivity());

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = new Date();

                            nonhed.setNONPRDHED_REFNO(RefNo.getText() + "");
                            nonhed.setNONPRDHED_REPCODE(new SalRepController(getActivity()).getCurrentRepCode().trim());
                            nonhed.setNONPRDHED_TXNDATE(dateFormat.format(date));
                            nonhed.setNONPRDHED_TRANSBATCH("");
                            nonhed.setNONPRDHED_REMARKS(Remark.getText() + "");
                            nonhed.setNONPRDHED_ADDDATE(dateFormat.format(date));
                            //nonhed.setNONPRDHED_ADDMACH(mSharedPref.getString("MAC_Address", "No MAC Address").toString());
                            nonhed.setNONPRDHED_LONGITUDE(SharedPref.getInstance(getActivity()).getGlobalVal("Longitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Longitude"));
                            nonhed.setNONPRDHED_LATITUDE(SharedPref.getInstance(getActivity()).getGlobalVal("Latitude").equals("") ? "0.00" : mSharedPref.getGlobalVal("Latitude"));
                            nonhed.setNONPRDHED_ADDUSER(new SalRepController(getActivity()).getCurrentRepCode().trim());
                            nonhed.setNONPRDHED_COSTCODE("000");
                            nonhed.setNONPRDHED_DEALCODE("");
                            nonhed.setNONPRDHED_IS_SYNCED("0");
                            nonhed.setNONPRDHED_DEBCODE(mSharedPref.getSelectedDebCode());
                            nonhed.setNONPRDHED_ADDMACH(mSharedPref.getMacAddress());
                            nonhed.setNONPRDHED_START_TIME(Start_Time);
                            nonhed.setNONPRDHED_END_TIME(currentTime());
//                            nonhed.setNONPRDHED_ADDRESS(localSP.getString("GPS_Address", "").toString());
                            NONHedList.add(nonhed);

                            if (nonHedDS.createOrUpdateNonPrdHed(NONHedList) > 0) {
                                new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.nonprdVal));

                                outlet = new CustomerController(context).getSelectedCustomerByCode(mSharedPref.getSelectedDebCode());
//                        activity.cusPosition = 0;
//                        activity.selectedDebtor = null;
                                // UtilityContainer.ClearNonSharedPref(getActivity());
                                Toast.makeText(getActivity(), "Non Productive saved successfully !", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(), DebtorDetailsActivity.class);
                                intent.putExtra("outlet", outlet);
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

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

//    private void deleteOrderDialog(final Context context, final String _id) {
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//        // alertDialogBuilder.setTitle(title);
//        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
//        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
//        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//
//                int count = new DayNPrdDetController(context).deleteOrdDetByID(_id);
//                if (count > 0) {
//                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();
//                    fatchData(_id);
//                    clearTextFields();
//                }
//            }
//        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//
//            }
//        });
//
//        AlertDialog alertD = alertDialogBuilder.create();
//
//        alertD.show();
//    }

    private void deleteNonProDialog(final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int count = new DayNPrdDetController(getActivity()).deleteOrdDetByID(loadlist.get(position).getNONPRDDET_REFNO(), loadlist.get(position).getNONPRDDET_REASON_CODE());
                if (count > 0) {
                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();
                    fatchData(loadlist.get(position).getNONPRDDET_REFNO());
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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void undoEditingData(final Context context) {

        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Are you sure you want to Undo this entry?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        try {
                            new DayNPrdHedController(getActivity()).undoOrdHedByID(RefNo.getText().toString().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            new DayNPrdDetController(getActivity()).OrdDetByRefno(RefNo.getText().toString().trim());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //activity.cusPosition = 0;
                        mainActivity.selectedNonDebtor = null;
                        Intent intnt = new Intent(getActivity(),DebtorDetailsActivity.class);
                        startActivity(intnt);
                        getActivity().finish();
                        //UtilityContainer.ClearNonSharedPref(getActivity());
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

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onResume() {
        super.onResume();
        r = new NonProductiveDetail.MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_NP_DETAILS"));

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "Please install Google Play services.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void mRefreshHeader() {
        mSharedPref = SharedPref.getInstance(getActivity());
        Log.d("TEST_NON_PRO", "123456");
        RefNo.setText(referenceNum.getCurrentRefNo(getResources().getString(R.string.nonprdVal)));
        CustomerController debtor = new CustomerController(getActivity());
        //Log.d("DEBTOR CHECK",debtor.getCustNameByCode(new SharedPref(getActivity()).getGlobalVal("NonkeyCusCode")));
        Customer cus = debtor.getSelectedCustomerByCode(SharedPref.getInstance(getActivity()).getGlobalVal("NonkeyCusCode"));
        if (mSharedPref.getGlobalVal("NonkeyCustomer").equals("Y")) {
            //RefNo.setText(sRefno);
            btnAdd.setEnabled(true);
            ReSearch.setEnabled(true);
            Remark.setEnabled(true);

            Retailer.setText(cus.getCusName());


        } else {
            Toast.makeText(getActivity(), "Select a customer to continue...", Toast.LENGTH_SHORT).show();
            RefNo.setEnabled(false);
            Retailer.setEnabled(false);
            btnAdd.setEnabled(false);
            //btnDeliDate.setEnabled(false);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NonProductiveDetail.this.mRefreshHeader();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        this.thisActivity = activity;
        super.onAttach(activity);
    }

    //---------- kaveesha ---------- 27/11/2020 ---------------- To get GPS location using google play service ----------------

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
