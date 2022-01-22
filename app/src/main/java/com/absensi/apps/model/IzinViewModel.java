package com.absensi.apps.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.entity.Izin;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class IzinViewModel extends ViewModel {
    private static final String TAG = IzinViewModel.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    public handleFailureLoad handleFailureLoad;

    public void IzinViewModelHandleFail(handleFailureLoad fail) {
        handleFailureLoad = fail;
    }

    private final MutableLiveData<ArrayList<Izin>> listIzins = new MutableLiveData<>();

    public void setIzin(final String nik) {
        final ArrayList<Izin> listIzin = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(API_URL + "api/izin?nik=" + nik, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resObject = new JSONObject(result);
                    JSONArray list = resObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Izin izin = new Izin(jsonObject);
                        listIzin.add(izin);
                    }

                    listIzins.setValue(listIzin);

                } catch (Exception e) {
                    handleFailureLoad.onFailureLoad();
                    Log.e(TAG + " Exception", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handleFailureLoad.onFailureLoad();
                Log.e(TAG + " onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });
    }

    public LiveData<ArrayList<Izin>> getIzinList() {
        return listIzins;
    }

    public interface handleFailureLoad {
        void onFailureLoad();
    }
}
