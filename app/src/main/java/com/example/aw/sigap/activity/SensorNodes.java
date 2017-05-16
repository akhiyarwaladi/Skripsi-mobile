package com.example.aw.sigap.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.aw.sigap.R;
import com.example.aw.sigap.adapter.SensorNodeAdapter;
import com.example.aw.sigap.model.SensorNode;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class SensorNodes extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SensorNodeAdapter adapter;
    private List<SensorNode> sensorNodeList;

    String Uk, apiKey, id_alat, latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_nodes);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        ButterKnife.bind(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        sensorNodeList = new ArrayList<>();
        adapter = new SensorNodeAdapter(this, sensorNodeList);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        prepareNodes();
    }

    private void prepareNodes() {

        sensorNodeList.add(new SensorNode("1", "Node1"));
        sensorNodeList.add(new SensorNode("2", "Node2"));
        sensorNodeList.add(new SensorNode("3", "Node3"));

        adapter.notifyDataSetChanged();

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
