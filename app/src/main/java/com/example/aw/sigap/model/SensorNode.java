package com.example.aw.sigap.model;

import java.io.Serializable;

/**
 * Created by AW on 5/16/2017.
 */

public class SensorNode implements Serializable{
    private String id;
    private String nama;

    public SensorNode(){

    }

    public SensorNode(String id, String nama) {
        this.id = id;
        this.nama = nama;
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
}
