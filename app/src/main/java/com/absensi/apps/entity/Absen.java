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
    private String image_in;
    private String image_out;

    public Absen() {}

    public Absen(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.nik = object.getString("nik");
            this.location_in = object.getString("location_in");
            this.location_out = object.getString("location_out");
            this.time_in = object.getString("time_in");
            this.time_out = object.getString("time_out");
            this.image_in = object.getString("url_photo_in");
            this.image_out = object.getString("url_photo_out");
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

    public void setLocation_in(String location_in) {
        this.location_in = location_in;
    }

    public String getLocation_out() {
        return location_out;
    }

    public void setLocation_out(String location_out) {
        this.location_out = location_out;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public String getImage_in() {
        return image_in;
    }

    public void setImage_in(String image_in) {
        this.image_in = image_in;
    }

    public String getImage_out() {
        return image_out;
    }

    public void setImage_out(String image_out) {
        this.image_out = image_out;
    }
}
