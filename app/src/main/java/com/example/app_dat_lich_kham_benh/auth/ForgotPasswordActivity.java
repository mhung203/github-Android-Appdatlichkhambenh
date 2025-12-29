package com.example.app_dat_lich_kham_benh.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.BuildConfig;
import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.api.ApiClient;
import com.example.app_dat_lich_kham_benh.api.model.User;
import com.example.app_dat_lich_kham_benh.api.service.ApiService;
import com.example.app_dat_lich_kham_benh.util.GMailSender;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private static final String SENDER_EMAIL = "repon.nolua@gmail.com";
    private static final String SENDER_PASSWORD = BuildConfig.GMAIL_APP_PASSWORD;

    private EditText etEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnResetPassword;
    private LinearLayout resetPasswordSection;

    private ApiService apiService;
    private User userToReset;
    private String generatedOtp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = ApiClient.getApiService();

        etEmail = findViewById(R.id.email_edittext_forgot);
        etOtp = findViewById(R.id.otp_edittext_forgot);
        etNewPassword = findViewById(R.id.new_password_edittext);
        btnSendOtp = findViewById(R.id.send_otp_button_forgot);
        btnResetPassword = findViewById(R.id.reset_password_button);
        resetPasswordSection = findViewById(R.id.reset_password_section);

        btnSendOtp.setOnClickListener(v -> handleSendOtp());
        btnResetPassword.setOnClickListener(v -> handleResetPassword());
    }

    private void handleSendOtp() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Vui lòng nhập email của bạn.");
            return;
        }

        btnSendOtp.setEnabled(false);
        showToast("Đang tìm kiếm tài khoản...");

        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        if (user.getEmail().equalsIgnoreCase(email)) {
                            userToReset = user;
                            sendOtpToUser();
                            return;
                        }
                    }
                    showToast("Email không tồn tại trong hệ thống.");
                    btnSendOtp.setEnabled(true);
                } else {
                    showToast("Lỗi khi tìm kiếm tài khoản.");
                    btnSendOtp.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Error finding user: " + t.getMessage());
                showToast("Lỗi kết nối.");
                btnSendOtp.setEnabled(true);
            }
        });
    }

    private void sendOtpToUser() {
        showToast("Đang gửi mã OTP...");
        new Thread(() -> {
            try {
                generatedOtp = String.format("%06d", new Random().nextInt(999999));
                sendOtpEmail(userToReset.getEmail(), generatedOtp);
                runOnUiThread(() -> {
                    showToast("Mã OTP đã được gửi đến email của bạn.");
                    resetPasswordSection.setVisibility(View.VISIBLE);
                    btnResetPassword.setVisibility(View.VISIBLE);
                });
            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: " + e.getMessage());
                showToast("Lỗi gửi email.");
            }
        }).start();
    }

    private void handleResetPassword() {
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (otp.length() != 6 || newPassword.isEmpty()) {
            showToast("Vui lòng nhập mã OTP và mật khẩu mới.");
            return;
        }

        if (userToReset != null && generatedOtp != null && generatedOtp.equals(otp)) {
            try {
                String hashedPassword = hashPassword(newPassword);
                userToReset.setPassword(hashedPassword);

                apiService.updateUser(userToReset.getUserId(), userToReset).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            showToast("Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Lỗi cập nhật mật khẩu.");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Error resetting password: " + t.getMessage());
                        showToast("Lỗi kết nối.");
                    }
                });

            } catch (NoSuchAlgorithmException e) {
                showToast("Lỗi bảo mật.");
            }
        } else {
            showToast("Mã OTP không chính xác.");
        }
    }

    private void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        GMailSender sender = new GMailSender(SENDER_EMAIL, SENDER_PASSWORD);
        sender.sendMail("Mã khôi phục mật khẩu của bạn",
                "Mã OTP để đặt lại mật khẩu của bạn là: " + otp,
                recipientEmail);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
