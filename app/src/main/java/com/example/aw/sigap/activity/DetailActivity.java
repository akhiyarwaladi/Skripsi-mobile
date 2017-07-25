package com.example.aw.sigap.activity;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.helper.AlarmReceiver;
import com.example.aw.sigap.helper.ProgressBarAnimation;
import com.example.aw.sigap.model.Alat;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.AllsData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {

    private String TAG = DashboardActivity.class.getSimpleName();
    private final static int INTERVAL = 1000 * 10 * 1; //2 minutes
    private Context context;
    Button btnHistory, btnSettings, btnON, btnOFF;
    Toolbar toolbar;
    String apiKey, id_alat, device, ukk, hpsp, durtime;
    int numkeys;
    ArrayList<String> valList = new ArrayList<String>();
    ArrayList<String> keyList = new ArrayList<String>();
    @Bind(R.id.fb_buddies)
    FlexboxLayout flexboxLayout;
    List<AllData> allDatas;
    List<AllsData> allsDataList;
    private String lastDataDate;

    Handler handler = new Handler();
    private boolean stop = false;
    private boolean isBusy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);
        final Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");
        device = intent.getStringExtra("device");

        allDatas = new ArrayList<AllData>();
        allsDataList = new ArrayList<AllsData>();
        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        Log.d("api", apiKey);


        btnHistory = (Button) findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DataHistoryActivity.class);
                intent.putExtra("id_alat", id_alat);
                intent.putExtra("device", device);
                startActivity(intent);
            }
        });

        btnSettings = (Button) findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(DetailActivity.this, SettingsActivity.class);
                intent2.putExtra("id_alat", id_alat);
                intent2.putExtra("device", device);
                startActivity(intent2);
            }
        });

        btnON = (Button) findViewById(R.id.bON);
        btnON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView header = (TextView) findViewById(R.id.tv_status);
                header.setText(" IRIGASI ON ");
                header.setTextColor(Color.WHITE);
                header.setBackgroundColor(Color.parseColor("#1BBC9B"));

                controlNode(id_alat, "1");
            }
        });
        btnOFF = (Button) findViewById(R.id.bOFF);
        btnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView header = (TextView) findViewById(R.id.tv_status);
                header.setText(" IRIGASI OFF ");
                header.setTextColor(Color.WHITE);
                header.setBackgroundColor(Color.RED);
                controlNode(id_alat, "0");
            }
        });

        if(device.equalsIgnoreCase("597206a1487a43110490c0b0")) {
            getData();
        }
        else {
            //Toast.makeText(this, "bukan sigap", Toast.LENGTH_SHORT).show();
            btnSettings.setAlpha(.5f);
            btnSettings.setClickable(false);
            btnON.setVisibility(View.GONE);
            btnOFF.setVisibility(View.GONE);

            getOther();
        }
