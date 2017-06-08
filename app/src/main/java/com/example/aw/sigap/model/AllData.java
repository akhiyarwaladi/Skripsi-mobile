package com.example.aw.sigap.model;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by AW on 2/17/2017.
 */

public class AllData implements Serializable {
    String Ukk;
    String Hpc;
    String Humidity;
    String Temperature;
    String Hpsp;
    String Durtime;
    String CreatedAt;
    String TimeStamp;

    protected AllData(Parcel in) {
        Ukk = in.readString();
        Hpc = in.readString();
        Hpsp = in.readString();
        Humidity = in.readString();
        Temperature = in.readString();
        Durtime = in.readString();
        CreatedAt = in.readString();
        TimeStamp = in.readString();
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

    public AllData(String ukk, String hpc, String humidity, String temperature , String hpsp, String durtime, String createdAt, String timeStamp) {
        Ukk = ukk;
        Hpc = hpc;
        Humidity = humidity;
        Temperature = temperature;
        Hpsp = hpsp;
        Durtime = durtime;
        CreatedAt = createdAt;
        TimeStamp = timeStamp;
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

    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String humidity) {
        Humidity = humidity;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
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

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
