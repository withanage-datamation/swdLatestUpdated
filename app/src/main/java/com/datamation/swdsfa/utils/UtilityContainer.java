package com.datamation.swdsfa.utils;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.swdsfa.R;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.helpers.SQLiteBackUp;
import com.datamation.swdsfa.helpers.SQLiteRestore;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.SalRep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * common functions
 */

public class UtilityContainer {


    //---------------------------------------------------------------------------------------------------------------------------------------------------

//    public static void  showSnackBarError(View v, String message, Context context) {
//        Snackbar snack = Snackbar.make(v, "" + message, Snackbar.LENGTH_SHORT);
//        View view = snack.getView();
//        view.setBackgroundColor(Color.parseColor("#CB4335"));
//        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//        tv.setTextColor(Color.WHITE);
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
//        params.gravity = Gravity.CENTER;
//        view.setLayoutParams(params);
//        snack.show();
//
//    }

  /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public static void mLoadFragment(Fragment fragment, Context context) {

        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(R.anim.enter, R.anim.exit_to_right);
        ft.replace(R.id.fragmentContainer, fragment, fragment.getClass().getSimpleName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }
    protected static void sacaSnackbar (Context context, View view, String s)
    {

    }

    public static void ClearReturnSharedPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("returnKeyRoute");
        editor.remove("returnKeyCostCode");
        editor.remove("returnKeyLocCode");
        editor.remove("returnKeyTouRef");
        editor.remove("returnKeyAreaCode");
        editor.remove("returnKeyRouteCode");
        editor.remove("returnKeyTourPos");
        editor.remove("returnkeyCustomer");
        editor.remove("returnkeyReasonCode");
        editor.remove("returnKeyRepCode");
        editor.remove("returnKeyDriverCode");
        editor.remove("returnKeyHelperCode");
        editor.remove("returnKeyLorryCode");
        editor.remove("returnKeyReason");
        editor.commit();
    }
    public static void ClearReceiptSharedPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("ReckeyPayModePos");
        editor.remove("ReckeyPayMode");
        editor.remove("isHeaderComplete");
        editor.remove("ReckeyHeader");
        editor.remove("ReckeyRecAmt");
        editor.remove("ReckeyRemnant");
        editor.remove("ReckeyCHQNo");
        editor.remove("Rec_Start_Time");
        editor.commit();
    }
    public static void ClearCustomerSharedPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("selected_out_id");
        editor.remove("selected_out_name");
        editor.remove("selected_out_route_code");
        editor.remove("selected_pril_code");
        editor.commit();
    }
    public static void ClearDBName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("Dist_DB");
        editor.commit();
    }



    //-----------------------------------------------------------------------------------------------------------------------------------------------------

    public static void mPrinterDialogbox(final Context context) {

        View promptView = LayoutInflater.from(context).inflate(R.layout.settings_printer_layout, null);
        final EditText serverURL = (EditText) promptView.findViewById(R.id.et_mac_address);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(promptView)
                .setTitle("Printer MAC Address")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button bOk = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button bClose = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                bOk.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (serverURL.length() > 0) {

                            if (validate(serverURL.getText().toString().toUpperCase()))
                            {
                                SharedPref.getInstance(context).setMacAddress(serverURL.getText().toString().toUpperCase());
                                dialog.dismiss();
                            }

                            else
                            {
                                Toast.makeText(context, "Enter Valid MAC Address", Toast.LENGTH_LONG).show();
                            }
                        } else
                            Toast.makeText(context, "Type in the MAC Address", Toast.LENGTH_LONG).show();
                    }
                });

                bClose.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public static boolean validate(String mac) {
        Pattern p = Pattern.compile("^([a-fA-F0-9]{2}[:-]){5}[a-fA-F0-9]{2}$");
        Matcher m = p.matcher(mac);
        return m.find();
    }

    public static void mRepsDetailsDialogBox(final Context context) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.settings_reps_details_layout, null);

        final EditText etUserName = (EditText) promptView.findViewById(R.id.et_rep_username);
        final EditText etRepCode = (EditText) promptView.findViewById(R.id.et_rep_code);
        final EditText etPreFix = (EditText) promptView.findViewById(R.id.et_rep_prefix);
        final EditText etLocCode = (EditText) promptView.findViewById(R.id.et_rep_loc_code);
        final EditText etAreaCode = (EditText) promptView.findViewById(R.id.et_rep_area_code);
        final EditText etDealerCode = (EditText) promptView.findViewById(R.id.et_rep_deal_code);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Sales Executive");
        alertDialogBuilder.setView(promptView);
        SalRep Vre = new SalRepController(context).getSaleRepDet(new SalRepController(context).getCurrentRepCode());

        //for (SalRep salRep : Vre) {
            etUserName.setText(Vre.getNAME());
            etRepCode.setText(Vre.getRepCode());
            etPreFix.setText(Vre.getPREFIX());
            etLocCode.setText(Vre.getLOCCODE());

        //}

        alertDialogBuilder.setCancelable(false).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
    public static void validateRestore(final Context context)
    {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_sqlite_password_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.et_password);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Go",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                /** DO THE METHOD HERE WHEN PROCEED IS CLICKED*/
                                String user_text = (userInput.getText()).toString();

                                /** CHECK FOR USER'S INPUT **/
                                if (user_text.equals("adminrs@swd"))
                                {
                                    Log.d(user_text, "HELLO THIS IS THE MESSAGE CAUGHT :)");
                                    mLoadFragment(new SQLiteRestore(), context);

                                }
                                else{
                                    Log.d(user_text,"string is empty");
                                    String message = "The password you have entered is incorrect." + " \n \n" + "Please try again!";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Error");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            validateRestore(context);
                                        }
                                    });
                                    builder.create().show();

                                }
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.dismiss();
                            }

                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    public static void mSQLiteDatabase(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.settings_sqlite_database_layout);
        dialog.setTitle("SQLite Backup/Restore");

        final Button b_backups = (Button) dialog.findViewById(R.id.b_backups);
        final Button b_restore = (Button) dialog.findViewById(R.id.b_restore);

        b_backups.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SQLiteBackUp backUp = new SQLiteBackUp(context);
                backUp.exportDB();
                dialog.dismiss();
            }
        });

        b_restore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               // mLoadFragment(new SQLiteRestore(), context);
                validateRestore(context);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
