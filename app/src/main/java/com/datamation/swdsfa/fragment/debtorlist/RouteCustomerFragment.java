package com.datamation.swdsfa.fragment.debtorlist;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.datamation.swdsfa.R;
import com.datamation.swdsfa.adapter.CustomerAdapter;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.RouteDetController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.customer.NewCustomerActivity;
import com.datamation.swdsfa.dialog.CustomProgressDialog;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.Customer;
import com.datamation.swdsfa.view.DebtorDetailsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class RouteCustomerFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
        , com.google.android.gms.location.LocationListener {

    View view;
    ListView lvCustomers;
    SharedPref mSharedPref;
    private NetworkFunctions networkFunctions;
    ArrayList<Customer> customerList;
    CustomerAdapter customerAdapter;
    private Customer debtor;
    String routeCode="";
    Switch mySwitch;
    double lati = 0.0;
    double longi = 0.0;
    private boolean isGPSSwitched;
    SearchView mSearchDeb;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_route_customer, container, false);

        lvCustomers = (ListView)view.findViewById(R.id.route_cus_lv);
        mSharedPref = new SharedPref(getActivity());
        networkFunctions = new NetworkFunctions(getActivity());
        mySwitch = (Switch)view.findViewById(R.id.gps_switch);
        mSearchDeb = (SearchView)view.findViewById(R.id.et_all_deb_search);

//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            mySwitch.setText("SWITCH");
//        } else {
//            mySwitch.setTextOff("OFF");
//            mySwitch.setTextOn("ON");
//        }

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

        lvCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

//                if (!(gpsTracker.canGetLocation())) {
//                    gpsTracker.showSettingsAlert();
//                }

                /* Check whether automatic date time option checked or not */
                int i = android.provider.Settings.Global.getInt(getActivity().getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0);
                /* If option is selected */
                if (i > 0) {

//                    if (mSharedPref.isSelectedDebtorStart())
//                    {
//                        if (mSharedPref.isSelectedDebtorEnd())
//                        {
                            debtor = customerList.get(position);

                            if (isGPSSwitched)
                            {
                                mSharedPref.setGPSDebtor("RY");
                            }
                            else
                            {
                                mSharedPref.setGPSDebtor("RN");
                            }

//                            if (isValidateCustomer(debtor))
//                            {
//                                Intent intent = new Intent(getActivity(), DebtorDetailsActivity.class);
//                                intent.putExtra("outlet",debtor);
//                                mSharedPref.clearPref();
//                                mSharedPref.setSelectedDebCode(debtor.getCusCode());
//                                mSharedPref.setSelectedDebName(debtor.getCusName());
//                                mSharedPref.setSelectedDebRouteCode(new RouteDetController(getActivity()).getRouteCodeByDebCode(debtor.getCusCode()));
//                                mSharedPref.setSelectedDebtorPrilCode(debtor.getCusPrilCode());
//                                //mSharedPref.setSelectedDebtorEnd(false);
//                                //mSharedPref.setSelectedDebtorStart(true);
//                                startActivity(intent);
//                                getActivity().finish();



                    try {
                        if (!debtor.getCusImage().isEmpty()) {
                            Intent intent = new Intent(getActivity(), DebtorDetailsActivity.class);
                            intent.putExtra("outlet", debtor);
                            mSharedPref.clearPref();
                            mSharedPref.setSelectedDebCode(debtor.getCusCode());
                            mSharedPref.setSelectedDebName(debtor.getCusName());
                            mSharedPref.setSelectedDebRouteCode(new RouteDetController(getActivity()).getRouteCodeByDebCode(debtor.getCusCode()));
                            mSharedPref.setSelectedDebtorPrilCode(debtor.getCusPrilCode());
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            mSharedPref.clearPref();
                            mSharedPref.setSelectedDebCode(debtor.getCusCode());
                            mSharedPref.setSelectedDebName(debtor.getCusName());
                            mSharedPref.setSelectedDebRouteCode(new RouteDetController(getActivity()).getRouteCodeByDebCode(debtor.getCusCode()));
                            mSharedPref.setSelectedDebtorPrilCode(debtor.getCusPrilCode());
                            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                                    .content("This debtor have not set business image. Do you want to update image first?")
                                    .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                                    .positiveText("Yes")
                                    .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                                    .negativeText("No, Continue to order.")
                                    .callback(new MaterialDialog.ButtonCallback() {

                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            super.onPositive(dialog);

                                            Intent intent = new Intent(getActivity(), NewCustomerActivity.class);
                                            intent.putExtra("outlet", debtor);
                                            intent.putExtra("allCusFrag",88);
                                            startActivity(intent);
                                            getActivity().finish();

                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog) {
                                            super.onNegative(dialog);


                                            Intent intent = new Intent(getActivity(), DebtorDetailsActivity.class);
                                            intent.putExtra("outlet", debtor);
                                            mSharedPref.clearPref();
                                            mSharedPref.setSelectedDebCode(debtor.getCusCode());
                                            mSharedPref.setSelectedDebName(debtor.getCusName());
                                            mSharedPref.setSelectedDebRouteCode(new RouteDetController(getActivity()).getRouteCodeByDebCode(debtor.getCusCode()));
                                            mSharedPref.setSelectedDebtorPrilCode(debtor.getCusPrilCode());
                                            dialog.dismiss();
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    })
                                    .build();
                            materialDialog.setCanceledOnTouchOutside(false);
                            materialDialog.show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                            //}
//                        }
//                        else
//                        {
//                            Toast.makeText(getActivity(), "Please end call to previously selected debtor..", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    else
//                    {
//                        debtor = customerList.get(position);
//
//                        if (isValidateCustomer(debtor))
//                        {
//                            Intent intent = new Intent(getActivity(), DebtorDetailsActivity.class);
//                            intent.putExtra("outlet",debtor);
//                            mSharedPref.setSelectedDebCode(debtor.getCusCode());
//                            mSharedPref.setSelectedDebName(debtor.getCusName());
//                            mSharedPref.setSelectedDebRouteCode(new RouteDetController(getActivity()).getRouteCodeByDebCode(debtor.getCusCode()));
//                            mSharedPref.setSelectedDebtorPrilCode(debtor.getCusPrilCode());
//                            mSharedPref.setSelectedDebtorEnd(false);
//                            mSharedPref.setSelectedDebtorStart(true);
//                            startActivity(intent);
//
//                        }
//                    }
                }
                else {
                    Toast.makeText(getActivity(), "Please tick the 'Automatic Date and Time' option to continue..", Toast.LENGTH_LONG).show();
                    /* Show Date/time settings dialog */
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                }
            }
        });

        isGPSSwitched = false;

        final String rCode = new SalRepController(getActivity()).getCurrentRepCode().trim();

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                  //  gpsTracker = new GPSTrackerNew(getActivity());
                    //gpsTracker = new GPSTracker(getActivity());

