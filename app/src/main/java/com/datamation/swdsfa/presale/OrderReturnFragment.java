package com.datamation.swdsfa.presale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.swdsfa.R;
import com.datamation.swdsfa.adapter.ProductAdapter;
import com.datamation.swdsfa.adapter.ReturnReasonAdapter;
import com.datamation.swdsfa.adapter.SalesReturnDetailsAdapter;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.ItemController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.controller.ReasonController;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.controller.SalesReturnController;
import com.datamation.swdsfa.controller.SalesReturnDetController;
import com.datamation.swdsfa.dialog.CustomKeypadDialogPrice;
import com.datamation.swdsfa.helpers.PreSalesResponseListener;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.model.FInvRDet;
import com.datamation.swdsfa.model.FInvRHed;
import com.datamation.swdsfa.model.Item;
import com.datamation.swdsfa.model.Reason;
import com.datamation.swdsfa.settings.ReferenceNum;
import com.datamation.swdsfa.view.PreSalesActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class OrderReturnFragment extends Fragment implements View.OnClickListener {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    public String RetRefNo = "", RetOrderRefNo = "";
    View view;
    Button itemSearch, bAdd, bFreeIssue, reasonSearch;
    EditText lblItemName, txtQty, editTotDisc, lblReason,lblNou;
    TextView lblPrice;
    int totPieces = 0;
    double amount = 0.00,price = 0.00,minPrice = 0.00, maxPrice = 0.00, changedPrice = 0.0;
    double values = 0.00, iQoh;

    PreSalesResponseListener preSalesResponseListener;
    //TextView lblNou, lblPrice;
    Item selectedItem = null;
    Reason selectedReason = null;
    int seqno = 0, index_id = 0;
    ListView lv_return_det;
    ArrayList<FInvRDet> returnList;
    SharedPref mSharedPref;
    boolean hasChanged = false;
    String locCode;
    double brandDisPer;
    Spinner returnType;
    ArrayList<Item> list = null;
    ArrayList<Item> itemList = null;
    ArrayList<Reason> reasonList = null;
    PreSalesActivity activity;
    MyReceiver r;
    private  SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_return, container, false);
        activity = (PreSalesActivity) getActivity();
        mSharedPref = new SharedPref(getActivity());
        seqno = 0;
        totPieces = 0;
        localSP = getActivity().getSharedPreferences(SETTINGS, 0);
        itemSearch = (Button) view.findViewById(R.id.btn_item_search);
        reasonSearch = (Button) view.findViewById(R.id.btn_reason_search);
        bAdd = (Button) view.findViewById(R.id.btn_add);
        bFreeIssue = (Button) view.findViewById(R.id.btn_free);
        lblItemName = (EditText) view.findViewById(R.id.et_item);
        lblReason = (EditText) view.findViewById(R.id.et_reason);

        lv_return_det = (ListView) view.findViewById(R.id.lv_pre_return_det);
        //RetRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.salRet));

        String orRefNo = new OrderController(getActivity()).getActiveRefNoFromOrders();
        String activeRetRefNo = new SalesReturnController(getActivity()).getActiveInnerReturnRefNoByOrderRefNo(orRefNo);

        if (activeRetRefNo.equals(""))
        {
            RetRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.salRet));
        }
        else
        {
            RetRefNo = activeRetRefNo;
        }

        RetOrderRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.NumVal));
//        RefNo = "/0001";

        editTotDisc = (EditText) view.findViewById(R.id.et_TotalDisc);
        lblNou = (EditText) view.findViewById(R.id.et_pieces);
        lblPrice = (TextView) view.findViewById(R.id.tv_price);
        txtQty = (EditText) view.findViewById(R.id.tv_unit);
        returnType = (Spinner) view.findViewById(R.id.spinner_return_Type);
        itemSearch.setOnClickListener(this);
        reasonSearch.setOnClickListener(this);
        bAdd.setOnClickListener(this);
        bFreeIssue.setOnClickListener(this);

