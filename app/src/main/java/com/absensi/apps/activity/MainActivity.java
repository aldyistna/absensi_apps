package com.absensi.apps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.R;
import com.absensi.apps.entity.Attendance;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.utils.ProgressButton;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    private SPManager spManager;
    ProgressButton buttonLogout, buttonAbsen, buttonRiwayat, buttonIzin, buttonKeg, buttonLembur;
    ProgressBar progressBar;
    TextView txtPesan, txtPesan2;
    View btnAbsen, btnLogout, btnRiwayat, btnIzin, btnKeg, btnLembur;

    String txtAbsen = "";

    LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    Resources r;
    int left, top, right, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        r = this.getResources();
        left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
        top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
        right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        spManager = new SPManager(this);

        if (!spManager.getSPLogin()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        } else {
            checkPermission();
            Gson gson = new Gson();
            String json = spManager.getSpUser();

            Karyawan karyawan = gson.fromJson(json, Karyawan.class);

            TextView txtNIK = findViewById(R.id.txt_nik);
            TextView txtName = findViewById(R.id.txt_kar);
            TextView txtJab = findViewById(R.id.txt_jab);
            TextView txtPos = findViewById(R.id.txt_posisi);
            txtPesan = findViewById(R.id.txt_absen);
            txtPesan2 = findViewById(R.id.txt_pesan);

            btnLogout = findViewById(R.id.btn_keluar);
            buttonLogout = new ProgressButton(MainActivity.this, btnLogout);
            buttonLogout.changeSizeButton(50);
            btnAbsen = findViewById(R.id.btn_absen);
            buttonAbsen = new ProgressButton(MainActivity.this, btnAbsen);
            buttonAbsen.changeSizeButton(50);
            btnIzin = findViewById(R.id.btn_izin);
            buttonIzin = new ProgressButton(MainActivity.this, btnIzin);
            buttonIzin.changeSizeButton(50);
            btnKeg = findViewById(R.id.btn_kegiatan);
            buttonKeg = new ProgressButton(MainActivity.this, btnKeg);
            buttonKeg.changeSizeButton(50);
            btnLembur = findViewById(R.id.btn_lembur);
            buttonLembur = new ProgressButton(MainActivity.this, btnLembur);
            buttonLembur.changeSizeButton(50);
            btnRiwayat = findViewById(R.id.btn_riwayat);
            buttonRiwayat = new ProgressButton(MainActivity.this, btnRiwayat);
            buttonRiwayat.changeSizeButton(50);
            progressBar = findViewById(R.id.progress_bar);

            buttonAbsen.setTextButton(txtAbsen, Color.WHITE, Color.parseColor("#A3CCE0"));
            buttonRiwayat.setTextButton("History", Color.WHITE, Color.parseColor("#ADF0E8"));
            buttonIzin.setTextButton("Izin", Color.WHITE, Color.parseColor("#A3CCE0"));
            buttonKeg.setTextButton("Kegiatan", Color.WHITE, Color.parseColor("#ADF0E8"));
            buttonLembur.setTextButton("Lembur", Color.WHITE, Color.parseColor("#A3CCE0"));
            buttonLogout.setTextButton("Logout", Color.WHITE, Color.parseColor("#ADF0E8"));

            txtNIK.setText(karyawan.getNik());
            txtName.setText(karyawan.getName());
            txtPos.setText(karyawan.getPosisi());
            txtJab.setText(karyawan.getJabatan());

            if (spManager.getSpStatusAbsen().equals("")) {
                chekcAbsenMasuk(karyawan.getNik());
            } else {
                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                if (date.equals(spManager.getSpDateAbsen())) {
                    switch (spManager.getSpStatusAbsen()) {
                        case "BELUM_ABSEN":
                            txtPesan.setText(getString(R.string.msg_blm_absen));
                            txtPesan2.setText(getString(R.string.msg_blm_absen_sub));
                            txtAbsen = "Absen Masuk";
                            break;
                        case "MASUK":
                            txtPesan.setText(getString(R.string.msg_absen_pulang));
                            txtPesan2.setText(getString(R.string.msg_absen_pulang_sub));
                            txtAbsen = "Absen Pulang";
                            break;
                        case "PULANG":
                            txtPesan.setText(getString(R.string.msg_sudah_absen));
                            txtPesan2.setText(getString(R.string.msg_sudah_absen_sub));
                            btnAbsen.setVisibility(View.GONE);
                            btnIzin.setVisibility(View.GONE);
                            btnKeg.setVisibility(View.GONE);
                            btnLembur.setVisibility(View.GONE);

                            lp.setMargins(left, top, right, bottom);
                            btnLogout.setLayoutParams(lp);
                            break;
                    }
                    if (spManager.getSpStatusIzin().equals("Y")) {
                        txtPesan.setText(getString(R.string.msg_izin));
                        txtPesan2.setText(getString(R.string.msg_izin_sub));
                        btnIzin.setVisibility(View.GONE);
                        btnAbsen.setVisibility(View.GONE);
                        btnKeg.setVisibility(View.GONE);
                        btnLembur.setVisibility(View.GONE);

                        lp.setMargins(left, top, right, bottom);
                        btnLogout.setLayoutParams(lp);
                    }
                    if (spManager.getSpStatusLembur().equals("Y")) {
                        btnLembur.setVisibility(View.GONE);
                    }
                    changeButtonColor();
                } else {
                    chekcAbsenMasuk(karyawan.getNik());
                }
            }
            btnAbsen.setOnClickListener(this);
            btnIzin.setOnClickListener(this);
            btnKeg.setOnClickListener(this);
            btnLembur.setOnClickListener(this);
            btnLogout.setOnClickListener(this);
            btnRiwayat.setOnClickListener(this);
        }
    }

    private void chekcAbsenMasuk(String nik) {
        txtPesan = findViewById(R.id.txt_absen);
        txtPesan2 = findViewById(R.id.txt_pesan);
        txtPesan.setVisibility(View.GONE);
        txtPesan2.setVisibility(View.GONE);
        AsyncHttpClient client = new AsyncHttpClient();

        progressBar.setVisibility(View.VISIBLE);
        buttonAbsen.buttonActivated();
        btnAbsen.setEnabled(false);
        buttonIzin.buttonActivated();
        btnIzin.setEnabled(false);
        buttonKeg.buttonActivated();
        btnKeg.setEnabled(false);
        buttonLembur.buttonActivated();
        btnLembur.setEnabled(false);
        buttonLogout.buttonActivated();
        btnLogout.setEnabled(false);
        buttonRiwayat.buttonActivated();
        btnRiwayat.setEnabled(false);

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String paramQuery = "?date=" + timeStamp + "&nik=" + nik;

        client.get(API_URL + "api/attendance" + paramQuery, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resObject = new JSONObject(result);
                    JSONArray list = resObject.getJSONArray("data");

                    @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    spManager.saveString(SPManager.SP_DATE_ABSEN, date);

                    if (list.length() > 0) {
                        Attendance attendance = new Attendance();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject = list.getJSONObject(i);
                            attendance = new Attendance(jsonObject);
                        }

                        if (attendance.getAbsen_in().equals("1")) {
                            txtPesan.setText(getString(R.string.msg_absen_pulang));
                            txtPesan2.setText(getString(R.string.msg_absen_pulang_sub));
                            spManager.saveString(SPManager.SP_STATUS_ABSEN, "MASUK");
                            spManager.saveInt(SPManager.SP_ID_ABSEN, Integer.parseInt(attendance.getId_absen()));
                            txtAbsen = "Absen Pulang";
                        } else if (attendance.getAbsen_out().equals("1")){
                            txtPesan.setText(getString(R.string.msg_sudah_absen));
                            txtPesan2.setText(getString(R.string.msg_sudah_absen_sub));
                            spManager.saveString(SPManager.SP_STATUS_ABSEN, "PULANG");
                            btnAbsen.setVisibility(View.GONE);
                            btnIzin.setVisibility(View.GONE);
                            btnKeg.setVisibility(View.GONE);
                            btnLembur.setVisibility(View.GONE);

                            lp.setMargins(left, top, right, bottom);
                            btnLogout.setLayoutParams(lp);
                        } else {
                            txtPesan.setText(getString(R.string.msg_blm_absen));
                            txtPesan2.setText(getString(R.string.msg_blm_absen_sub));
                            spManager.saveString(SPManager.SP_STATUS_ABSEN, "BELUM_ABSEN");
                            txtAbsen = "Absen Masuk";
                        }

                        if (attendance.getIzin().equals("1")) {
                            txtPesan.setText(getString(R.string.msg_izin));
                            txtPesan2.setText(getString(R.string.msg_izin_sub));
                            spManager.saveString(SPManager.SP_STATUS_IZIN, "Y");
                            btnIzin.setVisibility(View.GONE);
                            btnAbsen.setVisibility(View.GONE);
                            btnKeg.setVisibility(View.GONE);
                            btnLembur.setVisibility(View.GONE);

                            lp.setMargins(left, top, right, bottom);
                            btnLogout.setLayoutParams(lp);
                        } else {
                            spManager.saveString(SPManager.SP_STATUS_IZIN, "N");
                        }
                        if (attendance.getLembur().equals("1")) {
                            spManager.saveString(SPManager.SP_STATUS_LEMBUR, "Y");
                            btnLembur.setVisibility(View.GONE);
                        } else {
                            spManager.saveString(SPManager.SP_STATUS_LEMBUR, "N");
                        }
                    } else {
                        txtPesan.setText(getString(R.string.msg_blm_absen));
                        txtPesan2.setText(getString(R.string.msg_blm_absen_sub));
                        spManager.saveString(SPManager.SP_STATUS_ABSEN, "BELUM_ABSEN");
                        spManager.saveString(SPManager.SP_STATUS_IZIN, "N");
                        spManager.saveString(SPManager.SP_STATUS_LEMBUR, "N");
                        txtAbsen = "Absen Masuk";
                    }

                    progressBar.setVisibility(View.GONE);
                    txtPesan.setVisibility(View.VISIBLE);
                    txtPesan2.setVisibility(View.VISIBLE);
                    changeButtonColor();
                    btnAbsen.setEnabled(true);
                    btnIzin.setEnabled(true);
                    btnKeg.setEnabled(true);
                    btnLembur.setEnabled(true);
                    btnLogout.setEnabled(true);
                    btnRiwayat.setEnabled(true);

                } catch (Exception e) {
                    Log.e(TAG + " Exception", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan refresh halaman", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, " onFailure: fail");
                Toast.makeText(MainActivity.this, "Terjadi kesalahan, silahkan refresh halaman", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeButtonColor() {
        if (spManager.getSpStatusIzin().equals("Y") || spManager.getSpStatusAbsen().equals("PULANG")) {
            buttonRiwayat.setTextButton("History", Color.WHITE, Color.parseColor("#A3CCE0"));
            buttonLogout.setTextButton("Logout", Color.WHITE, Color.parseColor("#ADF0E8"));
        } else {
            if (spManager.getSpStatusLembur().equals("Y")) {
                buttonAbsen.setTextButton(txtAbsen, Color.WHITE, Color.parseColor("#A3CCE0"));
                buttonRiwayat.setTextButton("History", Color.WHITE, Color.parseColor("#ADF0E8"));
                buttonIzin.setTextButton("Izin", Color.WHITE, Color.parseColor("#A3CCE0"));
                buttonKeg.setTextButton("Kegiatan", Color.WHITE, Color.parseColor("#ADF0E8"));
                buttonLogout.setTextButton("Logout", Color.WHITE, Color.parseColor("#A3CCE0"));
            } else {
                buttonAbsen.setTextButton(txtAbsen, Color.WHITE, Color.parseColor("#A3CCE0"));
                buttonRiwayat.setTextButton("History", Color.WHITE, Color.parseColor("#ADF0E8"));
                buttonIzin.setTextButton("Izin", Color.WHITE, Color.parseColor("#A3CCE0"));
                buttonKeg.setTextButton("Kegiatan", Color.WHITE, Color.parseColor("#ADF0E8"));
                buttonLembur.setTextButton("Lembur", Color.WHITE, Color.parseColor("#A3CCE0"));
                buttonLogout.setTextButton("Logout", Color.WHITE, Color.parseColor("#ADF0E8"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_absen) {
            buttonAbsen.buttonActivated();
            btnAbsen.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(MainActivity.this, AbsenActivity.class);
                intent.putExtra(AbsenActivity.EXTRA_ABSEN, txtAbsen);

                changeButtonColor();
                btnAbsen.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_keluar) {

            buttonLogout.buttonActivated();
            btnLogout.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                changeButtonColor();
                btnLogout.setEnabled(true);
                spManager.saveString(SPManager.SP_USER, "");
                spManager.saveString(SPManager.SP_USER_NAME, "");
                spManager.saveString(SPManager.SP_STATUS_ABSEN, "");
                spManager.saveString(SPManager.SP_STATUS_IZIN, "N");
                spManager.saveString(SPManager.SP_STATUS_LEMBUR, "N");
                spManager.saveString(SPManager.SP_DATE_ABSEN, "");
                spManager.saveInt(SPManager.SP_USER_ID, 0);
                spManager.saveInt(SPManager.SP_USER_NIK, 0);
                spManager.saveInt(SPManager.SP_ID_ABSEN, 0);
                spManager.saveBoolean(SPManager.SP_LOGIN, false);
                finish();
                startActivity(getIntent()
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }, 1000);
        } else if (v.getId() == R.id.btn_riwayat) {
            buttonRiwayat.buttonActivated();
            btnRiwayat.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                changeButtonColor();
                btnRiwayat.setEnabled(true);
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }, 500);
        } else if (v.getId() == R.id.btn_izin) {
            buttonIzin.buttonActivated();
            btnIzin.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(MainActivity.this, IzinActivity.class);

                changeButtonColor();
                btnIzin.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_kegiatan) {
            buttonKeg.buttonActivated();
            btnKeg.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(MainActivity.this, KegiatanActivity.class);

                changeButtonColor();
                btnKeg.setEnabled(true);
                startActivity(intent);
            }, 500);
        } else if (v.getId() == R.id.btn_lembur) {
            buttonLembur.buttonActivated();
            btnLembur.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {

                Intent intent = new Intent(MainActivity.this, LemburActivity.class);

                changeButtonColor();
                btnLembur.setEnabled(true);
                startActivity(intent);
            }, 500);
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermission: granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for(String permission: permissions){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                    //denied
                    this.checkPermission();
                    Log.e("denied", permission);
                }else{
                    if(ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        Log.d("allowed", permission);
                    } else{
                        //set to never ask again
                        AlertDialog.Builder alBuilderBuilder = new AlertDialog.Builder(this);

                        alBuilderBuilder.setTitle("Warning");
                        alBuilderBuilder
                                .setMessage("Aplikasi ini membutuhkan izin akses kamera dan lokasi, silahkan izinkan aplikasi pada pengaturan perizinan")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                        AlertDialog alertDialog = alBuilderBuilder.create();
                        alertDialog.show();
                        Log.e("set to never ask again", permission);
                        //do something here.
                    }
                }
            }
        }
    }
}