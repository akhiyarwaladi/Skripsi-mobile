package com.example.aw.sigap.model;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by AW on 2/17/2017.
 */

public class AllData implements Serializable {
    String Ukk;
    String Hpc;
    String Hpsp;
    String Durtime;
    String CreatedAt;

    protected AllData(Parcel in) {
        Ukk = in.readString();
        Hpc = in.readString();
        Hpsp = in.readString();
        Durtime = in.readString();
        CreatedAt = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    int unread;

    public AllData() {

    }

    public AllData(String ukk, String hpc, String hpsp, String durtime, String createdAt) {
        Ukk = ukk;
        Hpc = hpc;
        Hpsp = hpsp;
        Durtime = durtime;
        CreatedAt = createdAt;
    }


    public String getUkk() {
        return Ukk;
    }

    public void setUkk(String ukk) {
        Ukk = ukk;
    }

    public String getHpc() {
        return Hpc;
    }

    public void setHpc(String hpc) {
        Hpc = hpc;
    }

    public String getHpsp() {
        return Hpsp;
    }

    public void setHpsp(String hpsp) {
        Hpsp = hpsp;
    }

    public String getDurtime() {
        return Durtime;
    }

    public void setDurtime(String durtime) {
        Durtime = durtime;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }
}
