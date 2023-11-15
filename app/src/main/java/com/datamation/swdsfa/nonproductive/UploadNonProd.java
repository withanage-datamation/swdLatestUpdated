package com.datamation.swdsfa.nonproductive;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.datamation.swdsfa.R;
import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.DayNPrdHedController;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.DayNPrdHed;
import com.datamation.swdsfa.model.FInvRHed;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.google.gson.Gson;
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
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadNonProd extends AsyncTask<ArrayList<DayNPrdHed>, Integer, ArrayList<DayNPrdHed>> {

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    ProgressDialog dialog;
    TaskType taskType;
    private Handler mHandler;
    UploadTaskListener taskListener;
    List<String> resultListNonProduct;
    NetworkFunctions networkFunctions;
    int totalRecords;

    public UploadNonProd(Context context, UploadTaskListener taskListener, TaskType taskType) {
        resultListNonProduct = new ArrayList<>();
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
        mHandler = new Handler(Looper.getMainLooper());
        //localSP = context.getSharedPreferences(SETTINGS, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Uploading nonproductive records");
        dialog.show();
    }

    @Override
    protected ArrayList<DayNPrdHed> doInBackground(ArrayList<DayNPrdHed>... params) {

        int recordCount = 0;
        publishProgress(recordCount);
        networkFunctions = new NetworkFunctions(context);

        final ArrayList<DayNPrdHed> RCSList = params[0];
        totalRecords = RCSList.size();

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        Log.v("## Json ##", URL);

//        for (DayNPrdHed c : RCSList) {
//
//            try {
//                List<String> List = new ArrayList<String>();
//                String sJsonHed = new Gson().toJson(c);
//                List.add(sJsonHed);
//
//                Log.d("&", "doInBackground: "+networkFunctions.syncNonProductive());
//                boolean bStatus = NetworkFunctions.mHttpManager(networkFunctions.syncNonProductive(),List.toString());
//
//                if (bStatus) {
//                    c.setNONPRDHED_IS_SYNCED("1");
//                } else {
//                    c.setNONPRDHED_IS_SYNCED("0");
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            ++recordCount;
//            publishProgress(recordCount);
//        }
        try
        {
            for(final DayNPrdHed np: RCSList)
            {
                try
                {
                    String content_type = "application/json";
                    ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                    JsonParser jsonParser = new JsonParser();
                    String nonPJson = new Gson().toJson(np);
                    JsonObject objectFromString = jsonParser.parse(nonPJson).getAsJsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(objectFromString);
                    Call<String> resultCall = apiInterface.uploadNonProd(jsonArray, content_type);

                    resultCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            int status = response.code();
                            Log.d(">>>response code",">>>res  "+status);
                            Log.d(">>>response message", ">>>res  " +response.message());
                            Log.d(">>>response body", ">>>res  " +response.body());
                            int reslength = response.body().toString().trim().length();
                            Log.d(">>>resLength", ">>>resLength " +reslength);
                            // Log.d(">>>resrefno", ">>>res " + response.body().toString().trim().substring(3,reslength));

                            String resmsg = ""+response.body().toString();
                            if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                       // resultListNonProduct.add(np.getNONPRDHED_REFNO()+ "--->SUCCESS");
                                           addRefNoResults_Non(np.getNONPRDHED_REFNO() + " --> Success\n",RCSList.size());
                                        np.setNONPRDHED_IS_SYNCED("1");
                                        new DayNPrdHedController(context).updateIsSynced(np);
                                      //  Toast.makeText(context,np.getNONPRDHED_REFNO()+"-Non-productive uploded Successfully" , Toast.LENGTH_SHORT).show();

                                    }
                                });
                                }
                            else
                            {
                            //    addRefNoResults_Non(np.getNONPRDHED_REFNO() + " --> Failed\n",RCSList.size());
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                      //  resultListNonProduct.add(np.getNONPRDHED_REFNO()+ "--->FAILED");
                                        addRefNoResults_Non(np.getNONPRDHED_REFNO()+ "--->FAILED",RCSList.size());
                                        np.setNONPRDHED_IS_SYNCED("0");
                                        new DayNPrdHedController(context).updateIsSynced(np);
                                      //  Toast.makeText(context,np.getNONPRDHED_REFNO()+"-Non-productive uploded Failed" , Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response nonproductive" + t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                ++recordCount;
                publishProgress(recordCount);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return RCSList;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. Non Prod Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<DayNPrdHed> NonPrdList) {

        super.onPostExecute(NonPrdList);
        dialog.dismiss();
        taskListener.onTaskCompleted(taskType,resultListNonProduct);
//        List<String> list = new ArrayList<>();
//
//        if (NonPrdList.size() > 0) {
//            list.add("\nNONPRODUCTIVE");
//            list.add("------------------------------------\n");
//        }
//        int i = 1;
//        for (DayNPrdHed c : NonPrdList) {
//
//            new DayNPrdHedController(context).updateIsSynced(c);
//
//            if (c.getNONPRDHED_IS_SYNCED().equals("1")) {
//                list.add(i + ". " + c.getNONPRDHED_REFNO()+ " --> Success\n");
//            } else {
//                list.add(i + ". " + c.getNONPRDHED_REFNO() + " --> Failed\n");
//            }
//            i++;
//        }
//
//        ;
        //taskListener.onTaskCompleted(list);
    }
    private void addRefNoResults_Non(String ref, int count) {
        resultListNonProduct.add(ref);
        Log.d(">>>msg",">>>"+ref+">>>"+count);
        if(count == resultListNonProduct.size()) {
            mUploadResult(resultListNonProduct);
        }
    }

    public void mUploadResult(List<String> messages) {
        String msg = "";
        for (String s : messages) {
            msg += s;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle("Nonproductive Upload Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}
