package com.absensi.apps.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Karyawan implements Parcelable {
    private String id;
    private String nik;
    private String name;
    private String posisi;
    private String jabatan;
    private String username;

    public Karyawan() {}

    public Karyawan(JSONObject object) {
        try {
            this.id = object.getString("id");
            this.nik = object.getString("nik");
            this.name = object.getString("name");
            this.posisi = object.getString("posisi");
            this.jabatan = object.getString("jabatan");
            this.username = object.getString("username");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Karyawan(Parcel in) {
        id = in.readString();
        nik = in.readString();
        name = in.readString();
        posisi = in.readString();
        jabatan = in.readString();
        username = in.readString();
    }

    public static final Creator<Karyawan> CREATOR = new Creator<Karyawan>() {
        @Override
        public Karyawan createFromParcel(Parcel in) {
            return new Karyawan(in);
        }

        @Override
        public Karyawan[] newArray(int size) {
            return new Karyawan[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosisi() {
        return posisi;
    }

    public String getJabatan() {
        return jabatan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nik);
        dest.writeString(name);
        dest.writeString(posisi);
        dest.writeString(jabatan);
        dest.writeString(username);
    }
}
