package com.example.aw.sigap.model;

import java.io.Serializable;

/**
 * Created by AW on 4/12/2017.
 */

public class PredictionData implements Serializable{
    String suhu;
    String kelembaban;
    String createdAt;

    public PredictionData(){

    }

    public PredictionData(String suhu, String kelembaban, String createdAt) {
        this.suhu = suhu;
        this.kelembaban = kelembaban;
        this.createdAt = createdAt;
    }

    public String getSuhu() {
        return suhu;
    }

    public void setSuhu(String suhu) {
        this.suhu = suhu;
    }

    public String getKelembaban() {
        return kelembaban;
    }

    public void setKelembaban(String kelembaban) {
        this.kelembaban = kelembaban;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
