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
    private int notification;

    public SensorNode(){

    }

    public SensorNode(String id, String nama, String device, String tipe, int notification) {
        this.id = id;
        this.nama = nama;
        this.device = device;
        this.tipe = tipe;
        this.notification = notification;
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

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }
}
