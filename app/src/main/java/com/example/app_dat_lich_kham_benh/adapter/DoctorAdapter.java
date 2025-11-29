package com.example.app_dat_lich_kham_benh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.model.Doctor;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private OnItemClickListener listener;

    public DoctorAdapter(List<Doctor> doctorList, OnItemClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.bind(doctor, listener);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDoctorName, tvDoctorSpecialty;
        private ImageButton btnEdit, btnDelete;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tv_doctor_name);
            tvDoctorSpecialty = itemView.findViewById(R.id.tv_doctor_specialty);
            btnEdit = itemView.findViewById(R.id.btn_edit_doctor);
            btnDelete = itemView.findViewById(R.id.btn_delete_doctor);
        }

        public void bind(final Doctor doctor, final OnItemClickListener listener) {
            tvDoctorName.setText(doctor.getFullName());
            // We are not saving specialty to the User table yet, so this is a placeholder.
            // We'll need to adjust the database schema later.
            tvDoctorSpecialty.setText(doctor.getSpecialty()); 

            btnEdit.setOnClickListener(v -> listener.onEditClick(doctor));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(doctor));
        }
    }

    public interface OnItemClickListener {
        void onEditClick(Doctor doctor);
        void onDeleteClick(Doctor doctor);
    }
}
