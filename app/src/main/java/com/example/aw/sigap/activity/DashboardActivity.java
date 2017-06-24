package com.example.aw.sigap.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.aw.sigap.model.Alat;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.aw.sigap.R.drawable.payload;

public class DashboardActivity extends BaseActivity {

    private String TAG = DashboardActivity.class.getSimpleName();
    private String userId, apiKey;
    private Button btnCreate;
    @Bind(R.id.fb_buddies)
    FlexboxLayout flexboxLayout;
    List<Alat> allAlat;

    @Bind(R.id.none_conn)
    LinearLayout nonConnection;

    ProgressDialog pDialog;
    Toolbar toolbar;

    @OnClick(R.id.sub)
    public void suub(){
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Toast.makeText(this, "subscribe press", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.token)
    public void token(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Toast.makeText(this, "getToken "+token, Toast.LENGTH_SHORT).show();
        Log.d(TAG, token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        allAlat = new ArrayList<Alat>();
        final SharedPreferences sharedPreferencesUid= getSharedPreferences(Config.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        userId = sharedPreferencesUid.getString(Config.USERNAME_SHARED_PREF, "");
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");

        Log.d("uid", userId);
        Log.d("api", apiKey);
        getAlat();
        //getAlatMQTT();

        btnCreate = (Button) findViewById(R.id.create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(DashboardActivity.this, CreateDevice.class);

                startActivity(intent2);
            }
        });

    }

    public void getAlat(){
        showPDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_ALAT+"/"+userId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getBoolean("error") == false) {
//                        Toast.makeText(DashboardActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("device");
                        nonConnection.setVisibility(View.GONE);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("dataDapat",""+dataObj);
                            String idAlat = dataObj.getString("_id");
                            String namaAlat = dataObj.getString("name");
                            String webAddr = dataObj.getString("webaddr");
                            String kodeAlat = dataObj.getString("_id");
                            String apayangPenting = dataObj.getString("created_at");
                            //int rssi = dataObj.getInt("rssi");
                            //int battery = dataObj.getInt("battery");
                            String latitude = dataObj.isNull("latitude") ? null : dataObj.getString("latitude");
                            String longitude = dataObj.isNull("longitude") ? null : dataObj.getString("longitude");
                            Alat alat = new Alat( idAlat, namaAlat, webAddr, kodeAlat, apayangPenting, latitude, longitude, 14, 14 );
                            allAlat.add(alat);
                        }
                        renderBuddies();

                    } else {
                        // error in fetching data
                        Toast.makeText(DashboardActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(DashboardActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DashboardActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        hidePDialog();
    }

    public void getAlatMQTT(){

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.113:1883", clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    //PUBLISH THE MESSAGE
                    MqttMessage message = new MqttMessage("Hello, I am an Android Mqtt Client.".getBytes());
                    message.setQos(2);
                    message.setRetained(false);

                    String topic = "test";

                    try {
                        client.publish(topic, message);
                        Log.i("mqtt", "Message published");
                        Toast.makeText(DashboardActivity.this, "success publish", Toast.LENGTH_SHORT).show();

                        // client.disconnect();
                        //Log.i("mqtt", "client disconnected");

                    } catch (MqttPersistenceException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    } catch (MqttException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    private void addBuddiesView(final Alat p) {
        final View itemView = getLayoutInflater().inflate(R.layout.layout_item_buddy_big_shadow, null);
        final String name = p.getNama();
        final String webaddr = p.getWebaddr();
        final String id_alat = p.getId();
        final String latitude = p.getLatitude();
        final String longitude = p.getLongitude();

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SensorNodes.class);
                intent.putExtra("objectPerson", p);
                intent.putExtra("id_alat", id_alat);
                intent.putExtra("name", name);
                intent.putExtra("webaddr", webaddr);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

        TextView tv = (TextView) itemView.findViewById(R.id.tv_name);
        tv.setText(name);

        if(flexboxLayout!=null)
            flexboxLayout.addView(itemView);
    }
    private void renderBuddies() {
        if(flexboxLayout!=null)
            if(flexboxLayout.getChildCount()>0)
                flexboxLayout.removeAllViews();
        for (Alat p : allAlat) {
            addBuddiesView(p);
        }
    }

    private void showPDialog(){
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

    private void hidePDialog(){
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
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
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Exit Application?");
            alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.APIKEY_SHARED_PREF, "");
                        editor.clear();

                        //Saving the sharedpreferences
                        editor.commit();
//                        SharedPreferences user = DashboardActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME,
//                                Context.MODE_PRIVATE);
//                        SharedPreferences apiKey = DashboardActivity.this.getSharedPreferences(Config.SHARED_PREF_API,
//                                Context.MODE_PRIVATE);
//
//                        SharedPreferences.Editor editor = user.edit();
//                        SharedPreferences.Editor editor1 = apiKey.edit();
//
//                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
//                        editor1.putBoolean(Config.API_SHARED_PREF, false);
//
//                        editor.putString(Config.USERNAME_SHARED_PREF, "");
//                        editor1.putString(Config.APIKEY_SHARED_PREF, "");
//
//                        editor.commit();
//                        editor1.commit();

                        //Starting login activity
                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

            alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

            //Showing the alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        else if(id == R.id.action_refresh){
            Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
