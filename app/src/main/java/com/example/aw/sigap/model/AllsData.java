package com.example.aw.sigap.model;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by AW on 2/17/2017.
 */

public class AllsData implements Serializable {
    String Sensor1, Sensor2, Sensor3, Sensor4, Sensor5, Sensor6, Sensor7, Sensor8, CreatedAt, TimeStamp, Status;

    public AllsData(Parcel in) {
        Sensor1 = in.readString();
        Sensor2 = in.readString();
        Sensor3 = in.readString();
        Sensor4 = in.readString();
        Sensor5 = in.readString();
        Sensor6 = in.readString();
        Sensor7 = in.readString();
        Sensor8 = in.readString();
        CreatedAt = in.readString();
        TimeStamp = in.readString();
        Status = in.readString();
    }

    public AllsData() {

    }

    public AllsData(String sensor1, String sensor2, String sensor3, String sensor4, String sensor5, String sensor6, String sensor7,
                    String sensor8, String createdAt, String timeStamp, String status) {
        Sensor1 = sensor1;
        Sensor2 = sensor2;
        Sensor3 = sensor3;
        Sensor4 = sensor4;
        Sensor5 = sensor5;
        Sensor6 = sensor6;
        Sensor7 = sensor7;
        Sensor8 = sensor8;
        CreatedAt = createdAt;
        TimeStamp = timeStamp;
        Status = status;
    }


    public String getSensor1() {
        return Sensor1;
    }

    public void setSensor1(String sensor1) {
        Sensor1 = sensor1;
    }

    public String getSensor2() {
        return Sensor2;
    }

    public void setSensor2(String sensor2) {
        Sensor2 = sensor2;
    }

    public String getSensor3() {
        return Sensor3;
    }

    public void setSensor3(String sensor3) {
        Sensor3 = sensor3;
    }

    public String getSensor4() {
        return Sensor4;
    }

    public void setSensor4(String sensor4) {
        Sensor4 = sensor4;
    }

    public String getSensor5() {
        return Sensor5;
    }

    public void setSensor5(String sensor5) {
        Sensor5 = sensor5;
    }

    public String getSensor6() {
        return Sensor6;
    }

    public void setSensor6(String sensor6) {
        Sensor6 = sensor6;
    }

    public String getSensor7() {
        return Sensor7;
    }

    public void setSensor7(String sensor7) {
        Sensor7 = sensor7;
    }

    public String getSensor8() {
        return Sensor8;
    }

    public void setSensor8(String sensor8) {
        Sensor8 = sensor8;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

}
