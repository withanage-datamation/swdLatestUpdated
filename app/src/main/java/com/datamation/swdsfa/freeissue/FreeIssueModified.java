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
public class FreeIssueModified {
    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    private String TAG = "FreeIssueModified";

    public FreeIssueModified(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
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

    public ArrayList<OrderDetail> sortAssortItems(ArrayList<OrderDetail> OrderList) {
        FreeHedController freeHedDS = new FreeHedController(context);
        FreeDetController freeDetDS = new FreeDetController(context);
        ArrayList<OrderDetail> newOrderList = new ArrayList<OrderDetail>();
        List<String> AssortList = new ArrayList<String>();

        for (int x = 0; x <= OrderList.size() - 1; x++) {
            // get Assort list for each Item code
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

    /**************************************************By Rashmi******************************************************************/

    public ArrayList<FreeItemDetails> getFreeItemsBySalesItem(ArrayList<OrderDetail> newOrderList,String debcode) {

        ArrayList<FreeItemDetails> freeList = new ArrayList<FreeItemDetails>();
        int slabassorted = 0, flatAssort = 0, mixAssort = 0;
        FreeHedController freeHedDS = new FreeHedController(context);
        FreeDetController freeDetDS = new FreeDetController(context);
        ArrayList<OrderDetail> dets = filterFreeItemsFromOrder(newOrderList);// rashmi -2019-09-09 :
       // ArrayList<FreeHed> freeSchemes = removeDuplicates(freeHedDS.getFreeIssueItemDetailByItem(dets));//// rashmi -2019-09-17-for get free schemes
        ArrayList<OrderDetail> assortDets = sortAssortItems(dets);//for free calculate
        ArrayList<FreeIssueDetail> freeIssueDetailsnotflat = new ArrayList<FreeIssueDetail>();
        ArrayList<FreeIssueDetail> freeIssueDetailsflat = new ArrayList<FreeIssueDetail>();
        for (OrderDetail det : assortDets) {
            int count = 0;
            //flat calculation 2023-04-03
//            Log.d( ">>>free-assortDets", ">>>assortDets"+assortDets.size());
//            Log.d( ">>>free-assortDets", ">>>assortDets"+assortDets.toString());
            count++;
            ArrayList<FreeHed> filterSchemes = freeHedDS.getFreeIssueItemDetailByItem(det.getFORDERDET_ITEMCODE(),debcode);
            Log.d( ">>>free-filterSchemesflat", ">>>filterSchemesflat"+filterSchemes.size());
            Log.d(">>>free-freeSchemesflat ","countflat"+count+"-"+ filterSchemes.toString());

            for (FreeHed free : filterSchemes) {
                FreeIssueDetail freeIssueDetail = new FreeIssueDetail();
                freeIssueDetail.setScheme(free.getFFREEHED_REFNO());
                freeIssueDetail.setPriority(free.getFFREEHED_PRIORITY());
                freeIssueDetail.setType(free.getFFREEHED_FTYPE());
                freeIssueDetail.setItemcode(det.getFORDERDET_ITEMCODE());
                freeIssueDetail.setQty(Integer.parseInt(det.getFORDERDET_QTY()));
                freeIssueDetail.setFreehed_free_item_qty(free.getFFREEHED_FREE_IT_QTY());
                freeIssueDetail.setFreehed_item_qty(free.getFFREEHED_ITEM_QTY());
                freeIssueDetailsflat.add(freeIssueDetail);
            }

        }
        for (OrderDetail detAssort : assortDets) {
            //flat calculation not flat 2023-04-03
//            Log.d( ">>>free-assortDets", ">>>assortDets"+assortDets.size());
//            Log.d( ">>>free-assortDets", ">>>assortDets"+assortDets.toString());
 //          count++;
            ArrayList<FreeHed> filterSchemesNotFlat = freeHedDS.getFreeIssueItemDetailByItem(detAssort.getFORDERDET_ITEMCODE(),debcode);
            Log.d( ">>>free-filterSchemesnotflat", ">>>filterSchemesnotflat"+assortDets.size());
          //  Log.d(">>>free-freeSchemesnotflat ","countnotflat"+count+"-"+ assortDets.toString());

            for (FreeHed free : filterSchemesNotFlat) {
                FreeIssueDetail freeIssueDetailNotFlat = new FreeIssueDetail();
                freeIssueDetailNotFlat.setScheme(free.getFFREEHED_REFNO());
                freeIssueDetailNotFlat.setPriority(free.getFFREEHED_PRIORITY());
                freeIssueDetailNotFlat.setType(free.getFFREEHED_FTYPE());
                freeIssueDetailNotFlat.setItemcode(detAssort.getFORDERDET_ITEMCODE());
                freeIssueDetailNotFlat.setQty(Integer.parseInt(detAssort.getFORDERDET_QTY()));
                freeIssueDetailNotFlat.setFreehed_free_item_qty(free.getFFREEHED_FREE_IT_QTY());
                freeIssueDetailNotFlat.setFreehed_item_qty(free.getFFREEHED_ITEM_QTY());
                freeIssueDetailsnotflat.add(freeIssueDetailNotFlat);
            }

        }

        ArrayList<FreeIssueDetail> filterListFlat = removeDuplicatesQtyFreeList(freeIssueDetailsflat);
        ArrayList<FreeIssueDetail> filterListNotFlat = removeDuplicatesQtyFreeList(freeIssueDetailsnotflat);
        // Menaka Edited
        String Old_itemcode="";
        String New_itemcode="";
        int calQty = 0;
        // Menaka Edited
        for (FreeIssueDetail free : filterListFlat) {

            int entedTotQty = free.getQty();
            Log.d(">>>free-entedTotQty ","entedTotQty"+entedTotQty);
    // Menaka Edited
            New_itemcode = free.getItemcode();

            if(Old_itemcode==New_itemcode){
                free.setQty(entedTotQty-calQty);
                entedTotQty = entedTotQty-calQty;
                Log.d(">>>free-Old_itemcode==New_itemcode","entedTotQty"+entedTotQty);
            }else{
                calQty = 0;
            }
    // Menaka Edited
            Log.d(">>>free-Inside ", free.getScheme() + " filter");

            //  int entedTotQty = Integer.parseInt("100");
            if (free.getType().equals("Flat")) {
                Log.d(">>>free-Inside ", free.getScheme() + " FLAT");
                // only-----
                // flat scheme item qty => 12:1
                int itemQty = (int) Float.parseFloat(free.getFreehed_item_qty());
                Log.d(">>>free-Inside ", free.getFreehed_item_qty());
                // Debtor code from selected debtor
                String debCode = SharedPref.getInstance(context).getSelectedDebCode();
                Log.d(">>>free-Inside ", debCode);
                // get debtor count from FIS no
                int debCount = new FreeDebController(context).getRefnoByDebCount(free.getScheme());
                Log.d(">>>free-Inside ", debCount + "");
                // select debtor from FIS no
                int IsValidDeb = new FreeDebController(context).isValidDebForFreeIssue(free.getScheme(), debCode);
                if (IsValidDeb == 1)
                    Log.d(">>>free-IsValidDeb", " TRUE");
                else
                    Log.d(">>>free-IsValidDeb", "TRUE");
                // get assort count from FIS no
                int assortCount = new FreeDetController(context).getAssoCountByRefno(free.getScheme());
                Log.d(">>>free-assortCount", free.getScheme() + " - " + assortCount);

                if (debCount > 0) {

                    // if debtor eligible for free issues
                    if (IsValidDeb == 1) {


                        if ((int) Math.round(entedTotQty / itemQty) > 0) {

                            FreeItemDetails details = new FreeItemDetails();
                            details.setRefno(free.getScheme());
                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                            details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(free.getFreehed_free_item_qty()));
                           // free.setCalculatedFreeQty();
                            calQty = (int) Math.round(entedTotQty / itemQty)* (int) itemQty;  // Menaka Edited
                            freeList.add(details);

                            Log.v(">>>free-Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(free.getFreehed_free_item_qty()) + "");
                            entedTotQty = (int) Math.round(entedTotQty % itemQty);
                        }
                    }

                    // if ref no is NOT in FreeDeb
                } else {

                    if ((int) Math.round(entedTotQty / itemQty) > 0) {

                        FreeItemDetails details = new FreeItemDetails();
                        details.setRefno(free.getScheme());
                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                        details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(free.getFreehed_free_item_qty()));
                        calQty = (int) Math.round(entedTotQty / itemQty)* (int) itemQty;  // Menaka Edited
                        freeList.add(details);

                        Log.d(">>>free-Free Issues ", (int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(free.getFreehed_free_item_qty()) + "");
                        entedTotQty = (int) Math.round(entedTotQty % itemQty);
                    }
                }
                //  }
            }
            Old_itemcode = New_itemcode;
        }
        for (FreeIssueDetail free : filterListNotFlat) {

            int entedTotQty = free.getQty();
            Log.d(">>>free-entedTotQty ","entedTotQty"+entedTotQty);
            // Menaka Edited
            New_itemcode = free.getItemcode();

            if(Old_itemcode==New_itemcode){
                free.setQty(entedTotQty-calQty);
                entedTotQty = entedTotQty-calQty;
                Log.d(">>>free-Old_itemcode==New_itemcode","entedTotQty"+entedTotQty);
            }else{
                calQty = 0;
            }
            // Menaka Edited
            Log.d(">>>free-Inside ", free.getScheme() + " filter");

            //  int entedTotQty = Integer.parseInt("100");
                if (free.getType().equals("Slab")) {
                Log.d(">>>free-Inside ", free.getScheme() + "Slab");
                FreeSlabController freeSlabDS = new FreeSlabController(context);
                final ArrayList<FreeSlab> slabList;

                slabList = freeSlabDS.getSlabdetails(free.getScheme(), entedTotQty);


                for (FreeSlab freeSlab : slabList) {

                    String debCode = SharedPref.getInstance(context).getSelectedDebCode();
                    int debCount = new FreeDebController(context).getRefnoByDebCount(free.getScheme());
                    int IsValidDeb = new FreeDebController(context).isValidDebForFreeIssue(free.getScheme(), debCode);

                    if (debCount > 0) {// selected debtors

                        if (IsValidDeb == 1) {
                            Log.d(">>>free-Stab", freeSlab.getFFREESLAB_FREE_QTY());
                            FreeItemDetails details = new FreeItemDetails();
                            details.setRefno(free.getScheme());
                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                            details.setFreeQty((int) (Float.parseFloat(freeSlab.getFFREESLAB_FREE_QTY())));
                            freeList.add(details);
                        }

                    } else {// all debtor for freeissues

                        Log.d(">>>free-Stab", freeSlab.getFFREESLAB_FREE_QTY());
                        FreeItemDetails details = new FreeItemDetails();
                        details.setRefno(free.getScheme());
                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                        details.setFreeQty((int) (Float.parseFloat(freeSlab.getFFREESLAB_FREE_QTY())));
                        freeList.add(details);

                    }
                }
            } else if (free.getType().equals("Mix")) {
                Log.d(">>>free-Inside ", free.getScheme() + " Mix");
                FreeMslabController freeMslabDS = new FreeMslabController(context);
                final ArrayList<FreeMslab> mixList;
                int assortCount = new FreeDetController(context).getAssoCountByRefno(free.getScheme());
                mixList = freeMslabDS.getMixDetails(free.getScheme(), entedTotQty);
                for (FreeMslab freeMslab : mixList) {
                    String debCode = SharedPref.getInstance(context).getSelectedDebCode();
                    int debCount = new FreeDebController(context).getRefnoByDebCount(free.getScheme());
                    int IsValidDeb = new FreeDebController(context).isValidDebForFreeIssue(free.getScheme(), debCode);
                    if (debCount > 0) {// selected debtors
                        if (IsValidDeb == 1) {

//                    if (assortCount > 1) {
//
//                        int index = 0;
//                        boolean assortUpdate = false;
//
//                        for (FreeItemDetails freeItemDetails : freeList) {
//                            if (freeItemDetails.getRefno().equals(free.getScheme())) {
//
//                                int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
//                                freeList.get(index).setRefno(free.getScheme());
//                                freeList.get(index).setFreeIssueSelectedItem("should be set");
//                                freeList.get(index).setFreeQty((int) Math.round(mixAssort / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
//
//                                assortUpdate = true;
//                            }
//                            index++;
//                        }
//
//                        if (!assortUpdate) {
//
//                            FreeItemDetails details = new FreeItemDetails();
//                            int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
//                            details.setRefno(free.getScheme());
//                            details.setFreeIssueSelectedItem("should be set");
//                            details.setFreeQty((int) Math.round(mixAssort / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
//                            freeList.add(details);
//                        }
//
//                    } else {

                            FreeItemDetails details = new FreeItemDetails();
                            int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                            details.setRefno(free.getScheme());
                            details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                            details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
                            freeList.add(details);

                            //   }
                        }

                    } else {// all debtor for freeissues

//                if (assortCount > 1) {
//                    int index = 0;
//                    boolean assortUpdate = false;
//                    for (FreeItemDetails freeItemDetails : freeList) {
//                        if (freeItemDetails.getRefno().equals(free.getScheme())) {
//
//                            int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
//                            freeList.get(index).setRefno(free.getScheme());
//                            freeList.get(index).setFreeIssueSelectedItem("should be set");
//                            freeList.get(index).setFreeQty((int) Math.round(mixAssort / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
//
//                            assortUpdate = true;
//                        }
//
//                        index++;
//                    }
//
//                    if (!assortUpdate) {
//
//                        FreeItemDetails details = new FreeItemDetails();
//                        int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
//                        details.setRefno(free.getScheme());
//                        details.setFreeIssueSelectedItem("should be set");
//                        details.setFreeQty((int) Math.round(mixAssort / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
//                        freeList.add(details);
//                    }
//
//                } else {
                        FreeItemDetails details = new FreeItemDetails();
                        int itemQty = (int) Float.parseFloat(freeMslab.getFFREEMSLAB_ITEM_QTY());
                        details.setRefno(free.getScheme());
                        details.setSaleItemList(new FreeItemController(context).getFreeItemsByRefno(free.getScheme()));
                        details.setFreeQty((int) Math.round(entedTotQty / itemQty) * (int) Float.parseFloat(freeMslab.getFFREEMSLAB_FREE_IT_QTY()));
                        freeList.add(details);
                        //  }
                    }
                }
            }
            Old_itemcode = New_itemcode;
        }
        //new combined freeIssueDetails loop close
        //      }//end free scheme loop
        //  }//end free items loop

        //     }

//            }else{
//                Toast.makeText(context,"cannot proceed free calculation for zero quantities",Toast.LENGTH_LONG).show();
//            }
        // }
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
