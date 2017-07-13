package com.example.aw.sigap.activity;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.aw.sigap.model.SensorNode;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.libspin.MultiSelectionSpinner;

public class CreateSensorNode extends AppCompatActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener{

    private String TAG = DashboardActivity.class.getSimpleName();
    private Toolbar toolbar;
    private EditText nama;
    private Spinner spMiconType;
    private TextView deviceName;
    private Button createNode;
    private String userId, apiKey, id_alat, device;

    List<String> where = new ArrayList<String>();
    List<String> selected = new ArrayList<String>();
    String miconType[] = { "Arduino Uno", "Arduino Leonardo" , "Arduino Mega" };
    ArrayAdapter<String> adapterMiconType;
    JSONObject sentype = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sensor_node);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");

        final SharedPreferences sharedPreferencesUid= getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        userId = sharedPreferencesUid.getString(Config.USERNAME_SHARED_PREF, "");
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");

        nama = (EditText)findViewById(R.id.sname);
        deviceName = (TextView)findViewById(R.id.deviceName);
        deviceName.setText(id_alat);
        //////////////////////////////////////////////////////////////////////////
        //initiate spinner
        spMiconType = (Spinner) findViewById(R.id.spinnerMiconType);
        // Initialize and set Adapter
        adapterMiconType = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, miconType);
        adapterMiconType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMiconType.setAdapter(adapterMiconType);
        ///////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////
        getSensorNode();
        //String[] array = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        selected.add("590f9508d71b1b270c77dfe4");
        selected.add("590f9523d71b1b270c77dfe5");

        /////////////////////////////////////////////////////////////////////////

        createNode = (Button)findViewById(R.id.createNode);
        createNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newnama = nama.getText().toString();
                String newtype = spMiconType.getSelectedItem().toString();
                createSensorNode(newnama, newtype);
            }
        });
    }
    public void getSensorNode(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_TYPES+"/list", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        //Toast.makeText(CreateSensorNode.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("sensortype");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("dataDapat",""+dataObj);
                            String _id = dataObj.getString("_id");
                            String name = dataObj.getString("name");
                            Log.d("namasensor", name);
                            where.add(name);
                            sentype.put(name, _id);
                            //Toast.makeText(CreateSensorNode.this, sentype.toString(), Toast.LENGTH_SHORT).show();
                        }

//                        String[] simpleArray = new String[ where.size() ];
//                        where.toArray( simpleArray );
                        MultiSelectionSpinner multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner);
                        multiSelectionSpinner.setItems(where);
                        multiSelectionSpinner.setSelection(new int[]{2, 2});
                        multiSelectionSpinner.setListener(CreateSensorNode.this);




                    } else {
                        // error in fetching data
                        Toast.makeText(CreateSensorNode.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(CreateSensorNode.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(CreateSensorNode.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void createSensorNode(final String nama, final String type){
        //Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoint.URL_CREATE_NODE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(CreateSensorNode.this, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(CreateSensorNode.this, DashboardActivity.class);
                        intent2.putExtra("id_alat", id_alat);
                        startActivity(intent2);

                    } else {
                        Toast.makeText(CreateSensorNode.this, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(CreateSensorNode.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(CreateSensorNode.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("name", nama);
                params.put("miconType", type);
                params.put("device", id_alat);
                for(int i=0; i< selected.size(); i++){
                    params.put("sensortype["+i+"]", selected.get(i));
                }


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
        else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {
        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
        selected.clear();
        for(int i=0; i< strings.size(); i++){
            String keyy = strings.get(i);

            //Toast.makeText(this, keyy, Toast.LENGTH_SHORT).show();
            try{
                String idd = sentype.getString(keyy);
                //Toast.makeText(this, idd, Toast.LENGTH_SHORT).show();
                selected.add(idd);
            }catch (JSONException e){

            }

        }


    }
}
