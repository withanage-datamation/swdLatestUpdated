package com.datamation.swdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.Bank;
import com.datamation.swdsfa.model.DiscValHed;
import com.datamation.swdsfa.model.Disched;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DiscValHedController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "DiscValHedController ";

    // table
    public static final String TABLE_FDISCVALHED = "fDisValHed";
    // table attributes
    public static final String FDISCVALHED_ID = "DiscValHed_id";
    public static final String FDISCVALHED_DISC_DESC= "DiscDesc";
    public static final String FDISCVALHED_DISC_TYPE = "DiscType";
    public static final String FDISCVALHED_PAY_TYPE = "PayType";
    public static final String FDISCVALHED_PRIORITY = "Priority";
    public static final String FDISCVALHED_REFNO = "RefNo";
    public static final String FDISCVALHED_REMARKS = "Remarks";
    public static final String FDISCVALHED_TXNDATE = "TxnDate";
    public static final String FDISCVALHED_VDATEF = "Vdatef";
    public static final String FDISCVALHED_VDATET = "Vdatet";


    // create String
    public static final String CREATE_FDISCVALHED_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FDISCVALHED + " (" + FDISCVALHED_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + FDISCVALHED_DISC_DESC + " TEXT, " + FDISCVALHED_DISC_TYPE + " TEXT, " + FDISCVALHED_PAY_TYPE + " TEXT, " +
            FDISCVALHED_PRIORITY + " TEXT, " + FDISCVALHED_REFNO + " TEXT, " + FDISCVALHED_REMARKS + " TEXT, " + FDISCVALHED_TXNDATE + " TEXT, " +
            FDISCVALHED_VDATEF + " TEXT, " + FDISCVALHED_VDATET + " TEXT); ";

  //  public static final String TESTBANK = "CREATE UNIQUE INDEX IF NOT EXISTS idxbank_something ON " + TABLE_FBANK + " (" + FBANK_BANK_CODE + ")";

    public DiscValHedController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public int createOrUpdateDiscValHed(ArrayList<DiscValHed> list) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            for (DiscValHed discValHed : list) {

                Cursor cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALHED + " WHERE " + FDISCVALHED_REFNO + "='" + discValHed.getFDISCVALHED_REFNO() + "'", null);

                ContentValues values = new ContentValues();

                values.put(FDISCVALHED_DISC_DESC, discValHed.getFDISCVALHED_DISC_DESC());
                values.put(FDISCVALHED_DISC_TYPE, discValHed.getFDISCVALHED_DISC_TYPE());
                values.put(FDISCVALHED_PAY_TYPE, discValHed.getFDISCVALHED_PAY_TYPE());
                values.put(FDISCVALHED_PRIORITY, discValHed.getFDISCVALHED_PRIORITY());
                values.put(FDISCVALHED_REFNO, discValHed.getFDISCVALHED_REFNO());
                values.put(FDISCVALHED_REMARKS, discValHed.getFDISCVALHED_REMARKS());
                values.put(FDISCVALHED_TXNDATE, discValHed.getFDISCVALHED_TXNDATE());
                values.put(FDISCVALHED_VDATEF, discValHed.getFDISCVALHED_VDATEF());
                values.put(FDISCVALHED_VDATET, discValHed.getFDISCVALHED_VDATET());

                if (cursor.getCount() > 0) {
                    dB.update(TABLE_FDISCVALHED, values, FDISCVALHED_REFNO + "=?", new String[]{discValHed.getFDISCVALHED_REFNO().toString()});
                    Log.v(TAG, "Updated");
                } else {
                    count = (int) dB.insert(TABLE_FDISCVALHED, null, values);
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
    public ArrayList<Disched> getDiscountSchemes(String DebCode,String payType) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        //String curdate = curYear+"/"+ String.format("%02d", curMonth) + "/" + String.format("%02d", curDate);
        // Old Rashmi 08-07-2020 String curdate = String.format("%02d", curMonth)+"-"+ String.format("%02d", curDate) + "-" + curYear;
        String curdate = curYear + "-" +  String.format("%02d", curMonth)+"-"+ String.format("%02d", curDate) ;

        // commented due to date format issue and M:D:Y format is available in DB
        //String selectQuery = "select * from fdisched where refno in (select refno from fdiscdet where itemcode='" + itemCode + "') and date('now') between vdatef and vdatet";
        String selectQuery = "select * from fDisValHed where RefNo in (select refno from fDisValDeb where DebCode = '"+DebCode+"' ) and '"+curdate+"' between Vdatef And Vdatet";


        Cursor cursor = dB.rawQuery(selectQuery, null);
        ArrayList<Disched> refNoList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Disched discHed = new Disched();
                discHed.setFDISCHED_REF_NO(cursor.getString(cursor.getColumnIndex(FDISCVALHED_REFNO)));
                discHed.setFDISCHED_TXN_DATE(cursor.getString(cursor.getColumnIndex(FDISCVALHED_TXNDATE)));
                discHed.setFDISCHED_DIS_TYPE(cursor.getString(cursor.getColumnIndex(FDISCVALHED_DISC_TYPE)));
                discHed.setFDISCHED_DISC_DESC(cursor.getString(cursor.getColumnIndex(FDISCVALHED_DISC_DESC)));
                discHed.setFDISCHED_V_DATE_F(cursor.getString(cursor.getColumnIndex(FDISCVALHED_VDATEF)));
                discHed.setFDISCHED_V_DATE_T(cursor.getString(cursor.getColumnIndex(FDISCVALHED_VDATET)));
                refNoList.add(discHed);

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return refNoList;
    }
    public ArrayList<Disched> getDiscountSchemesByPriority(String DebCode,String payType) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        //String curdate = curYear+"/"+ String.format("%02d", curMonth) + "/" + String.format("%02d", curDate);
        // Old Rashmi 08-07-2020 String curdate = String.format("%02d", curMonth)+"-"+ String.format("%02d", curDate) + "-" + curYear;
        String curdate = curYear + "-" +  String.format("%02d", curMonth)+"-"+ String.format("%02d", curDate) ;

        // commented due to date format issue and M:D:Y format is available in DB
        //String selectQuery = "select * from fdisched where refno in (select refno from fdiscdet where itemcode='" + itemCode + "') and date('now') between vdatef and vdatet";
        String selectQuery = "select * from fDisValHed where RefNo in (select refno from fDisValDeb where DebCode = '"+DebCode+"' ) and PayType = '"+payType+"' and Priority = '1' and '"+curdate+"' between Vdatef And Vdatet";
        Log.d(">>>DISVAL",">>>"+selectQuery);

        Cursor cursor = dB.rawQuery(selectQuery, null);
        ArrayList<Disched> refNoList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Disched discHed = new Disched();
                discHed.setFDISCHED_REF_NO(cursor.getString(cursor.getColumnIndex(FDISCVALHED_REFNO)));
                discHed.setFDISCHED_TXN_DATE(cursor.getString(cursor.getColumnIndex(FDISCVALHED_TXNDATE)));
                discHed.setFDISCHED_DIS_TYPE(cursor.getString(cursor.getColumnIndex(FDISCVALHED_DISC_TYPE)));
                discHed.setFDISCHED_DISC_DESC(cursor.getString(cursor.getColumnIndex(FDISCVALHED_DISC_DESC)));
                discHed.setFDISCHED_V_DATE_F(cursor.getString(cursor.getColumnIndex(FDISCVALHED_VDATEF)));
                discHed.setFDISCHED_V_DATE_T(cursor.getString(cursor.getColumnIndex(FDISCVALHED_VDATET)));
                refNoList.add(discHed);

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return refNoList;
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

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FDISCVALHED, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_FDISCVALHED, null, null);
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
