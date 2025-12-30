package com.example.app_dat_lich_kham_benh.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.dto.LoginRequestDTO;
import com.example.app_dat_lich_kham_benh.api.model.BacSi;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.ui.MainActivity;
import com.example.app_dat_lich_kham_benh.ui.admin.AdminDashboardActivity;
import com.example.app_dat_lich_kham_benh.ui.doctor.DoctorScheduleActivity;
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
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    sessionManager.createLoginSession(user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole());
                    showToast("Đăng nhập thành công!");

                    // Phân luồng dựa trên vai trò
                    if ("admin".equalsIgnoreCase(user.getRole())) {
                        navigateTo(AdminDashboardActivity.class, -1);
                    } else if ("doctor".equalsIgnoreCase(user.getRole())) {
                        // Cần lấy doctorId trước khi chuyển màn hình
                        getDoctorIdAndProceed(user.getUserId());
                    } else {
                        navigateTo(MainActivity.class, -1);
                    }
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
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Login failed: " + t.getMessage());
                showToast("Lỗi kết nối. Vui lòng thử lại.");
            }
        });
    }

    private void getDoctorIdAndProceed(int userId) {
        apiService.getBacSiByUserId(userId).enqueue(new Callback<BacSi>() {
            @Override
            public void onResponse(@NonNull Call<BacSi> call, @NonNull Response<BacSi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BacSi bacSi = response.body();
                    sessionManager.setBacSiId(bacSi.getBacSiId());
                    navigateTo(DoctorScheduleActivity.class, bacSi.getBacSiId());
                } else {
                    Log.e(TAG, "Không thể lấy thông tin bác sĩ cho userId: " + userId);
                    showToast("Đăng nhập thành công nhưng không thể lấy thông tin bác sĩ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BacSi> call, @NonNull Throwable t) {
                Log.e(TAG, "API call to get doctor details failed: " + t.getMessage());
                showToast("Lỗi kết nối khi lấy thông tin bác sĩ.");
            }
        });
    }

    private void navigateTo(Class<?> activityClass, int doctorId) {
        Intent intent = new Intent(LoginActivity.this, activityClass);
        if (doctorId != -1) {
            intent.putExtra("DOCTOR_ID", doctorId);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
