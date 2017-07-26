package com.example.aw.sigap.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.SensorNode;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class EmptyData extends AppCompatActivity {
    private String TAG = DashboardActivity.class.getSimpleName();
    private TextView tvDevice;
    private TextView tvNode;
    private TextView tvToken;
    private TextView tvKeyData;
    private Toolbar toolbar;
    private String id_alat, id_device, apiKey, emailaddress;
    private ImageButton copy_deviceid, copy_sensornodeid, copy_authorization;
    ArrayList<String> keyData = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_data);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");
        id_device = intent.getStringExtra("device");

        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesEmail = getSharedPreferences(Config.SHARED_PREF_EMAIL,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        emailaddress = sharedPreferencesEmail.getString(Config.EMAILADDRESS_SHARED_PREF, "");
        Log.d("api", apiKey);

        tvDevice = (TextView) findViewById(R.id.devID);
        tvNode = (TextView) findViewById(R.id.senID);
        tvToken = (TextView) findViewById(R.id.apiKey);
        tvKeyData = (TextView) findViewById(R.id.tvKeyData);
        tvDevice.setText(id_device);
        tvNode.setText(id_alat);
        tvToken.setText(apiKey);

        copy_deviceid =(ImageButton) findViewById(R.id.btn_copy_deviceid);
        copy_deviceid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(emailaddress, id_device, id_alat, apiKey, keyData.toString());
            }
        });

//        copy_sensornodeid = (ImageButton) findViewById(R.id.btn_copy_sensornodeid);
//        copy_sensornodeid.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("simple text", tvNode.getText());
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(EmptyData.this, "copied sensornodeid to clipboard", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        copy_authorization = (ImageButton) findViewById(R.id.btn_copy_authorization);
//        copy_authorization.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("simple text", tvToken.getText());
//                clipboard.setPrimaryClip(clip);
//                Toast.makeText(EmptyData.this, "copied authorization to clipboard", Toast.LENGTH_SHORT).show();
//            }
//        });
        getNodes();
    }
    public void getNodes(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_NODES2+"/"+id_alat, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
//                        Toast.makeText(DashboardActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        //JSONArray data = obj.getJSONArray("sensornode");
                        JSONObject dataObj = new JSONObject(obj.getString("sensornode"));
                        //Toast.makeText(EmptyData.this, dataObj.toString(), Toast.LENGTH_SHORT).show();
                        //for (int i = 0; i < data.length(); i++) {
                            //JSONObject dataObj = (JSONObject) data.get(i);
                        Log.i("dataDapat",""+dataObj);
                        String idNodes = dataObj.getString("_id");
                        String namaNodes = dataObj.getString("name");
                        String device = dataObj.getString("device");
                        String tipeNodes = dataObj.getString("miconType");
                        JSONArray sentype = dataObj.getJSONArray("sensortype");
                        for (int j = 0; j < sentype.length(); j++){
                            JSONObject dataObj2 = (JSONObject) sentype.get(j);
                            String senName = dataObj2.getString("dataKey");
                            //Toast.makeText(EmptyData.this, senName, Toast.LENGTH_SHORT).show();
                            keyData.add(senName);
                        }
                        tvKeyData.setText("Your Key: "+ keyData.toString());

                        //}


                    } else {
                        // error in fetching data
                        Toast.makeText(EmptyData.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(EmptyData.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(EmptyData.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void sendEmail(final String emailaddress, final String id_device, final String id_alat, final String apiKey, final String keyData){
        //Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoint.URL_EMAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(EmptyData.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(EmptyData.this, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(EmptyData.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(EmptyData.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("email", emailaddress);
                params.put("deviceid", id_device);
                params.put("nodeid", id_alat);
                params.put("auth", apiKey);
                params.put("keyData", keyData);

                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
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

        return super.onOptionsItemSelected(item);
    }
}
