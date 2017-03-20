package com.example.aw.sigap.model;

import java.io.Serializable;

/**
 * Created by AW on 2/15/2017.
 */

public class Alat implements Serializable {
    private String id;
    private String nama;
    private String kode;
    private String tglProduksi;
    private String latitude;
    private String longitude;
    private int rssi, battery;

    public Alat() {

    }

    public Alat(String id, String nama, String kode, String tglProduksi, String latitude, String longitude, int rssi, int battery) {
        this.id = id;
        this.nama = nama;
        this.kode = kode;
        this.tglProduksi = tglProduksi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rssi = rssi;
        this.battery = battery;
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

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getTglProduksi() {
        return tglProduksi;
    }

    public void setTglProduksi(String tglProduksi) {
        this.tglProduksi = tglProduksi;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }
}
