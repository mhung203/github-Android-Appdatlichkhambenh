package com.example.app_dat_lich_kham_benh.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.dto.LoginRequestDTO;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.ui.MainActivity;
import com.example.app_dat_lich_kham_benh.ui.admin.AdminDashboardActivity;
import com.example.app_dat_lich_kham_benh.ui.doctor.DoctorDashboardActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        apiService = ApiClient.getApiService();

        etEmail = findViewById(R.id.email_edittext);
        etPassword = findViewById(R.id.password_edittext);
        btnLogin = findViewById(R.id.login_button);
        tvGoToRegister = findViewById(R.id.register_textview);
        tvForgotPassword = findViewById(R.id.forgot_password_textview);

        btnLogin.setOnClickListener(v -> handleLogin());
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ Email và Mật khẩu.");
            return;
        }

        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);

        apiService.login(loginRequest).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    
                    // --- DÒNG LOG CHẨN ĐOÁN --- 
                    Log.d(TAG, "Đăng nhập thành công. Vai trò nhận được từ server: '" + user.getRole() + "'");

                    sessionManager.createLoginSession(user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole());

                    showToast("Đăng nhập thành công!");

                    Intent intent;
                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                    } else if ("doctor".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    String errorMessage = "Sai thông tin đăng nhập.";
                     if (response.code() == 401) { // Unauthorized
                        errorMessage = "Sai mật khẩu. Vui lòng thử lại.";
                    } else if (response.code() == 404) { // Not Found
                        errorMessage = "Tài khoản không tồn tại.";
                    }
                    showToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Login failed: " + t.getMessage());
                showToast("Lỗi kết nối. Vui lòng thử lại.");
            }
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
