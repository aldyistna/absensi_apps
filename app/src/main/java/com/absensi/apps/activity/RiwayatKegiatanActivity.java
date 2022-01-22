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
import com.absensi.apps.adapter.KegiatanAdapter;
import com.absensi.apps.entity.Karyawan;
import com.absensi.apps.entity.Kegiatan;
import com.absensi.apps.entity.KegiatanDate;
import com.absensi.apps.entity.KegiatanItem;
import com.absensi.apps.entity.ListItem;
import com.absensi.apps.model.KegiatanViewModel;
import com.absensi.apps.utils.SPManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class RiwayatKegiatanActivity extends AppCompatActivity implements KegiatanViewModel.handleFailureLoad {

    private ProgressBar progressBar;
    private RecyclerView rvKeg;
    private KegiatanViewModel viewModel;
    private KegiatanAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView txtEmpty;
    private SPManager spManager;

    List<ListItem> listItems = new ArrayList<>();
    ArrayList<Kegiatan> lisKeg = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_kegiatan);

        spManager = new SPManager(this);

        Gson gson = new Gson();
        String json = spManager.getSpUser();

        Karyawan karyawan = gson.fromJson(json, Karyawan.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kegiatan - " + karyawan.getName() + " - " + spManager.getSpUserNIK());
        }

        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(KegiatanViewModel.class);
        viewModel.KegiatanViewModelHandleFail(this);

        adapter = new KegiatanAdapter(this);

        rvKeg = findViewById(R.id.rv_keg_par);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvKeg.setLayoutManager(layoutManager);

        txtEmpty = findViewById(R.id.empty_text);
        swipe = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progress_bar);
        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            getData();
        });

        viewModel.getKegiatanList().observe(this, getKeg);
        getData();
    }

    private void getData() {
        listItems = new ArrayList<>();
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            txtEmpty.setVisibility(View.GONE);
            rvKeg.setVisibility(View.VISIBLE);
            viewModel.setKegiatan(String.valueOf(spManager.getSpUserNIK()));
            showLoading(true);
        } else {
            txtEmpty.setText(getString(R.string.no_internet));
            txtEmpty.setVisibility(View.VISIBLE);
            rvKeg.setVisibility(View.GONE);
            showLoading(false);
        }
    }

    private final Observer<ArrayList<Kegiatan>> getKeg = new Observer<ArrayList<Kegiatan>>() {
        @Override
        public void onChanged(ArrayList<Kegiatan> kegiatans) {
            if (kegiatans != null) {
                if (kegiatans.size() > 0) {
                    lisKeg = kegiatans;
                    txtEmpty.setVisibility(View.GONE);
                    rvKeg.setVisibility(View.VISIBLE);

                    LinkedHashMap<String, List<Kegiatan>> groupedHashMap = groupDataIntoHashMap(kegiatans);

                    for (String date : groupedHashMap.keySet()) {
                        KegiatanDate kegiatanDate = new KegiatanDate();
                        kegiatanDate.setDate(date);
                        listItems.add(kegiatanDate);

                        for (Kegiatan kegiatan : Objects.requireNonNull(groupedHashMap.get(date))) {
                            KegiatanItem kegiatanItem = new KegiatanItem();
                            kegiatanItem.setKegiatan(kegiatan);
                            listItems.add(kegiatanItem);
                        }
                    }

                    adapter.setData(listItems);
                    rvKeg.setAdapter(adapter);
                } else {
                    String sMatch = "Anda belum ada kegiatan";
                    txtEmpty.setText(sMatch);
                    txtEmpty.setVisibility(View.VISIBLE);
                    rvKeg.setVisibility(View.GONE);
                }
                showLoading(false);
            }
        }
    };

    private LinkedHashMap<String, List<Kegiatan>> groupDataIntoHashMap(List<Kegiatan> lists) {

        LinkedHashMap<String, List<Kegiatan>> groupedHashMap = new LinkedHashMap<>();

        for (Kegiatan kegiatan : lists) {

            String hashMapKey = kegiatan.getDate();

            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                Objects.requireNonNull(groupedHashMap.get(hashMapKey)).add(kegiatan);

            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<Kegiatan> list = new ArrayList<>();
                list.add(kegiatan);
                groupedHashMap.put(hashMapKey, list);
            }
        }
        return groupedHashMap;
    }

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
        rvKeg.setVisibility(View.GONE);
        showLoading(false);

    }
}