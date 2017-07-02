package com.example.aw.sigap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aw.sigap.MainActivity;
import com.example.aw.sigap.R;
import com.example.aw.sigap.model.AllData;
import com.example.aw.sigap.model.AllsData;

import java.util.List;

/**
 * Created by AW on 7/2/2017.
 */

public class OtherSensorAdapter extends RecyclerView.Adapter<OtherSensorAdapter.ViewHolder> {

    private List<AllsData> allsDataList;
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

    public OtherSensorAdapter(int stat, List<AllsData> moviesList) {
        this.allsDataList = moviesList;
        this.stat = stat;
    }

    @Override
    public OtherSensorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_data_list, parent, false);

        return new OtherSensorAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OtherSensorAdapter.ViewHolder holder, int position) {
        AllsData allsData = allsDataList.get(position);
        holder.no.setText(String.valueOf(position+1));
        if(stat == 1) holder.nilai.setText(allsData.getSensor1());
        else if (stat == 2) holder.nilai.setText(allsData.getSensor2());
        else if (stat == 3) holder.nilai.setText(allsData.getSensor3());
        else if (stat == 4) holder.nilai.setText(allsData.getSensor4());
        else if (stat == 5) holder.nilai.setText(allsData.getSensor5());
        else if (stat == 6) holder.nilai.setText(allsData.getSensor6());
        else holder.nilai.setText("");
        holder.waktu.setText(allsData.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return allsDataList.size();
    }

}
