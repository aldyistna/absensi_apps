package com.absensi.apps.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.absensi.apps.BuildConfig;
import com.absensi.apps.R;
import com.absensi.apps.adapter.AbsenAdapter;
import com.absensi.apps.entity.Absen;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.model.AbsenViewModel;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RiwayatAbsenActivity extends AppCompatActivity implements AbsenViewModel.handleFailureLoad {
    private static final String TAG = RiwayatAbsenActivity.class.getSimpleName();
    private static final String API_URL = BuildConfig.API_URL;

    private ProgressBar progressBar;
    private RecyclerView rvAbsen;
    private AbsenViewModel viewModel;
    private AbsenAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    private SPManager spManager;

    ArrayList<Absen> listAbsen = new ArrayList<>();
    Absen absen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_absen);

        spManager = new SPManager(this);

        Gson gson = new Gson();
        String json = spManager.getSpUser();

        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(karyawan.getName() + " - " + spManager.getSpUserNIK());
        }

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(AbsenViewModel.class);
        viewModel.AbsenViewModel(this);

        adapter = new AbsenAdapter(this);

        rvAbsen = findViewById(R.id.rv_absen);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvAbsen.setLayoutManager(layoutManager);

        txtEmpty = findViewById(R.id.empty_text);
        swipe = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progress_bar);
        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            getData();
        });

        viewModel.getAbsenList().observe(this, getAbsen);
        getData();
    }

    private void getData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            txtEmpty.setVisibility(View.GONE);
            rvAbsen.setVisibility(View.VISIBLE);
            viewModel.setAbsen(String.valueOf(spManager.getSpUserNIK()));
            showLoading(true);
        } else {
            txtEmpty.setText(getString(R.string.no_internet));
            txtEmpty.setVisibility(View.VISIBLE);
            rvAbsen.setVisibility(View.GONE);
            showLoading(false);
        }
    }

    private final Observer<ArrayList<Absen>> getAbsen = new Observer<ArrayList<Absen>>() {
        @Override
        public void onChanged(ArrayList<Absen> absens) {
            if (absens != null) {
                if (absens.size() > 0) {
                    listAbsen = absens;
                    txtEmpty.setVisibility(View.GONE);
                    rvAbsen.setVisibility(View.VISIBLE);

                    adapter.setData(listAbsen);
                    adapter.notifyDataSetChanged();
                    rvAbsen.setAdapter(adapter);
                } else {
                    String sMatch = "Anda belum pernah absen";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvAbsen.setVisibility(View.GONE);
                }
                showLoading(false);
            }
        }
    };

    private void showLoading(boolean state) {
        if (state) {
            swipe.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            swipe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailureLoad() {
        String sMatch = "Terjadi kesalahan, silahkan refresh halaman";
        txtEmpty.setText(sMatch);
        txtEmpty.setVisibility(View.VISIBLE);
        rvAbsen.setVisibility(View.GONE);
        showLoading(false);
    }
}