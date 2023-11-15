package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.ItemTarDet;
import com.datamation.swdsfa.model.ItemTarHed;

import java.util.ArrayList;

/*
    Created by kaveesha - 16-03-2022
 */

public class ItemTarDetController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "ItemTarDetController ";


    // table
    public static final String TABLE_FITEM_TAR_DET = "fItemTarDet";
    // table attributes
    public static final String FITEMTARDET_ID = "Id";
    public static final String FITEMTARDET_REPNO = "RefNo";
    public static final String FITEMTARDET_SBRANDCODE = "SBrandCode";
   // public static final String FITEMTARDET_ITEMCODE = "Itemcode";
    public static final String FITEMTARDET_TXNDATE = "TxnDate";
    public static final String FITEMTARDET_VOLUME = "Volume";

    // create String
    public static final String CREATE_FITEM_TAR_DET_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FITEM_TAR_DET +
            " (" + FITEMTARDET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FITEMTARDET_REPNO + " TEXT, " +
            FITEMTARDET_TXNDATE + " TEXT, " + FITEMTARDET_SBRANDCODE + " TEXT, " + FITEMTARDET_VOLUME + " TEXT); ";


    public ItemTarDetController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int CreateOrUpdateItemTarDet(ArrayList<ItemTarDet> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (ItemTarDet itemTarDet : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FITEM_TAR_DET + " WHERE " +
                        FITEMTARDET_REPNO + "='" + itemTarDet.getRefNo() + "'  and " + FITEMTARDET_SBRANDCODE +
                        " = '" + itemTarDet.getItemcode()+ "'", null);

                ContentValues values = new ContentValues();

                values.put(FITEMTARDET_REPNO, itemTarDet.getRefNo());
                values.put(FITEMTARDET_TXNDATE, itemTarDet.getTxnDate());
                values.put(FITEMTARDET_SBRANDCODE, itemTarDet.getItemcode());
                values.put(FITEMTARDET_VOLUME, itemTarDet.getVolume());

                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FITEM_TAR_DET, values, FITEMTARDET_REPNO + " = '" +itemTarDet.getRefNo() + "' and " + FITEMTARDET_SBRANDCODE + " = '" + itemTarDet.getItemcode()+"'", null);
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FITEM_TAR_DET, null, values);
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FITEM_TAR_DET, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FITEM_TAR_DET, null, null);
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
