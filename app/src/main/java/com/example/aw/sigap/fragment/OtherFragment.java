package com.example.aw.sigap.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.DataHistoryActivity;
import com.example.aw.sigap.adapter.OtherSensorAdapter;
import com.example.aw.sigap.adapter.SensorAdapter;
import com.example.aw.sigap.helper.HourAxisValueFormatter;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.AllsData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OtherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static OtherSensorAdapter mAdapter;
    private RecyclerView recyclerView;
    View view;
    LineChart chartSuhu;
    float dur;
    long timee, timeeref, ref;
    ArrayList<String> valList = new ArrayList<String>();

    private OnFragmentInteractionListener mListener;

    public OtherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherFragment newInstance(String param1, String param2) {
        OtherFragment fragment = new OtherFragment();
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
        Log.i("param1", mParam1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_other, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_other);
        mAdapter = new OtherSensorAdapter(Integer.parseInt(mParam1)+1, DataHistoryActivity.allsDataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        setupChart();
        return view;
    }

    public void setupChart(){
        chartSuhu = (LineChart) view.findViewById(R.id.chart_other);
        updateChart();
    }

    public void updateChart(){

        ArrayList<Entry> entrySuhu = new ArrayList<>();
        ArrayList<String> labelSuhu = new ArrayList<>();

        entrySuhu.clear();
        labelSuhu.clear();

        for (int i=0; i< DataHistoryActivity.allsDataList.size(); i++){
            AllsData dat = DataHistoryActivity.allsDataList.get((DataHistoryActivity.allsDataList.size()-1) - i);

            if(Integer.parseInt(mParam1)+1 == 1) dur = Float.parseFloat(dat.getSensor1());
            else if(Integer.parseInt(mParam1)+1 == 2) dur = Float.parseFloat(dat.getSensor2());
            else if(Integer.parseInt(mParam1)+1 == 3) dur = Float.parseFloat(dat.getSensor3());
            else if(Integer.parseInt(mParam1)+1 == 4) dur = Float.parseFloat(dat.getSensor4());
            else if(Integer.parseInt(mParam1)+1 == 5) dur = Float.parseFloat(dat.getSensor5());
            else if(Integer.parseInt(mParam1)+1 == 6) dur = Float.parseFloat(dat.getSensor6());
            else if(Integer.parseInt(mParam1)+1 == 7) dur = Float.parseFloat(dat.getSensor7());

            String timestamp = dat.getTimeStamp();
            String createdAt = dat.getCreatedAt();
            createdAt = createdAt.substring(0, createdAt.length() - 6);
            timee = Long.parseLong(timestamp);
            if(i==0) ref= timee;
            timeeref = timee - ref;

            valList.add(createdAt);
            Log.d("timee", Long.toString(timee));
            Log.d("timeeref", Long.toString(timeeref));

            entrySuhu.add(new Entry(i, dur));

//            HourAxisValueFormatter hourAxisValueFormatter = new HourAxisValueFormatter(timeeref);
//            XAxis xAxis = chartSuhu.getXAxis();
//            xAxis.setValueFormatter(hourAxisValueFormatter);
        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet dataSetSuhu = new LineDataSet(entrySuhu, "second");
        dataSetSuhu.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSuhu.setColor(Color.parseColor("#009688"));
        dataSetSuhu.setCircleColor(Color.parseColor("#ffcdd2"));
        dataSetSuhu.setCircleColorHole(Color.parseColor("#f44336"));
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        dataSetSuhu.setDrawFilled(true);
        dataSetSuhu.setFillDrawable(drawable);

        lineDataSets.add(dataSetSuhu);
        LineData dataSuhu = new LineData(lineDataSets);

        XAxis xAxis = chartSuhu.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis)
            {
                System.out.println(value);
                if(((int)value)<valList.size())
                {
                    return  (valList.get((int)value));
                }
                else
                {
                    return "";
                }
            }
        });
        chartSuhu.setData(dataSuhu);



        chartSuhu.setVisibleXRangeMaximum(10); //set n data only to display
        chartSuhu.moveViewToX(DataHistoryActivity.allsDataList.size() - 10); //move view to 10 last data
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
}
