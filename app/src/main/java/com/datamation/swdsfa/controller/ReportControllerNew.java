package com.datamation.swdsfa.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.model.DayExpDet;
import com.datamation.swdsfa.model.OrderDetail;
import com.datamation.swdsfa.model.Target;

import java.util.ArrayList;

public class ReportControllerNew {
    /**
     * Developed for write report functions by rashmi
     *
     * **/
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    Context context;
    private String TAG = "ReportController";

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";


    public ReportControllerNew(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }
///////////////////////////////////////////get target vs actual data //////////////////////////////////////////////////
public ArrayList<Target> getTargetVsActualsOrder(String category,String from, String to) {//developed by Kaveesha - 19-04-2022

    if (dB == null) {
        open();
    } else if (!dB.isOpen()) {
        open();
    }

    String selectQuery = "";
    ArrayList<Target> list = new ArrayList<Target>();

    if(category.equals("Case")){

        selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
                " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
                " GROUP BY a.SBrandCode), " +
                " tmpAchieve AS(\n" +
                " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
                " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS \n" +
                " FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                " GROUP BY x.SBrandCode)" +
                " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve as Achievement,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
                " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

    }else if (category.equals("Piece")){

//        selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
//                " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
//                " GROUP BY a.SBrandCode), " +
//                " tmpAchieve AS(\n" +
//                " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
//                " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
//                " FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                " GROUP BY x.SBrandCode)" +
//                " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve as Achievement,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
//                " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

    }else if(category.equals("Value")){

//        selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
//                " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
//                " GROUP BY a.SBrandCode), " +
//                " tmpAchieve AS(\n" +
//                " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
//                " FROM (SELECT b.SBrandCode,a.itemcode,b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
//                " FROM FOrddet a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
//                " GROUP BY x.SBrandCode)" +
//                " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve as Achievement,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
//                " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

    }


    Cursor cursor = dB.rawQuery(selectQuery, null);

