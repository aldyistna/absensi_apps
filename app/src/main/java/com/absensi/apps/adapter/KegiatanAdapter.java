package com.absensi.apps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.absensi.apps.R;
import com.absensi.apps.entity.KegiatanDate;
import com.absensi.apps.entity.KegiatanItem;
import com.absensi.apps.entity.ListItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class KegiatanAdapter extends RecyclerView.Adapter<KegiatanAdapter.ViewHolder> {
    private final Context context;
    private final List<ListItem> kegList = new ArrayList<>();

    public KegiatanAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ListItem>  items) {
        kegList.clear();
        kegList.addAll(items);
    }

    @Override
    public int getItemViewType(int position) {
        return kegList.get(position).getType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case ListItem.TYPE_ITEM:
                View v1 = inflater.inflate(R.layout.item_hist_keg_child, parent, false);
                viewHolder = new ItemViewHolder(v1);
                break;

            case ListItem.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.item_hist_keg_par, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {

            case ListItem.TYPE_ITEM:
                KegiatanItem kegiatanItem
                        = (KegiatanItem) kegList.get(position);
                ItemViewHolder itemViewHolder
                        = (ItemViewHolder) holder;

                itemViewHolder.bind(kegiatanItem);

                break;

            case ListItem.TYPE_DATE:
                KegiatanDate dateItem
                        = (KegiatanDate) kegList.get(position);
                DateViewHolder dateViewHolder
                        = (DateViewHolder) holder;

                dateViewHolder.bind(dateItem);

                // Populate date item data here

                break;
        }
    }

    @Override
    public int getItemCount() {
        return kegList.size();
    }

    public static class DateViewHolder extends ViewHolder {
        TextView tvDate;
        public DateViewHolder(@NonNull View v) {
            super(v);

            tvDate = v.findViewById(R.id.tc_date);
        }

        public void bind(KegiatanDate date) {
            tvDate.setText(date.getDate());
        }
    }

    public class ItemViewHolder extends ViewHolder {
        ImageView imgKeg;
        TextView tvKet, tvTime;
        public ItemViewHolder(@NonNull View v) {
            super(v);

            imgKeg = v.findViewById(R.id.img_keg);
            tvKet = v.findViewById(R.id.tv_ket);
            tvTime = v.findViewById(R.id.tv_jam);
        }

        public void bind(KegiatanItem item) {

            Glide.with(context)
                    .load(item.getKegiatan().getFoto())
                    .placeholder(R.drawable.no_photo)
                    .error(R.drawable.error_image)
                    .into(imgKeg);
            tvKet.setText(item.getKegiatan().getKeterangan());
            tvTime.setText(item.getKegiatan().getTime());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
