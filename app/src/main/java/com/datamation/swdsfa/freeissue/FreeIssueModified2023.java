package com.datamation.swdsfa.freeissue;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.swdsfa.controller.FreeDebController;
import com.datamation.swdsfa.controller.FreeDetController;
import com.datamation.swdsfa.controller.FreeHedController;
import com.datamation.swdsfa.controller.FreeItemController;
import com.datamation.swdsfa.controller.FreeMslabController;
import com.datamation.swdsfa.controller.FreeSlabController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.helpers.DatabaseHelper;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.FreeHed;
import com.datamation.swdsfa.model.FreeIssueDetail;
import com.datamation.swdsfa.model.FreeItemDetails;
import com.datamation.swdsfa.model.FreeMslab;
import com.datamation.swdsfa.model.FreeSlab;
import com.datamation.swdsfa.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;


/*created by rashmi-2019-09-10*/
public class FreeIssueModified2023 {
    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "FreeIssueModified";

    public FreeIssueModified2023(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    //filter free items from order details
    public ArrayList<OrderDetail> filterFreeItemsFromOrder(ArrayList<OrderDetail> OrderList) {
        //rashmi for new swadeshi sfa - 2019-09-09
        ArrayList<OrderDetail> newOrderList = new ArrayList<OrderDetail>();
        for (OrderDetail det : OrderList) {

            if (det.getFORDERDET_ITEMCODE().equals(getSaleItemByItemCode(det.getFORDERDET_ITEMCODE()))) {
                newOrderList.add(det);
            }
        }

        for (OrderDetail order : newOrderList) {

            Log.d("has free items", order.getFORDERDET_ITEMCODE() + " - " + order.getFORDERDET_QTY());

        }
        return newOrderList;

    }


    public ArrayList<OrderDetail> sortAssortItems(ArrayList<OrderDetail> OrderList,String debcode) {
        FreeHedController freeHedDS = new FreeHedController(context);
        FreeDetController freeDetDS = new FreeDetController(context);
        ArrayList<OrderDetail> newOrderList = new ArrayList<OrderDetail>();
        List<String> AssortList = new ArrayList<String>();

        for (int x = 0; x <= OrderList.size() - 1; x++) {
            // get Assort list for each Item code
            //    AssortList = freeDetDS.getAssortByRefno(freeHedDS.getRefoByItemCode(OrderList.get(x).getFORDERDET_ITEMCODE()));
            AssortList = freeDetDS.getAssortByRefno(freeHedDS.getRefoByItemCodeAndDebtor(OrderList.get(x).getFORDERDET_ITEMCODE(),debcode));
            Log.d( ">>>free-AssortList", ">>>AssortList"+AssortList.toString());
            if (AssortList.size() > 0) {

                for (int y = x + 1; y <= OrderList.size() - 1; y++) {

                    for (int i = 0; i <= AssortList.size() - 1; i++) {

                        if (OrderList.get(y).getFORDERDET_ITEMCODE().equals(AssortList.get(i))) {
                            OrderList.get(x).setFORDERDET_QTY(String.valueOf(Integer.parseInt(OrderList.get(x).getFORDERDET_QTY()) + Integer.parseInt(OrderList.get(y).getFORDERDET_QTY())));
                            OrderList.get(y).setFORDERDET_QTY("0");
                        }

                    }
                }
            }
        }

        // Remove zero quantities from Arraylist
        for (int i = 0; i <= OrderList.size() - 1; i++) {

            if (!OrderList.get(i).getFORDERDET_QTY().equals("0")) {

                newOrderList.add(OrderList.get(i));
            }

        }

        for (OrderDetail order : newOrderList) {

            Log.v("Rashmi", order.getFORDERDET_ITEMCODE() + " - " + order.getFORDERDET_QTY());

        }
        return newOrderList;

    }
    public ArrayList<OrderDetail> sortAssortItems(ArrayList<OrderDetail> OrderList) {
        FreeHedController freeHedDS = new FreeHedController(context);
        FreeDetController freeDetDS = new FreeDetController(context);
        ArrayList<OrderDetail> newOrderList = new ArrayList<OrderDetail>();
        List<String> AssortList = new ArrayList<String>();

        for (int x = 0; x <= OrderList.size() - 1; x++) {
            // get Assort list for each Item code
            //    AssortList = freeDetDS.getAssortByRefno(freeHedDS.getRefoByItemCode(OrderList.get(x).getFORDERDET_ITEMCODE()));
            AssortList = freeDetDS.getAssortByRefno(freeHedDS.getRefoByItemCode(OrderList.get(x).getFORDERDET_ITEMCODE()));
            Log.d( ">>>free-AssortList", ">>>AssortList"+AssortList.toString());
            if (AssortList.size() > 0) {

                for (int y = x + 1; y <= OrderList.size() - 1; y++) {

                    for (int i = 0; i <= AssortList.size() - 1; i++) {

                        if (OrderList.get(y).getFORDERDET_ITEMCODE().equals(AssortList.get(i))) {
                            OrderList.get(x).setFORDERDET_QTY(String.valueOf(Integer.parseInt(OrderList.get(x).getFORDERDET_QTY()) + Integer.parseInt(OrderList.get(y).getFORDERDET_QTY())));
                            OrderList.get(y).setFORDERDET_QTY("0");
                        }

                    }
                }
            }
        }

        // Remove zero quantities from Arraylist
        for (int i = 0; i <= OrderList.size() - 1; i++) {

            if (!OrderList.get(i).getFORDERDET_QTY().equals("0")) {

                newOrderList.add(OrderList.get(i));
            }

        }

        for (OrderDetail order : newOrderList) {

            Log.v("Rashmi", order.getFORDERDET_ITEMCODE() + " - " + order.getFORDERDET_QTY());

        }
        return newOrderList;

    }
    /**************************************************get free issue itemcode (sale item) ******************************************************************/
    public String getSaleItemByItemCode(String itemCode) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "select itemcode from ffreedet  where refno in (select refno from ffreehed where date('now') between vdatef and vdatet ) and itemcode='" + itemCode + "'";
        String saleItemToFree = null;
        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                saleItemToFree = cursor.getString(cursor.getColumnIndex(FreeDetController.FFREEDET_ITEM_CODE));

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return saleItemToFree;
    }

