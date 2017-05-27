package com.example.aw.sigap.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.aw.sigap.fragment.DurtimeFragment;
import com.example.aw.sigap.fragment.HpcFragment;
import com.example.aw.sigap.fragment.HumidityFragment;
import com.example.aw.sigap.fragment.TemperatureFragment;
import com.example.aw.sigap.fragment.UkFragment;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.PredictionData;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DataHistoryActivity extends BaseActivity implements HumidityFragment.OnFragmentInteractionListener, TemperatureFragment.OnFragmentInteractionListener{
    private String TAG = DashboardActivity.class.getSimpleName();
    public static int UK = 1;
    public static int HPC = 2;
    public static int DURTIME = 3;
    public static int HPSP = 6;
    public static int HUMIDITY = 4;
    public static int TEMPERATURE = 5;

    @Bind(R.id.tl_buddy_add)
    TabLayout tabLayout;
    @Bind(R.id.vp_buddy_add)
    ViewPager viewPager;

    private ViewPagerAdapter adapter;
    public static List<AllData> allDatas;
    public static List<PredictionData> predDatas;
    String id_alat, apiKey;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_history);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");

        allDatas = new ArrayList<AllData>();
        predDatas = new ArrayList<PredictionData>();

//        predDatas.add(new PredictionData(Integer.toString(7), Integer.toString(3)));
//        predDatas.add(new PredictionData(Integer.toString(4), Integer.toString(8)));
//        predDatas.add(new PredictionData(Integer.toString(10), Integer.toString(2)));

        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        Log.d("api", apiKey);
        getData();
        predictData(id_alat);

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
//                        Toast.makeText(DataHistoryActivity.this, "Data dapat"+response, Toast.LENGTH_SHORT).show();
                        JSONArray data = obj.getJSONArray("dataset");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            JSONObject setObj = new JSONObject(dataObj.getString("data"));
                            Log.i("dataDapat",""+dataObj);
                            String ukk = setObj.getString("humidity");
                            String hpc = setObj.getString("humidity");
                            String humidity = setObj.getString("humidity");
                            String temperature = setObj.getString("humidity");
                            String hpsp = setObj.getString("humidity");
                            String durtime = setObj.getString("humidity");
                            String createdAt = dataObj.getString("created_at");

//                            long dv = Long.valueOf(createdAt)*1000;// its need to be in milisecond
//                            Date df = new java.util.Date(dv);
//                            String vv = new SimpleDateFormat("MM dd, yyyy hh:mma").format(df);

                            AllData dataa = new AllData(ukk, hpc, humidity, temperature, hpsp, durtime, createdAt);
                            allDatas.add(dataa);

                            setupViewPager(viewPager);
                            tabLayout.setupWithViewPager(viewPager);
                            tabLayout.getTabAt(0).setIcon(R.drawable.thermometer);
                            tabLayout.getTabAt(1).setIcon(R.drawable.thermometer);
                            tabLayout.getTabAt(2).setIcon(R.drawable.thermometer);
                            tabLayout.getTabAt(3).setIcon(R.drawable.thermometer);
                            tabLayout.getTabAt(4).setIcon(R.drawable.thermometer);
                        }
                    } else {
                        Toast.makeText(DataHistoryActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(DataHistoryActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DataHistoryActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void predictData(final String idalat){
        //Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                EndPoint.URL_PREDICTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data = obj.getJSONArray("prediction");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject dataObj = (JSONObject) data.get(i);
                        Log.i("prediksiDapat", "" + dataObj);
                        String senval = dataObj.getString("senVal");

                        PredictionData pred = new PredictionData(senval, senval);
                        predDatas.add(pred);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(DataHistoryActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(DataHistoryActivity.this, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

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
                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DurtimeFragment(), "Durtime");
        adapter.addFragment(new UkFragment(), "Uk");
        adapter.addFragment(new HpcFragment(), "Hpc");
        adapter.addFragment(new HumidityFragment(), "Humid");
        adapter.addFragment(new TemperatureFragment(), "Temp");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
