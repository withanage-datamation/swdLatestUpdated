package com.datamation.swdsfa.controller;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;


import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.SubBrand;
import com.datamation.swdsfa.utils.ValueHolder;

import java.util.ArrayList;

/*
    create by kaveesha - 16/12/2021
 */

public class SubBrandController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "SubBrandController";
    public static final String TABLE_SUB_BRAND = "fSubBrand";
    //SubBrand
    public static final String SUB_BRANDCODE = "SBrandCode";
    public static final String SUB_BRANDNAME = "SBrandName";
    public static final String ID = "Id";
    public static final String CREATE_FSUB_BRAND_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_SUB_BRAND + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + SUB_BRANDCODE + " TEXT, "
            +  SUB_BRANDNAME + " TEXT); ";
    public SubBrandController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException{
        dB = dbHelper.getWritableDatabase();
    }

    public void CreateOrUpdateSubBrand(ArrayList<SubBrand> list) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            dB.beginTransactionNonExclusive();
            String sql = "INSERT OR REPLACE INTO " + TABLE_SUB_BRAND +
                    " (SBrandCode,SBrandName) " + " VALUES (?,?)";

            SQLiteStatement stmt = dB.compileStatement(sql);

            for (SubBrand subBrand : list) {
                stmt.bindString(1, subBrand.getFSUBRAND_SUBCODE());
                stmt.bindString(2, subBrand.getFSUBRAND_NAME());

                stmt.execute();
                stmt.clearBindings();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dB.setTransactionSuccessful();
            dB.endTransaction();
            dB.close();
        }

    }

    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_SUB_BRAND, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_SUB_BRAND, null, null);
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
