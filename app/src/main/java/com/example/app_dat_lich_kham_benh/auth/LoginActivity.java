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
import com.example.app_dat_lich_kham_benh.data.DatabaseConnector;
import com.example.app_dat_lich_kham_benh.ui.MainActivity;
import com.example.app_dat_lich_kham_benh.util.SessionManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());
        etEmail = findViewById(R.id.email_edittext);
        etPassword = findViewById(R.id.password_edittext);
        btnLogin = findViewById(R.id.login_button);
        tvGoToRegister = findViewById(R.id.register_textview);
        btnLogin.setOnClickListener(v -> handleLogin());
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ Email và Mật khẩu.");
            return;
        }

        new Thread(() -> {
            try {
                String hashedPassword = hashPassword(password);
                Connection connection = DatabaseConnector.getConnection();

                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    return;
                }

                String sql = "SELECT userId, name, password, is_active FROM User WHERE email = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String dbPassword = resultSet.getString("password");
                    boolean isActive = resultSet.getBoolean("is_active");

                    if (!isActive) {
                        showToast("Tài khoản của bạn chưa được kích hoạt. Vui lòng kiểm tra email.");
                    } else if (dbPassword.equals(hashedPassword)) {
                        // Đăng nhập thành công
                        int userId = resultSet.getInt("userId");
                        String name = resultSet.getString("name");
                        sessionManager.createLoginSession(userId, name, email);

                        showToast("Đăng nhập thành công!");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Sai mật khẩu. Vui lòng thử lại.");
                    }
                } else {
                    showToast("Tài khoản không tồn tại.");
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
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show());
    }
}