    try {
        while (cursor.moveToNext()) {

            Target target = new Target();
            target.setBrandCode(cursor.getString(cursor.getColumnIndex("SBrandCode")));
            target.setBrandName(cursor.getString(cursor.getColumnIndex("SBrandName")));
            target.setTargetAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Target"))));
            target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achievement"))));
            target.setPrecentage(Double.parseDouble(cursor.getString(cursor.getColumnIndex("precentage"))));

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

    public ArrayList<Target> getTargetVsActualsOrderForBoth(String category,String from, String to) {//developed by Kaveesha - 20-04-2022

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        ArrayList<Target> list = new ArrayList<Target>();

        if(category.equals("Case")){

            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achievement " +
                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
                    "FROM FOrddet as a, fitem as b, fSubBrand as s where " +
                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' and b.SBrandCode = s.SBrandCode " +
                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";

        }else if (category.equals("Piece")){

//            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achieve " +
//                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
//                    "FROM FOrddet as a, fitem as b, fSubBrand as s where " +
//                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' and b.SBrandCode = s.SBrandCode " +
//                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
//                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";

        }else if(category.equals("Value")){
/*

            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achieve " +
                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
                    "FROM FOrddet as a, fitem as b, fSubBrand as s where " +
                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND a.Types='SA' and b.SBrandCode = s.SBrandCode " +
                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";
*/

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                Target target = new Target();
                target.setBrandCode(cursor.getString(cursor.getColumnIndex("SBrandCode")));
                target.setBrandName(cursor.getString(cursor.getColumnIndex("SBrandName")));
                target.setTargetAmt(Double.parseDouble("0.0"));
                target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achievement"))));
                target.setPrecentage(Double.parseDouble("0.0"));

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

    public ArrayList<Target> getTargetVsActualsInvoice(String category,String from, String to) {//developed by Kaveesha - 19-04-2022

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        ArrayList<Target> list = new ArrayList<Target>();

        if(category.equals("Case")){

            selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
                    " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
                    " GROUP BY a.SBrandCode), " +
                    " tmpAchieve AS(\n" +
                    " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS \n" +
                    " FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    " GROUP BY x.SBrandCode)" +
                    " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
                    " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

        }else if (category.equals("Piece")){

            selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
                    " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
                    " GROUP BY a.SBrandCode), " +
                    " tmpAchieve AS(\n" +
                    " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,Sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty) % b.NOUCase as CS \n" +
                    " FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    " GROUP BY x.SBrandCode)" +
                    " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
                    " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

        }else if(category.equals("Value")){

            selectQuery   = "With tmpTarget AS (SELECT a.SBrandCode,s.SBrandName, a.Volume, SUM(b.TargetPercen) as Target, (a.Volume*SUM(b.TargetPercen))/100  as SBTar\n" +
                    " FROM fItemTarDet a, FdayTargetD b, fSubBrand as s WHERE  a.SBrandCode=B.SBrandCode AND a.SBrandCode = s.SBrandCode AND b.day BETWEEN '2022-04-01' AND '2022-04-07'\n" +
                    " GROUP BY a.SBrandCode), " +
                    " tmpAchieve AS(\n" +
                    " SELECT x.SBrandCode,Sum(x.CS )as Achieve\n" +
                    " FROM (SELECT b.SBrandCode,a.itemcode,b.SBrandCode,a.itemcode,Sum(a.Amt) as CS \n" +
                    " FROM FinvDetL3 a, fitem b WHERE a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x\n" +
                    " GROUP BY x.SBrandCode)" +
                    " SELECT t.SBrandCode as SBrandCode,t.SBrandName AS SBrandName, t.SBTar as Target, a.achieve as Achievement,(a.achieve/t.SBTar)* 100 as precentage FROM tmpTarget t\n" +
                    " LEFT JOIN tmpAchieve a ON t.SBrandCode = a.SBrandCode Where a.achieve > 0";

        }


        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                Target target = new Target();
                target.setBrandCode(cursor.getString(cursor.getColumnIndex("SBrandCode")));
                target.setBrandName(cursor.getString(cursor.getColumnIndex("SBrandName")));
                target.setTargetAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Target"))));
                target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achievement"))));
                target.setPrecentage(Double.parseDouble(cursor.getString(cursor.getColumnIndex("precentage"))));

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

    public ArrayList<Target> getTargetVsActualsInvoiceForBoth(String category,String from, String to) {//developed by Kaveesha - 20-04-2022

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "";
        ArrayList<Target> list = new ArrayList<Target>();

        if(category.equals("Case")){

            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achieve " +
                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
                    "FROM FinvDetL3 as a, fitem as b, fSubBrand as s where " +
                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND b.SBrandCode = s.SBrandCode " +
                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";

        }else if (category.equals("Piece")){

//            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achieve " +
//                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
//                    "FROM FinvDetL3 as a, fitem as b, fSubBrand as s where " +
//                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode and b.SBrandCode = s.SBrandCode " +
//                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
//                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";

        }else if(category.equals("Value")){

//            selectQuery   = "SELECT x.SBrandCode,x.SBrandName,Sum(x.CS )as Achieve " +
//                    "FROM (Select b.SBrandCode , s.SBrandName , sum(a.Qty) as Qty, b.NOUCase, Sum(a.Qty)/b.NOUCase as CS " +
//                    "FROM FinvDetL3 as a, fitem as b, fSubBrand as s where " +
//                    "a.txndate BETWEEN '" + from + "' AND '" + to + "' AND a.itemcode=b.itemcode AND  b.SBrandCode = s.SBrandCode " +
//                    "AND b.SBrandCode NOT IN (Select SBrandCode from fItemTarDet) " +
//                    "GROUP BY b.SBrandCode,a.itemcode,b.NOUCase) x GROUP BY x.SBrandCode ";
        }


        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                Target target = new Target();
                target.setBrandCode(cursor.getString(cursor.getColumnIndex("SBrandCode")));
                target.setBrandName(cursor.getString(cursor.getColumnIndex("SBrandName")));
                target.setTargetAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("0.0"))));
                target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("Achievement"))));
                target.setPrecentage(Double.parseDouble(cursor.getString(cursor.getColumnIndex("0.0"))));

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