//        if (new SalesReturnController(getActivity()).isAnyActive())
//        {
//            activity.selectedReturnDet = new OrderDetailController(getActivity()).getActiveReturnDet();
//            //activity.selectedOrderDet.setFORDERDET_REFNO(new OrderDetailController(getActivity()).getActiveRefNo());
//        }

        if(activity.selectedPreHed != null){
            Toast.makeText(getActivity(),"InvHed not null"+activity.selectedPreHed.toString(),Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"InvHed null",Toast.LENGTH_LONG).show();

//            preSalesResponseListener.moveBackToCustomer_pre(0);
//            Toast.makeText(getActivity(), "Cannot proceed,Please click arrow button to save header details...", Toast.LENGTH_LONG).show();

        }

        ArrayList<FInvRHed> getReturnHed = new SalesReturnController(getActivity()).getAllActiveInvrhed();

        if (!getReturnHed.isEmpty()) {

            for (FInvRHed returnHed : getReturnHed) {
                activity.selectedReturnHed = returnHed;

                if (activity.selectedRetDebtor == null) {
                    CustomerController debtorDS = new CustomerController(getActivity());
                    activity.selectedRetDebtor = debtorDS.getSelectedCustomerByCode(returnHed.getFINVRHED_DEBCODE());
                }
            }
        }

        ArrayList<String> strList = new ArrayList<String>();
        strList.add("Select Return type to continue...");
        strList.add("MR-MARKET RETURN");
        strList.add("UR-USABLE RETURN");
        strList.add("RP-REPLACEMENT");

        final ArrayAdapter<String> returnTypeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, strList);
        returnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        returnType.setAdapter(returnTypeAdapter);
        FetchData();

        /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
        lblPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((lblPrice.length() > 0)) {
                    if(!(minPrice<= Double.parseDouble(lblPrice.getText().toString())) || !(maxPrice>= Double.parseDouble(lblPrice.getText().toString()))){
                        //lblPrice.setText(String.format("%.2f",price));
                        Toast.makeText(getActivity(),"Cannot change price",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        txtQty.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if(! txtQty.getText().equals(null))
                {
                    if ((txtQty.length() > 0))
                    {
                        totPieces = Integer.parseInt(txtQty.getText().toString());
                        hasChanged = true;
                        amount = Double.parseDouble(txtQty.getText().toString())
                                * Double.parseDouble(lblPrice.getText().toString());
                    }
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        txtQty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                txtQty.setText("");
            }
        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_return_det.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FInvRDet returnDet = returnList.get(position);
                deleteReturnDialog(getActivity(), "Return Details ", returnDet.getFINVRDET_ITEMCODE(),RetRefNo);
                return true;
            }
        });

        /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

        lv_return_det.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                FInvRDet returnDet = returnList.get(position);
                FetchData();
                bAdd.setText("EDIT");
                selectedItem = new Item();
                selectedItem.setFITEM_ITEM_CODE(returnDet.getFINVRDET_ITEMCODE());
                index_id = Integer.parseInt(returnDet.getFINVRDET_ID());
                lblItemName.setText(new ItemController(getActivity()).getItemNameByCode(returnDet.getFINVRDET_ITEMCODE()));
                txtQty.setText(returnDet.getFINVRDET_QTY());
                //lblPrice.setText(new ItemPriController(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(), activity.selectedRetDebtor.getCusPrilCode()));
                hasChanged = false;
                editTotDisc.setText(returnDet.getFINVRDET_DIS_AMT());
                lblReason.setText(returnDet.getFINVRDET_RETURN_REASON());
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_item_search:
                clearTextFields();

                new LoardingItemsFromDB().execute();


                break;
            case R.id.btn_reason_search:
                reasonsDialogbox();
                break;
            case R.id.btn_add: {
                if (!(lblItemName.getText().toString().equals("")) && !((lblReason.getText().toString().equals(""))) && !(returnType.getSelectedItem().toString().contains("Select Return"))) {
//                    Log.v("ITEM NAME>>>>",lblItemName.getText().toString());
//                    Log.v("ITEM REASON>>>>",lblReason.getText().toString());
                    if(TextUtils.isEmpty(txtQty.getText()))
                    {
                        txtQty.setText("0");
                    }
                    if (Integer.parseInt(txtQty.getText().toString()) > 0) {

                        String orRefNo = new OrderController(getActivity()).getActiveRefNoFromOrders();
                        String activeRetRefNo = new SalesReturnController(getActivity()).getActiveInnerReturnRefNoByOrderRefNo(orRefNo);

                        if (activeRetRefNo.equals(""))
                        {
                            RetRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.salRet));
                        }
                        else
                        {
                            RetRefNo = activeRetRefNo;
                        }

                        //RetRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.salRet));
//                        Log.v("TOTAL PEIACES>>>>",totPieces+"");
                        FInvRDet ReturnDet = new FInvRDet();
                        ArrayList<FInvRDet> ReturnList = new ArrayList<FInvRDet>();

                        ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();

                        //   String TaxedAmt = new TaxDetDS(getActivity()).calculateTax(selectedItem.getFITEM_ITEM_CODE(),
                        //          new BigDecimal(amount - Double.parseDouble(editTotDisc.getText().toString())));
                        FInvRHed hed = new FInvRHed();

                        hed.setFINVRHED_REFNO(RetRefNo);
                        //hed.setFINVRHED_MANUREF(activity.selectedPreHed.getORDER_MANUREF());
                        //hed.setFINVRHED_INV_REFNO(activity.selectedPreHed.getORDER_REFNO());
                        //hed.setFINVRHED_REMARKS(activity.selectedPreHed.getORDER_REMARKS());
                        hed.setFINVRHED_ADD_USER(new SalRepController(getActivity()).getCurrentRepCode());
                        hed.setFINVRHED_ADD_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        hed.setFINVRHED_ADD_MACH(localSP.getString("MAC_Address", "No MAC Address").toString());
                        hed.setFINVRHED_TXNTYPE("24");
                        //hed.setFINVRHED_TXN_DATE(activity.selectedPreHed.getORDER_TXNDATE());
                        hed.setFINVRHED_IS_ACTIVE("1");
                        hed.setFINVRHED_IS_SYNCED("0");
                        hed.setFINVRHED_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                        if (activity.selectedDebtor != null) {
                            hed.setFINVRHED_DEBCODE(activity.selectedDebtor.getCusCode());
                        }

                        hed.setFINVRHED_LOCCODE(new SalRepController(getActivity()).getCurrentLocCode());
                        hed.setFINVRHED_ROUTE_CODE(new SharedPref(getActivity()).getGlobalVal("KeyRouteCode"));
                        hed.setFINVRHED_COSTCODE("");
                        hed.setFINVRHED_ORD_REFNO(RetOrderRefNo);

                        activity.selectedReturnHed = hed;

                        returnHedList.add(activity.selectedReturnHed);
                        if (new SalesReturnController(getActivity()).createOrUpdateInvRHed(returnHedList) > 0) {
//                            Log.v("START DET SAVE>>>>",">>>>>>");
                            seqno++;
                            ReturnDet.setFINVRDET_ID(index_id + "");
                            ReturnDet.setFINVRDET_SEQNO(seqno + "");
                            ReturnDet.setFINVRDET_COST_PRICE(lblPrice.getText().toString());
                            ReturnDet.setFINVRDET_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_T_SELL_PRICE(""+price);
                            ReturnDet.setFINVRDET_DIS_AMT(editTotDisc.getText().toString());
                            ReturnDet.setFINVRDET_AMT(
                                    String.format("%.2f", Double.parseDouble(txtQty.getText().toString())
                                            * changedPrice));

                            //  ReturnDet.setFINVRDET_TAX_AMT(TaxedAmt);
                            ReturnDet.setFINVRDET_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_BAL_QTY(totPieces + "");
                            ReturnDet.setFINVRDET_RETURN_REASON(lblReason.getText().toString());
                            ReturnDet.setFINVRDET_RETURN_REASON_CODE(new ReasonController(getActivity()).getReaCodeByName(lblReason.getText().toString()));
                            ReturnDet.setFINVRDET_REFNO(RetRefNo);
                            ReturnDet.setFINVRDET_ITEMCODE(selectedItem.getFITEM_ITEM_CODE());
                            ReturnDet.setFINVRDET_PRILCODE("");
                            ReturnDet.setFINVRDET_IS_ACTIVE("1");
                            ReturnDet.setFINVRDET_TAXCOMCODE(
                                    new ItemController(getActivity()).getTaxComCodeByItemCode(selectedItem.getFITEM_ITEM_CODE()));
                            ReturnDet.setFINVRDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            ReturnDet.setFINVRDET_TXN_TYPE("SR");
                            ReturnDet.setFINVRDET_RETURN_TYPE(returnType.getSelectedItem().toString().split("-")[0]);
                            ReturnDet.setFINVRDET_CHANGED_PRICE(""+changedPrice);

                            ReturnList.add(ReturnDet);
                            new SalesReturnDetController(getActivity()).createOrUpdateInvRDet(ReturnList);

                            if (bAdd.getText().equals("EDIT"))
                                Toast.makeText(getActivity(), "Edited successfully !", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity(), "Added successfully !", Toast.LENGTH_LONG).show();
                            FetchData();
                            clearTextFields();
                            lblReason.setText("");

                        }
                    }else{
                        Toast.makeText(getActivity(), "Please add quantities ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Please fill details ", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void deleteReturnDialog(final Context context, String title, final String itemCode,final String refNo) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                int count = new SalesReturnDetController(context).mDeleteRetDet(itemCode,refNo);

                if (count > 0)
                {
                    Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_LONG).show();
                    clearTextFields();
                    FetchData();
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void FetchData()
    {
        String orRefNo = new OrderController(getActivity()).getActiveRefNoFromOrders();
        String activeRetRefNo = new SalesReturnController(getActivity()).getActiveInnerReturnRefNoByOrderRefNo(orRefNo);

        if (activeRetRefNo.equals(""))
        {
            RetRefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.salRet));
        }
        else
        {
            RetRefNo = activeRetRefNo;
        }

        Log.d("ORDER_INNER_RETURN", "RET_REF_NO_IS:" + RetRefNo);

        try {
            lv_return_det.setAdapter(null);
            returnList = new SalesReturnDetController(getActivity()).getAllInvRDetForOrders(RetRefNo);
            lv_return_det.setAdapter(new SalesReturnDetailsAdapter(getActivity(), returnList));

        } catch (NullPointerException e) {
            Log.v(" Error", e.toString());
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void clearTextFields() {
        values = 0.0;
        index_id = 0;
        totPieces = 0;
        lblItemName.setText("");
        txtQty.setText("0");
        txtQty.clearFocus();
        lblNou.setText("0");
        lblPrice.setText("0.00");
        editTotDisc.setText("0.00");
        bAdd.setText("ADD");
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void prodcutDetailsDialogbox(ArrayList<Item> listParam) {

        list = listParam;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_details_search_item);

        final SearchView search = (SearchView) dialog.findViewById(R.id.et_search);
        final ListView productList = (ListView) dialog.findViewById(R.id.lv_product_list);

        //clearTextFields();
        dialog.setCancelable(true);
        productList.clearTextFilter();

        //list = new ItemsDS(getActivity()).getAllItem("", "txntype ='21'", RefNo, new SalRepDS(getActivity()).getCurrentLocCode(),activity.selectedDebtor.getFDEBTOR_PRILLCODE());
        list = new ItemController(getActivity()).getAllItemForSalesReturn("","","",new SalRepController(getActivity()).getCurrentLocCode().trim(),mSharedPref.getSelectedDebtorPrilCode());

        productList.setAdapter(new ProductAdapter(getActivity(), list));

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedItem = list.get(position);
//                brandDisPer = new DebItemPriDS(getActivity()).getBrandDiscount(selectedItem.getFITEM_BRANDCODE(),
//                        activity.selectedRetDebtor.getFDEBTOR_CODE());
                lblItemName.setText(selectedItem.getFITEM_ITEM_NAME());
                Log.v("ITEM NAME",selectedItem.getFITEM_ITEM_NAME().toString());
                //lblNou.setText(selectedItem.getFITEM_NOUCASE());
                lblPrice.setText(selectedItem.getFITEM_AVGPRICE());
                price = Double.parseDouble(selectedItem.getFITEM_AVGPRICE());
                changedPrice = price;
                // minPrice = Double.parseDouble(selectedItem.getFITEM);
                // maxPrice = Double.parseDouble(selectedItem.getFITEM_MAX_PRICE());
                // Log.v("ITEM PRICE",selectedItem.getFITEM_PRICE().toString());
//                lblPrice.setText(new ItemPriDS(getActivity()).getProductPriceByCode(selectedItem.getFITEM_ITEM_CODE(),
//                activity.selectedRetDebtor.getFDEBTOR_PRILLCODE()));
                iQoh = Double.parseDouble(selectedItem.getFITEM_QOH());
                txtQty.requestFocus();
                txtQty.selectAll();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog.dismiss();

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                list.clear();
               // list = new ItemController(getActivity()).getAllItem(newText, "TxnType ='SR'", RetRefNo, new SalRepController(getActivity()).getCurrentLocCode(),activity.selectedDebtor.getCusPrilCode());
                //2019-10-10 because loccode and prilcode hardcode for swadeshi
                list = new ItemController(getActivity()).getAllItem(newText, "TxnType ='SR'", RetRefNo, "MS","WSP001");
                productList.clearTextFilter();
                productList.setAdapter(new ProductAdapter(getActivity(), list));
                return false;
            }
        });
        dialog.show();
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void reasonsDialogbox() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.return_reason_item);
        final ListView reasonListView = (ListView) dialog.findViewById(R.id.lv_reason_list);
        dialog.setCancelable(true);
        reasonListView.clearTextFilter();

        reasonList = new ReasonController(getActivity()).getAllReasons();

        reasonListView.setAdapter(new ReturnReasonAdapter(getActivity(), reasonList));

        reasonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedReason = reasonList.get(position);
                lblReason.setText(selectedReason.getFREASON_NAME());
                lblItemName.requestFocus();
                dialog.dismiss();

            }
        });

        dialog.show();

    }
    public void mRefreshData() {
        try {
//            amount = Double.parseDouble(txtQty.getText().toString())
//                    * Double.parseDouble(lblPrice.getText().toString());
            FInvRHed hed = new FInvRHed();
            ArrayList<FInvRHed> returnHedList = new ArrayList<FInvRHed>();
            hed.setFINVRHED_REFNO(RetRefNo);
            hed.setFINVRHED_MANUREF(activity.selectedPreHed.getORDER_MANUREF());
            hed.setFINVRHED_REMARKS(activity.selectedPreHed.getORDER_REMARKS());
            hed.setFINVRHED_ADD_USER(new SalRepController(getActivity()).getCurrentRepCode());
            hed.setFINVRHED_ADD_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            hed.setFINVRHED_ADD_MACH(localSP.getString("MAC_Address", "No MAC Address").toString());
            hed.setFINVRHED_TXNTYPE("42");
            hed.setFINVRHED_TXN_DATE(activity.selectedPreHed.getORDER_TXNDATE());
            hed.setFINVRHED_IS_ACTIVE("1");
            hed.setFINVRHED_IS_SYNCED("0");

            if (activity.selectedRetDebtor != null) {
                hed.setFINVRHED_DEBCODE(activity.selectedRetDebtor.getCusCode());
                // hed.setFINVRHED_TAX_REG(activity.selectedRetDebtor.getFDEBTOR_TAX_REG());
            }

            hed.setFINVRHED_LOCCODE(new SalRepController(getActivity()).getCurrentLocCode());
            hed.setFINVRHED_ROUTE_CODE(new SharedPref(getActivity()).getGlobalVal("KeyRouteCode"));
            hed.setFINVRHED_COSTCODE("");

            activity.selectedReturnHed = hed;
            //SharedPreferencesClass.setLocalSharedPreference(activity, "Return_Start_Time", currentTime());

            returnHedList.add(activity.selectedReturnHed);
//                        Log.v("RETURN HED>>>>",activity.selectedInvHed.toString());
            new SalesReturnController(getActivity()).createOrUpdateInvRHed(returnHedList );
            lv_return_det.setAdapter(null);
            returnList = new SalesReturnDetController(getActivity()).getAllInvRDetForOrders(activity.selectedReturnHed.getFINVRHED_REFNO());
            lv_return_det.setAdapter(new SalesReturnDetailsAdapter(getActivity(), returnList));
            lblPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomKeypadDialogPrice keypadPrice = new CustomKeypadDialogPrice(getActivity(), true, new CustomKeypadDialogPrice.IOnOkClickListener() {
                        @Override
                        public void okClicked(double value) {
                            //price cannot be changed less than gross profit
                            //changedPrice = price;
                            //validation removed from return 2019/04/01 - said menaka
                            // if(minPrice <=value && value <= maxPrice) {
                            //  save changed price
                            new SalesReturnDetController(getActivity()).updateProductPrice(selectedItem.getFITEM_ITEM_CODE(), String.valueOf(price));
                            //  value should be set for another variable in preProduct
                            //  preProduct.setPREPRODUCT_PRICE(String.valueOf(value));
                            changedPrice = value;
                            lblPrice.setText(""+changedPrice);
//                            }else{
//                                //changedPrice = price;
//                                Toast.makeText(getActivity(),"Price cannot be change..",Toast.LENGTH_LONG).show();
//                            }
                        }
                    });
                    keypadPrice.show();

                    keypadPrice.setHeader("CHANGE PRICE");
//                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
                    keypadPrice.loadValue(changedPrice);
                }
            });
