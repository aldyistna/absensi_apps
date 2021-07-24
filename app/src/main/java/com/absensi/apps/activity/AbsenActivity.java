package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextClock;
import android.widget.TextView;

import com.absensi.apps.R;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;

public class AbsenActivity extends AppCompatActivity {

    public static final String EXTRA_ABSEN = "extra_absen";

//    private SPManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_ABSEN));
        }

        SPManager spManager = new SPManager(this);

        TextClock textClock = findViewById(R.id.text_clock);
        textClock.setFormat12Hour(null);
        textClock.setFormat24Hour("dd-MMM-yyyy HH:mm:ss");

        TextView txtNIK = findViewById(R.id.txt_nik);
        TextView txtName = findViewById(R.id.txt_name);

        Gson gson = new Gson();
        String json = spManager.getSpUser();
        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        txtNIK.setText(karyawan.getNik());
        txtName.setText(karyawan.getName());
    }
}