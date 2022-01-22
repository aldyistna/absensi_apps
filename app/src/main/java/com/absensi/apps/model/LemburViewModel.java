package com.absensi.apps.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.entity.Lembur;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class LemburViewModel extends ViewModel {
    private static final String TAG = LemburViewModel.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    public handleFailureLoad handleFailureLoad;

    public void LemburViewModelHandleFail(handleFailureLoad fail) {
        handleFailureLoad = fail;
    }

    private final MutableLiveData<ArrayList<Lembur>> listLemburs = new MutableLiveData<>();

    public void setIzin(final String nik) {
        final ArrayList<Lembur> listLembur = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(API_URL + "api/lembur?nik=" + nik, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resObject = new JSONObject(result);
                    JSONArray list = resObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Lembur lembur = new Lembur(jsonObject);
                        listLembur.add(lembur);
                    }

                    listLemburs.setValue(listLembur);

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

    public LiveData<ArrayList<Lembur>> getLemburList() {
        return listLemburs;
    }

    public interface handleFailureLoad {
        void onFailureLoad();
    }
}
