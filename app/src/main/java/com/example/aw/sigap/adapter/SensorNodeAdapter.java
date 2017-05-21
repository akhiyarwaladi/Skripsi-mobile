package com.example.aw.sigap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.DetailActivity;
import com.example.aw.sigap.model.SensorNode;

import java.util.List;

/**
 * Created by AW on 5/16/2017.
 */

public class SensorNodeAdapter extends RecyclerView.Adapter<SensorNodeAdapter.MyViewHolder>{

    private Context mContext;
    private List<SensorNode> sensorNodeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, type, count;
        public ImageView thumbnail;
        public Button bDetail;
        public CardView cv;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            type = (TextView) view.findViewById(R.id.type);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            bDetail = (Button) view.findViewById(R.id.bDetail);
            cv = (CardView) view.findViewById(R.id.cv);
        }
    }

    public SensorNodeAdapter(Context mContext, List<SensorNode> adabList) {
        this.mContext = mContext;
        this.sensorNodeList = adabList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sensornodes, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SensorNode sensorNode = sensorNodeList.get(position);
        holder.title.setText(sensorNode.getNama());
        holder.type.setText(sensorNode.getTipe());
        holder.count.setText(sensorNode.getNama() + " songs");

        //Glide.with(mContext).load(SensorNode.getUrlImage()).into(holder.thumbnail);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                Toast.makeText(mContext, "pilih "+sensorNode.getNama(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id_node", sensorNode.getId());
                intent.putExtra("device", sensorNode.getDevice());

                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return sensorNodeList.size();
    }

}
