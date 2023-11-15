package com.datamation.swdsfa.OtherUploads;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.datamation.swdsfa.api.ApiCllient;
import com.datamation.swdsfa.api.ApiInterface;
import com.datamation.swdsfa.controller.SalRepController;
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.SharedPref;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.NewCustomer;
import com.datamation.swdsfa.model.SalRep;
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

public class UploadSalRef extends AsyncTask<ArrayList<SalRep>, Integer, ArrayList<SalRep>> {

    Context context;
    public static final String SETTINGS = "SETTINGS";
    UploadTaskListener taskListener;
    TaskType taskType;
    ProgressDialog pDialog;
    int totalRecords;
    List<String> resultList;
    NetworkFunctions networkFunctions;
    ArrayList<SalRep> fSalReplist =  new ArrayList<>();
    public static SharedPreferences localSP;
    private String repcode;
    SalRepController salRepController;

    public UploadSalRef(Context context, UploadTaskListener taskListener, ArrayList<SalRep> repDeatil, TaskType tasktype)
    {
        this.context = context;
        this.taskListener = taskListener;
        this.taskType = tasktype;
        fSalReplist.addAll(repDeatil);
        salRepController = new SalRepController(context);
        resultList= new ArrayList<>();
        localSP = context.getSharedPreferences(SETTINGS,Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Uploading Sales Representative Data.....");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<SalRep> salReps) {
        super.onPostExecute(salReps);
        taskListener.onTaskCompleted(taskType,resultList);
        pDialog.dismiss();
    }

    @Override
    protected ArrayList<SalRep> doInBackground(ArrayList<SalRep>... arrayLists) {

        int recordCount = 0 ;
        publishProgress(recordCount);
        networkFunctions = new NetworkFunctions(context);

        totalRecords = fSalReplist.size();

        final String sp_url = localSP.getString("URL","").toString();
        String URL = "http://" +sp_url;
        Log.v("## Json ##", URL.toString());

        //create json list
//        for(SalRep sList: fSalReplist)
//        {
//           repcode = sList.getRepCode();
//           ArrayList<String> jsonlist = new ArrayList<>();
//           String sJsonHed = new Gson().toJson(sList);
//           jsonlist.add(sJsonHed);
//           Log.v("## json ##" , jsonlist.toString());
//
//            try
//            {
//                boolean bStatus = NetworkFunctions.mHttpManager(networkFunctions.syncEmailUpdatedSalrep(),jsonlist.toString());
//
//                if(bStatus)
//                {
//                   salRepController.updateIsSynced(repcode,"1");
//                    pDialog.dismiss();
//                }
//                else
//                {
//                    salRepController.updateIsSynced(repcode,"0");
//                }
//            }
//            catch (Exception e)
//            {
//                    e.getStackTrace();
//            }
//
//        }

        final ArrayList<SalRep> RCSList = arrayLists[0];
        try
        {
            for(final SalRep s: RCSList)
            {
                try
                {
                    String content_type = "application/json";
                    ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                    JsonParser jsonParser = new JsonParser();
                    String SalRepJson = new Gson().toJson(s);
                    JsonObject objectFromString = jsonParser.parse(SalRepJson).getAsJsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(objectFromString);
                    Call<String> resultCall = apiInterface.uploadRepEmail(jsonArray, content_type);

                    resultCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            int status = response.code();
                            int reslength = response.body().toString().trim().length();

                            String resmsg = ""+response.body().toString();
                            if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                s.setISSYNC("1");
                                Log.d( ">>response"+status,""+s.getRepCode() );
                                new SalRepController(context).updateIsSynced(s.getRepCode(),"1");
                                Toast.makeText(context,"SalRep email uploaded Successfully" , Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                s.setISSYNC("0");
                                Log.d( ">>response"+status,""+s.getRepCode() );
                                Toast.makeText(context,"SalRep email uploaded Failed" , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response email upload" + t.toString(), Toast.LENGTH_SHORT).show();
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
        Log.v(">>8>>", "Upload SalRep email execute finish");//
        return RCSList;
    }

}
