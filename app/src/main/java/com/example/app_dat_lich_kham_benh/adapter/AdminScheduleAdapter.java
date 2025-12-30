package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;

import java.util.List;

public class AdminScheduleAdapter extends RecyclerView.Adapter<AdminScheduleAdapter.AdminViewHolder> {

    private Context context;
    private List<LichHen> list;
    private OnActionCallback callback;

    public interface OnActionCallback {
        void onCancelClick(int id);
    }

    public AdminScheduleAdapter(Context context, List<LichHen> list, OnActionCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_schedule, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        LichHen lh = list.get(position);
        if (lh.getTimeStart() != null) {
            holder.tvTime.setText(lh.getTimeStart());
        }
        if (lh.getBacSi() != null && lh.getBacSi().getUser() != null) {
            String tenBacSi = lh.getBacSi().getUser().getFirstname() + " " + lh.getBacSi().getUser().getLastname();
            holder.tvDoctorName.setText("BS: " + tenBacSi);
        }
        if (lh.getBenhNhan() != null) {
            String tenBenhNhan = lh.getBenhNhan().getHoTen();
            holder.tvPatientName.setText("BN: " + tenBenhNhan);
        }

        holder.tvReason.setText("Lý do: " + lh.getLyDoKham());
        if (lh.getTrangThai() != null) {
            holder.tvStatus.setText(lh.getTrangThai());
            String statusLower = lh.getTrangThai().toLowerCase();
            if (statusLower.contains("hủy")) {
                holder.tvStatus.setTextColor(Color.RED);
                holder.btnCancel.setVisibility(View.GONE);
            } else if (statusLower.contains("xác nhận")) {
                holder.tvStatus.setTextColor(Color.GREEN);
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else {
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
                holder.btnCancel.setVisibility(View.VISIBLE);
            }
        }
        holder.btnCancel.setOnClickListener(v -> {
            callback.onCancelClick(lh.getLichHenId());
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvStatus, tvDoctorName, tvPatientName, tvReason;
        Button btnCancel;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvReason = itemView.findViewById(R.id.tvReason);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}