    /**************************************************By Rashmi 2023-04-05******************************************************************/

    public ArrayList<FreeItemDetails> getFreeItemsBySalesItem(ArrayList<OrderDetail> newOrderList,String debcode) {

        ArrayList<FreeItemDetails> freeList = new ArrayList<FreeItemDetails>();
        FreeHedController freeHedDS = new FreeHedController(context);

        ArrayList<OrderDetail> dets = sortAssortItems(newOrderList,debcode);


        for (OrderDetail det : dets) {// ---------------------- order list----------------------
            String debCodeStr = new OrderController(context).getRefnoByDebcode(det.getFORDERDET_REFNO());
            String freeRefno = new FreeHedController(context).getRefoByItemCode(det.getFORDERDET_ITEMCODE());
            Log.d("forddet", det.getFORDERDET_ITEMCODE());
            // get record from freeHed about valid scheme
            int slabassorted = 0, flatAssort = 0, mixAssort = 0;
            ArrayList<FreeHed> arrayListchk = freeHedDS.getFreeIssueItemDetailByItem(det.getFORDERDET_ITEMCODE(),debCodeStr);
            Log.d(">>>free", "arrayListchk"+arrayListchk.size());
            Log.d(">>>free", "freelist"+arrayListchk.toString());
            // ArrayList<FreeHed> arrayList = freeHedDS.getFreeIssueItemDetailByRefno(det.getFORDERDET_ITEMCODE());

            if(arrayListchk.size()==0){
                Log.d(">>>free", "NO FREE SCHEMES"+arrayListchk.toString());
                //android.widget.Toast.makeText(context, "Not Eligible  FREE ISSUES SCHEMA....!", android.widget.Toast.LENGTH_LONG).show();
            }else{
                // selected item qty
                int entedTotQtyIsZero = Integer.parseInt(det.getFORDERDET_QTY());

                if (entedTotQtyIsZero > 0) {

                    for (FreeHed freeHed : arrayListchk) {// --------Related free issue
                        // list------
                        int entedTotQty = Integer.parseInt(det.getFORDERDET_QTY());
                        if (freeHed.getFFREEHED_PRIORITY().equals("1")) {
                            // flat
                            if (freeHed.getFFREEHED_FTYPE().equals("Flat")) {
                                // -------Flat
                                // only-----
                                // flat scheme item qty => 12:1
                                int itemQty = (int) Float.parseFloat(freeHed.getFFREEHED_ITEM_QTY());
                                // Debtor code from order header ref no ORASA/00006
                                String debCode = new OrderController(context).getRefnoByDebcode(det.getFORDERDET_REFNO());
                                // get debtor count from FIS/098 no
                                int debCount = new FreeDebController(context).getRefnoByDebCount(freeHed.getFFREEHED_REFNO());
                                // select debtor from FIS no & R0001929
                                int IsValidDeb = new FreeDebController(context).isValidDebForFreeIssue(freeHed.getFFREEHED_REFNO(), debCode);
                                // get assort count from FIS no
                                int assortCount = new FreeDetController(context).getAssoCountByRefno(freeHed.getFFREEHED_REFNO());

                                // if its assorted
                                if (assortCount > 1) {

                                    flatAssort = flatAssort + entedTotQty;

                                    int index = 0;
                                    boolean assortUpdate = false;

                                    for (FreeItemDetails freeItemDetails : freeList) {

                                        if (freeItemDetails.getRefno().equals(freeHed.getFFREEHED_REFNO())) {

                                            freeList.get(index).setRefno(freeHed.getFFREEHED_REFNO());
                                            freeList.get(index).setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            freeList.get(index).setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                            freeList.get(index).setFreeQty((int) Math.round(flatAssort / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                            assortUpdate = true;
                                        }

                                        index++;
                                    }

                                    if (!assortUpdate) {// When 1st time running

                                        // available for everyone

                                        // IF entered qty/scheme qty is more
                                        // than 0
                                        if ((int) Math.round(entedTotQty / itemQty) > 0) {

                                            FreeItemDetails details = new FreeItemDetails();
                                            // details.setFreeItem(new
                                            // FreeItemDS(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                            details.setRefno(freeHed.getFFREEHED_REFNO());
                                            details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                            details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                            freeList.add(details);

                                            Log.v("Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()) + "");
                                            entedTotQty = (int) Math.round(entedTotQty % itemQty);
                                        }

                                    }

                                    // if not assorted
                                } else {

                                    if ((int) Math.round(entedTotQty / itemQty) > 0) {

                                        FreeItemDetails details = new FreeItemDetails();
                                        // details.setFreeItem(new
                                        // FreeItemDS(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                        details.setRefno(freeHed.getFFREEHED_REFNO());
                                        details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                        details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                        freeList.add(details);

                                        Log.v("Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()) + "");
                                        entedTotQty = (int) Math.round(entedTotQty % itemQty);
                                    }
                                }

                            }  else if (freeHed.getFFREEHED_FTYPE().equals("Mix")) {
                                // slabAssort

                                FreeMslabController freeMslabDS = new FreeMslabController(context);
                                final ArrayList<FreeMslab> mixList;
                                int assortCount = new FreeDetController(context).getAssoCountByRefno(freeHed.getFFREEHED_REFNO());
                                if (assortCount >= 1) {// if assorted
                                    mixAssort = mixAssort + entedTotQty;
                                    mixList = freeMslabDS.getMixDetails(freeHed.getFFREEHED_REFNO(), mixAssort);
                                } else {
                                    mixList = freeMslabDS.getMixDetails(freeHed.getFFREEHED_REFNO(), entedTotQty);
                                }

                                for (FreeMslab freeMslab : mixList) {

                                    // all debtor for freeissues

                                    if (assortCount > 1) {
                                        int index = 0;
                                        boolean assortUpdate = false;
                                        for (FreeItemDetails freeItemDetails : freeList) {
                                            if (freeItemDetails.getRefno().equals(freeHed.getFFREEHED_REFNO())) {

                                                int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                                freeList.get(index).setRefno(freeHed.getFFREEHED_REFNO());
                                                freeList.get(index).setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                                freeList.get(index).setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                                freeList.get(index).setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/itemQty) * mixAssort));
                                                assortUpdate = true;
                                            }

                                            index++;
                                        }

                                        if (!assortUpdate) {

                                            FreeItemDetails details = new FreeItemDetails();

                                            // resolved to free item count issue 08/08/2023 --------------------------------

                                            int freeItemQtyFromMSlab = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                            if (entedTotQty >= freeItemQtyFromMSlab)
                                            {
                                                details.setRefno(freeHed.getFFREEHED_REFNO());
                                                details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                                details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                                // have to update the calculation also

//                                                details.setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/freeItemQty) * mixAssort));

                                                int actualFreeItemQty = (int)(mixAssort/freeItemQtyFromMSlab) * (int)(Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
                                                details.setFreeQty(actualFreeItemQty);
                                                freeList.add(details);
                                            }

                                            //-------------------------------------------------------------------
                                        }

                                    } else {
                                        FreeItemDetails details = new FreeItemDetails();

                                        // resolved to free item count issue 08/08/2023 --------------------------------

                                        int freeItemQtyFromMSlab = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                        if (entedTotQty >= freeItemQtyFromMSlab)
                                        {
                                            details.setRefno(freeHed.getFFREEHED_REFNO());
                                            details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                            // // have to update the calculation also
//                                            details.setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/freeItemQty) * mixAssort));

                                            int actualFreeItemQty = (int)(mixAssort/freeItemQtyFromMSlab) * (int)(Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
                                            details.setFreeQty(actualFreeItemQty);
                                            freeList.add(details);
                                        }
                                    }
                                }
                            }
                        }else {
                            // flat
                            if (freeHed.getFFREEHED_FTYPE().equals("Flat")) {// -------Flat
                                // only-----
                                // flat scheme item qty => 12:1
                                int itemQty = (int) Float.parseFloat(freeHed.getFFREEHED_ITEM_QTY());
                                // Debtor code from order header ref no ORASA/00006
                                String debCode = new OrderController(context).getRefnoByDebcode(det.getFORDERDET_REFNO());
                                // get debtor count from FIS/098 no
                                int debCount = new FreeDebController(context).getRefnoByDebCount(freeHed.getFFREEHED_REFNO());
                                // select debtor from FIS no & R0001929
                                int IsValidDeb = new FreeDebController(context).isValidDebForFreeIssue(freeHed.getFFREEHED_REFNO(), debCode);
                                // get assort count from FIS no
                                int assortCount = new FreeDetController(context).getAssoCountByRefno(freeHed.getFFREEHED_REFNO());

                                // if its assorted
                                if (assortCount > 1) {

                                    flatAssort = flatAssort + entedTotQty;

                                    int index = 0;
                                    boolean assortUpdate = false;

                                    for (FreeItemDetails freeItemDetails : freeList) {

                                        if (freeItemDetails.getRefno().equals(freeHed.getFFREEHED_REFNO())) {

                                            freeList.get(index).setRefno(freeHed.getFFREEHED_REFNO());
                                            freeList.get(index).setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            freeList.get(index).setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                            freeList.get(index).setFreeQty((int) Math.round(flatAssort / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                            assortUpdate = true;
                                        }

                                        index++;
                                    }

                                    if (!assortUpdate) {// When 1st time running

                                        // available for everyone

                                        // IF entered qty/scheme qty is more
                                        // than 0
                                        if ((int) Math.round(entedTotQty / itemQty) > 0) {

                                            FreeItemDetails details = new FreeItemDetails();
                                            // details.setFreeItem(new
                                            // FreeItemDS(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                            details.setRefno(freeHed.getFFREEHED_REFNO());
                                            details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                            details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                            freeList.add(details);

                                            Log.v("Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()) + "");
                                            entedTotQty = (int) Math.round(entedTotQty % itemQty);
                                        }
                                    }

                                    // if not assorted
                                } else {
                                    if ((int) Math.round(entedTotQty / itemQty) > 0) {

                                        FreeItemDetails details = new FreeItemDetails();
                                        // details.setFreeItem(new
                                        // FreeItemDS(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                        details.setRefno(freeHed.getFFREEHED_REFNO());
                                        details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));

                                        details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()));
                                        freeList.add(details);

                                        Log.v("Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeHed.getFFREEHED_FREE_IT_QTY()) + "");
                                        entedTotQty = (int) Math.round(entedTotQty % itemQty);
                                    }
                                }

                            }  else if (freeHed.getFFREEHED_FTYPE().equals("Mix")) {
                                // slabAssort

                                FreeMslabController freeMslabDS = new FreeMslabController(context);
                                final ArrayList<FreeMslab> mixList;
                                int assortCount = new FreeDetController(context).getAssoCountByRefno(freeHed.getFFREEHED_REFNO());
                                if (assortCount > 1) {// if assorted
                                    mixAssort = mixAssort + entedTotQty;
                                    mixList = freeMslabDS.getMixDetails(freeHed.getFFREEHED_REFNO(), mixAssort);
                                } else {
                                    mixList = freeMslabDS.getMixDetails(freeHed.getFFREEHED_REFNO(), entedTotQty);
                                }

                                for (FreeMslab freeMslab : mixList) {

                                    // all debtor for freeissues

                                    if (assortCount > 1) {
                                        int index = 0;
                                        boolean assortUpdate = false;
                                        for (FreeItemDetails freeItemDetails : freeList) {
                                            if (freeItemDetails.getRefno().equals(freeHed.getFFREEHED_REFNO())) {

                                                int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                                freeList.get(index).setRefno(freeHed.getFFREEHED_REFNO());
                                                freeList.get(index).setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                                freeList.get(index).setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                                freeList.get(index).setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/itemQty) * mixAssort));
                                                assortUpdate = true;
                                            }

                                            index++;
                                        }

                                        if (!assortUpdate) {

                                            FreeItemDetails details = new FreeItemDetails();
                                            int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                            details.setRefno(freeHed.getFFREEHED_REFNO());
                                            details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                            details.setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/itemQty) * mixAssort));
                                            freeList.add(details);
                                        }

                                    } else {
                                        FreeItemDetails details = new FreeItemDetails();
                                        int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                                        details.setRefno(freeHed.getFFREEHED_REFNO());
                                        details.setFreeIssueSelectedItem(det.getFORDERDET_ITEMCODE());
                                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(freeHed.getFFREEHED_REFNO()));
                                        details.setFreeQty((int) (((Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()))/itemQty) * entedTotQty));
                                        freeList.add(details);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }

        return freeList;

    }

    public ArrayList<FreeIssueDetail> removeDuplicatesQtyFreeList(ArrayList<FreeIssueDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if ((list.get(i).getQty() == list.get(j).getQty()) && (list.get(i).getItemcode().equals(list.get(j).getItemcode())) && (list.get(i).getScheme().equals(list.get(j).getScheme()))) {
                    list.remove(list.get(j));
                }
            }
        }
        return list;
    }
}