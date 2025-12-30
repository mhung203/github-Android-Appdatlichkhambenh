package com.example.app_dat_lich_kham_benh.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.InboxAdapter;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.dto.ChatContactDTO;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView rvInbox;
    private InboxAdapter adapter;
    private int myId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        rvInbox = findViewById(R.id.rvInbox);
        rvInbox.setLayoutManager(new LinearLayoutManager(this));

         SessionManager session = new SessionManager(this);
         myId = session.getUserId();

        adapter = new InboxAdapter(this, myId);
        rvInbox.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInbox();
    }

    private void loadInbox() {
        ApiService apiService = ApiClient.getApiService();

        sessionManager = new SessionManager(this);
        String myRole = sessionManager.getUserRole();
        Log.d("InboxDebug", "Role hiện tại: " + myRole);

        Call<List<ChatContactDTO>> call;

        if ("doctor".equalsIgnoreCase(myRole)) {
            call = apiService.getAllPatients();
        } else {
            call = apiService.getAllDoctors();
        }
        call.enqueue(new Callback<List<ChatContactDTO>>() {
            @Override
            public void onResponse(Call<List<ChatContactDTO>> call, Response<List<ChatContactDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body());
                } else {
                    Toast.makeText(InboxActivity.this, "Danh sách trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatContactDTO>> call, Throwable t) {
                Log.e("InboxDebug", "Lỗi API: " + t.getMessage());
            }
        });
    }
}