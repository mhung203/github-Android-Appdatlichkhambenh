package com.example.app_dat_lich_kham_benh.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String SENDER_EMAIL = "repon.nolua@gmail.com";
    private static final String SENDER_PASSWORD = "iwwq jdsg tpge xohn";

    private EditText etName, etEmail, etPassword, etOtp;
    private Button btnSendOtp, btnRegister;
    private TextView tvGoToLogin;
    private LinearLayout otpSection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.name_edittext);
        etEmail = findViewById(R.id.email_edittext_register);
        etPassword = findViewById(R.id.password_edittext_register);
        etOtp = findViewById(R.id.otp_edittext);
        btnSendOtp = findViewById(R.id.send_otp_button);
        btnRegister = findViewById(R.id.register_button);
        tvGoToLogin = findViewById(R.id.login_textview);
        otpSection = findViewById(R.id.otp_section);
        btnSendOtp.setOnClickListener(v -> handleSendOtp());
        btnRegister.setOnClickListener(v -> handleRegister());
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleSendOtp() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ Tên, Email và Mật khẩu.");
            return;
        }

        btnSendOtp.setEnabled(false); // Vô hiệu hóa nút trong khi gửi
        showToast("Đang gửi mã OTP...");

        new Thread(() -> {
            try {
                String otp = String.format("%06d", new Random().nextInt(999999));
                Timestamp otpExpiry = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5 phút
                String hashedPassword = hashPassword(password);

                Connection connection = DatabaseConnector.getConnection();
                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    runOnUiThread(() -> btnSendOtp.setEnabled(true));
                    return;
                }

                // Lưu hoặc cập nhật người dùng với trạng thái chưa kích hoạt
                String sql = "INSERT INTO User (name, email, password, otp_code, otp_expiry, is_active) VALUES (?, ?, ?, ?, ?, ?)" +
                             " ON DUPLICATE KEY UPDATE name=?, password=?, otp_code=?, otp_expiry=?, is_active=false";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, hashedPassword);
                statement.setString(4, otp);
                statement.setTimestamp(5, otpExpiry);
                statement.setBoolean(6, false);
                statement.setString(7, name);
                statement.setString(8, hashedPassword);
                statement.setString(9, otp);
                statement.setTimestamp(10, otpExpiry);

                statement.executeUpdate();
                statement.close();
                connection.close();

                // Gửi email
                sendOtpEmail(email, otp);

                // Cập nhật giao diện
                runOnUiThread(() -> {
                    showToast("Mã OTP đã được gửi đến email của bạn.");
                    otpSection.setVisibility(View.VISIBLE);
                    btnSendOtp.setText("Gửi lại mã");
                    btnSendOtp.setEnabled(true);
                });

            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException: " + e.getMessage());
                showToast("Lỗi gửi email. Vui lòng kiểm tra lại thông tin.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi cơ sở dữ liệu: " + e.getMessage());
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                showToast("Đã có lỗi xảy ra.");
                runOnUiThread(() -> btnSendOtp.setEnabled(true));
            }
        }).start();
    }

    private void handleRegister() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();

        if (otp.length() != 6) {
            showToast("Mã OTP phải có 6 chữ số.");
            return;
        }

        new Thread(() -> {
            try {
                Connection connection = DatabaseConnector.getConnection();
                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    return;
                }

                // Tìm người dùng và kiểm tra mã OTP
                String sql = "SELECT otp_code, otp_expiry FROM User WHERE email = ? AND is_active = false";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String dbOtp = resultSet.getString("otp_code");
                    Timestamp dbOtpExpiry = resultSet.getTimestamp("otp_expiry");

                    if (dbOtp.equals(otp)) {
                        if (dbOtpExpiry.after(new Timestamp(System.currentTimeMillis()))) {
                            // Kích hoạt tài khoản
                            String updateSql = "UPDATE User SET is_active = true, otp_code = NULL, otp_expiry = NULL WHERE email = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                            updateStatement.setString(1, email);
                            int rowsAffected = updateStatement.executeUpdate();
                            updateStatement.close();

                            if (rowsAffected > 0) {
                                showToast("Đăng ký thành công! Vui lòng đăng nhập.");
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("Lỗi kích hoạt tài khoản.");
                            }
                        } else {
                            showToast("Mã OTP đã hết hạn. Vui lòng gửi lại mã.");
                        }
                    } else {
                        showToast("Mã OTP không chính xác.");
                    }
                } else {
                    showToast("Email không hợp lệ hoặc chưa yêu cầu mã OTP.");
                }

                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi cơ sở dữ liệu: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
                showToast("Đã có lỗi xảy ra.");
            }
        }).start();
    }

    private void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        GMailSender sender = new GMailSender(SENDER_EMAIL, SENDER_PASSWORD);
        sender.sendMail("Mã xác thực OTP của bạn",
                "Mã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn trong 5 phút.",
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
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
