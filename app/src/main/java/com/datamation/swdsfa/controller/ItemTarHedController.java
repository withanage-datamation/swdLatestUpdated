package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.Bank;
import com.datamation.swdsfa.model.ItemTarHed;

import java.util.ArrayList;

/*
    Created by kaveesha - 15-03-2022
 */

public class ItemTarHedController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "ItemTarHedController ";


    // table
    public static final String TABLE_FITEM_TAR_HED = "fItemTarHed";
    // table attributes
    public static final String FITEMTARHED_ID = "Id";
    public static final String FITEMTARHED_REPNO = "RefNo";
    public static final String FITEMTARHED_DEAL_CODE = "DealCode";
    public static final String FITEMTARHED_MANUREF = "ManuRef";
    public static final String FITEMTARHED_MONTH = "Month";
    public static final String FITEMTARHED_REPCODE = "RepCode";
    public static final String FITEMTARHED_TXNDATE = "TxnDate";
    public static final String FITEMTARHED_YEAR = "year";
    public static final String FITEMTARHED_ADD_DATE = "AddDate";
    public static final String FITEMTARHED_ADD_MACH = "AddMach";
    public static final String FITEMTARHED_ADD_USER = "AddUser";

    // create String
    public static final String CREATE_FITEM_TAR_HED_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FITEM_TAR_HED +
            " (" + FITEMTARHED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FITEMTARHED_REPNO + " TEXT, " +
            FITEMTARHED_TXNDATE + " TEXT, " + FITEMTARHED_REPCODE + " TEXT, " + FITEMTARHED_DEAL_CODE + " TEXT, " +
            FITEMTARHED_MANUREF + " TEXT, " + FITEMTARHED_MONTH + " TEXT, " + FITEMTARHED_YEAR + " TEXT, " + FITEMTARHED_ADD_DATE + " TEXT, " +
            FITEMTARHED_ADD_MACH + " TEXT, " + FITEMTARHED_ADD_USER + " TEXT); ";


    public ItemTarHedController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateItemTarHed(ArrayList<ItemTarHed> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (ItemTarHed itemTarHed : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FITEM_TAR_HED + " WHERE " + FITEMTARHED_REPNO + "='" + itemTarHed.getRefNo() + "'", null);

                ContentValues values = new ContentValues();

                values.put(FITEMTARHED_REPNO, itemTarHed.getRefNo());
                values.put(FITEMTARHED_TXNDATE, itemTarHed.getTxnDate());
                values.put(FITEMTARHED_DEAL_CODE, itemTarHed.getDealCode());
                values.put(FITEMTARHED_MANUREF, itemTarHed.getManuRef());
                values.put(FITEMTARHED_MONTH, itemTarHed.getMonth());
                values.put(FITEMTARHED_REPCODE, itemTarHed.getRepCode());
                values.put(FITEMTARHED_YEAR, itemTarHed.getYear());
                values.put(FITEMTARHED_ADD_DATE, itemTarHed.getAddDate());
                values.put(FITEMTARHED_ADD_MACH, itemTarHed.getAddMach());
                values.put(FITEMTARHED_ADD_USER, itemTarHed.getAddUser());

                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FITEM_TAR_HED, values, FITEMTARHED_REPNO + "=?", new String[]{itemTarHed.getRefNo().toString()});
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FITEM_TAR_HED, null, values);
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FITEM_TAR_HED, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FITEM_TAR_HED, null, null);
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
