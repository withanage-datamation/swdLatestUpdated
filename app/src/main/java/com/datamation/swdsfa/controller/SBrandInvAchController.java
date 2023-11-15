package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.Bank;
import com.datamation.swdsfa.model.SBrandInvAch;

import java.util.ArrayList;

/*
    create by kaveesha - 25-04-2022
 */

public class SBrandInvAchController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "SBrandInvAchController ";

    // table
    public static final String TABLE_FSBRAND_INV_ACH = "fSBrandInvAch";
    // table attributes
    public static final String FSBRAND_INV_ID = "bankre_id";
    public static final String FSBRAND_INV_ACHIEVEMENT = "Achievement";
    public static final String FSBRAND_INV_SBRANDCODE = "SBrandCode";
    public static final String FSBRAND_INV_TXNDATE = "TxnDate";

    // create String
    public static final String CREATE_FSBRAND_INV_ACH_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FSBRAND_INV_ACH + " (" + FSBRAND_INV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FSBRAND_INV_SBRANDCODE + " TEXT, " + FSBRAND_INV_TXNDATE + " TEXT, " + FSBRAND_INV_ACHIEVEMENT + " TEXT); ";


    public SBrandInvAchController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateSBrandInvAchieve(ArrayList<SBrandInvAch> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (SBrandInvAch invAch : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FSBRAND_INV_ACH + " WHERE " +
                        FSBRAND_INV_SBRANDCODE +"  = '" + invAch.getFSBRAND_INV_SBRANDCODE()+ "' AND "+ FSBRAND_INV_TXNDATE +"  = '" + invAch.getFSBRAND_INV_TXNDATE()+ "'", null);

                ContentValues values = new ContentValues();

                values.put(FSBRAND_INV_SBRANDCODE, invAch.getFSBRAND_INV_SBRANDCODE());
                values.put(FSBRAND_INV_TXNDATE, invAch.getFSBRAND_INV_TXNDATE());
                values.put(FSBRAND_INV_ACHIEVEMENT, invAch.getFSBRAND_INV_ACHIEVEMENT());

                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FSBRAND_INV_ACH, values, FSBRAND_INV_SBRANDCODE + " = '" +invAch.getFSBRAND_INV_SBRANDCODE() + "' and " + FSBRAND_INV_TXNDATE + " = '" + invAch.getFSBRAND_INV_TXNDATE()+"'", null);
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FSBRAND_INV_ACH, null, values);
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FSBRAND_INV_ACH, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FSBRAND_INV_ACH, null, null);
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
