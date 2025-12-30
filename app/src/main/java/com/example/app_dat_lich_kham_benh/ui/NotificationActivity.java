package com.example.app_dat_lich_kham_benh.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.NotificationAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.ThongBao;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rcvNotifications;
    private NotificationAdapter notificationAdapter;
    private List<ThongBao> listThongBao;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rcvNotifications = findViewById(R.id.rcvNotifications);

         ImageView imgBack = findViewById(R.id.btnBack);
         if (imgBack != null) imgBack.setOnClickListener(v -> finish());

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);
        listThongBao = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvNotifications.setLayoutManager(linearLayoutManager);

        loadNotifications();
    }

    private void loadNotifications() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int userId = sessionManager.getUserId();

        apiService.getNotifications(userId).enqueue(new Callback<List<ThongBao>>() {
            @Override
            public void onResponse(Call<List<ThongBao>> call, Response<List<ThongBao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listThongBao = response.body();

                    notificationAdapter = new NotificationAdapter(NotificationActivity.this, listThongBao);
                    rcvNotifications.setAdapter(notificationAdapter);


                } else {
                    Toast.makeText(NotificationActivity.this, "Không tải được thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ThongBao>> call, Throwable t) {
                Log.e("NotificationActivity", "Lỗi: " + t.getMessage());
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}