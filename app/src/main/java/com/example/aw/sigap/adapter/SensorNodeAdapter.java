package com.example.aw.sigap.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.aw.sigap.R;
import com.example.aw.sigap.activity.CreateSensorNode;
import com.example.aw.sigap.activity.DashboardActivity;
import com.example.aw.sigap.activity.DetailActivity;
import com.example.aw.sigap.activity.SensorNodes;
import com.example.aw.sigap.app.EndPoint;
import com.example.aw.sigap.app.MyApplication;
import com.example.aw.sigap.model.SensorNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AW on 5/16/2017.
 */

public class SensorNodeAdapter extends RecyclerView.Adapter<SensorNodeAdapter.MyViewHolder>{

    private Context mContext;
    private List<SensorNode> sensorNodeList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, type, count, notification;
        public ImageView thumbnail;
        public Button bDelete;
        public CardView cv;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            type = (TextView) view.findViewById(R.id.type);
            notification = (TextView) view.findViewById(R.id.notification);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            bDelete = (Button) view.findViewById(R.id.bDelete);
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
        if(sensorNode.getNotification() == 1) {
            holder.notification.setText(" Warning ");
            holder.notification.setBackgroundColor(Color.parseColor("#9575CD"));
        }
        else {
            holder.notification.setText(" Working ");
            holder.notification.setBackgroundColor(Color.parseColor("#27ae60"));
        }
        //Glide.with(mContext).load(SensorNode.getUrlImage()).into(holder.thumbnail);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                Toast.makeText(mContext, "pilih "+sensorNode.getNama(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id_alat", sensorNode.getId());
                intent.putExtra("device", sensorNode.getDevice());

                mContext.startActivity(intent);
            }
        });
        holder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "pilih "+sensorNode.getNama(), Toast.LENGTH_SHORT).show();
                String idnode = sensorNode.getId();
                String iddevice = sensorNode.getDevice();
                deleteSensorNode(idnode, iddevice);
            }
        });
    }
    @Override
    public int getItemCount() {
        return sensorNodeList.size();
    }

    public void deleteSensorNode(final String idnode, final String iddevice){
        //Toast.makeText(this, "HAHAHAHA", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE,
                EndPoint.URL_DELETE_NODE+"/"+idnode+"/remove", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getBoolean("error") == false) {
                        Toast.makeText(mContext, "" + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(mContext, DashboardActivity.class);
                        intent2.putExtra("id_alat", iddevice);
                        mContext.startActivity(intent2);

                    } else {
                        Toast.makeText(mContext, "" + obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {

                    Toast.makeText(mContext, "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Toast.makeText(mContext, "Volley errror: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                //headers.put("Authorization", apiKey);
                headers.put("x-snow-token", "SECRET_API_KEY");

                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }
        };
        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(stringRequest);
    }

}
