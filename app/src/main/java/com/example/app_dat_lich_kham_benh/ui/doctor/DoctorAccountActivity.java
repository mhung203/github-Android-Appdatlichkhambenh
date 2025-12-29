package com.example.app_dat_lich_kham_benh.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.auth.LoginActivity;
import com.example.app_dat_lich_kham_benh.auth.ProfileActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

public class DoctorAccountActivity extends AppCompatActivity {

    private TextView doctorName, doctorEmail, updateProfileButton, logoutButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_account);

        sessionManager = new SessionManager(this);

        doctorName = findViewById(R.id.doctor_name);
        doctorEmail = findViewById(R.id.doctor_email);
        updateProfileButton = findViewById(R.id.update_doctor_profile_button);
        logoutButton = findViewById(R.id.doctor_logout_button);

        // Load doctor info
        if (sessionManager.isLoggedIn()) {
            doctorName.setText(sessionManager.getUserName());
            doctorEmail.setText(sessionManager.getUserEmail());
        } else {
            // If not logged in, redirect to LoginActivity
            redirectToLogin();
        }

        // Set click listeners
        updateProfileButton.setOnClickListener(v -> {
            // Assuming ProfileActivity can be used to edit doctor's profile
            Intent intent = new Intent(DoctorAccountActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.logoutUser();
            redirectToLogin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to the activity
        if (sessionManager.isLoggedIn()) {
            doctorName.setText(sessionManager.getUserName());
            doctorEmail.setText(sessionManager.getUserEmail());
        } else {
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(DoctorAccountActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
