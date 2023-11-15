package com.datamation.swdsfa.OtherUploads;

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

public class UploadDebtorImges extends AsyncTask<ArrayList<Debtor>, Integer, ArrayList<Debtor>> {

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

    public UploadDebtorImges(Context context, UploadTaskListener taskListener, ArrayList<Debtor> ordList, TaskType taskType) {
        this.context = context;
        mHandler = new Handler(Looper.getMainLooper());
        this.taskListener = taskListener;
        this.taskType = taskType;
        fDebtorList.addAll(ordList);
        customerController = new CustomerController(context);

//        localSP = context.getSharedPreferences(SharedPreferencesClass.SETTINGS, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Uploading dealer images...");
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
        //ArrayList<Debtor> RCSList = arrayLists[0];
        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        Log.v("## Json ##", URL.toString());

        try
        {
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
                    Call<String> resultCall = apiInterface.uploadDebtorImg(jsonArray, content_type);

                    resultCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            int status = response.code();
                            int reslength = response.body().toString().trim().length();

                            String resmsg = ""+response.body().toString();
                            if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        i.setFDEBTOR_IS_SYNC("1");
                                        new CustomerController(context).updateIsSyncedImages(i.getFDEBTOR_CODE(),"1");
                                        Toast.makeText(context,"Debtor Image uploaded Successfully" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                                   }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response image upload" + t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        Log.v(">>8>>", "Upload debtor image execute finish");//
        return fDebtorList;
    }
}
