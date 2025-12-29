package com.example.app_dat_lich_kham_benh.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;

import java.util.List;

public class LichHenAdapter extends RecyclerView.Adapter<LichHenAdapter.LichHenViewHolder> {

    private List<LichHen> lichHenList;

    public LichHenAdapter(List<LichHen> lichHenList) {
        this.lichHenList = lichHenList;
    }

    @NonNull
    @Override
    public LichHenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lich_hen, parent, false);
        return new LichHenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LichHenViewHolder holder, int position) {
        LichHen lichHen = lichHenList.get(position);
        if (lichHen != null) {
            if (lichHen.getBenhNhan() != null && lichHen.getBenhNhan().getHoTen() != null) {
                holder.tvPatientName.setText(lichHen.getBenhNhan().getHoTen());
            }
            String time = lichHen.getTimeStart() + " - " + lichHen.getTimeEnd();
            holder.tvAppointmentTime.setText("Thời gian: " + time);
            holder.tvAppointmentStatus.setText("Trạng thái: " + lichHen.getTrangThai());
        }
    }

    @Override
    public int getItemCount() {
        return lichHenList != null ? lichHenList.size() : 0;
    }

    public void setData(List<LichHen> newData) {
        this.lichHenList = newData;
        notifyDataSetChanged();
    }

    public static class LichHenViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvAppointmentTime, tvAppointmentStatus;

        public LichHenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvAppointmentTime = itemView.findViewById(R.id.tv_appointment_time);
            tvAppointmentStatus = itemView.findViewById(R.id.tv_appointment_status);
        }
    }
}
