package com.datamation.swdsfa.presale;

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

import com.datamation.swdsfa.R;
import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.InvHedController;
import com.datamation.swdsfa.controller.OrderController;
import com.datamation.swdsfa.fragment.FragmentTools;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.InvHed;
import com.datamation.swdsfa.model.Order;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.datamation.swdsfa.utils.UtilityContainer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPreSales extends AsyncTask<ArrayList<Order>, Integer, ArrayList<Order>> {



    // Shared Preferences variables
    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    Context context;
    private Handler mHandler;
    ProgressDialog dialog;
    UploadTaskListener taskListener;
    List<String> resultListPreSale;
    TaskType taskType;
    NetworkFunctions networkFunctions;
    int totalRecords;

    public UploadPreSales(Context context, UploadTaskListener taskListener, TaskType taskType) {
        resultListPreSale = new ArrayList<>();
        this.context = context;
        mHandler = new Handler(Looper.getMainLooper());
        this.taskListener = taskListener;
        this.taskType = taskType;
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Uploading order records");
        dialog.show();
    }

    @Override
    protected ArrayList<Order> doInBackground(ArrayList<Order>... params) {
        int recordCount = 0;
        publishProgress(recordCount);
        networkFunctions = new NetworkFunctions(context);
        final ArrayList<Order> RCSList = params[0];
        totalRecords = RCSList.size();
        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        for (final Order c : RCSList)
        {
            try {
                String content_type = "application/json";
                ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                JsonParser jsonParser = new JsonParser();
                String orderJson = new Gson().toJson(c);
                JsonObject objectFromString = jsonParser.parse(orderJson).getAsJsonObject();
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(objectFromString);
                Log.d(">>>order json", ">>>json " + jsonArray.toString());
                Call<String> resultCall = apiInterface.uploadOrder(jsonArray, content_type);
                resultCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int status = response.code();
                        Log.d(">>>response code", ">>>res " + status);
                        Log.d(">>>response message", ">>>res " + response.message());
                        Log.d(">>>response body", ">>>res " + response.body().toString());
                        int resLength = response.body().toString().trim().length();
                        String resmsg = ""+response.body().toString();
                        if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // resultListNonProduct.add(np.getNONPRDHED_REFNO()+ "--->SUCCESS");
                                    //    addRefNoResults_Non(np.getNONPRDHED_REFNO() + " --> Success\n",RCSList.size());
                                  //  Log.d( ">>response"+status,""+c.getORDER_REFNO() );
                                    c.setORDER_IS_SYNCED("1");
                                    addRefNoResults(c.getORDER_REFNO() +" --> Success\n",RCSList.size());
                                    // new OrderController(context).updateIsSynced(c);
                                    new OrderController(context).updateIsSynced(c.getORDER_REFNO(),"1");
                                    //  Toast.makeText(context,np.getNONPRDHED_REFNO()+"-Non-productive uploded Successfully" , Toast.LENGTH_SHORT).show();
                                }
                            });
                            //addRefNoResults(c.getORDER_REFNO() +" --> Success\n",RCSList.size());

                          //  Toast.makeText(context, c.getORDER_REFNO()+" - Order uploded Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d( ">>response"+status,""+c.getORDER_REFNO() );
                            c.setORDER_IS_SYNCED("0");
                            new OrderController(context).updateIsSynced(c.getORDER_REFNO(),"0");
                            addRefNoResults(c.getORDER_REFNO() +" --> Failed\n",RCSList.size());
                         //   Toast.makeText(context, c.getORDER_REFNO()+" - Order uplod Failed", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(context, "Error response "+t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            ++recordCount;
            publishProgress(recordCount);
        }
      //  taskListener.onTaskCompleted(taskType,resultListPreSale);

        return RCSList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setMessage("Uploading.. PreSale Record " + values[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(ArrayList<Order> RCSList) {

        super.onPostExecute(RCSList);
        dialog.dismiss();
        taskListener.onTaskCompleted(taskType,resultListPreSale);
    }
    private void addRefNoResults(String ref, int count) {
        resultListPreSale.add(ref);
        if(count == resultListPreSale.size()) {
            mUploadResult(resultListPreSale);
        }
    }

    public void mUploadResult(List<String> messages) {
        String msg = "";
        for (String s : messages) {
            msg += s;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setTitle("Upload Order Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }





    ///////////////////////////////////////testing things ////////////////////////////////
    //
//                            try { // upload pre sale order
//
//                                OrderController orderHed = new OrderController(getActivity());
//                                final ArrayList<Order> ordHedList = orderHed.getAllUnSyncOrdHed();
////                    /* If records available for upload then */
//                                if (ordHedList.size() <= 0)
//                                {
//                                    Toast.makeText(getActivity(), "No Pre Sale Records to upload !", Toast.LENGTH_LONG).show();
//                                }
//                                else {
//                                    try {
//                                      //  new UploadPreSales(getActivity(), FragmentTools.this).execute(ordHedList);
//                                        for (final Order c : ordHedList) {
//                                            try {
//                                                String content_type = "application/json";
//                                                ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
//                                                JsonParser jsonParser = new JsonParser();
//                                                String orderJson = new Gson().toJson(c);
//                                                JsonObject objectFromString = jsonParser.parse(orderJson).getAsJsonObject();
//                                                JsonArray jsonArray = new JsonArray();
//                                                jsonArray.add(objectFromString);
//                                                Call<String> resultCall = apiInterface.uploadOrder(jsonArray, content_type);
//
//                                                resultCall.enqueue(new Callback<String>() {
//                                                    @Override
//                                                    public void onResponse(Call<String> call, Response<String> response) {
//
//                                                        int status = response.code();
//                                                        Log.d(">>>response code", ">>>res " + status);
//                                                        Log.d(">>>response message", ">>>res " + response.message());
//                                                        Log.d(">>>response body", ">>>res " + response.body().toString());
//                                                        int resLength = response.body().toString().trim().length();
//                                                        Log.d(">>>resLength", ">>>resLength " +resLength);
//                                                      //  Log.d(">>>resrefno", ">>>res " + response.body().toString().trim().substring(3,resLength));
//
//
//                                                         if (status == 200) {
////                                                            if(resLength>0){
////                                                                addRefNoResults(response.body().toString().trim().substring(3,resLength)+" --> Success\n",ordHedList.size());
////                                                            }
//                                                            addRefNoResults(c.getORDER_REFNO() +" --> Success\n",ordHedList.size());
////
//                                                            c.setORDER_IS_SYNCED("1");
//                                                            new OrderController(context).updateIsSynced(c);
//                                                           // Log.d(">>>response update", ">>>res " + new OrderController(context).updateIsSynced(c));
//                                                            Toast.makeText(context, "Order uploded Successfully", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            c.setORDER_IS_SYNCED("0");
//                                                            //addRefNoResults(response.body().toString().trim().substring(3,resLength)+" --> Fail\n",ordHedList.size());
//                                                            addRefNoResults(c.getORDER_REFNO() +" --> Success\n",ordHedList.size());
////                                                          +" --> Fail\n",ordHedList.size());
//                                                            Toast.makeText(context, "Order uplod Failed", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                       // Log.d(">>>response upload", ">>>res " + response.message().toString());
//
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Call<String> call, Throwable t) {
//
//                                                        Toast.makeText(context, "Error response " + t.toString(), Toast.LENGTH_SHORT).show();
//
//                                                    }
//                                                });
//
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }
//
/////////////////////////******************************************************////////////////////////////////////////////////////////////
//
//
//
//
//
//                                        //new presaleResult(getActivity(), FragmentTools.this).execute(ordHedList);
////                                       ApiInterface apiInterface = ApiCllient.getClient(getActivity()).create(ApiInterface.class);
////                                        for(final Order c : ordHedList) {
////                                             JsonParser jsonParser = new JsonParser();
////                                            String orderJson = new Gson().toJson(c);
////                                             JsonObject objectFromString = jsonParser.parse(orderJson).getAsJsonObject();
////                                            //String orderJson = new Gson().toJson(ordHedList);
////                                           // String orderJson = new Gson().toJson(c);
////                                            String content_type = "application/json";
////                                           // JsonParser jsonParser = new JsonParser();
////                                           // JsonArray arrayFromJsonObj = jsonParser.parse(objectFromString.toString()).getAsJsonArray();
////                                            JsonArray jsonArray = new JsonArray();
////                                            jsonArray.add(objectFromString);
////
////                                            Call<String> resultCall = apiInterface.uploadOrder(jsonArray, content_type);
////
////                                            resultCall.enqueue(new Callback<String>() {
////                                                @Override
////                                                public void onResponse(Call<String> call, Response<String> response) {
//////
////                                                    int status = response.code();
////                                                    Log.d(">>>response code", ">>>res " + status);
////                                                    if(status == 200){
////                                                        c.setORDER_IS_SYNCED("1");
////                                                    }else{
////                                                        c.setORDER_IS_SYNCED("0");
////                                                    }
////                                                    new OrderController(context).updateIsSynced(c);
////
////                                                    if (c.getORDER_IS_SYNCED().equals("1")) {
////                                                        Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                    } else {
////                                                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
////                                                    }
////                                                    Log.d(">>>response upload", ">>>res " + response.message().toString());
////                                                    if (response != null) {
////                                                        Log.d(">>>response upload", ">>>" + response.message().toString());
////                                                        Toast.makeText(getActivity(), "Get response "+ response.message().toString(), Toast.LENGTH_SHORT).show();
////                                                    }
////                                                }
////
////                                                @Override
////                                                public void onFailure(Call<String> call, Throwable t) {
////                                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
////
////                                                }
////                                            });
////                                        }//end order list loop
////
////
////////////////////////////////////////////////////////////////////////////////////
////                                        resultCall.enqueue(new Callback<ResponseBody>() {
////                                            @Override
////                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////                                             int status = response.code();
////                                                Log.d(">>>response code",">>>res "+status);
////
////                                                Log.d(">>>response upload",">>>res "+response.message().toString());
////                                                if(response != null) {
////                                                    Log.d(">>>response upload",">>>"+response.message().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
////
////                                            }
////                                        });
//
////                                        resultCall.enqueue(new Callback<List<Order>>() {
////                                            @Override
////                                            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
////                                                int status = response.code();
////                                                Log.d(">>>response upload",">>>"+response.message().toString());
////                                                if(response != null) {
////                                                    Log.d(">>>response upload",">>>"+response.message().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<List<Order>> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
//////
////                                            }
////                                        });
//
////                                        resultCall.enqueue(new Callback<String>() {
////                                            @Override
////                                            public void onResponse(Call<String> call, Response<String> response) {
////                                                int status = response.code();
////                                                Log.d(">>>response upload",">>>"+response.message().toString());
////                                                if(response != null) {
////                                                    Log.d(">>>response upload",">>>"+response.message().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<String> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
////
////                                            }
////                                        });
//
////                                        resultCall.enqueue(new Callback<JSONArray>() {
////                                            @Override
////                                            public void onResponse(Call<JSONArray> call, Response<JSONArray> response) {
////                                                Log.d(">>>response upload",">>>"+response.message().toString());
////                                                if(response != null) {
////                                                    Log.d(">>>response upload",">>>"+response.message().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<JSONArray> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
////                                            }
////                                        });
//
////                                        resultCall.enqueue(new Callback<ArrayList<Order>>() {
////                                            @Override
////                                            public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
////                                                if(response.body() != null) {
////                                                    Log.d(">>>response upload",">>>"+response.body().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
////
////                                            }
////                                        });
//
////                                        resultCall.enqueue(new Callback<String>() {
////                                            @Override
////                                            public void onResponse(Call<String> call, Response<String> response) {
////                                                if(response.body() != null) {
////                                                    Log.d(">>>response upload",">>>"+response.body().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<String> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
//////////
////                                            }
////                                        });
////                                        resultCall.enqueue(new Callback<ResponseBody>() {
////                                            @Override
////                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////                                                if(response.body() != null) {
////                                                    Log.d(">>>response upload",">>>"+response.body().toString());
////                                                    Toast.makeText(getActivity(), "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
////                                                }
////                                            }
////
////                                            @Override
////                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                                                Toast.makeText(getActivity(), "Error" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
////////
////                                            }
////                                        });
//                                    } catch (Exception e) {
//                                       // errors.add(e.toString());
//                                        throw e;
//                                    }
//                                    Log.v(">>8>>", "UploadPreSales execute finish");
//                                    // new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.NumVal));
//                                }
//
//                            } catch (Exception e) {
//                                Log.v("Exception in sync order", e.toString());
//                            }






}
