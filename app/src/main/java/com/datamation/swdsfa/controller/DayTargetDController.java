package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DayTargetD;
import com.datamation.swdsfa.model.ItemTarDet;

import java.util.ArrayList;

/*
    Created by kaveesha - 16-03-2022
 */

public class DayTargetDController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "DayTargetDController ";


    // table
    public static final String TABLE_FDAY_TARGETD = "fDayTargetD";
    // table attributes
    public static final String FDAYTARGETD_ID = "Id";
    public static final String FDAYTARGETD_REPNO = "RefNo";
    public static final String FDAYTARGETD_SBRANDCODE = "SBrandCode";
    public static final String FDAYTARGETD_TXNDATE = "TxnDate";
    public static final String FDAYTARGETD_TARGET_PERCEN = "TargetPercen";
    public static final String FDAYTARGETD_DAY = "Day";

    // create String
    public static final String CREATE_FDAY_TARGETD_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FDAY_TARGETD +
            " (" + FDAYTARGETD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FDAYTARGETD_REPNO + " TEXT, " +
            FDAYTARGETD_TXNDATE + " TEXT, " + FDAYTARGETD_SBRANDCODE + " TEXT, " + FDAYTARGETD_TARGET_PERCEN + " TEXT, " + FDAYTARGETD_DAY + " TEXT); ";


    public DayTargetDController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int CreateOrUpdateDayTargetD(ArrayList<DayTargetD> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (DayTargetD dayTargetD : list) {

//                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDAY_TARGETD + " WHERE " +
//                        FDAYTARGETD_REPNO + "='" + dayTargetD.getRefNo() + "'  and " + FDAYTARGETD_ITEMCODE +
//                        " = '" + dayTargetD.getItemcode()+ "' and " + FDAYTARGETD_TXNDATE +"  = '" + dayTargetD.getTxnDate()+ "'", null);

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDAY_TARGETD + " WHERE " +
                        FDAYTARGETD_DAY +"  = '" + dayTargetD.getDay()+ "' AND "+ FDAYTARGETD_SBRANDCODE +"  = '" + dayTargetD.getItemcode()+ "'", null);


                ContentValues values = new ContentValues();

                values.put(FDAYTARGETD_REPNO, dayTargetD.getRefNo());
                values.put(FDAYTARGETD_TXNDATE, dayTargetD.getTxnDate());
                values.put(FDAYTARGETD_SBRANDCODE, dayTargetD.getItemcode());
                values.put(FDAYTARGETD_TARGET_PERCEN, dayTargetD.getTargetPercen());
                values.put(FDAYTARGETD_DAY, dayTargetD.getDay());

                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FDAY_TARGETD, values,  FDAYTARGETD_DAY + " = '" + dayTargetD.getDay()+"'", null);
               //     dB.update(TABLE_FDAY_TARGETD, values, FDAYTARGETD_REPNO + " = '" +dayTargetD.getRefNo() + "' and " + FDAYTARGETD_ITEMCODE + " = '" + dayTargetD.getItemcode()+"' and " + FDAYTARGETD_TXNDATE + " = '" + dayTargetD.getTxnDate()+"'", null);
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FDAY_TARGETD, null, values);
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDAY_TARGETD, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FDAY_TARGETD, null, null);
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
