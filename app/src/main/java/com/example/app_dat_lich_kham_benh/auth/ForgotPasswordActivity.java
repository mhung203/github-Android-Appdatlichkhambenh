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
import com.example.app_dat_lich_kham_benh.data.DatabaseConnector;
import com.example.app_dat_lich_kham_benh.util.GMailSender;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import javax.mail.MessagingException;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private static final String SENDER_EMAIL = "repon.nolua@gmail.com";
    private static final String SENDER_PASSWORD = BuildConfig.GMAIL_APP_PASSWORD;

    private EditText etEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnResetPassword;
    private LinearLayout resetPasswordSection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

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
        showToast("Đang gửi mã OTP...");

        new Thread(() -> {
            try {
                Connection connection = DatabaseConnector.getConnection();
                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    runOnUiThread(() -> btnSendOtp.setEnabled(true));
                    return;
                }

                // Kiểm tra xem email có tồn tại không
                String checkEmailSql = "SELECT userId FROM User WHERE email = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkEmailSql);
                checkStatement.setString(1, email);
                ResultSet rs = checkStatement.executeQuery();

                if (!rs.next()) {
                    showToast("Email không tồn tại trong hệ thống.");
                    runOnUiThread(() -> btnSendOtp.setEnabled(true));
                    return;
                }

                String otp = String.format("%06d", new Random().nextInt(999999));
                Timestamp otpExpiry = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5 phút

                String updateOtpSql = "UPDATE User SET otp_code = ?, otp_expiry = ? WHERE email = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateOtpSql);
                updateStatement.setString(1, otp);
                updateStatement.setTimestamp(2, otpExpiry);
                updateStatement.setString(3, email);
                updateStatement.executeUpdate();

                sendOtpEmail(email, otp);

                runOnUiThread(() -> {
                    showToast("Mã OTP đã được gửi đến email của bạn.");
                    resetPasswordSection.setVisibility(View.VISIBLE);
                    btnSendOtp.setEnabled(true);
                });

                rs.close();
                checkStatement.close();
                updateStatement.close();
                connection.close();

            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: " + e.getMessage());
                showToast("Lỗi gửi email.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi cơ sở dữ liệu.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                showToast("Đã có lỗi xảy ra.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            }
        }).start();
    }

    private void handleResetPassword() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (otp.length() != 6 || newPassword.isEmpty()) {
            showToast("Vui lòng nhập mã OTP và mật khẩu mới.");
            return;
        }

        new Thread(() -> {
            try {
                Connection connection = DatabaseConnector.getConnection();
                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    return;
                }

                String sql = "SELECT otp_code, otp_expiry FROM User WHERE email = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String dbOtp = resultSet.getString("otp_code");
                    Timestamp dbOtpExpiry = resultSet.getTimestamp("otp_expiry");

                    if (dbOtp != null && dbOtp.equals(otp)) {
                        if (dbOtpExpiry.after(new Timestamp(System.currentTimeMillis()))) {
                            String hashedPassword = hashPassword(newPassword);
                            String updateSql = "UPDATE User SET password = ?, otp_code = NULL, otp_expiry = NULL WHERE email = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                            updateStatement.setString(1, hashedPassword);
                            updateStatement.setString(2, email);
                            
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                showToast("Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Lỗi cập nhật mật khẩu.");
                            }
                            updateStatement.close();
                        } else {
                            showToast("Mã OTP đã hết hạn.");
                        }
                    } else {
                        showToast("Mã OTP không chính xác.");
                    }
                } else {
                    showToast("Email không hợp lệ.");
                }

                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi cơ sở dữ liệu.");
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                showToast("Đã có lỗi xảy ra.");
            }
        }).start();
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
