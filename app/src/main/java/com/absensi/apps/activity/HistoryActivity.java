package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.absensi.apps.R;
import com.absensi.apps.utils.ProgressButton;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressButton buttonHisAbs, buttonHisIzin, buttonHisKeg, buttonHisLembur;
    View btnHisAbs, btnHisIzin, btnHisKeg, btnHisLembur;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener((v -> onBackPressed()));

        btnHisAbs = findViewById(R.id.btn_his_absen);
        buttonHisAbs = new ProgressButton(HistoryActivity.this, btnHisAbs);
        buttonHisAbs.changeSizeButton(70);

        btnHisIzin = findViewById(R.id.btn_his_izin);
        buttonHisIzin = new ProgressButton(HistoryActivity.this, btnHisIzin);
        buttonHisIzin.changeSizeButton(70);

        btnHisKeg = findViewById(R.id.btn_his_keg);
        buttonHisKeg = new ProgressButton(HistoryActivity.this, btnHisKeg);
        buttonHisKeg.changeSizeButton(70);

        btnHisLembur = findViewById(R.id.btn_his_lembur);
        buttonHisLembur = new ProgressButton(HistoryActivity.this, btnHisLembur);
        buttonHisLembur.changeSizeButton(70);

        buttonHisAbs.setTextButton("History Absen", Color.WHITE, Color.parseColor("#A3CCE0"));
        buttonHisIzin.setTextButton("History Izin", Color.WHITE, Color.parseColor("#ADF0E8"));
        buttonHisKeg.setTextButton("History Kegiatan", Color.WHITE, Color.parseColor("#A3CCE0"));
        buttonHisLembur.setTextButton("History Lembur", Color.WHITE, Color.parseColor("#ADF0E8"));

        btnHisAbs.setOnClickListener(this);
        btnHisIzin.setOnClickListener(this);
        btnHisKeg.setOnClickListener(this);
        btnHisLembur.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_his_absen) {
            buttonHisAbs.buttonActivated();
            btnHisAbs.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(HistoryActivity.this, RiwayatAbsenActivity.class);

                buttonHisAbs.setTextButton("History Absen", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnHisAbs.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_his_izin) {
            buttonHisIzin.buttonActivated();
            btnHisIzin.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(HistoryActivity.this, RiwayatIzinActivity.class);

                buttonHisIzin.setTextButton("History Izin", Color.WHITE, Color.parseColor("#ADF0E8"));
                btnHisIzin.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_his_keg) {
            buttonHisKeg.buttonActivated();
            btnHisKeg.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(HistoryActivity.this, RiwayatKegiatanActivity.class);

                buttonHisKeg.setTextButton("History Kegiatan", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnHisKeg.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_his_lembur) {
            buttonHisLembur.buttonActivated();
            btnHisLembur.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(HistoryActivity.this, RiwayatLemburActivity.class);

                buttonHisLembur.setTextButton("History Lembur", Color.WHITE, Color.parseColor("#ADF0E8"));
                btnHisLembur.setEnabled(true);
                startActivity(intent);
            }, 500);
        }
    }
}