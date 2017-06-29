package com.example.aw.sigap.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aw.sigap.R;

public class DeviceNotWorking extends AppCompatActivity {
    Button bBackDashboard;
    TextView tvMessage, tvNotWorkingDevice;
    String message, device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_not_working);

        final Intent intent = getIntent();
        device = intent.getStringExtra("notworkingdevice");
        message = intent.getStringExtra("message");

        tvNotWorkingDevice = (TextView) findViewById(R.id.tvNotWorkingDevice);
        tvNotWorkingDevice.setText(device);
        tvMessage = (TextView)findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        bBackDashboard = (Button)findViewById(R.id.bBackDashboard);
        bBackDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceNotWorking.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

    }
}