//    public ArrayList<Target> getTargetVsActuals(String from, String to) {//developed by rashmi
//
//        int curYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
//        int curMonth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
//        int curDate = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
//        if (dB == null) {
//            open();
//        } else if (!dB.isOpen()) {
//            open();
//        }
//
//        ArrayList<Target> list = new ArrayList<Target>();
//
//        // String selectQuery = "select * from " + dbHelper.TABLE_ORDER_DETAIL + " WHERE "
//        String selectQuery = "SELECT itd.txndate, itd.Rdtarget, ifnull((sum(a.Amt)),0)  as totAmt " +
//                "FROM FItenrDet itd " +
//                "LEFT JOIN FOrddet a " +
//                "ON itd.txndate = a.txndate " +
//                "where itd.txndate between '" + from + "' and " +
//                "'" + to + "' group by itd.txndate";
//
//        Cursor cursor = dB.rawQuery(selectQuery, null);
//
//        try {
//            while (cursor.moveToNext()) {
//
//                Target target = new Target();
//                target.setDate(cursor.getString(cursor.getColumnIndex("TxnDate")));
//                target.setTargetAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("RDTarget"))));
//                target.setAchieveAmt(Double.parseDouble(cursor.getString(cursor.getColumnIndex("totAmt"))));
//
//                list.add(target);
//
//            }
//        } catch (Exception e) {
//
//            Log.v(TAG + " Except getTargtVsActul", e.toString());
//
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            dB.close();
//        }
//
//        return list;
//    }



    ///////////////////////////////////////////get expense report data//////////////////////////////////////////////////////////////

    public ArrayList<DayExpDet> getDaExpenseData(String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<DayExpDet> list = new ArrayList<DayExpDet>();

        String selectQuery = "SELECT d.ExpCode,r.ExpName, d.Amt from fExpense r, DayExpDet d  where d.expcode = r.expcode  and d.txndate between '" + from + "' and "
                +"'" + to + "'";
        // String selectQuery = "select * from DayExpDet WHERE " + dbHelper.REFNO + "='" + refno + "' and  TxnDate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) +"'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                DayExpDet deDet = new DayExpDet();

                deDet.setEXPDET_EXPCODE(cursor.getString(cursor.getColumnIndex(DayExpDetController.DAYEXPDET_EXPCODE)));
                deDet.setEXPDET_DESCRIPTION(cursor.getString(cursor.getColumnIndex(ExpenseController.FEXPENSE_NAME)));
                deDet.setEXPDET_AMOUNT(cursor.getString(cursor.getColumnIndex(DayExpDetController.DAYEXPDET_AMT)));

                list.add(deDet);

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }
    public ArrayList<OrderDetail> getPreSaleData(String from, String to) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        ArrayList<OrderDetail> list = new ArrayList<OrderDetail>();

        String selectQuery = "select d.ItemCode,d.txndate,i.NOUCase, ifnull((sum(d.CaseQty)),0) as cases ,ifnull((sum(d.PiceQty)),0) as pieses ,ifnull((sum(d.Amt)),0) as Amt , count(distinct h.debcode)as reach ,i.ItemName from fItem i, Forddet d, fordhed h  where d.ItemCode = i.ItemCode and h.refno = d.refno and d.txndate between '" + from + "' and "
                +"'" + to + "' group by d.ItemCode";
        // String selectQuery = "select * from DayExpDet WHERE " + dbHelper.REFNO + "='" + refno + "' and  TxnDate = '" + curYear + "-" + String.format("%02d", curMonth) + "-" + String.format("%02d", curDate) +"'";

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                OrderDetail ordDet = new OrderDetail();

                ordDet.setFORDERDET_ITEMCODE(cursor.getString(cursor.getColumnIndex(OrderDetailController.FORDDET_ITEM_CODE)));
                ordDet.setFORDERDET_TXNDATE(cursor.getString(cursor.getColumnIndex(dbHelper.TXNDATE)));
                ordDet.setFORDERDET_QOH(cursor.getString(cursor.getColumnIndex(ItemController.FITEM_NOU_CASE)));
                ordDet.setFORDERDET_CASES(cursor.getString(cursor.getColumnIndex("cases")));
                ordDet.setFORDERDET_PICE_QTY(cursor.getString(cursor.getColumnIndex("pieses")));
                ordDet.setFORDERDET_AMT(cursor.getString(cursor.getColumnIndex(OrderDetailController.FORDDET_AMT)));
                ordDet.setFORDERDET_ITEMNAME(cursor.getString(cursor.getColumnIndex(ItemController.FITEM_ITEM_NAME)));
                ordDet.setFORDERDET_SEQNO(""+cursor.getInt(cursor.getColumnIndex("reach")));

                list.add(ordDet);

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return list;
    }
}
