package com.example.app_dat_lich_kham_benh.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.ui.InboxActivity;
import com.example.app_dat_lich_kham_benh.ui.adapter.LichHenAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView tvNoAppointments;
    private LichHenAdapter lichHenAdapter;
    private ApiService apiService;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabChat;
    private int currentDoctorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_schedule);

        calendarView = findViewById(R.id.calendar_view_doctor);
        recyclerView = findViewById(R.id.recycler_view_appointments);
        tvNoAppointments = findViewById(R.id.tv_no_appointments);
        bottomAppBar = findViewById(R.id.bottom_app_bar);
        fabChat = findViewById(R.id.fab_chat);

        currentDoctorId = getIntent().getIntExtra("DOCTOR_ID", -1);
        if (currentDoctorId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bác sĩ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        apiService = ApiClient.getApiService();
        setupRecyclerView();
        setupBottomBarAndFab();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            loadAppointments(currentDoctorId, selectedDate);
        });

        loadInitialAppointments();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lichHenAdapter = new LichHenAdapter(new ArrayList<>());
        recyclerView.setAdapter(lichHenAdapter);
    }

    private void setupBottomBarAndFab() {
        bottomAppBar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_doctor_account) {
                Intent intent = new Intent(DoctorScheduleActivity.this, DoctorAccountActivity.class);
                startActivity(intent);
            }
            return true;
        });
        fabChat.setOnClickListener(v -> {
            Intent intent = new Intent(DoctorScheduleActivity.this, InboxActivity.class);
            startActivity(intent);
        });
    }

    private void loadInitialAppointments() {
        Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
        String todayDate = String.format("%d-%02d-%02d", year, month + 1, day);
        loadAppointments(currentDoctorId, todayDate);
    }

    private void loadAppointments(int bacSiId, String date) {
        Call<List<LichHen>> call = apiService.getLichKham(bacSiId, date);
        call.enqueue(new Callback<List<LichHen>>() {
            @Override
            public void onResponse(@NonNull Call<List<LichHen>> call, @NonNull Response<List<LichHen>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    lichHenAdapter.setData(response.body());
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoAppointments.setVisibility(View.GONE);
                } else {
                    lichHenAdapter.setData(new ArrayList<>());
                    recyclerView.setVisibility(View.GONE);
                    tvNoAppointments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LichHen>> call, @NonNull Throwable t) {
                Toast.makeText(DoctorScheduleActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                lichHenAdapter.setData(new ArrayList<>());
                recyclerView.setVisibility(View.GONE);
                tvNoAppointments.setVisibility(View.VISIBLE);
            }
        });
    }
}
