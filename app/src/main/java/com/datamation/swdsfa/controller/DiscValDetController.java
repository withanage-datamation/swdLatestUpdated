package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DiscValDet;
import com.datamation.swdsfa.model.DiscValHed;
import com.datamation.swdsfa.model.Discslab;

import java.util.ArrayList;

public class DiscValDetController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "DiscValDetController ";

    // table
    public static final String TABLE_FDISCVALDET = "fDisValDet";
    // table attributes
    public static final String FDISCVALDET_ID = "Id";
    public static final String FDISCVALDET_DIS_PER= "DisPer";
    public static final String FDISCVALDET_DIS_AMT = "DisAmt";
    public static final String FDISCVALDET_SEQNO = "SeqNo";
    public static final String FDISCVALDET_REFNO = "RefNo";
    public static final String FDISCVALDET_VDATEF = "Vdatef";
    public static final String FDISCVALDET_VDATET = "Vdatet";


    // create String
    public static final String CREATE_FDISCVALDET_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FDISCVALDET + " (" + FDISCVALDET_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + FDISCVALDET_REFNO + " TEXT, " + FDISCVALDET_DIS_PER + " TEXT, " + FDISCVALDET_DIS_AMT + " TEXT, " +
            FDISCVALDET_SEQNO + " TEXT, " + FDISCVALDET_VDATEF + " TEXT, " + FDISCVALDET_VDATET + " TEXT); ";


    public DiscValDetController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateDiscValDet(ArrayList<DiscValDet> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (DiscValDet discValDet : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALDET + " WHERE " + FDISCVALDET_REFNO + "='" + discValDet.getFDISCVALDET_REFNO() + "' AND " + FDISCVALDET_SEQNO + "='" + discValDet.getFDISCVALDET_SEQNO() + "'", null);

                ContentValues values = new ContentValues();

                values.put(FDISCVALDET_REFNO, discValDet.getFDISCVALDET_REFNO());
                values.put(FDISCVALDET_DIS_PER, discValDet.getFDISCVALDET_DIS_PER());
                values.put(FDISCVALDET_DIS_AMT, discValDet.getFDISCVALDET_DIS_AMT());
                values.put(FDISCVALDET_SEQNO, discValDet.getFDISCVALDET_SEQNO());
                values.put(FDISCVALDET_VDATEF, discValDet.getFDISCVALDET_VDATEF());
                values.put(FDISCVALDET_VDATET, discValDet.getFDISCVALDET_VDATET());

                if (cursor.getCount() > 0) {

                    dB.update(TABLE_FDISCVALDET, values,  FDISCVALDET_REFNO + " =? AND " + FDISCVALDET_SEQNO + "=?", new String[]{discValDet.getFDISCVALDET_REFNO().toString(), discValDet.getFDISCVALDET_SEQNO().toString()});

                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FDISCVALDET, null, values);
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
    public DiscValDet getDiscountInfo(String refno, double amt) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        DiscValDet discValDet = null;

        String selectQuery = "select * from fDisValDet where refno='" + refno + "' and " + amt + " between CAST(Vdatef as double) and CAST(Vdatet as double)";

        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                discValDet = new DiscValDet();
                discValDet.setFDISCVALDET_REFNO(cursor.getString(cursor.getColumnIndex(FDISCVALDET_REFNO)));
                discValDet.setFDISCVALDET_DIS_PER(cursor.getString(cursor.getColumnIndex(FDISCVALDET_DIS_PER)));
                discValDet.setFDISCVALDET_DIS_AMT(cursor.getString(cursor.getColumnIndex(FDISCVALDET_DIS_AMT)));
                discValDet.setFDISCVALDET_SEQNO(cursor.getString(cursor.getColumnIndex(FDISCVALDET_SEQNO)));
                discValDet.setFDISCVALDET_VDATEF(cursor.getString(cursor.getColumnIndex(FDISCVALDET_VDATEF)));
                discValDet.setFDISCVALDET_VDATET(cursor.getString(cursor.getColumnIndex(FDISCVALDET_VDATET)));


            }
        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discValDet;
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALDET, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FDISCVALDET, null, null);
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
