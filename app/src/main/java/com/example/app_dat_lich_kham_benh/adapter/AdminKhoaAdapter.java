package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;

import java.util.List;

public class AdminKhoaAdapter extends RecyclerView.Adapter<AdminKhoaAdapter.KhoaViewHolder> {

    private Context context;
    private List<Khoa> list;
    private OnDeleteListener listener;

    public interface OnDeleteListener {
        void onDelete(int id);
    }

    public AdminKhoaAdapter(Context context, List<Khoa> list, OnDeleteListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KhoaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_khoa, parent, false);
        return new KhoaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhoaViewHolder holder, int position) {
        Khoa khoa = list.get(position);
        holder.tvTenKhoa.setText(khoa.getTenKhoa());
        holder.tvMoTa.setText(khoa.getMoTa());
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(khoa.getKhoaId()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class KhoaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenKhoa, tvMoTa;
        ImageButton btnDelete;

        public KhoaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKhoa = itemView.findViewById(R.id.tvTenKhoa);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}