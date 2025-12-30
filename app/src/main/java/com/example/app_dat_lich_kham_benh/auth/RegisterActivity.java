package com.example.app_dat_lich_kham_benh.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.BuildConfig;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.GMailSender;

import javax.mail.MessagingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String SENDER_EMAIL = "repon.nolua@gmail.com";
    private static final String SENDER_PASSWORD = BuildConfig.GMAIL_APP_PASSWORD;

    private EditText etFirstName, etLastName, etEmail, etPassword, etOtp;
    private Button btnSendOtp, btnRegister;
    private TextView tvGoToLogin;
    private LinearLayout otpSection;
    private ImageButton backButton;

    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = ApiClient.getApiService();

        etFirstName = findViewById(R.id.firstname_edittext);
        etLastName = findViewById(R.id.lastname_edittext);
        etEmail = findViewById(R.id.email_edittext_register);
        etPassword = findViewById(R.id.password_edittext_register);
        etOtp = findViewById(R.id.otp_edittext);
        btnSendOtp = findViewById(R.id.send_otp_button);
        btnRegister = findViewById(R.id.register_button);
        tvGoToLogin = findViewById(R.id.login_textview);
        otpSection = findViewById(R.id.otp_section);
        backButton = findViewById(R.id.back_button);

        btnSendOtp.setOnClickListener(v -> handleSendOtp());
        btnRegister.setOnClickListener(v -> handleRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        backButton.setOnClickListener(v -> finish());
    }

    private void handleSendOtp() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Vui lòng điền email để nhận mã OTP.");
            return;
        }

        btnSendOtp.setEnabled(false);
        showToast("Đang gửi mã OTP...");
        new Thread(() -> {
            try {
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                sendOtpEmail(email, otp);
                runOnUiThread(() -> {
                    showToast("Mã OTP đã được gửi đến email của bạn.");
                    otpSection.setVisibility(View.VISIBLE);
                    btnSendOtp.setText("Gửi lại mã");
                    btnSendOtp.setEnabled(true);
                });
            } catch (MessagingException e) {
                Log.e(TAG, "Error sending OTP email: " + e.getMessage());
                showToast("Lỗi gửi OTP. Vui lòng thử lại.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            }
        }).start();
    }

    private void handleRegister() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin.");
            return;
        }
        
        User newUser = new User();
        newUser.setFirstname(firstName);
        newUser.setLastname(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole("Bệnh nhân");

        apiService.createUser(newUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showToast("Đăng ký thành công! Vui lòng đăng nhập.");
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Đăng ký thất bại. Email có thể đã tồn tại.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = "Đăng ký thất bại (Lỗi: " + response.code() + ")";
                            Log.e(TAG, "Registration error body: " + response.errorBody().string());
                        } catch (java.io.IOException e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    showToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Registration failed: " + t.getMessage());
                showToast("Lỗi kết nối. Vui lòng kiểm tra lại.");
            }
        });
    }

    private void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        GMailSender sender = new GMailSender(SENDER_EMAIL, SENDER_PASSWORD);
        sender.sendMail("Mã xác thực OTP của bạn",
                "Mã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn trong 5 phút.",
                recipientEmail);
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
