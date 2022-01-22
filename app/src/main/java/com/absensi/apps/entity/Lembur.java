package com.absensi.apps.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Lembur {
    private String id;
    private String nik;
    private String date;
    private String keterangan;

//    public Lembur() {}

    public Lembur(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.nik = object.getString("nik");
            this.date = object.getString("date");
            this.keterangan = object.getString("keterangan");
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKeterangan() {
        return keterangan;
    }
}
