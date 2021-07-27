package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    EditText edtUserName, edtPass;
    SPManager spManager;
    InputMethodManager inputManager;
    ProgressButton progressButton;
    View btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        spManager = new SPManager(this);
        inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        edtUserName = findViewById(R.id.txt_user_name);
        edtPass = findViewById(R.id.txt_pass);

        btnLogin = findViewById(R.id.btn_masuk);
        progressButton = new ProgressButton(LoginActivity.this, btnLogin);
        progressButton.changeSizeButton(50);
        progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (v.getId() == R.id.btn_masuk) {
            progressButton.buttonActivated();
            btnLogin.setEnabled(false);
            String userName = edtUserName.getText().toString();
            String pass = edtPass.getText().toString();
            if (userName.trim().length() > 0 && pass.trim().length() > 0) {

                if (inputManager != null) {
                    if (getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    login(userName, pass);
                } else {
                    progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
                    btnLogin.setEnabled(true);
                    makeToast(getString(R.string.no_internet));
                }
            } else {
                progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnLogin.setEnabled(true);
                makeToast("NIK dan password tidak boleh kosong");
            }
        }
    }

    public void login(String username, String password) {
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("nik", username);
        params.put("password", password);
        params.put("login", "MOBILE");

        client.post(API_URL + "login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject resObject = new JSONObject(result);
                    if (resObject.getString("status").equals("0")) {
                        progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
                        btnLogin.setEnabled(true);
                        makeToast("NIK atau password salah !");
                    } else {
                        JSONObject val = resObject.getJSONObject("data");
                        Karyawan karyawan = new Karyawan(val);
                        Gson gson = new Gson();
                        String json = gson.toJson(karyawan);

                        spManager.saveString(SPManager.SP_USER, json);
                        spManager.saveString(SPManager.SP_USER_NAME, val.getString("username"));
                        spManager.saveInt(SPManager.SP_USER_ID, val.getInt("id"));
                        spManager.saveInt(SPManager.SP_USER_NIK, val.getInt("nik"));
                        spManager.saveBoolean(SPManager.SP_LOGIN, true);
                        progressButton.buttonFinished();
                        btnLogin.setEnabled(true);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    }
                } catch (JSONException e) {
                    progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
                    btnLogin.setEnabled(true);
                    makeToast("Terjadi kesalahan, silahkan login ulang");
                    Log.e(TAG + " JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressButton.setTextButton("Login", Color.WHITE, Color.parseColor("#A3CCE0"));
                btnLogin.setEnabled(true);
                makeToast("Terjadi kesalahan, silahkan login ulang");
                Log.e(TAG + " onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });
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
}