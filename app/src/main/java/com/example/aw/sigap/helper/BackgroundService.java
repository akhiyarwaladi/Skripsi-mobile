package com.example.aw.sigap.helper;

/**
 * Created by AW on 7/24/2017.
 */

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.DashboardActivity;
import com.example.aw.sigap.activity.DetailActivity;
import com.example.aw.sigap.activity.EmptyData;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.model.AllData;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    String apiKey, id_alat, device, ukk, hpsp, durtime;
    private String TAG = DashboardActivity.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            //Toast.makeText(context, "Background service running", Toast.LENGTH_SHORT).show();
            System.out.println("The background is running");
            getData();
            stopSelf();
        }
    };

    public void getData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_DATA+"/"+"596b4265e19ddd256001d344", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        //Toast.makeText(DetailActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("dataset");

                        JSONObject dataObj = (JSONObject) data.get(0);
                        JSONObject setObj = new JSONObject(dataObj.getString("data"));
                        JSONObject setObj2 = new JSONObject(dataObj.getString("sensornode"));
                        Log.i("dataDapat",""+dataObj);


                        String hpc = setObj.getString("waterlevel");
                        String humidity = setObj.getString("humidity");
                        String temperature = setObj.getString("temperature");
                        String status = setObj2.getString("status");

                        if(dataObj.has("uk")) ukk = dataObj.getString("uk");
                        else ukk = "1";
                        if(dataObj.has("setPoint")) hpsp = dataObj.getString("setPoint");
                        else hpsp = "5";
                        if(dataObj.has("opTime")) durtime = dataObj.getString("opTime");
                        else durtime = "60";
                        String createdAt = dataObj.getString("created_at");

                        DateTime dateTime = DateTime.parse(createdAt);

                        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");

                        String strDateOnly = fmt.print(dateTime.plusHours(7));



                        Log.d("dateonlybefore", createdAt);
                        Log.d("dateonlyafter", strDateOnly);

                        long secondsSinceEpoch = dateTime.getMillis() / 1000;
                        Log.d("haha", Long.toString(secondsSinceEpoch));



                    } else {
                        // error in fetching data
                        //Toast.makeText(DetailActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    //Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    Intent intent3 = new Intent(DetailActivity.this, EmptyData.class);
//                    intent3.putExtra("id_alat", id_alat);
//                    intent3.putExtra("device", device);
//                    startActivity(intent3);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                //Toast.makeText(DetailActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", apiKey);
                headers.put("x-snow-token", "SECRET_API_KEY");

                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}