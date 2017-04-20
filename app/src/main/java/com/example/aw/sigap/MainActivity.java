package com.example.aw.sigap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.aw.sigap.model.SensorModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int UK = 1;
    public static int HPC = 2;
    public static int DURTIME = 3;
    public static int HPSP = 6;
    public static int HUMIDITY = 4;
    public static int TEMPERATURE = 5;
    public static List<SensorModel> allDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
