package com.example.aw.sigap.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
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
import com.example.aw.sigap.model.AllData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {

    private String TAG = DashboardActivity.class.getSimpleName();
    Button btnHistory;
    Toolbar toolbar;
    String Uk;

    @Bind(R.id.fb_buddies)
    FlexboxLayout flexboxLayout;
    List<AllData> allDatas;
    String id_alat;

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
        Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");

        allDatas = new ArrayList<AllData>();
        getData();

        btnHistory = (Button) findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, DataHistoryActivity.class);
                intent.putExtra("id_alat", id_alat);
                startActivity(intent);
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
//                        Toast.makeText(DetailActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("tasks");
                        for (int i = 0; i < 1; i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("dataDapat",""+dataObj);
                            //String hpsp = dataObj.getString("status");
                            String ukk = dataObj.getString("suhu");
                            String hpc = dataObj.getString("ph");
                            String durtime = dataObj.getString("do");
                            Uk = ukk;
                            String hpsp = dataObj.getString("hasil");
                            String createdAt = dataObj.getString("createdAt");
                            AllData dataa = new AllData(ukk,hpc,hpsp,durtime, createdAt);
                            allDatas.add(dataa);
                        }
                        addBuddiesView(allDatas.get(allDatas.size()-(allDatas.size())));
                        TextView header = (TextView) findViewById(R.id.tv_status);
                        if(Float.parseFloat(Uk) > 0){
                            header.setText(" IRIGASI ON ");
                            header.setTextColor(Color.WHITE);
                            header.setBackgroundColor(Color.parseColor("#1BBC9B"));
                        }
                        else if(Float.parseFloat(Uk) == 0){
                            header.setText(" IRIGASI OFF ");
                            header.setTextColor(Color.WHITE);
                            header.setBackgroundColor(Color.RED);
                        }
                        else{
                            Uk = "-1";
                            Log.i("data",""+Uk);
                            header.setText(" DRAINASE ");
                            header.setTextColor(Color.WHITE);
                            header.setBackgroundColor(Color.BLUE);
                        }
                    } else {
                        // error in fetching data
                        Toast.makeText(DetailActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
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
    }

    private void addBuddiesView(final AllData p) {
        if(flexboxLayout!=null)
            if(flexboxLayout.getChildCount()>0)
                flexboxLayout.removeAllViews();

        for(int i=0; i<3; i++) {
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
                    namaSensor.setText("HPc");
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
                    namaSensor.setText("Uk");
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
                    namaSensor.setText("OpTime");
                    statusSensor.setText("ON");

                    nilai = Float.parseFloat(p.getDurtime());
                    progressBar.setMax(180);
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