//                    if (gpsTracker.canGetLocation())
//                    {
                        isGPSSwitched = true;

                        if (!mSharedPref.getGlobalVal("Latitude").equals("") && !mSharedPref.getGlobalVal("Longitude").equals(""))
                        {
                            lati = Double.parseDouble(mSharedPref.getGlobalVal("Latitude"));
                            longi = Double.parseDouble(mSharedPref.getGlobalVal("Longitude"));

                            new getRouteGPSCustomer().execute();

                            mSearchDeb.setQuery("", false);
                            mSearchDeb.clearFocus();
                        }
                        else
                        {
                            startActivityForResult(new Intent(Settings.ACTION_LOCALE_SETTINGS), 0);
                        }
//                    }
//                    else
//                    {
//                        gpsTracker.showSettingsAlert();
//                    }
                }
                else
                {
                    isGPSSwitched = false;
                    lati = 0.0;
                    longi = 0.0;

                    new getRouteCustomer().execute();

                    mSearchDeb.setQuery("", false);
                    mSearchDeb.clearFocus();

                }
            }
        });

        mSearchDeb.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!isGPSSwitched)
                {
                    customerList.clear();
                    customerList = new CustomerController(getActivity()).getAllCustomersByRoute(query);
                    lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
                }
                else
                {
                    customerList.clear();
                    customerList = new CustomerController(getActivity()).getGPSCustomersByRoute(lati, longi,query);
                    lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!isGPSSwitched)
                {
                    customerList.clear();
                    customerList = new CustomerController(getActivity()).getAllCustomersByRoute(newText);
                    lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
                }
                else
                {
                    customerList.clear();
                    customerList = new CustomerController(getActivity()).getGPSCustomersByRoute(lati, longi,newText);
                    lvCustomers.setAdapter(new CustomerAdapter(getActivity(), customerList));
                }

                return true;
            }
        });

        new getRouteCustomer().execute();

        return view;
    }

    private class getRouteCustomer extends AsyncTask<String, Integer, Boolean> {
        int totalRecords=0;
        CustomProgressDialog pdialog;

        public getRouteCustomer() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog  = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Authenticating...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try
            {
                customerList = new CustomerController(getActivity()).getAllCustomersByRoute("");

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            if (result)
            {
                customerAdapter = new CustomerAdapter(getActivity(), customerList);
                lvCustomers.setAdapter(customerAdapter);
            }
            pdialog.setMessage("Finalizing data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    private class getRouteGPSCustomer extends AsyncTask<String, Integer, Boolean> {
        int totalRecords=0;
        CustomProgressDialog pdialog;

        public getRouteGPSCustomer() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog  = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Authenticating...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try
            {
                Log.d("ROUTE_DEBTORS", "COORDINATES: " + lati + ", " + longi);
                customerList = new CustomerController(getActivity()).getGPSCustomersByRoute(lati, longi,"");

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            if (result)
            {
                customerAdapter = new CustomerAdapter(getActivity(), customerList);
                lvCustomers.setAdapter(customerAdapter);
            }
            pdialog.setMessage("Finalizing data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    public boolean isValidateCustomer(Customer customer)
    {
//        if (customer.getCusRoute().equals(""))
//        {
//            errorDialog("Route Error", "Selected debtor has no route to continue...");
//            return false;
//        }
//        else
        if (customer.getCusStatus().equals("I"))
        {
            errorDialog("Status Error", "Selected debtor is inactive to continue...");
            return false;
        }
        else if (customer.getCreditPeriod().equals("N"))
        {
            errorDialog("Period Error", "Credit period expired for Selected debtor...");
            return false;
        }
        else if (Double.parseDouble(customer.getCreditLimit())==0.00)
        {
            errorDialog("Limit Error", "Not enough credit limit for Selected debtor...");
            return false;
        }
        else if (customer.getCreditStatus().equals("N"))
        {
            errorDialog("Credit Status Error", "Credit status not valid for Selected debtor...");
            return false;
        }
        else
        {
            return true;
        }
    }

    public void errorDialog(String title, String msg)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.info);


        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //----------kaveesha -----------27/11/2020--------------To get GPS location using Google play serveice----------------------
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
    public void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(getActivity(), "Please install Google Play services.", Toast.LENGTH_LONG).show();
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
