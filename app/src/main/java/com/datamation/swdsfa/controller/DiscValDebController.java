package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DiscValDeb;
import com.datamation.swdsfa.model.DiscValDet;

import java.util.ArrayList;

public class DiscValDebController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "DiscValDebController ";

    // table
    public static final String TABLE_FDISCVALDEB = "fDisValDeb";
    // table attributes
    public static final String FDISCVALDEB_ID = "Id";
    public static final String FDISCVALDEB_DEBCODE = "DebCode";
    public static final String FDISCVALDEB_REFNO = "RefNo";


    // create String
    public static final String CREATE_FDISCVALDEB_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FDISCVALDEB + " (" + FDISCVALDEB_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + FDISCVALDEB_REFNO + " TEXT, " + FDISCVALDEB_DEBCODE + " TEXT); ";


    public DiscValDebController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateDiscValDeb(ArrayList<DiscValDeb> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (DiscValDeb discValDeb : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALDEB + " WHERE " + FDISCVALDEB_REFNO + "='" + discValDeb.getFDISCVALDEB_REFNO() + "' AND " + FDISCVALDEB_DEBCODE + "='" + discValDeb.getFDISCVALDEB_DEBCODE() + "'", null);


                ContentValues values = new ContentValues();

                values.put(FDISCVALDEB_REFNO, discValDeb.getFDISCVALDEB_REFNO());
                values.put(FDISCVALDEB_DEBCODE, discValDeb.getFDISCVALDEB_DEBCODE());


                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FDISCVALDEB, values,  FDISCVALDEB_REFNO + " =? AND " + FDISCVALDEB_DEBCODE + "=?", new String[]{discValDeb.getFDISCVALDEB_REFNO().toString(), discValDeb.getFDISCVALDEB_DEBCODE().toString()});
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FDISCVALDEB, null, values);
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALDEB, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FDISCVALDEB, null, null);
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
