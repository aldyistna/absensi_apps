package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.absensi.apps.R;

public class RiwayatAbsenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_absen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Riwayat Absen");
        }
    }
}