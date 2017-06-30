package com.example.aw.sigap.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.aw.sigap.helper.ProgressBarAnimation;
import com.example.aw.sigap.model.Alat;
import com.example.aw.sigap.model.AllData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {

    private String TAG = DashboardActivity.class.getSimpleName();
    Button btnHistory, btnMaps, btnSettings, btnON, btnOFF;
    Toolbar toolbar;
    String Uk, apiKey, id_alat, device, latitude, longitude;
    String[] mStrings;

    @Bind(R.id.fb_buddies)
    FlexboxLayout flexboxLayout;
    List<AllData> allDatas;

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
        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        Log.d("api", apiKey);
        if(device.equalsIgnoreCase("590e009c2476bf2dbca3e393")) {
            getData();
        }
        else {
            Toast.makeText(this, "bukan sigap", Toast.LENGTH_SHORT).show();
            getOther();
        }

        btnHistory = (Button) findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DataHistoryActivity.class);
                intent.putExtra("id_alat", id_alat);
                startActivity(intent);
            }
        });

//        btnMaps = (Button) findViewById(R.id.btn_map);
//        btnMaps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent1 = new Intent(DetailActivity.this, MapsActivity.class);
//                intent1.putExtra("id_alat", id_alat);
//                intent1.putExtra("latitude", latitude);
//                intent1.putExtra("longitude", longitude);
//                startActivity(intent1);
//
//            }
//        });

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

                        JSONObject dataObj = (JSONObject) data.get(1);
                        JSONObject setObj = new JSONObject(dataObj.getString("data"));
                        JSONObject setObj2 = new JSONObject(dataObj.getString("sensornode"));
                        Log.i("dataDapat",""+dataObj);


                        String hpc = setObj.getString("waterlevel");
                        String humidity = setObj.getString("humidity");
                        String temperature = setObj.getString("temperature");
                        String status = setObj2.getString("status");

                        String ukk = dataObj.getString("uk");
                        String hpsp = dataObj.getString("setPoint");
                        String durtime = dataObj.getString("opTime");
                        String createdAt = dataObj.getString("created_at");

                        DateTime dateTime = DateTime.parse(createdAt);
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");
                        String strDateOnly = fmt.print(dateTime);
                        long secondsSinceEpoch = dateTime.getMillis() / 1000;
                        Log.d("haha", Long.toString(secondsSinceEpoch));
                        AllData dataa = new AllData(ukk, hpc, humidity, temperature, hpsp, durtime, strDateOnly,
                                Long.toString(secondsSinceEpoch), status);
                        allDatas.add(dataa);
                        Uk = ukk;
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
                        Log.i("dataDapat",""+setObj);

                        Iterator<String> keys= setObj.keys();
                        int i=0;
                        while (keys.hasNext())
                        {
                            String keyValue = (String)keys.next();
                            Log.i("dataDapatkey",""+keyValue);
                            //mStrings[i] = setObj.getString(keyValue);
                            i++;
                        }

                        String createdAt = dataObj.getString("created_at");

                        DateTime dateTime = DateTime.parse(createdAt);
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");
                        String strDateOnly = fmt.print(dateTime);
                        long secondsSinceEpoch = dateTime.getMillis() / 1000;
                        Log.d("haha", Long.toString(secondsSinceEpoch));

                        //addBuddiesView(allDatas.get(allDatas.size()-(allDatas.size())));
                        //TextView header = (TextView) findViewById(R.id.tv_status);


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
                params.put("device", "590e009c2476bf2dbca3e393");

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
