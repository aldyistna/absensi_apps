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
import com.absensi.apps.entity.Absen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AbsenAdapter extends RecyclerView.Adapter<AbsenAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Absen> listAbsen = new ArrayList<>();

    public AbsenAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Absen> items) {
        listAbsen.clear();
        listAbsen.addAll(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hist_absen_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(listAbsen.get(position));
    }

    @Override
    public int getItemCount() {
        return listAbsen.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTimeIn, tvTimeOut, tvLocIn, tvLocOut;
        public ViewHolder(@NonNull View view) {
            super(view);

            tvDate = view.findViewById(R.id.tv_date);
            tvTimeIn = view.findViewById(R.id.tv_time_in);
            tvTimeOut = view.findViewById(R.id.tv_time_out);
            tvLocIn = view.findViewById(R.id.tv_loc_masuk);
            tvLocOut = view.findViewById(R.id.tv_loc_pulang);
        }

        @SuppressLint("SimpleDateFormat")
        public void bind(Absen absen) {

            Locale locale = context.getResources().getConfiguration().locale;
            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy", locale);
            DateFormat df2 = new SimpleDateFormat("HH:mm:ss", locale);

            try {
                Date dateIn;
                Date dateOut;
                String timeOut = "-";
                dateIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(absen.getTime_in());
                if (!absen.getTime_out().equals("null") && !absen.getTime_out().equals("") && !absen.getTime_out().isEmpty()) {
                    dateOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(absen.getTime_out());
                    assert dateOut != null;
                    timeOut = df2.format(dateOut);
                }

                assert dateIn != null;
                String date = df1.format(dateIn);
                String timeIn = df2.format(dateIn);

                tvDate.setText(date);
                tvTimeIn.setText(timeIn);
                tvTimeOut.setText(timeOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvLocIn.setText(absen.getLocation_in());
            if (!absen.getLocation_out().equals("null") && !absen.getLocation_out().equals("") && !absen.getLocation_out().isEmpty()){
                tvLocOut.setText(absen.getLocation_out());
            } else {
                tvLocOut.setText("-");
            }
        }
    }
}
