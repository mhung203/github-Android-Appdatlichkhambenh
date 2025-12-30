package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.Khoa;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboard";
    private ApiService apiService;
    private SessionManager sessionManager;
    private TextView tvDoctorCount, tvPatientCount, tvDepartmentCount;
    private CardView cardManageSchedule, cardManageDoctors, cardManageDept, cardLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);
        initViews();
        setEvents();
        loadDashboardData();
    }

    private void initViews() {
        tvDoctorCount = findViewById(R.id.tv_doctor_count);
        tvPatientCount = findViewById(R.id.tv_patient_count);
        tvDepartmentCount = findViewById(R.id.tv_department_count);
        cardManageSchedule = findViewById(R.id.card_manage_schedule);
        cardManageDoctors = findViewById(R.id.card_manage_doctors);
        cardManageDept = findViewById(R.id.card_manage_departments);
        cardLogout = findViewById(R.id.card_logout);
    }

    private void setEvents() {
        cardManageSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageScheduleActivity.class);
            startActivity(intent);
        });
        cardManageDoctors.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageDoctorsActivity.class);
            startActivity(intent);
        });
        cardManageDept.setOnClickListener(v -> {
             Intent intent = new Intent(AdminDashboardActivity.this, ManageDepartmentActivity.class);
             startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> {

            sessionManager.logoutUser();


            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadDashboardData() {

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