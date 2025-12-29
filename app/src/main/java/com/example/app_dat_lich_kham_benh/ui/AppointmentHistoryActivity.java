package com.example.app_dat_lich_kham_benh.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.AppointmentHistoryAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.LichHen;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentHistoryActivity extends AppCompatActivity {
    private RecyclerView rvAppointmentHistory;
    private AppointmentHistoryAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private TextView tvEmptyHistory;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        rvAppointmentHistory = findViewById(R.id.rvAppointmentHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvAppointmentHistory.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        loadAppointmentHistory();
    }

    private void loadAppointmentHistory() {
        rvAppointmentHistory.setVisibility(View.GONE);
        tvEmptyHistory.setVisibility(View.GONE);

        long userId = sessionManager.getUserId();
        apiService.getLichHenByUserId((int) userId).enqueue(new Callback<List<LichHen>>() {
            @Override
            public void onResponse(Call<List<LichHen>> call, Response<List<LichHen>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter = new AppointmentHistoryAdapter(response.body());
                    rvAppointmentHistory.setAdapter(adapter);
                    rvAppointmentHistory.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<LichHen>> call, Throwable t) {
                tvEmptyHistory.setText("Lỗi khi tải dữ liệu.");
                tvEmptyHistory.setVisibility(View.VISIBLE);
            }
        });
    }
}
