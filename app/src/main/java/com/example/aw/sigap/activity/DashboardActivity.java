package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardActivity extends BaseActivity {

    private String TAG = DashboardActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Bind(R.id.fb_buddies)
    FlexboxLayout flexboxLayout;
    List<Alat> allAlat;

    @Bind(R.id.none_conn)
    LinearLayout nonConnection;

    ProgressDialog pDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        allAlat = new ArrayList<Alat>();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        getAlat();
    }

    public void getAlat(){
        showPDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                EndPoint.URL_ALAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
//                        Toast.makeText(DashboardActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("tasks");
                        nonConnection.setVisibility(View.GONE);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("dataDapat",""+dataObj);
                            String namaAlat = dataObj.getString("nama");
                            String kodeAlat = dataObj.getString("id_alat");
                            String apayangPenting = dataObj.getString("id_alat");
                            Alat alat = new Alat(namaAlat,kodeAlat,apayangPenting);
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
                headers.put("Authorization", Config.USER_AUTHORIZATION);

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


    private void addBuddiesView(final Alat p) {
        final View itemView = getLayoutInflater().inflate(R.layout.layout_item_buddy_big_shadow, null);
        final String name = p.getNama();
        final String id_alat = p.getKode();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DetailActivity.class);
                intent.putExtra("objectPerson", p);
                intent.putExtra("id_alat", id_alat);
                startActivity(intent);
            }
        });
        TextView tv = (TextView) itemView.findViewById(R.id.tv_name);
        tv.setText(name);
//        ImageView im = (ImageView) itemView.findViewById(R.id.civ_photo);
//        Picasso.with(getActivity()).load(p.getPhotoUrl()).placeholder(R.drawable.user_photo).into(im);
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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
            FirebaseAuth.getInstance().signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
