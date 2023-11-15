package com.datamation.swdsfa.expense;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.DayExpHedController;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.DayExpHed;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadExpenses extends AsyncTask<ArrayList<DayExpHed>, Integer, ArrayList<DayExpHed>> {

    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    private Handler mHandler;
    ProgressDialog dialog;
    TaskType taskType;
    List<String> resultList;
    UploadTaskListener taskListener;
    NetworkFunctions networkFunctions;
    int totalRecords;

    public UploadExpenses(Context context, UploadTaskListener taskListener, TaskType taskType) {
        mHandler = new Handler(Looper.getMainLooper());
        resultList = new ArrayList<>();
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = taskType;
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Uploading expense records");
        dialog.show();
    }

    @Override
    protected ArrayList<DayExpHed> doInBackground(ArrayList<DayExpHed>... params) {

        int recordCount = 0;
        publishProgress(recordCount);
        networkFunctions = new NetworkFunctions(context);
        final ArrayList<DayExpHed> RCSList = params[0];
        totalRecords = RCSList.size();
        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;

        try {
            for (final DayExpHed exp : RCSList) {
                try {
                    String content_type = "application/json";
                    ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                    JsonParser jsonParser = new JsonParser();
                    String expenseJson = new Gson().toJson(exp);
                    JsonObject objectFromString_Ex = jsonParser.parse(expenseJson).getAsJsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(objectFromString_Ex);
                    Call<String> resultCall = apiInterface.uploadExpense(jsonArray, content_type);

                    resultCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            int status = response.code();
//                                                        Log.d(">>>response code",">>>res"+status);
//                                                        Log.d(">>>response message", ">>>res" +response.message());
//                                                        Log.d(">>>response body", ">>>res" +response.body());
//                                                        int reslength = response.body().toString().trim().length();
//                                                        Log.d("response length" , ">>>resLength" +reslength);

                            String resmsg = ""+response.body().toString();
                            if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                // addRefNoResults_exp(exp.getEXP_REFNO() + " --> Success\n",exHedList.size());

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addRefNoResults_exp(exp.getEXP_REFNO()+ "--->Success\n",RCSList.size());
                                        exp.setEXP_IS_SYNCED("1");
                                        new DayExpHedController(context).updateIsSynced(exp);
                                      //  mUploadResult(exp.getEXP_REFNO()+ "--->Success\n");
                                        dialog.setTitle( exp.getEXP_REFNO()+"Expense uploded Successfully");
                                        //Toast.makeText(context, exp.getEXP_REFNO()+"Expense uploded Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                              } else {
                                // addRefNoResults_exp(exp.getEXP_REFNO() + " --> Failed\n",exHedList.size());
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addRefNoResults_exp(exp.getEXP_REFNO()+ "--->Failed\n",RCSList.size());
                                      //  mUploadResult(exp.getEXP_REFNO()+ "--->Failed\n");
                                        dialog.setTitle(exp.getEXP_REFNO()+"Expense uploded Failed");
                                      //  Toast.makeText(context, exp.getEXP_REFNO()+"Expense uploded Failed", Toast.LENGTH_SHORT).show();
                                        }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response expense " + t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw e;
        }
        Log.v(">>8>>", "Upload expense execute finish");//
        ++recordCount;
        publishProgress(recordCount);

       return RCSList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. Expense Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<DayExpHed> RCSList) {
        ArrayList<String> list = new ArrayList<>();
        super.onPostExecute(RCSList);
        dialog.dismiss();
        taskListener.onTaskCompleted(taskType,list);
    }
    private void addRefNoResults_exp(String ref, int count) {
        resultList.add(ref);
        Log.d(">>>msg",">>>"+ref+">>>"+count);
        if(count == resultList.size()) {
            mUploadResult(resultList);
        }
    }

    public void mUploadResult(List<String> messages) {
        String msg = "";
        for (String s : messages) {
            msg += s;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle("Upload Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }
    public void mUploadResult(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Upload Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }
}
