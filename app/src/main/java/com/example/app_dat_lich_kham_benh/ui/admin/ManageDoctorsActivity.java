package com.example.app_dat_lich_kham_benh.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dat_lich_kham_benh.R;
import com.example.app_dat_lich_kham_benh.adapter.DoctorAdapter;
import com.example.app_dat_lich_kham_benh.data.DatabaseConnector;
import com.example.app_dat_lich_kham_benh.model.Doctor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageDoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnItemClickListener {

    private static final String TAG = "ManageDoctorsActivity";

    private RecyclerView recyclerViewDoctors;
    private FloatingActionButton fabAddDoctor;
    private DoctorAdapter doctorAdapter;
    private List<Doctor> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_doctors);

        recyclerViewDoctors = findViewById(R.id.recycler_view_doctors);
        fabAddDoctor = findViewById(R.id.fab_add_doctor);

        setupRecyclerView();

        fabAddDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(ManageDoctorsActivity.this, AddEditDoctorActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDoctors();
    }

    private void setupRecyclerView() {
        doctorAdapter = new DoctorAdapter(doctorList, this);
        recyclerViewDoctors.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDoctors.setAdapter(doctorAdapter);
    }

    private void loadDoctors() {
        new Thread(() -> {
            List<Doctor> loadedDoctors = new ArrayList<>();
            try {
                Connection connection = DatabaseConnector.getConnection();
                if (connection == null) {
                    showToast("Lỗi kết nối đến cơ sở dữ liệu.");
                    return;
                }

                String sql = "SELECT u.userId, u.firstname, u.lastname, u.email, b.chuyenMon FROM User u JOIN BacSi b ON u.userId = b.userId WHERE u.role = 'doctor'";
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("userId");
                    String firstName = resultSet.getString("firstname");
                    String lastName = resultSet.getString("lastname");
                    String email = resultSet.getString("email");
                    String specialty = resultSet.getString("chuyenMon");
                    loadedDoctors.add(new Doctor(id, firstName, lastName, email, specialty != null ? specialty : "Chưa có"));
                }

                resultSet.close();
                statement.close();
                connection.close();

                runOnUiThread(() -> {
                    doctorList.clear();
                    doctorList.addAll(loadedDoctors);
                    doctorAdapter.notifyDataSetChanged();
                });

            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi khi tải danh sách bác sĩ.");
            }
        }).start();
    }

    @Override
    public void onEditClick(Doctor doctor) {
        Intent intent = new Intent(this, AddEditDoctorActivity.class);
        intent.putExtra("DOCTOR_ID", doctor.getId());
        intent.putExtra("DOCTOR_FIRST_NAME", doctor.getFirstName());
        intent.putExtra("DOCTOR_LAST_NAME", doctor.getLastName());
        intent.putExtra("DOCTOR_EMAIL", doctor.getEmail());
        intent.putExtra("DOCTOR_SPECIALTY", doctor.getSpecialty());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Doctor doctor) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Xóa")
                .setMessage("Bạn có chắc chắn muốn xóa bác sĩ '" + doctor.getFullName() + "'? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> deleteDoctorFromDb(doctor.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteDoctorFromDb(int doctorId) {
        Connection connection = null;
        new Thread(() -> {
            try {
                Connection conn = DatabaseConnector.getConnection();
                if (conn == null) {
                    showToast("Lỗi kết nối cơ sở dữ liệu.");
                    return;
                }
                conn.setAutoCommit(false);

                // Delete from child table first
                String deleteBacSiSql = "DELETE FROM BacSi WHERE userId = ?";
                PreparedStatement bacSiStatement = conn.prepareStatement(deleteBacSiSql);
                bacSiStatement.setInt(1, doctorId);
                bacSiStatement.executeUpdate();
                bacSiStatement.close();

                // Then delete from parent table
                String deleteUserSql = "DELETE FROM User WHERE userId = ?";
                PreparedStatement userStatement = conn.prepareStatement(deleteUserSql);
                userStatement.setInt(1, doctorId);
                int rowsAffected = userStatement.executeUpdate();
                userStatement.close();
                
                conn.commit();

                if (rowsAffected > 0) {
                    showToast("Xóa bác sĩ thành công!");
                    loadDoctors(); // Reload the list
                } else {
                    showToast("Xóa bác sĩ thất bại (không tìm thấy user).");
                }

            } catch (SQLException e) {
                Log.e(TAG, "SQLException: " + e.getMessage());
                showToast("Lỗi cơ sở dữ liệu khi xóa.");
                // Rollback in case of error
                try {
                    if(DatabaseConnector.getConnection() != null) DatabaseConnector.getConnection().rollback();
                } catch (SQLException ex) {
                    Log.e(TAG, "Rollback failed: " + ex.getMessage());
                }
            } finally {
                 try {
                    if(DatabaseConnector.getConnection() != null) {
                        DatabaseConnector.getConnection().setAutoCommit(true);
                        DatabaseConnector.getConnection().close();
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Failed to close connection: " + e.getMessage());
                }
            }
        }).start();
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ManageDoctorsActivity.this, message, Toast.LENGTH_SHORT).show());
    }
}
