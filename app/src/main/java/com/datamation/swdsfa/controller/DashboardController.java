package com.datamation.swdsfa.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DayTargetD;
import com.datamation.swdsfa.model.FinvDetL3;
import com.datamation.swdsfa.model.Group;
import com.datamation.swdsfa.model.Item;
import com.datamation.swdsfa.model.Target;
import com.datamation.swdsfa.model.TargetCat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DashboardController {
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;
    private String TAG = "DashboardController";

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";


    public DashboardController(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    //current month target
    public Double getRepTarget() {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));


        double targetsum = 3000000.00;
        String selectQuery = "SELECT ifnull((sum(Rdtarget)),0)  as Target from FItenrDet where txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";

        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                targetsum = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Target")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getRepTarget", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return targetsum;

    }
    //rashmi-2019-11-29
    public Date subtractDay(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
    //rashmi-2019-11-29
    public String getFirstDay() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        String firstday = ""+curYear+"-"+String.format("%02d", curMonth)+"-"+"01";


        return firstday;


    }
    public ArrayList<Target> getTargetVsActuals(String from, String to) {//developed by rashmi

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<Target> list = new ArrayList<Target>();

        // String selectQuery = "select * from " + dbHelper.TABLE_ORDER_DETAIL + " WHERE "
        String selectQuery = "SELECT itd.txndate, itd.Rdtarget, ifnull((sum(a.Amt)),0)  as totAmt " +
                "FROM FItenrDet itd " +
                "LEFT JOIN FOrddet a " +
                "ON itd.txndate = a.txndate " +
                "where itd.txndate between '" + from + "' and " +
                "'" + to + "' group by itd.txndate";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                Target target = new Target();
                target.setDate(cursor.getString(cursor.getColumnIndex("TxnDate")));
                target.setTargetAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("RDTarget"))));
                target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt"))));

                list.add(target);

            }
        } catch (Exception e) {

            Log.v(TAG + " Except getTargtVsActul", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }
    //current month discount
    public Double getTMDiscounts() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        double discount = 0.0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();
        Cursor cursor = null;
        try {


            String selectQuery = "select ifnull((sum(a.DisAmt)),0)  as totAmt from FOrdDisc a where a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getTMDiscounts", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;


    }

    //current month gross
    public Double getMonthAchievement() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthAchieve = 0.0;
        Cursor cursor = null;
        try {
            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from Forddet a where a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                monthAchieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getMonthAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return monthAchieve;

    }

    //current month return
    public Double getTMReturn() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        Cursor cursor = null;
        double discount = 0.0;
        try {


            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from FOrddet a where (a.Types = 'MR' or a.Types = 'UR') and a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";


            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getTMDiscounts", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;

    }

    //today Usable Return
    public double getTodayUsableReturn() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        double result = 0;

        try {
            String selectQuery = "select ifnull((sum(Amt)),0)  as totURAmt from FOrddet where Types = 'UR' and txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" +
                    String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getDouble(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getTotalUReturn", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;


    }

    //today Market Return
    public double getTodayMarketReturn() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        double result = 0;

        try {
            String selectQuery = "select ifnull((sum(Amt)),0)  as totMRAmt from FOrddet where Types = 'MR' and txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" +
                    String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getDouble(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getTotalMReturn", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    //today Free company
    public int getFreeCompany() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        int result = 0;

        try {

            String selectQuery = "select ifnull((sum(Qty)),0)  as totfreeqty from FOrddet where Types ='FI'  and txndate= '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0) {
                    return result;
                }

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getFreeDist", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    //today Free Distributor
    public int getFreeDistributor() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        int result = 0;

        try {

            String selectQuery = "select ifnull((sum(Qty)),0)  as totfreeqty from FOrddet where Types ='FD'  and txndate= '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0) {
                    return result;
                }

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getFreeDist", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    //current month productive
    public int getTMProductiveCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result = 0;
        try {
            String selectQuery = "select count(DISTINCT DebCode) from FOrdHed where txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                if (cursor.getInt(0) > 0)
                    result = cursor.getInt(0);
            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getTMProductCunt", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    //current month non productive
    public int getTMNonPrdCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result = 0;
        try {
            String selectQuery = "select count(refno) from FDaynPrdHed where txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                if (cursor.getInt(0) > 0)
                    result = cursor.getInt(0);

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getTMNonPrdCount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;


    }


    //get numer of order
    public int getNumberOfOrder() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result1 = 0;
        try {
            String selectQuery = "select count(*) as amu from FOrdHed where AddDate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "%' ";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result1 = Integer.parseInt(cursor.getString(cursor.getColumnIndex("amu")));
            }
        } catch (Exception e) {

            Log.v(TAG + " Excep ordhed Count", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result1;
    }

    //get numer of order
    public int getNumberOfOrderDet() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        String date = (curYear + '-' + curMonth + '-' + curDate) + "";
        Log.d("88", "getNumberOfOrder: " + date);

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result = 0;
        try {
            String selectQuery = "select count(*) as amu from FOrddet where txndate Like '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "%' ";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = Integer.parseInt(cursor.getString(cursor.getColumnIndex("amu")));
            }
        } catch (Exception e) {

            Log.v(TAG + " Excep get orddetCount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;
    }


    //previous month target
    public Double getPMRepTarget() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        double targetsum = 2000000.00;
        try {
            String selectQuery = "SELECT Target from fTarget where Month = '" + String.format("%02d", curMonth - 1) + "' and Year = '" + curYear + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                targetsum = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Target")));
                return targetsum;
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getTMDiscounts", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return targetsum;
    }

    //previous month discount
    public Double getPMDiscounts() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();
        double discount = 0.0;


        Cursor cursor = null;
        try {


            String selectQuery = "select ifnull((sum(a.DisAmt)),0)  as totAmt from FOrdDisc a where a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth - 1) + "-_%'";


            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getPMDiscounts", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;


    }

    //previous month gross
    public Double getPMonthAchievement() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthAchieve = 0.0;

        Cursor cursor = null;
        try {

            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from Forddet a where a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth - 1) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                monthAchieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getPMonthAchiev", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return monthAchieve;

    }

    //previous month return
    public Double getPMReturn() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double discount = 0.0;
        Cursor cursor = null;
        try {

            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from FOrddet a where (a.Types = 'MR' or a.Types = 'UR') and a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth - 1) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getPMReturn", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return discount;

    }

    //previous month productive
    public int getPMProductiveCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result = 0;
        try {
            String selectQuery = "select count(DISTINCT DebCode) from FOrdHed where txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth - 1) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {


                if (cursor.getInt(0) > 0)
                    result = cursor.getInt(0);

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getPMProdnt", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    //previous month non productive
    public int getPMNonPrdCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        int result = 0;

        try {
            String selectQuery = "select count(refno) from FDaynPrdHed where txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth - 1) + "-_%'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getPMNCunt", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;


    }


    //today productive
    public int getProductiveCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        int result = 0;

        try {
            String selectQuery = "select count(DISTINCT DebCode) from FOrdHed where txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getProdCount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;


    }

    //today nonproductive
    public int getNonPrdCount() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        int result = 0;

        try {
            String selectQuery = "select count(refno) from FDaynPrdHed where txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getNPCount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return result;

    }

    public String getRoute() {
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        String curdate = curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate);

        String result = "";

        result = new FItenrDetController(context).getRouteFromItenary(curdate);

        return result;
    }

    public int getOutletCount(String route) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Cursor cursor = null;
        int result = 0;


        try {
            String selectQuery = "select count(DISTINCT DebCode) from fRouteDet where RouteCode = '" + route.trim() + "'";

            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {

                result = cursor.getInt(0);

                if (result > 0)
                    return result;

            }
        } catch (Exception e) {

            Log.v(TAG + " Excep getOutCount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return result;
    }


    public Double getTodayDiscount() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        double discount = 0.0;

        Cursor cursor = null;

        try {
            String selectQuery = "select ifnull((sum(a.DisAmt)),0)  as totAmt from FOrdDisc a where a.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }
            cursor.close();

        } catch (Exception e) {

            Log.v(TAG + " Excep getTDiscount", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;
    }

    public Double getTodayReturn() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        double todayreturn = 0.0;

        Cursor cursor = null;

        try {
            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from FOrddet a where (a.Types = 'MR' or a.Types = 'UR') and a.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" +
                    String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                todayreturn = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


            cursor.close();

        } catch (Exception e) {

            Log.v(TAG + " Excep getTReturn", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return todayreturn;

    }

    public Double getUReturn() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        double discount = 0.0;

        Cursor cursor = null;

        try {
            String selectQuery = "select ifnull((sum(a.Amt)),0)  as totAmt from FInvRDet a where a.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" +
                    String.format("%02d", curDate) + "'";

            cursor = dB.rawQuery(selectQuery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


            cursor.close();

        } catch (Exception e) {

            Log.v(TAG + " Excep getTReturn", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;

    }


    public Double getTodayCashCollection() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));


        double discount = 0.0;

        Cursor cursor = null;

        try {
            String selectQuery = "select ifnull((sum(det.amt)),0)  as totAmt from fprecdets  det, fprecheds hed where det.refno = hed.refno and " +

                    "det.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'" +
                    " and  det.dtxndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "' and hed.paytype = 'CA'";

            cursor = dB.rawQuery(selectQuery, null);
//                    "det.txndate = '2019-04-12'" +
//                    " and  det.dtxndate = '2019-02-13'", null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }

            cursor.close();

        } catch (Exception e) {

            Log.v(TAG + " Excep getTCashCol", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;

    }

    public Double getTodayChequeCollection() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        double discount = 0.0;


        Cursor cursor = null;

        try {
            String selectQuery = "select ifnull((sum(det.amt)),0)  as totAmt from fprecdets  det, fprecheds hed where det.refno = hed.refno and " +

                    "det.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'" +
                    " and  det.dtxndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "' and hed.paytype = 'CH'";

            cursor = dB.rawQuery(selectQuery, null);
//                    "det.txndate = '2019-04-12'" +
//                    " and  det.dtxndate = '2019-02-13'", null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + " Excep getTCashCol", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return discount;
    }//***

    public Double getTodayCashPreviousCollection() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        double discount = 0.0;


        Cursor cursor = null;
        try {


            String selectquery = "select ifnull((sum(det.aloamt)),0)  as totAmt from fprecdets  det, fprecheds hed where det.refno = hed.refno and " +
                    "det.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'" +
                    " and  det.dtxndate <> '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "' and hed.paytype = 'CA'";

            cursor = dB.rawQuery(selectquery, null);
//                    "det.txndate = '2019-04-12'" +
//                    " and  det.dtxndate <> '2019-02-13'", null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }


        } catch (Exception e) {

            Log.v(TAG + "getTodayCashPreviCollec", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;

    }

    public Double getTodayChequePreviousCollection() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double discount = 0.0;
        Cursor cursor = null;
        try {

            String selectquery = "select ifnull((sum(det.aloamt)),0)  as totAmt from fprecdets  det , fprecheds hed where det.refno = hed.refno and " +
                    "det.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'" +
                    " and  det.dtxndate <> '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "' and hed.paytype = 'CH'";


            cursor = dB.rawQuery(selectquery, null);
//                    "det.txndate = '2019-04-12'" +
//                    " and  det.dtxndate <> '2019-02-13'", null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));

            }

        } catch (Exception e) {

            Log.v(TAG + "getTodayCheqPrevsCollec", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }


        return discount;

    }

    public Double getDailyAchievement() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthAchieve = 0.0;
        Cursor cursor = null;
        try {

            String selectquery = "select ifnull((sum(a.Amt)),0)  as totAmt from Forddet a where a.Types = 'SA' and a.txndate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) + "'";
            cursor = dB.rawQuery(selectquery, null);
            // Old 18-12-2017 cursor1 = dB.rawQuery("select ifnull((sum(a.qty)),0)  as totqty from ftransodet a, fitem b,ftransohed c where a.itemcode=b.itemcode and b.brandcode='" + arr[0] + "' and c.costcode='" + costCode + "' and c.refno=a.refno AND c.txndate LIKE '" + iYear + "-" + String.format("%02d", iMonth) + "-_%'", null);

            while (cursor.moveToNext()) {
                monthAchieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt")));
            }

        } catch (Exception e) {

            Log.v(TAG + "getDailyAchievement", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return monthAchieve;
    }

    public Double getTargetBySubCode(String from, String to, String sbrandCode) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        double targetPrecent = 0.00;


        String selectQuery = " SELECT  (a.Volume*SUM(b.TargetPercen))/100  as SBTar \n" +
                " FROM fItemTarDet a, FdayTargetD b WHERE  a.SBrandCode=B.SBrandCode AND b.SBrandCode ='" + sbrandCode + "' AND b.day BETWEEN '" + from + "' and '" + to + "'";

//        String selectQuery = "SELECT ifnull((sum(b.TargetPercen)),0)  as TotPercen  from fItemTarDet as a, fDayTargetD as b \n" +
//                " where a.sBrandCode = b.sBrandCode AND b.sBrandCode = '" + sbrandCode + "' AND b.Day BETWEEN  '" + from + "' and '" + to + "'";


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                targetPrecent = Double.parseDouble(cursor.getString(cursor.getColumnIndex("SBTar")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getTarget", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return targetPrecent;

    }

    public Double getTargetForAll(String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        double targetPrecent = 0.00;


        String selectQuery = "Select Sum(x.SBTar) as ToTalTarget FROM\n" +
                " (SELECT  (a.Volume*SUM(b.TargetPercen))/100  as SBTar \n" +
                " FROM fItemTarDet a, FdayTargetD b WHERE  a.SBrandCode=B.SBrandCode AND b.day BETWEEN '" + from + "' and '" + to + "' " +
                " GROUP BY a.SBrandCode) x";

        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                targetPrecent = Double.parseDouble(cursor.getString(cursor.getColumnIndex("ToTalTarget")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getTarget", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return targetPrecent;

    }


    // Get Achievement
    public Double getOrderAchievement(String category,String subBrand,String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        double Achieve = 0.00;

        if(category.equals("Case")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }else if (category.equals("Piece")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                Achieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return Achieve;

    }

    public Double getAllOrderAchievement(String category,String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        double Achieve = 0.00;

        if(category.equals("Case")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(Round(a.Qty,2))/b.NOUCase as CS " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode,b.NOUCase) x ";

        }else if (category.equals("Piece")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)%b.NOUCase as CS " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode,b.NOUCase) x ";

//            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
//                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
//                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Amt) as CS, " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode) x ";

//            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
//                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                Achieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return Achieve;

    }

    public Double getInvoiceAchievement(String category,String subBrand,String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        double Achieve = 0.00;

        if(category.equals("Case")){

            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.SBrandCode = '" + subBrand + "' " +
                    " AND a.txndate BETWEEN '" + from + "' AND '" + to + "' ";

        }else if (category.equals("Piece")){

//            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

//            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                Achieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("TotAchieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return Achieve;

    }

    public Double getAllInvoiceAchievement(String category,String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        double Achieve = 0.00;

        if(category.equals("Case")){

            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.txndate" +
                    " BETWEEN '" + from + "' AND '" + to + "'";

        }else if (category.equals("Piece")){

//            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

//            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {

            while (cursor.moveToNext()) {
                Achieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("TotAchieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return Achieve;

    }

    public boolean isAnyInvoice(String category,String subBrand,String from, String to)
    {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        String selectQuery = "";

        if(category.equals("Case")){

            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.SBrandCode = '" + subBrand + "' " +
                    " AND a.txndate BETWEEN '" + from + "' AND '" + to + "' ";

        }else if (category.equals("Piece")){

//            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.SBrandCode = '" + subBrand + "' " +
//                    " AND a.txndate BETWEEN '" + from + "' AND '" + to + "' ";

        }else if(category.equals("Value")){

//            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.SBrandCode = '" + subBrand + "' " +
//                    " AND a.txndate BETWEEN '" + from + "' AND '" + to + "' ";

//            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount()>0)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return false;
    }

    public boolean isAnyInvoiceForAll(String category,String from, String to)
    {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        String selectQuery = "";

        if(category.equals("Case")){

            selectQuery   = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a WHERE a.txndate" +
                    " BETWEEN '" + from + "' AND '" + to + "'";

//            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }else if (category.equals("Piece")){

//            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

//            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
//                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                    "FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
//                    " b.SBrandCode = '" + subBrand + "' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                    "GROUP BY x.SBrandCode";

        }

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount()>0)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return false;
    }


    public boolean isAnyOrder(String category,String subBrand,String from, String to)
    {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        String selectQuery = "";

        if(category.equals("Case")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }else if (category.equals("Piece")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }else if(category.equals("Value")){

            selectQuery   = " SELECT Sum(x.CS ) as Achieve \n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND" +
                    " b.SBrandCode = '" + subBrand + "' AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    "GROUP BY x.SBrandCode";

        }

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount()>0)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return false;
    }

    public boolean isAnyOrderForAll(String category,String from, String to)
    {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        String selectQuery = "";

        if(category.equals("Case")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode,b.NOUCase) x ";

        }else if (category.equals("Piece")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)%b.NOUCase as CS " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode,b.NOUCase) x ";

        }else if(category.equals("Value")){

            selectQuery   = "SELECT Sum(x.CS ) as Achieve " +
                    "FROM (SELECT a.itemcode,Sum(a.Amt) as CS, " +
                    "FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND " +
                    " a.Types='SA' GROUP BY a.itemcode) x ";
        }


        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            if (cursor.getCount()>0)
            {
                return true;
            }
            else
            {
                return false;
            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return false;
    }

    // ------------------------- Get Target Categories  --------- 28-03-2022  ------------------------------------------
    public ArrayList<String> getItemCategories() {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String> list = new ArrayList<>();

        String selectQuery = " SELECT TarCatCode FROM fTargetCat";


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {

                list.add(cursor.getString(cursor.getColumnIndex(TargetCatController.FTARGETD_CAT_TARCATCODE)));

            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }
    public ArrayList<TargetCat> getTargetCategories() {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<TargetCat> list = new ArrayList<TargetCat>();

        String selectQuery = " SELECT * FROM fTargetCat";


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                TargetCat cat = new TargetCat();
                cat.setTarCatCode(cursor.getString(cursor.getColumnIndex(TargetCatController.FTARGETD_CAT_TARCATCODE)));
                cat.setTarcatName(cursor.getString(cursor.getColumnIndex(TargetCatController.FTARGETD_CAT_TARCATNAME)));
                cat.setId(Float.parseFloat(cursor.getString(cursor.getColumnIndex(TargetCatController.FTARGETD_CAT_ID))));
                list.add(cat);

            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }
    public Double getMonthlyOrderAchievement(String catcode,String type) {
        //  public Double getCaseAchievement() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthAchieve = 0.0;
        Cursor cursor = null;
        try {


            if(type.equals("Case")){

                selectQuery = "SELECT Sum(x.CS ) as Achieve\n" +
                        " FROM (SELECT Sum(a.Qty) as Qty, itm.NOUCase, Sum(a.Qty)/itm.NOUCase as CS from FOrddet as a, fTargetCat as b,\n" +
                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND " +
                        " itm.itemcode in (Select itemcode from FOrddet) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND  a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' "  +
                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase) x";

//                selectQuery = " SELECT ifnull((sum(a.CaseQty)),0)  as Achieve from FOrddet as a, fTargetCat as b,\n" +
//                        "  fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND\n" +
//                        "  itm.itemcode in (Select itemcode from FOrddet) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND  a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' "  +
//                        "  GROUP by itm.TarCatCode";


            }else if(type.equals("Piece")){

                selectQuery = "SELECT Sum(x.CS ) as Achieve\n" +
                        " FROM (SELECT Sum(a.Qty) as Qty, itm.NOUCase, Sum(a.Qty) % itm.NOUCase as CS from FOrddet as a, fTargetCat as b,\n" +
                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND " +
                        " itm.itemcode in (Select itemcode from FOrddet) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND  a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' "  +
                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase) x";

//                selectQuery = "SELECT ifnull((sum(a.PiceQty)),0)  as Achieve from FOrddet as a, fTargetCat as b,\n" +
//                        "fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND \n" +
//                        "itm.itemcode in (Select itemcode from FOrddet) AND itm.itemcode = a.itemcode AND a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' " +
//                        "GROUP by itm.TarCatCode";

            } else if(type.equals("Value")){

                selectQuery = "SELECT ifnull((sum(a.Amt)),0)  as Achieve from FOrddet as a, fTargetCat as b,\n" +
                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND \n" +
                        " itm.itemcode in (Select itemcode from FOrddet) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' " +
                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase ";

//                selectQuery = "SELECT ifnull((sum(a.Amt)),0)  as Achieve from FOrddet as a, fTargetCat as b," +
//                        " fItem itm where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND " +
//                        " itm.itemcode in (Select itemcode from FOrddet)  AND a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' " +
//                        "GROUP by itm.TarCatCode ";

            }


            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                monthAchieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getMonthAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return monthAchieve;

    }

    public Double getMonthlyInvoiceAchievement(String catcode,String type) {
        //  public Double getCaseAchievement() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthAchieve = 0.0;
        Cursor cursor = null;
        try {


            if(type.equals("Case")){

                selectQuery = "Select Sum(a.Achievement) as TotAchieve FROM fSBrandInvAch as a\n" +
                        "WHERE a.SBrandCode IN (Select SBrandCode From Fitem Where TarCatCode = '" + catcode + "' GROUP BY SBrandCode)\n" +
                        "AND a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' ";

                //selectQuery = "SELECT Sum(x.CS ) as Achieve\n" +
//                        " FROM (SELECT Sum(a.Qty) as Qty, itm.NOUCase, Sum(a.Qty) / itm.NOUCase as CS from FinvDetL3 as a, fTargetCat as b,\n" +
//                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND " +
//                        " itm.itemcode in (Select itemcode from FinvDetL3) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND  a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' "  +
//                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase) x";


            }else if(type.equals("Piece")){

//                selectQuery = "SELECT Sum(x.CS ) as Achieve\n" +
//                        " FROM (SELECT Sum(a.Qty) as Qty, itm.NOUCase, Sum(a.Qty) % itm.NOUCase as CS from FinvDetL3 as a, fTargetCat as b,\n" +
//                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND " +
//                        " itm.itemcode in (Select itemcode from FinvDetL3) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND  a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' "  +
//                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase) x";

            } else if(type.equals("Value")){

//                selectQuery = "SELECT ifnull((sum(a.Amt)),0)  as Achieve from FinvDetL3 as a, fTargetCat as b,\n" +
//                        " fItem itm, fSubBrand as s where  itm.TarCatCode =  b.TarCatCode AND b.TarCatCode = '" + catcode + "' AND s.SBrandCode = itm.SBrandCode AND \n" +
//                        " itm.itemcode in (Select itemcode from FinvDetL3) AND itm.itemcode = a.itemcode AND a.Types = 'SA' AND a.txndate LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' " +
//                        " GROUP by itm.TarCatCode,itm.SBrandCode,itm.itemcode,itm.NOUCase ";

            }


            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                monthAchieve = Double.parseDouble(cursor.getString(cursor.getColumnIndex("TotAchieve")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getMonthAchieve", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return monthAchieve;

    }

    // ------------------------------------ Get Monthly Target -------------------------------------------------------
    public Double getMonthlyTarget(String catcode,String type) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<String[]> list = new ArrayList<String[]>();

        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
        double monthTarget = 0.0;
        Cursor cursor = null;
        try {

            String selectQuery = "select sum(Volume) as Volume from fItemTarDet where SBrandCode in \n" +
                    "(select SBrandCode from fitem where TarCatCode='" + catcode + "') and SBrandCode in \n" +
                    "(Select SBrandCode From fDayTargetD Where Day LIKE '" + curYear + "-" + String.format("%02d", curMonth) + "-_%')";


            cursor = dB.rawQuery(selectQuery, null);

            while (cursor.moveToNext()) {
                monthTarget = Double.parseDouble(cursor.getString(cursor.getColumnIndex("Volume")));
            }

        } catch (Exception e) {

            Log.v(TAG + " Excep getMonthTarget", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return monthTarget;

    }

    public ArrayList<Item> getTonnage(String type,String fromDate,String toDate,String itemcode) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<Item> list = new ArrayList<Item>();
        String selectQuery = "";

        if(type.equals("Order")){

            selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0)  as Qty ,a.Types ,i.UnitCode as ItemWeight " + " from fItem as i, Forddet as a  " + " " +
                    "where i.ItemCode in (select ItemCode from Forddet where a.Txndate BETWEEN  '" + fromDate + "' and '" + toDate + "') " +
                    "and i.ItemCode = a.ItemCode and a.ItemCode = '" + itemcode + "' and a.Types ='SA' " + " group by a.ItemCode";

        }else if(type.equals("Invoice")){

//            selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0)  as Qty,i.UnitCode as ItemWeight from fItem as i, FinvDetL3 as a " +
//                    " where i.ItemCode in (select ItemCode from FinvDetL3 where a.Txndate BETWEEN  '" + fromDate + "' and '" + toDate + "') " +
//                    " and i.ItemCode = a.ItemCode and a.ItemCode = '" + itemcode + "' group by a.ItemCode";

        }

        Cursor cursor = dB.rawQuery(selectQuery, null);

        Log.d("##", "getTonnage cursor: "+cursor);

        try {
            while(cursor.moveToNext()) {
                Item items = new Item();
                items.setFITEM_ITEM_CODE(cursor.getString(cursor.getColumnIndex("ItemCode")));
                items.setFITEM_UNITCODE(cursor.getString(cursor.getColumnIndex("ItemWeight")));
                items.setFITEM_QOH(cursor.getString(cursor.getColumnIndex("Qty")));
                list.add(items);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }


        return list;
    }

    public ArrayList<Item> getMonthlyTonnage(String catcode,String tranType) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        ArrayList<Item> list = new ArrayList<Item>();
        String selectQuery = "";

        if(tranType.equals("Order")){

            selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0) as Qty,a.Types ,i.UnitCode as ItemWeight from fItem as i, Forddet as a ,\n" +
                    "  fTargetCat as h , fSubBrand as s \n" +
                    "  where i.ItemCode in (select ItemCode from Forddet where a.Txndate LIKE  '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' ) \n" +
                    "  and i.ItemCode = a.ItemCode  and a.Types ='SA' and i.SBrandCode = s.SBrandCode \n" +
                    "  and h.TarCatCode = i.TarCatCode and h.TarCatCode = '" + catcode + "'" +
                    "  group by a.ItemCode";

        }else if(tranType.equals("Invoice")){

            selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0) as Qty,a.Types ,i.UnitCode as ItemWeight from fItem as i, Forddet as a ,\n" +
                    "  fTargetCat as h , fSubBrand as s \n" +
                    "  where i.ItemCode in (select ItemCode from Forddet where a.Txndate LIKE  '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' ) \n" +
                    "  and i.ItemCode = a.ItemCode  and a.Types ='SA' and i.SBrandCode = s.SBrandCode \n" +
                    "  and h.TarCatCode = i.TarCatCode and h.TarCatCode = '" + catcode + "'" +
                    "  group by a.ItemCode";
        }

        Cursor cursor = dB.rawQuery(selectQuery, null);

        Log.d("##", "getMonthlyTonnage cursor: "+cursor);

        try {
            while(cursor.moveToNext()) {
                Item items = new Item();
                items.setFITEM_ITEM_CODE(cursor.getString(cursor.getColumnIndex("ItemCode")));
                items.setFITEM_UNITCODE(cursor.getString(cursor.getColumnIndex("ItemWeight")));
                items.setFITEM_QOH(cursor.getString(cursor.getColumnIndex("Qty")));
                list.add(items);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }


        return list;
    }

    public ArrayList<Item> getMonthlyTonnageForInvoice(String catcode) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));

        ArrayList<Item> list = new ArrayList<Item>();
        String selectQuery = "";

        selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0) as Qty,a.Types ,i.UnitCode as ItemWeight from fItem as i, Forddet as a ,\n" +
                "  fTargetCat as h , fSubBrand as s \n" +
                "  where i.ItemCode in (select ItemCode from Forddet where a.Txndate LIKE  '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' ) \n" +
                "  and i.ItemCode = a.ItemCode  and a.Types ='SA' and i.SBrandCode = s.SBrandCode \n" +
                "  and h.TarCatCode = i.TarCatCode and h.TarCatCode = '" + catcode + "'" +
                "  group by a.ItemCode";

//        selectQuery="select i.ItemCode,ifnull((sum(a.Qty)),0)  as Qty,a.Types ,i.UnitCode as ItemWeight from fItem as i, Forddet as a , fTargetCat h " +
//                "where i.ItemCode in (select ItemCode from Forddet where a.Txndate LIKE  '" + curYear + "-" + String.format("%02d", curMonth) + "-_%' ) " +
//                "and i.ItemCode = a.ItemCode  and a.Types ='SA' " +
//                "and h.TarCatCode = i.TarCatCode AND h.TarCatCode = '" + catcode + "'" +
//                " group by a.ItemCode ";



        Cursor cursor = dB.rawQuery(selectQuery, null);

        Log.d("##", "getMonthlyTonnage cursor: "+cursor);

        try {
            while(cursor.moveToNext()) {
                Item items = new Item();
                items.setFITEM_ITEM_CODE(cursor.getString(cursor.getColumnIndex("ItemCode")));
                items.setFITEM_UNITCODE(cursor.getString(cursor.getColumnIndex("ItemWeight")));
                items.setFITEM_QOH(cursor.getString(cursor.getColumnIndex("Qty")));
                list.add(items);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }


        return list;
    }

    public ArrayList<Item> getItemsBySbrand(String sBrand) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<Item> list = new ArrayList<>();

        String selectQuery = " SELECT ItemCode,SBrandCode FROM fItem Where SBrandCode = '" + sBrand + "'";


        Cursor cursor = dB.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {

                Item item = new Item();

                item.setFITEM_ITEM_CODE(cursor.getString(cursor.getColumnIndex(ItemController.FITEM_ITEM_CODE)));
                item.setFITEM_SBRAND_CODE(cursor.getString(cursor.getColumnIndex(ItemController.FITEM_SBRAND_CODE)));

                list.add(item);

            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }



}
