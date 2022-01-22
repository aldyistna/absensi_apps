package com.absensi.apps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.apps.R;
import com.absensi.apps.entity.Lembur;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LemburAdapter extends RecyclerView.Adapter<LemburAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Lembur> listData = new ArrayList<>();

    public LemburAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Lembur> items) {
        listData.clear();
        listData.addAll(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hist_izin_lembur_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvKet;
        public ViewHolder(@NonNull View v) {
            super(v);

            tvDate = v.findViewById(R.id.tc_date);
            tvTime = v.findViewById(R.id.tc_time);
            tvKet = v.findViewById(R.id.hist_ket);
        }

        @SuppressLint("SimpleDateFormat")
        public void bind(Lembur lembur) {
            Locale locale = context.getResources().getConfiguration().locale;
            DateFormat df1 = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss", locale);

            try {
                Date date;
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(lembur.getDate());

                assert date != null;
                String tgl = df1.format(date);
                String time = df2.format(date);

                tvDate.setText(tgl);
                tvTime.setText(time);
                tvKet.setText(lembur.getKeterangan());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
