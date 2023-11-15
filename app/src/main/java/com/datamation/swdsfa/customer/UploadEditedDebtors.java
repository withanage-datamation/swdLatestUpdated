package com.datamation.swdsfa.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.Debtor;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadEditedDebtors extends AsyncTask<ArrayList<Debtor>, Integer, ArrayList<Debtor>> {

    Context context;
    public static final String SETTINGS = "SETTINGS";

    ArrayList<Debtor> fDebtorList = new ArrayList<>();
    int totalRecords;
    private String debtorCode;
    TaskType taskType;
    UploadTaskListener taskListener;
    NetworkFunctions networkFunctions;
    private Handler mHandler;
    ProgressDialog pDialog;
    public static SharedPreferences localSP;
    CustomerController customerController;

    public UploadEditedDebtors(Context context, UploadTaskListener taskListener, ArrayList<Debtor> ordList, TaskType taskType) {
        this.context = context;
        this.taskListener = taskListener;
        fDebtorList.addAll(ordList);
        customerController = new CustomerController(context);
        mHandler = new Handler(Looper.getMainLooper());
        this.taskType = taskType;
//        localSP = context.getSharedPreferences(SharedPreferencesClass.SETTINGS, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Updating updated dealers...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<Debtor> debtors) {
        ArrayList<String> list = new ArrayList<>();
        super.onPostExecute(debtors);
        pDialog.dismiss();
        taskListener.onTaskCompleted(taskType,list);
    }

    @Override
    protected ArrayList<Debtor> doInBackground(ArrayList<Debtor>... arrayLists) {
        networkFunctions = new NetworkFunctions(context);
        totalRecords = fDebtorList.size();

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        Log.v("## Json ##", URL.toString());

//        for (Debtor debtor : fDebtorList) {
//
//            debtorCode = debtor.getFDEBTOR_CODE();
//            ArrayList<String> jsonList = new ArrayList<>();
//            String jObject = new Gson().toJson(debtor);
//            jsonList.add(jObject);
//
//            try {
//                boolean status = NetworkFunctions.mHttpManager(networkFunctions.syncEditedDebtors(), jsonList.toString());
//                if (status) {
//                    customerController.updateIsSynced(debtorCode,"2");
//                    pDialog.dismiss();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }

        for(final Debtor i: fDebtorList)
        {
            try
            {
                String content_type = "application/json";
                ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                JsonParser jsonParser = new JsonParser();
                String debtorimgJson = new Gson().toJson(i);
                JsonObject objectFromString = jsonParser.parse(debtorimgJson).getAsJsonObject();
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(objectFromString);
                Call<String> resultCall = apiInterface.uploadEditedDebtors(jsonArray, content_type);

                resultCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int status = response.code();
                        int reslength = response.body().toString().trim().length();

                        String resmsg = ""+response.body().toString();
                        if (status == 200 && !resmsg.equals("") && !resmsg.equals(null) && resmsg.equals("200")) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    i.setFDEBTOR_IS_SYNC("1");
                                    new CustomerController(context).updateIsSyncedEditedCus(i.getFDEBTOR_CODE(),"1");
                                    Toast.makeText(context,"Debtor updated Successfully" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    i.setFDEBTOR_IS_SYNC("0");
                                    new CustomerController(context).updateIsSyncedEditedCus(i.getFDEBTOR_CODE(),"0");
                                    Toast.makeText(context,"Debtor updated Failed" , Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Toast.makeText(context, "Error response update debtor" + t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return fDebtorList;
    }
}
