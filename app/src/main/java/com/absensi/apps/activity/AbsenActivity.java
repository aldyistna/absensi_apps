package com.absensi.apps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.R;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.utils.ProgressButton;
import com.absensi.apps.utils.SPManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class AbsenActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = AbsenActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    public static final String EXTRA_ABSEN = "extra_absen";

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SPManager spManager;

    ImageView imgFoto;
    TextView titleLocation, txtNIK;
    ProgressButton progressButton, backButton;
    View btnSave, btnBack;
    //    private SPManager spManager;
    File images = null;
    String currentPhotoPath = "";
    private GoogleMap mMap;
    private Location lastKnownLocation;
    private LocationRequest locationRequest;
    Marker mCurrLocationMarker;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    String absen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        absen = getIntent().getStringExtra(EXTRA_ABSEN);
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(absen);
        }*/

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        spManager = new SPManager(this);

        TextClock textDate = findViewById(R.id.text_date);
        TextClock textClock = findViewById(R.id.text_clock);
        textDate.setFormat12Hour(null);
        textClock.setFormat12Hour(null);
        textDate.setFormat24Hour("EEEE, dd MMMM yyyy");
        textClock.setFormat24Hour("HH:mm:ss");

        txtNIK = findViewById(R.id.txt_nik);
        TextView txtName = findViewById(R.id.txt_name);
        titleLocation = findViewById(R.id.txt_location);

        Gson gson = new Gson();
        String json = spManager.getSpUser();
        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        txtNIK.setText(karyawan.getNik());
        txtName.setText(karyawan.getName());

        imgFoto = findViewById(R.id.img_foto);
        imgFoto.setOnClickListener(v -> captureImage());

        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        progressButton = new ProgressButton(AbsenActivity.this, btnSave);
        backButton = new ProgressButton(AbsenActivity.this, btnBack);
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
                makeToast("Tidak ada foto untuk dilaporkan");
                return;
            }
            if (networkInfo != null && networkInfo.isConnected()) {
                progressButton.buttonActivatedSave();
                btnSave.setEnabled(false);
                btnBack.setEnabled(false);
                saveAbsen();
            } else {
                makeToast(getString(R.string.no_internet));
            }
        });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(300);
        locationRequest.setSmallestDisplacement(5f);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void saveAbsen() {
        AsyncHttpClient client = new AsyncHttpClient();
        String urlPath = "api/absens";

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        String dateNow = dateFormat.format(date);
        Log.d(TAG, "saveAbsen: " + dateNow);

        RequestParams params = new RequestParams();
        params.put("location", titleLocation.getText());
        params.put("nik", txtNIK.getText());
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
        if (spManager.getIdAbsen() != 0) {
            urlPath = urlPath + "/" + spManager.getIdAbsen();
        }

        Log.d(TAG, "saveAbsen: " + params);

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
                        Boolean deleted = images.delete();
                        Log.e(TAG + " onSuccess", String.valueOf(deleted));
                    }
                }
                spManager.saveString(SPManager.SP_DATE_ABSEN, "");
                progressButton.buttonFinished();
                makeToast("Berhasil " + absen);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(AbsenActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }, 500);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressButton.setTextButton("Save", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnSave.setEnabled(true);
                btnBack.setEnabled(true);
                makeToast("Terjadi kesalahan, silahkan absen ulang");
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocationUI();
                getDeviceLocation();
            }
        }, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                    LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mCurrLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    Geocoder geo = new Geocoder(AbsenActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    try {
                        addresses = geo.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                        Log.d(TAG, "getDeviceLocation: " + addresses.get(0));
                        if (addresses.isEmpty()) {
                            titleLocation.setText("-");
                        }
                        else {
                            String addrs = addresses.get(0).getAddressLine(0);
                            titleLocation.setText(addrs);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d(TAG, "Current location is null. Using defaults.");
                Log.e(TAG, "Exception: %s", task.getException());
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        });
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}