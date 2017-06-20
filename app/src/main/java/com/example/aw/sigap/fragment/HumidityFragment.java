package com.example.aw.sigap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.DashboardActivity;
import com.example.aw.sigap.activity.DataHistoryActivity;
import com.example.aw.sigap.activity.DetailActivity;
import com.example.aw.sigap.adapter.SensorAdapter;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.PredictionData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HumidityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HumidityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HumidityFragment extends Fragment {
    private String TAG = DashboardActivity.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idnode;

    private OnFragmentInteractionListener mListener;
    public static SensorAdapter mAdapter;
    private RecyclerView recyclerView;

    private Button btnpredict;
    public static List<PredictionData> predDatas;
    ProgressDialog pDialog;
    private int numData;
    View view;
    LineChart chartSuhu;
    public HumidityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HumidityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HumidityFragment newInstance(String param1, String param2) {
        HumidityFragment fragment = new HumidityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_humidity, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_humidity);
        numData = DataHistoryActivity.allDatas.size();
        idnode = DataHistoryActivity.idnode;
        Log.d("idfragment", idnode);

        mAdapter = new SensorAdapter(DataHistoryActivity.HUMIDITY, DataHistoryActivity.allDatas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        setupChart();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(true);

        predDatas = new ArrayList<PredictionData>();
        btnpredict = (Button)view.findViewById(R.id.bpredict);
        btnpredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupPredict();

            }
        });

        return view;

    }
    private void setupPredict(){
        showPDialog();
        predictData(idnode);


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
                    if (obj.getBoolean("error") == false) {
                        JSONArray data = obj.getJSONArray("prediction");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dataObj = (JSONObject) data.get(i);
                            Log.i("prediksiDapat", "" + dataObj);
                            String senval = dataObj.getString("senVal");

                            PredictionData pred = new PredictionData(senval, senval);
                            predDatas.add(pred);
                        }
                        chartSuhu = (LineChart) view.findViewById(R.id.chart_humidity);
                        updatePredict();
                        hidePDialog();
                    }
                    else{
                        Toast.makeText(getActivity(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getActivity(), "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();

                headers.put("x-snow-token", "SECRET_API_KEY");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idnode", idalat);
                params.put("tipe", "humidity");
                return params;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);

    }

    private void setupChart(){

        chartSuhu = (LineChart) view.findViewById(R.id.chart_humidity);
        updateChart();
    }

    private void updateChart(){
        ArrayList<Entry> entrySuhu = new ArrayList<>();
        ArrayList<String> labelSuhu = new ArrayList<>();


        entrySuhu.clear();
        labelSuhu.clear();

        for(int i = 0; i<DataHistoryActivity.allDatas.size(); i++){
            //get last data first because api sort by date
            AllData dat = DataHistoryActivity.allDatas.get((DataHistoryActivity.allDatas.size()-1) - i);
            float humid = Float.parseFloat(dat.getHumidity());

            //String timestamp = dat.getCreatedAt();
            //long timee = Long.parseLong(timestamp);
            entrySuhu.add(new Entry(i, humid));
            //labelSuhu.add(String.valueOf(i+1));
        }


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet dataSetSuhu = new LineDataSet(entrySuhu, "%now");
        dataSetSuhu.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSuhu.setColor(Color.parseColor("#009688"));
        dataSetSuhu.setCircleColor(Color.parseColor("#ffcdd2"));
        dataSetSuhu.setCircleColorHole(Color.parseColor("#f44336"));
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        dataSetSuhu.setFillDrawable(drawable);
        dataSetSuhu.setDrawFilled(true);

        lineDataSets.add(dataSetSuhu);

        LineData dataSuhu = new LineData(lineDataSets);
        chartSuhu.setData(dataSuhu);
        chartSuhu.setVisibleXRangeMaximum(10); //set n data only to display
        chartSuhu.moveViewToX((numData) - 10); //move view to 10 last data
        chartSuhu.notifyDataSetChanged();
        chartSuhu.animateY(1000);
    }

    private void updatePredict(){

        ArrayList<Entry> entrySuhu = new ArrayList<>();
        ArrayList<String> labelSuhu = new ArrayList<>();
        ArrayList<Entry> entrySuhuPred = new ArrayList<>();

        entrySuhu.clear();
        labelSuhu.clear();
        entrySuhuPred.clear();


        for(int i = 0; i<DataHistoryActivity.allDatas.size(); i++){
            //get last data first because api sort by date
            AllData dat = DataHistoryActivity.allDatas.get((DataHistoryActivity.allDatas.size()-1) - i);
            float humid = Float.parseFloat(dat.getHumidity());

            //String timestamp = dat.getCreatedAt();
            //long timee = Long.parseLong(timestamp);
            entrySuhu.add(new Entry(i, humid));
            //labelSuhu.add(String.valueOf(i+1));
        }

        for(int i = 0; i<predDatas.size(); i++){
            if (i == 0){
                AllData dat = DataHistoryActivity.allDatas.get(i);
                float humid = Float.parseFloat(dat.getHumidity());
                entrySuhuPred.add(new Entry((i - 1 + numData), humid));
            }
            else {
                PredictionData pred = predDatas.get(i);
                float kelembaban = Float.parseFloat(pred.getKelembaban());
                entrySuhuPred.add(new Entry((i - 1 + numData), kelembaban));
            }
        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet dataSetSuhu = new LineDataSet(entrySuhu, "%now");
        dataSetSuhu.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSuhu.setColor(Color.parseColor("#009688"));
        dataSetSuhu.setCircleColor(Color.parseColor("#ffcdd2"));
        dataSetSuhu.setCircleColorHole(Color.parseColor("#f44336"));
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        dataSetSuhu.setFillDrawable(drawable);
        dataSetSuhu.setDrawFilled(true);


        LineDataSet dataSetSuhuPred = new LineDataSet(entrySuhuPred, "%prediction");
        dataSetSuhuPred.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSuhuPred.setColor(Color.parseColor("#0000FF"));
        dataSetSuhuPred.setCircleColor(Color.parseColor("#9575CD"));
        dataSetSuhuPred.setCircleColorHole(Color.parseColor("#27ae60"));
        dataSetSuhuPred.setDrawFilled(true);

        lineDataSets.add(dataSetSuhu);
        lineDataSets.add(dataSetSuhuPred);


        LineData dataSuhu = new LineData(lineDataSets);
        chartSuhu.setData(dataSuhu);
        chartSuhu.setVisibleXRangeMaximum(10); //set n data only to display
        chartSuhu.moveViewToX((numData+entrySuhuPred.size()) - 10); //move view to 10 last data
        chartSuhu.notifyDataSetChanged();
        chartSuhu.animateY(1000);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