//            amount = Double.parseDouble(txtQty.getText().toString())
//                    * changedPrice;
//            if(changedPrice != price){
//                new FInvRDetDS(getActivity()).updateProductPrice(selectedItem.getFITEM_ITEM_CODE(),""+changedPrice);
//            }
        } catch (NullPointerException e) {
            Log.v(" Error", e.toString());
        }
    }

    //    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//
    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    //
//    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//
//
//
//    //------------------------------------------------------------------------------------------------------------------------------------
    public class LoardingItemsFromDB extends AsyncTask<Object, Object, ArrayList<Item>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetch Data Please Wait.");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<Item> doInBackground(Object... objects) {

            itemList = null;
            // itemList = new ItemsDS(getActivity()).getAllItem("", "TxnType ='SR'", RefNo, new SalRepController(getActivity()).getCurrentLocCode(),SharedPref.getInstance(getActivity()).getSelectedDebtorPrilCode());
            list = new ItemController(getActivity()).getAllItemForSalesReturn("","","",new SalRepController(getActivity()).getCurrentLocCode().trim(),mSharedPref.getSelectedDebtorPrilCode());

            return itemList;
        }


        @Override
        protected void onPostExecute(ArrayList<Item> products) {
            super.onPostExecute(products);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }

            prodcutDetailsDialogbox(itemList);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_INNER_RETURN"));
    }

    private class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            // InnerReturnDetails.this.
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            preSalesResponseListener = (PreSalesResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onButtonPressed");
        }
    }
}
