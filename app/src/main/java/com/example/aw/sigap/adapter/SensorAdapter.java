package com.example.aw.sigap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aw.sigap.MainActivity;
import com.example.aw.sigap.R;
import com.example.aw.sigap.model.AllData;

import java.util.List;

/**
 * Created by AW on 2/19/2017.
 */

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private List<AllData> allDataArrayList;
    private static String today;
    private int stat;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nilai, waktu, no;
        public ViewHolder(View view) {
            super(view);
            no = (TextView) view.findViewById(R.id.no);
            nilai = (TextView) view.findViewById(R.id.nilai);
            waktu = (TextView) view.findViewById(R.id.waktuu);
        }
    }

    public SensorAdapter(int stat, List<AllData> moviesList) {
        this.allDataArrayList = moviesList;
        this.stat = stat;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_data_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AllData allData = allDataArrayList.get(position);
        holder.no.setText(String.valueOf(position+1));
        if(stat == MainActivity.HPC) holder.nilai.setText(allData.getHpc());
        else if (stat == MainActivity.DURTIME) holder.nilai.setText(allData.getDurtime());
        else if (stat == MainActivity.UK) holder.nilai.setText(allData.getUkk());
        else if (stat == MainActivity.HPSP) holder.nilai.setText(allData.getHpsp());
        else holder.nilai.setText("");
        holder.waktu.setText(allData.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return allDataArrayList.size();
    }
}