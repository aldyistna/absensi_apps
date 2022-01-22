package com.absensi.apps.entity;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Kegiatan {
    private String id;
    private String nik;
    private String date;
    private String time;
    private String foto;
    private String keterangan;

    public Kegiatan() {}

    @SuppressLint("SimpleDateFormat")
    public Kegiatan(JSONObject object) {

        DateFormat df1 = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        try {
            this.id = object.getString("id");
            this.nik = object.getString("nik");
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(object.getString("date"));
            assert d != null;
            this.date = df1.format(d);
            this.time = df2.format(d);
            this.foto = object.getString("url_photo");
            this.keterangan = object.getString("keterangan");
        } catch (JSONException | ParseException e) {
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

    public String getTime() {
        return time;
    }

    public String getFoto() {
        return foto;
    }

    public String getKeterangan() {
        return keterangan;
    }

}
