package com.absensi.apps.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Attendance {
    private String id_absen;
    private String nik;
    private String absen_in;
    private String absen_out;
    private String izin;
    private String lembur;

    public Attendance() {}

    public Attendance(JSONObject object) {
        try {
            this.id_absen = object.getString("id_absen");
            this.nik = object.getString("nik");
            this.absen_in = object.getString("absen_in");
            this.absen_out = object.getString("absen_out");
            this.izin = object.getString("izin");
            this.lembur = object.getString("lembur");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId_absen() {
        return id_absen;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getAbsen_in() {
        return absen_in;
    }

    public String getAbsen_out() {
        return absen_out;
    }

    public String getIzin() {
        return izin;
    }

    public String getLembur() {
        return lembur;
    }
}
