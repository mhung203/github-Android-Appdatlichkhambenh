package com.example.app_dat_lich_kham_benh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.model.User;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AppointmentHistoryAdapter extends RecyclerView.Adapter<AppointmentHistoryAdapter.ViewHolder> {
    private List<LichHen> appointmentList;

    public AppointmentHistoryAdapter(List<LichHen> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LichHen lichHen = appointmentList.get(position);
        
        if (lichHen.getBacSi() != null && lichHen.getBacSi().getUser() != null) {
            User doctorUser = lichHen.getBacSi().getUser();
            String doctorName = doctorUser.getFirstname() + " " + doctorUser.getLastname();
            holder.tvDoctorName.setText("Bác sĩ: " + doctorName);
        } else {
            holder.tvDoctorName.setText("Bác sĩ: Không xác định");
        }

        if (lichHen.getNgayHen() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvDate.setText("Ngày: " + dateFormat.format(lichHen.getNgayHen()));
        } else {
            holder.tvDate.setText("Ngày: Không xác định");
        }

        String time = "Không xác định";
        if (lichHen.getTimeStart() != null && lichHen.getTimeEnd() != null) {
            time = lichHen.getTimeStart() + " - " + lichHen.getTimeEnd();
        }
        holder.tvTime.setText("Giờ: " + time);
        holder.tvStatus.setText("Trạng thái: " + lichHen.getTrangThai());
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDate, tvTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
