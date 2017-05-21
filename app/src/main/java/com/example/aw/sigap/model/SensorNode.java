package com.example.aw.sigap.model;

import java.io.Serializable;

/**
 * Created by AW on 5/16/2017.
 */

public class SensorNode implements Serializable{
    private String id;
    private String nama;
    private String device;
    private String tipe;

    public SensorNode(){

    }

    public SensorNode(String id, String nama, String device, String tipe) {
        this.id = id;
        this.nama = nama;
        this.device = device;
        this.tipe = tipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
