package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;

public class AdminDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnManageDoctors = findViewById(R.id.btn_manage_doctors);
        btnManageDoctors.setOnClickListener(v -> {
            // We will create this activity in the next steps
            Intent intent = new Intent(AdminDashboardActivity.this, ManageDoctorsActivity.class);
            startActivity(intent);
        });
    }
}
