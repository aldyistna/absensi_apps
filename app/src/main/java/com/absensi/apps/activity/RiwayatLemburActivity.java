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

import com.absensi.apps.R;
import com.absensi.apps.adapter.LemburAdapter;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.entity.Lembur;
import com.absensi.apps.model.LemburViewModel;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;

import java.util.ArrayList;

public class RiwayatLemburActivity extends AppCompatActivity implements LemburViewModel.handleFailureLoad {

    private ProgressBar progressBar;
    private RecyclerView rvLembur;
    private LemburViewModel viewModel;
    private LemburAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    private SPManager spManager;

    ArrayList<Lembur> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_lembur);

        spManager = new SPManager(this);

        Gson gson = new Gson();
        String json = spManager.getSpUser();

        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lembur - " + karyawan.getName() + " - " + spManager.getSpUserNIK());
        }

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(LemburViewModel.class);
        viewModel.LemburViewModelHandleFail(this);

        adapter = new LemburAdapter(this);

        rvLembur = findViewById(R.id.rv_lembur);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvLembur.setLayoutManager(layoutManager);

        txtEmpty = findViewById(R.id.empty_text);
        swipe = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progress_bar);
        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            getData();
        });

        viewModel.getLemburList().observe(this, getDatas);
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
            rvLembur.setVisibility(View.VISIBLE);
            viewModel.setIzin(String.valueOf(spManager.getSpUserNIK()));
            showLoading(true);
        } else {
            txtEmpty.setText(getString(R.string.no_internet));
            txtEmpty.setVisibility(View.VISIBLE);
            rvLembur.setVisibility(View.GONE);
            showLoading(false);
        }
    }

    private final Observer<ArrayList<Lembur>> getDatas = new Observer<ArrayList<Lembur>>() {
        @Override
        public void onChanged(ArrayList<Lembur> lemburs) {
            if (lemburs != null) {
                if (lemburs.size() > 0) {
                    listData = lemburs;
                    txtEmpty.setVisibility(View.GONE);
                    rvLembur.setVisibility(View.VISIBLE);

                    adapter.setData(listData);
                    rvLembur.setAdapter(adapter);
                } else {
                    String sMatch = "Anda belum pernah lembur";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvLembur.setVisibility(View.GONE);
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
        rvLembur.setVisibility(View.GONE);
        showLoading(false);
    }
}