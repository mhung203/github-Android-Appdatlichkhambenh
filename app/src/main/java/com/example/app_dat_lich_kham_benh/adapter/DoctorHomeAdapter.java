package com.example.app_dat_lich_kham_benh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import java.util.List;

public class DoctorHomeAdapter extends RecyclerView.Adapter<DoctorHomeAdapter.DoctorViewHolder> {

    private List<BacSi> doctorList;
    private Context context;

    public DoctorHomeAdapter(List<BacSi> doctorList, Context context) {
        this.doctorList = doctorList;
        this.context = context;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_home, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        BacSi doctor = doctorList.get(position);
        if (doctor.getUser() != null) {
            holder.tvDoctorName.setText(doctor.getUser().getFirstname() + " " + doctor.getUser().getLastname());
        }
        if (doctor.getKhoa() != null) {
            holder.tvDoctorSpecialty.setText(doctor.getKhoa().getTenKhoa());
        }

        Glide.with(context)
                .load(doctor.getAnhDaiDien())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(holder.ivDoctorAvatar);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDoctorAvatar;
        TextView tvDoctorName, tvDoctorSpecialty;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctorAvatar = itemView.findViewById(R.id.iv_doctor_avatar);
            tvDoctorName = itemView.findViewById(R.id.tv_doctor_name);
            tvDoctorSpecialty = itemView.findViewById(R.id.tv_doctor_specialty);
        }
    }
}
