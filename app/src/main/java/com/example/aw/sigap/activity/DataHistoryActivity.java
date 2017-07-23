package com.example.aw.sigap.activity;

import android.app.ProgressDialog;
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
import android.view.View;
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
import com.example.aw.sigap.fragment.DurtimeFragment;
import com.example.aw.sigap.fragment.HpcFragment;
import com.example.aw.sigap.fragment.HumidityFragment;
import com.example.aw.sigap.fragment.OtherFragment;
import com.example.aw.sigap.fragment.TemperatureFragment;
import com.example.aw.sigap.fragment.UkFragment;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.AllsData;
import com.example.aw.sigap.model.PredictionData;
import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DataHistoryActivity extends BaseActivity implements HumidityFragment.OnFragmentInteractionListener, TemperatureFragment.OnFragmentInteractionListener,
        OtherFragment.OnFragmentInteractionListener{
    private String TAG = DashboardActivity.class.getSimpleName();
    public static int UK = 1;
    public static int HPC = 2;
    public static int DURTIME = 3;
    public static int HPSP = 6;
    public static int HUMIDITY = 4;
    public static int TEMPERATURE = 5;
    public static String idnode;

    @Bind(R.id.tl_buddy_add)
    TabLayout tabLayout;
    @Bind(R.id.vp_buddy_add)
    ViewPager viewPager;
    ArrayList<String> valList = new ArrayList<String>();
    ArrayList<String> keyList = new ArrayList<String>();

    private ViewPagerAdapter adapter;
    public static List<AllData> allDatas;
    public static List<AllsData> allsDataList;
    public static List<PredictionData> predDatas;
    String id_alat, device, apiKey, ukk, hpsp, durtime;
    int numkeys;
    Toolbar toolbar;
    ProgressDialog pDialog;

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
        device = intent.getStringExtra("device");
        idnode = id_alat;
        allDatas = new ArrayList<AllData>();
        allsDataList = new ArrayList<AllsData>();
        predDatas = new ArrayList<PredictionData>();

//        predDatas.add(new PredictionData(Integer.toString(7), Integer.toString(3)));
//        predDatas.add(new PredictionData(Integer.toString(4), Integer.toString(8)));
//        predDatas.add(new PredictionData(Integer.toString(10), Integer.toString(2)));

        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        Log.d("api", apiKey);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        if(device.equalsIgnoreCase("597206a1487a43110490c0b0")) {
            getData();
        }
        else {
            Toast.makeText(this, "bukan sigap", Toast.LENGTH_SHORT).show();
            getOther();
        }
        //predictData(id_alat);
    }
    public void getData(){
        showPDialog();

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

//                            long dv = Long.valueOf(createdAt)*1000;// its need to be in milisecond
//                            Date df = new java.util.Date(dv);
//                            String vv = new SimpleDateFormat("MM dd, yyyy hh:mma").format(df);
                            DateTime dateTime = DateTime.parse(createdAt);
                            DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");
                            String strDateOnly = fmt.print(dateTime.plusHours(7));
                            long secondsSinceEpoch = dateTime.getMillis() / 1000;
                            Log.d("haha", Long.toString(secondsSinceEpoch));
                            AllData dataa = new AllData(ukk, hpc, humidity, temperature, hpsp, durtime, strDateOnly,
                                    Long.toString(secondsSinceEpoch), status);
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
        hidePDialog();
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
                        for (int count = 0; count < data.length(); count++) {
                            JSONObject dataObj = (JSONObject) data.get(count);
                            JSONObject setObj = new JSONObject(dataObj.getString("data"));
                            JSONObject setObj2 = new JSONObject(dataObj.getString("sensornode"));
                            //Log.i("dataDapat",""+setObj);
                            //AllsData allsData = new AllsData();
                            Iterator<String> keys = setObj.keys();
                            numkeys = 1;

                            try {
                                //String parameter
                                Class[] paramString = new Class[1];
                                paramString[0] = String.class;
                                String className = "com.example.aw.sigap.model.AllsData";
                                Class cls = Class.forName(className);
                                Object obj1 = cls.newInstance();

                                while (keys.hasNext()) {
                                    String keyValue = (String) keys.next();
                                    //Log.i("dataDapatkey",""+keyValue);
                                    keyList.add(keyValue);
                                    String Value = setObj.getString(keyValue);
                                    Log.i("dataDapatvalue", "" + Value);
                                    valList.add(Value);
                                    String methodName = "setSensor" + Integer.toString(numkeys);
                                    Log.i("dataDapatmethod", "" + methodName);

                                    Method method = cls.getDeclaredMethod(methodName, paramString);
                                    method.invoke(obj1, new String(Value));

                                    numkeys++;
                                }
                                for (int i = numkeys; i <= 8; i++) {
                                    String methodName = "setSensor" + Integer.toString(i);
                                    Log.i("dataDapatmethod", "" + methodName);

                                    Method method = cls.getDeclaredMethod(methodName, paramString);
                                    method.invoke(obj1, new String("10"));
                                    valList.add("10");
                                }
                                Log.i("dataDapatnumkeys", "" + numkeys);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            String createdAt = dataObj.getString("created_at");
                            DateTime dateTime = DateTime.parse(createdAt);
                            DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm:ss a");
                            String strDateOnly = fmt.print(dateTime);
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
                            Log.i("dataDapatvalList", "" + tujuh);
                            AllsData allsData = new AllsData(satu, dua, tiga, empat, lima, enam, tujuh, lapan, strDateOnly,
                                    Long.toString(secondsSinceEpoch), "0");
                            allsDataList.add(allsData);

                            adapter = new ViewPagerAdapter(getSupportFragmentManager());


                            for (int i = 0; i < numkeys - 1; i++) {
                                String keyy = keyList.get(i);
                                OtherFragment otherFragment = new OtherFragment();
                                adapter.addFragment(otherFragment, keyy);
                                Bundle args = new Bundle();
                                args.putString("param1", Integer.toString(i));
                                otherFragment.setArguments(args);
                            }
                            viewPager.setAdapter(adapter);

                            tabLayout.setupWithViewPager(viewPager);
                            for (int i = 0; i < numkeys - 1; i++) {
                                tabLayout.getTabAt(i).setIcon(R.drawable.thermometer);
                            }
                            keyList.clear();
                            valList.clear();
                        }


                    } else {
                        // error in fetching data
                        Toast.makeText(DataHistoryActivity.this, "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    //Toast.makeText(DetailActivity.this, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

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
        showPDialog();
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
                        String createdAt = dataObj.getString("time");

                        PredictionData pred = new PredictionData(senval, senval, createdAt);
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

        hidePDialog();
    }
    public static String getTimeStampOnWithoutTime(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String timestamp = "";
        try {
            Date date = format.parse(dateStr);

            format = new SimpleDateFormat("dd LLL, yyyy | HH:mm");
            String date1 = format.format(date);

            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> list;

        public TabsPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int index) {
            return list.get(index);
        }

        @Override
        public int getCount() {
            return list.size();
        }

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
}
