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
import com.datamation.swdsfa.helpers.NetworkFunctions;
import com.datamation.swdsfa.helpers.UploadTaskListener;
import com.datamation.swdsfa.model.Attendance;
import com.datamation.swdsfa.model.SalRep;
import com.datamation.swdsfa.model.apimodel.TaskType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadFirebaseTokenKey extends AsyncTask<ArrayList<SalRep>, Integer, ArrayList<SalRep>>  {
    Context context;
    public static final String SETTINGS = "SETTINGS";
    TaskType taskType;
    ArrayList<SalRep> fbsalRepList = new ArrayList<>();
    UploadTaskListener taskListener;
    NetworkFunctions networkFunctions;
    private Handler mHandler;
    ProgressDialog pDialog;
    public static SharedPreferences localSP;

    public UploadFirebaseTokenKey(Context context, UploadTaskListener taskListener,ArrayList<SalRep> salRepList, TaskType taskType) {
        this.context = context;
        this.taskType = taskType;
        this.taskListener = taskListener;
        fbsalRepList.addAll(salRepList);
        mHandler = new Handler(Looper.getMainLooper());
        localSP = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE + Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(ArrayList<SalRep> salRep) {
        super.onPostExecute(salRep);
    }

    @Override
    protected ArrayList<SalRep> doInBackground(ArrayList<SalRep>... arrayLists) {
        networkFunctions = new NetworkFunctions(context);

        final String sp_url = localSP.getString("URL", "").toString();
        String URL = "http://" + sp_url;
        Log.v("## Json ##", URL.toString());

        try
        {
            for(final SalRep f: fbsalRepList)
            {
                try
                {
                    String content_type = "application/json";
                    ApiInterface apiInterface = ApiCllient.getClient(context).create(ApiInterface.class);
                    JsonParser jsonParser = new JsonParser();
                    String firebaseJson = new Gson().toJson(f);
                    JsonObject objectFromString = jsonParser.parse(firebaseJson).getAsJsonObject();
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(objectFromString);
                    Call<String> resultCall = apiInterface.uploadfTokenID(jsonArray, content_type);

                    resultCall.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            int status = response.code();

                            String resmsg = ""+response.body().toString();
                            if (status == 200 && !resmsg.equals("") && !resmsg.equals(null)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"Firebase registerID upload success..!" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                                 }
                            else
                            {
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context,"Firebase registerID Failed..!" , Toast.LENGTH_SHORT).show();
//                                    }
//                                });
                                }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Toast.makeText(context, "Error response firebase token" + t.toString(), Toast.LENGTH_SHORT).show();
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
        Log.v(">>8>>", "Upload new firebase records finish");
        return fbsalRepList;
    }
}
