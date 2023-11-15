package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DayTargetD;
import com.datamation.swdsfa.model.TargetCat;

import java.util.ArrayList;

/*
    Created by kaveesha - 16-03-2022
 */

public class TargetCatController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "TargetCatController ";


    // table
    public static final String TABLE_FTARGET_CAT = "fTargetCat";
    // table attributes
    public static final String FTARGETD_CAT_ID = "Id";
    public static final String FTARGETD_CAT_INCENTIVE1 = "Incentive1";
    public static final String FTARGETD_CAT_INCENTIVE2 = "Incentive2";
    public static final String FTARGETD_CAT_TARCATCODE = "TarCatCode";
    public static final String FTARGETD_CAT_TARCATNAME = "TarcatName";


    // create String
    public static final String CREATE_FTARGET_CAT_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FTARGET_CAT +
            " (" + FTARGETD_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FTARGETD_CAT_INCENTIVE1 + " TEXT, " +
            FTARGETD_CAT_INCENTIVE2 + " TEXT, " + FTARGETD_CAT_TARCATCODE + " TEXT, " + FTARGETD_CAT_TARCATNAME + " TEXT); ";


    public TargetCatController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int CreateOrUpdateTargetCat(ArrayList<TargetCat> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (TargetCat targetCat : list) {

           Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FTARGET_CAT + " WHERE " +
                   FTARGETD_CAT_TARCATCODE +"  = '" + targetCat.getTarCatCode()+ "'", null);


                ContentValues values = new ContentValues();

                values.put(FTARGETD_CAT_INCENTIVE1, targetCat.getIncentive1());
                values.put(FTARGETD_CAT_INCENTIVE2, targetCat.getIncentive2());
                values.put(FTARGETD_CAT_TARCATCODE, targetCat.getTarCatCode());
                values.put(FTARGETD_CAT_TARCATNAME, targetCat.getTarcatName());


                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FTARGET_CAT, values,  FTARGETD_CAT_TARCATCODE + " = '" + targetCat.getTarCatCode()+"'", null);
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FTARGET_CAT, null, values);
                    Log.v(TAG, "Inserted " + count);
                }
                cursor.close();
            }

        } catch (Exception e) {
            Log.v(TAG, e.toString());
        } finally {
            dB.close();
        }
        return count;

    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FTARGET_CAT, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FTARGET_CAT, null, null);
                Log.v("Success", success + "");
            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return count;

    }

}
