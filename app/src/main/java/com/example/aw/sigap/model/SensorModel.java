package com.example.aw.sigap.model;

/**
 * Created by AW on 2/19/2017.
 */

public class SensorModel {
    private String uk, hpc, hpsp, durtime;


    public SensorModel(String uk, String hpc, String hpsp, String durtime) {
        this.uk = uk;
        this.hpc = hpc;
        this.hpsp = hpsp;
        this.durtime = durtime;
    }

    public SensorModel(){

    }

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    public String getHpc() {
        return hpc;
    }

    public void setHpc(String hpc) {
        this.hpc = hpc;
    }

    public String getHpsp() {
        return hpsp;
    }

    public void setHpsp(String hpsp) {
        this.hpsp = hpsp;
    }

    public String getDurtime() {
        return durtime;
    }

    public void setDurtime(String durtime) {
        this.durtime = durtime;
    }
}
