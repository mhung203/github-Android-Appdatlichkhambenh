package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboard";
    private ApiService apiService;

    private TextView tvDoctorCount, tvPatientCount, tvDepartmentCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        apiService = ApiClient.getApiService();

        // Ánh xạ các TextView
        tvDoctorCount = findViewById(R.id.tv_doctor_count);
        tvPatientCount = findViewById(R.id.tv_patient_count);
        tvDepartmentCount = findViewById(R.id.tv_department_count);

        // Gắn sự kiện cho nút Quản lý Bác sĩ
        Button btnManageDoctors = findViewById(R.id.btn_manage_doctors);
        btnManageDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageDoctorsActivity.class);
            startActivity(intent);
        });

        // Tải dữ liệu thống kê
        loadDashboardData();
    }

    private void loadDashboardData() {
        // 1. Tải số lượng Khoa
        apiService.getAllKhoa().enqueue(new Callback<List<Khoa>>() {
            @Override
            public void onResponse(Call<List<Khoa>> call, Response<List<Khoa>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvDepartmentCount.setText(String.valueOf(response.body().size()));
                } else {
                    showToast("Lỗi tải số liệu chuyên khoa.");
                }
            }

            @Override
            public void onFailure(Call<List<Khoa>> call, Throwable t) {
                handleApiFailure(t);
            }
        });

        // 2. Tải số lượng Bác sĩ
        apiService.getAllBacSi().enqueue(new Callback<List<BacSi>>() {
            @Override
            public void onResponse(Call<List<BacSi>> call, Response<List<BacSi>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvDoctorCount.setText(String.valueOf(response.body().size()));
                } else {
                    showToast("Lỗi tải số liệu bác sĩ.");
                }
            }

            @Override
            public void onFailure(Call<List<BacSi>> call, Throwable t) {
                handleApiFailure(t);
            }
        });

        // 3. Tải số lượng Bệnh nhân
        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int patientCount = 0;
                    for (User user : response.body()) {
                        if ("Bệnh nhân".equalsIgnoreCase(user.getRole())) {
                            patientCount++;
                        }
                    }
                    tvPatientCount.setText(String.valueOf(patientCount));
                } else {
                    showToast("Lỗi tải số liệu bệnh nhân.");
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    private void handleApiFailure(Throwable t) {
        Log.e(TAG, "API call failed: " + t.getMessage());
        showToast("Lỗi kết nối đến server.");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
