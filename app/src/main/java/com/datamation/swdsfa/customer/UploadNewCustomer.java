package com.datamation.swdsfa.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.CustomerController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.Order;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.google.gson.Gson;
import com.datamation.swdsfa.controller.NewCustomerController;
import com.datamation.swdsfa.model.NewCustomer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadNewCustomer extends AsyncTask<ArrayList<NewCustomer>, Integer, ArrayList<NewCustomer>> {

    Context context;
    public static final String SETTINGS = "SETTINGS";
    private Handler mHandler;
    UploadTaskListener taskListener;
    TaskType taskType;
    ProgressDialog pDialog;
    int totalRecords;
    NetworkFunctions networkFunctions;
    ArrayList<NewCustomer> fNewCustomerslist = new ArrayList<>();
    public static SharedPreferences localSP;
    private String customerID;
    NewCustomerController newCustomerDS;

    public UploadNewCustomer(Context context, UploadTaskListener taskListener, ArrayList<NewCustomer> ordList, TaskType taskType) {
        mHandler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
        fNewCustomerslist.addAll(ordList);
        newCustomerDS = new NewCustomerController(context);

//        localSP = context.getSharedPreferences(SharedPreferencesClass.SETTINGS, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Uploading New Customers...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<NewCustomer> fNewCustomers) {
        super.onPostExecute(fNewCustomers);
        List<String> list = new ArrayList<>();
        pDialog.dismiss();
        taskListener.onTaskCompleted(taskType,list);
    }

    @Override
    protected ArrayList<NewCustomer> doInBackground(ArrayList<NewCustomer>... arrayLists) {

        int recordCount = 0;
        publishProgress(recordCount);
        networkFunctions = new NetworkFunctions(context);

        // ArrayList<fNewCustomer> fNewCustomersList = arrayLists[0];
        totalRecords = fNewCustomerslist.size();

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        Log.v("## Json ##", URL.toString());

        //create json list
        try
        {
            for(final NewCustomer nc: fNewCustomerslist)
            {
                try
                {
                    String content_type = "application/json";
                    ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                    JsonParser jsonParser = new JsonParser();
                    String NCustomerJson = new Gson().toJson(nc);
                    JsonObject objectFromString = jsonParser.parse(NCustomerJson).getAsJsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(objectFromString);
                    Call<String> resultCall = apiInterface.uploadNCustomer(jsonArray, content_type);

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
                                        nc.setIS_SYNCED("1");
                                        new NewCustomerController(context).updateIsSynced(nc.getCUSTOMER_ID(),"1");
                                        Toast.makeText(context,"New Customer uploaded Successfully" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                              }
                            else
                            {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        nc.setIS_SYNCED("0");
                                        new NewCustomerController(context).updateIsSynced(nc.getCUSTOMER_ID(), "0");
                                        Toast.makeText(context,"New Customer uploaded Failed" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                               }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response new customer " + t.toString(), Toast.LENGTH_SHORT).show();
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
        Log.v(">>8>>", "Upload New Customer execute finish");//
        return fNewCustomerslist;
    }
}





