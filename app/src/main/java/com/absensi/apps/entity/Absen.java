package com.absensi.apps.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Absen {
    private String id;
    private String nik;
    private String location_in;
    private String location_out;
    private String time_in;
    private String time_out;

//    public Absen() {}

    public Absen(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.nik = object.getString("nik");
            this.location_in = object.getString("location_in");
            this.location_out = object.getString("location_out");
            this.time_in = object.getString("time_in");
            this.time_out = object.getString("time_out");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getLocation_in() {
        return location_in;
    }

    public String getLocation_out() {
        return location_out;
    }

    public String getTime_in() {
        return time_in;
    }

    public String getTime_out() {
        return time_out;
    }
}