//        this.context = this;
//        Intent alarm = new Intent(this.context, AlarmReceiver.class);
//        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//        if(alarmRunning == false) {
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 100, pendingIntent); //in milisecond
//        }



        startHandler();
    }

    public void startHandler()
    {
        handler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                if(!isBusy) callAysncTask();

                if(!stop) startHandler();
            }
        }, 1000);
    }
    private void callAysncTask()
    {
        //getData();
        checkLastData();
    }

    public void checkLastData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_DATA+"/"+id_alat, new Response.Listener<String>() {
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

                        if(strDateOnly.equals(lastDataDate)){

                        }
                        else {
                            AllData dataa = new AllData(ukk, hpc, humidity, temperature, hpsp, durtime, strDateOnly,
                                    Long.toString(secondsSinceEpoch), status);
                            allDatas.add(dataa);

                            addBuddiesView(allDatas.get(allDatas.size()-(allDatas.size())));
                            TextView header = (TextView) findViewById(R.id.tv_status);

                            if(status.equals("1")){
                                header.setText(" IRIGASI ON ");
                                header.setTextColor(Color.WHITE);
                                header.setBackgroundColor(Color.parseColor("#1BBC9B"));
                            }

                            else {
                                header.setText(" IRIGASI OFF ");
                                header.setTextColor(Color.WHITE);
                                header.setBackgroundColor(Color.RED);
                            }
                            lastDataDate = strDateOnly;
                        }
                    } else {
                        // error in fetching data
                        Toast.makeText(DetailActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    //Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(DetailActivity.this, EmptyData.class);
                    intent3.putExtra("id_alat", id_alat);
                    intent3.putExtra("device", device);
                    startActivity(intent3);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DetailActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void getData(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_DATA+"/"+id_alat, new Response.Listener<String>() {
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
                        lastDataDate = strDateOnly;


                        Log.d("dateonlybefore", createdAt);
                        Log.d("dateonlyafter", strDateOnly);

                        long secondsSinceEpoch = dateTime.getMillis() / 1000;
                        Log.d("haha", Long.toString(secondsSinceEpoch));
                        AllData dataa = new AllData(ukk, hpc, humidity, temperature, hpsp, durtime, strDateOnly,
                                Long.toString(secondsSinceEpoch), status);
                        allDatas.add(dataa);

                        addBuddiesView(allDatas.get(allDatas.size()-(allDatas.size())));
                        TextView header = (TextView) findViewById(R.id.tv_status);
//                        if(Float.parseFloat(Uk) > 0){
//                            header.setText(" IRIGASI ON ");
//                            header.setTextColor(Color.WHITE);
//                            header.setBackgroundColor(Color.parseColor("#1BBC9B"));
//                        }
//                        else if(Float.parseFloat(Uk) == 0){
//                            header.setText(" IRIGASI OFF ");
//                            header.setTextColor(Color.WHITE);
//                            header.setBackgroundColor(Color.RED);
//                        }
//                        else{
//                            Uk = "-1";
//                            Log.i("data",""+Uk);
//                            header.setText(" DRAINASE ");
//                            header.setTextColor(Color.WHITE);
//                            header.setBackgroundColor(Color.BLUE);
//                        }
                        if(status.equals("1")){
                            header.setText(" IRIGASI ON ");
                            header.setTextColor(Color.WHITE);
                            header.setBackgroundColor(Color.parseColor("#1BBC9B"));
                        }
                        else {
                            header.setText(" IRIGASI OFF ");
                            header.setTextColor(Color.WHITE);
                            header.setBackgroundColor(Color.RED);
                        }
                    } else {
                        // error in fetching data
                        Toast.makeText(DetailActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    //Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(DetailActivity.this, EmptyData.class);
                    intent3.putExtra("id_alat", id_alat);
                    intent3.putExtra("device", device);
                    startActivity(intent3);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DetailActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void getOther() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_DATA+"/"+id_alat, new Response.Listener<String>() {
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
                        //Log.i("dataDapat",""+setObj);
                        //AllsData allsData = new AllsData();
                        Iterator<String> keys= setObj.keys();
                        numkeys=1;

                        try{
                            //String parameter
                            Class[] paramString = new Class[1];
                            paramString[0] = String.class;
                            String className  = "com.example.aw.sigap.model.AllsData";
                            Class cls = Class.forName(className);
                            Object obj1 = cls.newInstance();

                            while (keys.hasNext())
                            {
                                String keyValue = (String)keys.next();
                                //Log.i("dataDapatkey",""+keyValue);
                                keyList.add(keyValue);
                                String Value = setObj.getString(keyValue);
                                Log.i("dataDapatvalue",""+Value);
                                valList.add(Value);
                                String methodName = "setSensor" + Integer.toString(numkeys);
                                Log.i("dataDapatmethod",""+methodName);

                                Method method = cls.getDeclaredMethod(methodName, paramString);
                                method.invoke(obj1, new String(Value));

                                numkeys++;
                            }
                            for(int i=numkeys; i<=8; i++){
                                String methodName = "setSensor" + Integer.toString(i);
                                Log.i("dataDapatmethod",""+methodName);

                                Method method = cls.getDeclaredMethod(methodName, paramString);
                                method.invoke(obj1, new String("10"));
                                valList.add("10");
                            }
                            Log.i("dataDapatnumkeys",""+numkeys);

                        }catch(Exception ex){
                            ex.printStackTrace();
                        }

                        String createdAt = dataObj.getString("created_at");
                        DateTime dateTime = DateTime.parse(createdAt);
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");
                        String strDateOnly = fmt.print(dateTime.plusHours(7));
                        lastDataDate = strDateOnly;
                        long secondsSinceEpoch = dateTime.getMillis() / 1000;
                        Log.d("haha", Long.toString(secondsSinceEpoch));
                        //allsData.setCreatedAt(strDateOnly);
                        //allsData.setTimeStamp(Long.toString(secondsSinceEpoch));
                        //allsData.setStatus("0");
                        String satu = valList.get(0);
                        String dua = valList.get(1);
                        String tiga = valList.get(2);
                        String empat = valList.get(3);
                        String lima = valList.get(4);
                        String enam = valList.get(5);
                        String tujuh = valList.get(6);
                        String lapan = valList.get(7);
                        Log.i("dataDapatvalList",""+tujuh);
                        AllsData allsData = new AllsData(satu, dua, tiga, empat, lima, enam, tujuh, lapan, strDateOnly,
                                Long.toString(secondsSinceEpoch), "0");
                        allsDataList.add(allsData);

                        //TextView header = (TextView) findViewById(R.id.tv_status);


                        addBuddiesView2(allsDataList.get(allsDataList.size()-(allsDataList.size())));
                        TextView header = (TextView) findViewById(R.id.tv_status);
                        header.setVisibility(View.GONE);
                        TextView tv2 = (TextView) findViewById(R.id.textView2);
                        tv2.setVisibility(View.GONE);

                    } else {
                        // error in fetching data
                        Toast.makeText(DetailActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    //Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(DetailActivity.this, EmptyData.class);
                    intent3.putExtra("id_alat", id_alat);
                    intent3.putExtra("device", device);
                    startActivity(intent3);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DetailActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void controlNode(final String idalat, final String status){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoint.URL_CONTROL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(DetailActivity.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(DetailActivity.this, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DetailActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("idnode", idalat);
                params.put("status", status);
                params.put("deviceid", "597206a1487a43110490c0b0");

                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    private void addBuddiesView(final AllData p) {
        if(flexboxLayout!=null)
            if(flexboxLayout.getChildCount()>0)
                flexboxLayout.removeAllViews();

        for(int i=0; i<5; i++) {
            final View itemView = getLayoutInflater().inflate(R.layout.layout_progress, null);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                    intent.putExtra("objectPerson", p);
                    startActivity(intent);
                }
            });
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressListrik);

            TextView namaSensor = (TextView) itemView.findViewById(R.id.nama_sensor);
            TextView nilaiSensor = (TextView) itemView.findViewById(R.id.nilai_sensor);
            TextView statusSensor = (TextView) itemView.findViewById(R.id.status_sensor);

            final int maxValue = 100;
            ProgressBarAnimation animation;
            float nilai;

            switch (i) {
                case 0:
                    nilaiSensor.setText(p.getHpc());
                    namaSensor.setText("HPc (WaterLevel)");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getHpc());
                    progressBar.setMax(10);
                    animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
                    animation.setDuration(500);
                    progressBar.setAnimation(animation);
                    startCountAnimation(nilaiSensor,1500,0, (int) nilai);

                    break;

                case 1:
                    nilaiSensor.setText(p.getUkk());
                    namaSensor.setText("Uk (Membership)");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getUkk());
                    progressBar.setMax(1);
                    animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
                    animation.setDuration(500);
                    progressBar.setAnimation(animation);
                    startCountAnimation(nilaiSensor,1500,0, (int) nilai);

                    break;

                case 2:
                    nilaiSensor.setText(p.getDurtime());
                    namaSensor.setText("Operation Time");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getDurtime());
                    progressBar.setMax(180);
                    animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
                    animation.setDuration(500);
                    progressBar.setAnimation(animation);
                    startCountAnimation(nilaiSensor,1500,0, (int) nilai);

                    break;

                case 3:
                    nilaiSensor.setText(p.getHumidity());
                    namaSensor.setText("Humidity");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getHumidity());
                    progressBar.setMax(100);
                    animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
                    animation.setDuration(500);
                    progressBar.setAnimation(animation);
                    startCountAnimation(nilaiSensor,1500,0, (int) nilai);

                    break;

                case 4:
                    nilaiSensor.setText(p.getTemperature());
                    namaSensor.setText("Temperature");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getTemperature());
                    progressBar.setMax(50);
                    animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
                    animation.setDuration(500);
                    progressBar.setAnimation(animation);
                    startCountAnimation(nilaiSensor,1500,0, (int) nilai);

                    break;
            }
            if (flexboxLayout != null)
                flexboxLayout.addView(itemView);
        }
    }

    private void addBuddiesView2(final AllsData q) {
        Log.i("dataDapatsensor1",""+q.getCreatedAt());
        if(flexboxLayout!=null)
            if(flexboxLayout.getChildCount()>0)
                flexboxLayout.removeAllViews();

        for(int i=1; i<=numkeys-1; i++) {
            final View itemView = getLayoutInflater().inflate(R.layout.layout_progress, null);

            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressListrik);

            TextView namaSensor = (TextView) itemView.findViewById(R.id.nama_sensor);
            TextView nilaiSensor = (TextView) itemView.findViewById(R.id.nilai_sensor);
            TextView statusSensor = (TextView) itemView.findViewById(R.id.status_sensor);

            final int maxValue = 100;
            ProgressBarAnimation animation;
            float nilai;
            String value = valList.get(i-1);
            String key = keyList.get(i-1);
            nilaiSensor.setText(value);
            namaSensor.setText(key);
            statusSensor.setText("ON");

            nilai = Float.parseFloat(value);
            progressBar.setMax(100);
            animation =  new ProgressBarAnimation(progressBar,progressBar.getProgress(),nilai);
            animation.setDuration(500);
            progressBar.setAnimation(animation);
            startCountAnimation(nilaiSensor,1500,0, (int) nilai);


            if (flexboxLayout != null)
                flexboxLayout.addView(itemView);
        }
    }

    private void startCountAnimation(TextView textProgress, int duration, int from, int to) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(from, to);
        animator.setDuration(duration);
        final TextView textView = textProgress;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText("" + (int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
