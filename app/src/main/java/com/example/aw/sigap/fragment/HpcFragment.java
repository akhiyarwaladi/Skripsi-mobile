package com.example.aw.sigap.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.example.aw.sigap.adapter.SensorAdapter;
import com.example.aw.sigap.helper.HourAxisValueFormatter;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.PredictionData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by AW on 2/17/2017.
 */

public class HpcFragment extends Fragment {

    public static SensorAdapter mAdapter;
    private RecyclerView recyclerView;
    private int numData;
    View view;
    LineChart chartSuhu, chartPH, chartDO;
    ArrayList<String> valList = new ArrayList<String>();
    public HpcFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hpc, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_hpc);
        numData = DataHistoryActivity.allDatas.size();
        mAdapter = new SensorAdapter(DataHistoryActivity.HPC, DataHistoryActivity.allDatas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        setupChart();

        return view;
    }

    private void setupChart(){
        chartSuhu = (LineChart) view.findViewById(R.id.chart_hpc);
        updateChart();
    }

    private void updateChart(){

        ArrayList<Entry> entrySuhu = new ArrayList<>();
        ArrayList<String> labelSuhu = new ArrayList<>();
        ArrayList<Entry> entrySuhuPred = new ArrayList<>();

        entrySuhu.clear();
        labelSuhu.clear();
        entrySuhuPred.clear();


        for(int i = 0; i<DataHistoryActivity.allDatas.size(); i++){
            AllData dat = DataHistoryActivity.allDatas.get((DataHistoryActivity.allDatas.size()-1) - i);
            float hpc = Float.parseFloat(dat.getHpc());
            String createdAt = dat.getCreatedAt();
            createdAt = createdAt.substring(0, createdAt.length() - 6);
            valList.add(createdAt);
            //String timestamp = dat.getCreatedAt();
            //long timee = Long.parseLong(timestamp);
            entrySuhu.add(new Entry(i, hpc));
            //labelSuhu.add(String.valueOf(i+1));
        }
//        for(int i = 0; i<DataHistoryActivity.predDatas.size(); i++){
//            PredictionData pred = DataHistoryActivity.predDatas.get(i);
//            float suhu = Float.parseFloat(pred.getKelembaban());
//            entrySuhuPred.add(new Entry((i+numData), suhu));
//        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet dataSetSuhu = new LineDataSet(entrySuhu, "cm");
        dataSetSuhu.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSetSuhu.setColor(Color.parseColor("#009688"));
        dataSetSuhu.setCircleColor(Color.parseColor("#ffcdd2"));
        dataSetSuhu.setCircleColorHole(Color.parseColor("#f44336"));
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_red);
        dataSetSuhu.setFillDrawable(drawable);
        dataSetSuhu.setDrawFilled(true);


//        LineDataSet dataSetSuhuPred = new LineDataSet(entrySuhuPred, "cm");
//        dataSetSuhuPred.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSetSuhuPred.setColor(Color.parseColor("#0000FF"));
//        dataSetSuhuPred.setCircleColor(Color.parseColor("#9575CD"));
//        dataSetSuhuPred.setCircleColorHole(Color.parseColor("#27ae60"));
//        dataSetSuhuPred.setDrawFilled(true);

        lineDataSets.add(dataSetSuhu);
//        lineDataSets.add(dataSetSuhuPred);



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
        chartSuhu.moveViewToX((numData+entrySuhuPred.size()) - 10); //move view to 10 last data
//        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(1451660400);
//        XAxis xAxis = chartSuhu.getXAxis();
//        xAxis.setValueFormatter(xAxisFormatter);
        chartSuhu.notifyDataSetChanged();
        chartSuhu.animateY(1000);

    }
//    public static long getTimeStampOnWithoutTime(String dateStr) {
//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long timee = 0;
//        String str = "";
//        try {
//            Date date = (Date)formatter.parse(dateStr);
//            timee = date.getTime()/1000L;
//            //str=Long.toString(timee);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return timee;
//    }
}