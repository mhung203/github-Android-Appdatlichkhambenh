package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.DoctorAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.model.Doctor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageDoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnItemClickListener {

    private static final String TAG = "ManageDoctorsActivity";

    private RecyclerView recyclerViewDoctors;
    private FloatingActionButton fabAddDoctor;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_doctors);

        apiService = ApiClient.getApiService();
        recyclerViewDoctors = findViewById(R.id.recycler_view_doctors);
        fabAddDoctor = findViewById(R.id.fab_add_doctor);

        setupRecyclerView();

        fabAddDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(ManageDoctorsActivity.this, AddEditDoctorActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDoctors();
    }

    private void setupRecyclerView() {
        doctorAdapter = new DoctorAdapter(doctorList, this);
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctors.setAdapter(doctorAdapter);
    }

    private void loadDoctors() {
        apiService.getAllBacSi().enqueue(new Callback<List<BacSi>>() {
            @Override
            public void onResponse(Call<List<BacSi>> call, Response<List<BacSi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doctorList.clear();
                    for (BacSi bacSi : response.body()) {
                        User user = bacSi.getUser();
                        if (user != null) {
                            String specialty = (bacSi.getKhoa() != null) ? bacSi.getKhoa().getTenKhoa() : "Chưa có";
                            doctorList.add(new Doctor(
                                    bacSi.getBacSiId(),
                                    user.getUserId(),
                                    user.getFirstname(),
                                    user.getLastname(),
                                    user.getEmail(),
                                    specialty
                            ));
                        }
                    }
                    doctorAdapter.notifyDataSetChanged();
                } else {
                    showToast("Không thể tải danh sách bác sĩ.");
                }
            }

            @Override
            public void onFailure(Call<List<BacSi>> call, Throwable t) {
                Log.e(TAG, "Error loading doctors: " + t.getMessage());
                showToast("Lỗi kết nối.");
            }
        });
    }

    @Override
    public void onEditClick(Doctor doctor) {
        Intent intent = new Intent(this, AddEditDoctorActivity.class);
        intent.putExtra("DOCTOR_ID", doctor.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Doctor doctor) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bác sĩ '" + doctor.getFullName() + "'? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDoctor(doctor.getUserId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteDoctor(int userId) {
        apiService.deleteUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    showToast("Xóa bác sĩ thành công!");
                    loadDoctors(); // Tải lại danh sách
                } else {
                    showToast("Xóa bác sĩ thất bại. Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting doctor: " + t.getMessage());
                showToast("Lỗi kết nối khi xóa.");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ManageDoctorsActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
