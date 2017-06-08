package com.example.aw.sigap.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.aw.sigap.R;
import com.example.aw.sigap.app.Config;
import com.example.aw.sigap.model.AllData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class EmptyData extends AppCompatActivity {
    private TextView tvDevice;
    private TextView tvNode;
    private TextView tvToken;
    private Toolbar toolbar;
    private String id_alat, id_device, apiKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_data);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id_alat = intent.getStringExtra("id_alat");
        id_device = intent.getStringExtra("device");

        final SharedPreferences sharedPreferencesApi = getSharedPreferences(Config.SHARED_PREF_API,
                Context.MODE_PRIVATE);
        apiKey = sharedPreferencesApi.getString(Config.APIKEY_SHARED_PREF, "");
        Log.d("api", apiKey);

        tvDevice = (TextView) findViewById(R.id.devID);
        tvNode = (TextView) findViewById(R.id.senID);
        tvToken = (TextView) findViewById(R.id.apiKey);

        tvDevice.setText(id_device);
        tvNode.setText(id_alat);
        tvToken.setText(apiKey);

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

        return super.onOptionsItemSelected(item);
    }
}