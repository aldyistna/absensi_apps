package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.R;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.utils.ProgressButton;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class KegiatanActivity extends AppCompatActivity {
    private static final String TAG = KegiatanActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    private SPManager spManager;
    InputMethodManager inputManager;
    TextView txtNIK;
    ProgressButton progressButton, backButton;
    View btnSave, btnBack;
    EditText edtKet;
    ImageView imgFoto;

    File images = null;
    String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kegiatan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        TextClock textDate = findViewById(R.id.text_date);
        TextClock textClock = findViewById(R.id.text_clock);
        textDate.setFormat12Hour(null);
        textClock.setFormat12Hour(null);
        textDate.setFormat24Hour("EEEE, dd MMMM yyyy");
        textClock.setFormat24Hour("HH:mm:ss");

        txtNIK = findViewById(R.id.txt_nik);
        edtKet = findViewById(R.id.edt_ket);
        TextView txtName = findViewById(R.id.txt_name);
        TextView txtPos = findViewById(R.id.txt_posisi);
        TextView txtJab = findViewById(R.id.txt_jab);

        Gson gson = new Gson();
        String json = spManager.getSpUser();
        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        txtNIK.setText(karyawan.getNik());
        txtName.setText(karyawan.getName());
        txtPos.setText(karyawan.getPosisi());
        txtJab.setText(karyawan.getJabatan());

        imgFoto = findViewById(R.id.img_foto);
        imgFoto.setOnClickListener(v -> captureImage());

        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        progressButton = new ProgressButton(KegiatanActivity.this, btnSave);
        backButton = new ProgressButton(KegiatanActivity.this, btnBack);
        progressButton.setTextButton("Save", Color.WHITE, Color.parseColor("#A3CCE0"));
        backButton.setTextButton("Back", Color.WHITE, Color.parseColor("#AE6262"));

        btnBack.setOnClickListener(v -> onBackPressed());
        btnSave.setOnClickListener(v -> {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (currentPhotoPath.equals("")) {
                makeToast("Harap masukkan foto terlebih dahulu!");
                return;
            }
            String ket = edtKet.getText().toString();
            if (ket.trim().length() > 0) {

                if (inputManager != null) {
                    if (getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    progressButton.buttonActivatedSave();
                    btnSave.setEnabled(false);
                    btnBack.setEnabled(false);
                    saveKegiatan(ket);
                } else {
                    makeToast(getString(R.string.no_internet));
                }
            } else {
                makeToast("Keterangan tidak boleh kosong");
            }
        });
    }

    private void saveKegiatan(String ket) {
        AsyncHttpClient client = new AsyncHttpClient();
        String urlPath = "api/kegiatan";

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        String dateNow = dateFormat.format(date);

        RequestParams params = new RequestParams();
        params.put("nik", txtNIK.getText());
        params.put("keterangan", ket);
        params.put("date", dateNow);
        File file = null;
        if (!currentPhotoPath.equals("")) {
            try {
                file = new File(currentPhotoPath);
                params.put("file", file);
            } catch (FileNotFoundException e) {
                progressButton.setTextButton("Save", Color.WHITE, Color.parseColor("#A3CCE0"));
                makeToast("Silahkan masukkan foto");
                return;
            }
        }

        final File finalFile = file;
        client.post(API_URL + urlPath, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (images != null) {
                    if (images.exists()) {
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onSuccess", String.valueOf(deleted));
                    }
                }
                if (finalFile != null) {
                    if (finalFile.exists()) {
                        Boolean deleted = finalFile.delete();
                        Log.e(TAG + " onSuccess", String.valueOf(deleted));
                    }
                }
                spManager.saveString(SPManager.SP_DATE_ABSEN, "");
                progressButton.buttonFinished();
                makeToast("Berhasil save kegiatan");
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(KegiatanActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }, 500);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressButton.setTextButton("Save", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnSave.setEnabled(true);
                btnBack.setEnabled(true);
                makeToast("Terjadi kesalahan, silahkan input kegiatan ulang");
                Log.e(TAG + " onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            images = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (images != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.absensi.apps.fileprovider", images);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            try {
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "captureImage: exception");
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        if (requestCode == 1) {
            if (images != null) {
                currentPhotoPath = images.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.absensi.apps.fileprovider", images);
//                imgFoto.setBackgroundResource(R.drawable.background);
                imgFoto.setImageURI(photoURI);
                imgFoto.getLayoutParams().height = Math.round((float) 350 * this.getResources().getDisplayMetrics().density);
                imgFoto.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                imgFoto.requestLayout();
            }
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (!currentPhotoPath.equals("")) {
            File file = new File(currentPhotoPath);
            if (file.exists()) {
                Boolean deleted = file.delete();
                Log.e(TAG + " onback-file delete:", String.valueOf(deleted));
            }
        }
        if (images != null) {
            if (images.exists()) {
                Boolean deleted = images.delete();
                Log.e(TAG + " onback-images delete:", String.valueOf(deleted));
            }
        }
        super.onBackPressed();
    }
}