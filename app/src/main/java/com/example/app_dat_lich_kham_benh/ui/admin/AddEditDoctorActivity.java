package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.data.DatabaseConnector;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddEditDoctorActivity extends AppCompatActivity {

    private static final String TAG = "AddEditDoctorActivity";

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword;
    private AutoCompleteTextView actvSpecialty;
    private Button btnSave;

    private boolean isEditMode = false;
    private int doctorIdToEdit = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_doctor);

        etFirstName = findViewById(R.id.et_doctor_firstname);
        etLastName = findViewById(R.id.et_doctor_lastname);
        etEmail = findViewById(R.id.et_doctor_email);
        etPassword = findViewById(R.id.et_doctor_password);
        actvSpecialty = findViewById(R.id.actv_doctor_specialty);
        btnSave = findViewById(R.id.btn_save_doctor);

        // Setup the dropdown menu
        String[] specialties = new String[]{"Tim mạch", "Da liễu", "Hô hấp"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                specialties
        );
        actvSpecialty.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("DOCTOR_ID")) {
            isEditMode = true;
            doctorIdToEdit = intent.getIntExtra("DOCTOR_ID", -1);

            setTitle("Chỉnh sửa Bác sĩ");
            etFirstName.setText(intent.getStringExtra("DOCTOR_FIRST_NAME"));
            etLastName.setText(intent.getStringExtra("DOCTOR_LAST_NAME"));
            etEmail.setText(intent.getStringExtra("DOCTOR_EMAIL"));
            actvSpecialty.setText(intent.getStringExtra("DOCTOR_SPECIALTY"), false);

            etPassword.setHint("Để trống nếu không đổi mật khẩu");
            btnSave.setText("Cập nhật thông tin");
        } else {
            setTitle("Thêm Bác sĩ mới");
        }

        btnSave.setOnClickListener(v -> saveDoctor());
    }

    private void saveDoctor() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String specialty = actvSpecialty.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || specialty.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin (trừ mật khẩu khi sửa).");
            return;
        }
        if (!isEditMode && password.isEmpty()) {
            showToast("Vui lòng nhập mật khẩu cho tài khoản mới.");
            return;
        }

        new Thread(() -> {
            if (isEditMode) {
                updateDoctorInDb(firstName, lastName, email, password, specialty);
            } else {
                insertDoctorInDb(firstName, lastName, email, password, specialty);
            }
        }).start();
    }

    private void insertDoctorInDb(String firstName, String lastName, String email, String password, String chuyenMon) {
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
            if (connection == null) {
                showToast("Lỗi kết nối cơ sở dữ liệu.");
                return;
            }

            connection.setAutoCommit(false);

            String hashedPassword = hashPassword(password);
            String userSql = "INSERT INTO User (firstname, lastname, email, password, role, is_active) VALUES (?, ?, ?, ?, 'doctor', 1)";
            PreparedStatement userStatement = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userStatement.setString(1, firstName);
            userStatement.setString(2, lastName);
            userStatement.setString(3, email);
            userStatement.setString(4, hashedPassword);

            if (userStatement.executeUpdate() == 0) {
                throw new SQLException("Tạo user thất bại.");
            }

            int newUserId;
            try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newUserId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Tạo user thất bại, không lấy được ID.");
                }
            }
            userStatement.close();

            String doctorSql = "INSERT INTO BacSi (userId, chuyenMon) VALUES (?, ?)";
            PreparedStatement doctorStatement = connection.prepareStatement(doctorSql);
            doctorStatement.setInt(1, newUserId);
            doctorStatement.setString(2, chuyenMon);

            if (doctorStatement.executeUpdate() == 0) {
                throw new SQLException("Tạo chi tiết bác sĩ thất bại.");
            }
            doctorStatement.close();

            connection.commit();
            showToast("Thêm bác sĩ thành công!");
            finish();

        } catch (SQLException e) {
            if (connection != null) try { connection.rollback(); } catch (SQLException ex) { Log.e(TAG, "Rollback thất bại: " + ex.getMessage()); }
            handleSqlException(e);
        } catch (Exception e) {
            if (connection != null) try { connection.rollback(); } catch (SQLException ex) { Log.e(TAG, "Rollback thất bại: " + ex.getMessage()); }
            handleGenericException(e);
        } finally {
            if (connection != null) try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { Log.e(TAG, "Đóng kết nối thất bại: " + e.getMessage()); }
        }
    }

    private void updateDoctorInDb(String firstName, String lastName, String email, String password, String chuyenMon) {
        Connection connection = null;
        try {
            connection = DatabaseConnector.getConnection();
            if (connection == null) {
                showToast("Lỗi kết nối cơ sở dữ liệu.");
                return;
            }
            connection.setAutoCommit(false);

            // Update User table
            PreparedStatement userStatement;
            if (password.isEmpty()) {
                String sql = "UPDATE User SET firstname = ?, lastname = ?, email = ? WHERE userId = ?";
                userStatement = connection.prepareStatement(sql);
                userStatement.setString(1, firstName);
                userStatement.setString(2, lastName);
                userStatement.setString(3, email);
                userStatement.setInt(4, doctorIdToEdit);
            } else {
                String hashedPassword = hashPassword(password);
                String sql = "UPDATE User SET firstname = ?, lastname = ?, email = ?, password = ? WHERE userId = ?";
                userStatement = connection.prepareStatement(sql);
                userStatement.setString(1, firstName);
                userStatement.setString(2, lastName);
                userStatement.setString(3, email);
                userStatement.setString(4, hashedPassword);
                userStatement.setInt(5, doctorIdToEdit);
            }
            userStatement.executeUpdate();
            userStatement.close();

            // Update bacSi table
            String doctorSql = "UPDATE BacSi SET chuyenMon = ? WHERE userId = ?";
            PreparedStatement doctorStatement = connection.prepareStatement(doctorSql);
            doctorStatement.setString(1, chuyenMon);
            doctorStatement.setInt(2, doctorIdToEdit);
            doctorStatement.executeUpdate();
            doctorStatement.close();

            connection.commit();
            showToast("Cập nhật thành công!");
            finish();

        } catch (SQLException e) {
            if (connection != null) try { connection.rollback(); } catch (SQLException ex) { Log.e(TAG, "Rollback thất bại: " + ex.getMessage()); }
            handleSqlException(e);
        } catch (Exception e) {
            if (connection != null) try { connection.rollback(); } catch (SQLException ex) { Log.e(TAG, "Rollback thất bại: " + ex.getMessage()); }
            handleGenericException(e);
        } finally {
            if (connection != null) try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { Log.e(TAG, "Đóng kết nối thất bại: " + e.getMessage()); }
        }
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

    private void handleSqlException(SQLException e) {
        Log.e(TAG, "SQLException: " + e.getMessage());
        if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
            showToast("Email này đã được sử dụng.");
        } else {
            showToast("Lỗi cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void handleGenericException(Exception e) {
        Log.e(TAG, "Exception: " + e.getMessage());
        showToast("Đã có lỗi xảy ra.");
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(AddEditDoctorActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
