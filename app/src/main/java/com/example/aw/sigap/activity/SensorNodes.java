package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aw.sigap.R;
import com.example.aw.sigap.adapter.SensorNodeAdapter;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.model.Alat;
import com.example.aw.sigap.model.SensorNode;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class SensorNodes extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SensorNodeAdapter adapter;
    private List<SensorNode> sensorNodeList;
    private TextView tvdevDetail, tvWebAddr;
    private Button bdelDevice, bshowmap;
    private String TAG = DashboardActivity.class.getSimpleName();
    String Uk, userId, apiKey, id_alat, name , webaddr, latitude, longitude;
    int notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_nodes);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");
        name = intent.getStringExtra("name");
        webaddr = intent.getStringExtra("webaddr");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        ButterKnife.bind(this);
        Log.d("id_alat", id_alat);

        tvdevDetail = (TextView)findViewById(R.id.tv_devdetail);
        tvdevDetail.setText(name);
        tvWebAddr = (TextView)findViewById(R.id.webaddr);
        tvWebAddr.setText(webaddr);

        bshowmap = (Button)findViewById(R.id.btnshowmap);
        bshowmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(SensorNodes.this, MapsActivity.class);
                intent1.putExtra("id_alat", id_alat);
                intent1.putExtra("name", name);
                intent1.putExtra("latitude", latitude);
                intent1.putExtra("longitude", longitude);
                startActivity(intent1);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        sensorNodeList = new ArrayList<>();
        adapter = new SensorNodeAdapter(this, sensorNodeList);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        final SharedPreferences sharedPreferencesUid= getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        userId = sharedPreferencesUid.getString(Config.USERNAME_SHARED_PREF, "");
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");

        prepareNodes();
//        bdelDevice = (Button)findViewById(R.id.bDeleteDevice);
//        bdelDevice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(SensorNodes.this, "delete this", Toast.LENGTH_SHORT).show();
//                deleteDevice(id_alat);
//            }
//        });
    }

    private void prepareNodes() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_NODES+"/"+id_alat, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
//                        Toast.makeText(DashboardActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("sensornode");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("dataDapat",""+dataObj);
                            String idNodes = dataObj.getString("_id");
                            String namaNodes = dataObj.getString("name");
                            String device = dataObj.getString("device");
                            String tipeNodes = dataObj.getString("miconType");
                            if (dataObj.has("notification")) {
                                notification = dataObj.getInt("notification");
                            }
                            else {
                                notification = 0;
                            }

                            SensorNode sensorNode = new SensorNode( idNodes, namaNodes, device, tipeNodes, notification );
                            sensorNodeList.add(sensorNode);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        // error in fetching data
                        Toast.makeText(SensorNodes.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(SensorNodes.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(SensorNodes.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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


//        sensorNodeList.add(new SensorNode("1", "Node1"));
//        sensorNodeList.add(new SensorNode("2", "Node2"));
//        sensorNodeList.add(new SensorNode("3", "Node3"));
//
//        adapter.notifyDataSetChanged();

    }

    private void deleteDevice(final String idalat){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                EndPoint.URL_DELETE_ALAT+"/"+idalat+"/remove", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(SensorNodes.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(SensorNodes.this, DashboardActivity.class);

                        startActivity(intent2);

                    } else {
                        Toast.makeText(SensorNodes.this, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(SensorNodes.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(SensorNodes.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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


                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.node, menu);
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
        else if(id == R.id.create_node){
            Intent intent2 = new Intent(SensorNodes.this, CreateSensorNode.class);
            intent2.putExtra("id_alat", id_alat);
            startActivity(intent2);
        }
        else if(id == R.id.action_refresh){
            Intent intent3 = new Intent(SensorNodes.this, SensorNodes.class);
            intent3.putExtra("id_alat", id_alat);
            startActivity(intent3);
        }
        return super.onOptionsItemSelected(item);
    }
}
