package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import java.util.List;
public class KhoaAdapter extends RecyclerView.Adapter<KhoaAdapter.KhoaViewHolder> {

    private List<Khoa> khoaList;
    private Context context;

    public KhoaAdapter(List<Khoa> khoaList, Context context) {
        this.khoaList = khoaList;
        this.context = context;
    }

    @NonNull
    @Override
    public KhoaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_khoa, parent, false);
        return new KhoaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhoaViewHolder holder, int position) {
        Khoa khoa = khoaList.get(position);
        holder.tvKhoaName.setText(khoa.getTenKhoa());
        Glide.with(context)
                .load(khoa.getHinhAnh())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivKhoaIcon);
    }

    @Override
    public int getItemCount() {
        return khoaList.size();
    }

    public static class KhoaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivKhoaIcon;
        TextView tvKhoaName;

        public KhoaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivKhoaIcon = itemView.findViewById(R.id.iv_khoa_icon);
            tvKhoaName = itemView.findViewById(R.id.tv_khoa_name);
        }
    }
}
