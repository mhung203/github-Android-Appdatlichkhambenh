package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.ThongBao;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotiViewHolder> {
    private List<ThongBao> mList;
    private Context context;

    public NotificationAdapter(Context context, List<ThongBao> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        ThongBao tb = mList.get(position);
        holder.tvTieuDe.setText(tb.getTieuDe());
        holder.tvNoiDung.setText(tb.getNoiDung());
        holder.tvThoiGian.setText(tb.getThoiGian());
        if (!tb.isDaXem()) {
            holder.tvTieuDe.setTypeface(null, Typeface.BOLD);
            holder.itemView.setBackgroundColor(Color.parseColor("#E3F2FD"));
        } else {
            holder.tvTieuDe.setTypeface(null, Typeface.NORMAL);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() { return mList.size(); }

    public class NotiViewHolder extends RecyclerView.ViewHolder {
        TextView tvTieuDe, tvNoiDung, tvThoiGian;
        public NotiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvNotiTitle);
            tvNoiDung = itemView.findViewById(R.id.tvNotiContent);
            tvThoiGian = itemView.findViewById(R.id.tvNotiTime);
        }
    }
}
