package com.example.aw.sigap.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.DataHistoryActivity;
import com.example.aw.sigap.adapter.SensorAdapter;
import com.example.aw.sigap.model.AllData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by AW on 2/17/2017.
 */

public class UkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static SensorAdapter mAdapter;
    private RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v;
    LineChart chartSuhu, chartPH, chartDO;

    private OnFragmentInteractionListener mListener;

    public UkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuhuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UkFragment newInstance(String param1, String param2) {
        UkFragment fragment = new UkFragment();
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
        v = inflater.inflate(R.layout.fragment_uk, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_uk);
        mAdapter = new SensorAdapter(DataHistoryActivity.UK, DataHistoryActivity.allDatas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        setupChart();
        return v;
        // Inflate the layout for this fragment
    }

    private void setupChart(){
        // Setup chart suhu
        chartSuhu = (LineChart) v.findViewById(R.id.chart_uk);
//        chartPH = (LineChart) view.findViewById(R.id.chart_ph);
//        chartDO = (LineChart) view.findViewById(R.id.chart_do);
        chartSuhu.setDescription("");
//        chartPH.setDescription("");
//        chartDO.setDescription("");

        updateChart();

    }

    private void updateChart(){

        ArrayList<Entry> entrySuhu = new ArrayList<>();
        ArrayList<String> labelSuhu = new ArrayList<>();

        entrySuhu.clear();
        labelSuhu.clear();

        // Date formater
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
        Date parsed = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        dateFormat.setTimeZone(TimeZone.getDefault());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getDefault());
        // end date formater

        for(int i = 0; i<DataHistoryActivity.allDatas.size();i++){
            AllData dat = DataHistoryActivity.allDatas.get(i);
            float dooo = Float.parseFloat(dat.getHpc());
            entrySuhu.add(new Entry(dooo,i));
            labelSuhu.add(String.valueOf(i+1));
        }

        LineDataSet dataSetSuhu = new LineDataSet(entrySuhu, "1 <= x <= -1");
        dataSetSuhu.setColor(Color.parseColor("#009688"));
        dataSetSuhu.setCircleColor(Color.parseColor("#ffcdd2"));
        dataSetSuhu.setCircleColorHole(Color.parseColor("#f44336"));

        LineData dataSuhu = new LineData(labelSuhu, dataSetSuhu);

        chartSuhu.setData(dataSuhu);

        // Update data
        chartSuhu.notifyDataSetChanged();

        // Animate
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
